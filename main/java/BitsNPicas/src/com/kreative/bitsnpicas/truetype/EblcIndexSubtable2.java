package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EblcIndexSubtable2 extends EblcIndexSubtable {
	public int imageSize;
	public SbitBigGlyphMetrics bigMetrics;
	
	@Override
	protected void read(DataInputStream in) throws IOException {
		imageSize = in.readInt();
		bigMetrics = new SbitBigGlyphMetrics();
		bigMetrics.read(in);
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		out.writeInt(imageSize);
		((bigMetrics != null) ? bigMetrics : new SbitBigGlyphMetrics()).write(out);
	}
	
	@Override
	protected int length() {
		return 12;
	}
	
	@Override
	public int[] getOffsets() {
		int n = header.lastGlyphIndex - header.firstGlyphIndex + 2;
		int[] offsets = new int[n];
		for (int i = 0; i < n; i++) {
			offsets[i] = i * imageSize + header.imageDataOffset;
		}
		return offsets;
	}
	
	@Override
	public void setOffsets(int[] offsets) {
		imageSize = offsets[1] - offsets[0];
	}
	
	@Override
	public EblcGlyphIdOffsetPair[] getGlyphIdOffsetPairs() {
		int n = header.lastGlyphIndex - header.firstGlyphIndex + 2;
		EblcGlyphIdOffsetPair[] pairs = new EblcGlyphIdOffsetPair[n];
		for (int i = 0; i < n; i++) {
			pairs[i] = new EblcGlyphIdOffsetPair();
			pairs[i].glyphID = header.firstGlyphIndex + i;
			pairs[i].offset = i * imageSize + header.imageDataOffset;
		}
		return pairs;
	}
	
	@Override
	public void setGlyphIdOffsetPairs(EblcGlyphIdOffsetPair[] pairs) {
		header.firstGlyphIndex = pairs[0].glyphID;
		header.lastGlyphIndex = pairs[0].glyphID + pairs.length - 2;
		imageSize = pairs[1].offset - pairs[0].offset;
	}
}
