package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CmapSubtableFormat10 extends CmapSubtable {
	public int languageID = 0;
	public int firstChar = 0;
	public int glyphIndex[] = new int[0];
	
	@Override
	public int format() {
		return 10;
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
		out.writeShort(10); // format
		out.writeShort(0); // subformat
		out.writeInt(20 + glyphIndex.length * 2);
		out.writeInt(languageID);
		out.writeInt(firstChar);
		out.writeInt(glyphIndex.length);
		for (int i = 0; i < glyphIndex.length; i++) {
			out.writeShort(glyphIndex[i]);
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length) throws IOException {
		in.readUnsignedShort(); // format
		in.readUnsignedShort(); // subformat
		in.readInt(); // length
		languageID = in.readInt();
		firstChar = in.readInt();
		glyphIndex = new int[in.readInt()];
		for (int i = 0; i < glyphIndex.length; i++) {
			glyphIndex[i] = in.readUnsignedShort();
		}
	}
}
