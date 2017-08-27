package com.kreative.bitsnpicas.edit;

import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public interface GlyphListListener {
	public void codePointSelected(int codePoint);
	public void codePointOpened(int codePoint, FontGlyph glyph, Font<?> font);
}
