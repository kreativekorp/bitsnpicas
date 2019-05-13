package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CbdtEntryFormat17 extends CbdtEntry {
	public SbitSmallGlyphMetrics glyphMetrics;
	
	@Override
	public int format() {
		return 17;
	}
	
	@Override
	protected void read(DataInputStream in, int length) throws IOException {
		glyphMetrics = new SbitSmallGlyphMetrics();
		glyphMetrics.read(in);
		int imageSize = in.readInt();
		imageData = new byte[imageSize];
		in.readFully(imageData);
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		((glyphMetrics != null) ? glyphMetrics : new SbitSmallGlyphMetrics()).write(out);
		out.writeInt(imageData.length);
		out.write(imageData);
	}
	
	@Override
	protected int length() {
		return 9 + imageData.length;
	}
}
