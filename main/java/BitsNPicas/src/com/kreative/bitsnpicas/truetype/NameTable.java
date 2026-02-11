package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NameTable extends ListBasedTable<NameTableEntry> {
	public static final int FORMAT_DEFAULT = 0;
	
	public int format = FORMAT_DEFAULT;
	
	@Override
	public String tableName() {
		return "name";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		out.writeShort(format);
		out.writeShort(this.size());
		out.writeShort(6 + this.size() * 12);
		
		List<NameTableEntryInfo> entryInfo = new ArrayList<NameTableEntryInfo>();
		int currentLocation = 0;
		for (NameTableEntry e : this) {
			NameTableEntryInfo info = new NameTableEntryInfo();
			info.entry = e;
			info.location = currentLocation;
			entryInfo.add(info);
			currentLocation += e.nameData.length;
			if (e.padding > 0 && e.padding <= 4) {
				currentLocation += e.padding;
			}
		}
		
		Collections.sort(entryInfo, SORT_BY_NAME_ID);
		for (NameTableEntryInfo info : entryInfo) {
			out.writeShort(info.entry.platformID);
			out.writeShort(info.entry.platformSpecificID);
			out.writeShort(info.entry.languageID);
			out.writeShort(info.entry.nameID);
			out.writeShort(info.entry.nameData.length);
			out.writeShort(info.location);
		}
		
		Collections.sort(entryInfo, SORT_BY_LOCATION);
		for (NameTableEntryInfo info : entryInfo) {
			out.write(info.entry.nameData);
			if (info.entry.padding > 0 && info.entry.padding <= 4) {
				out.write(new byte[info.entry.padding]);
			}
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		format = in.readUnsignedShort();
		int count = in.readUnsignedShort();
		int offset = in.readUnsignedShort();
		
		List<NameTableEntryInfo> entryInfo = new ArrayList<NameTableEntryInfo>();
		for (int i = 0; i < count; i++) {
			NameTableEntryInfo info = new NameTableEntryInfo();
			info.entry = new NameTableEntry();
			info.entry.index = i;
			info.entry.platformID = in.readUnsignedShort();
			info.entry.platformSpecificID = in.readUnsignedShort();
			info.entry.languageID = in.readUnsignedShort();
			info.entry.nameID = in.readUnsignedShort();
			info.entry.nameData = new byte[in.readUnsignedShort()];
			info.location = offset + in.readUnsignedShort();
			entryInfo.add(info);
		}
		
		for (NameTableEntryInfo info : entryInfo) {
			in.reset();
			in.skipBytes(info.location);
			in.readFully(info.entry.nameData);
		}
		
		Collections.sort(entryInfo, SORT_BY_LOCATION);
		for (int i = 0; i < count; i++) {
			NameTableEntryInfo info = entryInfo.get(i);
			int nextLocation = (i+1 < count) ? entryInfo.get(i+1).location : length;
			info.entry.padding = nextLocation - info.location - info.entry.nameData.length;
		}
		
		this.clear();
		for (NameTableEntryInfo info : entryInfo) {
			this.add(info.entry);
		}
	}
	
	private static final class NameTableEntryInfo {
		private NameTableEntry entry;
		private int location;
	}
	
	private static final Comparator<NameTableEntryInfo> SORT_BY_NAME_ID = new Comparator<NameTableEntryInfo>() {
		@Override
		public int compare(NameTableEntryInfo a, NameTableEntryInfo b) {
			return a.entry.compareTo(b.entry);
		}
	};
	
	private static final Comparator<NameTableEntryInfo> SORT_BY_LOCATION = new Comparator<NameTableEntryInfo>() {
		@Override
		public int compare(NameTableEntryInfo a, NameTableEntryInfo b) {
			return a.location - b.location;
		}
	};
}
