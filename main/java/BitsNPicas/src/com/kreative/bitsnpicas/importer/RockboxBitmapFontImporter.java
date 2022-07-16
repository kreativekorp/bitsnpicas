package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;

public class RockboxBitmapFontImporter implements BitmapFontImporter {
	private static final int RB11 = 0x52423131;
	private static final int RB12 = 0x52423132;
	
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		BitmapFont f = importFontImpl(new DataInputStream(in));
		in.close();
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(InputStream in) throws IOException {
		BitmapFont f = importFontImpl(new DataInputStream(in));
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		BitmapFont f = importFontImpl(new DataInputStream(in));
		in.close();
		
		String name = f.getName(Font.NAME_FAMILY);
		if (name == null || name.length() == 0) {
			name = file.getName();
			if (name.toLowerCase().endsWith(".fnt")) {
				name = name.substring(0, name.length() - 4);
			} else if (name.toLowerCase().endsWith(".rbf")) {
				name = name.substring(0, name.length() - 4);
			} else if (name.toLowerCase().endsWith(".rb11")) {
				name = name.substring(0, name.length() - 5);
			} else if (name.toLowerCase().endsWith(".rb12")) {
				name = name.substring(0, name.length() - 5);
			}
			f.setName(Font.NAME_FAMILY, name);
		}
		
		return new BitmapFont[]{f};
	}
	
	private BitmapFont importFontImpl(DataInputStream in) throws IOException {
		// Read magic number.
		int magic = in.readInt();
		if (magic != RB11 && magic != RB12) throw new IOException("bad magic number");
		
		// Read name and copyright info.
		String name;
		String copyright;
		if (magic == RB11) {
			byte[] nameBytes = new byte[64];
			byte[] copyrightBytes = new byte[256];
			in.readFully(nameBytes);
			in.readFully(copyrightBytes);
			name = new String(nameBytes, "UTF-8").trim();
			copyright = new String(copyrightBytes, "UTF-8").trim();
		} else {
			name = null;
			copyright = null;
		}
		
		// Read metrics.
		int maxWidth = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int height = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int ascent = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int depth = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int firstChar = Integer.reverseBytes(in.readInt());
		int defaultChar = Integer.reverseBytes(in.readInt());
		int numChars = Integer.reverseBytes(in.readInt());
		int numBits = Integer.reverseBytes(in.readInt());
		int numOffsets = Integer.reverseBytes(in.readInt());
		int numWidths = Integer.reverseBytes(in.readInt());
		if (depth != 0) throw new IOException("anti-aliased not supported right now");
		
		// Read bitmap.
		byte[] bitmap;
		if (magic == RB11) {
			bitmap = new byte[(((numBits * 2) + 3) / 4) * 4];
		} else {
			bitmap = new byte[((numBits + 1) / 2) * 2];
		}
		in.readFully(bitmap);
		
		// Read offsets and widths.
		int[] offsets = new int[numOffsets];
		int[] widths = new int[numWidths];
		if (magic == RB11) {
			for (int i = 0; i < numOffsets; i++) {
				offsets[i] = Integer.reverseBytes(in.readInt());
			}
		} else {
			for (int i = 0; i < numOffsets; i++) {
				offsets[i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
			}
		}
		for (int i = 0; i < numWidths; i++) {
			widths[i] = in.readUnsignedByte();
		}
		
		BitmapFont f = new BitmapFont(ascent, height-ascent, ascent, height-ascent, 0, 0, 0);
		if (name != null) f.setName(BitmapFont.NAME_FAMILY, name);
		if (copyright != null) f.setName(BitmapFont.NAME_COPYRIGHT, copyright);
		
		int defIndex = defaultChar - firstChar;
		int defOffset = (defIndex >= 0 && defIndex < numOffsets) ? offsets[defIndex] : 0;
		for (int i = 0; i < numChars; i++) {
			if (i != defIndex && i < numOffsets && offsets[i] == defOffset) continue;
			int width = (i < numWidths) ? widths[i] : maxWidth;
			int rowBytes = ((width + 15) / 16) * 2;
			byte[] glyphBitmap;
			if (magic == RB11) {
				int offset = (i < numOffsets) ? (offsets[i] * 2) : (rowBytes * height * i);
				glyphBitmap = swab(bitmap, offset, width, height);
			} else {
				int offset = (i < numOffsets) ? offsets[i] : (((height + 7) / 8) * maxWidth * i);
				glyphBitmap = rotright(bitmap, offset, width, height);
			}
			byte[][] glyphPixels = new byte[height][width];
			for (int by = 0, py = 0; py < height; py++, by += rowBytes) {
				for (int px = 0, bx = 0; bx < rowBytes; bx++) {
					for (int m = 0x80; m != 0; m >>= 1, px++) {
						if (px < width && (glyphBitmap[by + bx] & m) != 0) {
							glyphPixels[py][px] = -1;
						}
					}
				}
			}
			BitmapFontGlyph glyph = new BitmapFontGlyph(glyphPixels, 0, width, ascent);
			f.putCharacter(firstChar + i, glyph);
		}
		
		f.setXHeight();
		f.setCapHeight();
		return f;
	}
	
	private static byte[] swab(byte[] fntData, int fntOffset, int width, int height) {
		int rowByteCount = ((width + 15) / 16) * 2;
		int bmpByteCount = rowByteCount * height;
		byte[] bmpBytes = new byte[bmpByteCount];
		for (int i = 0; i < bmpByteCount; i++) {
			bmpBytes[i ^ 1] = fntData[fntOffset++];
		}
		return bmpBytes;
	}
	
	private static byte[] rotright(byte[] fntData, int fntOffset, int width, int height) {
		int rowWordCount = (width + 15) / 16;
		int bmpWordCount = rowWordCount * height;
		int[] bmpWords = new int[bmpWordCount];
		
		int fntMask = 1;
		for (int i = 0; i < height; i++) {
			int bmpOffset = i * rowWordCount;
			int bmpMask = 0x8000;
			for (int j = 0; j < width; j++) {
				if ((fntData[fntOffset + j] & fntMask) != 0) {
					bmpWords[bmpOffset] |= bmpMask;
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
		
		byte[] bmpBytes = new byte[bmpWordCount * 2];
		for (int bi = 0, wi = 0; wi < bmpWordCount; wi++) {
			bmpBytes[bi++] = (byte)(bmpWords[wi] >> 8);
			bmpBytes[bi++] = (byte)(bmpWords[wi] >> 0);
		}
		return bmpBytes;
	}
}
