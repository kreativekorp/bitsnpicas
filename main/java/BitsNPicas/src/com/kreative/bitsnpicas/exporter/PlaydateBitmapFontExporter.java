package com.kreative.bitsnpicas.exporter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import com.kreative.bitsnpicas.Base64OutputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.GlyphPair;

public class PlaydateBitmapFontExporter implements BitmapFontExporter {
	private static final int MIN_CODE_POINT = 0x20;
	private static final int MAX_CODE_POINT = 0x3FFFF;
	private static final Pattern FNT_FILE_PATTERN = Pattern.compile("^(.+)\\.fnt$", Pattern.CASE_INSENSITIVE);
	private static final Pattern PNG_FILE_PATTERN = Pattern.compile("^(.+)-table-(\\d+)-(\\d+)\\.png$", Pattern.CASE_INSENSITIVE);
	
	private static File fntFile(File parent, String basename) {
		return new File(parent, basename + ".fnt");
	}
	
	private static File pngFile(File parent, String basename, int cw, int ch) {
		return new File(parent, basename + "-table-" + cw + "-" + ch + ".png");
	}
	
	private boolean separate;
	
	public PlaydateBitmapFontExporter(boolean separate) {
		this.separate = separate;
	}
	
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		exportFont(font, pw, null, getMetrics(font));
		pw.close();
		out.close();
		return out.toByteArray();
	}
	
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
		exportFont(font, pw, null, getMetrics(font));
	}
	
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		int[] metrics = getMetrics(font);
		if (separate) {
			Matcher pm = PNG_FILE_PATTERN.matcher(file.getName());
			if (pm.matches()) {
				File fnt = fntFile(file.getParentFile(), pm.group(1));
				File png = pngFile(file.getParentFile(), pm.group(1), metrics[1], metrics[0]);
				exportFont(font, fnt, png, metrics);
				return;
			}
			Matcher fm = FNT_FILE_PATTERN.matcher(file.getName());
			if (fm.matches()) {
				File fnt = file;
				File png = pngFile(file.getParentFile(), fm.group(1), metrics[1], metrics[0]);
				exportFont(font, fnt, png, metrics);
				return;
			}
			File fnt = fntFile(file.getParentFile(), file.getName());
			File png = pngFile(file.getParentFile(), file.getName(), metrics[1], metrics[0]);
			exportFont(font, fnt, png, metrics);
			return;
		}
		exportFont(font, file, null, metrics);
	}
	
	private void exportFont(BitmapFont font, File fnt, File png, int[] metrics) throws IOException {
		FileOutputStream out = new FileOutputStream(fnt);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
		exportFont(font, pw, png, metrics);
		pw.close();
		out.close();
	}
	
	private void exportFont(BitmapFont font, PrintWriter out, File png, int[] metrics) throws IOException {
		int count = metrics[3];
		int tracking = metrics[2];
		int cw = metrics[1];
		int ch = metrics[0];
		if (cw < 1 || ch < 1) throw new IOException("invalid cell size: " + cw + ", " + ch);
		int cols = (count < 16) ? count : 16;
		int rows = (count + cols - 1) / cols;
		if (cols < 1 || rows < 1) throw new IOException("invalid cell count: " + cols + ", " + rows);
		Map<Integer,Integer> charWidths = new TreeMap<Integer,Integer>();
		
		// Create the sprite sheet.
		BufferedImage img = new BufferedImage(cols * cw, rows * ch, BufferedImage.TYPE_INT_ARGB);
		int[] tmpRgb = new int[cw * ch];
		for (int row = 0, col = 0, cp = MIN_CODE_POINT; cp <= MAX_CODE_POINT; cp++) {
			BitmapFontGlyph g = font.getCharacter(cp);
			if (g != null) {
				BufferedImage tmpImg = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_ARGB);
				Graphics2D gg = tmpImg.createGraphics();
				gg.setColor(Color.black);
				g.paint(gg, 0, font.getLineAscent(), 1);
				gg.dispose();
				tmpImg.getRGB(0, 0, cw, ch, tmpRgb, 0, cw);
				img.setRGB(col * cw, row * ch, cw, ch, tmpRgb, 0, cw);
				charWidths.put(cp, g.getCharacterWidth() - tracking);
				col++;
				if (col >= cols) {
					col = 0;
					row++;
					if (row >= rows) {
						row = 0;
					}
				}
			}
		}
		
		// Create the fnt file.
		String name = font.getName(BitmapFont.NAME_FAMILY_AND_STYLE);
		if (name == null) name = font.getName(BitmapFont.NAME_FAMILY);
		if (name != null) out.println("--name=" + name);
		
		int baseline = font.getLineAscent() - ch;
		int xHeight = font.getXHeight();
		int capHeight = font.getCapHeight();
		out.println("--metrics={\"baseline\":"+baseline+",\"xHeight\":"+xHeight+",\"capHeight\":"+capHeight+"}");
		
		if (png != null) {
			ImageIO.write(img, "png", png);
		} else {
			StringBuffer sb = new StringBuffer();
			Base64OutputStream bo = new Base64OutputStream(sb);
			ImageIO.write(img, "png", bo);
			bo.close();
			out.println("datalen=" + sb.length());
			out.println("data=" + sb.toString());
			out.println("width=" + cw);
			out.println("height=" + ch);
		}
		
		out.println("tracking=" + tracking);
		for (Map.Entry<Integer,Integer> e : charWidths.entrySet()) {
			out.println(joinCodePoints(e.getKey()) + "\t" + e.getValue());
		}
		for (Map.Entry<GlyphPair,Integer> e : font.kernPairs(false).entrySet()) {
			Object l = e.getKey().getLeft();
			Object r = e.getKey().getRight();
			if (l instanceof Integer && r instanceof Integer) {
				int lcp = (Integer)l;
				int rcp = (Integer)r;
				if (
					lcp >= MIN_CODE_POINT && lcp <= MAX_CODE_POINT &&
					rcp >= MIN_CODE_POINT && rcp <= MAX_CODE_POINT
				) {
					out.println(joinCodePoints(lcp, rcp) + "\t" + e.getValue());
				}
			}
		}
	}
	
	private static int[] getMetrics(BitmapFont font) {
		int ch = font.getLineAscent() + font.getLineDescent() + font.getLineGap();
		int cw = 0;
		Integer tracking = null;
		int count = 0;
		for (int cp = MIN_CODE_POINT; cp <= MAX_CODE_POINT; cp++) {
			BitmapFontGlyph g = font.getCharacter(cp);
			if (g != null) {
				int gw = g.getGlyphOffset() + g.getGlyphWidth();
				if (cw < gw) cw = gw;
				int gt = g.getCharacterWidth() - gw;
				if (tracking == null || tracking > gt) tracking = gt;
				count++;
			}
		}
		if (tracking == null) tracking = 1;
		return new int[]{ch, cw+1, tracking, count};
	}
	
	private static String joinCodePoints(int... cps) {
		if (cps == null) return null;
		if (cps.length == 0) return "";
		if (cps.length == 1 && cps[0] == 0x20) return "space";
		StringBuffer sb = new StringBuffer();
		boolean lastWasU = false;
		for (int cp : cps) {
			if (lastWasU && Character.digit(cp, 16) >= 0) {
				sb.append("U+");
				sb.append(Integer.toHexString(cp).toUpperCase());
				continue;
			}
			switch (Character.getType(cp)) {
				case Character.UNASSIGNED:
				case Character.NON_SPACING_MARK:
				case Character.ENCLOSING_MARK:
				case Character.COMBINING_SPACING_MARK:
				case Character.SPACE_SEPARATOR:
				case Character.LINE_SEPARATOR:
				case Character.PARAGRAPH_SEPARATOR:
				case Character.CONTROL:
				case Character.FORMAT:
				case Character.PRIVATE_USE:
				case Character.SURROGATE:
					sb.append("U+");
					sb.append(Integer.toHexString(cp).toUpperCase());
					lastWasU = true;
					break;
				default:
					sb.append(Character.toChars(cp));
					lastWasU = false;
					break;
			}
		}
		return sb.toString();
	}
}
