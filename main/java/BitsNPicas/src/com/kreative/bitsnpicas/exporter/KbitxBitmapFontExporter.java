package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import com.kreative.bitsnpicas.Base64OutputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.GlyphPair;
import com.kreative.bitsnpicas.WIBOutputStream;
import com.kreative.bitsnpicas.XMLUtility;

public class KbitxBitmapFontExporter implements BitmapFontExporter {
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		exportFont(font, pw);
		pw.close();
		out.close();
		return out.toByteArray();
	}
	
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		exportFont(font, pw);
	}
	
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		exportFont(font, pw);
		pw.close();
		out.close();
	}
	
	private void exportFont(BitmapFont font, PrintWriter out) throws IOException {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE kbits PUBLIC \"-//Kreative//DTD BitsNPicasBitmap 1.0//EN\" \"http://www.kreativekorp.com/dtd/kbitx.dtd\">");
		out.println("<kbits>");
		for (Map.Entry<String,String> e : getProperties(font).entrySet()) {
			out.println(XMLUtility.wrap("prop", true, "id", e.getKey(), "value", e.getValue()));
		}
		for (Map.Entry<Integer,String> e : font.names(false).entrySet()) {
			out.println(XMLUtility.wrap("name", true, "id", e.getKey().toString(), "value", e.getValue()));
		}
		for (Map.Entry<Integer,BitmapFontGlyph> e : font.characters(false).entrySet()) {
			out.println(encodeGlyph(e.getKey(), e.getValue()));
		}
		for (Map.Entry<String,BitmapFontGlyph> e : font.namedGlyphs(false).entrySet()) {
			out.println(encodeGlyph(e.getKey(), e.getValue()));
		}
		for (Map.Entry<GlyphPair,Integer> e : font.kernPairs(false).entrySet()) {
			Object l = e.getKey().getLeft();
			Object r = e.getKey().getRight();
			out.println(XMLUtility.wrap(
				"k", true,
				((l instanceof Integer) ? "lu" : "ln"), l.toString(),
				((r instanceof Integer) ? "ru" : "rn"), r.toString(),
				"o", e.getValue().toString()
			));
		}
		out.println("</kbits>");
	}
	
	private static Map<String,String> getProperties(BitmapFont font) {
		Map<String,String> props = new LinkedHashMap<String,String>();
		props.put("emAscent", Integer.toString(font.getEmAscent()));
		props.put("emDescent", Integer.toString(font.getEmDescent()));
		props.put("lineAscent", Integer.toString(font.getLineAscent()));
		props.put("lineDescent", Integer.toString(font.getLineDescent()));
		props.put("lineGap", Integer.toString(font.getLineGap()));
		props.put("xHeight", Integer.toString(font.getXHeight()));
		props.put("capHeight", Integer.toString(font.getCapHeight()));
		props.put("newGlyphWidth", Integer.toString(font.getNewGlyphWidth()));
		return props;
	}
	
	private static String encodeGlyph(Object id, BitmapFontGlyph g) throws IOException {
		StringBuffer sb = new StringBuffer();
		Base64OutputStream bs = new Base64OutputStream(sb, false);
		byte[][] data = g.getGlyph();
		int h = data.length, w = 0;
		for (byte[] row : data) {
			if (w < row.length) {
				w = row.length;
			}
		}
		writeLEB128(bs, h);
		writeLEB128(bs, w);
		WIBOutputStream ws = new WIBOutputStream(bs);
		for (byte[] row : data) {
			ws.write(row);
			ws.write(new byte[w - row.length]);
		}
		ws.close();
		bs.close();
		return XMLUtility.wrap(
			"g", true,
			((id instanceof Integer) ? "u" : "n"), id.toString(),
			"x", Integer.toString(g.getX()),
			"y", Integer.toString(g.getY()),
			"w", Integer.toString(g.getCharacterWidth()),
			"d", sb.toString()
		);
	}
	
	private static void writeLEB128(OutputStream out, int v) throws IOException {
		while (v >= 0x80) {
			out.write(v | 0x80);
			v >>= 7;
		}
		out.write(v);
	}
}
