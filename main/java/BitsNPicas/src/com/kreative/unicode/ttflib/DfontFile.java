package com.kreative.unicode.ttflib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DfontFile {
	private int dataOffset;
	private int mapOffset;
	private int attributes;
	private int typesOffset;
	private int namesOffset;
	private List<DfontResourceType> types;
	private Map<Integer,DfontResourceType> byType;
	private Map<String,DfontResourceType> byTypeString;
	
	public DfontFile(File file) throws IOException { read(file); }
	public DfontFile(InputStream in) throws IOException { read(in); }
	public DfontFile(byte[] data) throws IOException { read(data); }
	
	private void read(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		read(in);
		in.close();
	}
	
	private void read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[65536]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.close();
		read(out.toByteArray());
	}
	
	private void read(byte[] data) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(bin);
		readHead(in);
		readBody(in);
		in.close();
		bin.close();
	}
	
	private void readHead(DataInputStream in) throws IOException {
		this.dataOffset = in.readInt();
		this.mapOffset = in.readInt();
		in.reset();
		in.skipBytes(this.mapOffset + 22);
		this.attributes = in.readShort();
		this.typesOffset = in.readShort();
		this.namesOffset = in.readShort();
	}
	
	private void readBody(DataInputStream in) throws IOException {
		this.types = new ArrayList<DfontResourceType>();
		this.byType = new TreeMap<Integer,DfontResourceType>();
		this.byTypeString = new TreeMap<String,DfontResourceType>();
		// Read type entries
		in.reset();
		in.skipBytes(this.mapOffset + this.typesOffset);
		int count = (in.readShort() + 1) & 0xFFFF;
		for (int i = 0; i < count; i++) {
			DfontResourceType t = new DfontResourceType();
			t.readHead(in);
			this.types.add(t);
			this.byType.put(t.getType(), t);
			this.byTypeString.put(t.getTypeString(), t);
		}
		// Read resources
		for (DfontResourceType t : this.types) {
			t.readBody(in, this.dataOffset, this.mapOffset, this.typesOffset, this.namesOffset);
		}
		this.types = Collections.unmodifiableList(this.types);
		this.byType = Collections.unmodifiableMap(this.byType);
		this.byTypeString = Collections.unmodifiableMap(this.byTypeString);
	}
	
	public int getAttributes() { return attributes; }
	public List<DfontResourceType> getResourceTypes() { return types; }
	public Set<Integer> getResourceTypeIds() { return byType.keySet(); }
	public Set<String> getResourceTypeStrings() { return byTypeString.keySet(); }
	public DfontResourceType getResourceType(int type) { return byType.get(type); }
	public DfontResourceType getResourceType(String type) { return byTypeString.get(type); }
}
