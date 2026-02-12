package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;

public class HRCGBitmapFontImporter implements BitmapFontImporter {
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		BitmapFont f = importFontImpl(new DataInputStream(in));
		in.close();
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(InputStream in) throws IOException {
		BitmapFont f = importFontImpl(new DataInputStream(in));
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		BitmapFont f = importFontImpl(new DataInputStream(in));
		in.close();
		
		String name = file.getName();
		if (name.toLowerCase().endsWith(".set")) {
			name = name.substring(0, name.length() - 4);
		}
		f.setName(Font.NAME_FAMILY, name);
		
		return new BitmapFont[]{f};
	}
	
	private BitmapFont importFontImpl(DataInputStream in) throws IOException {
		byte[] data = new byte[768]; in.readFully(data);
		BitmapFont f = new BitmapFont(14, 2, 14, 2, 10, 14, 0, 14);
		for (int i = 0; i < 96; i++) {
			byte[][] glyph = new byte[16][16];
			for (int y = 0; y < 8; y++) {
				byte b = data[i * 8 + y];
				for (int m = 1, x = ((b < 0) ? 1 : 0); x < 14; x += 2, m <<= 1) {
					if ((b & m) != 0) {
						glyph[y * 2][x] = -1;
						glyph[y * 2][x + 1] = -1;
						glyph[y * 2 + 1][x] = -1;
						glyph[y * 2 + 1][x + 1] = -1;
					}
				}
			}
			f.putCharacter(i + 32, new BitmapFontGlyph(glyph, 0, 14, 14));
		}
		return f;
	}
}
