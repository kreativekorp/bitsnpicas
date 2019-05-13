package com.kreative.bitsnpicas.truetype;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class EbdtTable extends MapBasedTable<Integer, EbdtEntry> {
	public SbitTableType type;
	public int version;
	
	public EbdtTable(SbitTableType type) {
		this.type = type;
		this.version = type.version;
	}
	
	@Override
	public String tableName() {
		return type.dataTableName;
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{type.locTableName};
	}
	
	public int getNextKey() {
		int nextKey = 4;
		for (Map.Entry<Integer, EbdtEntry> e : this.entrySet()) {
			int end = e.getKey() + e.getValue().length();
			if (end > nextKey) nextKey = end;
		}
		return nextKey;
	}
	
	public void recalculate(EblcTable eblc) {
		SortedSet<Integer> oldOffsets = new TreeSet<Integer>();
		SortedMap<Integer,Integer> remap = new TreeMap<Integer,Integer>();
		Map<Integer,EbdtEntry> newmap = new HashMap<Integer,EbdtEntry>();
		int newOffset = 4;
		oldOffsets.addAll(this.keySet());
		for (int oldOffset : oldOffsets) {
			EbdtEntry entry = this.get(oldOffset);
			remap.put(oldOffset, newOffset);
			newmap.put(newOffset, entry);
			newOffset += entry.length();
		}
		for (EblcBitmapSize ebs : eblc) {
			for (EblcIndexSubtable st : ebs) {
				int[] oldOffs = st.getOffsets();
				int[] newOffs = new int[oldOffs.length];
				int newBase = -1;
				for (int i = 0; i < oldOffs.length; i++) {
					newOffs[i] = remap(remap, oldOffs[i]);
					if (newBase < 0 || newOffs[i] < newBase) {
						newBase = newOffs[i];
					}
				}
				st.header.imageDataOffset = newBase;
				st.setOffsets(newOffs);
			}
		}
		this.clear();
		this.putAll(newmap);
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		Map<Integer, byte[]> dataRuns = new HashMap<Integer, byte[]>();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeInt(version);
		dos.flush(); dos.close();
		byte[] data = bos.toByteArray();
		dataRuns.put(0, data);
		int totalLength = data.length;
		for (Map.Entry<Integer, EbdtEntry> e : this.entrySet()) {
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			e.getValue().write(dos);
			dos.flush(); dos.close();
			data = bos.toByteArray();
			dataRuns.put(e.getKey(), data);
			int end = e.getKey() + data.length;
			if (end > totalLength) totalLength = end;
		}
		byte[] dest = new byte[totalLength];
		for (Map.Entry<Integer, byte[]> e : dataRuns.entrySet()) {
			byte[] src = e.getValue();
			for (int di = e.getKey(), si = 0; si < src.length; si++, di++) {
				dest[di] = src[si];
			}
		}
		out.write(dest);
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		version = in.readInt();
		EblcTable eblc = (EblcTable)dependencies[0];
		for (EblcBitmapSize ebs : eblc) {
			for (EblcIndexSubtable st : ebs) {
				int[] offsets = st.getOffsets();
				for (int i = 1; i < offsets.length; i++) {
					int dataOffset = offsets[i - 1];
					int dataLength = offsets[i] - dataOffset;
					if (dataLength > 0 && !this.containsKey(dataOffset)) {
						in.reset();
						in.skip(dataOffset);
						EbdtEntry e = createEbdtEntry(st.header.imageFormat);
						e.read(in, dataLength);
						this.put(dataOffset, e);
					}
				}
			}
		}
	}
	
	private static final EbdtEntry createEbdtEntry(int format) throws IOException {
		switch (format) {
			case 1: return new EbdtEntryFormat1();
			case 2: return new EbdtEntryFormat2();
			case 5: return new EbdtEntryFormat5();
			case 6: return new EbdtEntryFormat6();
			case 7: return new EbdtEntryFormat7();
			case 8: return new EbdtEntryFormat8();
			case 9: return new EbdtEntryFormat9();
			case 17: return new CbdtEntryFormat17();
			case 18: return new CbdtEntryFormat18();
			case 19: return new CbdtEntryFormat19();
			default: throw new IOException("invalid imageFormat: " + format);
		}
	}
	
	private static final int remap(SortedMap<Integer,Integer> remap, int v) {
		int lastOldBase = 0, lastNewBase = 0;
		for (Map.Entry<Integer,Integer> e : remap.entrySet()) {
			int oldBase = e.getKey(), newBase = e.getValue();
			if (v < oldBase) return lastNewBase + (v - lastOldBase);
			lastOldBase = oldBase; lastNewBase = newBase;
		}
		return lastNewBase + (v - lastOldBase);
	}
}
