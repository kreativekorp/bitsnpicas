package com.kreative.bitsnpicas.exporter;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.importer.FNTBitmapFontImporter;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

// Exports the font sheet of pxfont (https://github.com/mike-shevchenko/retro-tools),
// a tool that makes Windows .fon and TrueType fonts of such sheets.
// The sheet has the characters of a single-byte encoding, 32 to a row,
// each at its code modulo 32. Cells are colored as a checkerboard,
// navy on white and black on light gray, so that the width of each
// character can be told. A code without a character is left transparent,
// and so is the paper of the default character of a font, when the font
// tells it and it is at 0x7F, where the sheets of pxfont have it.
public class PxfontBitmapFontExporter implements BitmapFontExporter {
	private static final int COLUMNS = 32;
	private static final int DEFAULT_CHAR = 0x7F;
	private static final int[] PAPER = { 0xFFFFFFFF, 0xFFC0C0C0 };
	private static final int[] INK = { 0xFF000080, 0xFF000000 };
	
	private GlyphList encoding;
	
	public PxfontBitmapFontExporter() {
		this.encoding = null;
	}
	
	public PxfontBitmapFontExporter(GlyphList encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		RenderedImage r = exportFontToImage(font);
		ImageIO.write(r, "png", b);
		return b.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		RenderedImage r = exportFontToImage(font);
		ImageIO.write(r, "png", os);
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		RenderedImage r = exportFontToImage(font);
		ImageIO.write(r, "png", file);
	}
	
	public RenderedImage exportFontToImage(BitmapFont font) throws IOException {
		// Without an encoding given, a font imported from FNT is exported in its own.
		GlyphList encoding = this.encoding;
		if (encoding == null) {
			int cs = intProperty(font, FNTBitmapFontImporter.PROP_CHARSET, 0);
			String name = (cs != 0) ? FNTBitmapFontImporter.getEncodingName(cs) : null;
			encoding = EncodingList.instance().getGlyphList((name != null) ? name : "windows-1252");
		}
		int ascent = font.getLineAscent();
		int height = ascent + font.getLineDescent();
		if (height < 1) throw new IOException("the line height of the font is zero");
		
		// Characters and their widths.
		BitmapFontGlyph[] glyphs = new BitmapFontGlyph[256];
		int[] widthCounts = new int[256 * 4];
		int firstChar = -1;
		int lastChar = -1;
		int numChars = 0;
		for (int idx = 0; idx < 256; idx++) {
			int ch = (encoding != null) ? encoding.get(idx) : idx;
			if (ch < 0) ch = 0xF000 + idx;
			BitmapFontGlyph g = font.getCharacter(ch);
			if (g == null || g.getCharacterWidth() < 1) continue;
			glyphs[idx] = g;
			if (g.getCharacterWidth() < widthCounts.length) widthCounts[g.getCharacterWidth()]++;
			if (firstChar < 0) firstChar = idx;
			lastChar = idx;
			numChars++;
		}
		if (firstChar < 0) throw new IOException("no characters in the selected encoding");
		
		// A code without a character takes the width that most characters have.
		int gap = 1;
		for (int w = 1; w < widthCounts.length; w++) {
			if (widthCounts[w] >= widthCounts[gap]) gap = w;
		}
		// A font of other than fixed pitch may tell its average width.
		if (widthCounts[gap] < numChars) {
			gap = Math.max(intProperty(font, FNTBitmapFontImporter.PROP_AVG_WIDTH, gap), 1);
		}
		int defaultChar = intProperty(font, FNTBitmapFontImporter.PROP_DEFAULT_CHAR, -1);
		
		// Rows go from that of the first character to that of the last one.
		int firstRow = firstChar / COLUMNS;
		int rows = lastChar / COLUMNS - firstRow + 1;
		int width = 0;
		for (int row = 0; row < rows; row++) {
			for (int x = 0, col = 0; col < COLUMNS; col++) {
				BitmapFontGlyph g = glyphs[(firstRow + row) * COLUMNS + col];
				x += (g != null) ? g.getCharacterWidth() : gap;
				if (g != null && x > width) width = x;
			}
		}
		
		BufferedImage bi = new BufferedImage(width, rows * height, BufferedImage.TYPE_INT_ARGB);
		for (int row = 0; row < rows; row++) {
			for (int x = 0, col = 0; col < COLUMNS; col++) {
				int idx = (firstRow + row) * COLUMNS + col;
				BitmapFontGlyph g = glyphs[idx];
				if (g == null) {
					x += gap;
					continue;
				}
				int w = g.getCharacterWidth();
				int ink = INK[(row + col) & 1];
				int paper = PAPER[(row + col) & 1];
				// The default character at its usual code is told by having no paper.
				if (idx == DEFAULT_CHAR && idx == defaultChar && w == gap) paper = 0;
				byte[][] gg = g.getGlyph();
				for (int y = 0, gy = g.getGlyphAscent() - ascent; y < height; y++, gy++) {
					for (int cx = 0, gx = -g.getGlyphOffset(); cx < w; cx++, gx++) {
						boolean on = (gy >= 0 && gy < gg.length && gx >= 0 && gx < gg[gy].length && gg[gy][gx] < 0);
						bi.setRGB(x + cx, row * height + y, on ? ink : paper);
					}
				}
				x += w;
			}
		}
		return bi;
	}
	
	private static int intProperty(BitmapFont font, String key, int def) {
		String value = font.getProperty(key);
		if (value == null) return def;
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return def;
		}
	}
}
