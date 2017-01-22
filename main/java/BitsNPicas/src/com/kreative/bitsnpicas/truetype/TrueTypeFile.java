package com.kreative.bitsnpicas.truetype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrueTypeFile extends ArrayList<TrueTypeTable> {
	private static final long serialVersionUID = 1L;
	
	public static final int SCALER_TRUETYPE      = 0x00010000;
	public static final int SCALER_OPENTYPE      = 0x4F54544F;
	public static final int SCALER_TRUETYPE_GX   = 0x74727565;
	public static final int SCALER_POSTSCRIPT_GX = 0x74797031;
	public static final int SCALER_META          = 0x778E7A00;
	
	public int scaler = SCALER_TRUETYPE;
	
	private static final TrueTypeTable createTable(int tableId) {
		switch (tableId) {
			case 0x4F532F32: return new Os2Table();
			case 0x53564720: return new SvgTable();
			case 0x636D6170: return new CmapTable();
			case 0x676C7966: return new GlyfTable();
			case 0x68656164: return new HeadTable();
			case 0x68686561: return new HheaTable();
			case 0x686D7478: return new HmtxTable();
			case 0x6C6F6361: return new LocaTable();
			case 0x6D617870: return new MaxpTable();
			case 0x6E616D65: return new NameTable();
			case 0x706F7374: return new PostTable();
			case 0x73626978: return new SbixTable();
			default: return new UnknownTable(tableId);
		}
	}
	
	public TrueTypeTable getByTableName(String name) {
		if (name != null) {
			for (TrueTypeTable table : this) {
				if (name.equals(table.tableName())) {
					return table;
				}
			}
		}
		return null;
	}
	
	public TrueTypeTable getByTableId(int id) {
		for (TrueTypeTable table : this) {
			if (id == table.tableId()) {
				return table;
			}
		}
		return null;
	}
	
	public byte[] compile() throws IOException {
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteout);
		
		int numTables = this.size();
		int searchRange = (1 << 30);
		while (searchRange > numTables) { searchRange >>>= 1; }
		int entrySelector = Integer.numberOfTrailingZeros(searchRange);
		searchRange <<= 4;
		int rangeShift = (numTables << 4) - searchRange;
		int headerLen = 12 + (numTables << 4);
		int checksumLoc = -1;
		
		out.writeInt(scaler);
		out.writeShort(numTables);
		out.writeShort(searchRange);
		out.writeShort(entrySelector);
		out.writeShort(rangeShift);
		
		// Gather tables.
		Map<Integer,TrueTypeTable> tablesById = new HashMap<Integer,TrueTypeTable>();
		List<TrueTypeTableInfo> tableInfo = new ArrayList<TrueTypeTableInfo>();
		for (TrueTypeTable table : this) {
			int id = table.tableId();
			tablesById.put(id, table);
			TrueTypeTableInfo info = new TrueTypeTableInfo();
			info.table = table;
			info.id = id;
			tableInfo.add(info);
		}
		
		// Compile.
		int currentLocation = headerLen;
		for (TrueTypeTableInfo info : tableInfo) {
			int[] depIds = info.table.dependencyIds();
			TrueTypeTable[] depTables = new TrueTypeTable[depIds.length];
			for (int i = 0; i < depIds.length; i++) {
				if (tablesById.containsKey(depIds[i])) {
					depTables[i] = tablesById.get(depIds[i]);
				} else {
					throw unresolvedDependency(info.table);
				}
			}
			info.data = info.table.compile(depTables);
			info.length = info.data.length;
			info.location = currentLocation;
			if (info.id == 0x68656164) {
				// This is the whole-file checksum in the head block.
				// It must be set to zero while calculating the checksum,
				// then updated after.
				checksumLoc = info.location + 8;
				putInt(info.data, 8, 0);
			}
			info.checksum = chksum(info.data);
			currentLocation += (info.length + 3) & ~0x3;
		}
		
		// Write headers.
		Collections.sort(tableInfo, SORT_BY_ID);
		for (TrueTypeTableInfo info : tableInfo) {
			out.writeInt(info.id);
			out.writeInt(info.checksum);
			out.writeInt(info.location);
			out.writeInt(info.length);
		}
		
		// Write data.
		Collections.sort(tableInfo, SORT_BY_LOCATION);
		for (TrueTypeTableInfo info : tableInfo) {
			out.write(info.data);
			int p = info.length & 0x3;
			if (p > 0) {
				while (p < 4) {
					out.writeByte(0);
					p++;
				}
			}
		}
		
		out.flush();
		byteout.flush();
		out.close();
		byteout.close();
		byte[] data = byteout.toByteArray();
		if (checksumLoc >= 0) {
			putInt(data, checksumLoc, 0xB1B0AFBA - chksum(data));
		}
		return data;
	}
	
	public void decompile(byte[] data) throws IOException {
		ByteArrayInputStream bytein = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(bytein);
		
		scaler = in.readInt();
		int numTables = in.readUnsignedShort();
		/* int searchRange = */ in.readUnsignedShort();
		/* int entrySelector = */ in.readUnsignedShort();
		/* int rangeShift = */ in.readUnsignedShort();
		
		// Read headers.
		List<TrueTypeTableInfo> tableInfo = new ArrayList<TrueTypeTableInfo>();
		Map<Integer,TrueTypeTableInfo> tableInfoById = new HashMap<Integer,TrueTypeTableInfo>();
		for (int i = 0; i < numTables; i++) {
			TrueTypeTableInfo info = new TrueTypeTableInfo();
			info.id = in.readInt();
			info.checksum = in.readInt();
			info.location = in.readInt();
			info.length = in.readInt();
			tableInfo.add(info);
			tableInfoById.put(info.id, info);
		}
		
		// Read data.
		for (TrueTypeTableInfo info : tableInfo) {
			info.data = new byte[info.length];
			in.reset();
			in.skipBytes(info.location);
			in.readFully(info.data);
		}
		
		// Decompile.
		while (true) {
			int tablesDecompiled = 0;
			int tablesToDecompile = 0;
			for (TrueTypeTableInfo info : tableInfo) {
				if (info.table == null) {
					TrueTypeTable table = createTable(info.id);
					int[] depIds = table.dependencyIds();
					TrueTypeTable[] depTables = new TrueTypeTable[depIds.length];
					boolean depsComplete = true;
					for (int i = 0; i < depIds.length; i++) {
						if (tableInfoById.containsKey(depIds[i])) {
							depTables[i] = tableInfoById.get(depIds[i]).table;
							if (depTables[i] == null) {
								depsComplete = false;
							}
						} else {
							throw unresolvedDependency(table);
						}
					}
					if (depsComplete) {
						table.decompile(info.data, depTables);
						info.table = table;
						tablesDecompiled++;
					} else {
						tablesToDecompile++;
					}
				}
			}
			if (tablesToDecompile == 0) break;
			if (tablesDecompiled == 0) throw new IllegalStateException("Circular dependency detected.");
		}
		
		// Gather tables.
		this.clear();
		Collections.sort(tableInfo, SORT_BY_LOCATION);
		for (TrueTypeTableInfo info : tableInfo) {
			this.add(info.table);
		}
		
		in.close();
		bytein.close();
	}
	
	private static final class TrueTypeTableInfo {
		private int id;
		private int checksum;
		private int location;
		private int length;
		private byte[] data;
		private TrueTypeTable table;
	}
	
	private static final Comparator<TrueTypeTableInfo> SORT_BY_ID = new Comparator<TrueTypeTableInfo>() {
		@Override
		public int compare(TrueTypeTableInfo a, TrueTypeTableInfo b) {
			return a.id - b.id;
		}
	};
	
	private static final Comparator<TrueTypeTableInfo> SORT_BY_LOCATION = new Comparator<TrueTypeTableInfo>() {
		@Override
		public int compare(TrueTypeTableInfo a, TrueTypeTableInfo b) {
			return a.location - b.location;
		}
	};
	
	private static final int chksum(byte[] data) {
		int sum = 0;
		int nl = data.length & ~0x3;
		for (int i = 0; i < nl; i += 4) {
			sum += getInt(data, i);
		}
		for (int i = nl, s = 24; i < data.length; i++, s -= 8) {
			sum += ((data[i] & 0xFF) << s);
		}
		return sum;
	}
	
	private static final int getInt(byte[] data, int i) {
		return ((data[i+0] & 0xFF) << 24)
		     | ((data[i+1] & 0xFF) << 16)
		     | ((data[i+2] & 0xFF) <<  8)
		     | ((data[i+3] & 0xFF) <<  0);
	}
	
	private static final void putInt(byte[] data, int i, int v) {
		data[i+0] = (byte)((v >> 24) & 0xFF);
		data[i+1] = (byte)((v >> 16) & 0xFF);
		data[i+2] = (byte)((v >>  8) & 0xFF);
		data[i+3] = (byte)((v >>  0) & 0xFF);
	}
	
	private static final IllegalStateException unresolvedDependency(TrueTypeTable table) {
		StringBuffer message = new StringBuffer();
		message.append("Unresolved dependency: ");
		message.append(table.tableName());
		message.append(" depends on");
		for (String depName : table.dependencyNames()) {
			message.append(" ");
			message.append(depName);
		}
		message.append(".");
		return new IllegalStateException(message.toString());
	}
}
