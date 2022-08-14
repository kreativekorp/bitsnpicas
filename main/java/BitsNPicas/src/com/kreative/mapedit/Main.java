package com.kreative.mapedit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {
	public static void main(String[] args) {
		try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MapEdit"); } catch (Exception e) {}
		try { System.setProperty("apple.laf.useScreenMenuBar", "true"); } catch (Exception e) {}
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
		
		try {
			Method getModule = Class.class.getMethod("getModule");
			Object javaDesktop = getModule.invoke(Toolkit.getDefaultToolkit().getClass());
			Object allUnnamed = getModule.invoke(Main.class);
			Class<?> module = Class.forName("java.lang.Module");
			Method addOpens = module.getMethod("addOpens", String.class, module);
			addOpens.invoke(javaDesktop, "sun.awt.X11", allUnnamed);
		} catch (Exception e) {}
		
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Field aacn = tk.getClass().getDeclaredField("awtAppClassName");
			aacn.setAccessible(true);
			aacn.set(tk, "MapEdit");
		} catch (Exception e) {}
		
		if (MapEditMenuBar.IS_MAC_OS) {
			try { Class.forName("com.kreative.mapedit.mac.MacDummyWindow").newInstance(); }
			catch (Exception e) { e.printStackTrace(); }
		}
		
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
	
	public static MapEditFrame newMapping(Charset cs) {
		if (cs == null) {
			return newMapping();
		} else {
			Mapping m = new Mapping();
			m.decode(cs);
			MapEditFrame f = new MapEditFrame(null, m);
			f.setVisible(true);
			return f;
		}
	}
	
	private static String lastOpenDirectory = null;
	public static MapEditFrame openMapping() {
		Frame frame = new Frame();
		FileDialog fd = new FileDialog(frame, "Open", FileDialog.LOAD);
		if (lastOpenDirectory != null) fd.setDirectory(lastOpenDirectory);
		fd.setVisible(true);
		String ds = fd.getDirectory(), fs = fd.getFile();
		fd.dispose();
		frame.dispose();
		if (ds == null || fs == null) return null;
		File file = new File((lastOpenDirectory = ds), fs);
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
	
	private static String lastSaveDirectory = null;
	public static File getSaveFile(String suffix) {
		Frame frame = new Frame();
		FileDialog fd = new FileDialog(frame, "Save", FileDialog.SAVE);
		if (lastSaveDirectory != null) fd.setDirectory(lastSaveDirectory);
		fd.setVisible(true);
		String ds = fd.getDirectory(), fs = fd.getFile();
		fd.dispose();
		frame.dispose();
		if (ds == null || fs == null) return null;
		if (!fs.toLowerCase().endsWith(suffix.toLowerCase())) fs += suffix;
		return new File((lastSaveDirectory = ds), fs);
	}
}
