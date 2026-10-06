package com.kreative.bitsnpicas.exporter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.fon.FONFile;
import com.kreative.unicode.data.GlyphList;

// Exports a Windows FON file of one font, which is in it as an FNT font
// of version 2, as the fonts of Windows are. The properties of the font
// that are those of a FON file tell the rest of the file.
public class FONBitmapFontExporter implements BitmapFontExporter {
	private FNTBitmapFontExporter exporter;
	
	public FONBitmapFontExporter() {
		this.exporter = new FNTBitmapFontExporter(2);
	}
	
	public FONBitmapFontExporter(GlyphList encoding) {
		this.exporter = new FNTBitmapFontExporter(2, encoding);
	}
	
	public FONBitmapFontExporter(GlyphList encoding, int averageWidth) {
		this.exporter = new FNTBitmapFontExporter(2, encoding, averageWidth);
	}
	
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		return exportFontImpl(font).write((String)null);
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		os.write(exportFontImpl(font).write((String)null));
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		exportFontImpl(font).write(file);
	}
	
	private FONFile exportFontImpl(BitmapFont font) throws IOException {
		FONFile fon = new FONFile();
		fon.addFont(exporter.exportFontToBytes(font));
		for (Map.Entry<String,String> e : font.properties(false).entrySet()) {
			if (FONFile.isProperty(e.getKey())) fon.setProperty(e.getKey(), e.getValue());
		}
		return fon;
	}
}
