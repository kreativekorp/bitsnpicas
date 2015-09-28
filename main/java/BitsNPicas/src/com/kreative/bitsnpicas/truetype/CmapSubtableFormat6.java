package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CmapSubtableFormat6 extends CmapSubtable {
	public int languageID = 0;
	public int firstChar = 0;
	public int glyphIndex[] = new int[0];
	
	@Override
	public int format() {
		return 6;
	}
	
	@Override
	public int getGlyphIndex(int charCode) {
		if (charCode >= firstChar && charCode < firstChar + glyphIndex.length) {
			return glyphIndex[charCode - firstChar];
		} else {
			return 0;
		}
	}
	
	@Override
	protected void compile(DataOutputStream out) throws IOException {
		out.writeShort(6); // format
		out.writeShort(10 + glyphIndex.length * 2);
		out.writeShort(languageID);
		out.writeShort(firstChar);
		out.writeShort(glyphIndex.length);
		for (int i = 0; i < glyphIndex.length; i++) {
			out.writeShort(glyphIndex[i]);
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length) throws IOException {
		in.readUnsignedShort(); // format
		in.readUnsignedShort(); // length
		languageID = in.readUnsignedShort();
		firstChar = in.readUnsignedShort();
		glyphIndex = new int[in.readUnsignedShort()];
		for (int i = 0; i < glyphIndex.length; i++) {
			glyphIndex[i] = in.readUnsignedShort();
		}
	}
}
