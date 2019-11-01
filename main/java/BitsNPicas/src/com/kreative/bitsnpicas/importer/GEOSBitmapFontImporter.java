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
			for (int fontSize : gff.getFontPointSizes()) {
				GEOSFontStrike gf = gff.getFontStrike(fontSize);
				int ascent = gf.ascent + 1;
				int descent = gf.height - ascent;
				int emAscent = ascent;
				int emDescent = descent;
				while (emAscent + emDescent < fontSize) {
					if (emAscent + emDescent < fontSize) emAscent++;
					if (emAscent + emDescent < fontSize) emDescent++;
				}
				while (emAscent + emDescent > fontSize) {
					if (emAscent + emDescent > fontSize) emAscent--;
					if (emAscent + emDescent > fontSize) emDescent--;
				}
				
				String classText = gff.getClassTextString();
				String[] classFields = classText.split(" +");
				
				BitmapFont f = new BitmapFont(emAscent, emDescent, ascent, descent, 0, 0);
				for (int i = 0; i < gf.numChars; i++) {
					byte[][] gd = gf.getGlyph(i);
					int width = gd[0].length;
					BitmapFontGlyph g = new BitmapFontGlyph(gd, 0, width, ascent);
					f.putCharacter(0x20 + i, g);
				}
				f.setName(BitmapFont.NAME_FAMILY, gff.getFontName());
				f.setName(BitmapFont.NAME_VERSION, classFields[classFields.length - 1]);
				f.setName(BitmapFont.NAME_DESCRIPTION, gff.getDescriptionString());
				f.setXHeight();
				fonts.add(f);
			}
		}
		return fonts.toArray(new BitmapFont[fonts.size()]);
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		BitmapFont[] bf = importFont(in);
		in.close();
		return bf;
	}
}
