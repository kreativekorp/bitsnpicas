package com.kreative.bitsnpicas.importer;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.mover.FONDEntry;
import com.kreative.bitsnpicas.mover.MoverFile;
import com.kreative.bitsnpicas.mover.ResourceBundle;
import com.kreative.unicode.data.GlyphList;
import com.kreative.unicode.ttflib.DfontFile;
import com.kreative.unicode.ttflib.DfontResource;

public abstract class NFNTBitmapFontImporter implements BitmapFontImporter {
	protected GlyphList encoding;
	protected NFNTBitmapFontImporter() { this.encoding = null; }
	protected NFNTBitmapFontImporter(GlyphList encoding) { this.encoding = encoding; }
	
	public static final class ResourceFile extends NFNTBitmapFontImporter {
		public ResourceFile() { super(); }
		public ResourceFile(GlyphList encoding) { super(encoding); }
		
		public BitmapFont[] importFont(byte[] data) throws IOException {
			return importFont(new DfontFile(data));
		}
		
		public BitmapFont[] importFont(File file) throws IOException {
			return importFont(new DfontFile(file));
		}
		
		public BitmapFont[] importFont(InputStream is) throws IOException {
			return importFont(new DfontFile(is));
		}
		
		public BitmapFont[] importFont(DfontFile rsrc) throws IOException {
			List<BitmapFont> fonts = new ArrayList<BitmapFont>();
			MoverFile mf = new MoverFile(rsrc);
			for (int i = 0, n = mf.size(); i < n; i++) {
				for (BitmapFont font : importFont(mf.get(i))) {
					fonts.add(font);
				}
			}
			return fonts.toArray(new BitmapFont[fonts.size()]);
		}
		
		public BitmapFont[] importFont(ResourceBundle rb) throws IOException {
			List<BitmapFont> fonts = new ArrayList<BitmapFont>();
			if (rb.fond != null) {
				Map<Integer,byte[]> resData = new HashMap<Integer,byte[]>();
				for (DfontResource res : rb.resources) {
					if (
						res.getTypeString().equals("NFNT") ||
						(res.getTypeString().equals("FONT") && !resData.containsKey(res.getId()))
					) {
						resData.put(res.getId(), res.getData());
					}
				}
				for (FONDEntry e : rb.fond.entries) {
					if (e.size != 0) {
						fonts.add(importFont(
							resData.get(e.id), rb.fond.id,
							e.size, e.style, rb.fond.name
						));
					}
				}
			}
			return fonts.toArray(new BitmapFont[fonts.size()]);
		}
	}
	
	private static final Pattern COMPONENT_PATTERN = Pattern.compile(
		"\\s+(#([0-9]+)|([0-9]+)|Bold|Italic|Underline|Outline|Shadow|Condensed|Extended)",
		Pattern.CASE_INSENSITIVE
	);
	
	public static final class FlatFile extends NFNTBitmapFontImporter {
		public FlatFile() { super(); }
		public FlatFile(GlyphList encoding) { super(encoding); }
		
		public BitmapFont[] importFont(File file) throws IOException {
			int fontId = 12;
			int fontSize = 12;
			int fontStyle = 0;
			StringBuffer sb = new StringBuffer();
			Matcher m = COMPONENT_PATTERN.matcher(file.getName());
			while (m.find()) {
				if (m.group(2) != null) fontId = Integer.parseInt(m.group(2));
				else if (m.group(3) != null) fontSize = Integer.parseInt(m.group(3));
				else if (m.group(1).equalsIgnoreCase("Bold")) fontStyle |= 1;
				else if (m.group(1).equalsIgnoreCase("Italic")) fontStyle |= 2;
				else if (m.group(1).equalsIgnoreCase("Underline")) fontStyle |= 4;
				else if (m.group(1).equalsIgnoreCase("Outline")) fontStyle |= 8;
				else if (m.group(1).equalsIgnoreCase("Shadow")) fontStyle |= 16;
				else if (m.group(1).equalsIgnoreCase("Condensed")) fontStyle |= 32;
				else if (m.group(1).equalsIgnoreCase("Extended")) fontStyle |= 64;
				m.appendReplacement(sb, "");
			}
			m.appendTail(sb);
			String fontName = sb.toString().trim();
			if (fontName.length() == 0) fontName = "Untitled";
			BitmapFont f = importFont(file, fontId, fontSize, fontStyle, fontName);
			return new BitmapFont[]{f};
		}
		
		public BitmapFont[] importFont(InputStream is) throws IOException {
			BitmapFont f = importFont(is, 12, 12, 0, "Untitled");
			return new BitmapFont[]{f};
		}
		
		public BitmapFont[] importFont(byte[] data) throws IOException {
			BitmapFont f = importFont(data, 12, 12, 0, "Untitled");
			return new BitmapFont[]{f};
		}
	}
	
	protected BitmapFont importFont(File file, int fontId, int fontSize, int fontStyle, String fontName) throws IOException {
		FileInputStream is = new FileInputStream(file);
		BitmapFont f = importFont(is, fontId, fontSize, fontStyle, fontName);
		is.close();
		return f;
	}
	
	protected BitmapFont importFont(InputStream is, int fontId, int fontSize, int fontStyle, String fontName) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buf = new byte[65536]; int count;
		while ((count = is.read(buf)) > 0) os.write(buf, 0, count);
		os.flush(); os.close(); buf = os.toByteArray();
		return importFont(buf, fontId, fontSize, fontStyle, fontName);
	}
	
	protected BitmapFont importFont(byte[] data, int fontId, int fontSize, int fontStyle, String fontName) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		DataInputStream nfntIn = new DataInputStream(in);
		/* int type = */ nfntIn.readShort();
		int firstChar = nfntIn.readShort();
		int lastChar = nfntIn.readShort();
		/* int maxWidth = */ nfntIn.readShort();
		int kerning = nfntIn.readShort();
		int nDescent = nfntIn.readShort();
		/* int rectWidth = */ nfntIn.readShort();
		int height = nfntIn.readShort();
		int wots = nfntIn.readUnsignedShort();
		if (nDescent > 0) wots |= (nDescent << 16);
		int ascent = nfntIn.readShort();
		int descent = nfntIn.readShort();
		int leading = nfntIn.readShort();
		int rowWidth = nfntIn.readUnsignedShort();
		byte[][] realBitmap = new byte[height][rowWidth * 2];
		for (int i = 0; i < height; i++) nfntIn.readFully(realBitmap[i]);
		short[] xcoords = new short[lastChar - firstChar + 3];
		for (int i = 0; i < xcoords.length; i++) xcoords[i] = nfntIn.readShort();
		nfntIn.skip(2 * (wots - (5 + (rowWidth * height) + (lastChar - firstChar + 3))));
		byte[] offsets = new byte[lastChar - firstChar + 2];
		byte[] widths = new byte[lastChar - firstChar + 2];
		for (int i = 0; i < offsets.length && i < widths.length; i++) {
			offsets[i] = nfntIn.readByte();
			widths[i] = nfntIn.readByte();
		}
		nfntIn.close();
		in.close();
		
		byte[][] bitmap = new byte[height][rowWidth * 16];
		for (int y = 0; y < height; y++) {
			for (int rx = 0, x = 0; rx < realBitmap[y].length && x < bitmap[y].length; rx++) {
				for (int k = 0; x < bitmap[y].length && k < 8; x++, k++) {
					if (((realBitmap[y][rx] << k) & 0x80) != 0) bitmap[y][x] = -1;
				}
			}
		}
		
		int emAscent = ascent;
		int emDescent = descent;
		while (emAscent + emDescent < fontSize) {
			if (emAscent + emDescent < fontSize) emAscent++;
			if (emAscent + emDescent < fontSize) emDescent++;
		}
		while (emAscent + emDescent > fontSize) {
			if (emAscent + emDescent > fontSize) emAscent--;
			if (emAscent + emDescent > fontSize) emDescent--;
		}
		
		BitmapFont font = new BitmapFont(emAscent, emDescent, ascent, descent, 0, 0, leading);
		for (int i = 0, ch = firstChar; ch <= lastChar; i++, ch++) {
			if (widths[i] != -1) {
				int xcoord = xcoords[i] & 0xFFFF;
				int width = (xcoords[i + 1] & 0xFFFF) - xcoord;
				if (width < 0 || (xcoord + width) > (rowWidth * 16)) continue;
				byte[][] glyph = new byte[height][width];
				for (int y = 0; y < height; y++) {
					for (int gx = 0, bx = xcoord; gx < width; gx++, bx++) {
						glyph[y][gx] = bitmap[y][bx];
					}
				}
				int offset = offsets[i] + kerning;
				int advance = widths[i] & 0xFF;
				BitmapFontGlyph g = new BitmapFontGlyph(glyph, offset, advance, ascent);
				int cp = (encoding != null) ? encoding.get(ch) : MACROMAN[ch];
				if (cp >= 0) font.putCharacter(cp, g);
			}
		}
		if (widths[lastChar - firstChar + 1] != -1) {
			int xcoord = xcoords[lastChar - firstChar + 1] & 0xFFFF;
			int width = (xcoords[lastChar - firstChar + 2] & 0xFFFF) - xcoord;
			if (!(width < 0 || (xcoord + width) > (rowWidth * 16))) {
				byte[][] glyph = new byte[height][width];
				for (int y = 0; y < height; y++) {
					for (int gx = 0, bx = xcoord; gx < width; gx++, bx++) {
						glyph[y][gx] = bitmap[y][bx];
					}
				}
				int offset = offsets[lastChar - firstChar + 1] + kerning;
				int advance = widths[lastChar - firstChar + 1] & 0xFF;
				BitmapFontGlyph g = new BitmapFontGlyph(glyph, offset, advance, ascent);
				font.putNamedGlyph(".notdef", g);
			}
		}
		font.setName(Font.NAME_FAMILY, fontName);
		font.setName(Font.NAME_STYLE, fontStyleToString(fontStyle));
		font.setXHeight();
		font.setCapHeight();
		return font;
	}
	
	private static String fontStyleToString(int fontStyle) {
		StringBuffer sb = new StringBuffer();
		if ((fontStyle & 0x01) != 0) sb.append(" Bold");
		if ((fontStyle & 0x02) != 0) sb.append(" Italic");
		if ((fontStyle & 0x04) != 0) sb.append(" Underline");
		if ((fontStyle & 0x08) != 0) sb.append(" Outline");
		if ((fontStyle & 0x10) != 0) sb.append(" Shadow");
		if ((fontStyle & 0x20) != 0) sb.append(" Condensed");
		if ((fontStyle & 0x40) != 0) sb.append(" Extended");
		return (sb.length() > 0) ? sb.toString().trim() : "Normal";
	}
	
	private static final int[] MACROMAN = new int[] {
		'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007',
		'\b', '\t', '\n', '\u000B', '\u000C', '\r', '\u000E', '\u000F',
		'\u0010', '\u2318', '\u2713', '\u25C6', '\uF8FF', '\u0015', '\u0016', '\u0017',
		'\u0018', '\u0019', '\u001A', '\u001B', '\u001C', '\u001D', '\u001E', '\u001F',
		' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?',
		'@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
		'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_',
		'`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
		'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u007F',
		'\u00C4', '\u00C5', '\u00C7', '\u00C9', '\u00D1', '\u00D6', '\u00DC', '\u00E1',
		'\u00E0', '\u00E2', '\u00E4', '\u00E3', '\u00E5', '\u00E7', '\u00E9', '\u00E8',
		'\u00EA', '\u00EB', '\u00ED', '\u00EC', '\u00EE', '\u00EF', '\u00F1', '\u00F3',
		'\u00F2', '\u00F4', '\u00F6', '\u00F5', '\u00FA', '\u00F9', '\u00FB', '\u00FC',
		'\u2020', '\u00B0', '\u00A2', '\u00A3', '\u00A7', '\u2022', '\u00B6', '\u00DF',
		'\u00AE', '\u00A9', '\u2122', '\u00B4', '\u00A8', '\u2260', '\u00C6', '\u00D8',
		'\u221E', '\u00B1', '\u2264', '\u2265', '\u00A5', '\u00B5', '\u2202', '\u2211',
		'\u220F', '\u03C0', '\u222B', '\u00AA', '\u00BA', '\u03A9', '\u00E6', '\u00F8',
		'\u00BF', '\u00A1', '\u00AC', '\u221A', '\u0192', '\u2248', '\u2206', '\u00AB',
		'\u00BB', '\u2026', '\u00A0', '\u00C0', '\u00C3', '\u00D5', '\u0152', '\u0153',
		'\u2013', '\u2014', '\u201C', '\u201D', '\u2018', '\u2019', '\u00F7', '\u25CA',
		'\u00FF', '\u0178', '\u2044', '\u20AC', '\u2039', '\u203A', '\uFB01', '\uFB02',
		'\u2021', '\u00B7', '\u201A', '\u201E', '\u2030', '\u00C2', '\u00CA', '\u00C1',
		'\u00CB', '\u00C8', '\u00CD', '\u00CE', '\u00CF', '\u00CC', '\u00D3', '\u00D4',
		'\uF8FF', '\u00D2', '\u00DA', '\u00DB', '\u00D9', '\u0131', '\u02C6', '\u02DC',
		'\u00AF', '\u02D8', '\u02D9', '\u02DA', '\u00B8', '\u02DD', '\u02DB', '\u02C7'
	};
}
