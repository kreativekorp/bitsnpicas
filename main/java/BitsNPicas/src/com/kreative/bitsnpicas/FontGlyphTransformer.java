package com.kreative.bitsnpicas;

public interface FontGlyphTransformer<T extends FontGlyph> {
	public T transformGlyph(T glyph);
}
