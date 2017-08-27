package com.kreative.bitsnpicas.edit;

import com.kreative.bitsnpicas.BitmapFontGlyph;

public class BitmapGlyphState {
	private final int x;
	private final int y;
	private final byte[][] glyph;
	
	public BitmapGlyphState(BitmapFontGlyph glyph) {
		this.x = glyph.getX();
		this.y = glyph.getY();
		this.glyph = memcpy(glyph.getGlyph());
	}
	
	public void apply(BitmapFontGlyph glyph) {
		glyph.setXY(this.x, this.y);
		glyph.setGlyph(memcpy(this.glyph));
	}
	
	private static byte[][] memcpy(byte[][] a) {
		byte[][] b = new byte[a.length][];
		for (int i = 0; i < a.length; i++) b[i] = memcpy(a[i]);
		return b;
	}
	
	private static byte[] memcpy(byte[] a) {
		byte[] b = new byte[a.length];
		for (int i = 0; i < a.length; i++) b[i] = a[i];
		return b;
	}
}
