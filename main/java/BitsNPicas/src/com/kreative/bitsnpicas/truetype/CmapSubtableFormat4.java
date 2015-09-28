package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CmapSubtableFormat4 extends ListBasedCmapSubtable<CmapSubtableEntry> {
	public int languageID = 0;
	
	@Override
	public int format() {
		return 4;
	}
	
	@Override
	public int getGlyphIndex(int charCode) {
		for (CmapSubtableEntry e : this) {
			if (e.contains(charCode)) {
				return e.getGlyphIndex(charCode);
			}
		}
		return 0;
	}
	
	@Override
	protected void compile(DataOutputStream out) throws IOException {
		int segCnt = this.size();
		int searchRange = (1 << 30);
		while (searchRange > segCnt) { searchRange >>>= 1; }
		int entrySelector = Integer.numberOfTrailingZeros(searchRange);
		searchRange <<= 1;
		int rangeShift = (segCnt << 1) - searchRange;
		int fmt4len = 16 + segCnt * 8;
		for (CmapSubtableEntry e : this) {
			if (e instanceof CmapSubtableRandomEntry) {
				fmt4len += ((CmapSubtableRandomEntry)e).glyphIndex.length * 2;
			}
		}
		
		out.writeShort(4); // format
		out.writeShort(fmt4len);
		out.writeShort(languageID);
		out.writeShort(segCnt * 2);
		out.writeShort(searchRange);
		out.writeShort(entrySelector);
		out.writeShort(rangeShift);
		for (CmapSubtableEntry e : this) {
			out.writeShort(e.endCharCode);
		}
		out.writeShort(0); // reservedPad
		for (CmapSubtableEntry e : this) {
			out.writeShort(e.startCharCode);
		}
		for (CmapSubtableEntry e : this) {
			if (e instanceof CmapSubtableSequentialEntry) {
				out.writeShort(((CmapSubtableSequentialEntry)e).glyphIndex - e.startCharCode);
			} else {
				out.writeShort(0);
			}
		}
		
		int idRangeOffset = this.size();
		for (CmapSubtableEntry e : this) {
			if (e instanceof CmapSubtableRandomEntry) {
				out.writeShort(idRangeOffset * 2);
				idRangeOffset += ((CmapSubtableRandomEntry)e).glyphIndex.length;
			} else {
				out.writeShort(0);
			}
			idRangeOffset--;
		}
		for (CmapSubtableEntry e : this) {
			if (e instanceof CmapSubtableRandomEntry) {
				for (int glyphIndex : ((CmapSubtableRandomEntry)e).glyphIndex) {
					out.writeShort(glyphIndex);
				}
			}
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length) throws IOException {
		in.readUnsignedShort(); // format
		in.readUnsignedShort(); // length
		languageID = in.readUnsignedShort();
		int segCnt = in.readUnsignedShort() / 2;
		in.readUnsignedShort(); // searchRange
		in.readUnsignedShort(); // entrySelector
		in.readUnsignedShort(); // rangeShift
		
		int[] endCodes = new int[segCnt];
		for (int i = 0; i < segCnt; i++) {
			endCodes[i] = in.readUnsignedShort();
		}
		in.readUnsignedShort(); // reservedPad
		int[] startCodes = new int[segCnt];
		for (int i = 0; i < segCnt; i++) {
			startCodes[i] = in.readUnsignedShort();
		}
		int[] idDeltas = new int[segCnt];
		for (int i = 0; i < segCnt; i++) {
			idDeltas[i] = in.readUnsignedShort();
		}
		in.mark(length);
		int[] idRangeOffsets = new int[segCnt];
		for (int i = 0; i < segCnt; i++) {
			idRangeOffsets[i] = in.readUnsignedShort();
		}
		
		this.clear();
		for (int i = 0; i < segCnt; i++) {
			if (idRangeOffsets[i] == 0) {
				CmapSubtableSequentialEntry e = new CmapSubtableSequentialEntry();
				e.startCharCode = startCodes[i];
				e.endCharCode = endCodes[i];
				e.glyphIndex = (startCodes[i] + idDeltas[i]) & 0xFFFF;
				this.add(e);
			} else {
				CmapSubtableRandomEntry e = new CmapSubtableRandomEntry();
				e.startCharCode = startCodes[i];
				e.endCharCode = endCodes[i];
				e.glyphIndex = new int[e.endCharCode - e.startCharCode + 1];
				in.reset();
				in.skipBytes(i + i + idRangeOffsets[i]);
				for (int j = 0; j < e.glyphIndex.length; j++) {
					e.glyphIndex[j] = in.readUnsignedShort();
				}
				this.add(e);
			}
		}
	}
}
