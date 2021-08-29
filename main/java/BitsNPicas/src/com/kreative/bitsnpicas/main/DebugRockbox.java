package com.kreative.bitsnpicas.main;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DebugRockbox {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				FileInputStream in = new FileInputStream(new File(arg));
				debugRockbox(new DataInputStream(in));
				in.close();
			} catch (IOException e) {
				System.out.println("  ERROR: " + e);
			}
		}
	}
	
	private static void debugRockbox(DataInputStream in) throws IOException {
		byte[] magic = new byte[4]; in.readFully(magic);
		System.out.println("Magic:\t" + new String(magic));
		
		if (magic[3] == '1') {
			byte[] name = new byte[64]; in.readFully(name);
			byte[] copyright = new byte[256]; in.readFully(copyright);
			System.out.println("Name:\t" + new String(name).trim());
			System.out.println("Copyrt:\t" + new String(copyright).trim());
		}
		
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
		System.out.println("MaxWid:\t" + maxWidth);
		System.out.println("Height:\t" + height);
		System.out.println("Ascent:\t" + ascent);
		System.out.println("Depth:\t" + depth);
		System.out.println("FrstCh:\t" + firstChar);
		System.out.println("DfltCh:\t" + defaultChar);
		System.out.println("NChars:\t" + numChars);
		System.out.println("NBits:\t" + numBits);
		System.out.println("NOffst:\t" + numOffsets);
		System.out.println("NWidth:\t" + numWidths);
		
		byte[] bitmap;
		if (magic[3] == '1') {
			bitmap = new byte[(((numBits * 2) + 3) / 4) * 4];
		} else {
			bitmap = new byte[((numBits + 1) / 2) * 2];
		}
		in.readFully(bitmap);
		
		int[] offsets = new int[numOffsets];
		int[] widths = new int[numWidths];
		for (int i = 0; i < numOffsets; i++) {
			if (magic[3] == '1') {
				offsets[i] = Integer.reverseBytes(in.readInt());
			} else {
				offsets[i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
			}
		}
		for (int i = 0; i < numWidths; i++) {
			widths[i] = in.readUnsignedByte();
		}
		
		/*
		if (numOffsets > 0) {
			System.out.print("Offsts:\t");
			for (int i : offsets) System.out.print(i + " ");
			System.out.println();
		}
		if (numWidths > 0) {
			System.out.print("Widths:\t");
			for (int i : widths) System.out.print(i + " ");
			System.out.println();
		}
		*/
		
		for (int i = 0; i < numChars; i++) {
			System.out.println("Glyph " + i + ":");
			int width = (i < widths.length) ? widths[i] : maxWidth;
			int rowBytes = ((width + 15) / 16) * 2;
			byte[] gb;
			if (magic[3] == '1') {
				int o = (i < offsets.length) ? (offsets[i] * 2) : (rowBytes * height * i);
				gb = swab(bitmap, o, width, height);
			} else {
				int o = (i < offsets.length) ? offsets[i] : (((height + 7) / 8) * maxWidth * i);
				gb = rotright(bitmap, o, width, height);
			}
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < rowBytes; x++) {
					for (int m = 0x80; m != 0; m >>= 1) {
						if ((gb[y * rowBytes + x] & m) == 0) {
							System.out.print(".");
						} else {
							System.out.print("#");
						}
					}
				}
				System.out.println();
			}
		}
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
