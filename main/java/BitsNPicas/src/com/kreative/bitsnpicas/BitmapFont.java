package com.kreative.bitsnpicas;

import java.awt.Graphics;
import java.awt.Point;

public class BitmapFont extends Font<BitmapFontGlyph> {
	protected int ascent, descent, typoascent, typodescent, xheight, linegap;
	
	public BitmapFont() {
		this.ascent = 0;
		this.descent = 0;
		this.typoascent = 0;
		this.typodescent = 0;
		this.xheight = 0;
		this.linegap = 0;
	}
	
	public BitmapFont(int ascent, int descent, int typoascent, int typodescent, int xheight, int linegap) {
		this.ascent = ascent;
		this.descent = descent;
		this.typoascent = typoascent;
		this.typodescent = typodescent;
		this.xheight = xheight;
		this.linegap = linegap;
	}
	
	public int getEmAscent() { return ascent; }
	public double getEmAscent2D() { return ascent; }
	public int getEmDescent() { return descent; }
	public double getEmDescent2D() { return descent; }
	public int getLineAscent() { return typoascent; }
	public double getLineAscent2D() { return typoascent; }
	public int getLineDescent() { return typodescent; }
	public double getLineDescent2D() { return typodescent; }
	public int getXHeight() { return xheight; }
	public double getXHeight2D() { return xheight; }
	public int getLineGap() { return linegap; }
	public double getLineGap2D() { return linegap; }
	
	public void setEmAscent(int v) { ascent = v; }
	public void setEmAscent2D(double v) { ascent = (int)Math.ceil(v); }
	public void setEmDescent(int v) { descent = v; }
	public void setEmDescent2D(double v) { descent = (int)Math.ceil(v); }
	public void setLineAscent(int v) { typoascent = v; }
	public void setLineAscent2D(double v) { typoascent = (int)Math.ceil(v); }
	public void setLineDescent(int v) { typodescent = v; }
	public void setLineDescent2D(double v) { typodescent = (int)Math.ceil(v); }
	public void setXHeight(int v) { xheight = v; }
	public void setXHeight2D(double v) { xheight = (int)Math.ceil(v); }
	public void setLineGap(int v) { linegap = v; }
	public void setLineGap2D(double v) { linegap = (int)Math.ceil(v); }
	
	public void setAscentDescent() {
		if (characters.containsKey((int)'x')) {
			BitmapFontGlyph g = characters.get((int)'x');
			g.contract();
			int adjust = g.getGlyphAscent() - g.getGlyphHeight();
			ascent -= adjust;
			descent += adjust;
			typoascent -= adjust;
			typodescent += adjust;
			for (BitmapFontGlyph glyph : characters.values()) {
				glyph.setXY(glyph.getX(), glyph.getY() - adjust);
			}
		}
	}
	
	public void setXHeight() {
		if (characters.containsKey((int)'x')) {
			BitmapFontGlyph g = characters.get((int)'x');
			g.contract();
			xheight = g.getGlyphAscent();
		}
	}
	
	public Point draw(Graphics g, String s, int bx, int by, int scale, int w) {
		return draw(g, s, bx, by, scale, w, (typoascent + typodescent + linegap) * scale);
	}
	
	public Point draw(Graphics g, String s, int bx, int by, int scale, int w, int lh) {
		int cx = bx, cy = by;
		int i = 0;
		while (i < s.length()) {
			int ch = s.codePointAt(i);
			if (ch < 0x10000) i++;
			else i += 2;
			switch (ch) {
			case '\n':
			case '\r':
				cx = bx;
				cy += lh;
				break;
			default:
				if (characters.containsKey(ch)) {
					BitmapFontGlyph bm = characters.get(ch);
					if (cx - bx + (bm.getCharacterWidth() * scale) >= w) {
						cx = bx;
						cy += lh;
					}
					cx += bm.paint(g, cx, cy, scale);
				}
				else if (characters.containsKey(-1)) {
					BitmapFontGlyph bm = characters.get(-1);
					if (cx - bx + (bm.getCharacterWidth() * scale) >= w) {
						cx = bx;
						cy += lh;
					}
					cx += bm.paint(g, cx, cy, scale);
				}
				break;
			}
		}
		return new Point(cx, cy);
	}
	
	public Point drawAlphabet(Graphics g, int bx, int by, int scale, int w) {
		return drawAlphabet(g, bx, by, scale, w, (typoascent + typodescent + linegap) * scale);
	}
	
	public Point drawAlphabet(Graphics g, int bx, int by, int scale, int w, int lh) {
		int cx = bx, cy = by;
		for (int ch = 0; ch < 0x110000; ch++) {
			if (characters.containsKey(ch)) {
				BitmapFontGlyph bm = characters.get(ch);
				if (cx - bx + (bm.getCharacterWidth() * scale) >= w) {
					cx = bx;
					cy += lh;
				}
				cx += bm.paint(g, cx, cy, scale);
			}
		}
		return new Point(cx, cy);
	}
	
	public void contractGlyphs() {
		for (BitmapFontGlyph glyph : characters.values()) {
			glyph.contract();
		}
	}
}
