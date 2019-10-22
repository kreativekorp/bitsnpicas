package com.kreative.bitsnpicas.u8m;

import java.util.ArrayList;

public class U8MGlyphTable extends ArrayList<U8MGlyph> {
	private static final long serialVersionUID = 1L;
	
	public int setBitmapLocations(int loc) {
		loc += this.size() * 4;
		for (U8MGlyph g : this) loc = g.setBitmapLocation(loc);
		return loc;
	}
}
