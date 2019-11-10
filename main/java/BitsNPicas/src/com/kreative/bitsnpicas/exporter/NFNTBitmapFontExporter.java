package com.kreative.bitsnpicas.exporter;

import java.io.*;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;
import com.kreative.bitsnpicas.unicode.EncodingTable;
import com.kreative.ksfl.*;
import com.kreative.rsrc.*;

public class NFNTBitmapFontExporter implements BitmapFontExporter {
	private IDGenerator idgen;
	private PointSizeGenerator sizegen;
	private EncodingTable encoding;
	
	public NFNTBitmapFontExporter(IDGenerator idgen, PointSizeGenerator sizegen) {
		this.idgen = idgen;
		this.sizegen = sizegen;
		this.encoding = null;
	}
	
	public NFNTBitmapFontExporter(IDGenerator idgen, PointSizeGenerator sizegen, EncodingTable enc) {
		this.idgen = idgen;
		this.sizegen = sizegen;
		this.encoding = enc;
	}
	
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		String name;
		int id;
		int size;
		int type = 0;
		int firstChar = Integer.MAX_VALUE;
		int lastChar = Integer.MIN_VALUE;
		int maxWidth = 0;
		int kerning = 0;
		int nDescent = -font.getLineDescent();
		int rectWidth = 0;
		int height = font.getLineAscent() + font.getLineDescent();
		int wots;
		int ascent = font.getLineAscent();
		int descent = font.getLineDescent();
		int leading = font.getLineGap();
		int rowWidth;
		byte[][] bitmap;
		short[] xcoords;
		byte[] offsets;
		byte[] widths;
		
		// calculate metrics
		int width = 0;
		for (int i = 0; i < 256; i++) {
			int cp = (encoding != null) ? encoding.get(i) : MACROMAN[i];
			BitmapFontGlyph c = font.getCharacter(cp);
			if (c != null) {
				if (i < firstChar) firstChar = i;
				if (i > lastChar) lastChar = i;
				if (c.getCharacterWidth() > maxWidth) maxWidth = c.getCharacterWidth();
				if (c.getGlyphOffset() < kerning) kerning = c.getGlyphOffset();
				if (c.getGlyphOffset() + c.getGlyphWidth() - kerning > rectWidth) rectWidth = c.getGlyphOffset() + c.getGlyphWidth() - kerning;
				width += c.getGlyphWidth();
			}
		}
		rowWidth = (width + 15) / 16;
		wots = 5 + (rowWidth*height) + (lastChar-firstChar+3);
		bitmap = new byte[height][rowWidth * 16];
		xcoords = new short[lastChar-firstChar+3];
		offsets = new byte[lastChar-firstChar+2];
		widths = new byte[lastChar-firstChar+2];
		
		// make bitmap and tables
		int xcoord = 0;
		for (int i = firstChar, o = 0; i <= lastChar && o < offsets.length; i++, o++) {
			int cp = (encoding != null) ? encoding.get(i) : MACROMAN[i];
			BitmapFontGlyph c = font.getCharacter(cp);
			if (c != null) {
				byte[][] g = c.getGlyph();
				for (int gy = 0, by = font.getLineAscent() - c.getGlyphAscent(); gy < g.length && by < bitmap.length; gy++, by++) {
					if (by >= 0) {
						for (int gx = 0, bx = xcoord; gx < g[gy].length && bx < bitmap[by].length; gx++, bx++) {
							bitmap[by][bx] = g[gy][gx];
						}
					}
				}
				xcoords[o] = (short)xcoord;
				offsets[o] = (byte)(c.getGlyphOffset()-kerning);
				widths[o] = (byte)c.getCharacterWidth();
				xcoord += c.getGlyphWidth();
			} else {
				xcoords[o] = (short)xcoord;
				offsets[o] = -1;
				widths[o] = -1;
			}
		}
		
		// make catchall bitmap
		{
			BitmapFontGlyph c = font.getCharacter(-1);
			if (c != null) {
				byte[][] g = c.getGlyph();
				for (int gy = 0, by = font.getLineAscent() - c.getGlyphAscent(); gy < g.length && by < bitmap.length; gy++, by++) {
					if (by >= 0) {
						for (int gx = 0, bx = xcoord; gx < g[gy].length && bx < bitmap[by].length; gx++, bx++) {
							bitmap[by][bx] = g[gy][gx];
						}
					}
				}
				xcoords[lastChar-firstChar+1] = (short)xcoord;
				offsets[lastChar-firstChar+1] = (byte)(c.getGlyphOffset()-kerning);
				widths[lastChar-firstChar+1] = (byte)c.getCharacterWidth();
				xcoord += c.getGlyphWidth();
			} else {
				xcoords[lastChar-firstChar+1] = (short)xcoord;
				offsets[lastChar-firstChar+1] = -1;
				widths[lastChar-firstChar+1] = -1;
			}
			xcoords[lastChar-firstChar+2] = (short)xcoord;
		}
		
		// make real bitmap
		byte[][] realBitmap = new byte[height][rowWidth*2];
		for (int y = 0; y < bitmap.length; y++) {
			for (int rx = 0, x = 0; rx < realBitmap[y].length && x < bitmap[y].length; rx++) {
				for (int k = 0; x < bitmap[y].length && k < 8; x++, k++) {
					realBitmap[y][rx] <<= 1;
					if ((bitmap[y][x] & 0xFF) >= 0x80) realBitmap[y][rx] |= 1;
				}
			}
		}
		
		// make meta
		font.autoFillNames();
		name = font.getName(Font.NAME_FAMILY);
		id = idgen.generateID(font);
		size = sizegen.generatePointSize(font);
		
		// make NFNT
		ByteArrayOutputStream nfntOut = new ByteArrayOutputStream();
		DataOutputStream nfntIn = new DataOutputStream(nfntOut);
		nfntIn.writeShort(type);
		nfntIn.writeShort(firstChar);
		nfntIn.writeShort(lastChar);
		nfntIn.writeShort(maxWidth);
		nfntIn.writeShort(kerning);
		nfntIn.writeShort(nDescent);
		nfntIn.writeShort(rectWidth);
		nfntIn.writeShort(height);
		nfntIn.writeShort(wots);
		nfntIn.writeShort(ascent);
		nfntIn.writeShort(descent);
		nfntIn.writeShort(leading);
		nfntIn.writeShort(rowWidth);
		for (byte[] line : realBitmap) nfntIn.write(line);
		for (short x : xcoords) nfntIn.writeShort(x);
		for (int i = 0; i < offsets.length && i < widths.length; i++) {
			nfntIn.writeByte(offsets[i]);
			nfntIn.writeByte(widths[i]);
		}
		
		// make FOND
		ByteArrayOutputStream fondOut = new ByteArrayOutputStream();
		DataOutputStream fondIn = new DataOutputStream(fondOut);
		fondIn.writeShort(type);
		fondIn.writeShort(id);
		fondIn.writeShort(firstChar);
		fondIn.writeShort(lastChar);
		fondIn.writeShort(ascent);
		fondIn.writeShort(descent);
		fondIn.writeShort(leading);
		fondIn.writeShort(maxWidth);
		fondIn.writeInt(0); // offset to width tables
		fondIn.writeInt(0); // offset to kerning tables
		fondIn.writeInt(0); // offset to style mapping tables
		fondIn.writeShort(0); // unused
		fondIn.writeShort(0); // extra width for bold
		fondIn.writeShort(0); // extra width for italic
		fondIn.writeShort(0); // extra width for underline
		fondIn.writeShort(0); // extra width for outline
		fondIn.writeShort(0); // extra width for shadow
		fondIn.writeShort(0); // extra width for condensed
		fondIn.writeShort(0); // extra width for extended
		fondIn.writeShort(0); // undefined
		fondIn.writeInt(0); // rsvd for international
		fondIn.writeShort(3); // fond version
		fondIn.writeShort(0); // font entries (-1 = no entries, 0 = 1 entry, 1 = 2 entries, etc.)
		fondIn.writeShort(size);
		fondIn.writeShort(font.getMacStyle());
		fondIn.writeShort(id);
		
		// make resource fork
		MacResourceArray rp = new MacResourceArray();
		rp.add(new MacResource(KSFLConstants.FOND, (short)id, name, fondOut.toByteArray()));
		rp.add(new MacResource(KSFLConstants.NFNT, (short)id, name, nfntOut.toByteArray()));
		return rp.getBytes();
	}

	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		OutputStream os = new FileOutputStream(file);
		os.write(exportFontToBytes(font));
		os.close();
	}

	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		os.write(exportFontToBytes(font));
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
