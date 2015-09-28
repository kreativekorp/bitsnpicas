package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UnknownCmapSubtable extends CmapSubtable {
	private final int format;
	public byte[] data;
	
	public UnknownCmapSubtable(int format) {
		this.format = format;
		this.data = new byte[0];
	}
	
	public UnknownCmapSubtable(int format, byte[] data) {
		this.format = format;
		this.data = data;
	}
	
	@Override
	public int format() {
		return format;
	}
	
	@Override
	public int getGlyphIndex(int charCode) {
		return 0;
	}
	
	@Override
	protected void compile(DataOutputStream out) throws IOException {
		out.write(data);
	}
	
	@Override
	protected void decompile(DataInputStream in, int length) throws IOException {
		data = new byte[length];
		in.readFully(data);
	}
}
