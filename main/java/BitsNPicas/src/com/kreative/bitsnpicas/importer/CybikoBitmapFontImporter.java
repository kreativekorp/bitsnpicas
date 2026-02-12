package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;
import com.kreative.unicode.data.GlyphList;

public class CybikoBitmapFontImporter implements BitmapFontImporter {
	private GlyphList encoding;
	
	public CybikoBitmapFontImporter() {
		this.encoding = null;
	}
	
	public CybikoBitmapFontImporter(GlyphList encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		BitmapFont f = importFontImpl(new DataInputStream(in));
		in.close();
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(InputStream in) throws IOException {
		BitmapFont f = importFontImpl(new DataInputStream(in));
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		BitmapFont f = importFontImpl(new DataInputStream(in));
		in.close();
		
		String name = file.getName();
		if (name.toLowerCase().endsWith(".cyf")) {
			name = name.substring(0, name.length() - 4);
		} else if (name.toLowerCase().endsWith(".fnt")) {
			name = name.substring(0, name.length() - 4);
		} else if (name.toLowerCase().endsWith(".fntz")) {
			name = name.substring(0, name.length() - 5);
		} else if (name.toLowerCase().endsWith(".fnty")) {
			name = name.substring(0, name.length() - 5);
		}
		f.setName(Font.NAME_FAMILY, name);
		
		return new BitmapFont[]{f};
	}
	
	private BitmapFont importFontImpl(DataInputStream in) throws IOException {
		if (in.readUnsignedByte() != 1) throw new IOException("bad magic number");
		int cc = in.readUnsignedByte();
		int mw = in.readUnsignedByte();
		int mh = in.readUnsignedByte();
		int ascent = mh;
		BitmapFont f = new BitmapFont(mh, 0, mh, 0, 0, 0, 0, mw);
		
		for (int c = 0; c < cc; c++) {
			int l = in.readUnsignedByte();
			int t = in.readUnsignedByte();
			int w = in.readUnsignedByte();
			int h = in.readUnsignedByte();
			boolean ok = l > 0 || t > 0 || w > 1 || h > 1;
			byte[][] gd = new byte[h][w];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x += 8) {
					int b = in.readUnsignedByte();
					for (int m = 0x80, i = x; m != 0 && i < w; m >>= 1, i++) {
						if ((b & m) != 0) {
							ok = true;
							gd[y][i] = -1;
						}
					}
				}
			}
			if (ok) {
				if (c == 0) {
					ascent = t + h;
					f.setEmAscent(ascent);
					f.setLineAscent(ascent);
					f.setEmDescent(mh - ascent);
					f.setLineDescent(mh - ascent);
				}
				BitmapFontGlyph g = new BitmapFontGlyph(gd, l, w, ascent - t);
				int ch = (c + 32) & 0xFF;
				int cp = (encoding != null) ? encoding.get(ch) : fromCybiko(ch);
				if (cp >= 0) f.putCharacter(cp, g);
			}
		}
		
		f.setXHeight();
		f.setCapHeight();
		return f;
	}
	
	private static int fromCybiko(int ch) {
		switch (ch) {
			// Cybiko Symbols
			case 0x7F: return 0x2026; // horizontal ellipsis
			case 0x80: return 0x2665; // black heart suit
			case 0x81: return 0x25CF; // black circle
			case 0x82: return 0x25B6; // black right-pointing triangle
			case 0x83: return 0x25B7; // white right-pointing triangle
			case 0x84: return 0xFFC22; // white right-pointing triangle, duplicate?
			case 0x85: return 0xFFC23; // cy-sign
			// Windows CP-1252
			case 0x86: return 0x2020; // dagger
			case 0x87: return 0x2021; // double dagger
			case 0x88: return 0x02C6; // modifier letter circumflex accent
			case 0x89: return 0x2030; // per mille sign
			case 0x8A: return 0x0160; // latin capital letter s with caron
			case 0x8B: return 0x2039; // single left-pointing angle quotation mark
			case 0x8C: return 0x0152; // latin capital ligature oe
			case 0x8D: return 0x0141; // latin capital letter l with stroke
			case 0x8E: return 0x017D; // latin capital letter z with caron
			case 0x8F: return 0x0131; // latin small letter dotless i
			case 0x90: return 0x20AC; // euro sign
			case 0x91: return 0x2018; // left single quotation mark
			case 0x92: return 0x2019; // right single quotation mark
			case 0x93: return 0x201C; // left double quotation mark
			case 0x94: return 0x201D; // right double quotation mark
			case 0x95: return 0x2022; // bullet
			case 0x96: return 0x2013; // en dash
			case 0x97: return 0x2014; // em dash
			case 0x98: return 0x02DC; // small tilde
			case 0x99: return 0x2122; // trade mark sign
			case 0x9A: return 0x0161; // latin small letter s with caron
			case 0x9B: return 0x203A; // single right-pointing angle quotation mark
			case 0x9C: return 0x0153; // latin small ligature oe
			case 0x9D: return 0x0142; // latin small letter l with stroke
			case 0x9E: return 0x017E; // latin small letter z with caron
			case 0x9F: return 0x0178; // latin capital letter y with diaeresis
			default: return ch;
		}
	}
}
