package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.unicode.data.GlyphList;

public class CybikoBitmapFontExporter implements BitmapFontExporter {
	private GlyphList encoding;
	
	public CybikoBitmapFontExporter() {
		this.encoding = null;
	}
	
	public CybikoBitmapFontExporter(GlyphList encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		exportFontImpl(font, new DataOutputStream(out));
		out.close();
		return out.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		exportFontImpl(font, new DataOutputStream(os));
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		exportFontImpl(font, new DataOutputStream(out));
		out.close();
	}
	
	private void exportFontImpl(BitmapFont font, DataOutputStream out) throws IOException {
		int lastch = 32;
		int width = 1;
		int height = font.getLineAscent() + font.getLineDescent() + font.getLineGap();
		
		for (int ch = 32; ch < 256; ch++) {
			int cp = (encoding != null) ? encoding.get(ch) : fromCybiko(ch);
			BitmapFontGlyph g = font.getCharacter(cp);
			if (g != null) {
				lastch = ch;
				int w = g.getCharacterWidth();
				if (w > width) width = w;
			}
		}
		
		out.writeByte(1);
		out.writeByte(lastch - 31);
		out.writeByte(width);
		out.writeByte(height);
		
		for (int ch = 32; ch <= lastch; ch++) {
			int cp = (encoding != null) ? encoding.get(ch) : fromCybiko(ch);
			BitmapFontGlyph g = font.getCharacter(cp);
			if (g == null) {
				out.writeByte(0);
				out.writeByte(0);
				out.writeByte(1);
				out.writeByte(1);
				out.writeByte(0);
			} else {
				int w = g.getCharacterWidth();
				if (w < 1) w = 1;
				
				int y = font.getLineAscent() - g.getGlyphAscent();
				int h = g.getGlyphHeight();
				int r = 0;
				while (h < 1) { y--; r--; h++; }
				while (y >= height) { y--; r--; h++; }
				while (y < 0) { y++; r++; h--; }
				if (h > height - y) h = height - y;
				if (h < 1) h = 1;
				
				out.writeByte(0);
				out.writeByte(y);
				out.writeByte(w);
				out.writeByte(h);
				for (byte[][] gd = g.getGlyph(); h > 0; h--, r++) {
					byte[] row = new byte[(w+7)/8];
					if (r >= 0 && r < gd.length) {
						for (int c = -g.getGlyphOffset(), p = 0, x = 0; x < w; x += 8, p++) {
							for (int m = 0x80, i = x; m != 0 && i < w; m >>= 1, i++, c++) {
								if (c >= 0 && c < gd[r].length && gd[r][c] < 0) {
									row[p] |= m;
								}
							}
						}
					}
					out.write(row);
				}
			}
		}
	}
	
	private static int fromCybiko(int ch) {
		switch (ch) {
			// Cybiko Symbols
			case 0x7F: return 0x2026; // horizontal ellipsis
			case 0x80: return 0x2665; // black heart suit
			case 0x81: return 0x25CF; // black circle
			case 0x82: return 0x25B6; // black right-pointing triangle
			case 0x83: return 0x25B7; // white right-pointing triangle
			case 0x84: return 0xF084; // white right-pointing triangle, duplicate?
			case 0x85: return 0xF085; // cy-sign
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
