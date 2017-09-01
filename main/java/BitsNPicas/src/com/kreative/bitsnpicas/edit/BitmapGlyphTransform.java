package com.kreative.bitsnpicas.edit;

import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.transformer.BoldBitmapFontGlyphTransformer;

public interface BitmapGlyphTransform {
	public void transform(BitmapFont font, BitmapFontGlyph glyph);
	
	public static class Bold implements BitmapGlyphTransform {
		private static final BoldBitmapFontGlyphTransformer tx =
			new BoldBitmapFontGlyphTransformer();
		public void transform(BitmapFont font, BitmapFontGlyph glyph) {
			new BitmapGlyphState(tx.transformGlyph(glyph)).apply(glyph);
		}
	}
	
	public static class Invert implements BitmapGlyphTransform {
		public void transform(BitmapFont font, BitmapFontGlyph glyph) {
			BitmapGlyphOps.expand(
				glyph, 0, -font.getEmAscent(), glyph.getCharacterWidth(),
				font.getEmAscent() + font.getEmDescent()
			);
			BitmapGlyphOps.invertRect(
				glyph, 0, -font.getEmAscent(), glyph.getCharacterWidth(),
				font.getEmAscent() + font.getEmDescent()
			);
		}
	}
}
