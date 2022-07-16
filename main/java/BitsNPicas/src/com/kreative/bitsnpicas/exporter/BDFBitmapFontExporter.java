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

import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;

public class BDFBitmapFontExporter implements BitmapFontExporter {
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		exportFont(font, pw);
		pw.close();
		out.close();
		return out.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		exportFont(font, pw);
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		exportFont(font, pw);
		pw.close();
		out.close();
	}
	
	private void exportFont(BitmapFont font, PrintWriter pw) {
		int cnt = 0;
		int bbl = 0;
		int bbr = 0;
		int bbt = 0;
		int bbb = 0;
		for (int i = 0; i < 0x110000; i++) {
			if (font.containsCharacter(i)) {
				cnt++;
				BitmapFontGlyph g = font.getCharacter(i);
				if (g.getGlyphOffset() < bbl) {
					bbl = g.getGlyphOffset();
				}
				if (g.getGlyphOffset() + g.getGlyphWidth() > bbr) {
					bbr = g.getGlyphOffset() + g.getGlyphWidth();
				}
				if (g.getGlyphAscent() > bbt) {
					bbt = g.getGlyphAscent();
				}
				if (g.getGlyphDescent() > bbb) {
					bbb = g.getGlyphDescent();
				}
			}
		}
		
		Map<String,String> props = new LinkedHashMap<String,String>();
		if (font.containsName(Font.NAME_FAMILY)) props.put("FAMILY_NAME", font.getName(Font.NAME_FAMILY));
		if (font.containsName(Font.NAME_STYLE)) props.put("WEIGHT_NAME", font.getName(Font.NAME_STYLE));
		if (font.containsName(Font.NAME_VERSION)) props.put("FONT_VERSION", font.getName(Font.NAME_VERSION));
		if (font.containsName(Font.NAME_COPYRIGHT)) props.put("COPYRIGHT", font.getName(Font.NAME_COPYRIGHT));
		if (font.containsName(Font.NAME_MANUFACTURER)) props.put("FOUNDRY", font.getName(Font.NAME_MANUFACTURER));
		props.put("FONT_ASCENT", Integer.toString(font.getLineAscent()));
		props.put("FONT_DESCENT", Integer.toString(font.getLineDescent()));
		props.put("POINT_SIZE", Integer.toString(font.getEmAscent() + font.getEmDescent()));
		props.put("X_HEIGHT", Integer.toString(font.getXHeight()));
		props.put("CAP_HEIGHT", Integer.toString(font.getCapHeight()));
		
		pw.println("STARTFONT 2.1");
		pw.println("FONT -" + font.getName(Font.NAME_FAMILY) + "-" + font.getName(Font.NAME_STYLE) + "-" + (font.isItalicStyle() ? "I" : "R") + "-" + font.getName(Font.NAME_STYLE) + "--" + (font.getLineAscent() + font.getLineDescent()) + "-" + (font.getLineAscent() + font.getLineDescent()) + "-75-75-c-80-iso10646-1");
		pw.println("SIZE " + (font.getLineAscent() + font.getLineDescent()) + " 75 75");
		pw.println("FONTBOUNDINGBOX " + (bbr-bbl) + " " + (bbt+bbb) + " " + bbl + " " + (-bbb));
		pw.println("STARTPROPERTIES " + props.size());
		for (Map.Entry<String,String> e : props.entrySet()) {
			pw.println(e.getKey() + " " + e.getValue());
		}
		pw.println("ENDPROPERTIES");
		pw.println("CHARS " + cnt);
		for (int i = 0; i < 0x110000; i++) {
			if (font.containsCharacter(i)) {
				BitmapFontGlyph g = font.getCharacter(i);
				String h = Integer.toHexString(i).toUpperCase();
				while (h.length() < 4) h = "0" + h;
				pw.println("STARTCHAR U+" + h);
				pw.println("ENCODING " + i);
				pw.println("SWIDTH " + (1000 * g.getCharacterWidth() / (bbr-bbl)) + " 0");
				pw.println("DWIDTH " + g.getCharacterWidth() + " 0");
				pw.println("BBX " + g.getGlyphWidth() + " " + g.getGlyphHeight() + " " + g.getGlyphOffset() + " " + (-g.getGlyphDescent()));
				pw.println("BITMAP");
				for (byte[] row : g.getGlyph()) {
					StringBuffer s = new StringBuffer();
					for (int col = 0; col < row.length; col += 8) {
						int b = 0;
						for (int c = 0; c < 8; c++) {
							b <<= 1;
							if (col+c < row.length && row[col+c] < 0) {
								b |= 1;
							}
						}
						String bh = "00" + Integer.toHexString(b).toUpperCase();
						bh = bh.substring(bh.length() - 2);
						s.append(bh);
					}
					if (s.length() == 0) s.append("00");
					pw.println(s.toString());
				}
				pw.println("ENDCHAR");
			}
		}
		pw.println("ENDFONT");
	}
}
