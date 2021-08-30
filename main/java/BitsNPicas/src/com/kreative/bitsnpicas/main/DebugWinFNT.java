package com.kreative.bitsnpicas.main;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DebugWinFNT {
	public static final int CHARSET_CP1252  =   0;
	public static final int CHARSET_DEFAULT =   1;
	public static final int CHARSET_SYMBOL  =   2;
	public static final int CHARSET_MAC     =  77;
	public static final int CHARSET_CP932   = 128;
	public static final int CHARSET_CP949   = 129;
	public static final int CHARSET_CP1361  = 130;
	public static final int CHARSET_CP936   = 134;
	public static final int CHARSET_CP950   = 136;
	public static final int CHARSET_CP1253  = 161;
	public static final int CHARSET_CP1254  = 162;
	public static final int CHARSET_CP1258  = 163;
	public static final int CHARSET_CP1255  = 177;
	public static final int CHARSET_CP1256  = 178;
	public static final int CHARSET_CP1257  = 186;
	public static final int CHARSET_CP1251  = 204;
	public static final int CHARSET_CP874   = 222;
	public static final int CHARSET_CP1250  = 238;
	public static final int CHARSET_OEM     = 255;
	
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				FileInputStream in = new FileInputStream(new File(arg));
				debugWinFNT(new DataInputStream(new BufferedInputStream(in)));
				in.close();
			} catch (IOException e) {
				System.out.println("  ERROR: " + e);
			}
		}
	}
	
	private static void debugWinFNT(DataInputStream in) throws IOException {
		// READ HEADER
		
		int magic = in.readUnsignedShort();
		if (magic < 1 || magic > 3) throw new IOException("bad magic number: " + magic);
		
		int size = Integer.reverseBytes(in.readInt());
		if (size < 118) throw new IOException("bad size: " + size);
		byte[] data = new byte[size];
		in.readFully(data, 6, size - 6);
		in = new DataInputStream(new ByteArrayInputStream(data, 6, size - 6));
		
		byte[] copyrightBytes = new byte[60];
		in.readFully(copyrightBytes);
		int copyrightLength = 0;
		while (copyrightLength < 60 && copyrightBytes[copyrightLength] != 0) copyrightLength++;
		String copyright = new String(copyrightBytes, 0, copyrightLength, "CP1252");
		
		int type = Short.reverseBytes(in.readShort()) & 0xFFFF;
		if ((type & 1) != 0) throw new IOException("vector fonts are not supported");
		
		int points = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int vertRes = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int horizRes = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int ascent = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int internalLeading = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int externalLeading = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int italic = in.readUnsignedByte();
		int underline = in.readUnsignedByte();
		int strikeOut = in.readUnsignedByte();
		int weight = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int charSet = in.readUnsignedByte();
		int pixWidth = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int pixHeight = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int pitchAndFamily = in.readUnsignedByte();
		int avgWidth = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int maxWidth = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int firstChar = in.readUnsignedByte();
		int lastChar = in.readUnsignedByte();
		int defaultChar = in.readUnsignedByte();
		int breakChar = in.readUnsignedByte();
		int widthBytes = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int device = Integer.reverseBytes(in.readInt());
		int face = Integer.reverseBytes(in.readInt());
		int bitsPointer = Integer.reverseBytes(in.readInt());
		int bitsOffset = Integer.reverseBytes(in.readInt());
		int reserved = in.readUnsignedByte();
		int flags = (magic >= 3) ? Integer.reverseBytes(in.readInt()) : 0;
		int aSpace = (magic >= 3) ? (Short.reverseBytes(in.readShort()) & 0xFFFF) : 0;
		int bSpace = (magic >= 3) ? (Short.reverseBytes(in.readShort()) & 0xFFFF) : 0;
		int cSpace = (magic >= 3) ? (Short.reverseBytes(in.readShort()) & 0xFFFF) : 0;
		int colorPointer = (magic >= 3) ? Integer.reverseBytes(in.readInt()) : 0;
		
		byte[] reserved1 = new byte[16];
		if (magic >= 3) in.readFully(reserved1);
		
		int deviceEnd = device;
		while (deviceEnd < size && data[deviceEnd] != 0) deviceEnd++;
		String deviceName = new String(data, device, deviceEnd - device, "CP1252");
		
		int faceEnd = face;
		while (faceEnd < size && data[faceEnd] != 0) faceEnd++;
		String faceName = new String(data, face, faceEnd - face, "CP1252");
		
		// PRINT HEADER
		
		System.out.println("Magic:\t" + magic);
		System.out.println("Size:\t" + size);
		System.out.println("Copyrt:\t" + copyright);
		System.out.println("Type:\t" + type);
		System.out.println("Points:\t" + points);
		System.out.println("VrtRes:\t" + vertRes);
		System.out.println("HrzRes:\t" + horizRes);
		System.out.println("Ascent:\t" + ascent);
		System.out.println("IntLdg:\t" + internalLeading);
		System.out.println("ExtLdg:\t" + externalLeading);
		System.out.println("Italic:\t" + italic);
		System.out.println("Undrln:\t" + underline);
		System.out.println("Stkout:\t" + strikeOut);
		System.out.println("Weight:\t" + weight);
		System.out.println("Charst:\t" + charSet);
		System.out.println("PixWid:\t" + pixWidth);
		System.out.println("PixHgt:\t" + pixHeight);
		System.out.println("PchFam:\t" + pitchAndFamily);
		System.out.println("AvgWid:\t" + avgWidth);
		System.out.println("MaxWid:\t" + maxWidth);
		System.out.println("FrstCh:\t" + firstChar);
		System.out.println("LastCh:\t" + lastChar);
		System.out.println("DfltCh:\t" + defaultChar);
		System.out.println("BrkCh:\t" + breakChar);
		System.out.println("WidByt:\t" + widthBytes);
		System.out.println("Device:\t" + device + "\t" + deviceName);
		System.out.println("Face:\t" + face + "\t" + faceName);
		System.out.println("BitPtr:\t" + bitsPointer);
		System.out.println("BitOfs:\t" + bitsOffset);
		System.out.println("Rsrvd:\t" + reserved);
		System.out.println("Flags:\t" + flags);
		System.out.println("ASpace:\t" + aSpace);
		System.out.println("BSpace:\t" + bSpace);
		System.out.println("CSpace:\t" + cSpace);
		System.out.println("ClrPtr:\t" + colorPointer);
		
		System.out.print("Rsrvd1:\t");
		for (byte b : reserved1) System.out.print(b + " ");
		System.out.println();
		
		// READ CHARTABLE
		
		int n = lastChar - firstChar + 2;
		int[] geWidth = new int[n];
		int[] geOffset = new int[n];
		int[] geHeight = new int[n];
		int[] geAspace = new int[n];
		int[] geBspace = new int[n];
		int[] geCspace = new int[n];
		if (magic < 3) {
			for (int i = 0; i < n; i++) {
				geWidth[i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
				geOffset[i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
				geHeight[i] = pixHeight;
			}
		} else {
			for (int i = 0; i < n; i++) {
				geWidth[i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
				geOffset[i] = Integer.reverseBytes(in.readInt());
				if ((flags & 0xF0) < 0x20) {
					geHeight[i] = pixHeight;
				} else {
					geHeight[i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
				}
				if ((flags & 0x0F) >= 0x04) {
					geAspace[i] = Integer.reverseBytes(in.readInt());
					geBspace[i] = Integer.reverseBytes(in.readInt());
					geCspace[i] = Integer.reverseBytes(in.readInt());
				}
			}
		}
		
		// PRINT CHARTABLE
		
		System.out.println("CharTable:");
		for (int i = 0; i < n; i++) {
			System.out.println(
				"\t" + geWidth[i] +
				"\t" + geOffset[i] +
				"\t" + geHeight[i] +
				"\t" + geAspace[i] +
				"\t" + geBspace[i] +
				"\t" + geCspace[i]
			);
		}
		
		// READ BITMAPS
		
		byte[][][] bitmaps = new byte[n][][];
		for (int i = 0; i < n; i++) {
			bitmaps[i] = new byte[geHeight[i]][geWidth[i]];
			for (int dy = geOffset[i], by = 0; by < geHeight[i]; by++, dy++) {
				for (int dx = dy, bx = 0; bx < geWidth[i]; dx += geHeight[i]) {
					for (int m = 0x80; bx < geWidth[i] && m != 0; bx++, m >>= 1) {
						if ((data[dx] & m) != 0) {
							bitmaps[i][by][bx] = -1;
						}
					}
				}
			}
		}
		
		// PRINT BITMAPS
		
		System.out.println("Bitmaps:");
		for (int i = 0; i < n; i++) {
			System.out.println("\t" + i + ":");
			for (byte[] row : bitmaps[i]) {
				System.out.print("\t\t");
				for (byte b : row) {
					System.out.print((b == 0) ? "." : "#");
				}
				System.out.println();
			}
		}
	}
}
