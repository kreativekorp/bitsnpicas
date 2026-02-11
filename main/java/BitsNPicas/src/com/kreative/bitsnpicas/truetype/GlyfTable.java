package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class GlyfTable extends ListBasedTable<byte[]> {
	@Override
	public String tableName() {
		return "glyf";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{"loca"};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		for (byte[] data : this) {
			out.write(data);
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		List<Integer> locations = (LocaTable)dependencies[0];
		this.clear();
		int s = locations.get(0);
		for (int i = 1; i < locations.size(); i++) {
			int e = locations.get(i);
			byte[] data = new byte[e - s];
			in.reset();
			in.skipBytes(s);
			in.readFully(data);
			this.add(data);
			s = e;
		}
	}
}
