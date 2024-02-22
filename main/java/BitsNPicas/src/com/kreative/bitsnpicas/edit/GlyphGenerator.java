package com.kreative.bitsnpicas.edit;

import java.util.List;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public abstract class GlyphGenerator<G extends FontGlyph> {
	public static enum Result { NO_CHANGE, CONTENT_CHANGED, REPERTOIRE_CHANGED }
	public abstract String getName();
	public abstract Class<G> getGlyphClass();
	public abstract Result generate(Font<G> font, List<GlyphLocator<G>> locators);
}
