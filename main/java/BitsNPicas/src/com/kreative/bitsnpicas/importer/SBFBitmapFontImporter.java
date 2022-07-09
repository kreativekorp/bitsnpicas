package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

public class SBFBitmapFontImporter implements BitmapFontImporter {
	private GlyphList encoding;
	
	public SBFBitmapFontImporter() {
		this.encoding = null;
	}
	
	public SBFBitmapFontImporter(GlyphList encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
		if (in.readInt() != 0x10B17E5C) throw new IOException("bad magic number");
		in.readUnsignedByte();
		int height = in.readUnsignedByte();
		int ascent = in.readUnsignedByte();
		int descent = in.readUnsignedByte();
		BitmapFont f = new BitmapFont(ascent, descent, ascent, descent, 0, height - ascent - descent);
		
		int[] offset = new int[256];
		int[] gwidth = new int[256];
		int[] gheight = new int[256];
		int[] goffset = new int[256];
		int[] gascent = new int[256];
		int[] advance = new int[256];
		int[] overhang = new int[256];
		for (int ch = 1; ch < 256; ch++) {
			offset[ch] = Short.reverseBytes(in.readShort()) & 0xFFFF;
			gwidth[ch] = in.readUnsignedByte();
			gheight[ch] = in.readUnsignedByte();
			goffset[ch] = in.readByte();
			gascent[ch] = in.readByte();
			advance[ch] = in.readUnsignedByte();
			overhang[ch] = in.readUnsignedByte();
		}
		
		for (int ch = 0; ch < 256; ch++) {
			if (offset[ch] > 0) {
				int w = gwidth[ch] * 8 - overhang[ch];
				byte[][] gd = new byte[gheight[ch]][w];
				for (int dy = offset[ch], gy = 0; dy < data.length && gy < gd.length; dy += gwidth[ch], gy++) {
					for (int dx = dy, gx = 0; dx < data.length && gx < w; dx++, gx += 8) {
						for (int dm = 0x80, gi = gx; dm != 0 && gi < w; dm >>= 1, gi++) {
							if ((data[dx] & dm) != 0) gd[gy][gi] = -1;
						}
					}
				}
				BitmapFontGlyph g = new BitmapFontGlyph(gd, goffset[ch], advance[ch], gascent[ch]);
				int cp = (encoding != null) ? encoding.get(ch) : fromSuperLatin(ch);
				if (cp >= 0) f.putCharacter(cp, g);
			}
		}
		
		in.close();
		f.setXHeight();
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1048576]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.close();
		return importFont(out.toByteArray());
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1048576]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.close();
		in.close();
		BitmapFont[] f = importFont(out.toByteArray());
		if (f.length > 0) {
			String name = file.getName();
			if (name.toLowerCase().endsWith(".sbf")) {
				name = name.substring(0, name.length() - 4);
			}
			for (BitmapFont ff : f) {
				ff.setName(Font.NAME_FAMILY, name);
			}
		}
		return f;
	}
	
	private static final int[] C0 = {
		0x0000, 0x02CB, 0x02DD, 0x02D9, 0x02DA, 0x02C7, 0x02D8, 0x02DB,
		0x0008, 0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0xFB01, 0xFB02,
		0xF8FF, 0x2044, 0x221A, 0x2211, 0x220F, 0x222B, 0x2206, 0x03A9,
		0x03C0, 0x2202, 0x221E, 0x001B, 0x2264, 0x2260, 0x2265, 0x2248,
	};
	
	private static final int[] C1 = {
		0x20AC, 0x25CA, 0x201A, 0x0192, 0x201E, 0x2026, 0x2020, 0x2021,
		0x02C6, 0x2030, 0x0160, 0x2039, 0x0152, 0x0141, 0x017D, 0x0131,
		0x2318, 0x2018, 0x2019, 0x201C, 0x201D, 0x2022, 0x2013, 0x2014,
		0x02DC, 0x2122, 0x0161, 0x203A, 0x0153, 0x0142, 0x017E, 0x0178,
	};
	
	private static int fromSuperLatin(int ch) {
		ch &= 0xFF;
		if (ch < 0x20) {
			return C0[ch];
		} else if (ch < 0x80) {
			return ch;
		} else if (ch < 0xA0) {
			return C1[ch & 0x1F];
		} else {
			return ch;
		}
	}
}
