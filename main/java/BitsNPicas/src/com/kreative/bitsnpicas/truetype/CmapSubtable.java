package com.kreative.bitsnpicas.truetype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class CmapSubtable {
	public abstract int format();
	
	public abstract int getGlyphIndex(int charCode);
	
	protected abstract void compile(DataOutputStream out) throws IOException;
	
	public byte[] compile() throws IOException {
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteout);
		compile(out);
		out.flush();
		byteout.flush();
		out.close();
		byteout.close();
		return byteout.toByteArray();
	}
	
	protected abstract void decompile(DataInputStream in, int length) throws IOException;
	
	public void decompile(byte[] data) throws IOException {
		ByteArrayInputStream bytein = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(bytein);
		decompile(in, data.length);
		in.close();
		bytein.close();
	}
}
