package com.kreative.fontmap;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFrame;

public class FontMapController {
	private static FontMapController instance;
	
	public static FontMapController getInstance() {
		if (instance == null) {
			instance = new FontMapController();
		}
		return instance;
	}
	
	private List<FontMapEntry> entries;
	private FontMapFrame frame;
	
	private FontMapController() {
		this.entries = new ArrayList<FontMapEntry>();
		this.frame = null;
		try {
			File file = getPreferencesFile();
			if (file.exists()) {
				Scanner in = new Scanner(file, "UTF-8");
				while (in.hasNextLine()) {
					String[] fields = in.nextLine().split("=", 2);
					if (fields.length == 2) {
						FontMapEntry e = new FontMapEntry();
						e.setCodePointsString(fields[0].trim());
						e.setFontString(fields[1].trim());
						entries.add(e);
					}
				}
				in.close();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public List<FontMapEntry> getEntries() {
		return this.entries;
	}
	
	public void setEntries(List<FontMapEntry> entries) {
		this.entries.clear();
		this.entries.addAll(entries);
		if (entries.isEmpty()) {
			getPreferencesFile().delete();
		} else try {
			FileOutputStream fos = new FileOutputStream(getPreferencesFile());
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			PrintWriter out = new PrintWriter(osw, true);
			for (FontMapEntry e : entries) {
				out.println(e.getCodePointsString() + " = " + e.getFontString());
			}
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public FontMapEntry entryForString(String s) {
		for (FontMapEntry e : entries) {
			if (e.containsAllCodePoints(s)) {
				return e;
			}
		}
		return null;
	}
	
	public FontMapFrame getFrame() {
		if (frame == null) {
			frame = new FontMapFrame(entries);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setEntries(frame.getFontMap());
					frame.dispose();
					frame = null;
				}
			});
		}
		return frame;
	}
	
	private static File getPreferencesFile() {
		if (System.getProperty("os.name").toUpperCase().contains("MAC OS")) {
			File u = new File(System.getProperty("user.home"));
			File l = new File(u, "Library");
			if (!l.exists()) l.mkdir();
			File p = new File(l, "Preferences");
			if (!p.exists()) p.mkdir();
			return new File(p, "com.kreative.mapedit.fontmap.txt");
		} else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
			File u = new File(System.getProperty("user.home"));
			File a = new File(u, "Application Data");
			if (!a.exists()) a.mkdir();
			File k = new File(a, "Kreative");
			if (!k.exists()) k.mkdir();
			return new File(k, "MapEdit.FontMap.txt");
		} else {
			File u = new File(System.getProperty("user.home"));
			return new File(u, ".mapedit.fontmap.txt");
		}
	}
}
