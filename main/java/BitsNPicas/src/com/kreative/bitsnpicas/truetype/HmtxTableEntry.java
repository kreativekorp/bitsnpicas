package com.kreative.bitsnpicas.truetype;

public class HmtxTableEntry {
	public int advanceWidth;
	public int leftSideBearing;
	
	public HmtxTableEntry() {
		this.advanceWidth = 0;
		this.leftSideBearing = 0;
	}
	
	public HmtxTableEntry(int advanceWidth, int leftSideBearing) {
		this.advanceWidth = advanceWidth;
		this.leftSideBearing = leftSideBearing;
	}
}
