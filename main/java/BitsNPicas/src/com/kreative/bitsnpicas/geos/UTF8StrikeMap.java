package com.kreative.bitsnpicas.geos;

public class UTF8StrikeMap {
	public final Entry[] lowEntries = new Entry[32];
	public final SubMap[] highEntries = new SubMap[16];
	public final AstralMap[] astralEntries = new AstralMap[6];
	
	public void clear() {
		for (int i = 0; i < 32; i++) lowEntries[i] = null;
		for (int i = 0; i < 16; i++) highEntries[i] = null;
		for (int i = 0; i < 6; i++) astralEntries[i] = null;
	}
	
	public Entry get(int cp) {
		if (cp < 0x800) {
			return lowEntries[cp >> 6];
		} else if (cp < 0x10000) {
			SubMap sm = highEntries[cp >> 12];
			if (sm == null) return null;
			return sm.entries[(cp >> 6) & 0x3F];
		} else if (cp < 0x180000) {
			AstralMap am = astralEntries[cp >> 18];
			if (am == null) return null;
			SubMap sm = am.entries[(cp >> 12) & 0x3F];
			if (sm == null) return null;
			return sm.entries[(cp >> 6) & 0x3F];
		} else {
			return null;
		}
	}
	
	public boolean isEmpty() {
		for (Entry e : lowEntries) {
			if (e != null) {
				return false;
			}
		}
		for (SubMap e : highEntries) {
			if (e != null && !e.isEmpty()) {
				return false;
			}
		}
		for (AstralMap e : astralEntries) {
			if (e != null && !e.isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	public void remove(int cp) {
		if (cp < 0x800) {
			lowEntries[cp >> 6] = null;
		} else if (cp < 0x10000) {
			SubMap sm = highEntries[cp >> 12];
			if (sm == null) return;
			sm.entries[(cp >> 6) & 0x3F] = null;
			if (sm.isEmpty()) highEntries[cp >> 12] = null;
		} else if (cp < 0x180000) {
			AstralMap am = astralEntries[cp >> 18];
			if (am == null) return;
			SubMap sm = am.entries[(cp >> 12) & 0x3F];
			if (sm == null) return;
			sm.entries[(cp >> 6) & 0x3F] = null;
			if (sm.isEmpty()) am.entries[(cp >> 12) & 0x3F] = null;
			if (am.isEmpty()) astralEntries[cp >> 18] = null;
		}
	}
	
	public void set(int cp, int ri, int si) {
		if (cp < 0x800) {
			lowEntries[cp >> 6] = new Entry(ri, si);
		} else if (cp < 0x10000) {
			SubMap sm = highEntries[cp >> 12];
			if (sm == null) sm = highEntries[cp >> 12] = new SubMap();
			sm.entries[(cp >> 6) & 0x3F] = new Entry(ri, si);
		} else if (cp < 0x180000) {
			AstralMap am = astralEntries[cp >> 18];
			if (am == null) am = astralEntries[cp >> 18] = new AstralMap();
			SubMap sm = am.entries[(cp >> 12) & 0x3F];
			if (sm == null) sm = am.entries[(cp >> 12) & 0x3F] = new SubMap();
			sm.entries[(cp >> 6) & 0x3F] = new Entry(ri, si);
		}
	}
	
	public void read(byte[] data, int offset) {
		this.clear();
		for (int i = 0; i < 32; i++) {
			if (offset + 2 <= data.length) {
				int ri = data[offset+0] & 0xFF;
				int si = data[offset+1] & 0xFF;
				if (ri != 0 || si != 0) {
					lowEntries[i] = new Entry(ri, si);
				}
				offset += 2;
			} else {
				break;
			}
		}
		for (int i = 0; i < 16; i++) {
			if (offset + 2 <= data.length) {
				int eo = (
					(data[offset+0] & 0xFF) |
					((data[offset+1] & 0xFF) << 8)
				);
				if (eo != 0) {
					highEntries[i] = new SubMap();
					highEntries[i].read(data, eo);
				}
				offset += 2;
			} else {
				break;
			}
		}
		for (int i = 0; i < 6; i++) {
			if (offset + 2 <= data.length) {
				int eo = (
					(data[offset+0] & 0xFF) |
					((data[offset+1] & 0xFF) << 8)
				);
				if (eo != 0) {
					astralEntries[i] = new AstralMap();
					astralEntries[i].read(data, eo);
				}
				offset += 2;
			} else {
				break;
			}
		}
	}
	
	public byte[] write(int offset) {
		int length = 108;
		byte[][] smd = new byte[16][];
		for (int i = 0; i < 16; i++) {
			if (highEntries[i] != null) {
				smd[i] = highEntries[i].write();
				length += smd[i].length;
			}
		}
		byte[][] amd = new byte[6][];
		for (int i = 0; i < 6; i++) {
			if (astralEntries[i] != null) {
				amd[i] = astralEntries[i].write(offset + length);
				length += amd[i].length;
			}
		}
		byte[] data = new byte[length];
		
		int ptr = 108;
		for (int a = 0, i = 0; i < 32; i++, a += 2) {
			if (lowEntries[i] != null) {
				data[a+0] = (byte)(lowEntries[i].recordIndex);
				data[a+1] = (byte)(lowEntries[i].sectorIndex);
			}
		}
		for (int a = 64, i = 0; i < 16; i++, a += 2) {
			if (smd[i] != null) {
				data[a+0] = (byte)(offset + ptr);
				data[a+1] = (byte)((offset + ptr) >> 8);
				for (int j = 0; j < smd[i].length; j++) {
					data[ptr] = smd[i][j];
					ptr++;
				}
			}
		}
		for (int a = 96, i = 0; i < 6; i++, a += 2) {
			if (amd[i] != null) {
				data[a+0] = (byte)(offset + ptr);
				data[a+1] = (byte)((offset + ptr) >> 8);
				for (int j = 0; j < amd[i].length; j++) {
					data[ptr] = amd[i][j];
					ptr++;
				}
			}
		}
		return data;
	}
	
	public static final class Entry {
		public final int recordIndex;
		public final int sectorIndex;
		public final int offset;
		
		public Entry(int recordIndex, int sectorIndex) {
			this.recordIndex = recordIndex;
			this.sectorIndex = sectorIndex;
			this.offset = sectorIndex * 254;
		}
	}
	
	public static final class SubMap {
		public final Entry[] entries = new Entry[64];
		
		public void clear() {
			for (int i = 0; i < 64; i++) entries[i] = null;
		}
		
		public boolean isEmpty() {
			for (Entry e : entries) {
				if (e != null) {
					return false;
				}
			}
			return true;
		}
		
		public void read(byte[] data, int offset) {
			this.clear();
			for (int i = 0; i < 64; i++) {
				if (offset + 2 <= data.length) {
					int ri = data[offset+0] & 0xFF;
					int si = data[offset+1] & 0xFF;
					if (ri != 0 || si != 0) {
						entries[i] = new Entry(ri, si);
					}
					offset += 2;
				} else {
					break;
				}
			}
		}
		
		public byte[] write() {
			byte[] data = new byte[128];
			for (int a = 0, i = 0; i < 64; i++, a += 2) {
				if (entries[i] != null) {
					data[a+0] = (byte)(entries[i].recordIndex);
					data[a+1] = (byte)(entries[i].sectorIndex);
				}
			}
			return data;
		}
	}
	
	public static final class AstralMap {
		public final SubMap[] entries = new SubMap[64];
		
		public void clear() {
			for (int i = 0; i < 64; i++) entries[i] = null;
		}
		
		public boolean isEmpty() {
			for (SubMap e : entries) {
				if (e != null && !e.isEmpty()) {
					return false;
				}
			}
			return true;
		}
		
		public void read(byte[] data, int offset) {
			this.clear();
			for (int i = 0; i < 64; i++) {
				if (offset + 2 <= data.length) {
					int eo = (
						(data[offset+0] & 0xFF) |
						((data[offset+1] & 0xFF) << 8)
					);
					if (eo != 0) {
						entries[i] = new SubMap();
						entries[i].read(data, eo);
					}
					offset += 2;
				} else {
					break;
				}
			}
		}
		
		public byte[] write(int offset) {
			int length = 128;
			byte[][] smd = new byte[64][];
			for (int i = 0; i < 64; i++) {
				if (entries[i] != null) {
					smd[i] = entries[i].write();
					length += smd[i].length;
				}
			}
			byte[] data = new byte[length];
			
			int ptr = 128;
			for (int a = 0, i = 0; i < 64; i++, a += 2) {
				if (smd[i] != null) {
					data[a+0] = (byte)(offset + ptr);
					data[a+1] = (byte)((offset + ptr) >> 8);
					for (int j = 0; j < smd[i].length; j++) {
						data[ptr] = smd[i][j];
						ptr++;
					}
				}
			}
			return data;
		}
	}
}
