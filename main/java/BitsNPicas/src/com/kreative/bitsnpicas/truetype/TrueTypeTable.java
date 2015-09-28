package com.kreative.bitsnpicas.truetype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class TrueTypeTable {
	public abstract String tableName();
	
	public int tableId() {
		char[] name = tableName().toCharArray();
		int id = (((name.length > 0 && name[0] >= 0x20 && name[0] < 0x7F) ? name[0] : 0x20) << 24)
		       | (((name.length > 1 && name[1] >= 0x20 && name[1] < 0x7F) ? name[1] : 0x20) << 16)
		       | (((name.length > 2 && name[2] >= 0x20 && name[2] < 0x7F) ? name[2] : 0x20) <<  8)
		       | (((name.length > 3 && name[3] >= 0x20 && name[3] < 0x7F) ? name[3] : 0x20) <<  0);
		return id;
	}
	
	public abstract String[] dependencyNames();
	
	public int[] dependencyIds() {
		String[] names = dependencyNames();
		int[] ids = new int[names.length];
		for (int i = 0; i < names.length; i++) {
			char[] name = names[i].toCharArray();
			ids[i] = (((name.length > 0 && name[0] >= 0x20 && name[0] < 0x7F) ? name[0] : 0x20) << 24)
			       | (((name.length > 1 && name[1] >= 0x20 && name[1] < 0x7F) ? name[1] : 0x20) << 16)
			       | (((name.length > 2 && name[2] >= 0x20 && name[2] < 0x7F) ? name[2] : 0x20) <<  8)
			       | (((name.length > 3 && name[3] >= 0x20 && name[3] < 0x7F) ? name[3] : 0x20) <<  0);
		}
		return ids;
	}
	
	protected abstract void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException;
	
	public byte[] compile(TrueTypeTable[] dependencies) throws IOException {
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteout);
		compile(out, dependencies);
		out.flush();
		byteout.flush();
		out.close();
		byteout.close();
		return byteout.toByteArray();
	}
	
	protected abstract void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException;
	
	public void decompile(byte[] data, TrueTypeTable[] dependencies) throws IOException {
		ByteArrayInputStream bytein = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(bytein);
		decompile(in, data.length, dependencies);
		in.close();
		bytein.close();
	}
}
