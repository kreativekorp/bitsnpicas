package com.kreative.bitsnpicas.geos;

public class UTF8StrikeEntry implements Comparable<UTF8StrikeEntry> {
	public final int recordIndex;
	public final int sectorIndex;
	public final int offset;
	public final int length;
	
	public UTF8StrikeEntry(int recordIndex, int sectorIndex, int length) {
		this.recordIndex = recordIndex;
		this.sectorIndex = sectorIndex;
		this.offset = sectorIndex * 254;
		this.length = length;
	}
	
	@Override
	public int compareTo(UTF8StrikeEntry that) {
		if (this.recordIndex != that.recordIndex) {
			return this.recordIndex - that.recordIndex;
		} else {
			return this.sectorIndex - that.sectorIndex;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof UTF8StrikeEntry) {
			UTF8StrikeEntry that = (UTF8StrikeEntry)o;
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
