package com.kreative.mapedit;

public class MappingTable {
	private final CodePointSequence[] seqs = new CodePointSequence[256];
	private final MappingTable[] submaps = new MappingTable[256];
	
	public CodePointSequence getSequence(byte... i) { return getSequence(i, 0, i.length); }
	public CodePointSequence getSequence(int... i) { return getSequence(i, 0, i.length); }
	public MappingTable getSubtable(byte... i) { return getSubtable(i, 0, i.length); }
	public MappingTable getSubtable(int... i) { return getSubtable(i, 0, i.length); }
	public void setSequence(CodePointSequence s, byte... i) { setSequence(s, i, 0, i.length); }
	public void setSequence(CodePointSequence s, int... i) { setSequence(s, i, 0, i.length); }
	public void setSubtable(MappingTable m, byte... i) { setSubtable(m, i, 0, i.length); }
	public void setSubtable(MappingTable m, int... i) { setSubtable(m, i, 0, i.length); }
	
	private CodePointSequence getSequence(byte[] i, int o, int l) {
		if (l <= 0) return null;
		if (l <= 1) return seqs[i[o] & 0xFF];
		MappingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) return null;
		return submap.getSequence(i, o + 1, l - 1);
	}
	
	private CodePointSequence getSequence(int[] i, int o, int l) {
		if (l <= 0) return null;
		if (l <= 1) return seqs[i[o] & 0xFF];
		MappingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) return null;
		return submap.getSequence(i, o + 1, l - 1);
	}
	
	private MappingTable getSubtable(byte[] i, int o, int l) {
		if (l <= 0) return null;
		MappingTable submap = submaps[i[o] & 0xFF];
		if (l <= 1) return submap;
		if (submap == null) return null;
		return submap.getSubtable(i, o + 1, l - 1);
	}
	
	private MappingTable getSubtable(int[] i, int o, int l) {
		if (l <= 0) return null;
		MappingTable submap = submaps[i[o] & 0xFF];
		if (l <= 1) return submap;
		if (submap == null) return null;
		return submap.getSubtable(i, o + 1, l - 1);
	}
	
	private CodePointSequence setSequence(CodePointSequence s, byte[] i, int o, int l) {
		if (l <= 0) return s;
		if (l <= 1) return seqs[i[o] & 0xFF] = s;
		MappingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) submap = submaps[i[o] & 0xFF] = new MappingTable();
		return submap.setSequence(s, i, o + 1, l - 1);
	}
	
	private CodePointSequence setSequence(CodePointSequence s, int[] i, int o, int l) {
		if (l <= 0) return s;
		if (l <= 1) return seqs[i[o] & 0xFF] = s;
		MappingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) submap = submaps[i[o] & 0xFF] = new MappingTable();
		return submap.setSequence(s, i, o + 1, l - 1);
	}
	
	private MappingTable setSubtable(MappingTable m, byte[] i, int o, int l) {
		if (l <= 0) return m;
		if (l <= 1) return submaps[i[o] & 0xFF] = m;
		MappingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) submap = submaps[i[o] & 0xFF] = new MappingTable();
		return submap.setSubtable(m, i, o + 1, l - 1);
	}
	
	private MappingTable setSubtable(MappingTable m, int[] i, int o, int l) {
		if (l <= 0) return m;
		if (l <= 1) return submaps[i[o] & 0xFF] = m;
		MappingTable submap = submaps[i[o] & 0xFF];
		if (submap == null) submap = submaps[i[o] & 0xFF] = new MappingTable();
		return submap.setSubtable(m, i, o + 1, l - 1);
	}
}
