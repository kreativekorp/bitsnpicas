package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EblcIndexSubtableHeader {
	public int firstGlyphIndex;
	public int lastGlyphIndex;
	public int additionalOffsetToIndexSubtable;
	public int indexFormat;
	public int imageFormat;
	public int imageDataOffset;
	
	protected void readElement(DataInputStream in) throws IOException {
		firstGlyphIndex = in.readUnsignedShort();
		lastGlyphIndex = in.readUnsignedShort();
		additionalOffsetToIndexSubtable = in.readInt();
	}
	
	protected void writeElement(DataOutputStream out) throws IOException {
		out.writeShort(firstGlyphIndex);
		out.writeShort(lastGlyphIndex);
		out.writeInt(additionalOffsetToIndexSubtable);
	}
	
	protected void readHeader(DataInputStream in) throws IOException {
		indexFormat = in.readUnsignedShort();
		imageFormat = in.readUnsignedShort();
		imageDataOffset = in.readInt();
	}
	
	protected void writeHeader(DataOutputStream out) throws IOException {
		out.writeShort(indexFormat);
		out.writeShort(imageFormat);
		out.writeInt(imageDataOffset);
	}
}
