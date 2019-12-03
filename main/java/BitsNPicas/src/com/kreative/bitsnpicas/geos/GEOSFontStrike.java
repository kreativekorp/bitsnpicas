package com.kreative.bitsnpicas.geos;

import java.io.ByteArrayOutputStream;

public class GEOSFontStrike {
	public int ascent;
	public int rowWidth;
	public int height;
	public int[] xCoord;
	public byte[] bitmap;
	public OffsetWidth[] offsetWidths;
	public UTF8StrikeMap utf8Map;
	
	public GEOSFontStrike() {
		this.clear(96, false, false);
	}
	
	public GEOSFontStrike(int numChars) {
		this.clear(numChars, false, false);
	}
	
	public GEOSFontStrike(boolean kerning, boolean utf8) {
		this.clear(96, kerning, utf8);
	}
	
	public GEOSFontStrike(int numChars, boolean kerning, boolean utf8) {
		this.clear(numChars, kerning, utf8);
	}
	
	public GEOSFontStrike(byte[] data) {
		this.read(data, 0, data.length);
	}
	
	public GEOSFontStrike(byte[] data, int offset, int length) {
		this.read(data, offset, length);
	}
	
	public void clear() {
		this.clear(96, false, false);
	}
	
	public void clear(int numChars) {
		this.clear(numChars, false, false);
	}
	
	public void clear(boolean kerning, boolean utf8) {
		this.clear(96, kerning, utf8);
	}
	
	public void clear(int numChars, boolean kerning, boolean utf8) {
		this.ascent = 0;
		this.rowWidth = 0;
		this.height = 0;
		this.xCoord = new int[numChars + 1];
		this.bitmap = new byte[0];
		this.offsetWidths = kerning ? new OffsetWidth[numChars] : null;
		this.utf8Map = utf8 ? new UTF8StrikeMap() : null;
	}
	
	public int getGlyphCount() {
		return xCoord.length - 1;
	}
	
	public byte[][] getGlyph(int i) {
		if (xCoord == null || xCoord.length == 0) return null;
		if (bitmap == null || bitmap.length == 0) return null;
		if (xCoord[i] > xCoord[i+1] || xCoord[i] > (rowWidth*8) || xCoord[i+1] > (rowWidth*8)) {
			System.err.println("Warning: Bad X coordinates: " + xCoord[i] + ", " + xCoord[i+1] + ", " + (rowWidth*8));
			return null;
		}
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
		if (xCoord == null || xCoord.length == 0) return;
		if (bitmap == null || bitmap.length == 0) return;
		if (xCoord[i] > xCoord[i+1] || xCoord[i] > (rowWidth*8) || xCoord[i+1] > (rowWidth*8)) {
			System.err.println("Warning: Bad X coordinates: " + xCoord[i] + ", " + xCoord[i+1] + ", " + (rowWidth*8));
			return;
		}
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
		read(data, 0, data.length);
	}
	
	public void read(byte[] data, int offset, int length) {
		// Read header.
		ascent    =  data[offset + 0] & 0xFF;
		rowWidth  = (data[offset + 1] & 0xFF) | ((data[offset + 2] & 0xFF) << 8);
		height    =  data[offset + 3] & 0xFF;
		int xo    = (data[offset + 4] & 0xFF) | ((data[offset + 5] & 0xFF) << 8);
		int bo    = (data[offset + 6] & 0xFF) | ((data[offset + 7] & 0xFF) << 8);
		int flags = (data[offset + 8] & 0xFF) | ((data[offset + 9] & 0xFF) << 8);
		boolean extended = ((flags & 0x8000) != 0);
		boolean kerning  = extended && ((flags & 0x2000) != 0);
		boolean utf8     = extended && ((flags & 0x1000) != 0);
		int ko = kerning ? ((data[offset + 10] & 0xFF) | ((data[offset + 11] & 0xFF) << 8)) : length;
		int uo = utf8    ? ((data[offset + 12] & 0xFF) | ((data[offset + 13] & 0xFF) << 8)) : length;
		
		// Calculate lengths.
		int xl = calculateLength(xo, length, bo, ko, uo);
		int bl = calculateLength(bo, length, xo, ko, uo);
		int kl = calculateLength(ko, length, xo, bo, uo);
		
		// Read x coordinate table.
		xCoord = new int[xl / 2];
		for (int a = offset + xo, i = 0; i < xCoord.length; i++, a += 2) {
			xCoord[i] = (data[a] & 0xFF) | ((data[a+1] & 0xFF) << 8);
		}
		
		// Read bitmap.
		bitmap = new byte[bl];
		for (int a = offset + bo, i = 0; i < bitmap.length; i++, a++) {
			bitmap[i] = data[a];
		}
		
		// Read kerning table.
		if (kerning) {
			offsetWidths = new OffsetWidth[kl / 2];
			for (int a = offset + ko, i = 0; i < offsetWidths.length; i++, a += 2) {
				offsetWidths[i] = new OffsetWidth(data[a], (data[a+1] & 0xFF));
			}
		} else {
			offsetWidths = null;
		}
		
		// Read UTF-8 table.
		if (utf8) {
			utf8Map = new UTF8StrikeMap();
			utf8Map.read(data, offset + uo);
		} else {
			utf8Map = null;
		}
	}
	
	public byte[] write() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		boolean extended = (offsetWidths != null || utf8Map != null);
		
		// Calculate offsets.
		int offset = extended ? 14 : 8;
		int xo = offset; offset += xCoord.length * 2;
		int ko = offset; if (offsetWidths != null) offset += offsetWidths.length * 2;
		int bo = offset; offset += bitmap.length;
		int uo = offset;
		
		// Write header.
		out.write(ascent);
		out.write(rowWidth);
		out.write(rowWidth >> 8);
		out.write(height);
		out.write(xo);
		out.write(xo >> 8);
		out.write(bo);
		out.write(bo >> 8);
		if (extended) {
			int flags = 0x8000;
			if (xCoord.length <    96) flags |= 0x4000;
			if (offsetWidths  != null) flags |= 0x2000;
			if (utf8Map       != null) flags |= 0x1000;
			out.write(flags);
			out.write(flags >> 8);
			out.write(ko);
			out.write(ko >> 8);
			out.write(uo);
			out.write(uo >> 8);
		}
		
		// Write x coordinate table.
		for (int i = 0; i < xCoord.length; i++) {
			out.write(xCoord[i]);
			out.write(xCoord[i] >> 8);
		}
		
		// Write kerning table.
		if (offsetWidths != null) {
			for (int i = 0; i < offsetWidths.length; i++) {
				if (offsetWidths[i] != null) {
					out.write(offsetWidths[i].offset);
					out.write(offsetWidths[i].width);
				} else {
					out.write(0);
					out.write(0);
				}
			}
		}
		
		// Write bitmap.
		for (int i = 0; i < bitmap.length; i++) {
			out.write(bitmap[i]);
		}
		
		// Write UTF-8 table.
		if (utf8Map != null) {
			byte[] utf8Data = utf8Map.write(uo);
			for (int i = 0; i < utf8Data.length; i++) {
				out.write(utf8Data[i]);
			}
		}
		
		return out.toByteArray();
	}
	
	private static int calculateLength(int thisStart, int finalEnd, int... otherStart) {
		int thisEnd = finalEnd;
		for (int otherEnd : otherStart) {
			if (otherEnd >= thisStart && otherEnd < thisEnd) {
				thisEnd = otherEnd;
			}
		}
		return thisEnd - thisStart;
	}
	
	public static final class OffsetWidth {
		public final int offset;
		public final int width;
		public OffsetWidth(int offset, int width) {
			this.offset = offset;
			this.width = width;
		}
	}
}
