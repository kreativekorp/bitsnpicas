package com.kreative.bitsnpicas.main;

import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class BitmapInputOptions {
	public String encodingName = null;
	public String fontxDoubleByteEncoding = null;
	public String psfLowEncoding = null;
	public String psfHighEncoding = null;
	public int psfPuaBase = -1;
	
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
