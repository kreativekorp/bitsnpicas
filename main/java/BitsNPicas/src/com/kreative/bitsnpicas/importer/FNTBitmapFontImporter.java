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
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class FNTBitmapFontImporter implements BitmapFontImporter {
	// Header fields that a font has no place for are kept as its properties.
	// The default and break characters are kept as their codes in the font.
	public static final String PROP_CHARSET = "fnt.charSet";
	public static final String PROP_VERT_RES = "fnt.vertRes";
	public static final String PROP_HORIZ_RES = "fnt.horizRes";
	public static final String PROP_FAMILY = "fnt.family";
	public static final String PROP_AVG_WIDTH = "fnt.avgWidth";
	public static final String PROP_DEFAULT_CHAR = "fnt.defaultChar";
	public static final String PROP_BREAK_CHAR = "fnt.breakChar";
	public static final String[] FAMILIES = { "DontCare", "Roman", "Swiss", "Modern", "Script", "Decorative" };
	
	private GlyphList encoding;
	
	public FNTBitmapFontImporter() {
		this.encoding = null;
	}
	
	public FNTBitmapFontImporter(GlyphList encoding) {
		this.encoding = encoding;
	}
	
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
		return new BitmapFont[]{f};
	}
	
	private BitmapFont importFontImpl(DataInputStream in) throws IOException {
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
		
		in.readShort(); // int points = Short.reverseBytes(in.readShort()) & 0xFFFF;
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
		in.readShort(); // int maxWidth = Short.reverseBytes(in.readShort()) & 0xFFFF;
		int firstChar = in.readUnsignedByte();
		int lastChar = in.readUnsignedByte();
		int defaultChar = in.readUnsignedByte();
		int breakChar = in.readUnsignedByte();
		in.readShort(); // int widthBytes = Short.reverseBytes(in.readShort()) & 0xFFFF;
		in.readInt(); // int device = Integer.reverseBytes(in.readInt());
		int face = Integer.reverseBytes(in.readInt());
		in.readInt(); // int bitsPointer = Integer.reverseBytes(in.readInt());
		in.readInt(); // int bitsOffset = Integer.reverseBytes(in.readInt());
		in.readByte(); // int reserved = in.readUnsignedByte();
		int flags = (magic >= 3) ? Integer.reverseBytes(in.readInt()) : 0;
		if (magic >= 3) in.readShort(); // int aSpace = (magic >= 3) ? (Short.reverseBytes(in.readShort()) & 0xFFFF) : 0;
		if (magic >= 3) in.readShort(); // int bSpace = (magic >= 3) ? (Short.reverseBytes(in.readShort()) & 0xFFFF) : 0;
		if (magic >= 3) in.readShort(); // int cSpace = (magic >= 3) ? (Short.reverseBytes(in.readShort()) & 0xFFFF) : 0;
		if (magic >= 3) in.readInt(); // int colorPointer = (magic >= 3) ? Integer.reverseBytes(in.readInt()) : 0;
		
		byte[] reserved1 = new byte[16];
		if (magic >= 3) in.readFully(reserved1);
		
		// int deviceEnd = device;
		// while (deviceEnd < size && data[deviceEnd] != 0) deviceEnd++;
		// String deviceName = new String(data, device, deviceEnd - device, "CP1252");
		
		int faceEnd = face;
		while (faceEnd < size && data[faceEnd] != 0) faceEnd++;
		String faceName = new String(data, face, faceEnd - face, "CP1252");
		
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
		
		// The em is the character cell without the internal leading.
		// (The point size is in points, not in pixels.)
		GlyphList encoding = this.encoding;
		if (encoding == null && charSet != 0) {
			String encodingName = getEncodingName(charSet);
			if (encodingName != null) encoding = EncodingList.instance().getGlyphList(encodingName);
		}
		
		int descent = pixHeight - ascent;
		int emAscent = Math.max(ascent - internalLeading, 0);
		int emDescent = descent;
		String styleName = styleName(italic, underline, strikeOut, weight);
		BitmapFont f = new BitmapFont(emAscent, emDescent, ascent, descent, 0, 0, externalLeading, pixWidth);
		f.setName(BitmapFont.NAME_COPYRIGHT, copyright);
		f.setName(BitmapFont.NAME_FAMILY, faceName);
		f.setName(BitmapFont.NAME_STYLE, styleName);
		f.setName(BitmapFont.NAME_FAMILY_AND_STYLE, faceName + " " + styleName);
		
		int family = pitchAndFamily >> 4;
		f.setProperty(PROP_CHARSET, Integer.toString(charSet));
		f.setProperty(PROP_VERT_RES, Integer.toString(vertRes));
		f.setProperty(PROP_HORIZ_RES, Integer.toString(horizRes));
		f.setProperty(PROP_FAMILY, (family < FAMILIES.length) ? FAMILIES[family] : Integer.toString(family));
		f.setProperty(PROP_AVG_WIDTH, Integer.toString(avgWidth));
		f.setProperty(PROP_DEFAULT_CHAR, Integer.toString(firstChar + defaultChar));
		f.setProperty(PROP_BREAK_CHAR, Integer.toString(firstChar + breakChar));
		
		for (int i = 0; i < n-1; i++) {
			byte[][] bitmap = new byte[geHeight[i]][geWidth[i]];
			for (int dy = geOffset[i], by = 0; by < geHeight[i]; by++, dy++) {
				for (int dx = dy, bx = 0; bx < geWidth[i]; dx += geHeight[i]) {
					for (int m = 0x80; bx < geWidth[i] && m != 0; bx++, m >>= 1) {
						if ((data[dx] & m) != 0) {
							bitmap[by][bx] = -1;
						}
					}
				}
			}
			BitmapFontGlyph g = new BitmapFontGlyph(bitmap, 0, geWidth[i], ascent);
			int ch = (encoding != null) ? encoding.get(firstChar + i) : fromCP1252(firstChar + i);
			if (ch < 0) ch = 0xF000 + firstChar + i;
			f.putCharacter(ch, g);
			// The default character is also the glyph of characters that the font lacks.
			if (i == defaultChar) {
				byte[][] copy = new byte[bitmap.length][];
				for (int by = 0; by < bitmap.length; by++) copy[by] = bitmap[by].clone();
				f.putNamedGlyph(".notdef", new BitmapFontGlyph(copy, 0, geWidth[i], ascent));
			}
		}
		
		f.setXHeight();
		f.setCapHeight();
		return f;
	}
	
	public static String getEncodingName(int charSet) {
		switch (charSet) {
			case 0: return "windows-1252";
			case 77: return "x-MacRoman";
			case 161: return "windows-1253";
			case 162: return "windows-1254";
			case 163: return "windows-1258";
			case 177: return "windows-1255";
			case 178: return "windows-1256";
			case 186: return "windows-1257";
			case 204: return "windows-1251";
			case 222: return "x-windows-874";
			case 238: return "windows-1250";
			case 255: return "IBM437";
			default: return null;
		}
	}
	
	public static String getEncodingName(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		try {
			byte[] header = new byte[86];
			if (in.read(header) < header.length) return null;
			return getEncodingName(header[85] & 0xFF);
		} finally {
			in.close();
		}
	}
	
	private static String styleName(int italic, int underline, int strikeOut, int weight) {
		StringBuffer sb = new StringBuffer();
		if (weight < 400) sb.append((weight < 200) ? " Thin" : " Light");
		if (weight >= 600) sb.append((weight >= 900) ? " Black" : " Bold");
		if (italic != 0) sb.append(" Italic");
		if (underline != 0) sb.append(" Underline");
		if (strikeOut != 0) sb.append(" Strikeout");
		return (sb.length() > 0) ? sb.toString().trim() : "Normal";
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
