package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
		Map<byte[], Integer> documents = new HashMap<byte[], Integer>();
		int currentLocation = 2 + this.size() * 12;
		for (SvgTableEntry e : this) {
			out.writeShort(e.startGlyphID);
			out.writeShort(e.endGlyphID);
			if (documents.containsKey(e.svgDocument)) {
				out.writeInt(documents.get(e.svgDocument));
			} else {
				out.writeInt(currentLocation);
				documents.put(e.svgDocument, currentLocation);
				currentLocation += e.svgDocument.length;
			}
			out.writeInt(e.svgDocument.length);
		}
		for (SvgTableEntry e : this) {
			if (documents.containsKey(e.svgDocument)) {
				out.write(e.svgDocument);
				documents.remove(e.svgDocument);
			}
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
		long[] entryHash = new long[numEntries];
		for (int i = 0; i < numEntries; i++) {
			entryStartGlyphID[i] = in.readUnsignedShort();
			entryEndGlyphID[i] = in.readUnsignedShort();
			entryOffset[i] = in.readInt();
			entryLength[i] = in.readInt();
			entryHash[i] = ((long)entryOffset[i] << 32L) | (long)entryLength[i];
		}
		Map<Long, byte[]> documents = new HashMap<Long, byte[]>();
		for (int i = 0; i < numEntries; i++) {
			if (!documents.containsKey(entryHash[i])) {
				byte[] document = new byte[entryLength[i]];
				in.reset();
				in.skipBytes(offsetToIndex + entryOffset[i]);
				in.readFully(document);
				documents.put(entryHash[i], document);
			}
		}
		this.clear();
		for (int i = 0; i < numEntries; i++) {
			SvgTableEntry e = new SvgTableEntry();
			e.startGlyphID = entryStartGlyphID[i];
			e.endGlyphID = entryEndGlyphID[i];
			e.svgDocument = documents.get(entryHash[i]);
			this.add(e);
		}
	}
}
