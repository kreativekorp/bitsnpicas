package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SvgTable extends ListBasedTable<SvgTableEntry> {
	@Override
	public String tableName() {
		return "SVG ";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		out.writeShort(0);
		out.writeInt(10);
		out.writeInt(0);
		out.writeShort(this.size());
		int currentLocation = 2 + this.size() * 12;
		for (SvgTableEntry e : this) {
			out.writeShort(e.startGlyphID);
			out.writeShort(e.endGlyphID);
			out.writeInt(currentLocation);
			out.writeInt(e.svgDocument.length);
			currentLocation += e.svgDocument.length;
		}
		for (SvgTableEntry e : this) {
			out.write(e.svgDocument);
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		in.readUnsignedShort();
		int offsetToIndex = in.readInt();
		in.reset();
		in.skipBytes(offsetToIndex);
		int numEntries = in.readUnsignedShort();
		int[] entryStartGlyphID = new int[numEntries];
		int[] entryEndGlyphID = new int[numEntries];
		int[] entryOffset = new int[numEntries];
		int[] entryLength = new int[numEntries];
		for (int i = 0; i < numEntries; i++) {
			entryStartGlyphID[i] = in.readUnsignedShort();
			entryEndGlyphID[i] = in.readUnsignedShort();
			entryOffset[i] = in.readInt();
			entryLength[i] = in.readInt();
		}
		this.clear();
		for (int i = 0; i < numEntries; i++) {
			SvgTableEntry e = new SvgTableEntry();
			e.startGlyphID = entryStartGlyphID[i];
			e.endGlyphID = entryEndGlyphID[i];
			e.svgDocument = new byte[entryLength[i]];
			in.reset();
			in.skipBytes(offsetToIndex + entryOffset[i]);
			in.readFully(e.svgDocument);
			this.add(e);
		}
	}
}
