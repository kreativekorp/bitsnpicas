package com.kreative.bitsnpicas.transformer;

import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontGlyphTransformer;

public class BoldBitmapFontGlyphTransformer implements BitmapFontGlyphTransformer {
	private final boolean extraWidth;
	
	public BoldBitmapFontGlyphTransformer(boolean extraWidth) {
		this.extraWidth = extraWidth;
	}
	
	public BitmapFontGlyph transformGlyph(BitmapFontGlyph glyph) {
		int x = glyph.getGlyphOffset();
		int w = glyph.getCharacterWidth();
		int a = glyph.getGlyphAscent();
		byte[][] oldg = glyph.getGlyph();
		byte[][] newg = new byte[oldg.length][];
		for (int j = 0; j < oldg.length; j++) {
			newg[j] = new byte[oldg[j].length+1];
			for (int i = 0; i < oldg[j].length; i++) {
				newg[j][i] = (byte)((newg[j][i] & 0xFF) + (255 - (newg[j][i] & 0xFF)) * (oldg[j][i] & 0xFF) / 255);
				newg[j][i+1] = (byte)((newg[j][i+1] & 0xFF) + (255 - (newg[j][i+1] & 0xFF)) * (oldg[j][i] & 0xFF) / 255);
			}
		}
		if (extraWidth) {
			if (w == 0) {
				if (x < 0) {
					x--;
				}
			} else {
				w++;
			}
		}
		return new BitmapFontGlyph(newg, x, w, a);
	}
}
