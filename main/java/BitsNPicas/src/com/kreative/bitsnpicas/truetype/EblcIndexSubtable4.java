package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EblcIndexSubtable4 extends ListBasedEblcIndexSubtable<EblcGlyphIdOffsetPair> {
	@Override
	protected void read(DataInputStream in) throws IOException {
		int n = in.readInt() + 1;
		for (int i = 0; i < n; i++) {
			EblcGlyphIdOffsetPair pair = new EblcGlyphIdOffsetPair();
			pair.read(in);
			this.add(pair);
		}
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		out.writeInt(this.size() - 1);
		for (EblcGlyphIdOffsetPair pair : this) {
			pair.write(out);
		}
	}
	
	@Override
	protected int length() {
		return 4 + (this.size() * 4);
	}
	
	@Override
	public int[] getOffsets() {
		int n = this.size();
		int[] offsets = new int[n];
		for (int i = 0; i < n; i++) {
			offsets[i] = this.get(i).offset + header.imageDataOffset;
		}
		return offsets;
	}
	
	@Override
	public void setOffsets(int[] offsets) {
		int n = Math.min(offsets.length, this.size());
		for (int i = 0; i < n; i++) {
			this.get(i).offset = offsets[i] - header.imageDataOffset;
		}
	}
	
	@Override
	public EblcGlyphIdOffsetPair[] getGlyphIdOffsetPairs() {
		int n = this.size();
		EblcGlyphIdOffsetPair[] pairs = new EblcGlyphIdOffsetPair[n];
		for (int i = 0; i < n; i++) {
			pairs[i] = new EblcGlyphIdOffsetPair();
			pairs[i].glyphID = this.get(i).glyphID;
			pairs[i].offset = this.get(i).offset + header.imageDataOffset;
		}
		return pairs;
	}
	
	@Override
	public void setGlyphIdOffsetPairs(EblcGlyphIdOffsetPair[] pairs) {
		this.clear();
		for (EblcGlyphIdOffsetPair pair : pairs) {
			EblcGlyphIdOffsetPair p = new EblcGlyphIdOffsetPair();
			p.glyphID = pair.glyphID;
			p.offset = pair.offset - header.imageDataOffset;
			this.add(p);
		}
	}
}
