package com.kreative.unicode.data;

public class EncodingTable {
	protected final String[] seqs = new String[256];
	protected final EncodingTable[] submaps = new EncodingTable[256];
	
	public String getSequence(byte... i) { return getSequence(i, 0, i.length); }
	public String getSequence(int... i) { return getSequence(i, 0, i.length); }
	public EncodingTable getSubtable(byte... i) { return getSubtable(i, 0, i.length); }
	public EncodingTable getSubtable(int... i) { return getSubtable(i, 0, i.length); }
	public void setSequence(String s, byte... i) { setSequence(s, i, 0, i.length); }
	public void setSequence(String s, int... i) { setSequence(s, i, 0, i.length); }
	public void setSubtable(EncodingTable m, byte... i) { setSubtable(m, i, 0, i.length); }
	public void setSubtable(EncodingTable m, int... i) { setSubtable(m, i, 0, i.length); }
	
	public String getSequence(byte[] i, int o, int l) {
		if (l <= 0) return null;
		if (l <= 1) return seqs[i[o] & 0xFF];
		EncodingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) return null;
		return submap.getSequence(i, o + 1, l - 1);
	}
	
	public String getSequence(int[] i, int o, int l) {
		if (l <= 0) return null;
		if (l <= 1) return seqs[i[o] & 0xFF];
		EncodingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) return null;
		return submap.getSequence(i, o + 1, l - 1);
	}
	
	public EncodingTable getSubtable(byte[] i, int o, int l) {
		if (l <= 0) return null;
		EncodingTable submap = submaps[i[o] & 0xFF];
		if (l <= 1) return submap;
		if (submap == null) return null;
		return submap.getSubtable(i, o + 1, l - 1);
	}
	
	public EncodingTable getSubtable(int[] i, int o, int l) {
		if (l <= 0) return null;
		EncodingTable submap = submaps[i[o] & 0xFF];
		if (l <= 1) return submap;
		if (submap == null) return null;
		return submap.getSubtable(i, o + 1, l - 1);
	}
	
	public String setSequence(String s, byte[] i, int o, int l) {
		if (l <= 0) return s;
		if (l <= 1) return seqs[i[o] & 0xFF] = s;
		EncodingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) submap = submaps[i[o] & 0xFF] = new EncodingTable();
		return submap.setSequence(s, i, o + 1, l - 1);
	}
	
	public String setSequence(String s, int[] i, int o, int l) {
		if (l <= 0) return s;
		if (l <= 1) return seqs[i[o] & 0xFF] = s;
		EncodingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) submap = submaps[i[o] & 0xFF] = new EncodingTable();
		return submap.setSequence(s, i, o + 1, l - 1);
	}
	
	public EncodingTable setSubtable(EncodingTable m, byte[] i, int o, int l) {
		if (l <= 0) return m;
		if (l <= 1) return submaps[i[o] & 0xFF] = m;
		EncodingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) submap = submaps[i[o] & 0xFF] = new EncodingTable();
		return submap.setSubtable(m, i, o + 1, l - 1);
	}
	
	public EncodingTable setSubtable(EncodingTable m, int[] i, int o, int l) {
		if (l <= 0) return m;
		if (l <= 1) return submaps[i[o] & 0xFF] = m;
		EncodingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) submap = submaps[i[o] & 0xFF] = new EncodingTable();
		return submap.setSubtable(m, i, o + 1, l - 1);
	}
}
