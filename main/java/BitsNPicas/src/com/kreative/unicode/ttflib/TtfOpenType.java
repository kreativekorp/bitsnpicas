package com.kreative.unicode.ttflib;

import java.awt.font.OpenType;

public class TtfOpenType implements OpenType {
	private final TtfBase ttf;
	
	public TtfOpenType(TtfBase ttf) {
		this.ttf = ttf;
	}
	
	@Override
	public byte[] getFontTable(int tag) {
		TtfTable t = ttf.getTable(tag);
		return (t != null) ? t.getData() : null;
	}
	
	@Override
	public byte[] getFontTable(String tag) {
		TtfTable t = ttf.getTable(tag);
		return (t != null) ? t.getData() : null;
	}
	
	@Override
	public byte[] getFontTable(int tag, int offset, int length) {
		TtfTable t = ttf.getTable(tag);
		return (t != null) ? copy(t.getData(), offset, length) : null;
	}
	
	@Override
	public byte[] getFontTable(String tag, int offset, int length) {
		TtfTable t = ttf.getTable(tag);
		return (t != null) ? copy(t.getData(), offset, length) : null;
	}
	
	@Override
	public int getFontTableSize(int tag) {
		TtfTable t = ttf.getTable(tag);
		return (t != null) ? t.getData().length : 0;
	}
	
	@Override
	public int getFontTableSize(String tag) {
		TtfTable t = ttf.getTable(tag);
		return (t != null) ? t.getData().length : 0;
	}
	
	@Override
	public int getVersion() {
		return ttf.getScaler();
	}
	
	private static byte[] copy(byte[] src, int offset, int length) {
		byte[] dst = new byte[length];
		for (int i = 0; i < length; i++) dst[i] = src[offset++];
		return dst;
	}
}
