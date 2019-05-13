package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EblcIndexSubtable5 extends ListBasedEblcIndexSubtable<Integer> {
	public int imageSize;
	public SbitBigGlyphMetrics bigMetrics;
	
	@Override
	protected void read(DataInputStream in) throws IOException {
		imageSize = in.readInt();
		bigMetrics = new SbitBigGlyphMetrics();
		bigMetrics.read(in);
		int n = in.readInt();
		for (int i = 0; i < n; i++) {
			this.add(in.readUnsignedShort());
		}
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		out.writeInt(imageSize);
		((bigMetrics != null) ? bigMetrics : new SbitBigGlyphMetrics()).write(out);
		out.writeInt(this.size());
		for (int glyphID : this) {
			out.writeShort(glyphID);
		}
		if ((size() & 1) != 0) {
			out.writeShort(0);
		}
	}
	
	@Override
	protected int length() {
		int l = 16 + (this.size() * 2);
		if ((size() & 1) != 0) l += 2;
		return l;
	}
	
	@Override
	public int[] getOffsets() {
		int n = this.size() + 1;
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
		int n = this.size() + 1;
		EblcGlyphIdOffsetPair[] pairs = new EblcGlyphIdOffsetPair[n];
		for (int i = 0; i < n; i++) {
			pairs[i] = new EblcGlyphIdOffsetPair();
			pairs[i].glyphID = (i < this.size()) ? this.get(i) : -1;
			pairs[i].offset = i * imageSize + header.imageDataOffset;
		}
		return pairs;
	}
	
	@Override
	public void setGlyphIdOffsetPairs(EblcGlyphIdOffsetPair[] pairs) {
		header.firstGlyphIndex = pairs[0].glyphID;
		header.lastGlyphIndex = pairs[0].glyphID;
		this.clear();
		for (EblcGlyphIdOffsetPair pair : pairs) {
			if (pair.glyphID < header.firstGlyphIndex) header.firstGlyphIndex = pair.glyphID;
			if (pair.glyphID > header.lastGlyphIndex) header.lastGlyphIndex = pair.glyphID;
			this.add(pair.glyphID);
		}
		imageSize = pairs[1].offset - pairs[0].offset;
	}
}
