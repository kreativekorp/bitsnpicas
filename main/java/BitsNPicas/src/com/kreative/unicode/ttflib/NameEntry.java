package com.kreative.unicode.ttflib;

import java.io.DataInputStream;
import java.io.IOException;

public class NameEntry {
	private int platformId;
	private int encodingId;
	private int languageId;
	private int nameId;
	private int length;
	private int offset;
	private byte[] data;
	private String name;
	
	NameEntry() {}
	
	void readHead(DataInputStream in) throws IOException {
		this.platformId = in.readUnsignedShort();
		this.encodingId = in.readUnsignedShort();
		this.languageId = in.readUnsignedShort();
		this.nameId = in.readUnsignedShort();
		this.length = in.readUnsignedShort();
		this.offset = in.readUnsignedShort();
	}
	
	void readBody(DataInputStream in, int stringOffset) throws IOException {
		in.reset();
		in.skipBytes(stringOffset + this.offset);
		byte[] d = new byte[this.length];
		in.readFully(d);
		this.data = d;
		if (this.platformId == 0 || this.platformId == 2 || this.platformId == 10) {
			this.name = new String(d, "UTF-16BE");
		} else if (this.platformId == 3 && (this.encodingId == 1 || this.encodingId == 10)) {
			this.name = new String(d, "UTF-16BE");
		} else if (this.platformId == 1 && this.encodingId == 0) {
			this.name = new String(d, "MacRoman");
		} else {
			this.name = null;
		}
	}
	
	public int getPlatformId() { return platformId; }
	public int getEncodingId() { return encodingId; }
	public int getLanguageId() { return languageId; }
	public int getNameId() { return nameId; }
	public byte[] getData() { return data; }
	public String getName() { return name; }
	
	public boolean isEnglish() {
		switch (platformId) {
			case 0: return true;
			case 1: return encodingId == 0 && languageId == 0;
			case 2: return true;
			case 3: return (encodingId == 1 || encodingId == 10) && languageId == 1033;
			case 10: return true;
			default: return false;
		}
	}
}
