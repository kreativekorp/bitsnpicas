package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmapTable extends TrueTypeTable {
	public static final int VERSION_DEFAULT = 0;
	public static final int VERSION_CONTAINS_TYPE_8_OR_HIGHER = 1;
	
	public int version = VERSION_DEFAULT;
	public final List<CmapTableEntry> entries = new ArrayList<CmapTableEntry>();
	public final List<CmapSubtable> subtables = new ArrayList<CmapSubtable>();
	
	private static final CmapSubtable createSubtable(int format) {
		switch (format) {
			case 0: return new CmapSubtableFormat0();
			case 4: return new CmapSubtableFormat4();
			case 6: return new CmapSubtableFormat6();
			case 10: return new CmapSubtableFormat10();
			case 12: return new CmapSubtableFormat12();
			default: return new UnknownCmapSubtable(format);
		}
	}
	
	@Override
	public String tableName() {
		return "cmap";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	public CmapSubtable getSubtable(int platformID, int platformSpecificID) {
		for (CmapTableEntry e : entries) {
			if (e.platformID == platformID && e.platformSpecificID == platformSpecificID) {
				return e.subtable;
			}
		}
		return null;
	}
	
	public CmapSubtable getBestSubtable() {
		CmapSubtable s;
		if ((s = getSubtable(PlatformConstants.PLATFORM_ID_UNICODE, PlatformConstants.PLATFORM_SPECIFIC_ID_UNICODE_2_0)) != null) return s;
		if ((s = getSubtable(PlatformConstants.PLATFORM_ID_UNICODE, PlatformConstants.PLATFORM_SPECIFIC_ID_UNICODE_1_1)) != null) return s;
		if ((s = getSubtable(PlatformConstants.PLATFORM_ID_UNICODE, PlatformConstants.PLATFORM_SPECIFIC_ID_UNICODE_DEFAULT)) != null) return s;
		if ((s = getSubtable(PlatformConstants.PLATFORM_ID_WINDOWS_UNICODE, PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_32)) != null) return s;
		if ((s = getSubtable(PlatformConstants.PLATFORM_ID_WINDOWS_UNICODE, PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_16)) != null) return s;
		if ((s = getSubtable(PlatformConstants.PLATFORM_ID_WINDOWS, PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_32)) != null) return s;
		if ((s = getSubtable(PlatformConstants.PLATFORM_ID_WINDOWS, PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_16)) != null) return s;
		return null;
	}
	
	public int getGlyphIndex(int charCode) {
		CmapSubtable s = getBestSubtable();
		if (s != null) return s.getGlyphIndex(charCode);
		return 0;
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		out.writeShort(version);
		out.writeShort(entries.size());
		int currentOffset = 4 + entries.size() * 8;
		Map<CmapSubtable,byte[]> subtableData = new HashMap<CmapSubtable,byte[]>();
		Map<CmapSubtable,Integer> subtableOffset = new HashMap<CmapSubtable,Integer>();
		for (CmapSubtable subtable : subtables) {
			byte[] data = subtable.compile();
			subtableData.put(subtable, data);
			subtableOffset.put(subtable, currentOffset);
			currentOffset += data.length;
		}
		for (CmapTableEntry e : entries) {
			out.writeShort(e.platformID);
			out.writeShort(e.platformSpecificID);
			out.writeInt(subtableOffset.get(e.subtable));
		}
		for (CmapSubtable subtable : subtables) {
			out.write(subtableData.get(subtable));
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		version = in.readUnsignedShort();
		int numSubtables = in.readUnsignedShort();
		
		// Read headers.
		List<CmapSubtableInfo> subtableInfo = new ArrayList<CmapSubtableInfo>();
		List<Integer> subtableOffsets = new ArrayList<Integer>();
		subtableOffsets.add(length);
		for (int i = 0; i < numSubtables; i++) {
			CmapSubtableInfo info = new CmapSubtableInfo();
			info.platformID = in.readUnsignedShort();
			info.platformSpecificID = in.readUnsignedShort();
			info.offset = in.readInt();
			subtableInfo.add(info);
			if (!subtableOffsets.contains(info.offset)) {
				subtableOffsets.add(info.offset);
			}
		}
		Collections.sort(subtableOffsets);
		
		// Read data.
		Map<Integer,byte[]> offsetToData = new HashMap<Integer,byte[]>();
		int s = subtableOffsets.get(0);
		for (int i = 1; i < subtableOffsets.size(); i++) {
			int e = subtableOffsets.get(i);
			byte[] data = new byte[e - s];
			in.reset();
			in.skipBytes(s);
			in.readFully(data);
			offsetToData.put(s, data);
			s = e;
		}
		
		// Decompile.
		Map<Integer,CmapSubtable> offsetToSubtable = new HashMap<Integer,CmapSubtable>();
		for (Map.Entry<Integer,byte[]> e : offsetToData.entrySet()) {
			int offset = e.getKey();
			byte[] data = e.getValue();
			int format = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
			CmapSubtable subtable = createSubtable(format);
			subtable.decompile(data);
			offsetToSubtable.put(offset, subtable);
		}
		
		// Gather tables.
		entries.clear();
		for (CmapSubtableInfo info : subtableInfo) {
			CmapTableEntry e = new CmapTableEntry();
			e.platformID = info.platformID;
			e.platformSpecificID = info.platformSpecificID;
			e.subtable = offsetToSubtable.get(info.offset);
			entries.add(e);
		}
		subtables.clear();
		for (int i = 0; i < subtableOffsets.size() - 1; i++) {
			int offset = subtableOffsets.get(i);
			subtables.add(offsetToSubtable.get(offset));
		}
	}
	
	private static final class CmapSubtableInfo {
		private int platformID;
		private int platformSpecificID;
		private int offset;
	}
}
