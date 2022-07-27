package com.kreative.bitsnpicas.edit;

import java.util.List;

public class GlyphListGlyphNameModel implements GlyphListModel {
	private final List<String> glyphNames;
	private final String name;
	private final String url;
	
	public GlyphListGlyphNameModel(List<String> glyphNames, String name, String url) {
		this.glyphNames = glyphNames;
		this.name = name;
		this.url = url;
	}
	
	@Override
	public boolean isGlyphName(int index) {
		String gn = glyphNames.get(index);
		return gn != null && gn.length() > 0;
	}
	
	@Override
	public String getGlyphName(int index) {
		return glyphNames.get(index);
	}
	
	@Override
	public int indexOfGlyphName(String name) {
		return glyphNames.indexOf(name);
	}
	
	@Override public boolean tracksFont() { return false; }
	@Override public int getCellCount() { return glyphNames.size(); }
	@Override public boolean isCodePoint(int index) { return false; }
	@Override public Integer getCodePoint(int index) { return null; }
	@Override public int indexOfCodePoint(Integer codePoint) { return -1; }
	@Override public String toString() { return name; }
	@Override public String getURL() { return url; }
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof GlyphListGlyphNameModel) {
			GlyphListGlyphNameModel that = (GlyphListGlyphNameModel)o;
			return this.glyphNames.equals(that.glyphNames) && this.name.equals(that.name);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.glyphNames.hashCode() ^ this.name.hashCode();
	}
}
