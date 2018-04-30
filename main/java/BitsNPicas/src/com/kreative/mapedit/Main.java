package com.kreative.mapedit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {
	public static void main(String[] args) {
		try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MapEdit"); } catch (Exception e) {}
		try { System.setProperty("apple.laf.useScreenMenuBar", "true"); } catch (Exception e) {}
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
		if (args.length == 0) {
			newMapping();
		} else {
			for (String arg : args) {
				openMapping(new File(arg));
			}
		}
		if (MapEditMenuBar.IS_MAC_OS) {
			try { Class.forName("com.kreative.mapedit.mac.MyApplicationListener").newInstance(); }
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public static MapEditFrame newMapping() {
		Mapping m = new Mapping();
		for (int i = 32; i < 127; i++) m.root.setSequence(new CodePointSequence(i), i);
		for (int i = 160; i < 256; i++) m.root.setSequence(new CodePointSequence(i), i);
		MapEditFrame f = new MapEditFrame(null, m);
		f.setVisible(true);
		return f;
	}
	
	public static MapEditFrame openMapping() {
		FileDialog fd = new FileDialog(new Frame(), "Open", FileDialog.LOAD);
		fd.setVisible(true);
		if (fd.getDirectory() == null || fd.getFile() == null) return null;
		File file = new File(fd.getDirectory(), fd.getFile());
		return openMapping(file);
	}
	
	public static MapEditFrame openMapping(File file) {
		if (file == null) {
			return openMapping();
		} else try {
			Mapping m = new Mapping();
			FileInputStream in = new FileInputStream(file);
			m.read(in);
			in.close();
			MapEditFrame f = new MapEditFrame(file, m);
			f.setVisible(true);
			return f;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				null, "An error occurred while reading the selected file.",
				"Open", JOptionPane.ERROR_MESSAGE
			);
			return null;
		}
	}
	
	public static File getSaveFile(String suffix) {
		FileDialog fd = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
		fd.setVisible(true);
		String parent = fd.getDirectory();
		String name = fd.getFile();
		if (parent == null || name == null) return null;
		if (!name.toLowerCase().endsWith(suffix.toLowerCase())) name += suffix;
		return new File(parent, name);
	}
}
