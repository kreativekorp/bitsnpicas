package com.kreative.unicode.ttflib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameTable {
	private List<NameEntry> names;
	
	public NameTable(InputStream in) throws IOException { read(in); }
	public NameTable(byte[] data) throws IOException { read(data); }
	
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
		readData(in);
		in.close();
		bin.close();
	}
	
	private void readData(DataInputStream in) throws IOException {
		this.names = new ArrayList<NameEntry>();
		
		in.readUnsignedShort();
		int count = in.readUnsignedShort();
		int stringOffset = in.readUnsignedShort();
		
		for (int i = 0; i < count; i++) {
			NameEntry e = new NameEntry();
			e.readHead(in);
			this.names.add(e);
		}
		
		for (NameEntry e : this.names) {
			e.readBody(in, stringOffset);
		}
		
		this.names = Collections.unmodifiableList(this.names);
	}
	
	public List<NameEntry> getNames() { return names; }
	
	public String getName(int nameId) {
		String name = null;
		for (NameEntry e : names) {
			if (e.isEnglish() && e.getNameId() == nameId) {
				if (name == null || e.getName().length() < name.length()) {
					name = e.getName();
				}
			}
		}
		return name;
	}
}
