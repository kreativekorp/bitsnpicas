package com.kreative.unicode.ttflib;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DfontResourceType {
	private int type;
	private String typeString;
	private int count;
	private int offset;
	private List<DfontResource> resources;
	private Map<Integer,DfontResource> byId;
	private Map<String,DfontResource> byName;
	
	DfontResourceType() {}
	
	void readHead(DataInputStream in) throws IOException {
		this.type = in.readInt();
		byte[] d = {
			(byte)(this.type >> 24),
			(byte)(this.type >> 16),
			(byte)(this.type >>  8),
			(byte)(this.type >>  0),
		};
		this.typeString = new String(d, "MacRoman");
		this.count = (in.readShort() + 1) & 0xFFFF;
		this.offset = in.readShort();
	}
	
	void readBody(DataInputStream in, int dataOffset, int mapOffset, int typesOffset, int namesOffset) throws IOException {
		this.resources = new ArrayList<DfontResource>();
		this.byId = new TreeMap<Integer,DfontResource>();
		this.byName = new TreeMap<String,DfontResource>();
		// Read resource entries
		in.reset();
		in.skipBytes(mapOffset + typesOffset + this.offset);
		for (int i = 0; i < count; i++) {
			DfontResource r = new DfontResource();
			r.readHead(in);
			this.resources.add(r);
			this.byId.put(r.getId(), r);
		}
		// Read resource names and data
		for (DfontResource r : this.resources) {
			r.readBody(in, dataOffset, mapOffset, namesOffset);
			if (r.getName() != null) this.byName.put(r.getName(), r);
		}
		this.resources = Collections.unmodifiableList(this.resources);
		this.byId = Collections.unmodifiableMap(this.byId);
		this.byName = Collections.unmodifiableMap(this.byName);
	}
	
	public int getType() { return type; }
	public String getTypeString() { return typeString; }
	public List<DfontResource> getResources() { return resources; }
	public Set<Integer> getResourceIds() { return byId.keySet(); }
	public Set<String> getResourceNames() { return byName.keySet(); }
	public DfontResource getResource(int id) { return byId.get(id); }
	public DfontResource getResource(String name) { return byName.get(name); }
}
