package com.kreative.unicode.ttflib;

import java.io.DataInputStream;
import java.io.IOException;

public class DfontResource {
	private int id;
	private int attributes;
	private int nameOffset;
	private int dataOffset;
	private String name;
	private byte[] data;
	
	DfontResource() {}
	
	void readHead(DataInputStream in) throws IOException {
		this.id = in.readShort();
		this.nameOffset = in.readShort();
		int tmp = in.readInt();
		this.attributes = tmp >> 24;
		this.dataOffset = tmp & 0xFFFFFF;
		in.readInt();
	}
	
	void readBody(DataInputStream in, int dataOffset, int mapOffset, int namesOffset) throws IOException {
		// Read name
		if (this.nameOffset < 0) {
			this.name = null;
		} else {
			in.reset();
			in.skipBytes(mapOffset + namesOffset + this.nameOffset);
			int l = in.readUnsignedByte();
			byte[] d = new byte[l];
			in.readFully(d);
			this.name = new String(d, "MacRoman");
		}
		// Read data
		in.reset();
		in.skipBytes(dataOffset + this.dataOffset);
		int l = in.readInt();
		if (l < 0) l = 0;
		byte[] d = new byte[l];
		in.readFully(d);
		this.data = d;
	}
	
	public int getId() { return id; }
	public int getAttributes() { return attributes; }
	public String getName() { return name; }
	public byte[] getData() { return data; }
}
