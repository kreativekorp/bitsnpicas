package com.kreative.unicode.ttflib;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DfontResource {
	public static final int ATTRIBUTE_COMPRESSED = 0x01;
	public static final int ATTRIBUTE_CHANGED    = 0x02;
	public static final int ATTRIBUTE_PRELOAD    = 0x04;
	public static final int ATTRIBUTE_PROTECTED  = 0x08;
	public static final int ATTRIBUTE_LOCKED     = 0x10;
	public static final int ATTRIBUTE_PURGEABLE  = 0x20;
	public static final int ATTRIBUTE_SYSHEAP    = 0x40;
	public static final int ATTRIBUTE_RESERVED   = 0x80;
	
	public static final int OWNER_TYPE_DRVR = -8;
	public static final int OWNER_TYPE_WDEF = -7;
	public static final int OWNER_TYPE_MDEF = -6;
	public static final int OWNER_TYPE_CDEF = -5;
	public static final int OWNER_TYPE_PDEF = -4;
	public static final int OWNER_TYPE_PACK = -3;
	public static final int OWNER_TYPE_RSV1 = -2;
	public static final int OWNER_TYPE_RSV2 = -1;
	
	public static int ownedId(int ownerType, int ownerId, int subId) {
		return (ownerType << 11) | ((ownerId & 0x3F) << 5) | (subId & 0x1F);
	}
	
	private final int type;
	private final String typeString;
	private int id;
	private int attributes;
	private int nameOffset;
	private int dataOffset;
	private String name;
	private byte[] data;
	
	public DfontResource(String type, int id, int attr, String name, byte[] data, int off, int len) {
		this(DfontResourceType.toInteger(type), id, attr, name, data, off, len);
	}
	
	public DfontResource(int type, int id, int attr, String name, byte[] data, int off, int len) {
		this(type, DfontResourceType.toString(type));
		this.id = id;
		this.attributes = attr;
		this.name = name;
		this.setData(data, off, len);
	}
	
	DfontResource(int type, String typeString) {
		this.type = type;
		this.typeString = typeString;
	}
	
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
	
	public int getType() { return type; }
	public String getTypeString() { return typeString; }
	public int getId() { return id; }
	public int getOwnerType() { return id >> 11; }
	public int getOwnerId() { return (id >> 5) & 0x3F; }
	public int getSubId() { return id & 0x1F; }
	public int getAttributes() { return attributes; }
	public String getName() { return name; }
	public byte[] getData() { return data; }
	
	public void setData(byte[] data, int offset, int length) {
		this.data = new byte[length];
		for (int i = 0; i < length; i++) {
			this.data[i] = data[offset];
			offset++;
		}
	}
	
	int writeData(DataOutputStream out, int ptr) throws IOException {
		byte[] d = this.data;
		int len = (d.length < 0xFFFFFF) ? d.length : 0xFFFFFF;
		out.writeInt(len);
		out.write(d, 0, len);
		this.dataOffset = ptr;
		return ptr + 4 + len;
	}
	
	int writeName(DataOutputStream out, int ptr) throws IOException {
		if (this.name == null) {
			this.nameOffset = -1;
			return ptr;
		}
		byte[] d = this.name.getBytes("MacRoman");
		int len = (d.length < 0xFF) ? d.length : 0xFF;
		out.writeByte(len);
		out.write(d, 0, len);
		this.nameOffset = ptr;
		return ptr + 1 + len;
	}
	
	void writeHead(DataOutputStream out) throws IOException {
		out.writeShort(this.id);
		out.writeShort(this.nameOffset);
		out.writeInt((this.attributes << 24) | this.dataOffset);
		out.writeInt(0);
	}
}
