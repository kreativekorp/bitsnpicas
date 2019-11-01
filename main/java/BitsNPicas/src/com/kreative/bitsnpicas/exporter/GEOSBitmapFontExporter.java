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
import com.kreative.bitsnpicas.geos.GEOSFontFile;
import com.kreative.bitsnpicas.geos.GEOSFontStrike;

public class GEOSBitmapFontExporter implements BitmapFontExporter {
	private int myID;
	private int mySize;
	private boolean generateID;
	private boolean generateSize;
	private boolean snapSize;
	
	public GEOSBitmapFontExporter() {
		myID = 0;
		mySize = 0;
		generateID = true;
		generateSize = true;
		snapSize = false;
	}
	
	public GEOSBitmapFontExporter(boolean snapsize) {
		myID = 0;
		mySize = 0;
		generateID = true;
		generateSize = true;
		snapSize = snapsize;
	}
	
	public GEOSBitmapFontExporter(float size) {
		myID = 0;
		mySize = (int)size;
		generateID = true;
		generateSize = false;
		snapSize = false;
	}
	
	public GEOSBitmapFontExporter(float size, boolean snapsize) {
		myID = 0;
		mySize = (int)size;
		generateID = true;
		generateSize = false;
		snapSize = snapsize;
	}
	
	public GEOSBitmapFontExporter(int id) {
		myID = id;
		mySize = 0;
		generateID = false;
		generateSize = true;
		snapSize = false;
	}
	
	public GEOSBitmapFontExporter(int id, boolean snapsize) {
		myID = id;
		mySize = 0;
		generateID = false;
		generateSize = true;
		snapSize = snapsize;
	}
	
	public GEOSBitmapFontExporter(int id, int size) {
		myID = id;
		mySize = size;
		generateID = false;
		generateSize = false;
		snapSize = false;
	}
	
	public GEOSBitmapFontExporter(int id, int size, boolean snapsize) {
		myID = id;
		mySize = size;
		generateID = false;
		generateSize = false;
		snapSize = snapsize;
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
		
		int id, size;
		if (generateID) {
			id = family.hashCode();
			id = (id & 0x1FF) ^ ((id >> 8) & 0x1FF) ^ ((id >> 16) & 0x1FF) ^ ((id >> 24) & 0x1FF);
			id |= 0x200;
		} else {
			id = myID++;
		}
		if (generateSize) {
			size = font.getEmAscent() + font.getEmDescent();
		} else {
			size = mySize;
		}
		if (snapSize) {
			if (size <= 9) size = 9;
			else if (size <= 11) size = 10;
			else if (size <= 13) size = 12;
			else if (size <= 16) size = 14;
			else if (size <= 21) size = 18;
			else if (size <= 30) size = 24;
			else if (size <= 42) size = 36;
			else if (size <= 54) size = 48;
			else size = 60;
		} else if (size > 60) {
			size = 60;
		}
		
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
