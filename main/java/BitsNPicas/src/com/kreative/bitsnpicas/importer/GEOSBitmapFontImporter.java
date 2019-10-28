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
import com.kreative.bitsnpicas.geos.CBMConstants;
import com.kreative.bitsnpicas.geos.ConvertFile;
import com.kreative.bitsnpicas.geos.GEOSFont;

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
		ConvertFile cvt = new ConvertFile();
		cvt.read(in);
		if (
			cvt.infoBlock != null &&
			cvt.infoBlock.geosFileType == CBMConstants.GEOS_FILE_TYPE_FONT &&
			cvt.vlirData != null
		) {
			for (int fontSize = 0; fontSize < cvt.vlirData.size(); fontSize++) {
				byte[] data = cvt.vlirData.get(fontSize);
				if (data.length > 8) {
					GEOSFont gf = new GEOSFont(data);
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
					
					String classText = cvt.infoBlock.getClassTextString();
					String[] classFields = classText.split(" +");
					
					BitmapFont f = new BitmapFont(emAscent, emDescent, ascent, descent, 0, 0);
					for (int i = 0; i < 96; i++) {
						byte[][] gd = gf.getGlyph(i);
						int width = gd[0].length;
						BitmapFontGlyph g = new BitmapFontGlyph(gd, 0, width, ascent);
						f.putCharacter(0x20 + i, g);
					}
					f.setName(BitmapFont.NAME_FAMILY, cvt.directoryBlock.getFileName(true, true));
					f.setName(BitmapFont.NAME_VERSION, classFields[classFields.length - 1]);
					f.setName(BitmapFont.NAME_DESCRIPTION, cvt.infoBlock.getDescriptionString());
					f.setXHeight();
					fonts.add(f);
				}
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
