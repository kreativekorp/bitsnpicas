package com.kreative.bitsnpicas.edit;

import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public interface GlyphListListener<G extends FontGlyph> {
	public void selectionChanged(GlyphList<G> gl, Font<G> font);
	public void selectionOpened(GlyphList<G> gl, Font<G> font);
	public void metricsChanged(GlyphList<G> gl, Font<G> font);
	public void glyphsChanged(GlyphList<G> gl, Font<G> font);
}
