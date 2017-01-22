package com.kreative.bitsnpicas.truetype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SvgTableEntry implements Comparable<SvgTableEntry> {
	public int startGlyphID = 0;
	public int endGlyphID = 0;
	public byte[] svgDocument = new byte[0];
	
	public boolean isCompressed() {
		return svgDocument.length >= 18
		    && svgDocument[0] == (byte)0x1F
		    && svgDocument[1] == (byte)0x8B;
	}
	
	public InputStream getInputStream() throws IOException {
		InputStream in = new ByteArrayInputStream(svgDocument);
		if (isCompressed()) in = new GZIPInputStream(in);
		return in;
	}
	
	public OutputStream getOutputStream(boolean compressed) throws IOException {
		// Note: Microsoft claims the SVG-in-OpenType specification supports
		// gzip-compressed SVG glyphs, and the availability of a version of
		// EmojiOne with gzip-compressed SVG glyphs appears to back this up.
		// However, I could not find any mention of gzip support in the W3C
		// specification, and I was unable to get fonts with gzip-compressed
		// SVG glyphs to work even in the latest version of Firefox (50.1.0).
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final OutputStream os = compressed ? new GZIPOutputStream(bos) : bos;
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				os.write(b);
			}
			@Override
			public void write(byte[] b) throws IOException {
				os.write(b);
			}
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				os.write(b, off, len);
			}
			@Override
			public void flush() throws IOException {
				os.flush();
				bos.flush();
			}
			@Override
			public void close() throws IOException {
				os.close();
				bos.close();
				svgDocument = bos.toByteArray();
			}
		};
	}
	
	@Override
	public int compareTo(SvgTableEntry that) {
		return this.startGlyphID - that.startGlyphID;
	}
}
