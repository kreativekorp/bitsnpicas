package com.kreative.unicode.ttflib;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

public class TtfTable {
	private int tag;
	private String tagString;
	private int checksum;
	private int offset;
	private int length;
	private byte[] data;
	
	TtfTable() {}
	
	void readHead(DataInputStream in) throws IOException {
		this.tag = in.readInt();
		byte[] d = {
			(byte)(this.tag >> 24),
			(byte)(this.tag >> 16),
			(byte)(this.tag >>  8),
			(byte)(this.tag >>  0),
		};
		this.tagString = new String(d, "US-ASCII");
		this.checksum = in.readInt();
		this.offset = in.readInt();
		this.length = in.readInt();
	}
	
	void readBody(DataInputStream in, Map<Long,byte[]> dataCache) throws IOException {
		if (dataCache != null) {
			long key = (((long)this.offset) << 32) | ((long)this.length);
			if (dataCache.containsKey(key)) {
				this.data = dataCache.get(key);
				return;
			}
		}
		
		in.reset();
		in.skipBytes(this.offset);
		byte[] d = new byte[this.length];
		in.readFully(d);
		this.data = d;
		
		if (dataCache != null) {
			long key = (((long)this.offset) << 32) | ((long)this.length);
			dataCache.put(key, d);
		}
	}
	
	public int getTag() { return tag; }
	public String getTagString() { return tagString; }
	public int getChecksum() { return checksum; }
	public int getOffset() { return offset; }
	public int getLength() { return length; }
	public byte[] getData() { return data; }
}
