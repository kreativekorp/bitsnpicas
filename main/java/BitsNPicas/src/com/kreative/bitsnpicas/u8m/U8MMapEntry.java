package com.kreative.bitsnpicas.u8m;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class U8MMapEntry implements Comparable<U8MMapEntry> {
	public int firstIndexValue;
	public int lastIndexValue;
	public int firstOutdexValue;
	
	public int lastOutdexValue() {
		return lastIndexValue - firstIndexValue + firstOutdexValue;
	}
	
	public boolean contains(int i) {
		return (i >= firstIndexValue && i <= lastIndexValue);
	}
	
	public int get(int i) {
		return i - firstIndexValue + firstOutdexValue;
	}
	
	public int size() {
		return lastIndexValue - firstIndexValue + 1;
	}
	
	public void read(DataInput in) throws IOException {
		firstIndexValue = in.readUnsignedByte();
		lastIndexValue = in.readUnsignedByte();
		firstOutdexValue = Short.reverseBytes(in.readShort()) & 0xFFFF;
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeByte(firstIndexValue);
		out.writeByte(lastIndexValue);
		out.writeShort(Short.reverseBytes((short)firstOutdexValue));
	}
	
	@Override
	public int compareTo(U8MMapEntry that) {
		return this.firstIndexValue - that.firstIndexValue;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof U8MMapEntry) {
			U8MMapEntry that = (U8MMapEntry)o;
			return (
				this.firstIndexValue == that.firstIndexValue &&
				this.lastIndexValue == that.lastIndexValue &&
				this.firstOutdexValue == that.firstOutdexValue
			);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(new int[] {
			firstIndexValue, lastIndexValue, firstOutdexValue
		});
	}
}
