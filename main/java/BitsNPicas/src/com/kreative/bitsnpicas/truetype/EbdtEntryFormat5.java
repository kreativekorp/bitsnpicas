package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EbdtEntryFormat5 extends EbdtEntry {
	public byte[] imageData;
	
	@Override
	public int format() {
		return 5;
	}
	
	@Override
	protected void read(DataInputStream in, int length) throws IOException {
		imageData = new byte[length];
		in.readFully(imageData);
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		out.write(imageData);
	}
	
	@Override
	protected int length() {
		return imageData.length;
	}
}
