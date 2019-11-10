package com.kreative.bitsnpicas.edit;

import java.awt.Dimension;
import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public interface BitmapExportOptions {
	public Dimension getPixelDimension();
	public int getSelectedColor();
	public Integer getLoadAddress();
	public EncodingTable getSelectedEncoding();
	public IDGenerator getIDGenerator();
	public PointSizeGenerator getPointSizeGenerator();
	public boolean getGEOSMega();
	public boolean getGEOSKerning();
	public boolean getGEOSUTF8();
}
