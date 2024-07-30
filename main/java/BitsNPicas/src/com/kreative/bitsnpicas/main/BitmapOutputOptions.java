package com.kreative.bitsnpicas.main;

import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class BitmapOutputOptions {
	public int xSize = 100, ySize = 100;
	public boolean extendWinMetrics = false;
	public IDGenerator idgen = new IDGenerator.HashCode(128, 32768);
	public PointSizeGenerator sizegen = new PointSizeGenerator.Automatic(4, 127);
	public String encodingName = null;
	public Integer u8mLoadAddress = null;
	public Boolean amigaProportional = null;
	public boolean geosMega = false;
	public boolean geosKerning = false;
	public boolean geosUTF8 = false;
	public boolean fontxDoubleByte = false;
	public String fontxDoubleByteEncoding = null;
	public String psfLowEncoding = null;
	public String psfHighEncoding = null;
	public boolean psfUseLowEncoding = true;
	public boolean psfUseHighEncoding = false;
	public boolean psfUseAllGlyphs = true;
	public boolean psfUnicodeTable = true;
	
	public GlyphList getEncoding() {
		if (encodingName == null) return null;
		return EncodingList.instance().getGlyphList(encodingName);
	}
	
	public GlyphList getPsfLowEncoding() {
		if (psfLowEncoding == null) return null;
		return EncodingList.instance().getGlyphList(psfLowEncoding);
	}
	
	public GlyphList getPsfHighEncoding() {
		if (psfHighEncoding == null) return null;
		return EncodingList.instance().getGlyphList(psfHighEncoding);
	}
}
