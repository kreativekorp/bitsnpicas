package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CmapSubtableFormat0 extends CmapSubtable {
	public int languageID = 0;
	public final int[] glyphIndex = new int[256];
	
	@Override
	public int format() {
		return 0;
	}
	
	@Override
	public int getGlyphIndex(int charCode) {
		if (charCode >= 0 && charCode < 256) {
			return glyphIndex[charCode];
		} else {
			return 0;
		}
	}
	
	@Override
	protected void compile(DataOutputStream out) throws IOException {
		out.writeShort(0); // format
		out.writeShort(262); // length
		out.writeShort(languageID);
		for (int i = 0; i < 256; i++) {
			out.writeByte(glyphIndex[i]);
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length) throws IOException {
		in.readUnsignedShort(); // format
		in.readUnsignedShort(); // length
		languageID = in.readUnsignedShort();
		for (int i = 0; i < 256; i++) {
			glyphIndex[i] = in.readUnsignedByte();
		}
	}
}
