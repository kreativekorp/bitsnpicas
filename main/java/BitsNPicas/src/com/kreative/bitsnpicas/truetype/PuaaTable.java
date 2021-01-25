package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PuaaTable extends ListBasedTable<PuaaSubtable> {
	public static final int DEFAULT_VERSION = 1;
	
	public int version = DEFAULT_VERSION;
	
	@Override
	public String tableName() {
		return "PUAA";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	public PuaaSubtable getOrCreateSubtable(String property) {
		if (property == null) return null;
		for (PuaaSubtable subtable : this) {
			if (property.equals(subtable.property)) {
				return subtable;
			}
		}
		PuaaSubtable subtable = new PuaaSubtable();
		subtable.property = property;
		this.add(subtable);
		return subtable;
	}
	
	public PuaaSubtable getSubtable(String property) {
		if (property == null) return null;
		for (PuaaSubtable subtable : this) {
			if (property.equals(subtable.property)) {
				return subtable;
			}
		}
		return null;
	}
	
	public String getPropertyValue(String property, int cp) {
		if (property == null) return null;
		for (PuaaSubtable subtable : this) {
			if (property.equals(subtable.property)) {
				return subtable.getPropertyValue(cp);
			}
		}
		return null;
	}
	
	public void removeEmptySubtables() {
		Iterator<PuaaSubtable> iter = this.iterator();
		while (iter.hasNext()) {
			PuaaSubtable subtable = iter.next();
			if (subtable == null || subtable.isEmpty()) {
				iter.remove();
			}
		}
	}
	
	public void sortSubtables() {
		Collections.sort(this, BY_PROPERTY);
		for (PuaaSubtable subtable : this) {
			if (subtable.isSortable()) {
				Collections.sort(subtable, BY_CODE_POINT);
			}
		}
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		removeEmptySubtables();
		sortSubtables();
		
		// Calculate table header values.
		int propertyCount = this.size();
		int[] propertyNameOffset = new int[propertyCount];
		int[] subtableHeaderOffset = new int[propertyCount];
		int[] entryCount = new int[propertyCount];
		int[][] entryType = new int[propertyCount][];
		int[][] entryData = new int[propertyCount][];
		int[][] valueCount = new int[propertyCount][];
		int[][][] valueData = new int[propertyCount][][];
		Map<String,Integer> stringTable = new HashMap<String,Integer>();
		List<byte[]> stringData = new ArrayList<byte[]>();
		int p = 4 + propertyCount * 8;
		
		// Calculate subtable header values.
		for (int i = 0; i < propertyCount; i++) {
			subtableHeaderOffset[i] = p;
			entryCount[i] = this.get(i).size();
			entryType[i] = new int[entryCount[i]];
			entryData[i] = new int[entryCount[i]];
			valueCount[i] = new int[entryCount[i]];
			valueData[i] = new int[entryCount[i]][];
			p += 2 + entryCount[i] * 10;
		}
		
		// Calculate entry header values.
		for (int i = 0; i < propertyCount; i++) {
			for (int j = 0; j < entryCount[i]; j++) {
				PuaaSubtableEntry e = this.get(i).get(j);
				if (e instanceof PuaaSubtableEntry.Single) {
					entryType[i][j] = PuaaSubtableEntry.SINGLE;
				}
				if (e instanceof PuaaSubtableEntry.Multiple) {
					entryType[i][j] = PuaaSubtableEntry.MULTIPLE;
					entryData[i][j] = p;
					valueCount[i][j] = ((PuaaSubtableEntry.Multiple)e).values.length;
					valueData[i][j] = new int[valueCount[i][j]];
					p += 2 + valueCount[i][j] * 4;
				}
				if (e instanceof PuaaSubtableEntry.Boolean) {
					entryType[i][j] = PuaaSubtableEntry.BOOLEAN;
					entryData[i][j] = ((PuaaSubtableEntry.Boolean)e).value ? -1 : 0;
				}
				if (e instanceof PuaaSubtableEntry.Decimal) {
					entryType[i][j] = PuaaSubtableEntry.DECIMAL;
					entryData[i][j] = ((PuaaSubtableEntry.Decimal)e).value;
				}
				if (e instanceof PuaaSubtableEntry.Hexadecimal) {
					entryType[i][j] = PuaaSubtableEntry.HEXADECIMAL;
					entryData[i][j] = ((PuaaSubtableEntry.Hexadecimal)e).value;
				}
				if (e instanceof PuaaSubtableEntry.HexMultiple) {
					entryType[i][j] = PuaaSubtableEntry.HEXMULTIPLE;
					entryData[i][j] = p;
					valueCount[i][j] = ((PuaaSubtableEntry.HexMultiple)e).values.length;
					valueData[i][j] = ((PuaaSubtableEntry.HexMultiple)e).values;
					p += 2 + valueCount[i][j] * 4;
				}
				if (e instanceof PuaaSubtableEntry.HexSequence) {
					entryType[i][j] = PuaaSubtableEntry.HEXSEQUENCE;
					entryData[i][j] = p;
					valueCount[i][j] = ((PuaaSubtableEntry.HexSequence)e).values.length;
					valueData[i][j] = ((PuaaSubtableEntry.HexSequence)e).values;
					p += 2 + valueCount[i][j] * 4;
				}
				if (e instanceof PuaaSubtableEntry.CaseMapping) {
					entryType[i][j] = PuaaSubtableEntry.CASEMAPPING;
					entryData[i][j] = p;
					valueCount[i][j] = ((PuaaSubtableEntry.CaseMapping)e).values.length + 1;
					valueData[i][j] = new int[valueCount[i][j]];
					p += 2 + valueCount[i][j] * 4;
				}
				if (e instanceof PuaaSubtableEntry.NameAlias) {
					entryType[i][j] = PuaaSubtableEntry.NAMEALIAS;
					entryData[i][j] = p;
					valueCount[i][j] = 2;
					valueData[i][j] = new int[2];
					p += 10;
				}
			}
		}
		
		// Calculate property name offsets.
		for (int i = 0; i < propertyCount; i++) {
			String s = this.get(i).property;
			p = setString(propertyNameOffset, i, s, stringTable, stringData, p, true);
		}
		
		// Calculate string data offsets.
		for (int i = 0; i < propertyCount; i++) {
			for (int j = 0; j < entryCount[i]; j++) {
				PuaaSubtableEntry e = this.get(i).get(j);
				if (e instanceof PuaaSubtableEntry.Single) {
					String s = ((PuaaSubtableEntry.Single)e).value;
					p = setString(entryData[i], j, s, stringTable, stringData, p, false);
				}
				if (e instanceof PuaaSubtableEntry.Multiple) {
					for (int k = 0; k < valueCount[i][j]; k++) {
						String s = ((PuaaSubtableEntry.Multiple)e).values[k];
						p = setString(valueData[i][j], k, s, stringTable, stringData, p, false);
					}
				}
				if (e instanceof PuaaSubtableEntry.CaseMapping) {
					int[] values = ((PuaaSubtableEntry.CaseMapping)e).values;
					for (int k = 0; k < values.length; k++) valueData[i][j][k] = values[k];
					String s = ((PuaaSubtableEntry.CaseMapping)e).condition;
					p = setString(valueData[i][j], values.length, s, stringTable, stringData, p, false);
				}
				if (e instanceof PuaaSubtableEntry.NameAlias) {
					String s1 = ((PuaaSubtableEntry.NameAlias)e).alias;
					p = setString(valueData[i][j], 0, s1, stringTable, stringData, p, false);
					String s2 = ((PuaaSubtableEntry.NameAlias)e).type;
					p = setString(valueData[i][j], 1, s2, stringTable, stringData, p, false);
				}
			}
		}
		
		// Write table header.
		out.writeShort(version);
		out.writeShort(propertyCount);
		for (int i = 0; i < propertyCount; i++) {
			out.writeInt(propertyNameOffset[i]);
			out.writeInt(subtableHeaderOffset[i]);
		}
		
		// Write subtable headers.
		for (int i = 0; i < propertyCount; i++) {
			out.writeShort(entryCount[i]);
			for (int j = 0; j < entryCount[i]; j++) {
				PuaaSubtableEntry e = this.get(i).get(j);
				out.writeByte(entryType[i][j]);
				out.writeByte(e.firstCodePoint >> 16);
				out.writeShort(e.firstCodePoint);
				out.writeShort(e.lastCodePoint);
				out.writeInt(entryData[i][j]);
			}
		}
		
		// Write entry data.
		for (int i = 0; i < propertyCount; i++) {
			for (int j = 0; j < entryCount[i]; j++) {
				if (valueData[i][j] != null) {
					out.writeShort(valueCount[i][j]);
					for (int k = 0; k < valueCount[i][j]; k++) {
						out.writeInt(valueData[i][j][k]);
					}
				}
			}
		}
		
		// Write string data.
		for (byte[] d : stringData) {
			out.writeByte(d.length);
			out.write(d);
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		// Read table header.
		version = in.readUnsignedShort();
		int propertyCount = in.readUnsignedShort();
		int[] propertyNameOffset = new int[propertyCount];
		int[] subtableHeaderOffset = new int[propertyCount];
		for (int i = 0; i < propertyCount; i++) {
			propertyNameOffset[i] = in.readInt();
			subtableHeaderOffset[i] = in.readInt();
		}
		
		// Read subtables.
		for (int i = 0; i < propertyCount; i++) {
			// Read property name.
			PuaaSubtable subtable = new PuaaSubtable();
			subtable.property = readString(in, propertyNameOffset[i]);
			
			// Read subtable header.
			in.reset();
			in.skipBytes(subtableHeaderOffset[i]);
			int entryCount = in.readUnsignedShort();
			int[] entryType = new int[entryCount];
			int[] firstCodePoint = new int[entryCount];
			int[] lastCodePoint = new int[entryCount];
			int[] entryData = new int[entryCount];
			for (int j = 0; j < entryCount; j++) {
				entryType[j] = in.readUnsignedByte();
				int plane = in.readUnsignedByte() << 16;
				firstCodePoint[j] = in.readUnsignedShort() | plane;
				lastCodePoint[j] = in.readUnsignedShort() | plane;
				entryData[j] = in.readInt();
			}
			
			// Read entries.
			for (int j = 0; j < entryCount; j++) {
				switch (entryType[j]) {
				case PuaaSubtableEntry.SINGLE:
					PuaaSubtableEntry.Single se = new PuaaSubtableEntry.Single();
					se.firstCodePoint = firstCodePoint[j];
					se.lastCodePoint = lastCodePoint[j];
					se.value = readString(in, entryData[j]);
					subtable.add(se);
					break;
					
				case PuaaSubtableEntry.MULTIPLE:
					PuaaSubtableEntry.Multiple me = new PuaaSubtableEntry.Multiple();
					me.firstCodePoint = firstCodePoint[j];
					me.lastCodePoint = lastCodePoint[j];
					int[] mv = readIntArray(in, entryData[j]);
					if (mv != null) {
						me.values = new String[mv.length];
						for (int k = 0; k < mv.length; k++) {
							me.values[k] = readString(in, mv[k]);
						}
					}
					subtable.add(me);
					break;
					
				case PuaaSubtableEntry.BOOLEAN:
					PuaaSubtableEntry.Boolean be = new PuaaSubtableEntry.Boolean();
					be.firstCodePoint = firstCodePoint[j];
					be.lastCodePoint = lastCodePoint[j];
					be.value = entryData[j] != 0;
					subtable.add(be);
					break;
					
				case PuaaSubtableEntry.DECIMAL:
					PuaaSubtableEntry.Decimal de = new PuaaSubtableEntry.Decimal();
					de.firstCodePoint = firstCodePoint[j];
					de.lastCodePoint = lastCodePoint[j];
					de.value = entryData[j];
					subtable.add(de);
					break;
					
				case PuaaSubtableEntry.HEXADECIMAL:
					PuaaSubtableEntry.Hexadecimal he = new PuaaSubtableEntry.Hexadecimal();
					he.firstCodePoint = firstCodePoint[j];
					he.lastCodePoint = lastCodePoint[j];
					he.value = entryData[j];
					subtable.add(he);
					break;
					
				case PuaaSubtableEntry.HEXMULTIPLE:
					PuaaSubtableEntry.HexMultiple xe = new PuaaSubtableEntry.HexMultiple();
					xe.firstCodePoint = firstCodePoint[j];
					xe.lastCodePoint = lastCodePoint[j];
					xe.values = readIntArray(in, entryData[j]);
					subtable.add(xe);
					break;
					
				case PuaaSubtableEntry.HEXSEQUENCE:
					PuaaSubtableEntry.HexSequence qe = new PuaaSubtableEntry.HexSequence();
					qe.firstCodePoint = firstCodePoint[j];
					qe.lastCodePoint = lastCodePoint[j];
					qe.values = readIntArray(in, entryData[j]);
					subtable.add(qe);
					break;
					
				case PuaaSubtableEntry.CASEMAPPING:
					PuaaSubtableEntry.CaseMapping ce = new PuaaSubtableEntry.CaseMapping();
					ce.firstCodePoint = firstCodePoint[j];
					ce.lastCodePoint = lastCodePoint[j];
					int[] cv = readIntArray(in, entryData[j]);
					if (cv != null && cv.length > 0) {
						int n = cv.length - 1;
						ce.values = new int[n];
						for (int k = 0; k < n; k++) ce.values[k] = cv[k];
						ce.condition = readString(in, cv[n]);
					}
					subtable.add(ce);
					break;
					
				case PuaaSubtableEntry.NAMEALIAS:
					PuaaSubtableEntry.NameAlias ne = new PuaaSubtableEntry.NameAlias();
					ne.firstCodePoint = firstCodePoint[j];
					ne.lastCodePoint = lastCodePoint[j];
					int[] nv = readIntArray(in, entryData[j]);
					if (nv != null && nv.length > 1) {
						ne.alias = readString(in, nv[0]);
						ne.type = readString(in, nv[1]);
					}
					subtable.add(ne);
					break;
					
				default:
					throw new IOException("Invalid entryType: " + entryType[j]);
				}
			}
			
			this.add(subtable);
		}
	}
	
	private static final int minify(byte[] d) {
		int value = Integer.MIN_VALUE;
		if (d.length > 4) return 0;
		if (d.length > 3) { if (d[3] < 0) return 0; value |= (d[3] & 0x7F) <<  0; }
		if (d.length > 2) { if (d[2] < 0) return 0; value |= (d[2] & 0x7F) <<  8; }
		if (d.length > 1) { if (d[1] < 0) return 0; value |= (d[1] & 0x7F) << 16; }
		if (d.length > 0) { if (d[0] < 0) return 0; value |= (d[0] & 0x7F) << 24; }
		return value;
	}
	
	private static final int setString(
		int[] array, int index, String s,
		Map<String,Integer> stringTable,
		List<byte[]> stringData,
		int p, boolean forceFull
	) throws IOException {
		if (s == null) {
			array[index] = 0;
		} else if (stringTable.containsKey(s)) {
			array[index] = stringTable.get(s);
		} else {
			byte[] d = s.getBytes("UTF-8");
			if (forceFull || (array[index] = minify(d)) == 0) {
				array[index] = p;
				stringTable.put(s, p);
				stringData.add(d);
				p += d.length + 1;
			}
		}
		return p;
	}
	
	private static final String readString(DataInputStream in, int offset) throws IOException {
		if (offset > 0) {
			in.reset();
			in.skipBytes(offset);
			byte[] d = new byte[in.readUnsignedByte()];
			in.readFully(d);
			return new String(d, "UTF-8");
		}
		if (offset < 0) {
			StringBuffer sb = new StringBuffer();
			char ch0 = (char)((offset >> 24) & 0x7F); if (ch0 != 0) sb.append(ch0);
			char ch1 = (char)((offset >> 16) & 0x7F); if (ch1 != 0) sb.append(ch1);
			char ch2 = (char)((offset >>  8) & 0x7F); if (ch2 != 0) sb.append(ch2);
			char ch3 = (char)((offset >>  0) & 0x7F); if (ch3 != 0) sb.append(ch3);
			return sb.toString();
		}
		return null;
	}
	
	private static final int[] readIntArray(DataInputStream in, int offset) throws IOException {
		if (offset > 0) {
			in.reset();
			in.skipBytes(offset);
			int[] a = new int[in.readUnsignedShort()];
			for (int i = 0; i < a.length; i++) a[i] = in.readInt();
			return a;
		}
		return null;
	}
	
	private static final Comparator<PuaaSubtable> BY_PROPERTY = new Comparator<PuaaSubtable>() {
		@Override
		public int compare(PuaaSubtable a, PuaaSubtable b) {
			String ap = a.property; if (ap == null) ap = "";
			String bp = b.property; if (bp == null) bp = "";
			return ap.compareTo(bp);
		}
	};
	
	private static final Comparator<PuaaSubtableEntry> BY_CODE_POINT = new Comparator<PuaaSubtableEntry>() {
		@Override
		public int compare(PuaaSubtableEntry a, PuaaSubtableEntry b) {
			if (a.firstCodePoint != b.firstCodePoint) return a.firstCodePoint - b.firstCodePoint;
			if (a.lastCodePoint != b.lastCodePoint) return a.lastCodePoint - b.lastCodePoint;
			return 0;
		}
	};
}
