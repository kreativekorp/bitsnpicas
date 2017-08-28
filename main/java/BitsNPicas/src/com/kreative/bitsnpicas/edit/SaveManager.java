package com.kreative.bitsnpicas.edit;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontExporter;

public class SaveManager extends WindowAdapter {
	private File file;
	private FontExporter<?> format;
	private Font<?> font;
	
	public SaveManager(File file, FontExporter<?> format, Font<?> font) {
		this.file = file;
		this.format = format;
		this.font = font;
	}
	
	public boolean save() {
		if (file == null || format == null) return saveAs();
		return Main.saveFont(file, format, font);
	}
	
	public boolean saveAs() {
		FontExporter<?> newFormat = Main.getSaveFormat(font);
		if (newFormat == null) return false;
		String newSuffix = Main.getSaveSuffix(font);
		if (newSuffix == null) return false;
		File newFile = Main.getSaveFile(newSuffix);
		if (newFile == null) return false;
		file = newFile;
		format = newFormat;
		return Main.saveFont(file, format, font);
	}
	
	public void windowClosing(WindowEvent e) {
		Window w = e.getWindow();
		switch (new SaveChangesDialog(w, font.toString()).showDialog()) {
			case SAVE: if (save()) w.dispose(); break;
			case DONT_SAVE: w.dispose(); break;
			case CANCEL: break;
		}
	}
}
