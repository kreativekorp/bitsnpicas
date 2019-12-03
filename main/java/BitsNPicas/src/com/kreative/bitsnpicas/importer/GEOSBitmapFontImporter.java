package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.geos.GEOSFontFile;
import com.kreative.bitsnpicas.geos.GEOSFontPointSize;
import com.kreative.bitsnpicas.geos.GEOSFontStrike;

public class GEOSBitmapFontImporter implements BitmapFontImporter {
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		BitmapFont[] bf = importFont(in);
		in.close();
		return bf;
	}
	
	@Override
	public BitmapFont[] importFont(InputStream is) throws IOException {
		List<BitmapFont> fonts = new ArrayList<BitmapFont>();
		DataInputStream in = new DataInputStream(is);
		GEOSFontFile gff = new GEOSFontFile(in);
		if (gff.isValid()) {
			for (int pointSize : gff.getFontPointSizes()) {
				BitmapFont f = importFont(gff, pointSize);
				if (f != null) fonts.add(f);
			}
		}
		return fonts.toArray(new BitmapFont[fonts.size()]);
	}
	
	public BitmapFont importFont(GEOSFontFile gff, int pointSize) {
		GEOSFontPointSize gfps = gff.getFontPointSize(pointSize);
		GEOSFontStrike gfs = gfps.isMega() ? gfps.megaStrikeIndex : gfps.strike;
		int ascent = gfs.ascent + 1;
		int descent = gfs.height - ascent;
		int emAscent = ascent;
		int emDescent = descent;
		while (emAscent + emDescent < pointSize) {
			if (emAscent + emDescent < pointSize) emAscent++;
			if (emAscent + emDescent < pointSize) emDescent++;
		}
		while (emAscent + emDescent > pointSize) {
			if (emAscent + emDescent > pointSize) emAscent--;
			if (emAscent + emDescent > pointSize) emDescent--;
		}
		
		String classText = gff.getClassTextString();
		String[] classFields = classText.split(" +");
		
		BitmapFont f = new BitmapFont(emAscent, emDescent, ascent, descent, 0, 0);
		for (int cp = 0x20; cp < 0x80; cp++) {
			BitmapFontGlyph g = getASCIIGlyph(gfps, cp);
			if (g != null) f.putCharacter(cp, g);
		}
		if (gfps.isUTF8()) {
			for (int cp = 0x80; cp < 0x110000; cp++) {
				BitmapFontGlyph g = getUTF8Glyph(gfps, cp);
				if (g != null) f.putCharacter(cp, g);
			}
		}
		if (f.isEmpty()) return null;
		f.setName(BitmapFont.NAME_FAMILY, gff.getFontName());
		f.setName(BitmapFont.NAME_VERSION, classFields[classFields.length - 1]);
		f.setName(BitmapFont.NAME_DESCRIPTION, gff.getDescriptionString());
		f.setXHeight();
		return f;
	}
	
	private static BitmapFontGlyph getASCIIGlyph(GEOSFontPointSize gfps, int cp) {
		int i = cp - 0x20;
		GEOSFontStrike gfs = gfps.isMega() ? gfps.megaStrikes[i >> 4] : gfps.strike;
		return getGlyph(gfs, i);
	}
	
	private static BitmapFontGlyph getUTF8Glyph(GEOSFontPointSize gfps, int cp) {
		GEOSFontStrike gfs = gfps.utf8Strikes.get(gfps.utf8Map.get(cp));
		return getGlyph(gfs, cp & 0x3F);
	}
	
	private static BitmapFontGlyph getGlyph(GEOSFontStrike gfs, int index) {
		if (gfs == null) return null;
		byte[][] gd = gfs.getGlyph(index);
		if (gd == null) return null;
		int offset, width;
		if (gfs.offsetWidths != null) {
			offset = gfs.offsetWidths[index].offset;
			width = gfs.offsetWidths[index].width;
		} else {
			offset = 0;
			width = gd[0].length;
		}
		if (width == 0 && gd[0].length == 0) return null;
		return new BitmapFontGlyph(gd, offset, width, gfs.ascent + 1);
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		BitmapFont[] bf = importFont(in);
		in.close();
		return bf;
	}
}
