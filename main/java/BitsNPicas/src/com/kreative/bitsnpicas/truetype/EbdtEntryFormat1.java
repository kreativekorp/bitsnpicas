package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EbdtEntryFormat1 extends EbdtEntry {
	public SbitSmallGlyphMetrics smallMetrics;
	public byte[] imageData;
	
	@Override
	public int format() {
		return 1;
	}
	
	@Override
	protected void read(DataInputStream in, int length) throws IOException {
		smallMetrics = new SbitSmallGlyphMetrics();
		smallMetrics.read(in);
		imageData = new byte[length - 5];
		in.readFully(imageData);
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		((smallMetrics != null) ? smallMetrics : new SbitSmallGlyphMetrics()).write(out);
		out.write(imageData);
	}
	
	@Override
	protected int length() {
		return 5 + imageData.length;
	}
}
