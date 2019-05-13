package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EblcGlyphIdOffsetPair {
	public int glyphID;
	public int offset;
	
	protected void read(DataInputStream in) throws IOException {
		glyphID = in.readUnsignedShort();
		offset = in.readUnsignedShort();
	}
	
	protected void write(DataOutputStream out) throws IOException {
		out.writeShort(glyphID);
		out.writeShort(offset);
	}
}
