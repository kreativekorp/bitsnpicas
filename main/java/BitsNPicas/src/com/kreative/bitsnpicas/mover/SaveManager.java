package com.kreative.bitsnpicas.mover;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.edit.CommonMenuItems;
import com.kreative.bitsnpicas.edit.Main;
import com.kreative.bitsnpicas.edit.SaveChangesDialog;
import com.kreative.bitsnpicas.edit.SaveInterface;
import com.kreative.rsrc.MacResourceArray;

public class SaveManager extends WindowAdapter implements SaveInterface {
	private JFrame frame;
	private File file;
	private File fork;
	private String type;
	private String creator;
	private MacResourceArray rp;
	private MoverInfoPanel ip;
	private boolean changed;
	
	public SaveManager(JFrame frame, File file, File fork, MacResourceArray rp) {
		this.frame = frame;
		this.file = file;
		this.fork = fork;
		
		if (file != null) {
			this.type = MacUtility.getType(file);
			this.creator = MacUtility.getCreator(file);
			if (type == null || type.equals("\0\0\0\0")) type = "FFIL";
			if (creator == null || creator.equals("\0\0\0\0")) creator = "DMOV";
		} else {
			this.type = "FFIL";
			this.creator = "DMOV";
		}
		
		this.rp = rp;
		this.changed = false;
		updateWindow();
	}
	
	public void setInfoPanel(MoverInfoPanel ip) {
		this.ip = ip;
	}
	
	public void setChanged() {
		this.changed = true;
		updateWindow();
	}
	
	public boolean save() {
		if (file == null || fork == null) return saveAs();
		boolean succeeded = write();
		if (succeeded) changed = false;
		updateWindow();
		return succeeded;
	}
	
	public boolean saveAs() {
		File newFile = Main.getSaveFile("");
		if (newFile == null) return false;
		file = newFile;
		if (newFile.getName().toLowerCase().endsWith(".dfont")) {
			fork = newFile;
		} else {
			try { newFile.createNewFile(); } catch (IOException e) {}
			MacUtility.setTypeAndCreator(newFile, type, creator);
			fork = MacUtility.getResourceFork(newFile);
		}
		boolean succeeded = write();
		if (succeeded) changed = false;
		updateWindow();
		return succeeded;
	}
	
	private boolean write() {
		try {
			FileOutputStream out = new FileOutputStream(fork);
			out.write(rp.getBytes());
			out.flush();
			out.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void windowClosing(WindowEvent e) {
		Window w = e.getWindow();
		if (!changed) w.dispose();
		else switch (new SaveChangesDialog(w, file.getName()).showDialog()) {
			case SAVE: if (save()) w.dispose(); break;
			case DONT_SAVE: w.dispose(); break;
			case CANCEL: break;
		}
	}
	
	private void updateWindow() {
		String fileName = (file == null) ? "Untitled Suitcase" : file.getName();
		if (CommonMenuItems.IS_MAC_OS) {
			frame.getRootPane().putClientProperty("Window.documentFile", file);
			frame.getRootPane().putClientProperty("Window.documentModified", changed);
			frame.setTitle(fileName);
		} else {
			frame.setTitle(changed ? (fileName + " \u2022") : fileName);
		}
		if (ip != null) ip.setFile(file);
	}
}
