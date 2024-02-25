package com.kreative.bitsnpicas.mover;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.unicode.ttflib.DfontFile;

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
				DfontFile rsrc = new DfontFile();
				MoverFile mf = new MoverFile(rsrc); mf.add(res);
				try {
					File tmp = new File(tempRoot, res.name);
					tmp.deleteOnExit();
					tmp.createNewFile();
					rsrc.write(MacUtility.getResourceFork(tmp));
					MacUtility.setTypeAndCreator(tmp, res.moverType, "movr");
					fileList.add(tmp);
				} catch (IOException e) {
					File tmp = new File(tempRoot, res.name + ".dfont");
					tmp.deleteOnExit();
					rsrc.write(tmp);
					fileList.add(tmp);
				}
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
