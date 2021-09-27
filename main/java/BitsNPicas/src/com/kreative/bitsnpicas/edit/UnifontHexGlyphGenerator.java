package com.kreative.bitsnpicas.edit;

import com.kreative.bitsnpicas.BitmapFontGlyph;

public class UnifontHexGlyphGenerator {
	public static BitmapFontGlyph createGlyph(int cp) {
		int offset = (cp < 0x10000) ? 1 : 0;
		return new BitmapFontGlyph(createGlyphData(cp), offset, 16, 13);
	}
	
	public static byte[][] createGlyphData(int cp) {
		int width = (cp < 0x10000) ? 14 : 16;
		byte[][] g = new byte[14][width];
		for (int y = 0; y < 14; y++) {
			for (int x = 0; x < width; x++) {
				g[y][x] = -1;
			}
		}
		if (cp < 0x10000) {
			copy(g, 2, 1, digits[(cp >> 12) & 0x0F]);
			copy(g, 8, 1, digits[(cp >>  8) & 0x0F]);
			copy(g, 2, 8, digits[(cp >>  4) & 0x0F]);
			copy(g, 8, 8, digits[(cp >>  0) & 0x0F]);
		} else {
			copy(g,  1, 1, digits[(cp >> 20) & 0x0F]);
			copy(g,  6, 1, digits[(cp >> 16) & 0x0F]);
			copy(g, 11, 1, digits[(cp >> 12) & 0x0F]);
			copy(g,  1, 8, digits[(cp >>  8) & 0x0F]);
			copy(g,  6, 8, digits[(cp >>  4) & 0x0F]);
			copy(g, 11, 8, digits[(cp >>  0) & 0x0F]);
		}
		return g;
	}
	
	private static void copy(byte[][] dst, int x, int y, byte[][] src) {
		for (byte[] row : src) {
			for (byte col : row) {
				dst[y][x] = col;
				x++;
			}
			x -= row.length;
			y++;
		}
	}
	
	private static final byte[][][] digits = {
		{{-1, 0, 0, -1}, {0, -1, -1, 0}, {0, -1, -1, 0}, {0, -1, -1, 0}, {-1, 0, 0, -1}},
		{{-1, -1, 0, -1}, {-1, 0, 0, -1}, {-1, -1, 0, -1}, {-1, -1, 0, -1}, {-1, 0, 0, 0}},
		{{0, 0, 0, 0}, {-1, -1, -1, 0}, {0, 0, 0, 0}, {0, -1, -1, -1}, {0, 0, 0, 0}},
		{{0, 0, 0, -1}, {-1, -1, -1, 0}, {-1, 0, 0, 0}, {-1, -1, -1, 0}, {0, 0, 0, -1}},
		{{0, -1, -1, 0}, {0, -1, -1, 0}, {0, 0, 0, 0}, {-1, -1, -1, 0}, {-1, -1, -1, 0}},
		{{0, 0, 0, 0}, {0, -1, -1, -1}, {0, 0, 0, 0}, {-1, -1, -1, 0}, {0, 0, 0, 0}},
		{{-1, 0, 0, -1}, {0, -1, -1, -1}, {0, 0, 0, -1}, {0, -1, -1, 0}, {-1, 0, 0, -1}},
		{{0, 0, 0, 0}, {-1, -1, -1, 0}, {-1, -1, 0, -1}, {-1, 0, -1, -1}, {-1, 0, -1, -1}},
		{{-1, 0, 0, -1}, {0, -1, -1, 0}, {-1, 0, 0, -1}, {0, -1, -1, 0}, {-1, 0, 0, -1}},
		{{-1, 0, 0, -1}, {0, -1, -1, 0}, {-1, 0, 0, 0}, {-1, -1, -1, 0}, {-1, 0, 0, -1}},
		{{0, 0, 0, 0}, {0, -1, -1, 0}, {0, 0, 0, 0}, {0, -1, -1, 0}, {0, -1, -1, 0}},
		{{0, 0, 0, -1}, {0, -1, -1, 0}, {0, 0, 0, -1}, {0, -1, -1, 0}, {0, 0, 0, -1}},
		{{-1, 0, 0, 0}, {0, -1, -1, -1}, {0, -1, -1, -1}, {0, -1, -1, -1}, {-1, 0, 0, 0}},
		{{0, 0, 0, -1}, {0, -1, -1, 0}, {0, -1, -1, 0}, {0, -1, -1, 0}, {0, 0, 0, -1}},
		{{0, 0, 0, 0}, {0, -1, -1, -1}, {0, 0, 0, -1}, {0, -1, -1, -1}, {0, 0, 0, 0}},
		{{0, 0, 0, 0}, {0, -1, -1, -1}, {0, 0, 0, -1}, {0, -1, -1, -1}, {0, -1, -1, -1}},
	};
}
