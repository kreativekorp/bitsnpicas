package com.kreative.bitsnpicas.exporter;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.truetype.CmapSubtableFormat12;
import com.kreative.bitsnpicas.truetype.CmapSubtableFormat4;
import com.kreative.bitsnpicas.truetype.CmapSubtableSequentialEntry;
import com.kreative.bitsnpicas.truetype.CmapTable;
import com.kreative.bitsnpicas.truetype.CmapTableEntry;
import com.kreative.bitsnpicas.truetype.EbdtEntry;
import com.kreative.bitsnpicas.truetype.EbdtEntryFormat1;
import com.kreative.bitsnpicas.truetype.EbdtTable;
import com.kreative.bitsnpicas.truetype.EblcBitmapSize;
import com.kreative.bitsnpicas.truetype.EblcIndexSubtable1;
import com.kreative.bitsnpicas.truetype.EblcIndexSubtableHeader;
import com.kreative.bitsnpicas.truetype.EblcTable;
import com.kreative.bitsnpicas.truetype.HeadTable;
import com.kreative.bitsnpicas.truetype.HheaTable;
import com.kreative.bitsnpicas.truetype.HmtxTable;
import com.kreative.bitsnpicas.truetype.HmtxTableEntry;
import com.kreative.bitsnpicas.truetype.MaxpTable;
import com.kreative.bitsnpicas.truetype.NameTable;
import com.kreative.bitsnpicas.truetype.NameTableEntry;
import com.kreative.bitsnpicas.truetype.Os2Table;
import com.kreative.bitsnpicas.truetype.PostTable;
import com.kreative.bitsnpicas.truetype.PostTableEntry;
import com.kreative.bitsnpicas.truetype.SbitLineMetrics;
import com.kreative.bitsnpicas.truetype.SbitSmallGlyphMetrics;
import com.kreative.bitsnpicas.truetype.SbitTableType;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class OTBBitmapFontExporter implements BitmapFontExporter {
	private boolean extendWinMetrics;
	
	public OTBBitmapFontExporter() {
		this.extendWinMetrics = false;
	}
	
	public OTBBitmapFontExporter(boolean extendWinMetrics) {
		this.extendWinMetrics = extendWinMetrics;
	}
	
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		return createTrueTypeTables(font, 100, 100, extendWinMetrics).compile();
	}
	
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(createTrueTypeTables(font, 100, 100, extendWinMetrics).compile());
		fos.close();
	}
	
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		os.write(createTrueTypeTables(font, 100, 100, extendWinMetrics).compile());
	}
	
	private static final class ThingsToKeepTrackOf {
		private int numGlyphs = 0;
		private boolean maxBoundingBox = false;
		private int bbx1 = 0;
		private int bby1 = 0;
		private int bbx2 = 0;
		private int bby2 = 0;
		private int minLSB = 0;
		private int minRSB = 0;
		private int maxAdvance = 0;
		private int maxExtent = 0;
		private int averageWidth = 0;
		private int numAverages = 0;
		private int xHeight = 0;
		private int HHeight = 0;
		private boolean highUnicode = false;
		private Map<Integer,Integer> charToGlyfMap = new HashMap<Integer,Integer>();
	}
	
	private static final TrueTypeFile createTrueTypeTables(BitmapFont bf, int xsize, int ysize, boolean extendWinMetrics) throws IOException {
		ThingsToKeepTrackOf a = new ThingsToKeepTrackOf();
		bf.autoFillNames();
		
		List<EbdtEntry> entries = new ArrayList<EbdtEntry>();
		HmtxTable hmtxTable = new HmtxTable();
		PostTable postTable = new PostTable();
		if (bf.isItalicStyle()) postTable.italicAngle = PostTable.ITALIC_ANGLE_ISOMETRIC;
		postTable.underlinePosition = -ysize;
		postTable.underlineThickness = ysize;
		
		int[] predefChars = new int[]{ -1, 0x00, 0x0D, 0x20 };
		List<Integer> chars = new ArrayList<Integer>();
		Iterator<Integer> cpi = bf.codePointIterator();
		while (cpi.hasNext()) chars.add(cpi.next());
		Collections.sort(chars);
		for (int ch : predefChars) {
			if (bf.containsCharacter(ch)) {
				makeCharacterGlyph(bf, ch, a, xsize, ysize, entries, hmtxTable, postTable);
				chars.remove((Integer)ch);
			} else {
				a.charToGlyfMap.put(ch, a.numGlyphs);
				EbdtEntryFormat1 e = new EbdtEntryFormat1();
				e.smallMetrics = new SbitSmallGlyphMetrics();
				e.imageData = new byte[0];
				entries.add(e);
				hmtxTable.add(new HmtxTableEntry());
				postTable.add(PostTableEntry.forCharacter(ch));
				a.numGlyphs++;
			}
		}
		for (int ch : chars) {
			makeCharacterGlyph(bf, ch, a, xsize, ysize, entries, hmtxTable, postTable);
		}
		
		EbdtTable ebdtTable = new EbdtTable(SbitTableType.OPENTYPE);
		EblcIndexSubtable1 st = new EblcIndexSubtable1();
		st.header = new EblcIndexSubtableHeader();
		st.header.firstGlyphIndex = 0;
		st.header.lastGlyphIndex = a.numGlyphs - 1;
		st.header.indexFormat = 1;
		st.header.imageFormat = 1;
		for (EbdtEntry entry : entries) {
			int key = ebdtTable.getNextKey();
			ebdtTable.put(key, entry);
			st.add(key);
		}
		st.add(ebdtTable.getNextKey());
		
		EblcBitmapSize ebs = createBitmapSize(bf, a, xsize);
		ebs.startGlyphIndex = 0;
		ebs.endGlyphIndex = a.numGlyphs - 1;
		ebs.add(st);
		
		EblcTable eblcTable = new EblcTable(SbitTableType.OPENTYPE);
		eblcTable.add(ebs);
		
		TrueTypeFile ttf = new TrueTypeFile();
		ttf.add(makeHeadTable(bf, a, ysize));
		ttf.add(makeHheaTable(bf, a, ysize));
		ttf.add(makeMaxpTable(a));
		ttf.add(makeOs2Table(bf, a, xsize, ysize, extendWinMetrics));
		ttf.add(hmtxTable);
		ttf.add(makeCmapTable(bf, a));
		ttf.add(eblcTable);
		ttf.add(ebdtTable);
		ttf.add(makeNameTable(bf));
		ttf.add(postTable);
		return ttf;
	}
	
	private static final void makeCharacterGlyph(
		BitmapFont bf, int ch, ThingsToKeepTrackOf a, int xsize, int ysize,
		List<EbdtEntry> entries, HmtxTable hmtxTable, PostTable postTable
	) {
		BitmapFontGlyph g = bf.getCharacter(ch);
		Rectangle r = new Rectangle(
			g.getGlyphOffset() * xsize,
			g.getGlyphAscent() * ysize,
			g.getGlyphWidth() * xsize,
			g.getGlyphHeight() * ysize
		);
		int advance = g.getCharacterWidth() * xsize;
		if (!a.maxBoundingBox) {
			a.maxBoundingBox = true;
			a.bbx1 = r.x; a.bby1 = r.y; a.bbx2 = r.x+r.width; a.bby2 = r.y+r.height;
			a.minLSB = r.x; a.minRSB = advance-r.x-r.width; a.maxAdvance = advance;
			a.maxExtent = r.x+r.width;
		} else {
			if (r.x < a.bbx1) a.bbx1 = r.x;
			if (r.y < a.bby1) a.bby1 = r.y;
			if (r.x+r.width > a.bbx2) a.bbx2 = r.x+r.width;
			if (r.y+r.height > a.bby2) a.bby2 = r.y+r.height;
			if (r.x < a.minLSB) a.minLSB = r.x;
			if (advance-r.x-r.width < a.minRSB) a.minRSB = advance-r.x-r.width;
			if (advance > a.maxAdvance) a.maxAdvance = advance;
			if (r.x+r.width > a.maxExtent) a.maxExtent = r.x+r.width;
		}
		if (advance > 0) { a.averageWidth += advance; a.numAverages++; }
		if (ch == 'x') a.xHeight = r.y+r.height;
		if (ch == 'H') a.HHeight = r.y+r.height;
		if (ch >= 0x10000) a.highUnicode = true;
		a.charToGlyfMap.put(ch, a.numGlyphs);
		entries.add(createEbdtEntry(g));
		hmtxTable.add(new HmtxTableEntry(advance, r.x));
		postTable.add(PostTableEntry.forCharacter(ch));
		a.numGlyphs++;
	}
	
	private static final EblcBitmapSize createBitmapSize(BitmapFont bf, ThingsToKeepTrackOf a, int xsize) {
		EblcBitmapSize ebs = new EblcBitmapSize();
		ebs.hori = new SbitLineMetrics();
		ebs.vert = new SbitLineMetrics();
		ebs.hori.ascender = ebs.vert.ascender = bf.getLineAscent();
		ebs.hori.descender = ebs.vert.descender = -bf.getLineDescent();
		ebs.hori.widthMax = ebs.vert.widthMax = a.maxAdvance / xsize;
		ebs.ppemX = ebs.ppemY = bf.getEmAscent() + bf.getEmDescent();
		ebs.bitDepth = 8;
		ebs.flags = 1;
		return ebs;
	}
	
	private static final EbdtEntry createEbdtEntry(BitmapFontGlyph g) {
		int w = g.getGlyphWidth();
		int h = g.getGlyphHeight();
		byte[] data = new byte[w * h];
		byte[][] rows = g.getGlyph();
		for (int y = 0, j = 0; y < rows.length; j += w, y++) {
			byte[] cols = rows[y];
			for (int x = 0, i = j; x < cols.length; i++, x++) {
				data[i] = cols[x];
			}
		}
		EbdtEntryFormat1 e = new EbdtEntryFormat1();
		e.smallMetrics = new SbitSmallGlyphMetrics();
		e.smallMetrics.height = h;
		e.smallMetrics.width = w;
		e.smallMetrics.bearingX = g.getGlyphOffset();
		e.smallMetrics.bearingY = g.getGlyphAscent();
		e.smallMetrics.advance = g.getCharacterWidth();
		e.imageData = data;
		return e;
	}
	
	private static final CmapTable makeCmapTable(BitmapFont bf, ThingsToKeepTrackOf a) {
		List<Integer> chars = new ArrayList<Integer>();
		Iterator<Integer> cpi = bf.codePointIterator();
		while (cpi.hasNext()) chars.add(cpi.next());
		Collections.sort(chars);
		CmapSubtableFormat4 lowTable = new CmapSubtableFormat4();
		CmapSubtableFormat12 highTable = new CmapSubtableFormat12();
		CmapSubtableSequentialEntry currentGroup = null;
		int lastGlyphIndex = 0;
		for (int ch : chars) {
			int gidx = a.charToGlyfMap.get(ch);
			if (currentGroup == null) {
				currentGroup = new CmapSubtableSequentialEntry();
				currentGroup.startCharCode = ch;
				currentGroup.endCharCode = ch;
				currentGroup.glyphIndex = gidx;
				lastGlyphIndex = gidx;
			} else if ((ch == currentGroup.endCharCode + 1) && (gidx == lastGlyphIndex + 1)) {
				currentGroup.endCharCode = ch;
				lastGlyphIndex = gidx;
			} else {
				if (currentGroup.startCharCode < 0x10000) lowTable.add(currentGroup);
				highTable.add(currentGroup);
				currentGroup = new CmapSubtableSequentialEntry();
				currentGroup.startCharCode = ch;
				currentGroup.endCharCode = ch;
				currentGroup.glyphIndex = gidx;
				lastGlyphIndex = gidx;
			}
		}
		if (currentGroup != null) {
			if (currentGroup.startCharCode < 0x10000) lowTable.add(currentGroup);
			highTable.add(currentGroup);
		}
		currentGroup = new CmapSubtableSequentialEntry();
		currentGroup.startCharCode = 0xFFFF;
		currentGroup.endCharCode = 0xFFFF;
		currentGroup.glyphIndex = 0;
		lowTable.add(currentGroup);
		CmapTable cmapTable = new CmapTable();
		cmapTable.subtables.add(lowTable);
		if (a.highUnicode) cmapTable.subtables.add(highTable);
		cmapTable.entries.add(CmapTableEntry.forUnicode(a.highUnicode ? highTable : lowTable));
		cmapTable.entries.add(CmapTableEntry.forWindowsUnicode16(lowTable));
		if (a.highUnicode) cmapTable.entries.add(CmapTableEntry.forWindowsUnicode32(highTable));
		return cmapTable;
	}
	
	private static final HeadTable makeHeadTable(BitmapFont bf, ThingsToKeepTrackOf a, int ysize) {
		Calendar now = new GregorianCalendar();
		double fontVersion;
		try {
			String s = bf.getName(Font.NAME_VERSION);
			if (s == null) fontVersion = 1.0;
			else if (s.startsWith("Version ")) fontVersion = Double.parseDouble(s.substring(8));
			else fontVersion = Double.parseDouble(s);
		} catch (NumberFormatException nfe) {
			fontVersion = 1.0;
		}
		HeadTable headTable = new HeadTable();
		headTable.setFontRevisionDouble(fontVersion);
		headTable.flags = HeadTable.FLAGS_Y_VALUE_OF_ZERO_SPECIFIES_BASELINE | HeadTable.FLAGS_MINIMUM_X_VALUE_IS_LEFT_SIDE_BEARING;
		headTable.unitsPerEm = (bf.getEmAscent() + bf.getEmDescent()) * ysize;
		headTable.setDateCreatedCalendar(now);
		headTable.setDateModifiedCalendar(now);
		headTable.xMin = a.bbx1;
		headTable.yMin = a.bby1;
		headTable.xMax = a.bbx2;
		headTable.yMax = a.bby2;
		headTable.macStyle = bf.getMacStyle();
		headTable.lowestRecPPEM = bf.getEmAscent() + bf.getEmDescent();
		headTable.fontDirectionHint = HeadTable.FONT_DIRECTION_HINT_MIXED;
		headTable.indexToLocFormat = HeadTable.INDEX_TO_LOC_FORMAT_LONG;
		return headTable;
	}
	
	private static final HheaTable makeHheaTable(BitmapFont bf, ThingsToKeepTrackOf a, int ysize) {
		HheaTable hheaTable = new HheaTable();
		hheaTable.ascent = bf.getLineAscent() * ysize;
		hheaTable.descent = -bf.getLineDescent() * ysize;
		hheaTable.lineGap = bf.getLineGap() * ysize;
		hheaTable.advanceWidthMax = a.maxAdvance;
		hheaTable.minLeftSideBearing = a.minLSB;
		hheaTable.minRightSideBearing = a.minRSB;
		hheaTable.xMaxExtent = a.maxExtent;
		if (bf.isItalicStyle()) {
			hheaTable.caretSlopeRise = 2;
			hheaTable.caretSlopeRun = 1;
		}
		hheaTable.numLongHorMetrics = a.numGlyphs;
		return hheaTable;
	}
	
	private static final MaxpTable makeMaxpTable(ThingsToKeepTrackOf a) {
		MaxpTable maxpTable = new MaxpTable();
		maxpTable.numGlyphs = a.numGlyphs;
		return maxpTable;
	}
	
	private static final Os2Table makeOs2Table(BitmapFont bf, ThingsToKeepTrackOf a, int xsize, int ysize, boolean extendWinMetrics) {
		List<Integer> chars = new ArrayList<Integer>();
		Iterator<Integer> cpi = bf.codePointIterator();
		while (cpi.hasNext()) chars.add(cpi.next());
		Os2Table os2Table = new Os2Table();
		os2Table.averageCharWidth = a.averageWidth / a.numAverages;
		os2Table.weightClass = bf.isBoldStyle() ? Os2Table.WEIGHT_CLASS_BOLD : Os2Table.WEIGHT_CLASS_MEDIUM;
		os2Table.widthClass = bf.isCondensedStyle() ? Os2Table.WIDTH_CLASS_CONDENSED : bf.isExtendedStyle() ? Os2Table.WIDTH_CLASS_EXPANDED : Os2Table.WIDTH_CLASS_MEDIUM;
		os2Table.subscriptXSize = (bf.getEmAscent() + bf.getEmDescent()) * xsize;
		os2Table.subscriptYSize = (bf.getEmAscent() + bf.getEmDescent()) * ysize;
		os2Table.subscriptXOffset = 0;
		os2Table.subscriptYOffset = (bf.getEmAscent() + bf.getEmDescent()) * ysize / 2;
		os2Table.superscriptXSize = (bf.getEmAscent() + bf.getEmDescent()) * xsize;
		os2Table.superscriptYSize = (bf.getEmAscent() + bf.getEmDescent()) * ysize;
		os2Table.superscriptXOffset = 0;
		os2Table.superscriptYOffset = (bf.getEmAscent() + bf.getEmDescent()) * ysize / 2;
		os2Table.strikeoutWidth = ysize;
		os2Table.strikeoutPosition = bf.getEmAscent() * ysize / 2;
		os2Table.panoseFamilyType = Os2Table.PANOSE_FAMILY_TYPE_TEXT_AND_DISPLAY;
		if (bf.isMonospaced()) os2Table.panoseProportion = Os2Table.PANOSE_PROPORTION_MONOSPACED;
		os2Table.setUnicodeRanges(chars);
		os2Table.setVendorIDString("KBnP");
		os2Table.fsSelection = bf.getFsSelection() | Os2Table.FS_SELECTION_USE_TYPO_METRICS;
		os2Table.setCharIndices(chars);
		os2Table.typoAscent = bf.getLineAscent() * ysize;
		os2Table.typoDescent = -bf.getLineDescent() * ysize;
		os2Table.typoLineGap = bf.getLineGap() * ysize;
		os2Table.winAscent = bf.getLineAscent() * ysize;
		os2Table.winDescent = bf.getLineDescent() * ysize;
		if (extendWinMetrics) {
			os2Table.winAscent = Math.max(a.bby2, os2Table.winAscent);
			os2Table.winDescent = Math.max(-a.bby1, os2Table.winDescent);
		}
		os2Table.setCodePages(chars);
		os2Table.xHeight = a.xHeight;
		os2Table.capHeight = a.HHeight;
		return os2Table;
	}
	
	private static final NameTable makeNameTable(BitmapFont bf) {
		List<Integer> nameTypes = new ArrayList<Integer>();
		for (int i : bf.nameTypes()) nameTypes.add(i);
		Collections.sort(nameTypes);
		NameTable nameTable = new NameTable();
		for (int nameID : nameTypes) nameTable.add(NameTableEntry.forUnicode(nameID, bf.getName(nameID)));
		for (int nameID : nameTypes) nameTable.add(NameTableEntry.forMacintosh(nameID, bf.getName(nameID)));
		for (int nameID : nameTypes) nameTable.add(NameTableEntry.forWindows(nameID, bf.getName(nameID)));
		return nameTable;
	}
}
