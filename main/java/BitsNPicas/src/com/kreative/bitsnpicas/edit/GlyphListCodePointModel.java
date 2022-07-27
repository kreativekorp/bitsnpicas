package com.kreative.bitsnpicas.edit;

import java.util.List;
import com.kreative.unicode.data.Block;

public class GlyphListCodePointModel implements GlyphListModel {
	private final List<Integer> codePoints;
	private final String name;
	private final String url;
	
	public GlyphListCodePointModel(List<Integer> codePoints, String name, String url) {
		this.codePoints = codePoints;
		this.name = name;
		this.url = url;
	}
	
	public GlyphListCodePointModel(Block block) {
		this.codePoints = block;
		this.name = block.name;
		if (isPUABlock(block)) {
			this.url = null;
		} else {
			String h = Integer.toHexString(block.firstCodePoint).toUpperCase();
			while (h.length() < 4) h = "0" + h;
			this.url = "http://www.unicode.org/charts/PDF/U" + h + ".pdf";
		}
	}
	
	private static boolean isPUABlock(Block block) {
		if (block.firstCodePoint >= 0xE000 && block.lastCodePoint <= 0xF8FF) {
			return (block.firstCodePoint != 0xE000 || block.lastCodePoint != 0xF8FF);
		}
		if (block.firstCodePoint >= 0xF0000 && block.lastCodePoint <= 0xFFFFF) {
			return (block.firstCodePoint != 0xF0000 || block.lastCodePoint != 0xFFFFF);
		}
		if (block.firstCodePoint >= 0x100000 && block.lastCodePoint <= 0x10FFFF) {
			return (block.firstCodePoint != 0x100000 || block.lastCodePoint != 0x10FFFF);
		}
		return false;
	}
	
	@Override
	public boolean isCodePoint(int index) {
		Integer cp = codePoints.get(index);
		return cp != null && Character.isValidCodePoint(cp);
	}
	
	@Override
	public Integer getCodePoint(int index) {
		return codePoints.get(index);
	}
	
	@Override
	public int indexOfCodePoint(Integer codePoint) {
		return codePoints.indexOf(codePoint);
	}
	
	@Override public boolean tracksFont() { return false; }
	@Override public int getCellCount() { return codePoints.size(); }
	@Override public boolean isGlyphName(int index) { return false; }
	@Override public String getGlyphName(int index) { return null; }
	@Override public int indexOfGlyphName(String name) { return -1; }
	@Override public String toString() { return name; }
	@Override public String getURL() { return url; }
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof GlyphListCodePointModel) {
			GlyphListCodePointModel that = (GlyphListCodePointModel)o;
			return this.codePoints.equals(that.codePoints) && this.name.equals(that.name);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.codePoints.hashCode() ^ this.name.hashCode();
	}
}
