package com.kreative.bitsnpicas.u8m;

import java.util.ArrayList;

public class U8MMapTable extends ArrayList<U8MMapData> {
	private static final long serialVersionUID = 1L;
	
	public int setMapLocations(int loc) {
		loc += this.size() * 4;
		for (U8MMapData m : this) loc = m.setMapLocation(loc);
		return loc;
	}
}
