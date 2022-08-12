package com.kreative.bitsnpicas.edit;

import java.util.ArrayList;
import java.util.List;
import com.kreative.bitsnpicas.Font;

public class GlyphListFontModel implements GlyphListModel {
	private final Font<?> font;
	private final boolean codePoints;
	private final boolean glyphNames;
	private final List<Object> ids;
	private final String name;
	
	public GlyphListFontModel(Font<?> font, boolean codePoints, boolean glyphNames, String name) {
		this.font = font;
		this.codePoints = codePoints;
		this.glyphNames = glyphNames;
		this.ids = new ArrayList<Object>();
		if (codePoints) {
			ids.addAll(font.characters(false).keySet());
		}
		if (glyphNames) {
			int split = ids.size();
			ids.addAll(font.namedGlyphs(false).keySet());
			ids.remove(".notdef");
			ids.add(split, ".notdef");
		}
		this.name = name;
	}
	
	@Override
	public boolean tracksFont() {
		ids.clear();
		if (codePoints) {
			ids.addAll(font.characters(false).keySet());
		}
		if (glyphNames) {
			int split = ids.size();
			ids.addAll(font.namedGlyphs(false).keySet());
			ids.remove(".notdef");
			ids.add(split, ".notdef");
		}
		return true;
	}
	
	@Override
	public int getCellCount() {
		return ids.size();
	}
	
	@Override
	public boolean isCodePoint(int index) {
		Object id = ids.get(index);
		if (!(id instanceof Integer)) return false;
		return Character.isValidCodePoint((Integer)id);
	}
	
	@Override
	public Integer getCodePoint(int index) {
		Object id = ids.get(index);
		if (!(id instanceof Integer)) return null;
		return (Integer)id;
	}
	
	@Override
	public int indexOfCodePoint(Integer codePoint) {
		return ids.indexOf(codePoint);
	}
	
	@Override
	public boolean isGlyphName(int index) {
		Object id = ids.get(index);
		if (!(id instanceof String)) return false;
		return ((String)id).length() > 0;
	}
	
	@Override
	public String getGlyphName(int index) {
		Object id = ids.get(index);
		if (!(id instanceof String)) return null;
		return (String)id;
	}
	
	@Override
	public int indexOfGlyphName(String name) {
		return ids.indexOf(name);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String getURL() {
		return null;
	}
	
	@Override
	public String getIconGroup() {
		return "font";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof GlyphListFontModel) {
			GlyphListFontModel that = (GlyphListFontModel)o;
			return (
				this.font == that.font &&
				this.codePoints == that.codePoints &&
				this.glyphNames == that.glyphNames &&
				this.name.equals(that.name)
			);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.font.hashCode() ^ this.name.hashCode();
	}
}
