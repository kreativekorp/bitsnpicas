package com.kreative.bitsnpicas;

import java.awt.*;

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
	
	public void setXHeight() {
		if (characters.containsKey((int)'x')) {
			BitmapFontGlyph g = characters.get((int)'x');
			xheight = g.getGlyphAscent();
		}
	}
	
	public Point draw(Graphics g, String s, Point b) {
		return draw(g, s, b.x, b.y, Integer.MAX_VALUE, typoascent+typodescent+linegap);
	}
	
	public Point draw(Graphics g, String s, Point b, int w) {
		return draw(g, s, b.x, b.y, w, typoascent+typodescent+linegap);
	}
	
	public Point draw(Graphics g, String s, Point b, int w, int lh) {
		return draw(g, s, b.x, b.y, w, lh);
	}
	
	public Point draw(Graphics g, String s, int bx, int by) {
		return draw(g, s, bx, by, Integer.MAX_VALUE, typoascent+typodescent+linegap);
	}

	public Point draw(Graphics g, String s, int bx, int by, int w) {
		return draw(g, s, bx, by, w, typoascent+typodescent+linegap);
	}
	
	public Point draw(Graphics g, String s, int bx, int by, int w, int lh) {
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
					if (cx - bx + bm.getCharacterWidth() >= w) {
						cx = bx;
						cy += lh;
					}
					cx += bm.paint(g, cx, cy, 1);
				}
				else if (characters.containsKey(-1)) {
					BitmapFontGlyph bm = characters.get(-1);
					if (cx - bx + bm.getCharacterWidth() >= w) {
						cx = bx;
						cy += lh;
					}
					cx += bm.paint(g, cx, cy, 1);
				}
				break;
			}
		}
		return new Point(cx, cy);
	}
	
	public Point drawAlphabet(Graphics g, Point b) {
		return drawAlphabet(g, b.x, b.y, Integer.MAX_VALUE, typoascent+typodescent+linegap);
	}
	
	public Point drawAlphabet(Graphics g, Point b, int w) {
		return drawAlphabet(g, b.x, b.y, w, typoascent+typodescent+linegap);
	}
	
	public Point drawAlphabet(Graphics g, Point b, int w, int lh) {
		return drawAlphabet(g, b.x, b.y, w, lh);
	}
	
	public Point drawAlphabet(Graphics g, int bx, int by) {
		return drawAlphabet(g, bx, by, Integer.MAX_VALUE, typoascent+typodescent+linegap);
	}

	public Point drawAlphabet(Graphics g, int bx, int by, int w) {
		return drawAlphabet(g, bx, by, w, typoascent+typodescent+linegap);
	}
	
	public Point drawAlphabet(Graphics g, int bx, int by, int w, int lh) {
		int cx = bx, cy = by;
		for (int ch=0; ch<0x110000; ch++) {
			if (characters.containsKey(ch)) {
				BitmapFontGlyph bm = characters.get(ch);
				if (cx - bx + bm.getCharacterWidth() >= w) {
					cx = bx;
					cy += lh;
				}
				cx += bm.paint(g, cx, cy, 1);
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
