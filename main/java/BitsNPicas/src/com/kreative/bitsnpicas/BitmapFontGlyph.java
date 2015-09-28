package com.kreative.bitsnpicas;

import java.awt.*;
import java.awt.image.*;

public class BitmapFontGlyph extends FontGlyph {
	protected byte[][] glyph;
	protected int x, y;
	protected int advance;
	
	public BitmapFontGlyph() {
		glyph = new byte[0][0];
		x = 0; y = 0; advance = 0;
	}

	public BitmapFontGlyph(byte[][] glyph) {
		this.glyph = glyph;
		x = 0; y = glyph.length; advance = (glyph.length < 1) ? 0 : (glyph[0].length);
	}

	public BitmapFontGlyph(byte[][] glyph, int offset, int width, int ascent) {
		this.glyph = glyph;
		x = offset; y = ascent; advance = width;
	}

	public byte[][] getGlyph() {
		return glyph;
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
		return glyph.length-y;
	}
	
	public double getGlyphDescent2D() {
		return glyph.length-y;
	}

	public int getCharacterWidth() {
		return advance;
	}
	
	public double getCharacterWidth2D() {
		return advance;
	}

	public int draw(Graphics g, int bx, int by) {
		Color c = g.getColor();
		int rgb = c.getRGB() & 0xFFFFFF;
		int a = c.getAlpha();

		int w = ((glyph.length < 1) ? 0 : (glyph[0].length));
		int h = glyph.length;
		int[] glyphPixels = new int[w*h];
		for (int k = 0, j = 0; j < h; j++) {
			for (int i = 0; i < w; k++, i++) {
				glyphPixels[k] = ((a * (glyph[j][i] & 0xFF) / 0xFF) << 24) | rgb;
			}
		}
		ImageProducer glyphProducer = new MemoryImageSource(w, h, glyphPixels, 0, w);
		Image glyphImage = Toolkit.getDefaultToolkit().createImage(glyphProducer);
		g.drawImage(glyphImage, bx+x, by-y+1, null);
		return advance;
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
}
