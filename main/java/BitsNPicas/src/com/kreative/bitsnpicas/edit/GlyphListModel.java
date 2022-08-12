package com.kreative.bitsnpicas.edit;

public interface GlyphListModel {
	public abstract boolean tracksFont();
	public abstract int getCellCount();
	public abstract boolean isCodePoint(int index);
	public abstract Integer getCodePoint(int index);
	public abstract int indexOfCodePoint(Integer codePoint);
	public abstract boolean isGlyphName(int index);
	public abstract String getGlyphName(int index);
	public abstract int indexOfGlyphName(String name);
	public abstract String toString();
	public abstract String getURL();
	public abstract String getIconGroup();
}
