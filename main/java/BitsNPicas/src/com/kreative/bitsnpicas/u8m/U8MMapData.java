package com.kreative.bitsnpicas.u8m;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class U8MMapData extends ArrayList<U8MMapEntry> {
	private static final long serialVersionUID = 1L;
	
	public int mapLocation;
	public int mapEntryCount;
	
	public boolean mapContains(int i) {
		for (U8MMapEntry e : this) {
			if (e.contains(i)) {
				return true;
			}
		}
		return false;
	}
	
	public int mapGet(int i) {
		for (U8MMapEntry e : this) {
			if (e.contains(i)) {
				return e.get(i);
			}
		}
		return 0;
	}
	
	public void mapPut(int index, int outdex) {
		for (U8MMapEntry e : this) {
			if (e.contains(index) && e.get(index) == outdex) {
				return;
			}
			if (e.contains(index - 1) && e.get(index - 1) == (outdex - 1)) {
				e.lastIndexValue++;
				return;
			}
		}
		U8MMapEntry e = new U8MMapEntry();
		e.firstIndexValue = index;
		e.lastIndexValue = index;
		e.firstOutdexValue = outdex;
		this.add(e);
		Collections.sort(this);
	}
	
	public int mapSize() {
		int n = 0;
		for (U8MMapEntry e : this) {
			n += e.size();
		}
		return n;
	}
	
	public int setMapLocation(int loc) {
		if (this.isEmpty()) {
			mapLocation = 0;
			mapEntryCount = 0;
			return loc;
		} else {
			int len = ((mapEntryCount = this.size())) * 4;
			if ((loc & 0xFF) + len > 0x100) loc = (loc | 0xFF) + 1;
			return ((mapLocation = loc)) + len;
		}
	}
	
	public void readHeader(DataInput in) throws IOException {
		int v = Integer.reverseBytes(in.readInt());
		mapLocation = v & 0xFFFFFF;
		mapEntryCount = (v >> 24) & 0xFF;
	}
	
	public void writeHeader(DataOutput out) throws IOException {
		int v = (mapLocation & 0xFFFFFF) | (mapEntryCount << 24);
		out.writeInt(Integer.reverseBytes(v));
	}
	
	public void readData(DataInput in) throws IOException {
		this.clear();
		for (int i = 0; i < mapEntryCount; i++) {
			U8MMapEntry e = new U8MMapEntry();
			e.read(in);
			this.add(e);
		}
	}
	
	public void writeData(DataOutput out) throws IOException {
		for (U8MMapEntry e : this) {
			e.write(out);
		}
	}
}
