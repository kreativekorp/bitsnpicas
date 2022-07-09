package com.kreative.bitsnpicas.edit;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
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
			add(new CommonMenuItems.NewMenu());
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
			addSeparator();
			add(new CommonMenuItems.FontMapMenuItem());
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
			add(new ZoomOutMenuItem(window, gl));
			add(new ZoomInMenuItem(window, gl));
			addSeparator();
			ButtonGroup csg = new ButtonGroup();
			add(new CellSizeMenuItem(csg, window, gl, 12, KeyStroke.getKeyStroke(KeyEvent.VK_1, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, window, gl, 24, KeyStroke.getKeyStroke(KeyEvent.VK_2, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, window, gl, 36, KeyStroke.getKeyStroke(KeyEvent.VK_3, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, window, gl, 48, KeyStroke.getKeyStroke(KeyEvent.VK_4, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, window, gl, 72, KeyStroke.getKeyStroke(KeyEvent.VK_5, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, window, gl, 96, KeyStroke.getKeyStroke(KeyEvent.VK_6, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, window, gl, 128, KeyStroke.getKeyStroke(KeyEvent.VK_7, CommonMenuItems.SHORTCUT_KEY)));
			addSeparator();
			ButtonGroup ccg = new ButtonGroup();
			add(new ColumnCountMenuItem(ccg, window, gl, 8, KeyStroke.getKeyStroke(KeyEvent.VK_8, CommonMenuItems.SHORTCUT_KEY)));
			add(new ColumnCountMenuItem(ccg, window, gl, 16, KeyStroke.getKeyStroke(KeyEvent.VK_9, CommonMenuItems.SHORTCUT_KEY)));
			add(new ColumnCountMenuItem(ccg, window, gl, 32, KeyStroke.getKeyStroke(KeyEvent.VK_0, CommonMenuItems.SHORTCUT_KEY)));
			addSeparator();
			ButtonGroup rcg = new ButtonGroup();
			add(new RowCountMenuItem(rcg, window, gl, 4, KeyStroke.getKeyStroke(KeyEvent.VK_8, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)));
			add(new RowCountMenuItem(rcg, window, gl, 8, KeyStroke.getKeyStroke(KeyEvent.VK_9, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)));
			add(new RowCountMenuItem(rcg, window, gl, 16, KeyStroke.getKeyStroke(KeyEvent.VK_0, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)));
		}
	}
	
	public static class ZoomInMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ZoomInMenuItem(final Window window, final GlyphList gl) {
			super("Zoom In");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switch (gl.getCellSize()) {
						case 12: gl.setCellSize(24); break;
						case 24: gl.setCellSize(36); break;
						case 36: gl.setCellSize(48); break;
						case 48: gl.setCellSize(72); break;
						case 72: gl.setCellSize(96); break;
						case 96: gl.setCellSize(128); break;
					}
					window.pack();
				}
			});
		}
	}
	
	public static class ZoomOutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ZoomOutMenuItem(final Window window, final GlyphList gl) {
			super("Zoom Out");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switch (gl.getCellSize()) {
						case 24: gl.setCellSize(12); break;
						case 36: gl.setCellSize(24); break;
						case 48: gl.setCellSize(36); break;
						case 72: gl.setCellSize(48); break;
						case 96: gl.setCellSize(72); break;
						case 128: gl.setCellSize(96); break;
					}
					window.pack();
				}
			});
		}
	}
	
	public static class CellSizeMenuItem extends JRadioButtonMenuItem {
		private static final long serialVersionUID = 1L;
		public CellSizeMenuItem(final ButtonGroup group, final Window window, final GlyphList gl, final int cellSize, final KeyStroke ks) {
			super(cellSize + " Pixel Cell Size");
			setAccelerator(ks);
			setSelected(gl.getCellSize() == cellSize);
			group.add(this);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.setCellSize(cellSize);
					setSelected(true);
					window.pack();
				}
			});
			gl.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					setSelected(gl.getCellSize() == cellSize);
				}
			});
		}
	}
	
	public static class ColumnCountMenuItem extends JRadioButtonMenuItem {
		private static final long serialVersionUID = 1L;
		public ColumnCountMenuItem(final ButtonGroup group, final Window window, final GlyphList gl, final int columnCount, final KeyStroke ks) {
			super(columnCount + " Cell Wide Window");
			setAccelerator(ks);
			setSelected(gl.getColumnCount() == columnCount);
			group.add(this);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.setColumnCount(columnCount);
					setSelected(true);
					window.pack();
				}
			});
			gl.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					setSelected(gl.getColumnCount() == columnCount);
				}
			});
		}
	}
	
	public static class RowCountMenuItem extends JRadioButtonMenuItem {
		private static final long serialVersionUID = 1L;
		public RowCountMenuItem(final ButtonGroup group, final Window window, final GlyphList gl, final int rowCount, final KeyStroke ks) {
			super(rowCount + " Cell High Window");
			setAccelerator(ks);
			setSelected(gl.getRowCount() == rowCount);
			group.add(this);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.setRowCount(rowCount);
					setSelected(true);
					window.pack();
				}
			});
			gl.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					setSelected(gl.getRowCount() == rowCount);
				}
			});
		}
	}
}
