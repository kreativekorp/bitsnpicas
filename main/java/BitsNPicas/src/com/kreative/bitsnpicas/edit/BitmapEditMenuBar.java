package com.kreative.bitsnpicas.edit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
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
import com.kreative.bitsnpicas.Font;

public class BitmapEditMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public BitmapEditMenuBar(final Frame frame, final SaveManager sm, final BitmapFont font, final BitmapEditPanel panel) {
		add(new FileMenu(frame, sm, font, panel));
		add(new EditMenu(panel));
		add(new GlyphEditMenuBar.ViewMenu<BitmapFontGlyph>(frame, panel, BitmapFontGlyph.class));
		add(new TransformMenu(frame, panel.getGlyphComponent()));
		add(new WindowMenu(frame));
	}
	
	public static final class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Frame frame, final SaveManager sm, final BitmapFont font, final BitmapEditPanel panel) {
			super("File");
			add(new CommonMenuItems.NewMenu());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(frame));
			addSeparator();
			add(new CommonMenuItems.SaveMenuItem(sm));
			add(new CommonMenuItems.SaveAsMenuItem(sm));
			add(new BitmapListMenuBar.ExportMenuItem(font));
			addSeparator();
			add(new ImportMenuItem(frame, panel));
			addSeparator();
			add(new CommonMenuItems.FontInfoMenuItem(font, sm));
			add(new BitmapListMenuBar.PreviewMenuItem(font, panel.getGlyphList()));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
	
	public static final class ImportMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		private String lastDirectory = null;
		public ImportMenuItem(final Frame frame, final BitmapEditPanel panel) {
			super("Import Image...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FileDialog fd = new FileDialog(frame, "Import Image", FileDialog.LOAD);
					if (lastDirectory != null) fd.setDirectory(lastDirectory);
					fd.setVisible(true);
					String ds = fd.getDirectory(), fs = fd.getFile();
					fd.dispose();
					if (ds == null || fs == null) return;
					File file = new File((lastDirectory = ds), fs);
					try {
						BufferedImage image = ImageIO.read(file);
						if (image != null) {
							panel.getToolHandler().pushUndoState(null);
							Font<BitmapFontGlyph> font = panel.getGlyphFont();
							BitmapFontGlyph glyph = panel.getGlyph();
							glyph.setToImage(0, -font.getLineAscent(), image);
							glyph.setCharacterWidth(image.getWidth());
							panel.getGlyphComponent().glyphChanged();
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
	
	public static final class EditMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public EditMenu(final BitmapEditPanel panel) {
			super("Edit");
			BitmapToolHandler handler = panel.getToolHandler();
			add(new UndoMenuItem(handler));
			add(new RedoMenuItem(handler));
			addSeparator();
			add(new CutMenuItem(panel));
			add(new CopyMenuItem(panel));
			add(new PasteMenuItem(panel, false));
			add(new PasteMenuItem(panel, true));
			add(new ClearMenuItem(panel));
		}
	}
	
	public static final class UndoMenuItem extends JMenuItem {
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
	
	public static final class RedoMenuItem extends JMenuItem {
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
	
	public static final class CutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CutMenuItem(final BitmapEditPanel panel) {
			super("Cut");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					BitmapGlyphListSelection sel = new BitmapGlyphListSelection(Arrays.asList(panel.getGlyphLocator()));
					cb.setContents(sel, sel);
					panel.getToolHandler().pushUndoState(null);
					BitmapFontGlyph glyph = panel.getGlyph();
					glyph.setXY(0, 0);
					glyph.setGlyph(new byte[0][0]);
					glyph.setCharacterWidth(0);
					panel.getGlyphComponent().glyphChanged();
				}
			});
		}
	}
	
	public static final class CopyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyMenuItem(final BitmapEditPanel panel) {
			super("Copy");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					BitmapGlyphListSelection sel = new BitmapGlyphListSelection(Arrays.asList(panel.getGlyphLocator()));
					cb.setContents(sel, sel);
				}
			});
		}
	}
	
	public static final class PasteMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PasteMenuItem(final BitmapEditPanel panel, final boolean into) {
			super(into ? "Paste Into" : "Paste");
			setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_V,
				CommonMenuItems.SHORTCUT_KEY | (into ? KeyEvent.SHIFT_MASK : 0)
			));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
						if (cb.isDataFlavorAvailable(BitmapGlyphListSelection.flavor)) {
							BitmapGlyphState[] content = (BitmapGlyphState[])cb.getData(BitmapGlyphListSelection.flavor);
							if (content.length == 1) {
								panel.getToolHandler().pushUndoState(null);
								BitmapFontGlyph glyph = new BitmapFontGlyph();
								content[0].apply(glyph);
								if (into) glyph = BitmapFontGlyph.compose(panel.getGlyph(), glyph);
								new BitmapGlyphState(glyph).apply(panel.getGlyph());
								panel.getGlyphComponent().glyphChanged();
								return;
							}
						}
						if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
							Image content = (Image)cb.getData(DataFlavor.imageFlavor);
							BufferedImage image = SwingUtils.toBufferedImage(content);
							if (image != null) {
								panel.getToolHandler().pushUndoState(null);
								BitmapFontGlyph glyph = new BitmapFontGlyph();
								glyph.setToImage(0, -panel.getGlyphFont().getLineAscent(), image);
								glyph.setCharacterWidth(image.getWidth());
								if (into) glyph = BitmapFontGlyph.compose(panel.getGlyph(), glyph);
								new BitmapGlyphState(glyph).apply(panel.getGlyph());
								panel.getGlyphComponent().glyphChanged();
								return;
							}
						}
					} catch (IOException ioe) {
						ioe.printStackTrace();
					} catch (UnsupportedFlavorException ufe) {
						ufe.printStackTrace();
					}
					Toolkit.getDefaultToolkit().beep();
				}
			});
		}
	}
	
	public static final class ClearMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ClearMenuItem(final BitmapEditPanel panel) {
			super("Clear");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					panel.getToolHandler().pushUndoState(null);
					BitmapFontGlyph glyph = panel.getGlyph();
					glyph.setXY(0, 0);
					glyph.setGlyph(new byte[0][0]);
					glyph.setCharacterWidth(0);
					panel.getGlyphComponent().glyphChanged();
				}
			});
		}
	}
	
	public static final class TransformMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public TransformMenu(final Frame frame, final GlyphComponent<BitmapFontGlyph> gc) {
			super("Transform");
			add(new SetWidthMenuItem(frame, gc));
			add(new SetLeftBearingMenuItem(frame, gc));
			add(new SetRightBearingMenuItem(frame, gc));
			addSeparator();
			for (BitmapGlyphTransform tx : BitmapGlyphTransform.TRANSFORMS) {
				if (tx == null) addSeparator();
				else add(new TransformMenuItem(tx, gc));
			}
		}
	}
	
	public static final class TransformMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public TransformMenuItem(final BitmapGlyphTransform tx, final GlyphComponent<BitmapFontGlyph> gc) {
			super(tx.name);
			setAccelerator(tx.keystroke);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tx.transform(gc.getGlyphFont(), gc.getGlyph());
					gc.glyphChanged();
				}
			});
		}
	}
	
	public static final class SetWidthMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SetWidthMenuItem(final Frame frame, final GlyphComponent<BitmapFontGlyph> gc) {
			super("Set Width...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BitmapFontGlyph g = gc.getGlyph();
					Number width = g.getCharacterWidth();
					width = new SetWidthDialog(frame, "Set Width").showDialog(width);
					if (width == null) return;
					g.setCharacterWidth2D(width.doubleValue());
					gc.metricsChanged();
				}
			});
		}
	}
	
	public static final class SetLeftBearingMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SetLeftBearingMenuItem(final Frame frame, final GlyphComponent<BitmapFontGlyph> gc) {
			super("Set Left Bearing...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BitmapFontGlyph g = gc.getGlyph();
					Number lb = g.getGlyphOffset();
					lb = new SetWidthDialog(frame, "Set Left Bearing").showDialog(lb);
					if (lb == null) return;
					g.setXY(lb.intValue(), g.getY());
					gc.metricsChanged();
				}
			});
		}
	}
	
	public static final class SetRightBearingMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SetRightBearingMenuItem(final Frame frame, final GlyphComponent<BitmapFontGlyph> gc) {
			super("Set Right Bearing...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BitmapFontGlyph g = gc.getGlyph();
					Number rb = g.getCharacterWidth() - g.getGlyphOffset() - g.getGlyphWidth();
					rb = new SetWidthDialog(frame, "Set Right Bearing").showDialog(rb);
					if (rb == null) return;
					g.setCharacterWidth2D(g.getGlyphOffset2D() + g.getGlyphWidth2D() + rb.doubleValue());
					gc.metricsChanged();
				}
			});
		}
	}
}
