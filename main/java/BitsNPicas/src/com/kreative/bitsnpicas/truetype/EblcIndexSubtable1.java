package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EblcIndexSubtable1 extends ListBasedEblcIndexSubtable<Integer> {
	@Override
	protected void read(DataInputStream in) throws IOException {
		int n = header.lastGlyphIndex - header.firstGlyphIndex + 2;
		for (int i = 0; i < n; i++) this.add(in.readInt());
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		for (int offset : this) out.writeInt(offset);
	}
	
	@Override
	protected int length() {
		return this.size() * 4;
	}
	
	@Override
	public int[] getOffsets() {
		int n = this.size();
		int[] offsets = new int[n];
		for (int i = 0; i < n; i++) {
			offsets[i] = this.get(i) + header.imageDataOffset;
		}
		return offsets;
	}
	
	@Override
	public void setOffsets(int[] offsets) {
		this.clear();
		for (int offset : offsets) {
			this.add(offset - header.imageDataOffset);
		}
	}
	
	@Override
	public EblcGlyphIdOffsetPair[] getGlyphIdOffsetPairs() {
		int n = this.size();
		EblcGlyphIdOffsetPair[] pairs = new EblcGlyphIdOffsetPair[n];
		for (int i = 0; i < n; i++) {
			pairs[i] = new EblcGlyphIdOffsetPair();
			pairs[i].glyphID = header.firstGlyphIndex + i;
			pairs[i].offset = this.get(i) + header.imageDataOffset;
		}
		return pairs;
	}
	
	@Override
	public void setGlyphIdOffsetPairs(EblcGlyphIdOffsetPair[] pairs) {
		header.firstGlyphIndex = pairs[0].glyphID;
		header.lastGlyphIndex = pairs[0].glyphID + pairs.length - 2;
		this.clear();
		for (EblcGlyphIdOffsetPair pair : pairs) {
			this.add(pair.offset - header.imageDataOffset);
		}
	}
}
