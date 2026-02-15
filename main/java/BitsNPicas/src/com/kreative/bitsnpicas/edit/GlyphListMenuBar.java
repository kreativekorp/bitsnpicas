package com.kreative.bitsnpicas.edit;

import java.awt.Frame;
import java.awt.Toolkit;
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
	
	public GlyphListMenuBar(final Frame frame, final SaveManager sm, final Font<?> font, final GlyphList<?> gl) {
		add(new FileMenu(frame, sm, font));
		add(new EditMenu(frame, gl));
		add(new ViewMenu(frame, gl));
		add(new WindowMenu(frame));
	}
	
	public static final class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Frame frame, final SaveManager sm, final Font<?> font) {
			super("File");
			add(new CommonMenuItems.NewMenu());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(frame));
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
	
	public static final class EditMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public EditMenu(final Frame frame, final GlyphList<?> gl) {
			super("Edit");
			add(new SelectAllMenuItem(gl));
			add(new SelectNoneMenuItem(gl));
			add(new SetSelectionMenuItem(frame, gl));
			addSeparator();
			add(new EditMenuItem(gl));
			add(new DeleteMenuItem(gl));
			addSeparator();
			add(new CommonMenuItems.FontMapMenuItem());
		}
	}
	
	public static final class SelectAllMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SelectAllMenuItem(final GlyphList<?> gl) {
			super("Select All");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.selectAll();
				}
			});
		}
	}
	
	public static final class SelectNoneMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SelectNoneMenuItem(final GlyphList<?> gl) {
			super("Select None");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.clearSelection();
				}
			});
		}
	}
	
	public static final class SetSelectionMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SetSelectionMenuItem(final Frame frame, final GlyphList<?> gl) {
			super("Set Selection...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SetSelectionDialog(frame, gl).setVisible(true);
				}
			});
		}
	}
	
	public static final class EditMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public EditMenuItem(final GlyphList<?> gl) {
			super("Edit Glyphs");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (gl.getSelection().isEmpty()) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					gl.openSelection();
				}
			});
		}
	}
	
	public static final class DeleteMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public DeleteMenuItem(final GlyphList<?> gl) {
			super("Delete Glyphs");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (gl.getSelection().isEmpty()) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					gl.deleteSelection();
				}
			});
		}
	}
	
	public static final class ViewMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public ViewMenu(final Frame frame, final GlyphList<?> gl) {
			super("View");
			add(new ZoomOutMenuItem(frame, gl));
			add(new ZoomInMenuItem(frame, gl));
			addSeparator();
			ButtonGroup csg = new ButtonGroup();
			add(new CellSizeMenuItem(csg, frame, gl, 12, KeyStroke.getKeyStroke(KeyEvent.VK_1, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, frame, gl, 24, KeyStroke.getKeyStroke(KeyEvent.VK_2, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, frame, gl, 36, KeyStroke.getKeyStroke(KeyEvent.VK_3, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, frame, gl, 48, KeyStroke.getKeyStroke(KeyEvent.VK_4, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, frame, gl, 72, KeyStroke.getKeyStroke(KeyEvent.VK_5, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, frame, gl, 96, KeyStroke.getKeyStroke(KeyEvent.VK_6, CommonMenuItems.SHORTCUT_KEY)));
			add(new CellSizeMenuItem(csg, frame, gl, 128, KeyStroke.getKeyStroke(KeyEvent.VK_7, CommonMenuItems.SHORTCUT_KEY)));
			addSeparator();
			ButtonGroup ccg = new ButtonGroup();
			add(new ColumnCountMenuItem(ccg, frame, gl, 8, KeyStroke.getKeyStroke(KeyEvent.VK_8, CommonMenuItems.SHORTCUT_KEY)));
			add(new ColumnCountMenuItem(ccg, frame, gl, 16, KeyStroke.getKeyStroke(KeyEvent.VK_9, CommonMenuItems.SHORTCUT_KEY)));
			add(new ColumnCountMenuItem(ccg, frame, gl, 32, KeyStroke.getKeyStroke(KeyEvent.VK_0, CommonMenuItems.SHORTCUT_KEY)));
			addSeparator();
			ButtonGroup rcg = new ButtonGroup();
			add(new RowCountMenuItem(rcg, frame, gl, 4, KeyStroke.getKeyStroke(KeyEvent.VK_8, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)));
			add(new RowCountMenuItem(rcg, frame, gl, 8, KeyStroke.getKeyStroke(KeyEvent.VK_9, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)));
			add(new RowCountMenuItem(rcg, frame, gl, 16, KeyStroke.getKeyStroke(KeyEvent.VK_0, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)));
		}
	}
	
	public static final class ZoomInMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ZoomInMenuItem(final Frame frame, final GlyphList<?> gl) {
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
					frame.pack();
				}
			});
		}
	}
	
	public static final class ZoomOutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ZoomOutMenuItem(final Frame frame, final GlyphList<?> gl) {
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
					frame.pack();
				}
			});
		}
	}
	
	public static final class CellSizeMenuItem extends JRadioButtonMenuItem {
		private static final long serialVersionUID = 1L;
		public CellSizeMenuItem(final ButtonGroup group, final Frame frame, final GlyphList<?> gl, final int cellSize, final KeyStroke ks) {
			super(cellSize + " Pixel Cell Size");
			setAccelerator(ks);
			setSelected(gl.getCellSize() == cellSize);
			group.add(this);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.setCellSize(cellSize);
					setSelected(true);
					frame.pack();
				}
			});
			gl.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					setSelected(gl.getCellSize() == cellSize);
				}
			});
		}
	}
	
	public static final class ColumnCountMenuItem extends JRadioButtonMenuItem {
		private static final long serialVersionUID = 1L;
		public ColumnCountMenuItem(final ButtonGroup group, final Frame frame, final GlyphList<?> gl, final int columnCount, final KeyStroke ks) {
			super(columnCount + " Cell Wide Window");
			setAccelerator(ks);
			setSelected(gl.getColumnCount() == columnCount);
			group.add(this);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.setColumnCount(columnCount);
					setSelected(true);
					frame.pack();
				}
			});
			gl.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					setSelected(gl.getColumnCount() == columnCount);
				}
			});
		}
	}
	
	public static final class RowCountMenuItem extends JRadioButtonMenuItem {
		private static final long serialVersionUID = 1L;
		public RowCountMenuItem(final ButtonGroup group, final Frame frame, final GlyphList<?> gl, final int rowCount, final KeyStroke ks) {
			super(rowCount + " Cell High Window");
			setAccelerator(ks);
			setSelected(gl.getRowCount() == rowCount);
			group.add(this);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gl.setRowCount(rowCount);
					setSelected(true);
					frame.pack();
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
