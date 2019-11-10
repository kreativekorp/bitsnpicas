package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;
import com.kreative.bitsnpicas.geos.GEOSFontFile;
import com.kreative.bitsnpicas.geos.GEOSFontStrike;

public class GEOSBitmapFontExporter implements BitmapFontExporter {
	private IDGenerator idgen;
	private PointSizeGenerator sizegen;
	
	public GEOSBitmapFontExporter(IDGenerator idgen, PointSizeGenerator sizegen) {
		this.idgen = idgen;
		this.sizegen = sizegen;
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
		
		GEOSFontStrike gf = new GEOSFontStrike();
		gf.height = font.getLineAscent() + font.getLineDescent();
		gf.ascent = font.getLineAscent() - 1;
		for (int i = 0; i < 96; i++) {
			BitmapFontGlyph g = font.getCharacter(0x20 + i);
			if (g != null) {
				int width = g.getCharacterWidth();
				gf.rowWidth += width;
				gf.xCoord[i+1] = gf.xCoord[i] + width;
			} else {
				gf.xCoord[i+1] = gf.xCoord[i];
			}
		}
		gf.rowWidth = (gf.rowWidth + 7) / 8;
		gf.bitmap = new byte[gf.rowWidth * gf.height];
		for (int i = 0; i < 96; i++) {
			BitmapFontGlyph g = font.getCharacter(0x20 + i);
			if (g != null) {
				int width = g.getCharacterWidth();
				byte[][] gd = g.getGlyph();
				byte[][] ngd = new byte[gf.height][width];
				for (int gy = g.getGlyphAscent() - font.getLineAscent(), ny = 0; ny < gf.height; ny++, gy++) {
					if (gy >= 0 && gy < gd.length) {
						for (int gx = -g.getGlyphOffset(), nx = 0; nx < width; nx++, gx++) {
							if (gx >= 0 && gx < gd[gy].length) {
								ngd[ny][nx] = gd[gy][gx];
							}
						}
					}
				}
				gf.setGlyph(i, ngd);
			}
		}
		
		GEOSFontFile gff = new GEOSFontFile();
		gff.setFontName(family);
		gff.setClassTextString(getClassText(font));
		gff.setDescriptionString(desc);
		gff.setFontID(id);
		gff.setFontStrike(size, gf);
		gff.recalculate();
		
		DataOutputStream out = new DataOutputStream(os);
		gff.write(out);
		out.flush();
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		exportFontToStream(font, out);
		out.flush();
		out.close();
	}
	
	private static String getClassText(BitmapFont font) {
		String version = font.getName(BitmapFont.NAME_VERSION);
		if (version == null) version = "";
		int vlen = version.length();
		if (vlen >= 16) return version.substring(vlen - 16);
		int flen = 16 - vlen;
		String family = font.getName(BitmapFont.NAME_FAMILY);
		if (family == null) family = "";
		while (family.length() < flen) family += " ";
		return family.substring(0, flen) + version;
	}
}
