package com.kreative.keyedit.edit;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.kreative.keyedit.KeyboardFormat;
import com.kreative.keyedit.KeyboardMapping;
import com.kreative.keyedit.KkbWriter;

public class KeyEditFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private File file;
	private boolean changed;
	private final KeyboardMapping mapping;
	private final KeyboardMappingPanel keyboardPanel;
	private final KeyEditController controller;
	
	public KeyEditFrame(File file, KeyboardMapping mapping) {
		this.file = file;
		this.changed = false;
		this.mapping = mapping;
		this.keyboardPanel = new KeyboardMappingPanel(mapping);
		this.controller = new KeyEditController(this, keyboardPanel);
		
		setContentPane(keyboardPanel);
		setJMenuBar(new KeyEditMenuBar(this, this, controller));
		
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		updateWindow();
		
		controller.addListener(new MyKeyEditListener());
		addWindowListener(new MyWindowListener());
	}
	
	public boolean save() {
		if (file == null) {
			return saveAs();
		} else try {
			KkbWriter.write(file, mapping);
			changed = false;
			updateWindow();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				null, "An error occurred while saving this file.",
				"Save", JOptionPane.ERROR_MESSAGE
			);
			updateWindow();
			return false;
		}
	}
	
	public boolean saveAs() {
		File newFile = Main.getSaveFile(".kkbx");
		if (newFile == null) return false;
		file = newFile;
		return save();
	}
	
	public boolean export(KeyboardFormat format) {
		File newFile = Main.getSaveFile(format.getSuffix());
		if (newFile == null) {
			return false;
		} else try {
			format.write(newFile, mapping);
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				null, "An error occurred while exporting this file.",
				"Export", JOptionPane.ERROR_MESSAGE
			);
			return false;
		}
	}
	
	private void updateWindow() {
		if (OSUtils.IS_MAC_OS) {
			getRootPane().putClientProperty("Window.documentFile", file);
			getRootPane().putClientProperty("Window.documentModified", changed);
			setTitle(mapping.getNameNotEmpty());
		} else if (changed) {
			setTitle(mapping.getNameNotEmpty() + " \u2022");
		} else {
			setTitle(mapping.getNameNotEmpty());
		}
	}
	
	private class MyKeyEditListener implements KeyEditListener {
		public void metadataChanged() {
			changed = true;
			updateWindow();
			keyboardPanel.updateModifiers();
		}
		public void keyMappingChanged() {
			changed = true;
			updateWindow();
			keyboardPanel.updateKeys();
		}
	}
	
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			if (changed) {
				String name = mapping.getNameNotEmpty();
				switch (new SaveChangesDialog(KeyEditFrame.this, name).showDialog()) {
					case CANCEL: return;
					case DONT_SAVE: break;
					case SAVE:
						if (save()) break;
						else return;
				}
			}
			dispose();
		}
	}
}
