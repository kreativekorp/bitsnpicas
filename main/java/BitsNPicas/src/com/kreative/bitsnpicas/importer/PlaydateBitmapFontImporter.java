package com.kreative.bitsnpicas.importer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import com.kreative.bitsnpicas.Base64InputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.FileProxy;

public class PlaydateBitmapFontImporter implements BitmapFontImporter {
	private static final Pattern FNT_FILE_PATTERN = Pattern.compile("^(.+)\\.fnt$", Pattern.CASE_INSENSITIVE);
	private static final Pattern PNG_FILE_PATTERN = Pattern.compile("^(.+)-table-(\\d+)-(\\d+)\\.png$", Pattern.CASE_INSENSITIVE);
	
	private static Pattern fntFilePattern(String basename) {
		return Pattern.compile("^(" + Pattern.quote(basename) + ")\\.fnt$", Pattern.CASE_INSENSITIVE);
	}
	
	private static Pattern pngFilePattern(String basename) {
		return Pattern.compile("^(" + Pattern.quote(basename) + ")-table-(\\d+)-(\\d+)\\.png$", Pattern.CASE_INSENSITIVE);
	}
	
	private static File findFileMatching(Pattern pattern, File parent) {
		for (File child : parent.listFiles()) {
			if (pattern.matcher(child.getName()).matches()) {
				return child;
			}
		}
		return null;
	}
	
	private static boolean looksLikeFnt(FileProxy fp) {
		return (
			fp.startsWith('t','r','a','c','k','i','n','g') ||
			fp.startsWith('d','a','t','a') ||
			fp.startsWith('w','i','d','t','h') ||
			fp.startsWith('h','e','i','g','h','t') ||
			fp.startsWith('s','p','a','c','e') ||
			fp.startsWith('-','-')
		);
	}
	
	public static boolean canImportFont(FileProxy fp) {
		File file = fp.getFile();
		Matcher pm = PNG_FILE_PATTERN.matcher(file.getName());
		if (pm.matches()) {
			File fnt = findFileMatching(fntFilePattern(pm.group(1)), file.getParentFile());
			return fnt != null && looksLikeFnt(new FileProxy(fnt));
		}
		Matcher fm = FNT_FILE_PATTERN.matcher(file.getName());
		return fm.matches() && looksLikeFnt(fp);
	}
	
	public BitmapFont[] importFont(byte[] data) throws IOException {
		return importFont(new ByteArrayInputStream(data));
	}
	
	public BitmapFont[] importFont(InputStream is) throws IOException {
		Scanner scan = new Scanner(is, "UTF-8");
		BitmapFont f = importFont(scan, null, null, -1, -1);
		return new BitmapFont[]{f};
	}
	
	public BitmapFont[] importFont(File file) throws IOException {
		Matcher pm = PNG_FILE_PATTERN.matcher(file.getName());
		if (pm.matches()) {
			File fnt = findFileMatching(fntFilePattern(pm.group(1)), file.getParentFile());
			if (fnt == null) throw new FileNotFoundException();
			int cw = Integer.parseInt(pm.group(2));
			int ch = Integer.parseInt(pm.group(3));
			BufferedImage img = ImageIO.read(file);
			return importFont(fnt, pm.group(1), img, cw, ch);
		}
		Matcher fm = FNT_FILE_PATTERN.matcher(file.getName());
		if (fm.matches()) {
			File png = findFileMatching(pngFilePattern(fm.group(1)), file.getParentFile());
			if (png != null) {
				pm = PNG_FILE_PATTERN.matcher(png.getName());
				if (pm.matches()) {
					int cw = Integer.parseInt(pm.group(2));
					int ch = Integer.parseInt(pm.group(3));
					BufferedImage img = ImageIO.read(png);
					return importFont(file, pm.group(1), img, cw, ch);
				}
			}
			return importFont(file, fm.group(1), null, -1, -1);
		}
		return importFont(file, file.getName(), null, -1, -1);
	}
	
	private BitmapFont[] importFont(File file, String name, BufferedImage img, int cw, int ch) throws IOException {
		Scanner scan = new Scanner(file, "UTF-8");
		BitmapFont f = importFont(scan, name, img, cw, ch);
		return new BitmapFont[]{f};
	}
	
	private static final Pattern PROPERTY_LINE = Pattern.compile("^(--\\s*)?(\\w+)\\s*=\\s*(.+)$");
	private static final Pattern METRICS_LINE = Pattern.compile("\"(\\w+)\"\\s*:\\s*(\\d+)");
	
	private BitmapFont importFont(Scanner scan, String name, BufferedImage img, int cw, int ch) throws IOException {
		int baseline = -1;
		int xHeight = -1;
		int capHeight = -1;
		int tracking = 1;
		ArrayList<Integer> codePoints = new ArrayList<Integer>();
		ArrayList<Integer> charWidths = new ArrayList<Integer>();
		ArrayList<Integer> kernPairLeft = new ArrayList<Integer>();
		ArrayList<Integer> kernPairRight = new ArrayList<Integer>();
		ArrayList<Integer> kernPairWidths = new ArrayList<Integer>();
		
		// Read the fnt file.
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			Matcher plm = PROPERTY_LINE.matcher(line);
			if (plm.matches()) {
				String key = plm.group(2);
				String value = plm.group(3);
				if (key.equals("name")) {
					name = value;
				} else if (key.equals("metrics")) {
					Matcher mlm = METRICS_LINE.matcher(value);
					while (mlm.find()) {
						key = mlm.group(1);
						value = mlm.group(2);
						if (key.equals("baseline")) {
							try { baseline = Integer.parseInt(value); }
							catch (NumberFormatException nfe) { continue; }
						} else if (key.equals("xHeight")) {
							try { xHeight = Integer.parseInt(value); }
							catch (NumberFormatException nfe) { continue; }
						} else if (key.equals("capHeight")) {
							try { capHeight = Integer.parseInt(value); }
							catch (NumberFormatException nfe) { continue; }
						}
					}
				} else if (key.equals("tracking")) {
					try { tracking = Integer.parseInt(value); }
					catch (NumberFormatException nfe) { continue; }
				} else if (key.equals("data")) {
					try { img = ImageIO.read(new Base64InputStream(value)); }
					catch (IOException ioe) { continue; }
				} else if (key.equals("width")) {
					try { cw = Integer.parseInt(value); }
					catch (NumberFormatException nfe) { continue; }
				} else if (key.equals("height")) {
					try { ch = Integer.parseInt(value); }
					catch (NumberFormatException nfe) { continue; }
				}
				continue;
			}
			if (line.startsWith("--")) continue;
			String[] fields = line.split("\\s+");
			if (fields.length != 2) continue;
			int width;
			try { width = Integer.parseInt(fields[1]); }
			catch (NumberFormatException nfe) { continue; }
			ArrayList<Integer> cps = splitCodePoints(fields[0]);
			switch (cps.size()) {
				case 1:
					codePoints.add(cps.get(0));
					charWidths.add(width);
					break;
				case 2:
					kernPairLeft.add(cps.get(0));
					kernPairRight.add(cps.get(1));
					kernPairWidths.add(width);
					break;
			}
		}
		
		// Check properties.
		if (name == null) name = "Untitled";
		if (img == null) throw new IOException("no png data found");
		if (cw < 1 || ch < 1) throw new IOException("invalid cell size: " + cw + ", " + ch);
		int cols = img.getWidth() / cw;
		int rows = img.getHeight() / ch;
		if (cols < 1 || rows < 1) throw new IOException("invalid cell count: " + cols + ", " + rows);
		
		// Create the glyphs.
		ArrayList<BitmapFontGlyph> glyphs = new ArrayList<BitmapFontGlyph>();
		BufferedImage tmpImg = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_ARGB);
		int[] tmpRgb = new int[cw * ch];
		for (int row = 0, col = 0, i = 0, n = codePoints.size(); i < n; i++) {
			img.getRGB(col * cw, row * ch, cw, ch, tmpRgb, 0, cw);
			tmpImg.setRGB(0, 0, cw, ch, tmpRgb, 0, cw);
			BitmapFontGlyph g = new BitmapFontGlyph();
			g.setCharacterWidth(charWidths.get(i) + tracking);
			g.setToImage(0, 0, tmpImg);
			g.contract();
			glyphs.add(g);
			col++;
			if (col >= cols) {
				col = 0;
				row++;
				if (row >= rows) {
					row = 0;
				}
			}
		}
		
		// Calculate ascent/descent/xheight and adjust glyphs.
		BitmapFontGlyph Xg = (codePoints.contains(0x58)) ? glyphs.get(codePoints.indexOf(0x58)) : null;
		BitmapFontGlyph xg = (codePoints.contains(0x78)) ? glyphs.get(codePoints.indexOf(0x78)) : null;
		int ascent = (
			(baseline >= 0) ? baseline :
			(Xg != null) ? (Xg.getGlyphHeight() - Xg.getY()) :
			(xg != null) ? (xg.getGlyphHeight() - xg.getY()) :
			ch
		);
		if (xHeight < 0) xHeight = (xg != null) ? xg.getGlyphHeight() : 0;
		if (capHeight < 0) capHeight = (Xg != null) ? Xg.getGlyphHeight() : 0;
		for (BitmapFontGlyph g : glyphs) g.setXY(g.getX(), g.getY() + ascent);
		
		// Create the font.
		BitmapFont f = new BitmapFont(ascent, ch - ascent, ascent, ch - ascent, xHeight, 0);
		f.setName(BitmapFont.NAME_FAMILY, name);
		f.setName(BitmapFont.NAME_FAMILY_AND_STYLE, name);
		for (int i = 0, n = codePoints.size(); i < n; i++) {
			f.putCharacter(codePoints.get(i), glyphs.get(i));
		}
		return f;
	}
	
	private static ArrayList<Integer> splitCodePoints(String s) {
		ArrayList<Integer> cps = new ArrayList<Integer>();
		int i = 0, n = s.length();
		while (i < n) {
			if (s.regionMatches(i, "space", 0, 5)) {
				i += 5;
				cps.add(0x20);
			} else if (s.regionMatches(i, "U+", 0, 2)) {
				i += 2;
				int cp = 0;
				while (i < n) {
					int dcp = s.codePointAt(i);
					int d = Character.digit(dcp, 16);
					if (d < 0) break;
					i += Character.charCount(dcp);
					cp = ((cp << 4) | d);
				}
				cps.add(cp);
			} else {
				int cp = s.codePointAt(i);
				i += Character.charCount(cp);
				cps.add(cp);
			}
		}
		return cps;
	}
}
