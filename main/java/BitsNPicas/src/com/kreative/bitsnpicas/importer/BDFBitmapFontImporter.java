package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;

public class BDFBitmapFontImporter implements BitmapFontImporter {
	// List of mappings from charsets encountered in BDF files to ones supported by Java.
	private static final String[][] CHARSET_REGISTRIES = {
		{"JISX0208.1990", "JIS0208"},
	};
	
	public BitmapFont[] importFont(byte[] data) throws IOException {
		return importFont(new Scanner(new ByteArrayInputStream(data), "UTF-8"));
	}
	
	public BitmapFont[] importFont(InputStream is) throws IOException {
		return importFont(new Scanner(is, "UTF-8"));
	}
	
	public BitmapFont[] importFont(File file) throws IOException {
		return importFont(new Scanner(new FileInputStream(file), "UTF-8"));
	}
	
	public BitmapFont[] importFont(Scanner scan) throws IOException {
		List<BitmapFont> fonts = new ArrayList<BitmapFont>();
		while (scan.hasNextLine()) {
			String[] kv = scan.nextLine().trim().split("\\s+", 2);
			if (kv[0].equals("STARTFONT")) fonts.add(readFont(scan));
		}
		return fonts.toArray(new BitmapFont[fonts.size()]);
	}
	
	private BitmapFont readFont(Scanner scan) throws IOException {
		BitmapFont bm = new BitmapFont();
		Charset cs = null;
		while (scan.hasNextLine()) {
			String[] kv = scan.nextLine().trim().split("\\s+", 2);
			if (kv[0].equals("STARTCHAR")) readChar(scan, bm, cs);
			else if (kv[0].equals("ENDFONT")) break;
			else if (kv.length < 2) continue;
			else if (kv[0].equals("FAMILY_NAME")) bm.setName(Font.NAME_FAMILY, dequote(kv[1]));
			else if (kv[0].equals("WEIGHT_NAME")) bm.setName(Font.NAME_STYLE, dequote(kv[1]));
			else if (kv[0].equals("FONT_VERSION")) bm.setName(Font.NAME_VERSION, dequote(kv[1]));
			else if (kv[0].equals("COPYRIGHT")) bm.setName(Font.NAME_COPYRIGHT, dequote(kv[1]));
			else if (kv[0].equals("FOUNDRY")) bm.setName(Font.NAME_MANUFACTURER, dequote(kv[1]));
			else if (kv[0].equals("FONT_ASCENT")) {
				try {
					int i = Integer.parseInt(dequote(kv[1]));
					bm.setLineAscent(i);
					bm.setEmAscent(i);
				} catch (NumberFormatException nfe) {}
			}
			else if (kv[0].equals("FONT_DESCENT")) {
				try {
					int i = Integer.parseInt(dequote(kv[1]));
					bm.setLineDescent(i);
					bm.setEmDescent(i);
				} catch (NumberFormatException nfe) {}
			}
			else if (kv[0].equals("X_HEIGHT")) {
				try {
					int i = Integer.parseInt(dequote(kv[1]));
					bm.setXHeight(i);
				} catch (NumberFormatException nfe) {}
			}
			else if (kv[0].equals("CAP_HEIGHT")) {
				try {
					int i = Integer.parseInt(dequote(kv[1]));
					bm.setCapHeight(i);
				} catch (NumberFormatException nfe) {}
			}
			else if (kv[0].equals("CHARSET_REGISTRY")) {
				String name = dequote(kv[1]);
				if (name.equalsIgnoreCase("ISO10646")) {
					cs = null;
				} else if (name.equalsIgnoreCase("FontSpecific")) {
					cs = FONT_SPECIFIC;
				} else try {
					cs = Charset.forName(name);
				} catch (Exception e1) {
					cs = null;
					for (String[] entry : CHARSET_REGISTRIES) {
						if (name.equalsIgnoreCase(entry[0])) {
							try { cs = Charset.forName(entry[1]); break; }
							catch (Exception e2) { continue; }
						}
					}
					if (cs == null) {
						System.err.println("Warning: Unsupported CHARSET_REGISTRY: " + name);
					}
				}
			}
		}
		return bm;
	}
	
	private void readChar(Scanner scan, BitmapFont bm, Charset cs) throws IOException {
		BitmapFontGlyph g = new BitmapFontGlyph();
		int encoding = -1;
		while (scan.hasNextLine()) {
			String[] kv = scan.nextLine().trim().split("\\s+", 2);
			if (kv[0].equals("BITMAP")) { if (readBitmap(scan, g)) break; }
			else if (kv[0].equals("ENDCHAR")) break;
			else if (kv.length < 2) continue;
			else if (kv[0].equals("ENCODING")) {
				try {
					encoding = Integer.parseInt(dequote(kv[1]));
					if (cs == FONT_SPECIFIC) encoding += 0xF000;
					else if (cs != null) {
						String es = new String(toByteArray(encoding), cs);
						if (es.codePointCount(0, es.length()) == 1) {
							encoding = es.codePointAt(0);
						} else {
							System.err.println("Warning: Undecodable ENCODING: " + encoding);
							encoding = -1;
						}
					}
				} catch (NumberFormatException nfe) {
					encoding = -1;
				}
			}
			else if (kv[0].equals("DWIDTH")) {
				try {
					String[] va = dequote(kv[1]).split("\\s+");
					int i = Integer.parseInt(va[0]);
					g.setCharacterWidth(i);
				} catch (NumberFormatException nfe) {}
			}
			else if (kv[0].equals("BBX")) {
				try {
					String[] va = dequote(kv[1]).split("\\s+");
					int w = (va.length > 0) ? Integer.parseInt(va[0]) : 0;
					int h = (va.length > 1) ? Integer.parseInt(va[1]) : 0;
					int o = (va.length > 2) ? Integer.parseInt(va[2]) : 0;
					int d = (va.length > 3) ? Integer.parseInt(va[3]) : 0;
					g.setGlyph(new byte[h][w]);
					g.setXY(o, h + d);
				} catch (NumberFormatException nfe) {}
			}
		}
		if (encoding >= 0) bm.putCharacter(encoding, g);
	}
	
	private boolean readBitmap(Scanner scan, BitmapFontGlyph g) {
		byte[][] glyph = g.getGlyph();
		int row = 0;
		while (scan.hasNextLine() && row < glyph.length) {
			String[] kv = scan.nextLine().trim().split("\\s+", 2);
			if (kv[0].equals("ENDCHAR")) return true;
			else unpack(kv[0], glyph[row++]);
		}
		return false;
	}
	
	private static String dequote(String s) {
		if (s.length() < 2) {
			return s;
		} else if (s.startsWith("\"") && s.endsWith("\"")) {
			return s.substring(1, s.length() - 1);
		} else {
			return s;
		}
	}
	
	private static byte[] toByteArray(int v) {
		if ((v & 0xFFFFFF00) == 0) return new byte[]{(byte)v};
		if ((v & 0xFFFF0000) == 0) return new byte[]{(byte)(v >> 8), (byte)v};
		if ((v & 0xFF000000) == 0) return new byte[]{(byte)(v >> 16), (byte)(v >> 8), (byte)v};
		return new byte[]{(byte)(v >> 24), (byte)(v >> 16), (byte)(v >> 8), (byte)v};
	}
	
	private static void unpack(String h, byte[] b) {
		int i = 0;
		CharacterIterator ci = new StringCharacterIterator(h);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			int v; if (ch >= '0' && ch <= '9') v = (ch - '0');
			else if (ch >= 'A' && ch <= 'F') v = (ch - 'A' + 10);
			else if (ch >= 'a' && ch <= 'f') v = (ch - 'a' + 10);
			else continue;
			if (i < b.length) b[i++] = (byte)(((v & 0x08) == 0) ? 0 : -1);
			if (i < b.length) b[i++] = (byte)(((v & 0x04) == 0) ? 0 : -1);
			if (i < b.length) b[i++] = (byte)(((v & 0x02) == 0) ? 0 : -1);
			if (i < b.length) b[i++] = (byte)(((v & 0x01) == 0) ? 0 : -1);
		}
	}
	
	// This is only used as a marker and is handled internally.
	private static final Charset FONT_SPECIFIC = new Charset("X", null) {
		public boolean contains(Charset cs) { return false; }
		public CharsetDecoder newDecoder() { return null; }
		public CharsetEncoder newEncoder() { return null; }
	};
}
