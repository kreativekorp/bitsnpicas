package com.kreative.bitsnpicas.main;

import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;

public class BitmapOutputOptions {
	public int xSize = 100, ySize = 100;
	public boolean extendWinMetrics = false;
	public IDGenerator idgen = new IDGenerator.HashCode(128, 32768);
	public PointSizeGenerator sizegen = new PointSizeGenerator.Automatic(4, 127);
	public String encodingName = null;
	public Integer u8mLoadAddress = null;
	public boolean geosMega = false;
	public boolean geosKerning = false;
	public boolean geosUTF8 = false;
	public boolean fontxDoubleByte = false;
	public String fontxDoubleByteEncoding = null;
}
