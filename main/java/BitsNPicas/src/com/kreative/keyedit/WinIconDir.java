package com.kreative.keyedit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class WinIconDir extends ArrayList<WinIconDirEntry> {
	private static final long serialVersionUID = 1L;
	
	private boolean isCursor;
	
	public WinIconDir() {
		this.isCursor = false;
	}
	
	public WinIconDir(boolean isCursor) {
		this.isCursor = isCursor;
	}
	
	public void read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1 << 20]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.flush(); out.close(); read(out.toByteArray());
	}
	
	public void read(byte[] data) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);
		
		int reserved = Short.reverseBytes(din.readShort());
		int type = Short.reverseBytes(din.readShort());
		int count = Short.reverseBytes(din.readShort());
		if (reserved != 0 || type < 1 || type > 2 || count < 1) {
			throw new IOException("invalid value in ico header");
		}
		
		WinIconDirEntry[] entries = new WinIconDirEntry[count];
		for (int i = 0; i < count; i++) {
			entries[i] = new WinIconDirEntry(type > 1);
			entries[i].readHeader(din);
		}
		for (int i = 0; i < count; i++) {
			entries[i].readData(din);
		}
		
		clear(type > 1);
		addAll(Arrays.asList(entries));
	}
	
	public void write(OutputStream out) throws IOException {
		write(new DataOutputStream(out));
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeShort(Short.reverseBytes((short)0));
		out.writeShort(Short.reverseBytes((short)(isCursor ? 2 : 1)));
		out.writeShort(Short.reverseBytes((short)size()));
		int offset = size() * 16 + 6;
		for (WinIconDirEntry e : this) {
			e.setDataOffset(offset);
			e.writeHeader(out);
			offset += e.getDataLength();
		}
		for (WinIconDirEntry e : this) {
			out.write(e.getData());
		}
	}
	
	@Override
	public boolean add(WinIconDirEntry e) {
		if (e.isCursor() != isCursor) {
			throw new IllegalArgumentException("cannot put a cursor in an icon file or vice versa");
		}
		return super.add(e);
	}
	
	@Override
	public void add(int index, WinIconDirEntry e) {
		if (e.isCursor() != isCursor) {
			throw new IllegalArgumentException("cannot put a cursor in an icon file or vice versa");
		}
		super.add(index, e);
	}
	
	@Override
	public boolean addAll(Collection<? extends WinIconDirEntry> c) {
		for (WinIconDirEntry e : c) {
			if (e.isCursor() != isCursor) {
				throw new IllegalArgumentException("cannot put a cursor in an icon file or vice versa");
			}
		}
		return super.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends WinIconDirEntry> c) {
		for (WinIconDirEntry e : c) {
			if (e.isCursor() != isCursor) {
				throw new IllegalArgumentException("cannot put a cursor in an icon file or vice versa");
			}
		}
		return super.addAll(index, c);
	}
	
	@Override
	public WinIconDirEntry set(int index, WinIconDirEntry e) {
		if (e.isCursor() != isCursor) {
			throw new IllegalArgumentException("cannot put a cursor in an icon file or vice versa");
		}
		return super.set(index, e);
	}
	
	public void clear(boolean isCursor) {
		this.isCursor = isCursor;
		super.clear();
	}
	
	public boolean isCursor() {
		return isCursor;
	}
	
	public WinIconDirEntry get(Integer wantWidth, Integer wantHeight, Integer wantBpp, Boolean wantPNG) {
		WinIconDirEntry best = null;
		int bestWidth = 0;
		int bestHeight = 0;
		int bestBpp = 0;
		boolean bestIsPNG = false;
		for (WinIconDirEntry e : this) {
			int eWidth = e.getWidth();
			int eHeight = e.getHeight();
			int eBpp = e.getBitsPerPixel();
			boolean eIsPNG = e.isPNG();
			if (
				(wantWidth == null || wantWidth.intValue() == eWidth) && (eWidth >= bestWidth) &&
				(wantHeight == null || wantHeight.intValue() == eHeight) && (eHeight >= bestHeight) &&
				(wantBpp == null || wantBpp.intValue() == eBpp) && (eBpp >= bestBpp) &&
				(wantPNG == null || wantPNG.booleanValue() == eIsPNG) && (eIsPNG || !bestIsPNG)
			) {
				best = e;
				bestWidth = eWidth;
				bestHeight = eHeight;
				bestBpp = eBpp;
				bestIsPNG = eIsPNG;
			}
		}
		return best;
	}
	
	public List<WinIconDirEntry> getAll(Integer wantWidth, Integer wantHeight, Integer wantBpp, Boolean wantPNG) {
		List<WinIconDirEntry> matches = new ArrayList<WinIconDirEntry>();
		for (WinIconDirEntry e : this) {
			int eWidth = e.getWidth();
			int eHeight = e.getHeight();
			int eBpp = e.getBitsPerPixel();
			boolean eIsPNG = e.isPNG();
			if (
				(wantWidth == null || wantWidth.intValue() == eWidth) &&
				(wantHeight == null || wantHeight.intValue() == eHeight) &&
				(wantBpp == null || wantBpp.intValue() == eBpp) &&
				(wantPNG == null || wantPNG.booleanValue() == eIsPNG)
			) {
				matches.add(e);
			}
		}
		return matches;
	}
}
