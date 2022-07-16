package com.kreative.bitsnpicas.edit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontExporter;
import com.kreative.bitsnpicas.FontGlyph;
import com.kreative.bitsnpicas.FontImporter;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.VectorFont;
import com.kreative.bitsnpicas.VectorFontExporter;
import com.kreative.bitsnpicas.VectorFontGlyph;
import com.kreative.bitsnpicas.exporter.KbitsBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.KpcasVectorFontExporter;

public class Main {
	public static void main(String[] args) {
		try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Bits'n'Picas"); } catch (Exception e) {}
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
			aacn.set(tk, "BitsNPicas");
		} catch (Exception e) {}
		
		if (args.length == 0) {
			newBitmapFont();
		} else {
			for (String arg : args) {
				openFonts(new File(arg));
			}
		}
		
		if (CommonMenuItems.IS_MAC_OS) {
			try { Class.forName("com.kreative.bitsnpicas.edit.mac.MyApplicationListener").newInstance(); }
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public static JFrame newBitmapFont() {
		BitmapFont bfont = new BitmapFont(14, 2, 14, 2, 0, 0, 0);
		bfont.autoFillNames();
		return openFont(null, new KbitsBitmapFontExporter(), bfont);
	}
	
	public static JFrame newVectorFont() {
		VectorFont vfont = new VectorFont(800, 200, 800, 200, 0, 0, 0);
		vfont.autoFillNames();
		return openFont(null, new KpcasVectorFontExporter(), vfont);
	}
	
	public static JFrame openFonts() {
		FileDialog fd = new FileDialog(new Frame(), "Open", FileDialog.LOAD);
		fd.setVisible(true);
		if (fd.getDirectory() == null || fd.getFile() == null) return null;
		File file = new File(fd.getDirectory(), fd.getFile());
		return openFonts(file);
	}
	
	public static JFrame openFonts(File file) {
		try {
			ImportFormat format = ImportFormat.forFile(file);
			if (format != null) {
				if (format.macResFork) file = MacUtility.getResourceFork(file);
				FontImporter<?> importer = format.createImporter();
				if (importer != null) {
					Font<?>[] fonts = importer.importFont(file);
					if (fonts != null && fonts.length > 0) {
						return openFonts(file, format.createExporter(), fonts);
					}
				}
				JFrame f = format.createOptionFrame(file);
				if (f != null) {
					f.setVisible(true);
					return f;
				}
				JOptionPane.showMessageDialog(
					null, "The selected file did not contain any fonts.",
					"Open", JOptionPane.ERROR_MESSAGE
				);
				return null;
			} else {
				JOptionPane.showMessageDialog(
					null, "The selected file was not recognized as a font file readable by Bits'n'Picas.",
					"Open", JOptionPane.ERROR_MESSAGE
				);
				return null;
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				null, "An error occurred while reading the selected file.",
				"Open", JOptionPane.ERROR_MESSAGE
			);
			return null;
		} catch (NoClassDefFoundError e) {
			JOptionPane.showMessageDialog(
				null, "The selected file requires KSFL, but KSFL is not in the classpath.",
				"Open", JOptionPane.ERROR_MESSAGE
			);
			return null;
		}
	}
	
	public static JFrame openFonts(File fontFile, FontExporter<?> format, Font<?>[] fonts) {
		if (fonts.length > 1) {
			JFrame f = new FontListFrame(fontFile, format, fonts);
			f.setVisible(true);
			return f;
		} else if (fonts.length > 0) {
			return openFont(fontFile, format, fonts[0]);
		} else {
			JOptionPane.showMessageDialog(
				null, "The selected file did not contain any fonts.",
				"Open", JOptionPane.ERROR_MESSAGE
			);
			return null;
		}
	}
	
	public static JFrame openFont(File fontFile, FontExporter<?> format, Font<?> font) {
		if (font instanceof BitmapFont) {
			BitmapFontExporter bformat = (BitmapFontExporter)format;
			BitmapFont bfont = (BitmapFont)font;
			JFrame f = new BitmapListFrame(fontFile, bformat, bfont);
			f.setVisible(true);
			return f;
		} else if (font instanceof VectorFont) {
			VectorFontExporter vformat = (VectorFontExporter)format;
			VectorFont vfont = (VectorFont)font;
			JFrame f = new GlyphListFrame(fontFile, vformat, vfont);
			f.setVisible(true);
			return f;
		} else {
			JFrame f = new GlyphListFrame(fontFile, format, font);
			f.setVisible(true);
			return f;
		}
	}
	
	public static JFrame openFont(SaveRoutine routine, Font<?> font) {
		if (font instanceof BitmapFont) {
			BitmapFont bfont = (BitmapFont)font;
			JFrame f = new BitmapListFrame(routine, bfont);
			f.setVisible(true);
			return f;
		} else if (font instanceof VectorFont) {
			VectorFont vfont = (VectorFont)font;
			JFrame f = new GlyphListFrame(routine, vfont);
			f.setVisible(true);
			return f;
		} else {
			JFrame f = new GlyphListFrame(routine, font);
			f.setVisible(true);
			return f;
		}
	}
	
	public static JFrame openGlyph(Font<?> font, int codePoint, GlyphList gl, SaveManager sm) {
		if (font instanceof BitmapFont) {
			BitmapFont bfont = (BitmapFont)font;
			BitmapFontGlyph bglyph = bfont.getCharacter(codePoint);
			if (bglyph == null) {
				bglyph = new BitmapFontGlyph();
				bfont.putCharacter(codePoint, bglyph);
				gl.glyphsChanged();
			}
			JFrame f = new BitmapEditFrame(bfont, bglyph, codePoint, gl, sm);
			f.setVisible(true);
			return f;
		} else if (font instanceof VectorFont) {
			VectorFont vfont = (VectorFont)font;
			VectorFontGlyph vglyph = vfont.getCharacter(codePoint);
			if (vglyph == null) {
				vglyph = new VectorFontGlyph();
				vfont.putCharacter(codePoint, vglyph);
				gl.glyphsChanged();
			}
			JFrame f = new GlyphEditFrame(vfont, vglyph, codePoint, gl, sm);
			f.setVisible(true);
			return f;
		} else {
			FontGlyph glyph = font.getCharacter(codePoint);
			if (glyph == null) return null;
			JFrame f = new GlyphEditFrame(font, glyph, codePoint, gl, sm);
			f.setVisible(true);
			return f;
		}
	}
	
	public static String getSaveSuffix(Font<?> font) {
		if (font instanceof BitmapFont) return ".kbits";
		if (font instanceof VectorFont) return ".kpcas";
		return null;
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
	
	public static FontExporter<?> getSaveFormat(Font<?> font) {
		if (font instanceof BitmapFont) return new KbitsBitmapFontExporter();
		if (font instanceof VectorFont) return new KpcasVectorFontExporter();
		return null;
	}
	
	public static boolean saveFont(File fontFile, FontExporter<?> format, Font<?> font) {
		if (font instanceof BitmapFont) {
			BitmapFontExporter bformat = (BitmapFontExporter)format;
			BitmapFont bfont = (BitmapFont)font;
			bfont.contractGlyphs();
			try {
				bformat.exportFontToFile(bfont, fontFile);
				return true;
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(
					null, "An error occurred while saving this file.",
					"Save", JOptionPane.ERROR_MESSAGE
				);
				return false;
			} catch (NoClassDefFoundError e) {
				JOptionPane.showMessageDialog(
					null, "The selected format requires KSFL, but KSFL is not in the classpath.",
					"Save", JOptionPane.ERROR_MESSAGE
				);
				return false;
			}
		} else if (font instanceof VectorFont) {
			VectorFontExporter vformat = (VectorFontExporter)format;
			VectorFont vfont = (VectorFont)font;
			try {
				vformat.exportFontToFile(vfont, fontFile);
				return true;
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(
					null, "An error occurred while saving this file.",
					"Save", JOptionPane.ERROR_MESSAGE
				);
				return false;
			}
		} else {
			JOptionPane.showMessageDialog(
				null, "An error occurred while saving this file.",
				"Save", JOptionPane.ERROR_MESSAGE
			);
			return false;
		}
	}
}
