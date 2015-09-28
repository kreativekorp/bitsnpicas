package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SbixTable extends ListBasedTable<SbixSubtable> {
	public static final int VERSION_DEFAULT = 0x00010001;
	
	public int version = VERSION_DEFAULT;
	
	@Override
	public String tableName() {
		return "sbix";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{"maxp"};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		int numGlyphs = ((MaxpTable)dependencies[0]).numGlyphs;
		out.writeInt(version);
		out.writeInt(this.size());
		List<byte[]> subtableData = new ArrayList<byte[]>();
		int currentLocation = 8 + this.size() * 4;
		for (SbixSubtable st : this) {
			out.writeInt(currentLocation);
			byte[] d = st.compile(numGlyphs);
			subtableData.add(d);
			currentLocation += d.length;
		}
		for (byte[] d : subtableData) {
			out.write(d);
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		int numGlyphs = ((MaxpTable)dependencies[0]).numGlyphs;
		version = in.readInt();
		int numTables = in.readInt();
		int[] tableOffset = new int[numTables + 1];
		for (int i = 0; i < numTables; i++) {
			tableOffset[i] = in.readInt();
		}
		tableOffset[numTables] = length;
		this.clear();
		for (int i = 0; i < numTables; i++) {
			in.reset();
			in.skipBytes(tableOffset[i]);
			byte[] d = new byte[tableOffset[i+1] - tableOffset[i]];
			in.readFully(d);
			SbixSubtable e = new SbixSubtable();
			e.decompile(d, numGlyphs);
			this.add(e);
		}
	}
}
