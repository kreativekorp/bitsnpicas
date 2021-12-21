package com.kreative.bitsnpicas.main;

import com.kreative.bitsnpicas.unicode.EncodingList;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class BitmapInputOptions {
	public String encodingName = null;
	public String fontxDoubleByteEncoding = null;
	public String psfLowEncoding = null;
	public String psfHighEncoding = null;
	public int psfPuaBase = -1;
	
	public EncodingTable getEncoding() {
		if (encodingName == null) return null;
		return EncodingList.instance().get(encodingName);
	}
	
	public EncodingTable getPsfLowEncoding() {
		if (psfLowEncoding == null) return null;
		return EncodingList.instance().get(psfLowEncoding);
	}
	
	public EncodingTable getPsfHighEncoding() {
		if (psfHighEncoding == null) return null;
		return EncodingList.instance().get(psfHighEncoding);
	}
}
