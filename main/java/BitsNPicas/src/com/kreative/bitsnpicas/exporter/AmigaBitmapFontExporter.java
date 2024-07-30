package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.unicode.data.GlyphList;

public abstract class AmigaBitmapFontExporter implements BitmapFontExporter {
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
	
	protected Boolean proportional;
	protected GlyphList encoding;
	
	protected AmigaBitmapFontExporter(Boolean proportional) {
		this.proportional = proportional;
		this.encoding = null;
	}
	
	protected AmigaBitmapFontExporter(Boolean proportional, GlyphList encoding) {
		this.proportional = proportional;
		this.encoding = encoding;
	}
	
	public static final class ContentsFile extends AmigaBitmapFontExporter {
		public ContentsFile(Boolean proportional) {
			super(proportional);
		}
		public ContentsFile(Boolean proportional, GlyphList encoding) {
			super(proportional, encoding);
		}
		public byte[] exportFontToBytes(BitmapFont font) throws IOException {
			boolean p = (proportional != null) ? proportional : !font.isMonospaced();
			return exportContentsFile(null, null, p, font);
		}
		public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
			boolean p = (proportional != null) ? proportional : !font.isMonospaced();
			os.write(exportContentsFile(null, null, p, font));
		}
		public void exportFontToFile(BitmapFont font, File file) throws IOException {
			File contentsFile, fontDirectory, descriptorFile;
			File fileParent = file.getParentFile();
			String fileName = file.getName();
			if (fileName.toLowerCase().endsWith(".font")) {
				contentsFile = file;
				String otherName = fileName.substring(0, fileName.length() - 5);
				fontDirectory = new File(fileParent, otherName);
				fontDirectory.mkdir();
				descriptorFile = new File(fontDirectory, getFileName(font));
			} else {
				String otherName = fileName + ".font";
				contentsFile = new File(fileParent, otherName);
				fontDirectory = file;
				fontDirectory.mkdir();
				descriptorFile = new File(fontDirectory, getFileName(font));
			}
			boolean p = (proportional != null) ? proportional : !font.isMonospaced();
			byte[] contents = exportContentsFile(contentsFile, fontDirectory.getName(), p, font);
			byte[] descriptor = exportDescriptorFile(font, p);
			OutputStream cos = new FileOutputStream(contentsFile);
			cos.write(contents);
			cos.close();
			OutputStream dos = new FileOutputStream(descriptorFile);
			dos.write(descriptor);
			dos.close();
		}
	}
	
	public static final class DescriptorFile extends AmigaBitmapFontExporter {
		public DescriptorFile(Boolean proportional) {
			super(proportional);
		}
		public DescriptorFile(Boolean proportional, GlyphList encoding) {
			super(proportional, encoding);
		}
		public byte[] exportFontToBytes(BitmapFont font) throws IOException {
			boolean p = (proportional != null) ? proportional : !font.isMonospaced();
			return exportDescriptorFile(font, p);
		}
		public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
			boolean p = (proportional != null) ? proportional : !font.isMonospaced();
			os.write(exportDescriptorFile(font, p));
		}
		public void exportFontToFile(BitmapFont font, File file) throws IOException {
			boolean p = (proportional != null) ? proportional : !font.isMonospaced();
			OutputStream os = new FileOutputStream(file);
			os.write(exportDescriptorFile(font, p));
			os.close();
		}
	}
	
	protected byte[] exportContentsFile(File existing, String prefix, boolean proportional, BitmapFont... fonts) throws IOException {
		int id = 0x0F00;
		ArrayList<byte[]> items = new ArrayList<byte[]>();
		if (existing != null && existing.exists()) {
			FileInputStream fin = new FileInputStream(existing);
			DataInputStream din = new DataInputStream(fin);
			id = din.readUnsignedShort();
			if (!(id == 0x0F00 || id == 0x0F02)) {
				din.close();
				fin.close();
				throw new IOException("bad magic number");
			}
			int count = din.readUnsignedShort();
			for (int i = 0; i < count; i++) {
				byte[] data = new byte[260];
				din.readFully(data);
				items.add(data);
			}
			din.close();
			fin.close();
		}
		for (BitmapFont font : fonts) {
			String dirName = prefix;
			if (dirName == null || dirName.length() == 0) {
				dirName = font.getName(BitmapFont.NAME_FAMILY);
				if (dirName == null || dirName.length() == 0) {
					dirName = "Untitled";
				}
			}
			byte[] pdata = (dirName + "/" + getFileName(font)).getBytes("ISO-8859-1");
			int len = pdata.length; if (len > 255) len = 255;
			ByteArrayOutputStream iout = new ByteArrayOutputStream();
			DataOutputStream iin = new DataOutputStream(iout);
			iin.write(pdata, 0, len);
			iin.write(new byte[256 - len]);
			iin.writeShort(getYSize(font));
			iin.writeByte(getStyle(font));
			iin.writeByte(getFlags(font, proportional));
			items.add(iout.toByteArray());
		}
		ByteArrayOutputStream cout = new ByteArrayOutputStream();
		DataOutputStream cin = new DataOutputStream(cout);
		cin.writeShort(id);
		cin.writeShort(items.size());
		for (byte[] item : items) cin.write(item);
		return cout.toByteArray();
	}
	
	protected byte[] exportDescriptorFile(BitmapFont font, boolean proportional) throws IOException {
		byte[] code = exportCodeHunk(font, proportional);
		int codeSize = (code.length + 3) / 4;
		int padSize = (codeSize * 4) - code.length;
		ByteArrayOutputStream dout = new ByteArrayOutputStream();
		DataOutputStream din = new DataOutputStream(dout);
		din.writeInt(0x03F3); // HUNK_HEADER
		din.writeInt(0); // strList
		din.writeInt(1); // hunks
		din.writeInt(0); // firstHunk
		din.writeInt(0); // lastHunk
		din.writeInt(codeSize); // hunkSize
		din.writeInt(0x03E9); // HUNK_CODE
		din.writeInt(codeSize); // hunkSize
		din.write(code);
		din.write(new byte[padSize]);
		din.writeInt(0x03EC); // HUNK_RELOC32
		din.writeInt(proportional ? 6 : 4); // number of offsets
		din.writeInt(0); // hunkNumber
		if (proportional) din.writeInt(0x6A); // tfCharKern
		if (proportional) din.writeInt(0x66); // tfCharSpace
		din.writeInt(0x62); // tfCharLoc
		din.writeInt(0x5C); // tfCharData
		din.writeInt(0x44); // lnName
		din.writeInt(0x0E); // lnName
		din.writeInt(0); // number of offsets
		din.writeInt(0x03F2); // HUNK_END
		return dout.toByteArray();
	}
	
	protected byte[] exportCodeHunk(BitmapFont font, boolean proportional) throws IOException {
		byte[] textFont = exportTextFont(font, proportional);
		ByteArrayOutputStream chOut = new ByteArrayOutputStream();
		DataOutputStream chIn = new DataOutputStream(chOut);
		chIn.writeInt(0x70644E75); // MOVEQ #0,D0; RTS
		chIn.writeInt(0); // lnSucc
		chIn.writeInt(0); // lnPred
		chIn.writeByte(0x0C); // lnType
		chIn.writeByte(0x00); // lnPri
		chIn.writeInt(0x1A); // lnName
		chIn.writeShort(0x0F80); // fileID
		chIn.writeShort(0); // revision
		chIn.writeInt(0); // segment
		String name = font.getName(Font.NAME_FAMILY);
		byte[] ndata = (name != null) ? name.getBytes("ISO-8859-1") : new byte[0];
		int len = ndata.length; if (len > 31) len = 31;
		chIn.write(ndata, 0, len); chIn.write(new byte[32 - len]);
		chIn.writeInt(0); // lnSucc
		chIn.writeInt(0); // lnPred
		chIn.writeByte(0x0C); // lnType
		chIn.writeByte(0x00); // lnPri
		chIn.writeInt(0x1A); // lnName
		chIn.writeInt(0); // mnReplyPort
		chIn.writeShort(textFont.length + 20); // mnLength
		chIn.write(textFont);
		return chOut.toByteArray();
	}
	
	protected byte[] exportTextFont(BitmapFont font, boolean proportional) throws IOException {
		int height = font.getLineAscent() + font.getLineDescent();
		int ySize = getYSize(font);
		int style = getStyle(font);
		int flags = getFlags(font, proportional);
		int xSize = proportional ? height : 1;
		int baseline = font.getLineAscent() - 1;
		int boldSmear = 1;
		int accessors = 0;
		int loChar = Integer.MAX_VALUE;
		int hiChar = Integer.MIN_VALUE;
		
		// calculate metrics
		HashSet<GlyphKey> gset = new HashSet<GlyphKey>();
		int width = 0;
		for (int i = 0; i < 256; i++) {
			int cp = (encoding != null) ? encoding.get(i) : i;
			BitmapFontGlyph c = font.getCharacter(cp);
			if (c != null) {
				if (i < loChar) loChar = i;
				if (i > hiChar) hiChar = i;
				GlyphKey key = new GlyphKey(c, proportional);
				if (gset.contains(key)) continue;
				gset.add(key);
				if (c.getCharacterWidth() > xSize) xSize = c.getCharacterWidth();
				width += proportional ? c.getGlyphWidth() : c.getCharacterWidth();
			}
		}
		BitmapFontGlyph notdef = font.getNamedGlyph(".notdef");
		if (notdef != null) {
			if (notdef.getCharacterWidth() > xSize) xSize = notdef.getCharacterWidth();
			width += proportional ? notdef.getGlyphWidth() : notdef.getCharacterWidth();
		}
		
		int modulo = ((width + 15) / 16) * 2;
		byte[][] bitmap = new byte[ySize][modulo * 8];
		int n = hiChar - loChar + 2;
		int[] bitOffset = new int[n];
		int[] bitSize = new int[n];
		int[] spacing = new int[n];
		int[] kerning = new int[n];
		
		int charLoc, charSpace, charKern, charData;
		if (proportional) {
			charLoc = 0x6E;
			charSpace = charLoc + n * 4;
			charKern = charSpace + n * 2;
			charData = charKern + n * 2;
		} else {
			charLoc = 0x6E;
			charSpace = 0;
			charKern = 0;
			charData = charLoc + n * 4;
		}
		
		// make bitmap and tables
		HashMap<GlyphKey,GlyphValues> gmap = new HashMap<GlyphKey,GlyphValues>();
		int xcoord = 0;
		for (int i = loChar, o = 0; i <= hiChar && o < n; i++, o++) {
			int cp = (encoding != null) ? encoding.get(i) : i;
			BitmapFontGlyph c = font.getCharacter(cp);
			if (c != null) {
				xcoord = addGlyph(font, c, proportional, xcoord, o, bitmap, bitOffset, bitSize, spacing, kerning, gmap);
			} else if (notdef != null) {
				xcoord = addGlyph(font, notdef, proportional, xcoord, o, bitmap, bitOffset, bitSize, spacing, kerning, gmap);
			}
		}
		if (notdef != null) {
			xcoord = addGlyph(font, notdef, proportional, xcoord, n-1, bitmap, bitOffset, bitSize, spacing, kerning, gmap);
		}
		
		// make real bitmap
		byte[][] realBitmap = new byte[ySize][modulo];
		for (int y = 0; y < bitmap.length; y++) {
			for (int rx = 0, x = 0; rx < realBitmap[y].length && x < bitmap[y].length; rx++) {
				for (int k = 0; x < bitmap[y].length && k < 8; x++, k++) {
					realBitmap[y][rx] <<= 1;
					if ((bitmap[y][x] & 0xFF) >= 0x80) realBitmap[y][rx] |= 1;
				}
			}
		}
		
		// make TextFont
		ByteArrayOutputStream tfOut = new ByteArrayOutputStream();
		DataOutputStream tfIn = new DataOutputStream(tfOut);
		tfIn.writeShort(ySize);
		tfIn.writeByte(style);
		tfIn.writeByte(flags);
		tfIn.writeShort(xSize);
		tfIn.writeShort(baseline);
		tfIn.writeShort(boldSmear);
		tfIn.writeShort(accessors);
		tfIn.writeByte(loChar);
		tfIn.writeByte(hiChar);
		tfIn.writeInt(charData);
		tfIn.writeShort(modulo);
		tfIn.writeInt(charLoc);
		tfIn.writeInt(charSpace);
		tfIn.writeInt(charKern);
		for (int i = 0; i < n; i++) {
			tfIn.writeShort(bitOffset[i]);
			tfIn.writeShort(bitSize[i]);
		}
		if (proportional) {
			for (int s : spacing) tfIn.writeShort(s);
			for (int k : kerning) tfIn.writeShort(k);
		}
		for (byte[] line : realBitmap) tfIn.write(line);
		return tfOut.toByteArray();
	}
	
	private static int addGlyph(
		BitmapFont font, BitmapFontGlyph c, boolean proportional, int xcoord, int o,
		byte[][] bitmap, int[] bitOffset, int[] bitSize, int[] spacing, int[] kerning,
		HashMap<GlyphKey,GlyphValues> gmap
	) {
		GlyphKey key = new GlyphKey(c, proportional);
		GlyphValues values = gmap.get(key);
		if (values == null) {
			int gbx = proportional ? 0 : (c.getGlyphOffset() < 0) ? -c.getGlyphOffset() : 0;
			int bbx = proportional ? xcoord : (c.getGlyphOffset() <= 0) ? xcoord : (xcoord + c.getGlyphOffset());
			byte[][] g = c.getGlyph();
			for (int gy = 0, by = font.getLineAscent() - c.getGlyphAscent(); gy < g.length && by < bitmap.length; gy++, by++) {
				if (by >= 0) {
					for (int gx = gbx, bx = bbx; gx < g[gy].length && bx < bitmap[by].length; gx++, bx++) {
						bitmap[by][bx] = g[gy][gx];
					}
				}
			}
			values = new GlyphValues();
			values.bitOffset = xcoord;
			values.bitSize = proportional ? c.getGlyphWidth() : c.getCharacterWidth();
			xcoord += values.bitSize;
			gmap.put(key, values);
		}
		bitOffset[o] = values.bitOffset;
		bitSize[o] = values.bitSize;
		spacing[o] = (proportional && values.bitSize > 0) ? (c.getCharacterWidth() - c.getGlyphOffset()) : c.getCharacterWidth();
		kerning[o] = (proportional && values.bitSize > 0) ? c.getGlyphOffset() : 0;
		return xcoord;
	}
	
	private static final class GlyphKey {
		private final BitmapFontGlyph glyph;
		private final Object[] keys;
		private GlyphKey(BitmapFontGlyph glyph, boolean proportional) {
			this.glyph = glyph;
			if (proportional) {
				this.keys = new Object[] {
					glyph.getGlyph(),
					glyph.getGlyphAscent()
				};
			} else {
				this.keys = new Object[] {
					glyph.getGlyph(),
					glyph.getGlyphOffset(),
					glyph.getGlyphAscent(),
					glyph.getCharacterWidth()
				};
			}
		}
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof GlyphKey)) return false;
			if (this.glyph == ((GlyphKey)o).glyph) return true;
			return Arrays.deepEquals(keys, ((GlyphKey)o).keys);
		}
		public int hashCode() {
			return Arrays.deepHashCode(keys);
		}
	}
	
	private static final class GlyphValues {
		private int bitOffset;
		private int bitSize;
	}
	
	private static int getYSize(BitmapFont font) {
		return font.getLineAscent() + font.getLineDescent() + font.getLineGap();
	}
	
	private static int getStyle(BitmapFont font) {
		int s = 0;
		if (font.isUnderlineStyle()) s |= STYLE_UNDERLINED;
		if (font.isBoldStyle())      s |= STYLE_BOLD;
		if (font.isItalicStyle())    s |= STYLE_ITALIC;
		if (font.isExtendedStyle())  s |= STYLE_EXTENDED;
		return s;
	}
	
	private static int getFlags(BitmapFont font, boolean proportional) {
		int f = FLAGS_DESIGNED;
		if (proportional) f |= FLAGS_PROPORTIONAL;
		return f;
	}
	
	private static String getFileName(BitmapFont font) {
		StringBuffer sb = new StringBuffer();
		sb.append(getYSize(font));
		if (font.isUnderlineStyle()) sb.append("U");
		if (font.isBoldStyle())      sb.append("B");
		if (font.isItalicStyle())    sb.append("I");
		if (font.isExtendedStyle())  sb.append("E");
		return sb.toString();
	}
}
