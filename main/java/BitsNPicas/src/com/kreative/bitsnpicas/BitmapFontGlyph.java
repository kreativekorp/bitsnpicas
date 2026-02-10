package com.kreative.bitsnpicas;

import java.awt.*;
import java.awt.image.*;

public class BitmapFontGlyph extends FontGlyph {
	protected byte[][] glyph;
	protected int x, y;
	protected int advance;
	
	public BitmapFontGlyph() {
		glyph = new byte[0][0];
		x = 0; y = 0;
		advance = 0;
	}
	
	public BitmapFontGlyph(byte[][] glyph) {
		this.glyph = glyph;
		x = 0; y = glyph.length;
		advance = (glyph.length < 1) ? 0 : (glyph[0].length);
	}
	
	public BitmapFontGlyph(byte[][] glyph, int offset, int width, int ascent) {
		this.glyph = glyph;
		x = offset; y = ascent;
		advance = width;
	}
	
	public byte[][] getGlyph() {
		return glyph;
	}
	
	public void setGlyph(byte[][] glyph) {
		this.glyph = glyph;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getGlyphWidth() {
		return (glyph.length < 1) ? 0 : (glyph[0].length);
	}
	
	public double getGlyphWidth2D() {
		return (glyph.length < 1) ? 0 : (glyph[0].length);
	}
	
	public int getGlyphHeight() {
		return glyph.length;
	}
	
	public double getGlyphHeight2D() {
		return glyph.length;
	}
	
	public int getGlyphOffset() {
		return x;
	}
	
	public double getGlyphOffset2D() {
		return x;
	}
	
	public int getGlyphAscent() {
		return y;
	}
	
	public double getGlyphAscent2D() {
		return y;
	}
	
	public int getGlyphDescent() {
		return glyph.length - y;
	}
	
	public double getGlyphDescent2D() {
		return glyph.length - y;
	}
	
	public int getCharacterWidth() {
		return advance;
	}
	
	public double getCharacterWidth2D() {
		return advance;
	}
	
	public void setCharacterWidth(int v) {
		advance = v;
	}
	
	public void setCharacterWidth2D(double v) {
		advance = (int)Math.ceil(v);
	}
	
	public double paint(Graphics g, double x, double y, double scale) {
		Color c = g.getColor();
		int rgb = c.getRGB() & 0xFFFFFF;
		int a = c.getAlpha();
		int w = ((glyph.length < 1) ? 0 : (glyph[0].length));
		int h = glyph.length;
		int[] glyphPixels = new int[w * h];
		for (int k = 0, j = 0; j < h; j++) {
			for (int i = 0; i < w; k++, i++) {
				glyphPixels[k] = ((a * (glyph[j][i] & 0xFF) / 0xFF) << 24) | rgb;
			}
		}
		ImageProducer glyphProducer = new MemoryImageSource(w, h, glyphPixels, 0, w);
		Image glyphImage = Toolkit.getDefaultToolkit().createImage(glyphProducer);
		int dx = (int)Math.round(x + this.x * scale);
		int dy = (int)Math.round(y - this.y * scale);
		int dw = (int)Math.round(w * scale);
		int dh = (int)Math.round(h * scale);
		g.drawImage(glyphImage, dx, dy, dx + dw, dy + dh, 0, 0, w, h, null);
		return advance * scale;
	}
	
	public PathGraph convertToPathGraph(int size) {
		PathGraph pg = new PathGraph();
		int w = ((glyph.length < 1) ? 0 : (glyph[0].length));
		int h = glyph.length;
		for (int j = 0, y = this.y-1; j < h; j++, y--) {
			for (int i = 0, x = this.x; i < w; i++, x++) {
				if ((glyph[j][i] & 0xFF) >= 0x80) pg.plot(size, x, y);
			}
		}
		return pg;
	}
	
	public PathGraph convertToPathGraph(int xsize, int ysize) {
		PathGraph pg = new PathGraph();
		int w = ((glyph.length < 1) ? 0 : (glyph[0].length));
		int h = glyph.length;
		for (int j = 0, y = this.y-1; j < h; j++, y--) {
			for (int i = 0, x = this.x; i < w; i++, x++) {
				if ((glyph[j][i] & 0xFF) >= 0x80) pg.plot(xsize, ysize, x, y);
			}
		}
		return pg;
	}
	
	public byte getPixel(int x, int y) {
		byte[][] data = getGlyph();
		int ix = x - getX();
		int iy = y + getY();
		if (iy >= 0 && iy < data.length) {
			if (ix >= 0 && ix < data[iy].length) {
				return data[iy][ix];
			}
		}
		return 0;
	}
	
	public void drawLine(int x1, int y1, int x2, int y2, byte v) {
		byte[][] data = getGlyph();
		int gx = getX();
		int gy = getY();
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
	
	public void drawRect(int x1, int y1, int w, int h, byte v) {
		byte[][] data = getGlyph();
		int gx = getX();
		int gy = getY();
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
	
	public void fillRect(int x1, int y1, int w, int h, byte v) {
		byte[][] data = getGlyph();
		int gx = getX();
		int gy = getY();
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
	
	public void invertRect(int x1, int y1, int w, int h) {
		byte[][] data = getGlyph();
		int gx = getX();
		int gy = getY();
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
	
	public void setToImage(int x, int y, BufferedImage image) {
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
		setXY(x, -y);
		setGlyph(data);
	}
	
	public void expand(int x, int y, int w, int h) {
		byte[][] data = getGlyph();
		if (data.length == 0) {
			setXY(x, -y);
			setGlyph(new byte[h][w]);
			return;
		}
		int gx = getX();
		int gy = getY();
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
			setXY(ex, -ey);
			setGlyph(newData);
		}
	}
	
	public void contract() {
		byte[][] data = getGlyph();
		if (data.length == 0) {
			setXY(0,0);
			return;
		}
		int gx = getX();
		int gy = getY();
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
		if (cx2 == cx1 || cy2 == cy1) {
			setXY(0,0);
			setGlyph(new byte[0][0]);
			return;
		}
		if (cx1 != gx || cy1 != -gy || cx2 != gx + gw || cy2 != -gy + gh) {
			int cw = cx2 - cx1;
			int ch = cy2 - cy1;
			byte[][] newData = new byte[ch][cw];
			for (int dy = 0, sy = cy1 + gy; dy < ch; dy++, sy++) {
				for (int dx = 0, sx = cx1 - gx; dx < cw; dx++, sx++) {
					newData[dy][dx] = data[sy][sx];
				}
			}
			setXY(cx1, -cy1);
			setGlyph(newData);
		}
	}
	
	public static BitmapFontGlyph compose(BitmapFontGlyph... glyphs) {
		int x0 = Integer.MAX_VALUE, y0 = Integer.MAX_VALUE;
		int x1 = Integer.MIN_VALUE, y1 = Integer.MIN_VALUE;
		int advance = Integer.MIN_VALUE;
		for (BitmapFontGlyph glyph : glyphs) {
			if (glyph == null) continue;
			if (glyph.x < x0) x0 = glyph.x;
			if (-glyph.y < y0) y0 = -glyph.y;
			if (glyph.advance > advance) advance = glyph.advance;
			if (-glyph.y + glyph.glyph.length > y1) y1 = -glyph.y + glyph.glyph.length;
			for (byte[] row : glyph.glyph) {
				if (glyph.x + row.length > x1) x1 = glyph.x + row.length;
			}
		}
		if (y1 < y0 || x1 < x0) return null;
		byte[][] g = new byte[y1 - y0][x1 - x0];
		for (BitmapFontGlyph glyph : glyphs) {
			if (glyph == null) continue;
			int bx = glyph.x - x0;
			int by = -glyph.y - y0;
			for (int y = 0; y < glyph.glyph.length; y++) {
				for (int x = 0; x < glyph.glyph[y].length; x++) {
					if ((glyph.glyph[y][x] & 0xFF) > (g[by + y][bx + x] & 0xFF)) {
						g[by + y][bx + x] = glyph.glyph[y][x];
					}
				}
			}
		}
		return new BitmapFontGlyph(g, x0, advance, -y0);
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
