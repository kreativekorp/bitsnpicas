package com.kreative.bitsnpicas.importer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;

public class BinaryBitmapFontImporter implements BitmapFontImporter {
	public int offset = 0, bytesPerChar = 8, bytesPerRow = 1;
	public int cellWidth = 8, cellHeight = 8, ascent = 7;
	public int bitsPerPixel = 1;
	public boolean invert = false, rightAlign = false;
	public boolean flipBits = false, flipBytes = false;
	public int cellCount = 128;
	public List<Integer> encoding = null;
	
	public List<BufferedImage> preview(byte[] b) {
		List<BufferedImage> images = new ArrayList<BufferedImage>();
		int[] rgb = new int[cellWidth * cellHeight];
		for (int off = offset, i = 0; i < cellCount; i++, off += bytesPerChar) {
			BufferedImage image = new BufferedImage(cellWidth, cellHeight, BufferedImage.TYPE_INT_ARGB);
			for (int ro = 0, yo = off, y = 0; y < cellHeight; y++, yo += bytesPerRow, ro += cellWidth) {
				byte[] row = unpackRow(b, yo);
				for (int x = 0; x < cellWidth; x++) {
					int k = 255 - (row[x] & 0xFF);
					rgb[ro + x] = (0xFF << 24) | (k << 16) | (k << 8) | (k << 0);
					if (y == ascent) rgb[ro + x] ^= 0x00FFFF;
				}
			}
			image.setRGB(0, 0, cellWidth, cellHeight, rgb, 0, cellWidth);
			images.add(image);
		}
		return images;
	}
	
	public BitmapFont[] importFont(byte[] b) {
		BitmapFont bm = new BitmapFont(ascent, cellHeight - ascent, ascent, cellHeight - ascent, 0, 0, 0);
		for (int off = offset, i = 0; i < cellCount; i++, off += bytesPerChar) {
			byte[][] gd = new byte[cellHeight][];
			for (int yo = off, y = 0; y < cellHeight; y++, yo += bytesPerRow) {
				gd[y] = unpackRow(b, yo);
			}
			BitmapFontGlyph glyph = new BitmapFontGlyph(gd, 0, cellWidth, ascent);
			if (encoding == null || encoding.isEmpty()) {
				bm.putCharacter(0xF0000 + i, glyph);
			} else if (i < encoding.size()) {
				Integer e = encoding.get(i);
				if (e != null && e.intValue() >= 0) {
					bm.putCharacter(e.intValue(), glyph);
				}
			} else {
				bm.putCharacter(0xF0000 + i - encoding.size(), glyph);
			}
		}
		bm.setXHeight();
		bm.setCapHeight();
		return new BitmapFont[]{bm};
	}
	
	public BitmapFont[] importFont(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1048576]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.close();
		return importFont(out.toByteArray());
	}
	
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1048576]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.close();
		in.close();
		return importFont(out.toByteArray());
	}
	
	private byte[] unpackRow(byte[] b, int off) {
		byte[] row = new byte[cellWidth];
		int mask = ((1 << bitsPerPixel) - 1);
		int bits = cellWidth * bitsPerPixel;
		int bytes = (bits + 7) / 8;
		int bit = rightAlign ? (bytes * 8 - bits) : 0;
		for (int i = 0; i < cellWidth; i++, bit += bitsPerPixel) {
			int value = 0;
			int m = (1 << (bitsPerPixel - 1));
			for (int j = 0; j < bitsPerPixel; j++, m >>= 1) {
				int bytei = (bit + j) / 8; if (flipBytes) bytei = (bytes - 1) - bytei;
				int biti = (bit + j) % 8; if (!flipBits) biti = 7 - biti;
				if (off + bytei < b.length && ((b[off + bytei] >> biti) & 1) != 0) value |= m;
			}
			if (invert) value ^= mask;
			row[i] = (byte)(255 * value / mask);
		}
		return row;
	}
}
