package com.kreative.bitsnpicas.geos;

import java.io.ByteArrayOutputStream;

public class GEOSFontStrike {
	public final int numChars;
	public int ascent;
	public int rowWidth;
	public int height;
	public int[] xCoord;
	public byte[] bitmap;
	
	public GEOSFontStrike() {
		this.numChars = 96;
		this.clear();
	}
	
	public GEOSFontStrike(byte[] data) {
		this.numChars = 96;
		this.read(data);
	}
	
	public void clear() {
		ascent = 0;
		rowWidth = 0;
		height = 0;
		xCoord = new int[numChars + 1];
		bitmap = new byte[0];
	}
	
	public byte[][] getGlyph(int i) {
		byte[][] gd = new byte[height][xCoord[i+1] - xCoord[i]];
		for (int ba = 0, y = 0; y < height; y++, ba += rowWidth) {
			int a = ba + (xCoord[i] >> 3);
			int m = 0x80 >> (xCoord[i] & 7);
			for (int gx = 0, bx = xCoord[i]; bx < xCoord[i+1]; bx++, gx++) {
				if ((bitmap[a] & m) != 0) gd[y][gx] = -1;
				m >>= 1;
				if (m == 0) {
					m = 0x80;
					a++;
				}
			}
		}
		return gd;
	}
	
	public void setGlyph(int i, byte[][] gd) {
		for (int ba = 0, y = 0; y < height; y++, ba += rowWidth) {
			int a = ba + (xCoord[i] >> 3);
			int m = 0x80 >> (xCoord[i] & 7);
			for (int gx = 0, bx = xCoord[i]; bx < xCoord[i+1]; bx++, gx++) {
				if (gd[y][gx] < 0) bitmap[a] |= m;
				m >>= 1;
				if (m == 0) {
					m = 0x80;
					a++;
				}
			}
		}
	}
	
	public void read(byte[] data) {
		ascent = data[0] & 0xFF;
		rowWidth = (data[1] & 0xFF) | ((data[2] & 0xFF) << 8);
		height = data[3] & 0xFF;
		int xo = (data[4] & 0xFF) | ((data[5] & 0xFF) << 8);
		int bo = (data[6] & 0xFF) | ((data[7] & 0xFF) << 8);
		xCoord = new int[numChars + 1];
		for (int a = xo, i = 0; i < xCoord.length; i++, a += 2) {
			xCoord[i] = (data[a] & 0xFF) | ((data[a+1] & 0xFF) << 8);
		}
		bitmap = new byte[rowWidth * height];
		for (int a = bo, i = 0; i < bitmap.length; i++, a++) {
			bitmap[i] = data[a];
		}
	}
	
	public byte[] write() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(ascent);
		out.write(rowWidth);
		out.write(rowWidth >> 8);
		out.write(height);
		out.write(0x08);
		out.write(0);
		out.write(0x08 + xCoord.length * 2);
		out.write(0);
		for (int i = 0; i < xCoord.length; i++) {
			out.write(xCoord[i]);
			out.write(xCoord[i] >> 8);
		}
		for (int i = 0; i < bitmap.length; i++) {
			out.write(bitmap[i]);
		}
		return out.toByteArray();
	}
}
