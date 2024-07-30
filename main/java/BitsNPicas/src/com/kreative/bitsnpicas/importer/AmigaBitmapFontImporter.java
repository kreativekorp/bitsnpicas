package com.kreative.bitsnpicas.importer;

import java.io.*;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;
import com.kreative.unicode.data.GlyphList;

public abstract class AmigaBitmapFontImporter implements BitmapFontImporter {
	public static final int STYLE_UNDERLINED = 0x01;
	public static final int STYLE_BOLD = 0x02;
	public static final int STYLE_ITALIC = 0x04;
	public static final int STYLE_EXTENDED = 0x08;
	public static final int STYLE_COLORFONT = 0x40;
	public static final int STYLE_TAGGED = 0x80;
	
	public static final int FLAGS_ROMFONT = 0x01;
	public static final int FLAGS_DISKFONT = 0x02;
	public static final int FLAGS_REVPATH = 0x04;
	public static final int FLAGS_TALLDOT = 0x08;
	public static final int FLAGS_WIDEDOT = 0x10;
	public static final int FLAGS_PROPORTIONAL = 0x20;
	public static final int FLAGS_DESIGNED = 0x40;
	public static final int FLAGS_REMOVED = 0x80;
	
	public static final int CTFFLAGS_COLORFONT = 0x0001;
	public static final int CTFFLAGS_GREYFONT = 0x0002;
	public static final int CTFFLAGS_ANTIALIAS = 0x0004;
	
	protected GlyphList encoding;
	protected AmigaBitmapFontImporter() { this.encoding = null; }
	protected AmigaBitmapFontImporter(GlyphList encoding) { this.encoding = encoding; }
	
	public static final class ContentsFile extends AmigaBitmapFontImporter {
		public ContentsFile() { super(); }
		public ContentsFile(GlyphList encoding) { super(encoding); }
		
		public BitmapFont[] importFont(File file) throws IOException {
			FileInputStream fis = new FileInputStream(file);
			DataInputStream dis = new DataInputStream(fis);
			BitmapFont[] f = importFont(file.getParentFile(), dis);
			dis.close();
			fis.close();
			return f;
		}
		
		public BitmapFont[] importFont(byte[] data) throws IOException {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			DataInputStream dis = new DataInputStream(bis);
			BitmapFont[] f = importFont(new File("."), dis);
			dis.close();
			bis.close();
			return f;
		}
		
		public BitmapFont[] importFont(InputStream is) throws IOException {
			return importFont(new File("."), new DataInputStream(is));
		}
		
		public BitmapFont[] importFont(File parent, DataInput in) throws IOException {
			int id = in.readUnsignedShort();
			if (!(id == 0x0F00 || id == 0x0F02)) throw new IOException("bad magic number");
			int count = in.readUnsignedShort();
			BitmapFont[] f = new BitmapFont[count];
			for (int i = 0; i < count; i++) {
				byte[] pdata = new byte[256]; in.readFully(pdata);
				int len = 0; while (len < 256 && pdata[len] != 0) len++;
				String[] path = new String(pdata, 0, len, "UTF-8").split("/");
				File ff = findFile(parent, path);
				in.readUnsignedShort(); // YSize
				in.readUnsignedByte(); // Style
				in.readUnsignedByte(); // Flags
				f[i] = importFont1(ff, path[0]);
			}
			return f;
		}
		
		private static File findFile(File parent, String[] path) {
			for (String name : path) {
				File[] children = parent.listFiles();
				parent = new File(parent, name);
				if (children != null) {
					for (File child : children) {
						if (child.getName().equalsIgnoreCase(name)) {
							parent = child;
						}
					}
				}
			}
			return parent;
		}
	}
	
	public static final class DescriptorFile extends AmigaBitmapFontImporter {
		public DescriptorFile() { super(); }
		public DescriptorFile(GlyphList encoding) { super(encoding); }
		
		public BitmapFont[] importFont(File file) throws IOException {
			BitmapFont f = importFont1(file, "Untitled");
			return new BitmapFont[]{f};
		}
		
		public BitmapFont[] importFont(InputStream is) throws IOException {
			BitmapFont f = importFont1(is, "Untitled");
			return new BitmapFont[]{f};
		}
		
		public BitmapFont[] importFont(byte[] data) throws IOException {
			BitmapFont f = importFont1(data, "Untitled");
			return new BitmapFont[]{f};
		}
	}
	
	protected BitmapFont importFont1(File file, String fontName) throws IOException {
		FileInputStream is = new FileInputStream(file);
		BitmapFont f = importFont1(is, fontName);
		is.close();
		return f;
	}
	
	protected BitmapFont importFont1(InputStream is, String fontName) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buf = new byte[65536]; int count;
		while ((count = is.read(buf)) > 0) os.write(buf, 0, count);
		os.flush(); os.close(); buf = os.toByteArray();
		return importFont1(buf, fontName);
	}
	
	protected BitmapFont importFont1(byte[] data, String fontName) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		DataInputStream fontIn = new DataInputStream(in);
		int hunkHeader = fontIn.readInt();
		if (hunkHeader != 0x03F3) throw new IOException("bad magic number");
		int strList = fontIn.readInt();
		if (strList != 0) throw new IOException("bad magic number");
		int hunks = fontIn.readInt();
		if (hunks != 1) throw new IOException("bad magic number");
		int firstHunk = fontIn.readInt();
		if (firstHunk != 0) throw new IOException("bad magic number");
		int lastHunk = fontIn.readInt();
		if (lastHunk != 0) throw new IOException("bad magic number");
		fontIn.readInt(); // hunk size
		int hunkType = fontIn.readInt();
		if (hunkType != 0x03E9) throw new IOException("bad magic number");
		fontIn.readInt(); // hunk size
		int base = 32; // current position; start of code hunk data
		fontIn.readInt(); // MOVEQ #0,D0; RTS
		int lnSucc = fontIn.readInt();
		if (lnSucc != 0) throw new IOException("bad magic number");
		int lnPred = fontIn.readInt();
		if (lnPred != 0) throw new IOException("bad magic number");
		int lnType = fontIn.readUnsignedByte();
		if (lnType != 0x0C) throw new IOException("bad magic number");
		int lnPri = fontIn.readUnsignedByte();
		if (lnPri != 0) throw new IOException("bad magic number");
		int lnName = fontIn.readInt();
		if (lnName != 0x1A) throw new IOException("bad magic number");
		int fileID = fontIn.readUnsignedShort();
		if (fileID != 0x0F80) throw new IOException("bad magic number");
		fontIn.readUnsignedShort(); // revision
		int segment = fontIn.readInt();
		if (segment != 0) throw new IOException("bad magic number");
		byte[] ndata = new byte[32]; fontIn.readFully(ndata);
		int len = 0; while (len < 32 && ndata[len] != 0) len++;
		if (len > 0) fontName = new String(ndata, 0, len, "ISO-8859-1");
		lnSucc = fontIn.readInt();
		if (lnSucc != 0) throw new IOException("bad magic number");
		lnPred = fontIn.readInt();
		if (lnPred != 0) throw new IOException("bad magic number");
		lnType = fontIn.readUnsignedByte();
		if (lnType != 0x0C) throw new IOException("bad magic number");
		lnPri = fontIn.readUnsignedByte();
		if (lnPri != 0) throw new IOException("bad magic number");
		lnName = fontIn.readInt();
		if (lnName != 0x1A) throw new IOException("bad magic number");
		int mnReplyPort = fontIn.readInt();
		if (mnReplyPort != 0) throw new IOException("bad magic number");
		fontIn.readUnsignedShort(); // mnLength
		int ySize = fontIn.readUnsignedShort();
		int style = fontIn.readUnsignedByte();
		fontIn.readUnsignedByte(); // flags
		int xSize = fontIn.readUnsignedShort();
		int baseline = fontIn.readUnsignedShort();
		fontIn.readUnsignedShort(); // boldSmear
		fontIn.readUnsignedShort(); // accessors
		int loChar = fontIn.readUnsignedByte();
		int hiChar = fontIn.readUnsignedByte();
		int charData = fontIn.readInt();
		int modulo = fontIn.readUnsignedShort();
		int charLoc = fontIn.readInt();
		int charSpace = fontIn.readInt();
		int charKern = fontIn.readInt();
		/*
		boolean isColor = ((style & STYLE_COLORFONT) != 0);
		int ctfFlags = isColor ? fontIn.readUnsignedShort() : 0;
		int ctfDepth = isColor ? fontIn.readUnsignedByte() : 0;
		int ctfFgColor = isColor ? fontIn.readUnsignedByte() : 0;
		int ctfLowColor = isColor ? fontIn.readUnsignedByte() : 0;
		int ctfHighColor = isColor ? fontIn.readUnsignedByte() : 0;
		int ctfPlanePick = isColor ? fontIn.readUnsignedByte() : 0;
		int ctfPlaneOnOff = isColor ? fontIn.readUnsignedByte() : 0;
		int ctfColorFontColors = isColor ? fontIn.readInt() : 0;
		int[] ctfCharData = new int[ctfDepth];
		for (int i = 0; i < ctfDepth; i++) ctfCharData[i] = fontIn.readInt();
		*/
		fontIn.close();
		in.close();
		
		System.out.println("charData: " + Integer.toHexString(charData));
		System.out.println("modulo: " + modulo);
		System.out.println("charLoc: " + Integer.toHexString(charLoc));
		System.out.println("charSpace: " + Integer.toHexString(charSpace));
		System.out.println("charKern: " + Integer.toHexString(charKern));
		
		byte[][] bitmap = new byte[ySize][modulo * 8];
		if (charData > 0) {
			for (int j = base + charData, y = 0; y < ySize; y++, j += modulo) {
				for (int di = 0, si = j, x = 0; x < modulo; x++, si++) {
					for (int m = 0x80; m != 0; m >>= 1, di++) {
						if ((data[si] & m) != 0) bitmap[y][di] = -1;
					}
				}
			}
		}
		
		int n = hiChar - loChar + 2;
		int[] bitOffset = new int[n];
		int[] bitSize = new int[n];
		int[] spacing = new int[n];
		int[] kerning = new int[n];
		if (charLoc > 0) {
			in = new ByteArrayInputStream(data, base + charLoc, data.length - (base + charLoc));
			fontIn = new DataInputStream(in);
			for (int i = 0; i < n; i++) {
				bitOffset[i] = fontIn.readUnsignedShort();
				bitSize[i] = fontIn.readUnsignedShort();
			}
			fontIn.close();
			in.close();
		}
		if (charSpace > 0) {
			in = new ByteArrayInputStream(data, base + charSpace, data.length - (base + charSpace));
			fontIn = new DataInputStream(in);
			for (int i = 0; i < n; i++) spacing[i] = fontIn.readShort();
			fontIn.close();
			in.close();
		} else {
			for (int i = 0; i < n; i++) spacing[i] = xSize;
		}
		if (charKern > 0) {
			in = new ByteArrayInputStream(data, base + charKern, data.length - (base + charKern));
			fontIn = new DataInputStream(in);
			for (int i = 0; i < n; i++) kerning[i] = fontIn.readShort();
			fontIn.close();
			in.close();
		}
		
		System.out.println("char\toffs\tsize\tspace\tkern");
		for (int i = 0; i < n; i++) {
			System.out.println(
				Integer.toHexString(loChar + i) +
				"\t" + bitOffset[i] + "\t" + bitSize[i] +
				"\t" + spacing[i] + "\t" + kerning[i]
			);
		}
		
		/*
		if (ctfColorFontColors > 0) {
			in = new ByteArrayInputStream(data, base + ctfColorFontColors, data.length - (base + ctfColorFontColors));
			fontIn = new DataInputStream(in);
			int cfcReserved = fontIn.readUnsignedShort();
			int cfcCount = fontIn.readUnsignedShort();
			int[] cfcColorTable = new int[cfcCount];
			for (int i = 0; i < cfcCount; i++) cfcColorTable[i] = fontIn.readUnsignedShort();
			fontIn.close();
			in.close();
		}
		*/
		
		// Add glyphs for characters
		int ascent = baseline + 1;
		int descent = ySize - ascent;
		BitmapFont font = new BitmapFont(ascent, descent, ascent, descent, 0, 0, 0);
		for (int i = 0, ch = loChar; ch <= hiChar; i++, ch++) {
			byte[][] glyph = new byte[ySize][bitSize[i]];
			for (int y = 0; y < ySize; y++) {
				for (int gx = 0, bx = bitOffset[i]; gx < bitSize[i]; gx++, bx++) {
					glyph[y][gx] = bitmap[y][bx];
				}
			}
			int offset = kerning[i];
			int advance = spacing[i] + kerning[i];
			BitmapFontGlyph g = new BitmapFontGlyph(glyph, offset, advance, ascent);
			int cp = (encoding != null) ? encoding.get(ch) : ch;
			if (cp >= 0) font.putCharacter(cp, g);
		}
		
		// Add .notdef glyph
		byte[][] glyph = new byte[ySize][bitSize[n-1]];
		for (int y = 0; y < ySize; y++) {
			for (int gx = 0, bx = bitOffset[n-1]; gx < bitSize[n-1]; gx++, bx++) {
				glyph[y][gx] = bitmap[y][bx];
			}
		}
		int offset = kerning[n-1];
		int advance = spacing[n-1] + kerning[n-1];
		BitmapFontGlyph g = new BitmapFontGlyph(glyph, offset, advance, ascent);
		font.putNamedGlyph(".notdef", g);
		
		// Set font properties
		font.setName(Font.NAME_FAMILY, fontName);
		font.setName(Font.NAME_STYLE, styleToString(style));
		font.setXHeight();
		font.setCapHeight();
		return font;
	}
	
	private static String styleToString(int style) {
		StringBuffer sb = new StringBuffer();
		if ((style & STYLE_BOLD) != 0) sb.append(" Bold");
		if ((style & STYLE_ITALIC) != 0) sb.append(" Italic");
		if ((style & STYLE_UNDERLINED) != 0) sb.append(" Underline");
		if ((style & STYLE_EXTENDED) != 0) sb.append(" Extended");
		return (sb.length() > 0) ? sb.toString().trim() : "Normal";
	}
}
