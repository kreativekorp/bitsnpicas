package com.kreative.bitsnpicas.edit;

import java.awt.image.BufferedImage;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class BitmapGlyphOps {
	public static byte getPixel(BitmapFontGlyph g, int x, int y) {
		byte[][] data = g.getGlyph();
		int ix = x - g.getX();
		int iy = y + g.getY();
		if (iy >= 0 && iy < data.length) {
			if (ix >= 0 && ix < data[iy].length) {
				return data[iy][ix];
			}
		}
		return 0;
	}
	
	public static void drawLine(BitmapFontGlyph g, int x1, int y1, int x2, int y2, byte v) {
		byte[][] data = g.getGlyph();
		int gx = g.getX();
		int gy = g.getY();
		for (int i = 0, n = Math.abs(x2 - x1) + Math.abs(y2 - y1) + 1; i <= n; i++) {
			int ix = x1 + (int)Math.round((x2 - x1) * i / (double)n) - gx;
			int iy = y1 + (int)Math.round((y2 - y1) * i / (double)n) + gy;
			if (iy >= 0 && iy < data.length) {
				if (ix >= 0 && ix < data[iy].length) {
					data[iy][ix] = v;
				}
			}
		}
	}
	
	public static void drawRect(BitmapFontGlyph g, int x1, int y1, int w, int h, byte v) {
		byte[][] data = g.getGlyph();
		int gx = g.getX();
		int gy = g.getY();
		for (int y = y1, y2 = y1 + h; y <= y2; y++) {
			int ix1 = x1 - gx;
			int ix2 = x1 + w - gx;
			int iy = y + gy;
			if (iy >= 0 && iy < data.length) {
				if (ix1 >= 0 && ix1 < data[iy].length) {
					data[iy][ix1] = v;
				}
				if (ix2 >= 0 && ix2 < data[iy].length) {
					data[iy][ix2] = v;
				}
			}
		}
		for (int x = x1, x2 = x1 + w; x <= x2; x++) {
			int ix = x - gx;
			int iy1 = y1 + gy;
			int iy2 = y1 + h + gy;
			if (iy1 >= 0 && iy1 < data.length) {
				if (ix >= 0 && ix < data[iy1].length) {
					data[iy1][ix] = v;
				}
			}
			if (iy2 >= 0 && iy2 < data.length) {
				if (ix >= 0 && ix < data[iy2].length) {
					data[iy2][ix] = v;
				}
			}
		}
	}
	
	public static void fillRect(BitmapFontGlyph g, int x1, int y1, int w, int h, byte v) {
		byte[][] data = g.getGlyph();
		int gx = g.getX();
		int gy = g.getY();
		for (int y = y1, y2 = y1 + h; y < y2; y++) {
			int iy = y + gy;
			for (int x = x1, x2 = x1 + w; x < x2; x++) {
				int ix = x - gx;
				if (iy >= 0 && iy < data.length) {
					if (ix >= 0 && ix < data[iy].length) {
						data[iy][ix] = v;
					}
				}
			}
		}
	}
	
	public static void invertRect(BitmapFontGlyph g, int x1, int y1, int w, int h) {
		byte[][] data = g.getGlyph();
		int gx = g.getX();
		int gy = g.getY();
		for (int y = y1, y2 = y1 + h; y < y2; y++) {
			int iy = y + gy;
			for (int x = x1, x2 = x1 + w; x < x2; x++) {
				int ix = x - gx;
				if (iy >= 0 && iy < data.length) {
					if (ix >= 0 && ix < data[iy].length) {
						data[iy][ix] ^= -1;
					}
				}
			}
		}
	}
	
	public static void setToImage(BitmapFontGlyph g, int x, int y, BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] p = new int[w * h];
		image.getRGB(0, 0, w, h, p, 0, w);
		byte[][] data = new byte[h][w];
		for (int dy = 0, py = 0; dy < h; dy++, py += w) {
			for (int ix = 0; ix < w; ix++) {
				int pa = (p[py + ix] >> 24) & 0xFF;
				int pr = (p[py + ix] >> 16) & 0xFF;
				int pg = (p[py + ix] >>  8) & 0xFF;
				int pb = (p[py + ix] >>  0) & 0xFF;
				int px = (25500 - (30 * pr + 59 * pg + 11 * pb)) * pa / 25500;
				data[dy][ix] = (byte)px;
			}
		}
		g.setXY(x, -y);
		g.setGlyph(data);
	}
	
	public static void expand(BitmapFontGlyph g, int x, int y, int w, int h) {
		byte[][] data = g.getGlyph();
		if (data.length == 0) {
			g.setXY(x, -y);
			g.setGlyph(new byte[h][w]);
			return;
		}
		int gx = g.getX();
		int gy = g.getY();
		int gw = data[0].length;
		int gh = data.length;
		int ex = Math.min(x, gx);
		int ey = Math.min(y, -gy);
		int ew = Math.max(x + w, gx + gw) - ex;
		int eh = Math.max(y + h, -gy + gh) - ey;
		if (ex < gx || ey < -gy || ex + ew > gx + gw || ey + eh > -gy + gh) {
			byte[][] newData = new byte[eh][ew];
			for (int sy = 0, dy = -gy - ey; sy < gh; sy++, dy++) {
				for (int sx = 0, dx = gx - ex; sx < gw; sx++, dx++) {
					newData[dy][dx] = data[sy][sx];
				}
			}
			g.setXY(ex, -ey);
			g.setGlyph(newData);
		}
	}
	
	public static void contract(BitmapFontGlyph g) {
		byte[][] data = g.getGlyph();
		if (data.length == 0) return;
		int gx = g.getX();
		int gy = g.getY();
		int gw = data[0].length;
		int gh = data.length;
		int cx1 = gx;
		int cy1 = -gy;
		int cx2 = gx + gw;
		int cy2 = -gy + gh;
		while (cy2 > cy1 && rowEmpty(data, cy2 + gy - 1)) cy2--;
		while (cy1 < cy2 && rowEmpty(data, cy1 + gy)) cy1++;
		while (cx2 > cx1 && colEmpty(data, cx2 - gx - 1)) cx2--;
		while (cx1 < cx2 && colEmpty(data, cx1 - gx)) cx1++;
		if (cx1 != gx || cy1 != -gy || cx2 != gx + gw || cy2 != -gy + gh) {
			int cw = cx2 - cx1;
			int ch = cy2 - cy1;
			byte[][] newData = new byte[ch][cw];
			for (int dy = 0, sy = cy1 + gy; dy < ch; dy++, sy++) {
				for (int dx = 0, sx = cx1 - gx; dx < cw; dx++, sx++) {
					newData[dy][dx] = data[sy][sx];
				}
			}
			g.setXY(cx1, -cy1);
			g.setGlyph(newData);
		}
	}
	
	private static boolean rowEmpty(byte[][] a, int row) {
		for (byte b : a[row]) if (b != 0) return false;
		return true;
	}
	
	private static boolean colEmpty(byte[][] a, int col) {
		for (byte[] b : a) if (b[col] != 0) return false;
		return true;
	}
}
