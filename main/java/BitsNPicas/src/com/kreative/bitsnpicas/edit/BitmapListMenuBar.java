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
import java.util.List;
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
		add(new TransformMenu(font, gl));
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
			add(new GlyphListMenuBar.SelectAllMenuItem(gl));
			add(new GlyphListMenuBar.SelectNoneMenuItem(gl));
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
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new BitmapGlyphListSelection(font, gl.getSelectedCodePoints()), new ClipboardOwner() {
						public void lostOwnership(Clipboard cb, Transferable t) {}
					});
					for (int cp : gl.getSelectedCodePoints()) {
						font.removeCharacter(cp);
					}
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
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new BitmapGlyphListSelection(font, gl.getSelectedCodePoints()), new ClipboardOwner() {
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
					try {
						Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
						if (cb.isDataFlavorAvailable(BitmapGlyphListSelection.flavor)) {
							BitmapGlyphState[] content = (BitmapGlyphState[])cb.getData(BitmapGlyphListSelection.flavor);
							if (content.length > 0) {
								List<Integer> cps = gl.getSelectedCodePoints();
								for (int i = 0, n = cps.size(); i < n; i++) {
									BitmapFontGlyph glyph = new BitmapFontGlyph();
									content[i % content.length].apply(glyph);
									font.putCharacter(cps.get(i), glyph);
								}
							}
							gl.repaint();
						} else if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
							Image content = (Image)cb.getData(DataFlavor.imageFlavor);
							BufferedImage image = SwingUtils.toBufferedImage(content);
							if (image != null) {
								for (int cp : gl.getSelectedCodePoints()) {
									BitmapFontGlyph glyph = new BitmapFontGlyph();
									BitmapGlyphOps.setToImage(glyph, 0, -font.getEmAscent(), image);
									glyph.setCharacterWidth(image.getWidth());
									font.putCharacter(cp, glyph);
								}
							}
							gl.repaint();
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
					for (int cp : gl.getSelectedCodePoints()) {
						font.removeCharacter(cp);
					}
					gl.repaint();
				}
			});
		}
	}
	
	public static class TransformMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public TransformMenu(final BitmapFont font, final GlyphList gl) {
			super("Transform");
			add(new TransformMenuItem(font, gl, new BitmapGlyphTransform.Bold(), "Bold"));
			add(new TransformMenuItem(font, gl, new BitmapGlyphTransform.Invert(), "Invert"));
		}
	}
	
	public static class TransformMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public TransformMenuItem(final BitmapFont font, final GlyphList gl, final BitmapGlyphTransform tx, final String name) {
			super(name);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int cp : gl.getSelectedCodePoints()) {
						BitmapFontGlyph glyph = font.getCharacter(cp);
						if (glyph != null) tx.transform(font, glyph);
					}
					gl.repaint();
				}
			});
		}
	}
}
