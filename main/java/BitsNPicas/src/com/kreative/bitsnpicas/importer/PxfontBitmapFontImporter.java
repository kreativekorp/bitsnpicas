package com.kreative.bitsnpicas.importer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

// Imports the font sheet of pxfont (https://github.com/mike-shevchenko/retro-tools):
// the characters of a single-byte encoding, 32 to a row, each at its code
// modulo 32. A sheet is one of two kinds. In the full coloring its cells are
// navy on white and black on light gray by turns, which tells their widths;
// a code without a character is transparent, as wide as characters usually
// are, and the default character, at 0x7F, may have no paper. A sheet in other
// colors has cells of one size, which the name of its file tells, as in
// "zx 6x8px.png"; its first cell is blank and tells the paper, and its rows
// start at the space. The sheet of a TrueType font may go on with rows of
// glyphs that have no code in the encoding; those are left out.
public class PxfontBitmapFontImporter implements BitmapFontImporter, ImportWarnings {
	private static final int COLUMNS = 32;
	private static final int MAX_ROWS = 8;
	private static final int DEFAULT_CHAR = 0x7F;
	private static final int[] PAPER = { 0xFFFFFFFF, 0xFFC0C0C0 };
	private static final int[] INK = { 0xFF000080, 0xFF000000 };
	// How far from the paper a color of a sheet in other colors is, in the channel
	// that differs most, to be paper at most and to be ink at least.
	private static final int PAPER_DISTANCE = 64;
	private static final int INK_DISTANCE = 120;
	// Letters that start on one row and end on one row in any design.
	private static final String LETTERS = "EFHLTZ";
	private static final Pattern NAME_SIZE = Pattern.compile("(?i) (?:(\\d+)x)?(\\d+)px(?: bold)?(?: italic)?$");
	private static final Pattern NAME_STYLE = Pattern.compile("(?i)(?: normal| regular|( bold)?( italic)?)$");
	
	// What a column of a row of cells is.
	private static final int INVALID = -2;
	private static final int CLEAR = -1;
	private static final int TURN = 1;
	private static final int PAPERED = 2;
	private static final int MIXED = 4;
	
	private GlyphList encoding;
	private final List<String> warnings = new ArrayList<String>();
	
	public PxfontBitmapFontImporter() {
		this.encoding = null;
	}
	
	public PxfontBitmapFontImporter(GlyphList encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		return importFont(new ByteArrayInputStream(data));
	}
	
	@Override
	public BitmapFont[] importFont(InputStream is) throws IOException {
		BufferedImage image = ImageIO.read(is);
		if (image == null) throw new IOException("not an image");
		return new BitmapFont[]{ importFont(image, null) };
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		if (image == null) throw new IOException("not an image");
		return new BitmapFont[]{ importFont(image, file.getName()) };
	}
	
	// Whether an image is such a sheet: by its colors, or else by the size
	// of its cells in the name of its file.
	public static boolean canImportFont(BufferedImage image, String fileName) {
		if (image == null) return false;
		Sheet sheet = new Sheet(image, fileName);
		return sheet.fullColoring() || sheet.plainHeight() > 0;
	}
	
	@Override
	public List<String> getImportWarnings() {
		return warnings;
	}
	
	public BitmapFont importFont(BufferedImage image, String fileName) throws IOException {
		warnings.clear();
		Sheet sheet = new Sheet(image, fileName);
		if (sheet.fullColoring()) sheet.readCells();
		else sheet.readGrid();
		if (sheet.glyphs.isEmpty()) throw new IOException("no characters in the sheet");
		
		// The ascent is where the letters end, and what is above them is internal leading.
		int height = sheet.height;
		int ascent = height;
		int leading = 0;
		for (int i = 0; i < LETTERS.length(); i++) {
			byte[][] glyph = sheet.glyphs.get((int)LETTERS.charAt(i));
			int top = -1, bottom = -1;
			for (int y = 0; glyph != null && y < height; y++) {
				for (byte pixel : glyph[y]) {
					if (pixel != 0) {
						if (top < 0) top = y;
						bottom = y;
						break;
					}
				}
			}
			if (top < 0 || (i > 0 && (bottom + 1 != ascent || top != leading))) {
				ascent = height;
				leading = 0;
				break;
			}
			ascent = bottom + 1;
			leading = top;
		}
		
		GlyphList encoding = this.encoding;
		if (encoding == null) encoding = EncodingList.instance().getGlyphList("windows-1252");
		int fixedWidth = -1;
		for (byte[][] glyph : sheet.glyphs.values()) {
			if (fixedWidth < 0) fixedWidth = glyph[0].length;
			if (fixedWidth != glyph[0].length) fixedWidth = 0;
		}
		BitmapFont f = new BitmapFont(ascent - leading, height - ascent, ascent, height - ascent, 0, 0, 0, fixedWidth);
		for (Map.Entry<Integer,byte[][]> e : sheet.glyphs.entrySet()) {
			int idx = e.getKey();
			byte[][] glyph = e.getValue();
			int ch = (encoding != null) ? encoding.get(idx) : idx;
			if (ch < 0) {
				ch = 0xF000 + idx;
				warnings.add(String.format(
					"The place 0x%02X has a glyph but no character in %s; the glyph is U+%04X now.",
					idx, encoding.getName(), ch
				));
			}
			f.putCharacter(ch, new BitmapFontGlyph(glyph, 0, glyph[0].length, ascent));
			// The default character is also the glyph of characters that the font lacks.
			if (idx == sheet.defaultChar) {
				byte[][] copy = new byte[glyph.length][];
				for (int y = 0; y < glyph.length; y++) copy[y] = glyph[y].clone();
				f.putNamedGlyph(".notdef", new BitmapFontGlyph(copy, 0, copy[0].length, ascent));
			}
		}
		if (sheet.defaultChar >= 0) {
			f.setProperty(FNTBitmapFontImporter.PROP_DEFAULT_CHAR, Integer.toString(sheet.defaultChar));
		}
		if (sheet.gap > 0 && fixedWidth == 0) {
			f.setProperty(FNTBitmapFontImporter.PROP_AVG_WIDTH, Integer.toString(sheet.gap));
		}
		if (sheet.skipped > 0) {
			warnings.add(
				sheet.skipped + " glyph(s) in the rows after code 255 are left out: the sheet does not tell" +
				" which characters they are."
			);
		}
		if (sheet.name != null) {
			Matcher m = NAME_STYLE.matcher(sheet.name);
			m.find();
			String style = ((m.group(1) != null) ? "Bold " : "") + ((m.group(2) != null) ? "Italic" : "");
			style = (style.length() > 0) ? style.trim() : "Normal";
			String family = sheet.name.substring(0, m.start());
			f.setName(BitmapFont.NAME_FAMILY, family);
			f.setName(BitmapFont.NAME_STYLE, style);
			f.setName(BitmapFont.NAME_FAMILY_AND_STYLE, family + " " + style);
		}
		f.setXHeight();
		f.setCapHeight();
		return f;
	}
	
	private static class Run {
		private int turn, start, end;
		private boolean papered, mixed;
	}
	
	private static class Sheet {
		private final int w, h;
		private final int[] px;
		private final String name;
		private int namedWidth, namedHeight;
		private int height;
		private int gap;
		private int defaultChar = -1;
		private int skipped = 0;
		// The glyph of each code, as rows of pixels.
		private final SortedMap<Integer,byte[][]> glyphs = new TreeMap<Integer,byte[][]>();
		
		private Sheet(BufferedImage image, String fileName) {
			w = image.getWidth();
			h = image.getHeight();
			px = image.getRGB(0, 0, w, h, null, 0, w);
			if (fileName != null && fileName.lastIndexOf('.') > 0) {
				fileName = fileName.substring(0, fileName.lastIndexOf('.'));
			}
			name = fileName;
			Matcher m = (name != null) ? NAME_SIZE.matcher(name) : null;
			if (m != null && m.find()) {
				try {
					namedWidth = (m.group(1) != null) ? Integer.parseInt(m.group(1)) : 0;
					namedHeight = Integer.parseInt(m.group(2));
				} catch (NumberFormatException e) {
					namedWidth = namedHeight = 0;
				}
			}
		}
		
		// Both papers, and nothing else but their inks and transparent pixels.
		private boolean fullColoring() {
			boolean white = false, gray = false, navy = false, black = false;
			for (int c : px) {
				if ((c >>> 24) == 0) continue;
				if (c == PAPER[0]) white = true;
				else if (c == PAPER[1]) gray = true;
				else if (c == INK[0]) navy = true;
				else if (c == INK[1]) black = true;
				else return false;
			}
			return white && gray && (navy || !black);
		}
		
		// The height of the cells of a sheet in other colors, which the name
		// of its file must tell with their width. Zero when it does not.
		private int plainHeight() {
			if (namedWidth <= 0 || namedHeight <= 0) return 0;
			if (w != COLUMNS * namedWidth || h % namedHeight != 0) return 0;
			int rows = h / namedHeight;
			return (rows >= 1 && rows < MAX_ROWS) ? namedHeight : 0;
		}
		
		// A sheet in other colors.
		private void readGrid() throws IOException {
			height = plainHeight();
			if (height == 0) {
				throw new IOException(
					"not in the colors of a font sheet, and then the name of the file must tell" +
					" the size of a cell, as in \"zx 6x8px.png\", " + COLUMNS + " of which make a row"
				);
			}
			int width = namedWidth;
			int paper = px[0];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (px[y * w + x] != paper) {
						throw new IOException("the first cell must be blank, for its color to tell the paper");
					}
				}
			}
			Map<Integer,Boolean> inks = new HashMap<Integer,Boolean>();
			int rows = h / height;
			for (int r = 0; r < rows; r++) {
				for (int slot = 0; slot < COLUMNS; slot++) {
					byte[][] glyph = new byte[height][width];
					for (int y = 0; y < height; y++) {
						for (int x = 0; x < width; x++) {
							int c = px[(r * height + y) * w + slot * width + x];
							Boolean ink = inks.get(c);
							if (ink == null) {
								int distance = distance(c, paper);
								if (distance > PAPER_DISTANCE && distance < INK_DISTANCE) {
									throw new IOException(String.format(
										"the pixel at %d,%d is neither paper nor ink: its color is %d from that" +
										" of the first cell, where up to %d is paper and %d or more is ink",
										slot * width + x, r * height + y, distance, PAPER_DISTANCE, INK_DISTANCE
									));
								}
								inks.put(c, ink = (distance >= INK_DISTANCE));
							}
							if (ink) glyph[y][x] = -1;
						}
					}
					glyphs.put((r + 1) * COLUMNS + slot, glyph);
				}
			}
			gap = width;
			if (glyphs.containsKey(DEFAULT_CHAR)) defaultChar = DEFAULT_CHAR;
		}
		
		// How far a color stands out of the paper: the largest difference in a channel,
		// as much of it as the color is opaque. On transparent paper, its opacity.
		private static int distance(int color, int paper) {
			int alpha = color >>> 24;
			if ((paper >>> 24) == 0) return alpha;
			int most = 0;
			for (int shift = 0; shift < 24; shift += 8) {
				most = Math.max(most, Math.abs(((color >> shift) & 0xFF) - ((paper >> shift) & 0xFF)));
			}
			return (most * alpha + 127) / 255;
		}
		
		// What the column of a row of cells is: clear, or one of the two colorings
		// with or without paper in it, and with or without transparent pixels.
		private int column(int x, int top, int height) {
			int turn = -1;
			boolean papered = false, clear = false;
			for (int y = top; y < top + height; y++) {
				int c = px[y * w + x];
				if ((c >>> 24) == 0) { clear = true; continue; }
				int t = (c == PAPER[0] || c == INK[0]) ? 0 : 1;
				if (turn >= 0 && turn != t) return INVALID;
				turn = t;
				if (c == PAPER[t]) papered = true;
			}
			if (turn < 0) return CLEAR;
			if (clear && papered) return INVALID;
			return turn | (papered ? PAPERED : 0) | (clear ? MIXED : 0);
		}
		
		private boolean validHeight(int height) {
			for (int top = 0; top < h; top += height) {
				for (int x = 0; x < w; x++) {
					if (column(x, top, height) == INVALID) return false;
				}
			}
			return true;
		}
		
		// A sheet in the full coloring.
		private void readCells() throws IOException {
			// Rows of cells: as few as can be with no column of a row in two colorings.
			height = 0;
			if (namedHeight > 0 && h % namedHeight == 0 && validHeight(namedHeight)) height = namedHeight;
			for (int rows = 1; height == 0 && rows <= MAX_ROWS; rows++) {
				if (h % rows == 0 && validHeight(h / rows)) height = h / rows;
			}
			if (height == 0 || h / height > MAX_ROWS) throw new IOException("the rows of cells cannot be told apart");
			int rows = h / height;
			
			// In each row, the cells with paper, and after them the cell without paper, if any.
			List<List<Run>> cells = new ArrayList<List<Run>>();
			Run[] bares = new Run[rows];
			Map<Integer,Integer> widthCounts = new HashMap<Integer,Integer>();
			int bareRow = -1;
			for (int r = 0; r < rows; r++) {
				List<Run> runs = new ArrayList<Run>();
				Run run = null;
				for (int x = 0; x < w; x++) {
					int column = column(x, r * height, height);
					if (column == CLEAR) { run = null; continue; }
					if (run == null || run.turn != (column & TURN)) {
						run = new Run();
						run.turn = column & TURN;
						run.start = x;
						runs.add(run);
					}
					run.end = x + 1;
					if ((column & PAPERED) != 0) run.papered = true;
					if ((column & MIXED) != 0) run.mixed = true;
				}
				int count = runs.size();
				while (count > 0 && !runs.get(count - 1).papered && runs.get(count - 1).mixed) count--;
				if (count < runs.size()) {
					Run bare = runs.get(count);
					for (Run other : runs.subList(count, runs.size())) {
						if (other.turn != bare.turn) throw wrong(r, "the cell without paper has ink of two colors");
						bare.end = other.end;
					}
					if (bareRow >= 0) throw wrong(r, "a second cell without paper");
					bares[r] = bare;
					bareRow = r;
					runs = new ArrayList<Run>(runs.subList(0, count));
				}
				for (Run cell : runs) {
					if (cell.mixed) throw wrong(r, "ink on transparent pixels before the last cell");
					Integer n = widthCounts.get(cell.end - cell.start);
					widthCounts.put(cell.end - cell.start, (n == null) ? 1 : (n + 1));
				}
				cells.add(runs);
			}
			
			// How wide a place without a cell is: as wide as most cells, if that fits
			// the transparent pixels and the turns of the colorings, or else the widest that fits.
			int usual = 0;
			for (Map.Entry<Integer,Integer> e : widthCounts.entrySet()) {
				Integer most = widthCounts.get(usual);
				if (most == null || e.getValue() > most || (e.getValue().equals(most) && e.getKey() > usual)) usual = e.getKey();
			}
			gap = 0;
			if (usual > 0 && places(cells, bares, usual, null)) gap = usual;
			for (int g = w; gap == 0 && g >= 1; g--) {
				if (places(cells, bares, g, null)) gap = g;
			}
			if (gap == 0) {
				throw new IOException(
					"the places of the cells cannot be told: the papers must alternate by the places" +
					" of a row, those without a cell counted, and each of these be of one width"
				);
			}
			
			// The row of the cell without paper is that of 0x7F; or else eight rows
			// start at code 0, and fewer at the space. The sheet of a TrueType font
			// goes on with rows of glyphs that have no code here, which are left out.
			int firstRow = (bareRow >= 0) ? (DEFAULT_CHAR / COLUMNS - bareRow) : (rows >= MAX_ROWS) ? 0 : 1;
			if (firstRow < 0) throw new IOException("the sheet has too many rows above the cell without paper");
			List<int[]> slots = new ArrayList<int[]>();
			places(cells, bares, gap, slots);
			for (int[] s : slots) {
				int r = s[0], slot = s[1], start = s[2], end = s[3], ink = INK[s[4]];
				if (firstRow + r >= MAX_ROWS) { skipped++; continue; }
				byte[][] glyph = new byte[height][end - start];
				for (int y = 0; y < height; y++) {
					for (int x = start; x < end && x < w; x++) {
						if (px[(r * height + y) * w + x] == ink) glyph[y][x - start] = -1;
					}
				}
				glyphs.put((firstRow + r) * COLUMNS + slot, glyph);
			}
			if (bareRow >= 0) defaultChar = DEFAULT_CHAR;
		}
		
		// Whether the cells fit the places of their rows when a place without a cell
		// is of the given width. Each cell found is added to the list, if there is
		// one, as its row, place, left and right edges and coloring.
		private boolean places(List<List<Run>> cells, Run[] bares, int gap, List<int[]> slots) {
			for (int r = 0; r < cells.size(); r++) {
				int slot = 0, x = 0;
				for (Run cell : cells.get(r)) {
					int clear = cell.start - x;
					if (clear % gap != 0) return false;
					slot += clear / gap;
					if (slot >= COLUMNS || ((r + slot) & 1) != cell.turn) return false;
					if (slots != null) slots.add(new int[]{ r, slot, cell.start, cell.end, cell.turn });
					slot++;
					x = cell.end;
				}
				if (bares[r] != null) {
					int last = COLUMNS - 1;
					int left = x + (last - slot) * gap;
					if (slot > last || ((r + last) & 1) != bares[r].turn) return false;
					if (bares[r].start < left || bares[r].end > left + gap) return false;
					if (slots != null) slots.add(new int[]{ r, last, left, left + gap, bares[r].turn });
				}
			}
			return true;
		}
		
		private static IOException wrong(int row, String message) {
			return new IOException("row " + (row + 1) + " of cells: " + message);
		}
	}
}
