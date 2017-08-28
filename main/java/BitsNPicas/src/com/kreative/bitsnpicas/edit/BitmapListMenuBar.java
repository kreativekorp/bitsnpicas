package com.kreative.bitsnpicas.edit;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.main.ViewFont;

public class BitmapListMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public BitmapListMenuBar(final Window window, final SaveManager sm, final BitmapFont font, final GlyphList gl) {
		add(new FileMenu(window, sm, font));
		add(new EditMenu(font, gl, sm));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final SaveManager sm, final BitmapFont font) {
			super("File");
			add(new CommonMenuItems.NewBitmapFontMenuItem());
			// add(new CommonMenuItems.NewVectorFontMenuItem());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			addSeparator();
			add(new CommonMenuItems.SaveMenuItem(sm));
			add(new CommonMenuItems.SaveAsMenuItem(sm));
			add(new ExportMenuItem(font));
			addSeparator();
			add(new CommonMenuItems.FontInfoMenuItem(font));
			add(new PreviewMenuItem(font));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
	
	public static class ExportMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ExportMenuItem(final BitmapFont font) {
			super("Export...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new BitmapExportFrame(font).setVisible(true);
				}
			});
		}
	}
	
	public static class PreviewMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PreviewMenuItem(final BitmapFont font) {
			super("Preview");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new ViewFont(font).setVisible(true);
				}
			});
		}
	}
	
	public static class EditMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public EditMenu(final BitmapFont font, final GlyphList gl, final SaveManager sm) {
			super("Edit");
			add(new CutMenuItem(font, gl));
			add(new CopyMenuItem(font, gl));
			add(new PasteMenuItem(font, gl));
			add(new ClearMenuItem(font, gl));
			addSeparator();
			add(new GlyphListMenuBar.EditMenuItem(font, gl, sm));
			add(new GlyphListMenuBar.DeleteMenuItem(font, gl));
		}
	}
	
	public static class CutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CutMenuItem(final BitmapFont font, final GlyphList gl) {
			super("Cut");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int codePoint = gl.getSelectedCodePoint();
					if (codePoint < 0) return;
					BitmapFontGlyph glyph = font.getCharacter(codePoint);
					if (glyph == null) return;
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new BitmapGlyphSelection(font, glyph, codePoint), new ClipboardOwner() {
						public void lostOwnership(Clipboard cb, Transferable t) {}
					});
					font.removeCharacter(codePoint);
					gl.repaint();
				}
			});
		}
	}
	
	public static class CopyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyMenuItem(final BitmapFont font, final GlyphList gl) {
			super("Copy");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int codePoint = gl.getSelectedCodePoint();
					if (codePoint < 0) return;
					BitmapFontGlyph glyph = font.getCharacter(codePoint);
					if (glyph == null) return;
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new BitmapGlyphSelection(font, glyph, codePoint), new ClipboardOwner() {
						public void lostOwnership(Clipboard cb, Transferable t) {}
					});
				}
			});
		}
	}
	
	public static class PasteMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PasteMenuItem(final BitmapFont font, final GlyphList gl) {
			super("Paste");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int codePoint = gl.getSelectedCodePoint();
					if (codePoint < 0) return;
					try {
						Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
						if (cb.isDataFlavorAvailable(BitmapGlyphSelection.flavor)) {
							BitmapGlyphState content = (BitmapGlyphState)cb.getData(BitmapGlyphSelection.flavor);
							BitmapFontGlyph glyph = new BitmapFontGlyph();
							content.apply(glyph);
							font.putCharacter(codePoint, glyph);
							gl.repaint();
						} else if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
							Image content = (Image)cb.getData(DataFlavor.imageFlavor);
							BufferedImage image = SwingUtils.toBufferedImage(content);
							if (image != null) {
								BitmapFontGlyph glyph = new BitmapFontGlyph();
								BitmapGlyphOps.setToImage(glyph, 0, -image.getHeight(), image);
								glyph.setCharacterWidth(image.getWidth());
								font.putCharacter(codePoint, glyph);
								gl.repaint();
							}
						}
					} catch (IOException ioe) {
						ioe.printStackTrace();
					} catch (UnsupportedFlavorException ufe) {
						ufe.printStackTrace();
					}
				}
			});
		}
	}
	
	public static class ClearMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ClearMenuItem(final BitmapFont font, final GlyphList gl) {
			super("Clear");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int codePoint = gl.getSelectedCodePoint();
					if (codePoint < 0) return;
					font.removeCharacter(codePoint);
					gl.repaint();
				}
			});
		}
	}
}
