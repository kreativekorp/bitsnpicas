package com.kreative.bitsnpicas.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.fon.FNTHeader;
import com.kreative.bitsnpicas.fon.FONFile;
import com.kreative.unicode.data.GlyphList;

// Imports the fonts of a Windows FON file, each of which is an FNT font.
// The font of a file that has no other keeps what the file tells besides
// the font as properties, which the exporter of FON files writes back.
public class FONBitmapFontImporter implements BitmapFontImporter {
	private GlyphList encoding;
	
	public FONBitmapFontImporter() {
		this.encoding = null;
	}
	
	public FONBitmapFontImporter(GlyphList encoding) {
		this.encoding = encoding;
	}
	
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		return importFontImpl(FONFile.read(data));
	}
	
	@Override
	public BitmapFont[] importFont(InputStream in) throws IOException {
		return importFontImpl(FONFile.read(in));
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		return importFontImpl(FONFile.read(file));
	}
	
	private BitmapFont[] importFontImpl(FONFile fon) throws IOException {
		List<BitmapFont> fonts = new ArrayList<BitmapFont>();
		FNTBitmapFontImporter importer = new FNTBitmapFontImporter(encoding);
		for (int i = 0; i < fon.getFontCount(); i++) {
			if (new FNTHeader(fon.getFont(i)).isVector()) continue;
			for (BitmapFont font : importer.importFont(fon.getFont(i))) fonts.add(font);
		}
		if (fon.getFontCount() == 1) {
			for (BitmapFont font : fonts) {
				for (Map.Entry<String,String> e : fon.properties().entrySet()) {
					font.setProperty(e.getKey(), e.getValue());
				}
			}
		}
		return fonts.toArray(new BitmapFont[fonts.size()]);
	}
	
	// The encoding of the first font of a file, by its character set.
	public static String getEncodingName(File file) throws IOException {
		FONFile fon = FONFile.read(file);
		if (fon.getFontCount() == 0) return null;
		return FNTBitmapFontImporter.getEncodingName(new FNTHeader(fon.getFont(0)).charSet);
	}
}
