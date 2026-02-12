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
import com.kreative.bitsnpicas.FontImporter;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.VectorFont;
import com.kreative.bitsnpicas.VectorFontExporter;
import com.kreative.bitsnpicas.VectorFontGlyph;
import com.kreative.bitsnpicas.edit.importer.ImportFormat;
import com.kreative.bitsnpicas.exporter.KbitxBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.KpcaxVectorFontExporter;

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
		
		if (CommonMenuItems.IS_MAC_OS) {
			try { Class.forName("com.kreative.bitsnpicas.edit.mac.MacDummyWindow").newInstance(); }
			catch (Exception e) { e.printStackTrace(); }
		}
		
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
		BitmapFont bfont = new BitmapFont(14, 2, 14, 2, 8, 10, 0, 16);
		bfont.autoFillNames();
		return openFont(null, new KbitxBitmapFontExporter(), bfont);
	}
	
	public static JFrame newVectorFont() {
		VectorFont vfont = new VectorFont(800, 200, 800, 200, 0, 0, 0, 1000);
		vfont.autoFillNames();
		return openFont(null, new KpcaxVectorFontExporter(), vfont);
	}
	
	private static String lastOpenDirectory = null;
	public static JFrame openFonts() {
		Frame frame = new Frame();
		FileDialog fd = new FileDialog(frame, "Open", FileDialog.LOAD);
		if (lastOpenDirectory != null) fd.setDirectory(lastOpenDirectory);
		fd.setVisible(true);
		String ds = fd.getDirectory(), fs = fd.getFile();
		fd.dispose();
		frame.dispose();
		if (ds == null || fs == null) return null;
		File file = new File((lastOpenDirectory = ds), fs);
		return openFonts(file);
	}
	
	public static JFrame openFonts(File file) {
		ImportFormat format = ImportFormat.forFile(file);
		if (format != null) return openFonts(file, format);
		JFrame f = new FormatListFrame(file);
		f.setVisible(true);
		return f;
	}
	
	public static JFrame openFonts(File file, ImportFormat format) {
		try {
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
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				null, "An error occurred while reading the selected file.",
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
			JFrame f = new GlyphListFrame<VectorFontGlyph>(fontFile, vformat, vfont);
			f.setVisible(true);
			return f;
		} else {
			JOptionPane.showMessageDialog(
				null, "The selected font was not recognized by the Bits'n'Picas editor.",
				"Open", JOptionPane.ERROR_MESSAGE
			);
			return null;
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
			JFrame f = new GlyphListFrame<VectorFontGlyph>(routine, vfont);
			f.setVisible(true);
			return f;
		} else {
			JOptionPane.showMessageDialog(
				null, "The selected font was not recognized by the Bits'n'Picas editor.",
				"Open", JOptionPane.ERROR_MESSAGE
			);
			return null;
		}
	}
	
	public static JFrame openGlyph(Font<?> font, GlyphLocator<?> loc, GlyphList<?> gl, SaveManager sm) {
		if (font instanceof BitmapFont) {
			BitmapFont bfont = (BitmapFont)font;
			@SuppressWarnings("unchecked")
			GlyphLocator<BitmapFontGlyph> bloc = (GlyphLocator<BitmapFontGlyph>)loc;
			@SuppressWarnings("unchecked")
			GlyphList<BitmapFontGlyph> bgl = (GlyphList<BitmapFontGlyph>)gl;
			if (bloc.getGlyph() == null) {
				BitmapFontGlyph g = new BitmapFontGlyph();
				g.setCharacterWidth(bfont.getNewGlyphWidth());
				bloc.setGlyph(g);
				bgl.glyphRepertoireChanged();
			}
			JFrame f = new BitmapEditFrame(bfont, bloc, bgl, sm);
			f.setVisible(true);
			return f;
		} else if (font instanceof VectorFont) {
			VectorFont vfont = (VectorFont)font;
			@SuppressWarnings("unchecked")
			GlyphLocator<VectorFontGlyph> vloc = (GlyphLocator<VectorFontGlyph>)loc;
			@SuppressWarnings("unchecked")
			GlyphList<VectorFontGlyph> vgl = (GlyphList<VectorFontGlyph>)gl;
			if (vloc.getGlyph() == null) {
				VectorFontGlyph g = new VectorFontGlyph();
				g.setCharacterWidth2D(vfont.getNewGlyphWidth2D());
				vloc.setGlyph(g);
				vgl.glyphRepertoireChanged();
			}
			JFrame f = new GlyphEditFrame<VectorFontGlyph>(VectorFontGlyph.class, vfont, vloc, vgl, sm);
			f.setVisible(true);
			return f;
		} else {
			JOptionPane.showMessageDialog(
				null, "The selected font was not recognized by the Bits'n'Picas editor.",
				"Open", JOptionPane.ERROR_MESSAGE
			);
			return null;
		}
	}
	
	public static String getSaveSuffix(Font<?> font) {
		if (font instanceof BitmapFont) return ".kbitx";
		if (font instanceof VectorFont) return ".kpcax";
		return null;
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
	
	public static FontExporter<?> getSaveFormat(Font<?> font) {
		if (font instanceof BitmapFont) return new KbitxBitmapFontExporter();
		if (font instanceof VectorFont) return new KpcaxVectorFontExporter();
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
