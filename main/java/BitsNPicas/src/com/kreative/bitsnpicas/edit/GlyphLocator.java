package com.kreative.bitsnpicas.edit;

import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;
import com.kreative.unicode.data.NameResolver;

public class GlyphLocator<G extends FontGlyph> {
	private final Font<G> font;
	private final GlyphListModel model;
	private final int index;
	private final Integer codePoint;
	private final String glyphName;
	
	public GlyphLocator(Font<G> font, GlyphListModel model, int index) {
		this.font = font;
		this.model = model;
		this.index = index;
		this.codePoint = model.isCodePoint(index) ? model.getCodePoint(index) : null;
		this.glyphName = model.isGlyphName(index) ? model.getGlyphName(index) : null;
	}
	
	public boolean isValid() {
		return codePoint != null || glyphName != null;
	}
	
	public G getGlyph() {
		if (codePoint != null) return font.getCharacter(codePoint);
		if (glyphName != null) return font.getNamedGlyph(glyphName);
		return null;
	}
	
	public G setGlyph(G g) {
		if (codePoint != null) return font.putCharacter(codePoint, g);
		if (glyphName != null) return font.putNamedGlyph(glyphName, g);
		return null;
	}
	
	public G removeGlyph() {
		if (codePoint != null) return font.removeCharacter(codePoint);
		if (glyphName != null) return font.removeNamedGlyph(glyphName);
		return null;
	}
	
	public Font<G> getGlyphFont() {
		return font;
	}
	
	public GlyphListModel getGlyphListModel() {
		return model;
	}
	
	public int getGlyphIndex() {
		if (model.tracksFont()) {
			if (codePoint != null) return model.indexOfCodePoint(codePoint);
			if (glyphName != null) return model.indexOfGlyphName(glyphName);
			return -1;
		}
		return index;
	}
	
	public boolean isCodePoint() {
		return codePoint != null;
	}
	
	public Integer getCodePoint() {
		return codePoint;
	}
	
	public boolean isGlyphName() {
		return glyphName != null;
	}
	
	public String getGlyphName() {
		return glyphName;
	}
	
	public String toString() {
		if (codePoint != null) {
			String h = Integer.toHexString(codePoint).toUpperCase();
			while (h.length() < 4) h = "0" + h;
			String n = NameResolver.instance(codePoint).getName(codePoint);
			return "U+" + h + " " + n + " from " + font.toString();
		}
		if (glyphName != null) {
			return glyphName + " from " + font.toString();
		}
		return "Undefined from " + font.toString();
	}
	
	public GlyphLocator<G> getPrevious() {
		int i = getGlyphIndex();
		if (i >= 0) {
			int n = model.getCellCount();
			for (int x = 0; x < n; x++) {
				if (i <= 0) i = n;
				i--;
				GlyphLocator<G> loc = new GlyphLocator<G>(font, model, i);
				if (loc.isValid()) return loc;
			}
		}
		return null;
	}
	
	public GlyphLocator<G> getNext() {
		int i = getGlyphIndex();
		if (i >= 0) {
			int n = model.getCellCount();
			for (int x = 0; x < n; x++) {
				i++;
				if (i >= n) i = 0;
				GlyphLocator<G> loc = new GlyphLocator<G>(font, model, i);
				if (loc.isValid()) return loc;
			}
		}
		return null;
	}
	
	public GlyphLocator<G> getPreviousDefined() {
		int i = getGlyphIndex();
		if (i >= 0) {
			int n = model.getCellCount();
			for (int x = 0; x < n; x++) {
				if (i <= 0) i = n;
				i--;
				GlyphLocator<G> loc = new GlyphLocator<G>(font, model, i);
				if (loc.getGlyph() != null) return loc;
			}
		}
		return null;
	}
	
	public GlyphLocator<G> getNextDefined() {
		int i = getGlyphIndex();
		if (i >= 0) {
			int n = model.getCellCount();
			for (int x = 0; x < n; x++) {
				i++;
				if (i >= n) i = 0;
				GlyphLocator<G> loc = new GlyphLocator<G>(font, model, i);
				if (loc.getGlyph() != null) return loc;
			}
		}
		return null;
	}
}
