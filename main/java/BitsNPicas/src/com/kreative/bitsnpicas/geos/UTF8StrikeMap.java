package com.kreative.bitsnpicas.geos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UTF8StrikeMap {
	public final UTF8StrikeEntry[] lowEntries = new UTF8StrikeEntry[32];
	public final SubMap[] highEntries = new SubMap[16];
	public final AstralMap[] astralEntries = new AstralMap[6];
	
	public void clear() {
		for (int i = 0; i < 32; i++) lowEntries[i] = null;
		for (int i = 0; i < 16; i++) highEntries[i] = null;
		for (int i = 0; i < 6; i++) astralEntries[i] = null;
	}
	
	public List<UTF8StrikeEntry> entryList() {
		List<UTF8StrikeEntry> list = new ArrayList<UTF8StrikeEntry>();
		for (UTF8StrikeEntry e : lowEntries) {
			if (e != null) {
				list.add(e);
			}
		}
		for (SubMap e : highEntries) {
			if (e != null) {
				list.addAll(e.entryList());
			}
		}
		for (AstralMap e : astralEntries) {
			if (e != null) {
				list.addAll(e.entryList());
			}
		}
		return list;
	}
	
	public UTF8StrikeEntry get(int cp) {
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
		for (UTF8StrikeEntry e : lowEntries) {
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
	
	public void remap(Map<UTF8StrikeEntry,UTF8StrikeEntry> remap) {
		for (int i = 0; i < 32; i++) {
			if (remap.containsKey(lowEntries[i])) {
				lowEntries[i] = remap.get(lowEntries[i]);
			}
		}
		for (SubMap e : highEntries) {
			if (e != null) {
				e.remap(remap);
			}
		}
		for (AstralMap e : astralEntries) {
			if (e != null) {
				e.remap(remap);
			}
		}
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
	
	public void set(int cp, int ri, int si, int len) {
		if (cp < 0x800) {
			lowEntries[cp >> 6] = new UTF8StrikeEntry(ri, si, len);
		} else if (cp < 0x10000) {
			SubMap sm = highEntries[cp >> 12];
			if (sm == null) sm = highEntries[cp >> 12] = new SubMap();
			sm.entries[(cp >> 6) & 0x3F] = new UTF8StrikeEntry(ri, si, len);
		} else if (cp < 0x180000) {
			AstralMap am = astralEntries[cp >> 18];
			if (am == null) am = astralEntries[cp >> 18] = new AstralMap();
			SubMap sm = am.entries[(cp >> 12) & 0x3F];
			if (sm == null) sm = am.entries[(cp >> 12) & 0x3F] = new SubMap();
			sm.entries[(cp >> 6) & 0x3F] = new UTF8StrikeEntry(ri, si, len);
		}
	}
	
	public void read(byte[] data, int offset) {
		this.clear();
		for (int i = 0; i < 32; i++) {
			if (offset + 4 <= data.length) {
				int ri = data[offset+0] & 0xFF;
				int si = data[offset+1] & 0xFF;
				int len = data[offset+2] & 0xFF;
				len |= ((data[offset+3] & 0xFF) << 8);
				if (ri != 0 || si != 0 || len != 0) {
					lowEntries[i] = new UTF8StrikeEntry(ri, si, len);
				}
				offset += 4;
			} else {
				break;
			}
		}
		for (int i = 0; i < 16; i++) {
			if (offset + 2 <= data.length) {
				int eo = data[offset+0] & 0xFF;
				eo |= ((data[offset+1] & 0xFF) << 8);
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
				int eo = data[offset+0] & 0xFF;
				eo |= ((data[offset+1] & 0xFF) << 8);
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
		int length = 172;
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
		
		int ptr = 172;
		for (int a = 0, i = 0; i < 32; i++, a += 4) {
			if (lowEntries[i] != null) {
				data[a+0] = (byte)(lowEntries[i].recordIndex);
				data[a+1] = (byte)(lowEntries[i].sectorIndex);
				data[a+2] = (byte)(lowEntries[i].length);
				data[a+3] = (byte)(lowEntries[i].length >> 8);
			}
		}
		for (int a = 128, i = 0; i < 16; i++, a += 2) {
			if (smd[i] != null) {
				data[a+0] = (byte)(offset + ptr);
				data[a+1] = (byte)((offset + ptr) >> 8);
				for (int j = 0; j < smd[i].length; j++) {
					data[ptr] = smd[i][j];
					ptr++;
				}
			}
		}
		for (int a = 160, i = 0; i < 6; i++, a += 2) {
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
	
	public static final class SubMap {
		public final UTF8StrikeEntry[] entries = new UTF8StrikeEntry[64];
		
		public void clear() {
			for (int i = 0; i < 64; i++) {
				entries[i] = null;
			}
		}
		
		public List<UTF8StrikeEntry> entryList() {
			List<UTF8StrikeEntry> list = new ArrayList<UTF8StrikeEntry>();
			for (UTF8StrikeEntry e : entries) {
				if (e != null) {
					list.add(e);
				}
			}
			return list;
		}
		
		public boolean isEmpty() {
			for (UTF8StrikeEntry e : entries) {
				if (e != null) {
					return false;
				}
			}
			return true;
		}
		
		public void remap(Map<UTF8StrikeEntry,UTF8StrikeEntry> remap) {
			for (int i = 0; i < 64; i++) {
				if (remap.containsKey(entries[i])) {
					entries[i] = remap.get(entries[i]);
				}
			}
		}
		
		public void read(byte[] data, int offset) {
			this.clear();
			for (int i = 0; i < 64; i++) {
				if (offset + 4 <= data.length) {
					int ri = data[offset+0] & 0xFF;
					int si = data[offset+1] & 0xFF;
					int len = data[offset+2] & 0xFF;
					len |= ((data[offset+3] & 0xFF) << 8);
					if (ri != 0 || si != 0 || len != 0) {
						entries[i] = new UTF8StrikeEntry(ri, si, len);
					}
					offset += 4;
				} else {
					break;
				}
			}
		}
		
		public byte[] write() {
			byte[] data = new byte[256];
			for (int a = 0, i = 0; i < 64; i++, a += 4) {
				if (entries[i] != null) {
					data[a+0] = (byte)(entries[i].recordIndex);
					data[a+1] = (byte)(entries[i].sectorIndex);
					data[a+2] = (byte)(entries[i].length);
					data[a+3] = (byte)(entries[i].length >> 8);
				}
			}
			return data;
		}
	}
	
	public static final class AstralMap {
		public final SubMap[] entries = new SubMap[64];
		
		public void clear() {
			for (int i = 0; i < 64; i++) {
				entries[i] = null;
			}
		}
		
		public List<UTF8StrikeEntry> entryList() {
			List<UTF8StrikeEntry> list = new ArrayList<UTF8StrikeEntry>();
			for (SubMap e : entries) {
				if (e != null) {
					list.addAll(e.entryList());
				}
			}
			return list;
		}
		
		public boolean isEmpty() {
			for (SubMap e : entries) {
				if (e != null && !e.isEmpty()) {
					return false;
				}
			}
			return true;
		}
		
		public void remap(Map<UTF8StrikeEntry,UTF8StrikeEntry> remap) {
			for (SubMap e : entries) {
				if (e != null) {
					e.remap(remap);
				}
			}
		}
		
		public void read(byte[] data, int offset) {
			this.clear();
			for (int i = 0; i < 64; i++) {
				if (offset + 2 <= data.length) {
					int eo = data[offset+0] & 0xFF;
					eo |= ((data[offset+1] & 0xFF) << 8);
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
