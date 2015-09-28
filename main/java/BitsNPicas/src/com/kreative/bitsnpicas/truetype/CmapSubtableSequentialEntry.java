package com.kreative.bitsnpicas.truetype;

public class CmapSubtableSequentialEntry extends CmapSubtableEntry {
	public int glyphIndex = 0;
	
	@Override
	public int getGlyphIndex(int charCode) {
		return glyphIndex + (charCode - startCharCode);
	}
}
