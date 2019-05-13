package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CbdtEntryFormat19 extends CbdtEntry {
	@Override
	public int format() {
		return 19;
	}
	
	@Override
	protected void read(DataInputStream in, int length) throws IOException {
		int imageSize = in.readInt();
		imageData = new byte[imageSize];
		in.readFully(imageData);
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		out.writeInt(imageData.length);
		out.write(imageData);
	}
	
	@Override
	protected int length() {
		return 4 + imageData.length;
	}
}
