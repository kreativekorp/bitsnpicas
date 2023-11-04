package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.unicode.data.GlyphList;

public class MousePaintBitmapFontExporter implements BitmapFontExporter {
	private GlyphList encoding;
	
	public MousePaintBitmapFontExporter() {
		this.encoding = null;
	}
	
	public MousePaintBitmapFontExporter(GlyphList encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		exportFontImpl(font, new DataOutputStream(out));
		out.close();
		return out.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		exportFontImpl(font, new DataOutputStream(os));
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		exportFontImpl(font, new DataOutputStream(out));
		out.close();
	}
	
	private void exportFontImpl(BitmapFont font, DataOutputStream out) throws IOException {
		int lastChar = 0;
		int maxWidth = 1;
		int height = font.getLineAscent() + font.getLineDescent() + font.getLineGap();
		if (height > 255) height = 255;
		
		BitmapFontGlyph[] bfgs = new BitmapFontGlyph[256];
		int[] widths = new int[256];
		for (int i = 0; i < 256; i++) {
			widths[i] = 1;
			Integer cp = (encoding == null) ? i : encoding.get(i);
			if (cp == null || cp < 0) continue;
			bfgs[i] = font.getCharacter(cp);
			if (bfgs[i] == null) continue;
			widths[i] = bfgs[i].getCharacterWidth();
			if (widths[i] < 1) widths[i] = 1;
			if (widths[i] > 14) widths[i] = 14;
			if (widths[i] > maxWidth) maxWidth = widths[i];
			lastChar = i;
		}
		
		int fontType = (maxWidth > 7) ? 0x80 : 0;
		maxWidth = (fontType == 0) ? 7 : (fontType == 0x80) ? 14 : 0;
		byte[][][] glyphs = new byte[lastChar + 1][height][maxWidth];
		for (int i = 0; i <= lastChar; i++) {
			if (bfgs[i] == null) continue;
			int y = font.getLineAscent() - bfgs[i].getGlyphAscent();
			for (byte[] row : bfgs[i].getGlyph()) {
				if (y >= 0 && y < height) {
					int x = bfgs[i].getGlyphOffset();
					for (byte b : row) {
						if (x >= 0 && x < maxWidth) {
							glyphs[i][y][x] = b;
						}
						x++;
					}
				}
				y++;
			}
		}
		
		out.writeByte(fontType);
		out.writeByte(lastChar);
		out.writeByte(height);
		for (int i = 0; i <= lastChar; i++) {
			out.writeByte(widths[i]);
		}
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < maxWidth; z += 7) {
				for (int i = 0; i <= lastChar; i++) {
					int b = 0;
					for (int x = z, m = 1; m < 0x80; m <<= 1, x++) {
						if (glyphs[i][y][x] < 0) b |= m;
					}
					out.writeByte(b);
				}
			}
		}
	}
}
