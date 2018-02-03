package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class FZXBitmapFontExporter implements BitmapFontExporter {
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		exportFontToStream(font, os);
		os.close();
		return os.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		int ascent = font.getLineAscent();
		int height = ascent + font.getLineDescent();
		int tracking = 255;
		int lastchar = 255;
		while (lastchar > 32 && !font.containsCharacter(zxcp(lastchar))) lastchar--;
		for (int ch = 32; ch <= lastchar; ch++) {
			BitmapFontGlyph g = font.getCharacter(zxcp(ch));
			if (g != null) {
				int rsb = g.getCharacterWidth() - (g.getGlyphOffset() + g.getGlyphWidth());
				if (rsb <= 0) { tracking = 0; break; }
				else if (rsb < tracking) tracking = rsb;
			}
		}
		if (height < 1) height = 1;
		if (height > 255) height = 255;
		
		int end = (lastchar - 30) * 3;
		int ptr = end + 2;
		int[] offset = new int[lastchar - 31];
		int[] kern = new int[lastchar - 31];
		int[] shift = new int[lastchar - 31];
		int[] width = new int[lastchar - 31];
		byte[][] cdef = new byte[lastchar - 31][];
		for (int i = 0, ch = 32; ch <= lastchar; ch++, i++) {
			BitmapFontGlyph g = font.getCharacter(zxcp(ch));
			if (g == null) {
				offset[i] = ptr;
				kern[i] = 0;
				shift[i] = 0;
				width[i] = 1;
				cdef[i] = new byte[0];
			} else {
				int lsb = g.getGlyphOffset();
				int tsb = ascent - g.getGlyphAscent();
				int rsb = g.getCharacterWidth() - (lsb + g.getGlyphWidth());
				offset[i] = ptr;
				kern[i] = (lsb <= -3) ? 3 : (lsb >= 0) ? 0 : -lsb;
				shift[i] = (tsb <= 0) ? 0 : (tsb >= 15) ? 15 : tsb;
				int kernExtra = lsb + kern[i];
				int shiftExtra = tsb - shift[i];
				int widthExtra = rsb - tracking;
				int zxw = g.getGlyphWidth() + kernExtra + widthExtra;
				int zxh = g.getGlyphHeight() + shiftExtra;
				int bpr = (zxw <= 8) ? 1 : 2;
				width[i] = (zxw <= 1) ? 1 : (zxw >= 16) ? 16 : zxw;
				cdef[i] = new byte[zxh * bpr];
				boolean cdefChanged = false;
				byte[][] gd = g.getGlyph();
				for (int gdy = -shiftExtra, cdy = 0, zxy = 0; zxy < zxh; zxy++, cdy += bpr, gdy++) {
					if (gdy >= 0 && gdy < gd.length) {
						for (int gdx = -kernExtra, cdx = 0x8000, zxx = 0; zxx < width[i]; zxx++, cdx >>= 1, gdx++) {
							if (gdx >= 0 && gdx < gd[gdy].length && gd[gdy][gdx] < 0) {
								if (cdx < 0x100) cdef[i][cdy + 1] |= cdx;
								else cdef[i][cdy] |= (cdx >> 8);
								cdefChanged = true;
							}
						}
					}
				}
				if (cdefChanged) {
					ptr += cdef[i].length;
				} else {
					kern[i] = 0;
					shift[i] = 0;
					cdef[i] = new byte[0];
				}
			}
		}
		
		os.write(height);
		os.write(tracking);
		os.write(lastchar);
		for (int o = 3, i = 0, ch = 32; ch <= lastchar; ch++, i++, o += 3) {
			int ok = ((offset[i] - o) & 0x3FFF) | (kern[i] << 14);
			int sw = (shift[i] << 4) | ((width[i] - 1) & 0x0F);
			os.write(ok);
			os.write(ok >> 8);
			os.write(sw);
		}
		os.write(ptr - end);
		os.write((ptr - end) >> 8);
		for (byte[] cd : cdef) os.write(cd);
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		exportFontToStream(font, os);
		os.close();
	}
	
	private int zxcp(int ch) {
		if (ch == 96) return 163;
		if (ch == 127) return 169;
		if (ch < 128) return ch;
		return (0xF000 + ch);
	}
}
