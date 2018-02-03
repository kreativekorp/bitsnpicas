package com.kreative.bitsnpicas.exporter;

import java.io.*;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class TOSBitmapFontExporter implements BitmapFontExporter {
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		exportFont(font, new DataOutputStream(out));
		out.close();
		return out.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		exportFont(font, new DataOutputStream(os));
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		exportFont(font, new DataOutputStream(out));
		out.close();
	}
	
	private void exportFont(BitmapFont font, DataOutputStream out) throws IOException {
		int w = 0;
		int a = font.getLineAscent();
		int d = font.getLineDescent();
		int h = a+d;
		int maxchar = 0;
		for (int i = 0; i < 0x110000; i++) {
			if (font.containsCharacter(i)) {
				int cw = font.getCharacter(i).getCharacterWidth();
				if (cw > w) w = cw;
				maxchar = i+1;
			}
		}
		out.writeInt(0x7056F097);
		out.writeShort(Short.reverseBytes((short)w));
		out.writeShort(Short.reverseBytes((short)h));
		for (int i = 0; i < maxchar; i++) {
			if (font.containsCharacter(i)) {
				BitmapFontGlyph g = font.getCharacter(i);
				int by = a-g.getGlyphAscent();
				int bx = g.getGlyphOffset();
				byte[][] gg = g.getGlyph();
				for (int y = 0; y < h; y++) {
					int gy = y-by;
					if (gy >= 0 && gy < gg.length) {
						for (int x = 0; x < w; x += 8) {
							byte b = 0;
							for (int ix = 0; ix < 8; ix++) {
								int gx = x+ix-bx;
								b <<= 1;
								if (gx >= 0 && gx < gg[gy].length && gg[gy][gx] < 0) {
									b |= 1;
								}
							}
							out.writeByte(b);
						}
					} else {
						for (int x = 0; x < w; x += 8) {
							out.writeByte(0);
						}
					}
				}
			} else {
				for (int y = 0; y < h; y++) {
					for (int x = 0; x < w; x += 8) {
						out.writeByte(0);
					}
				}
			}
		}
	}
}
