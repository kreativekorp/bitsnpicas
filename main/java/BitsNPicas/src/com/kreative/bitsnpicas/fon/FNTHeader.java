package com.kreative.bitsnpicas.fon;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

// What a FON file needs to know of a font in it, which is a whole FNT file.
public class FNTHeader {
	public static final int MIN_SIZE = 118;
	// How much of a font its entry in the font directory repeats.
	public static final int DIRECTORY_SIZE = 113;
	
	public final int version;
	public final int type;
	public final int points;
	public final int vertRes;
	public final int horizRes;
	public final int italic;
	public final int weight;
	public final int charSet;
	public final int pixWidth;
	public final int pixHeight;
	public final int avgWidth;
	public final int firstChar;
	public final int lastChar;
	public final byte[] copyright;
	public final byte[] deviceName;
	public final byte[] faceName;
	// The width that all characters have, or zero when they differ.
	public final int fixedWidth;
	
	public FNTHeader(byte[] d) throws IOException {
		if (d.length < MIN_SIZE) throw new IOException("too short for an FNT font");
		version = u16(d, 0);
		if (version < 0x100 || version > 0x300) throw new IOException("not an FNT font");
		type = u16(d, 66);
		points = u16(d, 68);
		vertRes = u16(d, 70);
		horizRes = u16(d, 72);
		italic = d[80] & 0xFF;
		weight = u16(d, 83);
		charSet = d[85] & 0xFF;
		pixWidth = u16(d, 86);
		pixHeight = u16(d, 88);
		avgWidth = u16(d, 91);
		firstChar = d[95] & 0xFF;
		lastChar = d[96] & 0xFF;
		copyright = string(d, 6, 66);
		deviceName = string(d, u32(d, 101), d.length);
		faceName = string(d, u32(d, 105), d.length);
		
		int width = 0;
		if (!isVector()) {
			int table = (version >= 0x300) ? 148 : 118;
			int entry = (version >= 0x300) ? 6 : 4;
			for (int ch = firstChar; ch <= lastChar; ch++, table += entry) {
				if (table + 2 > d.length) break;
				int w = u16(d, table);
				if (w == 0) continue;
				if (width == 0) width = w;
				if (width != w) { width = -1; break; }
			}
		}
		fixedWidth = (width > 0) ? width : 0;
	}
	
	public boolean isVector() {
		return (type & 1) != 0;
	}
	
	public boolean isBold() {
		return weight > 500;
	}
	
	public boolean isItalic() {
		return italic != 0;
	}
	
	public String getFaceName() {
		return decode(faceName);
	}
	
	public String getCopyright() {
		return decode(copyright);
	}
	
	private static String decode(byte[] b) {
		try {
			return new String(b, "CP1252");
		} catch (UnsupportedEncodingException e) {
			return new String(b);
		}
	}
	
	private static byte[] string(byte[] d, int start, int limit) {
		if (start <= 0 || start >= limit) return new byte[0];
		int end = start;
		while (end < limit && d[end] != 0) end++;
		byte[] s = new byte[end - start];
		System.arraycopy(d, start, s, 0, s.length);
		return s;
	}
	
	static int u16(byte[] d, int o) {
		return (d[o] & 0xFF) | ((d[o + 1] & 0xFF) << 8);
	}
	
	static int u32(byte[] d, int o) {
		return u16(d, o) | (u16(d, o + 2) << 16);
	}
}
