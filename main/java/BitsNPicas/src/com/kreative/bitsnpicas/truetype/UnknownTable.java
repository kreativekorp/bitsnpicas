package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UnknownTable extends TrueTypeTable {
	private final String name;
	public byte[] data;
	
	public UnknownTable(String tableName) {
		name = tableName;
		data = new byte[0];
	}
	
	public UnknownTable(String tableName, byte[] tableData) {
		name = tableName;
		data = tableData;
	}
	
	public UnknownTable(int tableId) {
		name = new String(new char[]{
			(char)((tableId >> 24) & 0xFF),
			(char)((tableId >> 16) & 0xFF),
			(char)((tableId >>  8) & 0xFF),
			(char)((tableId >>  0) & 0xFF),
		});
		data = new byte[0];
	}
	
	public UnknownTable(int tableId, byte[] tableData) {
		name = new String(new char[]{
			(char)((tableId >> 24) & 0xFF),
			(char)((tableId >> 16) & 0xFF),
			(char)((tableId >>  8) & 0xFF),
			(char)((tableId >>  0) & 0xFF),
		});
		data = tableData;
	}
	
	@Override
	public String tableName() {
		return name;
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		out.write(data);
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		data = new byte[length];
		in.readFully(data);
	}
}
