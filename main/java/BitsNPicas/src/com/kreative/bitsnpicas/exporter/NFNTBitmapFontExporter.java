package com.kreative.bitsnpicas.exporter;

import java.io.*;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;
import com.kreative.bitsnpicas.mover.FONDEntry;
import com.kreative.bitsnpicas.mover.FONDResource;
import com.kreative.unicode.data.GlyphList;
import com.kreative.unicode.ttflib.DfontFile;
import com.kreative.unicode.ttflib.DfontResource;

public abstract class NFNTBitmapFontExporter implements BitmapFontExporter {
	protected GlyphList encoding;
	protected NFNTBitmapFontExporter() { this.encoding = null; }
	protected NFNTBitmapFontExporter(GlyphList encoding) { this.encoding = encoding; }
	
	public static final class ResourceFile extends NFNTBitmapFontExporter {
		private IDGenerator idgen;
		private PointSizeGenerator sizegen;
		
		public ResourceFile(IDGenerator idgen, PointSizeGenerator sizegen) {
			super();
			this.idgen = idgen;
			this.sizegen = sizegen;
		}
		
		public ResourceFile(IDGenerator idgen, PointSizeGenerator sizegen, GlyphList enc) {
			super(enc);
			this.idgen = idgen;
			this.sizegen = sizegen;
		}
		
		public byte[] exportFontToBytes(BitmapFont font) throws IOException {
			// make meta
			font.autoFillNames();
			String name = font.getName(Font.NAME_FAMILY);
			int id = idgen.generateID(font);
			int size = sizegen.generatePointSize(font);
			
			// make FOND
			FONDResource fond = new FONDResource(name, id);
			fond.entries.add(new FONDEntry(size, font.getMacStyle(), id));
			byte[] fondData = fond.toByteArray();
			byte[] nfntData = exportNFNT(font);
			
			// make resource fork
			DfontFile rsrc = new DfontFile();
			rsrc.addResource(new DfontResource("FOND", id, 0x60, name, fondData, 0, fondData.length));
			rsrc.addResource(new DfontResource("NFNT", id, 0x60, name, nfntData, 0, nfntData.length));
			return rsrc.write();
		}
		
		public void exportFontToFile(BitmapFont font, File file) throws IOException {
			OutputStream os = new FileOutputStream(file);
			os.write(exportFontToBytes(font));
			os.close();
		}
		
		public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
			os.write(exportFontToBytes(font));
		}
	}
	
	public static final class FlatFile extends NFNTBitmapFontExporter {
		public FlatFile() { super(); }
		public FlatFile(GlyphList encoding) { super(encoding); }
		
		public byte[] exportFontToBytes(BitmapFont font) throws IOException {
			return exportNFNT(font);
		}
		
		public void exportFontToFile(BitmapFont font, File file) throws IOException {
			OutputStream os = new FileOutputStream(file);
			os.write(exportFontToBytes(font));
			os.close();
		}
		
		public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
			os.write(exportFontToBytes(font));
		}
	}
	
	protected byte[] exportNFNT(BitmapFont font) throws IOException {
		int type = 0x9000;
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
		BitmapFontGlyph notdef = font.getNamedGlyph(".notdef");
		if (notdef != null) {
			if (notdef.getCharacterWidth() > maxWidth) maxWidth = notdef.getCharacterWidth();
			if (notdef.getGlyphOffset() < kerning) kerning = notdef.getGlyphOffset();
			if (notdef.getGlyphOffset() + notdef.getGlyphWidth() - kerning > rectWidth) rectWidth = notdef.getGlyphOffset() + notdef.getGlyphWidth() - kerning;
			width += notdef.getGlyphWidth();
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
		if (notdef != null) {
			byte[][] g = notdef.getGlyph();
			for (int gy = 0, by = font.getLineAscent() - notdef.getGlyphAscent(); gy < g.length && by < bitmap.length; gy++, by++) {
				if (by >= 0) {
					for (int gx = 0, bx = xcoord; gx < g[gy].length && bx < bitmap[by].length; gx++, bx++) {
						bitmap[by][bx] = g[gy][gx];
					}
				}
			}
			xcoords[lastChar-firstChar+1] = (short)xcoord;
			offsets[lastChar-firstChar+1] = (byte)(notdef.getGlyphOffset()-kerning);
			widths[lastChar-firstChar+1] = (byte)notdef.getCharacterWidth();
			xcoord += notdef.getGlyphWidth();
		} else {
			xcoords[lastChar-firstChar+1] = (short)xcoord;
			offsets[lastChar-firstChar+1] = -1;
			widths[lastChar-firstChar+1] = -1;
		}
		xcoords[lastChar-firstChar+2] = (short)xcoord;
		
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
		return nfntOut.toByteArray();
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
