package com.kreative.bitsnpicas.edit;

import java.awt.FileDialog;
import java.awt.Frame;
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
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class BitmapEditMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public BitmapEditMenuBar(
		final Window window, final SaveManager sm, final BitmapFont font,
		final BitmapToolHandler handler, final GlyphComponent gc,
		final BitmapFontGlyph glyph, final int codePoint
	) {
		add(new FileMenu(window, sm, font, handler, glyph, gc));
		add(new EditMenu(handler, font, glyph, codePoint, gc));
		add(new GlyphEditMenuBar.ViewMenu(gc));
		add(new TransformMenu(font, glyph, gc));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(
			final Window window, final SaveManager sm, final BitmapFont font,
			final BitmapToolHandler handler, final BitmapFontGlyph glyph,
			final GlyphComponent gc
		) {
			super("File");
			add(new CommonMenuItems.NewBitmapFontMenuItem());
			// add(new CommonMenuItems.NewVectorFontMenuItem());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			addSeparator();
			add(new CommonMenuItems.SaveMenuItem(sm));
			add(new CommonMenuItems.SaveAsMenuItem(sm));
			add(new BitmapListMenuBar.ExportMenuItem(font));
			addSeparator();
			add(new ImportMenuItem(handler, glyph, gc));
			addSeparator();
			add(new CommonMenuItems.FontInfoMenuItem(font));
			add(new BitmapListMenuBar.PreviewMenuItem(font));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
	
	public static class ImportMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ImportMenuItem(final BitmapToolHandler handler, final BitmapFontGlyph glyph, final GlyphComponent gc) {
			super("Import Image...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FileDialog fd = new FileDialog(new Frame(), "Import Image", FileDialog.LOAD);
					fd.setVisible(true);
					if (fd.getDirectory() == null || fd.getFile() == null) return;
					File file = new File(fd.getDirectory(), fd.getFile());
					try {
						BufferedImage image = ImageIO.read(file);
						if (image != null) {
							handler.pushUndoState(null);
							BitmapGlyphOps.setToImage(glyph, 0, -image.getHeight(), image);
							glyph.setCharacterWidth(image.getWidth());
							gc.glyphChanged();
						} else {
							JOptionPane.showMessageDialog(
								null, "The selected file was not recognized as an image file.",
								"Import Image", JOptionPane.ERROR_MESSAGE
							);
						}
					} catch (IOException ioe) {
						JOptionPane.showMessageDialog(
							null, "An error occurred while reading the selected file.",
							"Import Image", JOptionPane.ERROR_MESSAGE
						);
					}
				}
			});
		}
	}
	
	public static class EditMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public EditMenu(
			final BitmapToolHandler handler, final BitmapFont font,
			final BitmapFontGlyph glyph, final int codePoint,
			final GlyphComponent gc
		) {
			super("Edit");
			add(new UndoMenuItem(handler));
			add(new RedoMenuItem(handler));
			addSeparator();
			add(new CutMenuItem(font, glyph, codePoint, handler, gc));
			add(new CopyMenuItem(font, glyph, codePoint));
			add(new PasteMenuItem(font, glyph, handler, gc));
			add(new ClearMenuItem(glyph, handler, gc));
		}
	}
	
	public static class UndoMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public UndoMenuItem(final BitmapToolHandler handler) {
			super("Undo");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handler.undo();
				}
			});
		}
	}
	
	public static class RedoMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public RedoMenuItem(final BitmapToolHandler handler) {
			super("Redo");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handler.redo();
				}
			});
		}
	}
	
	public static class CutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CutMenuItem(
			final BitmapFont font, final BitmapFontGlyph glyph, final int codePoint,
			final BitmapToolHandler handler, final GlyphComponent gc
		) {
			super("Cut");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new BitmapGlyphListSelection(font, Arrays.asList(codePoint)), new ClipboardOwner() {
						public void lostOwnership(Clipboard cb, Transferable t) {}
					});
					handler.pushUndoState(null);
					glyph.setXY(0, 0);
					glyph.setGlyph(new byte[0][0]);
					glyph.setCharacterWidth(0);
					gc.glyphChanged();
				}
			});
		}
	}
	
	public static class CopyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyMenuItem(final BitmapFont font, final BitmapFontGlyph glyph, final int codePoint) {
			super("Copy");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new BitmapGlyphListSelection(font, Arrays.asList(codePoint)), new ClipboardOwner() {
						public void lostOwnership(Clipboard cb, Transferable t) {}
					});
				}
			});
		}
	}
	
	public static class PasteMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PasteMenuItem(final BitmapFont font, final BitmapFontGlyph glyph, final BitmapToolHandler handler, final GlyphComponent gc) {
			super("Paste");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
						if (cb.isDataFlavorAvailable(BitmapGlyphListSelection.flavor)) {
							BitmapGlyphState[] content = (BitmapGlyphState[])cb.getData(BitmapGlyphListSelection.flavor);
							if (content.length == 1) {
								handler.pushUndoState(null);
								content[0].apply(glyph);
								gc.glyphChanged();
								return;
							}
						}
						if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
							Image content = (Image)cb.getData(DataFlavor.imageFlavor);
							BufferedImage image = SwingUtils.toBufferedImage(content);
							if (image != null) {
								handler.pushUndoState(null);
								BitmapGlyphOps.setToImage(glyph, 0, -font.getEmAscent(), image);
								glyph.setCharacterWidth(image.getWidth());
								gc.glyphChanged();
								return;
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
		public ClearMenuItem(final BitmapFontGlyph glyph, final BitmapToolHandler handler, final GlyphComponent gc) {
			super("Clear");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					handler.pushUndoState(null);
					glyph.setXY(0, 0);
					glyph.setGlyph(new byte[0][0]);
					glyph.setCharacterWidth(0);
					gc.glyphChanged();
				}
			});
		}
	}
	
	public static class TransformMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public TransformMenu(final BitmapFont font, final BitmapFontGlyph glyph, final GlyphComponent gc) {
			super("Transform");
			add(new TransformMenuItem(font, glyph, gc, new BitmapGlyphTransform.Bold(), "Bold"));
			add(new TransformMenuItem(font, glyph, gc, new BitmapGlyphTransform.Invert(), "Invert"));
		}
	}
	
	public static class TransformMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public TransformMenuItem(final BitmapFont font, final BitmapFontGlyph glyph, final GlyphComponent gc, final BitmapGlyphTransform tx, final String name) {
			super(name);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tx.transform(font, glyph);
					gc.glyphChanged();
				}
			});
		}
	}
}
