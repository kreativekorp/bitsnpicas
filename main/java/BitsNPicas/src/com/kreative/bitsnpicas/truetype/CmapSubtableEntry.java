package com.kreative.bitsnpicas.truetype;

public abstract class CmapSubtableEntry {
	public int startCharCode = 0;
	public int endCharCode = 0;
	
	public boolean contains(int charCode) {
		return (charCode >= startCharCode && charCode <= endCharCode);
	}
	
	public abstract int getGlyphIndex(int charCode);
}
