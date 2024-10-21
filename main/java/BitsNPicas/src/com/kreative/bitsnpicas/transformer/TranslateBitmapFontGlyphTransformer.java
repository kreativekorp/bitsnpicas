package com.kreative.bitsnpicas.transformer;

import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontGlyphTransformer;

public class TranslateBitmapFontGlyphTransformer implements BitmapFontGlyphTransformer {
	private final int x, w, a;
	
	public TranslateBitmapFontGlyphTransformer(int x, int w, int a) {
		this.x = x;
		this.w = w;
		this.a = a;
	}
	
	public BitmapFontGlyph transformGlyph(BitmapFontGlyph glyph) {
		int x = glyph.getGlyphOffset() + this.x;
		int w = glyph.getCharacterWidth() + this.w;
		int a = glyph.getGlyphAscent() + this.a;
		byte[][] g = glyph.getGlyph();
		if (w < 0) w = 0;
		return new BitmapFontGlyph(g, x, w, a);
	}
}
