package com.kreative.mapedit;

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MapEditMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public static final int SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	public static final boolean IS_MAC_OS;
	static {
		boolean isMacOS;
		try { isMacOS = System.getProperty("os.name").toUpperCase().contains("MAC OS"); }
		catch (Exception e) { isMacOS = false; }
		IS_MAC_OS = isMacOS;
	}
	
	public MapEditMenuBar(MapEditFrame f, Window w, MapEditController ctrl) {
		add(new FileMenu(f, w));
		add(new EditMenu(ctrl));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(MapEditFrame f, Window w) {
			super("File");
			add(new NewMappingMenuItem());
			add(new OpenMappingMenuItem());
			add(new CloseMenuItem(w));
			addSeparator();
			add(new SaveMenuItem(f));
			add(new SaveAsMenuItem(f));
			if (!IS_MAC_OS) {
				addSeparator();
				add(new ExitMenuItem());
			}
		}
	}
	
	public static class NewMappingMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public NewMappingMenuItem() {
			super("New");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Main.newMapping();
				}
			});
		}
	}
	
	public static class OpenMappingMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public OpenMappingMenuItem() {
			super("Open...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Main.openMapping();
				}
			});
		}
	}
	
	public static class CloseMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CloseMenuItem(final Window window) {
			super("Close Window");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				}
			});
		}
	}
	
	public static class SaveMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SaveMenuItem(final MapEditFrame f) {
			super("Save");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					f.save();
				}
			});
		}
	}
	
	public static class SaveAsMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SaveAsMenuItem(final MapEditFrame f) {
			super("Save As...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					f.saveAs();
				}
			});
		}
	}
	
	public static class ExitMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ExitMenuItem() {
			super("Exit");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.gc();
					for (Window window : Window.getWindows()) {
						if (window.isVisible()) {
							window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
							if (window.isVisible()) return;
						}
					}
					System.exit(0);
				}
			});
		}
	}
	
	public static class EditMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public EditMenu(MapEditController ctrl) {
			super("Edit");
			add(new CutMenuItem(ctrl));
			add(new CopyMenuItem(ctrl));
			add(new PasteMenuItem(ctrl));
			add(new ClearMenuItem(ctrl));
			addSeparator();
			add(new OpenSubtableMenuItem(ctrl));
			add(new DeleteSubtableMenuItem(ctrl));
			addSeparator();
			add(new LeftToRightMenuItem(ctrl));
			add(new RightToLeftMenuItem(ctrl));
			add(new ReverseVideoMenuItem(ctrl));
		}
	}
	
	public static class CutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CutMenuItem(final MapEditController ctrl) {
			super("Cut");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = ctrl.getSelectedIndex();
					if (index < 0) return;
					String content = ctrl.getSequenceString(index);
					if (content == null) return;
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new StringSelection(content), new ClipboardOwner() {
						public void lostOwnership(Clipboard cb, Transferable t) {}
					});
					ctrl.deleteSequence(index);
				}
			});
		}
	}
	
	public static class CopyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyMenuItem(final MapEditController ctrl) {
			super("Copy");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = ctrl.getSelectedIndex();
					if (index < 0) return;
					String content = ctrl.getSequenceString(index);
					if (content == null) return;
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new StringSelection(content), new ClipboardOwner() {
						public void lostOwnership(Clipboard cb, Transferable t) {}
					});
				}
			});
		}
	}
	
	public static class PasteMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PasteMenuItem(final MapEditController ctrl) {
			super("Paste");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = ctrl.getSelectedIndex();
					if (index < 0) return;
					try {
						Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
						if (cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
							String content = cb.getData(DataFlavor.stringFlavor).toString();
							if (content == null || content.length() == 0) ctrl.deleteSequence(index);
							else ctrl.setSequenceString(index, content);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		}
	}
	
	public static class ClearMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ClearMenuItem(final MapEditController ctrl) {
			super("Clear");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = ctrl.getSelectedIndex();
					if (index < 0) return;
					ctrl.deleteSequence(index);
				}
			});
		}
	}
	
	public static class OpenSubtableMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public OpenSubtableMenuItem(final MapEditController ctrl) {
			super("Open Subtable");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = ctrl.getSelectedIndex();
					if (index < 0) return;
					ctrl.createSubtableFrame(index).setVisible(true);
				}
			});
		}
	}
	
	public static class DeleteSubtableMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public DeleteSubtableMenuItem(final MapEditController ctrl) {
			super("Delete Subtable");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = ctrl.getSelectedIndex();
					if (index < 0) return;
					ctrl.deleteSubtable(index);
				}
			});
		}
	}
	
	public static class LeftToRightMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public LeftToRightMenuItem(final MapEditController ctrl) {
			super("Left-to-Right");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = ctrl.getSelectedIndex();
					if (index < 0) return;
					CodePointSequence seq = ctrl.getSequence(index);
					if (seq == null) return;
					List<Integer> sl = seq.toList();
					sl.remove(Integer.valueOf(MappingTag.RL.intValue));
					if (!sl.remove(Integer.valueOf(MappingTag.LR.intValue))) {
						sl.add(0, Integer.valueOf(MappingTag.LR.intValue));
					}
					seq = new CodePointSequence(sl);
					ctrl.setSequence(index, seq);
				}
			});
		}
	}
	
	public static class RightToLeftMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public RightToLeftMenuItem(final MapEditController ctrl) {
			super("Right-to-Left");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = ctrl.getSelectedIndex();
					if (index < 0) return;
					CodePointSequence seq = ctrl.getSequence(index);
					if (seq == null) return;
					List<Integer> sl = seq.toList();
					sl.remove(Integer.valueOf(MappingTag.LR.intValue));
					if (!sl.remove(Integer.valueOf(MappingTag.RL.intValue))) {
						sl.add(0, Integer.valueOf(MappingTag.RL.intValue));
					}
					seq = new CodePointSequence(sl);
					ctrl.setSequence(index, seq);
				}
			});
		}
	}
	
	public static class ReverseVideoMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ReverseVideoMenuItem(final MapEditController ctrl) {
			super("Reverse Video");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = ctrl.getSelectedIndex();
					if (index < 0) return;
					CodePointSequence seq = ctrl.getSequence(index);
					if (seq == null) return;
					List<Integer> sl = seq.toList();
					if (!sl.remove(Integer.valueOf(MappingTag.RV.intValue))) {
						sl.add(0, Integer.valueOf(MappingTag.RV.intValue));
					}
					seq = new CodePointSequence(sl);
					ctrl.setSequence(index, seq);
				}
			});
		}
	}
}
