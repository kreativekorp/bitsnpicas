package com.kreative.keyedit.edit;

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ImageSelection implements ClipboardOwner, Transferable {
	private Image myImage;
	
	public ImageSelection(Image image) {
		myImage = image;
	}
	
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (DataFlavor.imageFlavor.equals(flavor)) {
			return myImage;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
	
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{DataFlavor.imageFlavor};
	}
	
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (DataFlavor.imageFlavor.equals(flavor));
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		//nothing
	}
}
