package com.kreative.bitsnpicas.geos;

import java.util.TreeSet;

public class UTF8StrikeIndex extends TreeSet<UTF8StrikeEntry> {
	private static final long serialVersionUID = 1L;
	
	public void read(byte[] data) {
		this.clear();
		for (int i = 0; i + 4 <= data.length; i += 4) {
			int recordIndex = data[i+0] & 0xFF;
			int sectorIndex = data[i+1] & 0xFF;
			int length = (data[i+2] & 0xFF) | ((data[i+3] & 0xFF) << 8);
			if (recordIndex == 0 && sectorIndex == 0 && length == 0) break;
			this.add(new UTF8StrikeEntry(recordIndex, sectorIndex, length));
		}
	}
	
	public byte[] write() {
		byte[] data = new byte[this.size() * 4];
		int i = 0;
		for (UTF8StrikeEntry e : this) {
			data[i+0] = (byte)(e.recordIndex);
			data[i+1] = (byte)(e.sectorIndex);
			data[i+2] = (byte)(e.length);
			data[i+3] = (byte)(e.length >> 8);
			i += 4;
		}
		return data;
	}
}
