package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;
import com.kreative.bitsnpicas.geos.GEOSFontFile;
import com.kreative.bitsnpicas.geos.GEOSFontPointSize;
import com.kreative.bitsnpicas.geos.GEOSFontStrike;
import com.kreative.bitsnpicas.geos.UTF8StrikeEntry;

public class GEOSBitmapFontExporter implements BitmapFontExporter {
	private IDGenerator idgen;
	private PointSizeGenerator sizegen;
	private boolean mega;
	private boolean kerning;
	private boolean utf8;
	
	public GEOSBitmapFontExporter(IDGenerator idgen, PointSizeGenerator sizegen) {
		this.idgen = idgen;
		this.sizegen = sizegen;
		this.mega = false;
		this.kerning = false;
		this.utf8 = false;
	}
	
	public GEOSBitmapFontExporter(
		IDGenerator idgen, PointSizeGenerator sizegen,
		boolean mega, boolean kerning, boolean utf8
	) {
		this.idgen = idgen;
		this.sizegen = sizegen;
		this.mega = mega;
		this.kerning = kerning;
		this.utf8 = utf8;
	}
	
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		exportFontToStream(font, out);
		out.flush();
		out.close();
		return out.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		font.autoFillNames();
		String family = font.getName(BitmapFont.NAME_FAMILY);
		int id = idgen.generateID(font);
		int size = sizegen.generatePointSize(font);
		
		String desc = font.getName(BitmapFont.NAME_DESCRIPTION);
		if (desc == null || desc.length() == 0) desc = "Available in " + size + " point.";
		
		GEOSFontPointSize gfps = new GEOSFontPointSize(size);
		if (mega) {
			gfps.megaStrikes = new GEOSFontStrike[6];
			gfps.megaStrikes[0] = createStrike(font, 0x20, 0x00, 16, 96, false, true);
			gfps.megaStrikes[1] = createStrike(font, 0x30, 0x10, 16, 96, false, true);
			gfps.megaStrikes[2] = createStrike(font, 0x40, 0x20, 16, 96, false, true);
			gfps.megaStrikes[3] = createStrike(font, 0x50, 0x30, 16, 96, false, true);
			gfps.megaStrikes[4] = createStrike(font, 0x60, 0x40, 16, 96, false, true);
			gfps.megaStrikes[5] = createStrike(font, 0x70, 0x50, 16, 96, false, true);
			gfps.megaStrikeIndex = createStrike(font, 0x20, 0, 96, 96, utf8, false);
			gfps.utf8Map = gfps.megaStrikeIndex.utf8Map;
		} else {
			gfps.strike = createStrike(font, 0x20, 0, 96, 96, utf8, true);
			gfps.utf8Map = gfps.strike.utf8Map;
		}
		if (utf8) {
			gfps.utf8Strikes = new HashMap<UTF8StrikeEntry,GEOSFontStrike>();
			for (int cp = 0x80, i = 0; cp < 0x110000; i++, cp += 64) {
				if (containsAnyChars(font, cp, 64)) {
					gfps.utf8Map.set(cp, i >> 8, i & 0xFF, 256);
					gfps.utf8Strikes.put(
						new UTF8StrikeEntry(i >> 8, i & 0xFF, 256),
						createStrike(font, cp, 0, 64, 64, false, true)
					);
				}
			}
		}
		
		GEOSFontFile gff = new GEOSFontFile();
		gff.setFontName(family);
		gff.setClassTextString(getClassText(font));
		gff.setDescriptionString(desc);
		gff.setFontID(id);
		gff.setFontPointSize(size, gfps);
		gff.recalculate();
		
		DataOutputStream out = new DataOutputStream(os);
		gff.write(out);
		out.flush();
	}
	
	private GEOSFontStrike createStrike(
		BitmapFont font,
		int cpStart, int indexStart,
		int cpCount, int indexCount,
		boolean utf8, boolean bitmap
	) {
		GEOSFontStrike gf = new GEOSFontStrike(indexCount, kerning, utf8);
		gf.height = font.getLineAscent() + font.getLineDescent();
		gf.ascent = font.getLineAscent() - 1;
		for (int i = 0; i < indexStart; i++) {
			gf.rowWidth++;
			gf.xCoord[i+1] = gf.rowWidth;
		}
		for (int cp = cpStart, i = indexStart, j = 0; j < cpCount; j++, i++, cp++) {
			BitmapFontGlyph g = font.getCharacter(cp);
			if (g != null) {
				gf.rowWidth += getWidth(g);
				if (kerning) {
					gf.offsetWidths[i] = getOffsetWidth(g);
				}
			}
			gf.xCoord[i+1] = gf.rowWidth;
		}
		for (int i = indexStart + cpCount; i < indexCount; i++) {
			gf.rowWidth++;
			gf.xCoord[i+1] = gf.rowWidth;
		}
		gf.rowWidth = (gf.rowWidth + 7) / 8;
		if (bitmap) {
			gf.bitmap = new byte[gf.rowWidth * gf.height];
			for (int cp = cpStart, i = indexStart, j = 0; j < cpCount; j++, i++, cp++) {
				BitmapFontGlyph g = font.getCharacter(cp);
				if (g != null) {
					byte[][] gd = convertGlyph(font, g, gf.height);
					gf.setGlyph(i, gd);
				}
			}
		}
		return gf;
	}
	
	private int getWidth(BitmapFontGlyph g) {
		int width = g.getCharacterWidth();
		if (kerning) {
			int leftBearing = g.getGlyphOffset();
			int rightBearing = width - leftBearing - g.getGlyphWidth();
			if (leftBearing < 0) width -= leftBearing;
			if (rightBearing < 0) width -= rightBearing;
		}
		return width;
	}
	
	private GEOSFontStrike.OffsetWidth getOffsetWidth(BitmapFontGlyph g) {
		int width = g.getCharacterWidth();
		int offset = g.getGlyphOffset();
		if (offset > 0) offset = 0;
		return new GEOSFontStrike.OffsetWidth(offset, width);
	}
	
	private byte[][] convertGlyph(BitmapFont font, BitmapFontGlyph g, int height) {
		int width = g.getCharacterWidth();
		int startgx = -g.getGlyphOffset();
		int startgy = g.getGlyphAscent() - font.getLineAscent();
		if (kerning) {
			int leftBearing = g.getGlyphOffset();
			int rightBearing = width - leftBearing - g.getGlyphWidth();
			if (leftBearing < 0) { width -= leftBearing; startgx = 0; }
			if (rightBearing < 0) width -= rightBearing;
		}
		byte[][] gd = g.getGlyph();
		byte[][] ngd = new byte[height][width];
		for (int gy = startgy, ny = 0; ny < height; ny++, gy++) {
			if (gy >= 0 && gy < gd.length) {
				for (int gx = startgx, nx = 0; nx < width; nx++, gx++) {
					if (gx >= 0 && gx < gd[gy].length) {
						ngd[ny][nx] = gd[gy][gx];
					}
				}
			}
		}
		return ngd;
	}
	
	private static boolean containsAnyChars(BitmapFont font, int cp, int count) {
		while (count-- > 0) if (font.containsCharacter(cp++)) return true;
		return false;
	}
	
	private static final String VP = "[Vv][Ee][Rr]([.]|[Ss]([.]|[Ii][Oo][Nn])?)? *";
	private static String getClassText(BitmapFont font) {
		String version = font.getName(BitmapFont.NAME_VERSION);
		if (version == null) {
			version = "";
		} else {
			version = version.replaceAll(VP, "V").trim();
			if (version.length() > 0) version = " " + version;
		}
		int vlen = version.length();
		if (vlen >= 16) return version.substring(vlen - 16);
		int flen = 16 - vlen;
		String family = font.getName(BitmapFont.NAME_FAMILY);
		if (family == null) family = "";
		while (family.length() < flen) family += " ";
		return family.substring(0, flen) + version;
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		exportFontToStream(font, out);
		out.flush();
		out.close();
	}
}
