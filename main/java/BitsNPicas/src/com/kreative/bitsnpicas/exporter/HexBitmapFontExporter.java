package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class HexBitmapFontExporter implements BitmapFontExporter {
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		exportFont(font, pw);
		pw.close();
		out.close();
		return out.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		exportFont(font, pw);
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		exportFont(font, pw);
		pw.close();
		out.close();
	}
	
	private void exportFont(BitmapFont font, PrintWriter pw) {
		for (int cp = 0; cp < 0x110000; cp++) {
			if (font.containsCharacter(cp)) {
				BitmapFontGlyph g = font.getCharacter(cp);
				int width = (g.getCharacterWidth() + 3) / 4;
				if (width > 0) {
					byte[][] glyph = g.getGlyph();
					StringBuffer hex = new StringBuffer();
					for (int y = g.getGlyphAscent() - 14, j = 0; j < 16; j++, y++) {
						for (int x = -g.getGlyphOffset(), i = 0; i < width; i++) {
							int b = 0;
							for (int m = 8; m > 0; m >>= 1, x++) {
								if (
									y >= 0 && y < glyph.length &&
									x >= 0 && x < glyph[y].length &&
									glyph[y][x] < 0
								) {
									b |= m;
								}
							}
							char ch = Character.forDigit(b, 16);
							hex.append(Character.toUpperCase(ch));
						}
					}
					String cps = Integer.toHexString(cp).toUpperCase();
					while (!(cps.length() == 4 || cps.length() == 6)) cps = "0" + cps;
					pw.println(cps + ":" + hex.toString());
				}
			}
		}
	}
}
