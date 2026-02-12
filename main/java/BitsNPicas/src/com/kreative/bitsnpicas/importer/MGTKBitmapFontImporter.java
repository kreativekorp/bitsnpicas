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
import com.kreative.unicode.data.GlyphList;

public class MGTKBitmapFontImporter implements BitmapFontImporter {
	private GlyphList encoding;
	
	public MGTKBitmapFontImporter() {
		this.encoding = null;
	}
	
	public MGTKBitmapFontImporter(GlyphList encoding) {
		this.encoding = encoding;
	}
	
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
		if (name.toLowerCase().endsWith(".fnt")) {
			name = name.substring(0, name.length() - 4);
		} else if (name.toLowerCase().endsWith(".mgf")) {
			name = name.substring(0, name.length() - 4);
		} else if (name.toLowerCase().endsWith(".mpf")) {
			name = name.substring(0, name.length() - 4);
		}
		f.setName(Font.NAME_FAMILY, name);
		
		return new BitmapFont[]{f};
	}
	
	private BitmapFont importFontImpl(DataInputStream in) throws IOException {
		int fontType = in.readUnsignedByte();
		int maxWidth = (fontType == 0) ? 7 : (fontType == 0x80) ? 14 : 0;
		if (maxWidth == 0) throw new IOException("bad magic number");
		int lastChar = in.readUnsignedByte();
		int height = in.readUnsignedByte();
		if (height == 0) throw new IOException("bad magic number");
		int[] widths = new int[lastChar + 1];
		for (int i = 0; i <= lastChar; i++) {
			widths[i] = in.readUnsignedByte();
			if (widths[i] > maxWidth) throw new IOException("bad magic number");
		}
		byte[][][] glyphs = new byte[lastChar + 1][height][maxWidth];
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < maxWidth; z += 7) {
				for (int i = 0; i <= lastChar; i++) {
					int b = in.readUnsignedByte();
					for (int x = z, m = 1; m < 0x80; m <<= 1, x++) {
						if ((b & m) != 0) glyphs[i][y][x] = -1;
					}
				}
			}
		}
		BitmapFont f = new BitmapFont(height, 0, height, 0, 0, 0, 0, height);
		for (int i = 0; i <= lastChar; i++) {
			Integer cp = (encoding == null) ? i : encoding.get(i);
			if (cp == null || cp < 0) continue;
			BitmapFontGlyph g = new BitmapFontGlyph(glyphs[i], 0, widths[i], height);
			f.putCharacter(cp, g);
		}
		f.setAscentDescent();
		f.setCapHeight();
		f.setXHeight();
		return f;
	}
}
