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
import com.kreative.unicode.data.GlyphList;

public class FONTXBitmapFontImporter implements BitmapFontImporter {
	private GlyphList singleByteEncoding;
	private String doubleByteEncoding;
	
	public FONTXBitmapFontImporter() {
		this.singleByteEncoding = null;
		this.doubleByteEncoding = "CP943"; // IBM's version of Shift-JIS
	}
	
	public FONTXBitmapFontImporter(GlyphList singleByteEncoding) {
		this.singleByteEncoding = singleByteEncoding;
		this.doubleByteEncoding = "CP943"; // IBM's version of Shift-JIS
	}
	
	public FONTXBitmapFontImporter(GlyphList singleByteEncoding, String doubleByteEncoding) {
		this.singleByteEncoding = singleByteEncoding;
		this.doubleByteEncoding = doubleByteEncoding;
	}
	
	public FONTXBitmapFontImporter(String doubleByteEncoding) {
		this.singleByteEncoding = null;
		this.doubleByteEncoding = doubleByteEncoding;
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
		return new BitmapFont[]{f};
	}
	
	private BitmapFont importFontImpl(DataInputStream in) throws IOException {
		if (in.readInt() != 0x464F4E54) throw new IOException("bad magic number");
		if (in.readShort() != 0x5832) throw new IOException("bad magic number");
		byte[] nameBytes = new byte[8]; in.readFully(nameBytes);
		String name = new String(nameBytes, "US-ASCII").trim();
		int w = in.readUnsignedByte();
		int h = in.readUnsignedByte();
		int flag = in.readByte();
		
		BitmapFont f = new BitmapFont(h, 0, h, 0, h, 0);
		f.setName(BitmapFont.NAME_FAMILY, name);
		
		if (flag == 0) {
			for (int i = 0; i < 256; i++) {
				BitmapFontGlyph g = readGlyph(in, w, h);
				int cp = fromSingleByte(i);
				f.putCharacter(cp, g);
			}
		} else {
			int n = in.readUnsignedByte();
			int[] bs = new int[n];
			int[] be = new int[n];
			for (int i = 0; i < n; i++) {
				bs[i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
				be[i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
			}
			for (int i = 0; i < n; i++) {
				for (int j = bs[i]; j <= be[i]; j++) {
					BitmapFontGlyph g = readGlyph(in, w, h);
					int cp = fromDoubleByte(j);
					f.putCharacter(cp, g);
				}
			}
		}
		
		f.setAscentDescent();
		f.setXHeight();
		return f;
	}
	
	private BitmapFontGlyph readGlyph(DataInputStream in, int w, int h) throws IOException {
		byte[][] data = new byte[h][w];
		for (int y = 0; y < h; y++) {
			int x = 0;
			while (x < w) {
				int b = in.readByte();
				for (int m = 0x80; x < w && m != 0; x++, m >>= 1) {
					if ((b & m) != 0) {
						data[y][x] = -1;
					}
				}
			}
		}
		return new BitmapFontGlyph(data, 0, w, h);
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
