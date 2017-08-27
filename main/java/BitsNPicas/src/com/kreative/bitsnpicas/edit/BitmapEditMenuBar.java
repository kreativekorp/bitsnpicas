package com.kreative.bitsnpicas.edit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
	
	public BitmapEditMenuBar(final Window window, final SaveManager sm, final BitmapFont font, final BitmapToolHandler handler, final GlyphComponent gc) {
		add(new FileMenu(window, sm, font, handler, gc));
		add(new EditMenu(handler));
		add(new GlyphEditMenuBar.ViewMenu(gc));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final SaveManager sm, final BitmapFont font, final BitmapToolHandler handler, final GlyphComponent gc) {
			super("File");
			add(new CommonMenuItems.NewBitmapFontMenuItem());
			add(new CommonMenuItems.NewVectorFontMenuItem());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			addSeparator();
			add(new CommonMenuItems.SaveMenuItem(sm));
			add(new CommonMenuItems.SaveAsMenuItem(sm));
			add(new BitmapListMenuBar.ExportMenuItem(font));
			addSeparator();
			add(new ImportMenuItem(handler, gc));
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
		public ImportMenuItem(final BitmapToolHandler handler, final GlyphComponent gc) {
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
							BitmapGlyphOps.setToImage(
								(BitmapFontGlyph)gc.getGlyph(),
								0, -image.getHeight(), image
							);
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
		public EditMenu(final BitmapToolHandler handler) {
			super("Edit");
			add(new UndoMenuItem(handler));
			add(new RedoMenuItem(handler));
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
}
