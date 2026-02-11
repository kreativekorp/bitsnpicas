package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CmapSubtableFormat12 extends ListBasedCmapSubtable<CmapSubtableSequentialEntry> {
	public int languageID = 0;
	
	@Override
	public int format() {
		return 12;
	}
	
	@Override
	public int getGlyphIndex(int charCode) {
		for (CmapSubtableSequentialEntry e : this) {
			if (e.contains(charCode)) {
				return e.getGlyphIndex(charCode);
			}
		}
		return 0;
	}
	
	@Override
	protected void compile(DataOutputStream out) throws IOException {
		out.writeShort(12); // format
		out.writeShort(0); // subformat
		out.writeInt(16 + this.size() * 12);
		out.writeInt(languageID);
		out.writeInt(this.size());
		for (CmapSubtableSequentialEntry e : this) {
			out.writeInt(e.startCharCode);
			out.writeInt(e.endCharCode);
			out.writeInt(e.glyphIndex);
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length) throws IOException {
		in.readUnsignedShort(); // format
		in.readUnsignedShort(); // subformat
		in.readInt(); // length
		languageID = in.readInt();
		int count = in.readInt();
		this.clear();
		for (int i = 0; i < count; i++) {
			CmapSubtableSequentialEntry e = new CmapSubtableSequentialEntry();
			e.startCharCode = in.readInt();
			e.endCharCode = in.readInt();
			e.glyphIndex = in.readInt();
			this.add(e);
		}
	}
}
