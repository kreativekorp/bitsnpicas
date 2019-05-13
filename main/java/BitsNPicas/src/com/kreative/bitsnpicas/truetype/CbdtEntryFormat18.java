package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CbdtEntryFormat18 extends CbdtEntry {
	public SbitBigGlyphMetrics glyphMetrics;
	
	@Override
	public int format() {
		return 18;
	}
	
	@Override
	protected void read(DataInputStream in, int length) throws IOException {
		glyphMetrics = new SbitBigGlyphMetrics();
		glyphMetrics.read(in);
		int imageSize = in.readInt();
		imageData = new byte[imageSize];
		in.readFully(imageData);
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		((glyphMetrics != null) ? glyphMetrics : new SbitBigGlyphMetrics()).write(out);
		out.writeInt(imageData.length);
		out.write(imageData);
	}
	
	@Override
	protected int length() {
		return 12 + imageData.length;
	}
}
