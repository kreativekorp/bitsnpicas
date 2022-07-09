package com.kreative.unicode.ttflib;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class TtcFont extends TtfBase {
	private int offset;
	
	TtcFont() {}
	
	void readHead(DataInputStream in) throws IOException {
		this.offset = in.readInt();
	}
	
	void readBody(DataInputStream in, Map<Long,byte[]> dataCache) throws IOException {
		in.reset();
		in.skipBytes(this.offset);
		super.read(in, dataCache);
	}
}
