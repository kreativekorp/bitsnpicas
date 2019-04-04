package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;

public class HexBitmapFontImporter implements BitmapFontImporter {
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		Scanner in = new Scanner(new ByteArrayInputStream(data), "UTF-8");
		BitmapFont f = importFont(in);
		in.close();
		if (f.isEmpty()) return new BitmapFont[0];
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(InputStream is) throws IOException {
		Scanner in = new Scanner(is, "UTF-8");
		BitmapFont f = importFont(in);
		if (f.isEmpty()) return new BitmapFont[0];
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		Scanner in = new Scanner(file, "UTF-8");
		BitmapFont f = importFont(in);
		in.close();
		if (f.isEmpty()) return new BitmapFont[0];
		return new BitmapFont[]{f};
	}
	
	private static BitmapFont importFont(Scanner in) {
		BitmapFont f = new BitmapFont(10, 2, 14, 2, 8, 0);
		lines: while (in.hasNextLine()) {
			String[] fields = in.nextLine().split(":");
			if (fields.length != 2) continue lines;
			int cp;
			try { cp = Integer.parseInt(fields[0].trim(), 16); }
			catch (NumberFormatException nfe) { continue lines; }
			char[] hex = fields[1].trim().toCharArray();
			int width = hex.length / 16;
			if (width < 1) continue lines;
			byte[][] glyph = new byte[16][width * 4];
			for (int i = 0, y = 0; y < 16; y++) {
				for (int x = 0, j = 0; j < width; j++) {
					int b = Character.getNumericValue(hex[i++]);
					if (b < 0 || b > 15) continue lines;
					glyph[y][x++] = ((b & 8) != 0) ? (byte)0xFF : 0;
					glyph[y][x++] = ((b & 4) != 0) ? (byte)0xFF : 0;
					glyph[y][x++] = ((b & 2) != 0) ? (byte)0xFF : 0;
					glyph[y][x++] = ((b & 1) != 0) ? (byte)0xFF : 0;
				}
			}
			BitmapFontGlyph g = new BitmapFontGlyph(glyph, 0, width * 4, 14);
			f.putCharacter(cp, g);
		}
		return f;
	}
}
