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

public class RockboxBitmapFontExporter implements BitmapFontExporter {
	public static final int RB11 = 0x52423131;
	public static final int RB12 = 0x52423132;
	
	private int magic;
	private int depth;
	
	public RockboxBitmapFontExporter(int magic) {
		if (magic != RB11 && magic != RB12) throw new IllegalArgumentException("bad magic number");
		this.magic = magic;
		this.depth = 0;
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
		int ascent = font.getLineAscent();
		int height = ascent + font.getLineDescent() + font.getLineGap();
		int maxWidth = 0;
		int firstChar = -1;
		int numChars = 0;
		int numBits = 0;
		HashMap<Integer,byte[]> bitmaps = new HashMap<Integer,byte[]>();
		HashMap<Integer,Integer> offsets = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> widths = new HashMap<Integer,Integer>();
		
		for (int ch = 0; ch < 0x110000; ch++) {
			BitmapFontGlyph g = font.getCharacter(ch);
			if (g == null) continue;
			int width = g.getCharacterWidth();
			if (width > maxWidth) maxWidth = width;
			if (firstChar < 0) firstChar = ch;
			numChars = ch - firstChar + 1;
			
			int rowBytes = ((width + 15) / 16) * 2;
			byte[] glyphBitmap = new byte[rowBytes * height];
			byte[][] glyphPixels = g.getGlyph();
			for (int by = 0, j = g.getGlyphAscent() - ascent, y = 0; y < height; y++, j++, by += rowBytes) {
				if (j >= 0 && j < glyphPixels.length) {
					int bx = 0, i = -g.getGlyphOffset(), x = 0;
					while (x < width) {
						byte b = 0;
						for (int m = 0x80; m != 0 && x < width; m >>= 1, x++, i++) {
							if (i >= 0 && i < glyphPixels[j].length) {
								if (glyphPixels[j][i] < 0) b |= m;
							}
						}
						glyphBitmap[by + bx] = b;
						bx++;
					}
				}
			}
			
			if (magic == RB11) {
				glyphBitmap = swab(glyphBitmap, 0, width, height);
			} else {
				glyphBitmap = rotleft(glyphBitmap, 0, width, height);
			}
			
			widths.put(ch, width);
			offsets.put(ch, numBits);
			bitmaps.put(ch, glyphBitmap);
			numBits += glyphBitmap.length;
		}
		
		// Write magic number, name, and copyright string.
		out.writeInt(magic);
		if (magic == RB11) {
			String name = font.getName(BitmapFont.NAME_FAMILY);
			byte[] nameBytes = (name != null) ? name.getBytes("UTF-8") : new byte[0];
			out.write(nameBytes, 0, Math.min(nameBytes.length, 64));
			for (int i = nameBytes.length; i < 64; i++) out.write(0x20);
			String copyright = font.getName(BitmapFont.NAME_COPYRIGHT);
			byte[] copyrightBytes = (copyright != null) ? copyright.getBytes("UTF-8") : new byte[0];
			out.write(copyrightBytes, 0, Math.min(copyrightBytes.length, 256));
			for (int i = copyrightBytes.length; i < 256; i++) out.write(0x20);
		}
		
		// Write metrics.
		boolean pro = !isMono(firstChar, numChars, bitmaps);
		out.writeShort(Short.reverseBytes((short)maxWidth));
		out.writeShort(Short.reverseBytes((short)height));
		out.writeShort(Short.reverseBytes((short)ascent));
		out.writeShort(Short.reverseBytes((short)depth));
		out.writeInt(Integer.reverseBytes(firstChar));
		out.writeInt(Integer.reverseBytes(firstChar));
		out.writeInt(Integer.reverseBytes(numChars));
		out.writeInt(Integer.reverseBytes((magic == RB11) ? (numBits / 2) : numBits));
		out.writeInt(Integer.reverseBytes(pro ? numChars : 0));
		out.writeInt(Integer.reverseBytes(pro ? numChars : 0));
		
		// Write bitmaps.
		for (int i = 0; i < numChars; i++) {
			if (bitmaps.containsKey(firstChar + i)) {
				out.write(bitmaps.get(firstChar + i));
			}
		}
		if (magic == RB11) {
			while ((numBits & 3) != 0) {
				out.writeByte(0);
				numBits++;
			}
		} else {
			while ((numBits & 1) != 0) {
				out.writeByte(0);
				numBits++;
			}
		}
		
		if (pro) {
			// Write offsets.
			for (int i = 0; i < numChars; i++) {
				if (offsets.containsKey(firstChar + i)) {
					if (magic == RB11) {
						out.writeInt(Integer.reverseBytes(offsets.get(firstChar + i).intValue() / 2));
					} else {
						out.writeShort(Short.reverseBytes(offsets.get(firstChar + i).shortValue()));
					}
				} else {
					if (magic == RB11) {
						out.writeInt(0);
					} else {
						out.writeShort(0);
					}
				}
			}
			// Write widths.
			for (int i = 0; i < numChars; i++) {
				if (widths.containsKey(firstChar + i)) {
					out.writeByte(widths.get(firstChar + i));
				} else {
					out.writeByte(widths.get(firstChar));
				}
			}
		}
	}
	
	private static boolean isMono(int firstChar, int numChars, HashMap<Integer,byte[]> bitmaps) {
		byte[] bitmap = bitmaps.get(firstChar);
		if (bitmap == null) return true;
		int length = bitmap.length;
		for (int i = 1; i < numChars; i++) {
			bitmap = bitmaps.get(firstChar + i);
			if (bitmap == null) return false;
			if (bitmap.length != length) return false;
		}
		return true;
	}
	
	private static byte[] swab(byte[] bmpBytes, int bmpOffset, int width, int height) {
		int rowByteCount = ((width + 15) / 16) * 2;
		int bmpByteCount = rowByteCount * height;
		byte[] fntData = new byte[bmpByteCount];
		for (int i = 0; i < bmpByteCount; i++) {
			fntData[i ^ 1] = bmpBytes[bmpOffset++];
		}
		return fntData;
	}
	
	private static byte[] rotleft(byte[] bmpBytes, int bmpOffset, int width, int height) {
		int rowWordCount = (width + 15) / 16;
		int bmpWordCount = rowWordCount * height;
		int[] bmpWords = new int[bmpWordCount];
		
		for (int wi = 0; wi < bmpWordCount; wi++) {
			bmpWords[wi]  = (bmpBytes[bmpOffset++] & 0xFF) << 8;
			bmpWords[wi] |= (bmpBytes[bmpOffset++] & 0xFF) << 0;
		}
		
		byte[] fntData = new byte[((height + 7) / 8) * width];
		int fntOffset = 0;
		int fntMask = 1;
		for (int i = 0; i < height; i++) {
			bmpOffset = i * rowWordCount;
			int bmpMask = 0x8000;
			for (int j = 0; j < width; j++) {
				if ((bmpWords[bmpOffset] & bmpMask) != 0) {
					fntData[fntOffset + j] |= fntMask;
				}
				bmpMask >>= 1;
				if (bmpMask == 0) {
					bmpMask = 0x8000;
					bmpOffset++;
				}
			}
			fntMask <<= 1;
			if (fntMask >= 256) {
				fntMask = 1;
				fntOffset += width;
			}
		}
		return fntData;
	}
}
