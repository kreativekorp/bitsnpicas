package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class FONTXBitmapFontExporter implements BitmapFontExporter {
	private int flag;
	private EncodingTable singleByteEncoding;
	private String doubleByteEncoding;
	
	public FONTXBitmapFontExporter(boolean isDoubleByte) {
		this.flag = isDoubleByte ? 1 : 0;
		this.singleByteEncoding = null;
		this.doubleByteEncoding = "CP943"; // IBM's version of Shift-JIS
	}
	
	public FONTXBitmapFontExporter(EncodingTable singleByteEncoding) {
		this.flag = 0;
		this.singleByteEncoding = singleByteEncoding;
		this.doubleByteEncoding = "CP943"; // IBM's version of Shift-JIS
	}
	
	public FONTXBitmapFontExporter(String doubleByteEncoding) {
		this.flag = 1;
		this.singleByteEncoding = null;
		this.doubleByteEncoding = doubleByteEncoding;
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
		out.writeInt(0x464F4E54);
		out.writeShort(0x5832);
		String name = font.getName(BitmapFont.NAME_FAMILY);
		byte[] nameBytes = (name != null) ? name.getBytes("US-ASCII") : new byte[0];
		for (int i = 0; i < nameBytes.length && i < 8; i++) out.writeByte(nameBytes[i]);
		for (int i = nameBytes.length; i < 8; i++) out.writeByte(0x20);
		
		int a = font.getLineAscent();
		int h = a + font.getLineDescent() + font.getLineGap();
		if (flag == 0) {
			int maxWidth = 0;
			BitmapFontGlyph[] glyphs = new BitmapFontGlyph[256];
			for (int i = 0; i < 256; i++) {
				BitmapFontGlyph g = font.getCharacter(fromSingleByte(i));
				if (g != null) {
					int w = g.getCharacterWidth();
					if (w > maxWidth) maxWidth = w;
					glyphs[i] = g;
				}
			}
			out.writeByte(maxWidth);
			out.writeByte(h);
			out.writeByte(flag);
			for (BitmapFontGlyph g : glyphs) {
				writeGlyph(out, maxWidth, h, a, g);
			}
		} else {
			int bp = -1;
			int[] bs = new int[256];
			int[] be = new int[256];
			int maxWidth = 0;
			LinkedList<BitmapFontGlyph> glyphs = new LinkedList<BitmapFontGlyph>();
			for (int i = 0; i < 65536; i++) {
				BitmapFontGlyph g = font.getCharacter(fromDoubleByte(i));
				if (g != null) {
					if (bp < 0 || (be[bp] + 1) != i) {
						if (bp >= 254) break;
						bp++;
						bs[bp] = i;
					}
					be[bp] = i;
					int w = g.getCharacterWidth();
					if (w > maxWidth) maxWidth = w;
					glyphs.add(g);
				}
			}
			out.writeByte(maxWidth);
			out.writeByte(h);
			out.writeByte(flag);
			out.writeByte(bp + 1);
			for (int i = 0; i <= bp; i++) {
				out.writeShort(Short.reverseBytes((short)bs[i]));
				out.writeShort(Short.reverseBytes((short)be[i]));
			}
			for (BitmapFontGlyph g : glyphs) {
				writeGlyph(out, maxWidth, h, a, g);
			}
		}
	}
	
	private void writeGlyph(DataOutputStream out, int w, int h, int a, BitmapFontGlyph g) throws IOException {
		if (g == null) {
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x += 8) {
					out.writeByte(0);
				}
			}
		} else {
			byte[][] data = g.getGlyph();
			for (int j = g.getGlyphAscent() - a, y = 0; y < h; y++, j++) {
				if (j >= 0 && j < data.length) {
					int i = -g.getGlyphOffset(), x = 0;
					while (x < w) {
						int b = 0;
						for (int m = 0x80; m != 0 && x < w; m >>= 1, x++, i++) {
							if (i >= 0 && i < data[j].length) {
								if (data[j][i] < 0) b |= m;
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
		}
	}
	
	private int fromSingleByte(int i) {
		if (singleByteEncoding != null) {
			int cp = singleByteEncoding.get(i);
			if (cp >= 0) return cp;
		}
		return 0xF000 + i;
	}
	
	private int fromDoubleByte(int i) {
		if (doubleByteEncoding != null) {
			try {
				byte[] b = { (byte)(i >> 8), (byte)i };
				String s = new String(b, doubleByteEncoding);
				if (s.codePointCount(0, s.length()) == 1) {
					int cp = s.codePointAt(0);
					if (cp != 0xFFFD) return cp;
				}
			} catch (IOException e) {
				// Ignored.
			}
		}
		return 0xF0000 + i;
	}
}
