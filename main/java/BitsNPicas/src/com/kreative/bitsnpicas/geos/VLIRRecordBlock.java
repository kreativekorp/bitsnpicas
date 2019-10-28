package com.kreative.bitsnpicas.geos;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

public class VLIRRecordBlock extends ArrayList<VLIRRecordBlock.Entry> {
	private static final long serialVersionUID = 1L;
	
	public void read(DataInput in) throws IOException {
		this.clear();
		boolean eof = false;
		for (int i = 0; i < 127; i++) {
			int sectorCount = in.readUnsignedByte();
			int byteCount = in.readUnsignedByte();
			if (!eof) {
				if (sectorCount == 0 && byteCount == 0) eof = true;
				else this.add(new Entry(sectorCount, byteCount));
			}
		}
	}
	
	public void write(DataOutput out) throws IOException {
		for (int i = 0; i < this.size() && i < 127; i++) {
			Entry e = this.get(i);
			out.writeByte(e.sectorCount);
			out.writeByte(e.byteCount);
		}
		for (int i = this.size(); i < 127; i++) {
			out.writeByte(0);
			out.writeByte(0);
		}
	}
	
	public static final class Entry {
		public final int sectorCount;
		public final int byteCount;
		public final int length;
		
		public Entry(int sectorCount, int byteCount) {
			this.sectorCount = sectorCount;
			this.byteCount = byteCount;
			this.length = (sectorCount * 254) - (255 - byteCount);
		}
		
		public Entry(int length) {
			this.sectorCount = (length + 253) / 254;
			this.byteCount = 255 - (sectorCount * 254 - length);
			this.length = length;
		}
		
		@Override
		public String toString() {
			return sectorCount + "." + byteCount + ":" + length;
		}
	}
}
