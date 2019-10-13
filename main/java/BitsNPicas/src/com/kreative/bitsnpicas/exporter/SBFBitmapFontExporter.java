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
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class SBFBitmapFontExporter implements BitmapFontExporter {
	private EncodingTable encoding;
	
	public SBFBitmapFontExporter() {
		this.encoding = null;
	}
	
	public SBFBitmapFontExporter(EncodingTable encoding) {
		this.encoding = encoding;
	}
	
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
		int[] offset = new int[256];
		int currentOffset = 2048;
		boolean first = true;
		int width = 0;
		int height = font.getLineAscent() + font.getLineDescent() + font.getLineGap();
		for (int ch = 0; ch < 256; ch++) {
			offset[ch] = currentOffset;
			int cp = (encoding != null) ? encoding.get(ch) : fromSuperLatin(ch);
			BitmapFontGlyph g = font.getCharacter(cp);
			if (g != null) {
				currentOffset += bitsToBytes(g.getGlyphWidth()) * g.getGlyphHeight();
				if (first) {
					first = false;
					width = g.getCharacterWidth();
				} else {
					if (width != 0) {
						if (width != g.getCharacterWidth()) {
							width = 0;
						}
					}
				}
			}
		}
		
		out.writeInt(0x10B17E5C);
		out.writeByte(width);
		out.writeByte(height);
		out.writeByte(font.getLineAscent());
		out.writeByte(font.getLineDescent());
		
		for (int ch = 1; ch < 256; ch++) {
			int cp = (encoding != null) ? encoding.get(ch) : fromSuperLatin(ch);
			BitmapFontGlyph g = font.getCharacter(cp);
			if (g == null) {
				out.writeLong(0);
			} else {
				int byteWidth = bitsToBytes(g.getGlyphWidth());
				int overhang = (byteWidth << 3) - g.getGlyphWidth();
				out.writeShort(Short.reverseBytes((short)offset[ch]));
				out.writeByte(byteWidth);
				out.writeByte(g.getGlyphHeight());
				out.writeByte(g.getGlyphOffset());
				out.writeByte(g.getGlyphAscent());
				out.writeByte(g.getCharacterWidth());
				out.writeByte(overhang);
			}
		}
		
		for (int ch = 0; ch < 256; ch++) {
			int cp = (encoding != null) ? encoding.get(ch) : fromSuperLatin(ch);
			BitmapFontGlyph g = font.getCharacter(cp);
			if (g != null) {
				for (byte[] row : g.getGlyph()) {
					byte[] newrow = new byte[bitsToBytes(row.length)];
					int i = 0;
					int m = 0x80;
					for (byte col : row) {
						if (col < 0) {
							newrow[i] |= m;
						}
						m >>= 1;
						if (m == 0) {
							i++;
							m = 0x80;
						}
					}
					out.write(newrow);
				}
			}
		}
	}
	
	private static final int[] C0 = {
		0x0000, 0x02CB, 0x02DD, 0x02D9, 0x02DA, 0x02C7, 0x02D8, 0x02DB,
		0x0008, 0x0009, 0x000A, 0x000B, 0x000C, 0x000D, 0xFB01, 0xFB02,
		0xF8FF, 0x2044, 0x221A, 0x2211, 0x220F, 0x222B, 0x2206, 0x03A9,
		0x03C0, 0x2202, 0x221E, 0x001B, 0x2264, 0x2260, 0x2265, 0x2248,
	};
	
	private static final int[] C1 = {
		0x20AC, 0x25CA, 0x201A, 0x0192, 0x201E, 0x2026, 0x2020, 0x2021,
		0x02C6, 0x2030, 0x0160, 0x2039, 0x0152, 0x0141, 0x017D, 0x0131,
		0x2318, 0x2018, 0x2019, 0x201C, 0x201D, 0x2022, 0x2013, 0x2014,
		0x02DC, 0x2122, 0x0161, 0x203A, 0x0153, 0x0142, 0x017E, 0x0178,
	};
	
	private static int fromSuperLatin(int ch) {
		ch &= 0xFF;
		if (ch < 0x20) {
			return C0[ch];
		} else if (ch < 0x80) {
			return ch;
		} else if (ch < 0xA0) {
			return C1[ch & 0x1F];
		} else {
			return ch;
		}
	}
	
	private static int bitsToBytes(int bits) {
		int bytes = bits >> 3;
		if ((bits & 7) != 0) bytes++;
		return bytes;
	}
}
