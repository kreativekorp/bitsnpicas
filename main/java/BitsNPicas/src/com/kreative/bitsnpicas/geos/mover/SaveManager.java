package com.kreative.bitsnpicas.geos.mover;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.edit.CommonMenuItems;
import com.kreative.bitsnpicas.edit.Main;
import com.kreative.bitsnpicas.edit.SaveChangesDialog;
import com.kreative.bitsnpicas.edit.SaveInterface;
import com.kreative.bitsnpicas.geos.GEOSFontFile;

public class SaveManager extends WindowAdapter implements SaveInterface {
	private JFrame frame;
	private File file;
	private GEOSFontFile gff;
	private boolean changed;
	
	public SaveManager(JFrame frame, File file, GEOSFontFile gff) {
		this.frame = frame;
		this.file = file;
		this.gff = gff;
		this.changed = false;
		updateWindow();
	}
	
	public void setChanged() {
		this.changed = true;
		updateWindow();
	}
	
	public boolean save() {
		if (file == null) return saveAs();
		boolean succeeded = write();
		if (succeeded) changed = false;
		updateWindow();
		return succeeded;
	}
	
	public boolean saveAs() {
		File newFile = Main.getSaveFile(".cvt");
		if (newFile == null) return false;
		file = newFile;
		boolean succeeded = write();
		if (succeeded) changed = false;
		updateWindow();
		return succeeded;
	}
	
	private boolean write() {
		try {
			DataOutputStream out =
				new DataOutputStream(
					new FileOutputStream(file));
			gff.write(out);
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
		else switch (new SaveChangesDialog(w, gff.getFontName()).showDialog()) {
			case SAVE: if (save()) w.dispose(); break;
			case DONT_SAVE: w.dispose(); break;
			case CANCEL: break;
		}
	}
	
	private void updateWindow() {
		if (CommonMenuItems.IS_MAC_OS) {
			frame.getRootPane().putClientProperty("Window.documentFile", file);
			frame.getRootPane().putClientProperty("Window.documentModified", changed);
			frame.setTitle(gff.getFontName());
		} else {
			frame.setTitle(changed ? (gff.getFontName() + " \u2022") : gff.getFontName());
		}
	}
}
