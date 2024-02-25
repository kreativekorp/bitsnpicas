package com.kreative.unicode.ttflib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	private final List<DfontResourceType> rwTypes = new ArrayList<DfontResourceType>();
	private final Map<Integer,DfontResourceType> rwByType = new TreeMap<Integer,DfontResourceType>();
	private final Map<String,DfontResourceType> rwByTypeString = new TreeMap<String,DfontResourceType>();
	private final List<DfontResourceType> roTypes = Collections.unmodifiableList(rwTypes);
	private final Map<Integer,DfontResourceType> roByType = Collections.unmodifiableMap(rwByType);
	private final Map<String,DfontResourceType> roByTypeString = Collections.unmodifiableMap(rwByTypeString);
	
	public DfontFile() {}
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
		// Read type entries
		in.reset();
		in.skipBytes(mapOffset + typesOffset);
		int count = (in.readShort() + 1) & 0xFFFF;
		for (int i = 0; i < count; i++) {
			int type = in.readInt();
			String typeString = DfontResourceType.toString(type);
			DfontResourceType t = new DfontResourceType(type, typeString);
			t.readHead(in);
			rwTypes.add(t);
			rwByType.put(type, t);
			rwByTypeString.put(typeString, t);
		}
		// Read resources
		for (DfontResourceType t : rwTypes) {
			t.readBody(in, dataOffset, mapOffset, typesOffset, namesOffset);
		}
	}
	
	public int getAttributes() { return attributes; }
	public List<DfontResourceType> getResourceTypes() { return roTypes; }
	public Set<Integer> getResourceTypeIds() { return roByType.keySet(); }
	public Set<String> getResourceTypeStrings() { return roByTypeString.keySet(); }
	public DfontResourceType getResourceType(int type) { return roByType.get(type); }
	public DfontResourceType getResourceType(String type) { return roByTypeString.get(type); }
	
	public DfontResource getResource(int type, int id) {
		DfontResourceType t = getResourceType(type);
		return (t != null) ? t.getResource(id) : null;
	}
	public DfontResource getResource(int type, String name) {
		DfontResourceType t = getResourceType(type);
		return (t != null) ? t.getResource(name) : null;
	}
	public DfontResource getResource(String type, int id) {
		DfontResourceType t = getResourceType(type);
		return (t != null) ? t.getResource(id) : null;
	}
	public DfontResource getResource(String type, String name) {
		DfontResourceType t = getResourceType(type);
		return (t != null) ? t.getResource(name) : null;
	}
	
	public boolean addResource(DfontResource r) {
		if (r == null) return false;
		DfontResourceType t = getResourceType(r.getType());
		if (t == null) {
			t = new DfontResourceType(r.getType(), r.getTypeString());
			rwTypes.add(t);
			rwByType.put(r.getType(), t);
			rwByTypeString.put(r.getTypeString(), t);
		}
		return t.addResource(r);
	}
	
	public boolean removeResource(DfontResource r) {
		DfontResourceType t = getResourceType(r.getType());
		boolean ret = (t != null) ? t.removeResource(r) : false;
		checkType(t); return ret;
	}
	public DfontResource removeResource(int type, int id) {
		DfontResourceType t = getResourceType(type);
		DfontResource ret = (t != null) ? t.removeResource(id) : null;
		checkType(t); return ret;
	}
	public DfontResource removeResource(int type, String name) {
		DfontResourceType t = getResourceType(type);
		DfontResource ret = (t != null) ? t.removeResource(name) : null;
		checkType(t); return ret;
	}
	public DfontResource removeResource(String type, int id) {
		DfontResourceType t = getResourceType(type);
		DfontResource ret = (t != null) ? t.removeResource(id) : null;
		checkType(t); return ret;
	}
	public DfontResource removeResource(String type, String name) {
		DfontResourceType t = getResourceType(type);
		DfontResource ret = (t != null) ? t.removeResource(name) : null;
		checkType(t); return ret;
	}
	private void checkType(DfontResourceType t) {
		if (t != null && t.getResources().isEmpty()) {
			rwTypes.remove(t);
			rwByType.values().remove(t);
			rwByTypeString.values().remove(t);
		}
	}
	
	public byte[] write() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		write(out);
		return out.toByteArray();
	}
	
	public void write(File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		write(out);
		out.close();
	}
	
	public void write(OutputStream out) throws IOException {
		writeImpl(new DataOutputStream(out));
	}
	
	private void writeImpl(DataOutputStream out) throws IOException {
		ByteArrayOutputStream dataArr = new ByteArrayOutputStream();
		ByteArrayOutputStream nameArr = new ByteArrayOutputStream();
		ByteArrayOutputStream listArr = new ByteArrayOutputStream();
		ByteArrayOutputStream typeArr = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(dataArr);
		DataOutputStream nameOut = new DataOutputStream(nameArr);
		DataOutputStream listOut = new DataOutputStream(listArr);
		DataOutputStream typeOut = new DataOutputStream(typeArr);
		int dataPtr = 0;
		int namePtr = 0;
		int listPtr = roTypes.size() * 8 + 2;
		typeOut.writeShort(roTypes.size() - 1);
		for (DfontResourceType t : roTypes) {
			dataPtr = t.writeData(dataOut, dataPtr);
			namePtr = t.writeName(nameOut, namePtr);
			listPtr = t.writeList(listOut, listPtr);
			t.writeHead(typeOut);
		}
		out.writeInt(256);                    // resource data offset
		out.writeInt(dataPtr + 256);          // resource map offset
		out.writeInt(dataPtr);                // resource data size
		out.writeInt(namePtr + listPtr + 28); // resource map size
		out.write(new byte[240]);             // empty space
		out.write(dataArr.toByteArray());     // resource data
		out.writeInt(256);                    // resource data offset
		out.writeInt(dataPtr + 256);          // resource map offset
		out.writeInt(dataPtr);                // resource data size
		out.writeInt(namePtr + listPtr + 28); // resource map size
		out.writeInt(0);                      // next resource map
		out.writeShort(0);                    // file ref
		out.writeShort(attributes);           // attributes
		out.writeShort(28);                   // offset from map to type list
		out.writeShort(listPtr + 28);         // offset from map to name list
		out.write(typeArr.toByteArray());     // type list
		out.write(listArr.toByteArray());     // resource list
		out.write(nameArr.toByteArray());     // name list
	}
}
