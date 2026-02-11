package com.kreative.bitsnpicas;

import java.awt.Graphics;
import java.awt.Point;

public class BitmapFont extends Font<BitmapFontGlyph> {
	protected int emAscent, emDescent;
	protected int lineAscent, lineDescent;
	protected int xHeight, capHeight, lineGap;
	
	public BitmapFont() {
		this.emAscent = 0;
		this.emDescent = 0;
		this.lineAscent = 0;
		this.lineDescent = 0;
		this.xHeight = 0;
		this.capHeight = 0;
		this.lineGap = 0;
	}
	
	public BitmapFont(
		int emAscent, int emDescent,
		int lineAscent, int lineDescent,
		int xHeight, int capHeight, int lineGap
	) {
		this.emAscent = emAscent;
		this.emDescent = emDescent;
		this.lineAscent = lineAscent;
		this.lineDescent = lineDescent;
		this.xHeight = xHeight;
		this.capHeight = capHeight;
		this.lineGap = lineGap;
	}
	
	public int getEmAscent() { return emAscent; }
	public double getEmAscent2D() { return emAscent; }
	public int getEmDescent() { return emDescent; }
	public double getEmDescent2D() { return emDescent; }
	public int getLineAscent() { return lineAscent; }
	public double getLineAscent2D() { return lineAscent; }
	public int getLineDescent() { return lineDescent; }
	public double getLineDescent2D() { return lineDescent; }
	public int getXHeight() { return xHeight; }
	public double getXHeight2D() { return xHeight; }
	public int getCapHeight() { return capHeight; }
	public double getCapHeight2D() { return capHeight; }
	public int getLineGap() { return lineGap; }
	public double getLineGap2D() { return lineGap; }
	
	public void setEmAscent(int v) { emAscent = v; }
	public void setEmAscent2D(double v) { emAscent = (int)Math.ceil(v); }
	public void setEmDescent(int v) { emDescent = v; }
	public void setEmDescent2D(double v) { emDescent = (int)Math.ceil(v); }
	public void setLineAscent(int v) { lineAscent = v; }
	public void setLineAscent2D(double v) { lineAscent = (int)Math.ceil(v); }
	public void setLineDescent(int v) { lineDescent = v; }
	public void setLineDescent2D(double v) { lineDescent = (int)Math.ceil(v); }
	public void setXHeight(int v) { xHeight = v; }
	public void setXHeight2D(double v) { xHeight = (int)Math.ceil(v); }
	public void setCapHeight(int v) { capHeight = v; }
	public void setCapHeight2D(double v) { capHeight = (int)Math.ceil(v); }
	public void setLineGap(int v) { lineGap = v; }
	public void setLineGap2D(double v) { lineGap = (int)Math.ceil(v); }
	
	public void contractGlyphs() {
		for (BitmapFontGlyph g : characters.values()) g.contract();
		for (BitmapFontGlyph g : namedGlyphs.values()) g.contract();
	}
	
	public void setAscentDescent() {
		contractGlyphs();
		int adjust = guessBaselineAdjustment();
		if (adjust == 0) return;
		emAscent += adjust;
		emDescent -= adjust;
		lineAscent += adjust;
		lineDescent -= adjust;
		for (BitmapFontGlyph g : characters.values()) g.setXY(g.getX(), g.getY() + adjust);
		for (BitmapFontGlyph g : namedGlyphs.values()) g.setXY(g.getX(), g.getY() + adjust);
	}
	
	public void setXHeight() {
		contractGlyphs();
		int xh = guessXHeight();
		if (xh != 0) xHeight = xh;
	}
	
	public void setCapHeight() {
		contractGlyphs();
		int ch = guessCapHeight();
		if (ch != 0) capHeight = ch;
	}
	
	public Point draw(Graphics g, String s, int bx, int by, int scale, int w) {
		return draw(g, s, bx, by, scale, w, (lineAscent + lineDescent + lineGap) * scale);
	}
	
	public Point draw(Graphics g, String s, int bx, int by, int scale, int w, int lh) {
		int cx = bx, cy = by;
		int i = 0, n = s.length();
		while (i < n) {
			int ch = s.codePointAt(i);
			i += Character.charCount(ch);
			if (ch == '\n' || ch == '\r') {
				cx = bx;
				cy += lh;
			} else {
				BitmapFontGlyph bm = characters.get(ch);
				if (bm == null) bm = namedGlyphs.get(".notdef");
				if (bm != null) {
					if (cx - bx + (bm.getCharacterWidth() * scale) >= w) {
						cx = bx;
						cy += lh;
					}
					cx += bm.paint(g, cx, cy, scale);
				}
			}
		}
		return new Point(cx, cy);
	}
	
	public Point drawAlphabet(Graphics g, int bx, int by, int scale, int w) {
		return drawAlphabet(g, bx, by, scale, w, (lineAscent + lineDescent + lineGap) * scale);
	}
	
	public Point drawAlphabet(Graphics g, int bx, int by, int scale, int w, int lh) {
		int cx = bx, cy = by;
		for (BitmapFontGlyph bm : characters.values()) {
			if (cx - bx + (bm.getCharacterWidth() * scale) >= w) {
				cx = bx;
				cy += lh;
			}
			cx += bm.paint(g, cx, cy, scale);
		}
		for (BitmapFontGlyph bm : namedGlyphs.values()) {
			if (cx - bx + (bm.getCharacterWidth() * scale) >= w) {
				cx = bx;
				cy += lh;
			}
			cx += bm.paint(g, cx, cy, scale);
		}
		return new Point(cx, cy);
	}
}
