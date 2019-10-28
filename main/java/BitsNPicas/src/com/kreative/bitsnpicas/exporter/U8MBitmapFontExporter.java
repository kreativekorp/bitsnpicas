package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.u8m.U8MFile;
import com.kreative.bitsnpicas.u8m.U8MGlyph;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class U8MBitmapFontExporter implements BitmapFontExporter {
	private Integer loadAddress;
	private EncodingTable nativeEncoding;
	
	public U8MBitmapFontExporter() {
		this.loadAddress = null;
		this.nativeEncoding = null;
	}
	
	public U8MBitmapFontExporter(Integer loadAddress) {
		this.loadAddress = loadAddress;
		this.nativeEncoding = null;
	}
	
	public U8MBitmapFontExporter(EncodingTable nativeEncoding) {
		this.loadAddress = null;
		this.nativeEncoding = nativeEncoding;
	}
	
	public U8MBitmapFontExporter(Integer loadAddress, EncodingTable nativeEncoding) {
		this.loadAddress = loadAddress;
		this.nativeEncoding = nativeEncoding;
	}
	
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		exportFontToStream(font, os);
		os.close();
		return os.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		File file = File.createTempFile("u8m", ".u8m");
		exportFontToFile(font, file);
		FileInputStream in = new FileInputStream(file);
		int read; byte[] buf = new byte[65536];
		while ((read = in.read(buf)) >= 0) os.write(buf, 0, read);
		in.close();
		file.delete();
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		U8MFile u = new U8MFile();
		u.familyName = font.getName(Font.NAME_FAMILY);
		u.familyID = (int)(((u.familyName.hashCode() & 0xFFFFFFFFL) % 65535L) + 1L);
		u.style = font.getMacStyle();
		u.pointSize = font.getEmAscent() + font.getEmDescent();
		u.lineAscent = font.getLineAscent();
		u.lineDescent = font.getLineDescent();
		u.lineGap = font.getLineGap();
		u.lineHeight = u.lineAscent + u.lineDescent + u.lineGap;
		u.createMap(); // map zero which must be the empty map
		u.createGlyph(); // glyph zero which must be the notdef glyph
		
		for (int i = 0; i < 0x110000; i++) {
			if (font.containsCharacter(i)) {
				BitmapFontGlyph g = font.getCharacter(i);
				int index = (i == 0) ? 0 : u.createGlyph();
				U8MGlyph ug = u.glyphs.get(index);
				ug.advanceWidth = g.getCharacterWidth();
				ug.yOffset = -g.getGlyphAscent();
				ug.xOffset = g.getGlyphOffset();
				ug.from2DArray(g.getGlyph());
				if (i != 0) u.setUnicodeGlyphIndex(i, index);
			}
		}
		
		if (nativeEncoding != null) {
			for (int i = 0; i < 256; i++) {
				int cp = nativeEncoding.get(i);
				if (cp > 0) {
					int index = u.getUnicodeGlyphIndex(cp);
					if (index > 0) {
						u.setNativeGlyphIndex(i, index);
					}
				}
			}
		}
		
		RandomAccessFile raf = new RandomAccessFile(file, "rwd");
		raf.setLength(0);
		if (loadAddress != null) {
			raf.writeShort(Short.reverseBytes(loadAddress.shortValue()));
			u.write(raf, 0, 2);
		} else {
			u.write(raf, 0, 0);
		}
		raf.close();
	}
}
