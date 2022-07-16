package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.unicode.data.GlyphList;

public class PSFBitmapFontExporter implements BitmapFontExporter {
	private int version;
	private GlyphList lowEncoding;
	private GlyphList highEncoding;
	private boolean useLowEncoding;
	private boolean useHighEncoding;
	private boolean useAllGlyphs;
	private boolean unicodeTable;
	private boolean gzip;
	
	public PSFBitmapFontExporter() {
		this.version = 2;
		this.lowEncoding = null;
		this.highEncoding = null;
		this.useLowEncoding = true;
		this.useHighEncoding = false;
		this.useAllGlyphs = true;
		this.unicodeTable = true;
		this.gzip = false;
	}
	
	public PSFBitmapFontExporter(boolean gzip) {
		this.version = 2;
		this.lowEncoding = null;
		this.highEncoding = null;
		this.useLowEncoding = true;
		this.useHighEncoding = false;
		this.useAllGlyphs = true;
		this.unicodeTable = true;
		this.gzip = gzip;
	}
	
	public PSFBitmapFontExporter(
		int version,
		GlyphList lowEncoding,
		GlyphList highEncoding,
		boolean useLowEncoding,
		boolean useHighEncoding,
		boolean useAllGlyphs,
		boolean unicodeTable
	) {
		this.version = version;
		this.lowEncoding = lowEncoding;
		this.highEncoding = highEncoding;
		this.useLowEncoding = useLowEncoding;
		this.useHighEncoding = useHighEncoding;
		this.useAllGlyphs = useAllGlyphs;
		this.unicodeTable = unicodeTable;
		this.gzip = false;
	}
	
	public PSFBitmapFontExporter(
		int version,
		GlyphList lowEncoding,
		GlyphList highEncoding,
		boolean useLowEncoding,
		boolean useHighEncoding,
		boolean useAllGlyphs,
		boolean unicodeTable,
		boolean gzip
	) {
		this.version = version;
		this.lowEncoding = lowEncoding;
		this.highEncoding = highEncoding;
		this.useLowEncoding = useLowEncoding;
		this.useHighEncoding = useHighEncoding;
		this.useAllGlyphs = useAllGlyphs;
		this.unicodeTable = unicodeTable;
		this.gzip = gzip;
	}
	
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		exportFontToStream(font, out);
		out.close();
		return out.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		if (gzip) {
			GZIPOutputStream gz = new GZIPOutputStream(os);
			exportFontImpl(font, new DataOutputStream(gz));
			gz.finish();
		} else {
			exportFontImpl(font, new DataOutputStream(os));
		}
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		exportFontToStream(font, out);
		out.close();
	}
	
	private void exportFontImpl(BitmapFont font, DataOutputStream out) throws IOException {
		ArrayList<Integer> codePoints = new ArrayList<Integer>();
		
		if (useLowEncoding || version < 2) {
			for (int i = 0; i < 256; i++) {
				codePoints.add((lowEncoding != null) ? lowEncoding.get(i & 0xFF) : i);
			}
		}
		if (useHighEncoding) {
			for (int i = 256; i < 512; i++) {
				codePoints.add((highEncoding != null) ? highEncoding.get(i & 0xFF) : i);
			}
		}
		if (useAllGlyphs && version >= 2) {
			TreeSet<Integer> remainder = new TreeSet<Integer>();
			remainder.addAll(font.characters(false).keySet());
			remainder.removeAll(codePoints);
			codePoints.addAll(remainder);
		}
		
		int a = font.getLineAscent();
		int h = a + font.getLineDescent() + font.getLineGap();
		int w = (version < 2) ? 8 : getMaxWidth(font, codePoints);
		boolean mode512 = (codePoints.size() > 256);
		
		// Header
		if (version < 2) {
			out.writeShort(0x3604);
			out.writeByte((mode512 ? 1 : 0) | (unicodeTable ? 2 : 0));
			out.writeByte(h);
		} else {
			out.writeInt(0x72B54A86);
			out.writeInt(0);
			out.writeInt(Integer.reverseBytes(32));
			out.writeInt(Integer.reverseBytes(unicodeTable ? 1 : 0));
			out.writeInt(Integer.reverseBytes(codePoints.size()));
			out.writeInt(Integer.reverseBytes(h * ((w + 7) / 8)));
			out.writeInt(Integer.reverseBytes(h));
			out.writeInt(Integer.reverseBytes(w));
		}
		
		// Bitmap Data
		for (int cp : codePoints) {
			writeGlyph(out, w, h, a, font.getCharacter(cp));
		}
		
		// Unicode Table
		if (unicodeTable) {
			for (int cp : codePoints) {
				if (cp >= 0) {
					String[] entries = {String.valueOf(Character.toChars(cp))};
					writeUnicodeEntry(out, version, entries);
				} else {
					writeUnicodeEntry(out, version, null);
				}
			}
		}
	}
	
	private int getMaxWidth(BitmapFont font, ArrayList<Integer> codePoints) {
		int maxWidth = 0;
		for (int cp : codePoints) {
			BitmapFontGlyph g = font.getCharacter(cp);
			if (g != null) {
				int w = g.getCharacterWidth();
				if (w > maxWidth) maxWidth = w;
			}
		}
		return maxWidth;
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
	
	private void writeUnicodeEntry(DataOutputStream out, int version, String[] entries) throws IOException {
		if (entries != null) {
			for (String s : entries) {
				if (s.length() == 1) {
					if (version < 2) {
						out.write(s.getBytes("UTF-16LE"));
					} else {
						out.write(s.getBytes("UTF-8"));
					}
				}
			}
			for (String s : entries) {
				if (s.length() > 1) {
					if (version < 2) {
						out.writeChar(Character.reverseBytes('\uFFFE'));
						out.write(s.getBytes("UTF-16LE"));
					} else {
						out.writeByte(0xFE);
						out.write(s.getBytes("UTF-8"));
					}
				}
			}
		}
		if (version < 2) {
			out.writeChar(0xFFFF);
		} else {
			out.writeByte(0xFF);
		}
	}
}
