package com.kreative.bitsnpicas.main;

import com.kreative.bitsnpicas.unicode.EncodingList;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class BitmapInputOptions {
	public String encodingName = null;
	public String fontxDoubleByteEncoding = null;
	
	public EncodingTable getEncoding() {
		if (encodingName == null) return null;
		return EncodingList.instance().get(encodingName);
	}
}
