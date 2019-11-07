package com.kreative.bitsnpicas.geos;

import java.util.TreeSet;

public class UTF8StrikeIndex extends TreeSet<UTF8StrikeIndex.Entry> {
	private static final long serialVersionUID = 1L;
	
	public void read(byte[] data) {
		this.clear();
		for (int i = 0; i + 4 <= data.length; i += 4) {
			int recordIndex = data[i+0] & 0xFF;
			int sectorIndex = data[i+1] & 0xFF;
			int length = (data[i+2] & 0xFF) | ((data[i+3] & 0xFF) << 8);
			if (recordIndex == 0 && sectorIndex == 0 && length == 0) break;
			this.add(new Entry(recordIndex, sectorIndex, length));
		}
	}
	
	public byte[] write() {
		byte[] data = new byte[this.size() * 4];
		int i = 0;
		for (Entry e : this) {
			data[i+0] = (byte)(e.recordIndex);
			data[i+1] = (byte)(e.sectorIndex);
			data[i+2] = (byte)(e.length);
			data[i+3] = (byte)(e.length >> 8);
			i += 4;
		}
		return data;
	}
	
	public static final class Entry implements Comparable<Entry> {
		public final int recordIndex;
		public final int sectorIndex;
		public final int offset;
		public final int length;
		
		public Entry(int recordIndex, int sectorIndex, int length) {
			this.recordIndex = recordIndex;
			this.sectorIndex = sectorIndex;
			this.offset = sectorIndex * 254;
			this.length = length;
		}
		
		@Override
		public int compareTo(Entry that) {
			if (this.recordIndex != that.recordIndex) {
				return this.recordIndex - that.recordIndex;
			} else {
				return this.sectorIndex - that.sectorIndex;
			}
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof Entry) {
				Entry that = (Entry)o;
				return (
					this.recordIndex == that.recordIndex &&
					this.sectorIndex == that.sectorIndex
				);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return (this.recordIndex << 16) ^ this.sectorIndex;
		}
		
		@Override
		public String toString() {
			return recordIndex + ":" + sectorIndex + ":" + length;
		}
	}
}
