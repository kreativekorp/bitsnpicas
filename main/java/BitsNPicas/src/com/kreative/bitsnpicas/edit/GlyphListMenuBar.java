package com.kreative.bitsnpicas.edit;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.Font;

public class GlyphListMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public GlyphListMenuBar(final Window window, final SaveManager sm, final Font<?> font, final GlyphList gl) {
		add(new FileMenu(window, sm, font));
		add(new EditMenu(gl, window, font, sm));
		add(new ViewMenu(window, gl));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final SaveManager sm, final Font<?> font) {
			super("File");
			add(new CommonMenuItems.NewBitmapFontMenuItem());
			// add(new CommonMenuItems.NewVectorFontMenuItem());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			addSeparator();
			add(new CommonMenuItems.SaveMenuItem(sm));
			add(new CommonMenuItems.SaveAsMenuItem(sm));
			addSeparator();
			add(new CommonMenuItems.FontInfoMenuItem(font, sm));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
	
	public static class EditMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public EditMenu(final GlyphList gl, final Window window, final Font<?> font, final SaveManager sm) {
			super("Edit");
			add(new SelectAllMenuItem(gl));
			add(new SelectNoneMenuItem(gl));
			add(new SetSelectionMenuItem(window, gl));
			addSeparator();
			add(new EditMenuItem(font, gl, sm));
			add(new DeleteMenuItem(font, gl));
		}
	}
	
	public static class SelectAllMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SelectAllMenuItem(final GlyphList gl) {
			super("Select All");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.selectAll();
				}
			});
		}
	}
	
	public static class SelectNoneMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SelectNoneMenuItem(final GlyphList gl) {
			super("Select None");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.clearSelection();
				}
			});
		}
	}
	
	public static class SetSelectionMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SetSelectionMenuItem(final Window window, final GlyphList gl) {
			super("Set Selection...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SetSelectionDialog(window, gl).setVisible(true);
				}
			});
		}
	}
	
	public static class EditMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public EditMenuItem(final Font<?> font, final GlyphList gl, final SaveManager sm) {
			super("Edit Glyphs");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int cp : gl.getSelectedCodePoints()) {
						Main.openGlyph(font, cp, gl, sm);
					}
				}
			});
		}
	}
	
	public static class DeleteMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public DeleteMenuItem(final Font<?> font, final GlyphList gl) {
			super("Delete Glyphs");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int cp : gl.getSelectedCodePoints()) {
						font.removeCharacter(cp);
					}
					gl.glyphsChanged();
				}
			});
		}
	}
	
	public static class ViewMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public ViewMenu(final Window window, final GlyphList gl) {
			super("View");
			add(new CellSizeMenuItem(window, gl, 12, KeyStroke.getKeyStroke(KeyEvent.VK_1, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(window, gl, 24, KeyStroke.getKeyStroke(KeyEvent.VK_2, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(window, gl, 36, KeyStroke.getKeyStroke(KeyEvent.VK_3, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(window, gl, 48, KeyStroke.getKeyStroke(KeyEvent.VK_4, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(window, gl, 72, KeyStroke.getKeyStroke(KeyEvent.VK_5, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(window, gl, 96, KeyStroke.getKeyStroke(KeyEvent.VK_6, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(window, gl, 128, KeyStroke.getKeyStroke(KeyEvent.VK_7, CommonMenuItems.SHORTCUT_KEY)));
			addSeparator();
			add(new ColumnCountMenuItem(window, gl, 8, KeyStroke.getKeyStroke(KeyEvent.VK_8, CommonMenuItems.SHORTCUT_KEY)));
			add(new ColumnCountMenuItem(window, gl, 16, KeyStroke.getKeyStroke(KeyEvent.VK_9, CommonMenuItems.SHORTCUT_KEY)));
			add(new ColumnCountMenuItem(window, gl, 32, KeyStroke.getKeyStroke(KeyEvent.VK_0, CommonMenuItems.SHORTCUT_KEY)));
		}
	}
	
	public static class CellSizeMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CellSizeMenuItem(final Window window, final GlyphList gl, final int cellSize, final KeyStroke ks) {
			super(cellSize + " Pixel Cell Size");
			setAccelerator(ks);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.setCellSize(cellSize);
					window.pack();
				}
			});
		}
	}
	
	public static class ColumnCountMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ColumnCountMenuItem(final Window window, final GlyphList gl, final int columnCount, final KeyStroke ks) {
			super(columnCount + " Cell Window");
			setAccelerator(ks);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.setColumnCount(columnCount);
					window.pack();
				}
			});
		}
	}
}
