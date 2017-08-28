package com.kreative.bitsnpicas.edit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontExporter;
import com.kreative.bitsnpicas.FontGlyph;
import com.kreative.bitsnpicas.VectorFont;
import com.kreative.bitsnpicas.VectorFontExporter;
import com.kreative.bitsnpicas.VectorFontGlyph;
import com.kreative.bitsnpicas.exporter.KBnPBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.KBnPVectorFontExporter;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.KBnPBitmapFontImporter;
import com.kreative.bitsnpicas.importer.KBnPVectorFontImporter;
import com.kreative.bitsnpicas.importer.NFNTBitmapFontImporter;
import com.kreative.bitsnpicas.importer.S10BitmapFontImporter;
import com.kreative.bitsnpicas.importer.SFDBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SRFontBitmapFontImporter;

public class Main {
	public static void main(String[] args) {
		try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Bits'n'Picas"); } catch (Exception e) {}
		try { System.setProperty("apple.laf.useScreenMenuBar", "true"); } catch (Exception e) {}
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
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
		BitmapFont bfont = new BitmapFont();
		bfont.autoFillNames();
		return openFont(null, new KBnPBitmapFontExporter(), bfont);
	}
	
	public static JFrame newVectorFont() {
		VectorFont vfont = new VectorFont();
		vfont.autoFillNames();
		return openFont(null, new KBnPVectorFontExporter(), vfont);
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
			String lname = file.getName().toLowerCase();
			if (lname.endsWith(".kbits")) {
				BitmapFont[] fonts = new KBnPBitmapFontImporter().importFont(file);
				return openFonts(file, new KBnPBitmapFontExporter(), fonts);
			} else if (lname.endsWith(".kpcas")) {
				VectorFont[] fonts = new KBnPVectorFontImporter().importFont(file);
				return openFonts(file, new KBnPVectorFontExporter(), fonts);
			} else if (lname.endsWith(".sfd")) {
				BitmapFont[] fonts = new SFDBitmapFontImporter().importFont(file);
				return openFonts(file, null, fonts);
			} else if (lname.endsWith(".suit")) {
				file = new File(file, "..namedfork");
				file = new File(file, "rsrc");
				BitmapFont[] fonts = new NFNTBitmapFontImporter().importFont(file);
				return openFonts(file, null, fonts);
			} else if (lname.endsWith(".dfont")) {
				BitmapFont[] fonts = new NFNTBitmapFontImporter().importFont(file);
				return openFonts(file, null, fonts);
			} else if (lname.endsWith(".png")) {
				BitmapFont[] fonts = new SRFontBitmapFontImporter().importFont(file);
				return openFonts(file, null, fonts);
			} else if (lname.endsWith(".dsf")) {
				BitmapFont[] fonts = new DSFBitmapFontImporter().importFont(file);
				return openFonts(file, null, fonts);
			} else if (lname.endsWith(".s10")) {
				BitmapFont[] fonts = new S10BitmapFontImporter().importFont(file);
				return openFonts(file, null, fonts);
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
	
	public static JFrame openGlyph(Font<?> font, FontGlyph glyph, int codePoint, GlyphList gl, SaveManager sm) {
		if (font instanceof BitmapFont) {
			BitmapFont bfont = (BitmapFont)font;
			BitmapFontGlyph bglyph = (BitmapFontGlyph)glyph;
			if (bglyph == null) {
				bglyph = new BitmapFontGlyph();
				bfont.putCharacter(codePoint, bglyph);
				gl.repaint();
			}
			JFrame f = new BitmapEditFrame(bfont, bglyph, codePoint, gl, sm);
			f.setVisible(true);
			return f;
		} else if (font instanceof VectorFont) {
			VectorFont vfont = (VectorFont)font;
			VectorFontGlyph vglyph = (VectorFontGlyph)glyph;
			if (vglyph == null) {
				vglyph = new VectorFontGlyph();
				vfont.putCharacter(codePoint, vglyph);
				gl.repaint();
			}
			JFrame f = new GlyphEditFrame(vfont, vglyph, codePoint, gl, sm);
			f.setVisible(true);
			return f;
		} else {
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
		if (font instanceof BitmapFont) return new KBnPBitmapFontExporter();
		if (font instanceof VectorFont) return new KBnPVectorFontExporter();
		return null;
	}
	
	public static boolean saveFont(File fontFile, FontExporter<?> format, Font<?> font) {
		if (font instanceof BitmapFont) {
			BitmapFontExporter bformat = (BitmapFontExporter)format;
			BitmapFont bfont = (BitmapFont)font;
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
