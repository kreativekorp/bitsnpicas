package com.kreative.bitsnpicas.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.u8m.U8MFile;
import com.kreative.bitsnpicas.u8m.U8MGlyph;

public class U8MBitmapFontImporter implements BitmapFontImporter {
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		File file = File.createTempFile("u8m", ".u8m");
		FileOutputStream out = new FileOutputStream(file);
		out.write(data);
		out.flush();
		out.close();
		BitmapFont[] fonts = importFont(file);
		file.delete();
		return fonts;
	}
	
	@Override
	public BitmapFont[] importFont(InputStream is) throws IOException {
		File file = File.createTempFile("u8m", ".u8m");
		FileOutputStream out = new FileOutputStream(file);
		int read; byte[] buf = new byte[65536];
		while ((read = is.read(buf)) >= 0) out.write(buf, 0, read);
		out.flush();
		out.close();
		BitmapFont[] fonts = importFont(file);
		file.delete();
		return fonts;
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		for (long base = 0; base <= 16; base++) {
			raf.seek(base);
			if (raf.readInt() == U8MFile.MAGIC_NUMBER) {
				raf.seek(base);
				U8MFile u = new U8MFile();
				u.read(raf, base);
				raf.close();
				return importFont(u);
			}
		}
		raf.close();
		return new BitmapFont[0];
	}
	
	private static BitmapFont[] importFont(U8MFile u) {
		BitmapFont b = new BitmapFont();
		b.setName(Font.NAME_FAMILY, u.familyName);
		b.setName(Font.NAME_STYLE, fontStyleToString(u.style));
		b.autoFillNames();
		b.setLineAscent(u.lineAscent);
		b.setLineDescent(u.lineDescent);
		b.setLineGap(u.lineGap);
		double m = u.pointSize / (double)(u.lineAscent + u.lineDescent);
		b.setEmAscent2D(u.lineAscent * m);
		b.setEmDescent2D(u.lineDescent * m);
		
		for (int i = 0; i < 0x110000; i++) {
			int index = u.getUnicodeGlyphIndex(i);
			if (i == 0 || index != 0) {
				U8MGlyph ug = u.glyphs.get(index);
				BitmapFontGlyph g = new BitmapFontGlyph();
				g.setCharacterWidth(ug.advanceWidth);
				g.setXY(ug.xOffset, -ug.yOffset);
				g.setGlyph(ug.to2DArray());
				b.putCharacter(i, g);
			}
		}
		
		return new BitmapFont[]{b};
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
}
