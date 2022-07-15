package com.kreative.bitsnpicas.edit;

import java.awt.Dimension;
import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;
import com.kreative.unicode.data.GlyphList;

public interface BitmapExportOptions {
	public Dimension getPixelDimension();
	public boolean getExtendWinMetrics();
	public int getSelectedColor();
	public Integer getLoadAddress();
	public GlyphList getSelectedEncoding();
	public IDGenerator getIDGenerator();
	public PointSizeGenerator getPointSizeGenerator();
	public boolean getGEOSMega();
	public boolean getGEOSKerning();
	public boolean getGEOSUTF8();
	public boolean getFONTXDoubleByte();
	public String getFONTXDoubleByteEncoding();
	public int getPSFVersion();
	public GlyphList getPSFLowEncoding();
	public GlyphList getPSFHighEncoding();
	public boolean getPSFUseLowEncoding();
	public boolean getPSFUseHighEncoding();
	public boolean getPSFUseAllGlyphs();
	public boolean getPSFUnicodeTable();
	public boolean getPlaydateSeparate();
}
