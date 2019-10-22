package com.kreative.bitsnpicas.u8m;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class U8MGlyph {
	public int bitmapLocation;
	public int advanceWidth;
	public int yOffset;
	public int xOffset;
	public int height;
	public int width;
	public byte[] data;
	
	public void from2DArray(byte[][] a) {
		if (a == null || a.length == 0) {
			height = 0;
			width = 0;
			data = new byte[0];
		} else {
			height = a.length;
			width = a[0].length;
			data = new byte[(height * width + 7) / 8];
			int i = 0, m = 0x80;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (a[y][x] < 0) data[i] |= m;
					if ((m >>= 1) == 0) { m = 0x80; i++; }
				}
			}
		}
	}
	
	public byte[][] to2DArray() {
		byte[][] a = new byte[height][width];
		if (data != null) {
			int i = 0, m = 0x80;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					a[y][x] = ((data[i] & m) == 0) ? (byte)(0) : (byte)(-1);
					if ((m >>= 1) == 0) { m = 0x80; i++; }
				}
			}
		}
		return a;
	}
	
	public int setBitmapLocation(int loc) {
		if (data == null || data.length == 0) {
			bitmapLocation = 0;
			return loc;
		} else {
			int len = data.length + 4;
			if ((loc & 0xFF) + len > 0x100) loc = (loc | 0xFF) + 1;
			return ((bitmapLocation = loc)) + len;
		}
	}
	
	public void readGlyphRecord(DataInput in) throws IOException {
		int v = Integer.reverseBytes(in.readInt());
		bitmapLocation = v & 0xFFFFFF;
		advanceWidth = (v >> 24) & 0xFF;
	}
	
	public void writeGlyphRecord(DataOutput out) throws IOException {
		int v = (bitmapLocation & 0xFFFFFF) | (advanceWidth << 24);
		out.writeInt(Integer.reverseBytes(v));
	}
	
	public void readBitmapRecord(DataInput in) throws IOException {
		yOffset = in.readByte();
		xOffset = in.readByte();
		height = in.readUnsignedByte();
		width = in.readUnsignedByte();
		data = new byte[(height * width + 7) / 8];
		in.readFully(data);
	}
	
	public void writeBitmapRecord(DataOutput out) throws IOException {
		out.writeByte(yOffset);
		out.writeByte(xOffset);
		out.writeByte(height);
		out.writeByte(width);
		if (data != null) out.write(data);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof U8MGlyph) {
			U8MGlyph that = (U8MGlyph)o;
			return (
				this.advanceWidth == that.advanceWidth &&
				this.yOffset == that.yOffset &&
				this.xOffset == that.xOffset &&
				this.height == that.height &&
				this.width == that.width &&
				equals(this.data, that.data)
			);
		}
		return false;
	}
	
	private static boolean equals(byte[] a, byte[] b) {
		if (a == null || a.length == 0) return (b == null || b.length == 0);
		if (b == null || b.length == 0) return (a == null || a.length == 0);
		return Arrays.equals(a, b);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(new int[] {
			advanceWidth, yOffset, xOffset, height, width,
			((data == null || data.length == 0) ? 0 : Arrays.hashCode(data))
		});
	}
}
