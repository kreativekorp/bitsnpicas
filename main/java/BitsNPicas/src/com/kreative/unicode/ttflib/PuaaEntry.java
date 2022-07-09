package com.kreative.unicode.ttflib;

import java.io.DataInputStream;
import java.io.IOException;

public class PuaaEntry {
	private int entryType;
	private int firstCodePoint;
	private int lastCodePoint;
	private int entryData;
	private Object value;
	
	PuaaEntry() {}
	
	void readHead(DataInputStream in) throws IOException {
		this.entryType = in.readUnsignedByte();
		int plane = in.readUnsignedByte() << 16;
		this.firstCodePoint = in.readUnsignedShort() | plane;
		this.lastCodePoint = in.readUnsignedShort() | plane;
		this.entryData = in.readInt();
	}
	
	void readBody(DataInputStream in, int to) throws IOException {
		switch (this.entryType) {
			case 1:
				this.value = readString(in, to, this.entryData);
				break;
			case 2:
				int[] sd = readIntArray(in, to, this.entryData);
				int sn = sd.length; String[] sv = new String[sn];
				for (int i = 0; i < sn; i++) sv[i] = readString(in, to, sd[i]);
				this.value = sv;
				break;
			case 3:
				this.value = (this.entryData != 0);
				break;
			case 4: case 5:
				this.value = this.entryData;
				break;
			case 6: case 7:
				this.value = readIntArray(in, to, this.entryData);
				break;
			case 8:
				int[] cd = readIntArray(in, to, this.entryData);
				int cn = cd.length - 1; int[] cs = new int[cn];
				for (int i = 0; i < cn; i++) cs[i] = cd[i];
				String cc = readString(in, to, cd[cn]);
				this.value = new CaseMapping(cs, cc);
				break;
			case 9:
				int[] nd = readIntArray(in, to, this.entryData);
				String n0 = readString(in, to, nd[0]);
				String n1 = readString(in, to, nd[1]);
				this.value = new NameAlias(n0, n1);
				break;
			default:
				this.value = null;
				break;
		}
	}
	
	void setData(int cp, String value) {
		this.entryType = 1;
		this.firstCodePoint = cp;
		this.lastCodePoint = cp;
		this.entryData = 0;
		this.value = value;
	}
	
	boolean extendData(int cp, String value) {
		if ((this.lastCodePoint + 1) != cp) return false;
		if (this.value == null && value != null) return false;
		if (this.value != null && !this.value.equals(value)) return false;
		this.lastCodePoint++;
		return true;
	}
	
	public int getFirstCodePoint() { return firstCodePoint; }
	public int getLastCodePoint() { return lastCodePoint; }
	
	public boolean contains(int cp) {
		return cp >= firstCodePoint && cp <= lastCodePoint;
	}
	
	public Object getPropertyValue(int cp) {
		if (value == null) return null;
		switch (entryType) {
			case 2: return ((String[])value)[cp - firstCodePoint];
			case 6: return ((int[])value)[cp - firstCodePoint];
			default: return value;
		}
	}
	
	public String getPropertyString(int cp) {
		if (value == null) return null;
		switch (entryType) {
			case 2: return ((String[])value)[cp - firstCodePoint];
			case 3: return ((Boolean)value) ? "Y" : "N";
			case 5: return toHexString((Integer)value);
			case 6: return toHexString(((int[])value)[cp - firstCodePoint]);
			case 7: return toHexString((int[])value);
			default: return value.toString();
		}
	}
	
	static String readString(DataInputStream in, int to, int ed) throws IOException {
		if (ed > 0) {
			in.reset();
			in.skipBytes(to + ed);
			int l = in.readUnsignedByte();
			byte[] d = new byte[l];
			in.readFully(d);
			return new String(d, "UTF-8");
		}
		if (ed < 0) {
			StringBuffer sb = new StringBuffer();
			char ch0 = (char)((ed >> 24) & 0x7F); if (ch0 != 0) sb.append(ch0);
			char ch1 = (char)((ed >> 16) & 0x7F); if (ch1 != 0) sb.append(ch1);
			char ch2 = (char)((ed >>  8) & 0x7F); if (ch2 != 0) sb.append(ch2);
			char ch3 = (char)((ed >>  0) & 0x7F); if (ch3 != 0) sb.append(ch3);
			return sb.toString();
		}
		return null;
	}
	
	static int[] readIntArray(DataInputStream in, int to, int ed) throws IOException {
		if (ed > 0) {
			in.reset();
			in.skipBytes(to + ed);
			int l = in.readUnsignedShort();
			int[] d = new int[l];
			for (int i = 0; i < l; i++) d[i] = in.readInt();
			return d;
		}
		return null;
	}
	
	static String toHexString(int v) {
		String s = Integer.toHexString(v).toUpperCase();
		if (s.length() < 4) s = ("0000" + s).substring(s.length());
		return s;
	}
	
	static String toHexString(int[] v) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.length; i++) {
			if (i > 0) sb.append(" ");
			sb.append(toHexString(v[i]));
		}
		return sb.toString();
	}
	
	public static final class CaseMapping {
		public final int[] codePoints;
		public final String condition;
		public CaseMapping(int[] codePoints, String condition) {
			this.codePoints = codePoints;
			this.condition = condition;
		}
		@Override
		public String toString() {
			String s = toHexString(codePoints);
			if (condition == null) return s;
			return s + "; " + condition;
		}
	}
	
	public static final class NameAlias {
		public final String alias;
		public final String type;
		public NameAlias(String alias, String type) {
			this.alias = alias;
			this.type = type;
		}
		@Override
		public String toString() {
			return alias + ";" + type;
		}
	}
}
