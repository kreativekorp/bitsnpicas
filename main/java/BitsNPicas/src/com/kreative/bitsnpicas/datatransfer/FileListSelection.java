package com.kreative.bitsnpicas.datatransfer;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

public class FileListSelection implements ClipboardOwner, Transferable {
	private java.util.List<File> myList;
	private boolean isCut;
	
	public FileListSelection(File f) {
		myList = new Vector<File>();
		myList.add(f);
		isCut = false;
	}
	
	public FileListSelection(File[] f) {
		myList = new Vector<File>();
		myList.addAll(Arrays.asList(f));
		isCut = false;
	}
	
	public FileListSelection(Collection<File> f) {
		myList = new Vector<File>();
		myList.addAll(f);
		isCut = false;
	}
	
	public FileListSelection(File f, boolean cut) {
		myList = new Vector<File>();
		myList.add(f);
		isCut = cut;
	}
	
	public FileListSelection(File[] f, boolean cut) {
		myList = new Vector<File>();
		myList.addAll(Arrays.asList(f));
		isCut = cut;
	}
	
	public FileListSelection(Collection<File> f, boolean cut) {
		myList = new Vector<File>();
		myList.addAll(f);
		isCut = cut;
	}
	
	public boolean isCutOperation() {
		return isCut;
	}
	
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (DataFlavor.javaFileListFlavor.equals(flavor)) {
			return myList;
		} else if (DataFlavor.stringFlavor.equals(flavor)) {
			String s = "";
			for (File f : myList) {
				s += "\n"+f.getAbsolutePath();
			}
			return ((s.length() > 0) ? s.substring(1) : s);
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
	
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor};
	}
	
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (DataFlavor.javaFileListFlavor.equals(flavor) || DataFlavor.stringFlavor.equals(flavor));
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		//nothing
	}
}
