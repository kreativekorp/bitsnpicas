package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EbdtEntryFormat6 extends EbdtEntry {
	public SbitBigGlyphMetrics bigMetrics;
	public byte[] imageData;
	
	@Override
	public int format() {
		return 6;
	}
	
	@Override
	protected void read(DataInputStream in, int length) throws IOException {
		bigMetrics = new SbitBigGlyphMetrics();
		bigMetrics.read(in);
		imageData = new byte[length - 8];
		in.readFully(imageData);
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		((bigMetrics != null) ? bigMetrics : new SbitBigGlyphMetrics()).write(out);
		out.write(imageData);
	}
	
	@Override
	protected int length() {
		return 8 + imageData.length;
	}
}
