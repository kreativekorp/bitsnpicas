package com.kreative.bitsnpicas.mover;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.rsrc.MacResourceArray;

public class ResourceSelection implements ClipboardOwner, Transferable {
	// I has a flavor.
	public static final DataFlavor resourceFlavor = new DataFlavor(ResourceBundle.class, "Resource");
	
	private List<ResourceBundle> resources;
	
	public ResourceSelection(ResourceBundle res) {
		this.resources = new ArrayList<ResourceBundle>();
		this.resources.add(res.clone());
	}
	
	public ResourceSelection(ResourceBundle[] ra) {
		this.resources = new ArrayList<ResourceBundle>();
		for (ResourceBundle res : ra) this.resources.add(res.clone());
	}
	
	public ResourceSelection(List<ResourceBundle> rl) {
		this.resources = new ArrayList<ResourceBundle>();
		for (ResourceBundle res : rl) this.resources.add(res.clone());
	}
	
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (resourceFlavor.equals(flavor)) {
			return resources;
		} else if (DataFlavor.javaFileListFlavor.equals(flavor)) {
			File tempLock = File.createTempFile("kbnptemp", ".tmp");
			File tempRoot = new File(tempLock.getAbsolutePath() + "d");
			tempLock.delete();
			tempRoot.mkdir();
			tempRoot.deleteOnExit();
			List<File> fileList = new ArrayList<File>();
			for (ResourceBundle res : resources) {
				MacResourceArray rp = new MacResourceArray();
				MoverFile mf = new MoverFile(rp); mf.add(res);
				File tempFile = new File(tempRoot, res.name);
				tempFile.createNewFile();
				try {
					File fork = MacUtility.getResourceFork(tempFile);
					FileOutputStream out = new FileOutputStream(fork);
					out.write(rp.getBytes());
					out.flush(); out.close();
					MacUtility.setTypeAndCreator(tempFile, res.moverType, "movr");
				} catch (IOException e) {
					FileOutputStream out = new FileOutputStream(tempFile);
					out.write(rp.getBytes());
					out.flush(); out.close();
				}
				fileList.add(tempFile);
			}
			return fileList;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
	
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{
			resourceFlavor,
			DataFlavor.javaFileListFlavor
		};
	}
	
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (
			resourceFlavor.equals(flavor) ||
			DataFlavor.javaFileListFlavor.equals(flavor)
		);
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// nothing
	}
}
