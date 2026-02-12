package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;
import com.kreative.unicode.data.GlyphList;

public class PSFBitmapFontImporter implements BitmapFontImporter {
	private GlyphList lowEncoding;
	private GlyphList highEncoding;
	private int puaBase;
	private boolean gzip;
	
	public PSFBitmapFontImporter() {
		this.lowEncoding = null;
		this.highEncoding = null;
		this.puaBase = -1;
		this.gzip = false;
	}
	
	public PSFBitmapFontImporter(boolean gzip) {
		this.lowEncoding = null;
		this.highEncoding = null;
		this.puaBase = -1;
		this.gzip = gzip;
	}
	
	public PSFBitmapFontImporter(GlyphList low, GlyphList high, int puaBase) {
		this.lowEncoding = low;
		this.highEncoding = high;
		this.puaBase = puaBase;
		this.gzip = false;
	}
	
	public PSFBitmapFontImporter(GlyphList low, GlyphList high, int puaBase, boolean gzip) {
		this.lowEncoding = low;
		this.highEncoding = high;
		this.puaBase = puaBase;
		this.gzip = gzip;
	}
	
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		InputStream in = new ByteArrayInputStream(data);
		if (gzip) in = new GZIPInputStream(in);
		BitmapFont f = importFontImpl(new DataInputStream(in));
		in.close();
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(InputStream in) throws IOException {
		if (gzip) in = new GZIPInputStream(in);
		BitmapFont f = importFontImpl(new DataInputStream(in));
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		if (gzip) in = new GZIPInputStream(in);
		BitmapFont f = importFontImpl(new DataInputStream(in));
		in.close();
		
		String name = file.getName();
		if (name.toLowerCase().endsWith(".psf")) {
			name = name.substring(0, name.length() - 4);
		} else if (name.toLowerCase().endsWith(".psfu")) {
			name = name.substring(0, name.length() - 5);
		} else if (name.toLowerCase().endsWith(".psf.gz")) {
			name = name.substring(0, name.length() - 7);
		} else if (name.toLowerCase().endsWith(".psfu.gz")) {
			name = name.substring(0, name.length() - 8);
		}
		f.setName(Font.NAME_FAMILY, name);
		
		return new BitmapFont[]{f};
	}
	
	private BitmapFont importFontImpl(DataInputStream in) throws IOException {
		int version    = readVersion(in);
		int headerSize = (version < 2) ? 4                        : Integer.reverseBytes(in.readInt());
		int flags      = (version < 2) ? in.readUnsignedByte()    : Integer.reverseBytes(in.readInt());
		int numGlyphs  = (version < 2) ? (((flags&1)==0)?256:512) : Integer.reverseBytes(in.readInt());
		int charSize   = (version < 2) ? in.readUnsignedByte()    : Integer.reverseBytes(in.readInt());
		int height     = (version < 2) ? charSize                 : Integer.reverseBytes(in.readInt());
		int width      = (version < 2) ? 8                        : Integer.reverseBytes(in.readInt());
		
		if (version >= 2 && headerSize > 32) in.readFully(new byte[headerSize - 32]);
		
		byte[][][] glyphs = new byte[numGlyphs][][];
		byte[] data = new byte[charSize];
		for (int i = 0; i < numGlyphs; i++) {
			in.readFully(data);
			glyphs[i] = new byte[height][width];
			for (int j = 0, y = 0; y < height; y++) {
				for (int x = 0; x < width; j++) {
					for (int m = 0x80; x < width && m != 0; x++, m >>= 1) {
						if ((data[j] & m) != 0) {
							glyphs[i][y][x] = -1;
						}
					}
				}
			}
		}
		
		HashMap<String,Integer> unicodeTable = new HashMap<String,Integer>();
		if ((flags & ((version < 2) ? 2 : 1)) != 0) {
			for (int i = 0; i < numGlyphs; i++) {
				String[] entry = readUnicodeEntry(in, version);
				for (String s : entry) unicodeTable.put(s, i);
			}
		}
		
		BitmapFont f = new BitmapFont(height, 0, height, 0, height, height, 0, width);
		
		if (lowEncoding != null) {
			for (int i = 0; i < 256 && i < numGlyphs; i++) {
				Integer cp = lowEncoding.get(i & 0xFF);
				if (cp != null && cp >= 0) {
					f.putCharacter(cp, new BitmapFontGlyph(glyphs[i], 0, width, height));
				}
			}
		}
		if (highEncoding != null) {
			for (int i = 256; i < 512 && i < numGlyphs; i++) {
				Integer cp = highEncoding.get(i & 0xFF);
				if (cp != null && cp >= 0) {
					f.putCharacter(cp, new BitmapFontGlyph(glyphs[i], 0, width, height));
				}
			}
		}
		if (puaBase >= 0) {
			for (int i = 0; i < numGlyphs; i++) {
				f.putCharacter(puaBase + i, new BitmapFontGlyph(glyphs[i], 0, width, height));
			}
		}
		for (HashMap.Entry<String,Integer> e : unicodeTable.entrySet()) {
			String key = e.getKey();
			if (key.codePointCount(0, key.length()) == 1) {
				int cp = key.codePointAt(0);
				int i = e.getValue().intValue();
				f.putCharacter(cp, new BitmapFontGlyph(glyphs[i], 0, width, height));
			}
		}
		if (f.isEmpty()) {
			for (int i = 0; i < numGlyphs; i++) {
				f.putCharacter(i, new BitmapFontGlyph(glyphs[i], 0, width, height));
			}
		}
		
		f.setAscentDescent();
		f.setXHeight();
		f.setCapHeight();
		return f;
	}
	
	private int readVersion(DataInputStream in) throws IOException {
		int m1 = in.readUnsignedShort();
		if (m1 == 0x3604) return 1;
		if (m1 == 0x72B5) {
			int m2 = in.readUnsignedShort();
			if (m2 == 0x4A86) {
				int m3 = in.readInt();
				if (m3 == 0) return 2;
				throw new IOException("bad magic number m3: " + m3);
			}
			throw new IOException("bad magic number m2: " + m2);
		}
		throw new IOException("bad magic number m1: " + m1);
	}
	
	private String[] readUnicodeEntry(DataInputStream in, int version) throws IOException {
		if (version < 2) {
			StringBuffer sb = new StringBuffer();
			while (true) {
				char ch = Character.reverseBytes(in.readChar());
				if (ch == (char)0xFFFF) break;
				sb.append(ch);
			}
			String s = sb.toString();
			return splitUnicodeEntry(s);
		} else {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while (true) {
				int b = in.readUnsignedByte();
				if (b == 0xFF) break;
				if (b == 0xFE) {
					out.write(0xEF);
					out.write(0xBF);
					out.write(0xBE);
				} else {
					out.write(b);
				}
			}
			byte[] b = out.toByteArray();
			String s = new String(b, "UTF-8");
			return splitUnicodeEntry(s);
		}
	}
	
	private String[] splitUnicodeEntry(String s) {
		ArrayList<String> a = new ArrayList<String>();
		String[] pieces = s.split("\uFFFE+");
		for (int i = 0, n = pieces[0].length(); i < n;) {
			int cp = pieces[0].codePointAt(i);
			a.add(String.valueOf(Character.toChars(cp)));
			i += Character.charCount(cp);
		}
		for (int i = 1, n = pieces.length; i < n; i++) {
			a.add(pieces[i]);
		}
		return a.toArray(new String[a.size()]);
	}
}
