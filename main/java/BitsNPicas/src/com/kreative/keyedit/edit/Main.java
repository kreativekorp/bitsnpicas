package com.kreative.keyedit.edit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.kreative.keyedit.KeyboardFormat;
import com.kreative.keyedit.KeyboardMapping;

public class Main {
	public static void main(String[] args) {
		try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "KeyEdit"); } catch (Exception e) {}
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
			aacn.set(tk, "KeyEdit");
		} catch (Exception e) {}
		
		if (args.length == 0) {
			newMapping();
		} else {
			for (String arg : args) {
				openMapping(new File(arg));
			}
		}
		
		if (OSUtils.IS_MAC_OS) {
			try { Class.forName("com.kreative.keyedit.edit.mac.MyApplicationListener").newInstance(); }
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public static KeyEditFrame newMapping() {
		KeyboardMapping km = new KeyboardMapping();
		KeyEditFrame f = new KeyEditFrame(null, km);
		f.setVisible(true);
		return f;
	}
	
	public static KeyEditFrame openMapping() {
		FileDialog fd = new FileDialog(new Frame(), "Open", FileDialog.LOAD);
		fd.setVisible(true);
		if (fd.getDirectory() == null || fd.getFile() == null) return null;
		File file = new File(fd.getDirectory(), fd.getFile());
		return openMapping(file);
	}
	
	public static KeyEditFrame openMapping(File file) {
		if (file == null) return openMapping();
		
		KeyboardFormat fmt = KeyboardFormat.forInputFile(file);
		if (fmt == null) {
			JOptionPane.showMessageDialog(
				null, "The selected file was not recognized as a keyboard layout file readable by KeyEdit.",
				"Open", JOptionPane.ERROR_MESSAGE
			);
			return null;
		}
		
		try {
			KeyboardMapping km = fmt.read(file);
			if (km == null) {
				JOptionPane.showMessageDialog(
					null, "The selected file was not recognized as a keyboard layout file readable by KeyEdit.",
					"Open", JOptionPane.ERROR_MESSAGE
				);
				return null;
			}
			
			if (fmt != KeyboardFormat.KKB) file = null;
			KeyEditFrame f = new KeyEditFrame(file, km);
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
