package com.kreative.bitsnpicas.edit;

import java.awt.Dimension;
import com.kreative.bitsnpicas.exporter.NFNTBitmapFontExporter;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public interface BitmapExportOptions {
	public Dimension getPixelDimension();
	public int getSelectedColor();
	public Integer getLoadAddress();
	public EncodingTable getSelectedEncoding();
	public NFNTBitmapFontExporter createNFNTExporter();
}
