package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class EblcIndexSubtable {
	public EblcIndexSubtableHeader header;
	
	protected abstract void read(DataInputStream in) throws IOException;
	protected abstract void write(DataOutputStream out) throws IOException;
	protected abstract int length();
	
	public abstract int[] getOffsets();
	public abstract void setOffsets(int[] offsets);
	public abstract EblcGlyphIdOffsetPair[] getGlyphIdOffsetPairs();
	public abstract void setGlyphIdOffsetPairs(EblcGlyphIdOffsetPair[] pairs);
}
