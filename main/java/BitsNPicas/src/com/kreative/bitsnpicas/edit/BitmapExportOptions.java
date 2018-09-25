package com.kreative.bitsnpicas.edit;

import java.awt.Dimension;
import com.kreative.bitsnpicas.exporter.NFNTBitmapFontExporter;

public interface BitmapExportOptions {
	public Dimension getPixelDimension();
	public int getSelectedColor();
	public NFNTBitmapFontExporter createNFNTExporter();
}
