package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import com.kreative.bitsnpicas.GlyphPair;
import com.kreative.bitsnpicas.VectorFont;
import com.kreative.bitsnpicas.VectorFontExporter;
import com.kreative.bitsnpicas.VectorFontGlyph;
import com.kreative.bitsnpicas.VectorPath;
import com.kreative.bitsnpicas.XMLUtility;

public class KpcaxVectorFontExporter implements VectorFontExporter {
	public byte[] exportFontToBytes(VectorFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		exportFont(font, pw);
		pw.close();
		out.close();
		return out.toByteArray();
	}
	
	public void exportFontToStream(VectorFont font, OutputStream os) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		exportFont(font, pw);
	}
	
	public void exportFontToFile(VectorFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		exportFont(font, pw);
		pw.close();
		out.close();
	}
	
	private void exportFont(VectorFont font, PrintWriter out) throws IOException {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE kpcas PUBLIC \"-//Kreative//DTD BitsNPicasVector 1.0//EN\" \"http://www.kreativekorp.com/dtd/kpcax.dtd\">");
		out.println("<kpcas>");
		for (Map.Entry<String,String> e : getProperties(font).entrySet()) {
			out.println(XMLUtility.wrap("prop", true, "id", e.getKey(), "value", e.getValue()));
		}
		for (Map.Entry<Integer,String> e : font.names(false).entrySet()) {
			out.println(XMLUtility.wrap("name", true, "id", e.getKey().toString(), "value", e.getValue()));
		}
		for (Map.Entry<Integer,VectorFontGlyph> e : font.characters(false).entrySet()) {
			out.println(encodeGlyph(e.getKey(), e.getValue()));
		}
		for (Map.Entry<String,VectorFontGlyph> e : font.namedGlyphs(false).entrySet()) {
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
		out.println("</kpcas>");
	}
	
	private static Map<String,String> getProperties(VectorFont font) {
		Map<String,String> props = new LinkedHashMap<String,String>();
		props.put("emAscent", Double.toString(font.getEmAscent2D()));
		props.put("emDescent", Double.toString(font.getEmDescent2D()));
		props.put("lineAscent", Double.toString(font.getLineAscent2D()));
		props.put("lineDescent", Double.toString(font.getLineDescent2D()));
		props.put("lineGap", Double.toString(font.getLineGap2D()));
		props.put("xHeight", Double.toString(font.getXHeight2D()));
		props.put("capHeight", Double.toString(font.getCapHeight2D()));
		props.put("newGlyphWidth", Double.toString(font.getNewGlyphWidth2D()));
		return props;
	}
	
	private static String encodeGlyph(Object id, VectorFontGlyph g) throws IOException {
		return XMLUtility.wrap(
			"g", true,
			((id instanceof Integer) ? "u" : "n"), id.toString(),
			"w", Integer.toString(g.getCharacterWidth()),
			"d", encodeVectorData(g.getContours())
		);
	}
	
	public static String encodeVectorData(Collection<VectorPath> paths) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (VectorPath path : paths) {
			if (first) first = false;
			else sb.append(" ZZ ");
			sb.append(path.toString());
		}
		return sb.toString();
	}
}
