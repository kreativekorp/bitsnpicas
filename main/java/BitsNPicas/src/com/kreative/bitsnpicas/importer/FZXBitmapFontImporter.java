package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;
import com.kreative.unicode.data.GlyphList;

public class FZXBitmapFontImporter implements BitmapFontImporter {
	private static final char[] X_HEIGHT_CHARS = new char[]{'x', 'X', '0', '!'};
	
	private GlyphList encoding;
	
	public FZXBitmapFontImporter() {
		this.encoding = null;
	}
	
	public FZXBitmapFontImporter(GlyphList encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		// HEADER
		if (data.length < 3) return new BitmapFont[0];
		int height = data[0] & 0xFF;
		int tracking = data[1] & 0xFF;
		int lastchar = data[2] & 0xFF;
		if (lastchar < 32) return new BitmapFont[0];
		
		// TABLE
		int end = (lastchar - 30) * 3;
		if (data.length < end + 2) return new BitmapFont[0];
		int[] offset = new int[lastchar - 30];
		int[] kern = new int[lastchar - 31];
		int[] shift = new int[lastchar - 31];
		int[] width = new int[lastchar - 31];
		for (int o = 3, i = 0, ch = 32; ch <= lastchar; ch++, i++, o += 3) {
			int ok = (data[o] & 0xFF) | ((data[o + 1] & 0xFF) << 8);
			int sw = (data[o + 2] & 0xFF);
			offset[i] = (ok & 0x3FFF) + o;
			kern[i] = ok >> 14;
			shift[i] = sw >> 4;
			width[i] = (sw & 0x0F) + 1;
		}
		offset[lastchar - 31] = ((data[end] & 0xFF) | ((data[end + 1] & 0xFF) << 8)) + end;
		
		// CHARACTER DEFINITIONS
		byte[][][] gd = new byte[lastchar - 31][][];
		for (int i = 0, ch = 32; ch <= lastchar; ch++, i++) {
			int bpr = ((width[i] <= 8) ? 1 : 2);
			int rows = (offset[i + 1] - offset[i]) / bpr;
			if (rows < 0) rows = 0;
			gd[i] = new byte[rows][width[i]];
			for (int o = offset[i], y = 0; y < rows; y++, o += bpr) {
				int row = (o < data.length) ? ((data[o] & 0xFF) << 8) : 0;
				row |= (o + 1 < data.length) ? (data[o + 1] & 0xFF) : 0;
				for (int m = 0x8000, x = 0; x < width[i]; x++, m >>= 1) {
					if ((row & m) != 0) gd[i][y][x] = -1;
				}
			}
		}
		
		int ascent = height;
		int descent = 0;
		int xheight = 0;
		for (char x : X_HEIGHT_CHARS) {
			if (lastchar >= x) {
				byte[][] gdx = gd[x - 32];
				xheight = gdx.length;
				ascent = shift[x - 32] + xheight;
				descent = height - ascent;
				break;
			}
		}
		
		BitmapFont f = new BitmapFont(ascent, descent, ascent, descent, xheight, 0);
		for (int i = 0, ch = 32; ch <= lastchar; ch++, i++) {
			int cp = zxcp(ch);
			if (cp >= 0) {
				BitmapFontGlyph g = new BitmapFontGlyph(
					gd[i], -kern[i],
					width[i] - kern[i] + tracking,
					ascent - shift[i]
				);
				f.putCharacter(cp, g);
			}
		}
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
			if (name.toLowerCase().endsWith(".fzx")) {
				name = name.substring(0, name.length() - 4);
			}
			for (BitmapFont ff : f) {
				ff.setName(Font.NAME_FAMILY, name);
			}
		}
		return f;
	}
	
	private int zxcp(int ch) {
		if (encoding != null) return encoding.get(ch);
		if (ch == 96) return 163;
		if (ch == 127) return 169;
		if (ch < 128) return ch;
		return (0xF000 + ch);
	}
}
