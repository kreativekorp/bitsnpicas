package com.kreative.bitsnpicas.geos.mover;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.bitsnpicas.geos.GEOSFontPointSize;

public class GEOSFontPointSizeSelection implements ClipboardOwner, Transferable {
	// I has a flavor.
	public static final DataFlavor geosFontPointSizeFlavor = new DataFlavor(GEOSFontPointSize.class, "GEOS Font Point Size");
	
	private List<GEOSFontPointSize> gfps;
	
	public GEOSFontPointSizeSelection(GEOSFontPointSize gfps) {
		this.gfps = new ArrayList<GEOSFontPointSize>();
		this.gfps.add(gfps);
	}
	
	public GEOSFontPointSizeSelection(GEOSFontPointSize[] gfps) {
		this.gfps = new ArrayList<GEOSFontPointSize>();
		this.gfps.addAll(Arrays.asList(gfps));
	}
	
	public GEOSFontPointSizeSelection(List<GEOSFontPointSize> gfps) {
		this.gfps = new ArrayList<GEOSFontPointSize>();
		this.gfps.addAll(gfps);
	}
	
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (geosFontPointSizeFlavor.equals(flavor)) {
			return gfps;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
	
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{geosFontPointSizeFlavor};
	}
	
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (geosFontPointSizeFlavor.equals(flavor));
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// nothing
	}
}
