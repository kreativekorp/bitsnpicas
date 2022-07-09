package com.kreative.unicode.ttflib;

import java.awt.font.OpenType;
import java.lang.reflect.Method;

public class RefOpenType implements OpenType {
	private final Method getTableBytes;
	private final Object instance;
	
	public RefOpenType(Method getTableBytes, Object instance) {
		this.getTableBytes = getTableBytes;
		this.instance = instance;
	}
	
	@Override
	public byte[] getFontTable(int tag) {
		try { return (byte[])getTableBytes.invoke(instance, tag); }
		catch (Exception e) { return null; }
	}
	
	@Override
	public byte[] getFontTable(String tag) {
		return getFontTable(tagInt(tag));
	}
	
	@Override
	public byte[] getFontTable(int tag, int offset, int length) {
		byte[] data = getFontTable(tag);
		return (data != null) ? copy(data, offset, length) : null;
	}
	
	@Override
	public byte[] getFontTable(String tag, int offset, int length) {
		return getFontTable(tagInt(tag), offset, length);
	}
	
	@Override
	public int getFontTableSize(int tag) {
		byte[] data = getFontTable(tag);
		return (data != null) ? data.length : 0;
	}
	
	@Override
	public int getFontTableSize(String tag) {
		return getFontTableSize(tagInt(tag));
	}
	
	@Override
	public int getVersion() {
		return 0;
	}
	
	private static int tagInt(String s) {
		char[] ch = s.toCharArray();
		int tag = 0;
		tag |= (int)((ch.length > 0) ? ((ch[0] < 0x80) ? ch[0] : '?') : ' ') << 24;
		tag |= (int)((ch.length > 1) ? ((ch[1] < 0x80) ? ch[1] : '?') : ' ') << 16;
		tag |= (int)((ch.length > 2) ? ((ch[2] < 0x80) ? ch[2] : '?') : ' ') <<  8;
		tag |= (int)((ch.length > 3) ? ((ch[3] < 0x80) ? ch[3] : '?') : ' ') <<  0;
		return tag;
	}
	
	private static byte[] copy(byte[] src, int offset, int length) {
		byte[] dst = new byte[length];
		for (int i = 0; i < length; i++) dst[i] = src[offset++];
		return dst;
	}
}
