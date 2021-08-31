package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class FNTBitmapFontExporter implements BitmapFontExporter {
	private int magic;
	private EncodingTable encoding;
	
	public FNTBitmapFontExporter() {
		this.magic = 3;
		this.encoding = null;
	}
	
	public FNTBitmapFontExporter(EncodingTable encoding) {
		this.magic = 3;
		this.encoding = encoding;
	}
	
	public FNTBitmapFontExporter(int magic) {
		if (magic < 1 || magic > 3) throw new IllegalArgumentException("bad magic number");
		this.magic = magic;
		this.encoding = null;
	}
	
	public FNTBitmapFontExporter(int magic, EncodingTable encoding) {
		if (magic < 1 || magic > 3) throw new IllegalArgumentException("bad magic number");
		this.magic = magic;
		this.encoding = encoding;
	}
	
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		exportFontImpl(font, new DataOutputStream(out));
		out.close();
		return out.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		exportFontImpl(font, new DataOutputStream(os));
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		exportFontImpl(font, new DataOutputStream(out));
		out.close();
	}
	
	private void exportFontImpl(BitmapFont font, DataOutputStream out) throws IOException {
		// Vertical metrics.
		int ascent = font.getLineAscent();
		int height = ascent + font.getLineDescent();
		int points = font.getEmAscent() + font.getEmDescent();
		int leading = font.getLineGap();
		
		// Font style.
		String styleName = font.getName(BitmapFont.NAME_STYLE);
		styleName = (styleName != null) ? styleName.toLowerCase() : "";
		int italic = (styleName.contains("italic") || styleName.contains("oblique")) ? 1 : 0;
		int underline = styleName.contains("underline") ? 1 : 0;
		int strikeOut = styleName.contains("strike") ? 1 : 0;
		int weight = (
			styleName.contains("thin") ? 100 : styleName.contains("light") ? 300 :
			styleName.contains("bold") ? 700 : styleName.contains("black") ? 900 :
			400
		);
		
		// Character set.
		String encName = (encoding != null) ? encoding.name.toUpperCase() : "CP1252";
		int charSet = (
			encName.equals("CP1252") ? 0 : encName.equals("MACROMAN") ? 77 :
			encName.equals("CP932") ? 128 : encName.equals("CP949") ? 129 :
			encName.equals("CP1361") ? 130 : encName.equals("CP936") ? 134 :
			encName.equals("CP950") ? 136 : encName.equals("CP1253") ? 161 :
			encName.equals("CP1254") ? 162 : encName.equals("CP1258") ? 163 :
			encName.equals("CP1255") ? 177 : encName.equals("CP1256") ? 178 :
			encName.equals("CP1257") ? 186 : encName.equals("CP1251") ? 204 :
			encName.equals("CP874") ? 222 : encName.equals("CP1250") ? 238 :
			encName.equals("CP437") ? 255 : 2
		);
		
		// Horizontal metrics and bitmaps.
		int maxWidth = 0;
		int avgWidth = 0;
		int numChars = 0;
		int firstChar = -1;
		int lastChar = -1;
		int defaultChar = -1;
		int breakChar = -1;
		int widthBytes = 0;
		HashMap<Integer,Integer> widths = new HashMap<Integer,Integer>();
		HashMap<Integer,byte[]> bitmaps = new HashMap<Integer,byte[]>();
		
		for (int idx = 0; idx < 256; idx++) {
			int ch = (encoding != null) ? encoding.get(idx) : fromCP1252(idx);
			if (ch < 0) ch = 0xF000 + idx;
			BitmapFontGlyph g = font.getCharacter(ch);
			if (g == null) continue;
			int width = g.getCharacterWidth();
			if (width > maxWidth) maxWidth = width;
			avgWidth += width;
			numChars++;
			if (ch == 32) breakChar = idx;
			if (firstChar < 0) firstChar = idx;
			lastChar = idx;
			int rowBytes = (width + 7) / 8;
			widthBytes += rowBytes;
			byte[] data = new byte[rowBytes * height];
			byte[][] glyphPixels = g.getGlyph();
			for (int dy = 0, j = g.getGlyphAscent() - ascent, y = 0; y < height; y++, j++, dy++) {
				if (j >= 0 && j < glyphPixels.length) {
					for (int dx = dy, i = -g.getGlyphOffset(), x = 0; x < width; dx += height) {
						for (int m = 0x80; m != 0 && x < width; m >>= 1, x++, i++) {
							if (i >= 0 && i < glyphPixels[j].length) {
								if (glyphPixels[j][i] < 0) data[dx] |= m;
							}
						}
					}
				}
			}
			widths.put(idx, width);
			bitmaps.put(idx, data);
		}
		
		// Create notdef bitmap.
		avgWidth /= numChars;
		if (avgWidth < 1) avgWidth = 1;
		int rowBytes = (avgWidth + 7) / 8;
		byte[] data = new byte[rowBytes * height];
		for (int dy = 0, y = 0; y < height; y++, dy++) {
			if (y > 0 && y < height-1) {
				for (int dx = dy, x = 0; x < avgWidth; dx += height) {
					for (int m = 0x80; m != 0 && x < avgWidth; m >>= 1, x++) {
						if (x > 0) {
							data[dx] |= m;
						}
					}
				}
			}
		}
		
		// Add notdef bitmaps.
		boolean isMono = true;
		for (int idx = firstChar; idx <= lastChar; idx++) {
			if (widths.containsKey(idx)) {
				if (widths.get(idx) != avgWidth) {
					isMono = false;
				}
			} else {
				if (defaultChar < 0) defaultChar = idx;
				widthBytes += rowBytes;
				numChars++;
				widths.put(idx, avgWidth);
				bitmaps.put(idx, data);
			}
		}
		
		// Add absolute space bitmap.
		if (breakChar < 0) breakChar = lastChar + 1;
		if (defaultChar < 0) defaultChar = lastChar + 1;
		widthBytes += rowBytes;
		numChars++;
		widths.put(lastChar + 1, avgWidth);
		bitmaps.put(lastChar + 1, new byte[rowBytes * height]);
		assert((lastChar - firstChar + 2) == numChars);
		assert(widths.size() == numChars);
		assert(bitmaps.size() == numChars);
		
		// Sizes and offsets and stuff.
		int charTableSize = numChars * ((magic >= 3) ? 6 : 4);
		int bitsOffset = charTableSize + ((magic >= 3) ? 148 : 118);
		int face = bitsOffset + (widthBytes * height);
		String copyright = font.getName(BitmapFont.NAME_COPYRIGHT);
		byte[] copyrightBytes = (copyright != null) ? copyright.getBytes("CP1252") : new byte[0];
		String faceName = font.getName(BitmapFont.NAME_FAMILY);
		byte[] faceBytes = (faceName != null) ? faceName.getBytes("CP1252") : new byte[0];
		int size = face + faceBytes.length + 1;
		
		// Write it out.
		out.writeShort(magic);
		out.writeInt(Integer.reverseBytes(size));
		out.write(copyrightBytes, 0, Math.min(copyrightBytes.length, 60));
		for (int i = copyrightBytes.length; i < 60; i++) out.write(0);
		out.writeShort(0); // type
		out.writeShort(Short.reverseBytes((short)points));
		out.writeShort(Short.reverseBytes((short)96)); // vertRes
		out.writeShort(Short.reverseBytes((short)96)); // horizRes
		out.writeShort(Short.reverseBytes((short)ascent));
		out.writeShort(0); // internalLeading
		out.writeShort(Short.reverseBytes((short)leading)); // externalLeading
		out.writeByte(italic);
		out.writeByte(underline);
		out.writeByte(strikeOut);
		out.writeShort(Short.reverseBytes((short)weight));
		out.writeByte(charSet);
		out.writeShort(Short.reverseBytes((short)(isMono ? avgWidth : 0))); // pixWidth
		out.writeShort(Short.reverseBytes((short)height)); // pixHeight
		out.writeByte(isMono ? 0 : 1); // pitchAndFamily
		out.writeShort(Short.reverseBytes((short)avgWidth));
		out.writeShort(Short.reverseBytes((short)maxWidth));
		out.writeByte(firstChar);
		out.writeByte(lastChar);
		out.writeByte(defaultChar - firstChar);
		out.writeByte(breakChar - firstChar);
		out.writeShort(Short.reverseBytes((short)widthBytes));
		out.writeInt(0); // device
		out.writeInt(Integer.reverseBytes(face));
		out.writeInt(0); // bitsPointer
		out.writeInt(Integer.reverseBytes(bitsOffset));
		out.writeByte(0); // reserved
		if (magic >= 3) {
			out.writeInt(Integer.reverseBytes(isMono ? 0x11 : 0x12)); // flags
			out.writeShort(0); // aSpace
			out.writeShort(0); // bSpace
			out.writeShort(0); // cSpace
			out.writeInt(0); // colorPointer
			out.write(new byte[16]); // reserved1
		}
		// charTable
		for (int idx = firstChar; idx <= lastChar + 1; idx++) {
			out.writeShort(Short.reverseBytes(widths.get(idx).shortValue()));
			if (magic >= 3) out.writeInt(Integer.reverseBytes(bitsOffset));
			else out.writeShort(Short.reverseBytes((short)bitsOffset));
			bitsOffset += bitmaps.get(idx).length;
		}
		assert(bitsOffset == face);
		// bitmaps
		for (int idx = firstChar; idx <= lastChar + 1; idx++) {
			out.write(bitmaps.get(idx));
		}
		// faceName
		out.write(faceBytes);
		out.write(0);
	}
	
	private static int fromCP1252(int ch) {
		if (ch < 0x80 || ch >= 0xA0) return ch;
		return CP1252_C1[ch - 0x80];
	}
	
	private static final int[] CP1252_C1 = new int[] {
		0x20AC, 0x25CA, 0x201A, 0x0192, 0x201E, 0x2026, 0x2020, 0x2021,
		0x02C6, 0x2030, 0x0160, 0x2039, 0x0152, 0x0141, 0x017D, 0x0131,
		0x2318, 0x2018, 0x2019, 0x201C, 0x201D, 0x2022, 0x2013, 0x2014,
		0x02DC, 0x2122, 0x0161, 0x203A, 0x0153, 0x0142, 0x017E, 0x0178,
	};
}
