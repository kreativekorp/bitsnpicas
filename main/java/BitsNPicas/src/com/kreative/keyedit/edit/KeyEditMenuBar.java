package com.kreative.keyedit.edit;

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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.keyedit.Key;
import com.kreative.keyedit.KeyBlock;
import com.kreative.keyedit.KeyboardFormat;
import com.kreative.unicode.fontmap.FontMapController;

public class KeyEditMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public static final int SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	public KeyEditMenuBar(KeyEditFrame f, Window w, KeyEditController ctrl) {
		add(new FileMenu(f, w, ctrl));
		add(new EditMenu(ctrl));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(KeyEditFrame f, Window w, KeyEditController ctrl) {
			super("File");
			add(new NewMappingMenuItem());
			add(new OpenMappingMenuItem());
			add(new CloseMenuItem(w));
			addSeparator();
			add(new SaveMenuItem(f));
			add(new SaveAsMenuItem(f));
			add(new ExportMenu(f));
			addSeparator();
			add(new LayoutInfoMenuItem(ctrl));
			if (!OSUtils.IS_MAC_OS) {
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
		public SaveMenuItem(final KeyEditFrame f) {
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
		public SaveAsMenuItem(final KeyEditFrame f) {
			super("Save As...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					f.saveAs();
				}
			});
		}
	}
	
	public static class ExportMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public ExportMenu(final KeyEditFrame f) {
			super("Export");
			for (KeyboardFormat format : KeyboardFormat.values()) {
				if (format != KeyboardFormat.KKB) {
					add(new ExportMenuItem(f, format));
				}
			}
		}
	}
	
	public static class ExportMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ExportMenuItem(final KeyEditFrame f, final KeyboardFormat format) {
			super(format.getName() + "...");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					f.export(format);
				}
			});
		}
	}
	
	public static class LayoutInfoMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public LayoutInfoMenuItem(final KeyEditController ctrl) {
			super("Layout Info");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ctrl.getKeyboardMappingInfoFrame().setVisible(true);
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
		public EditMenu(KeyEditController ctrl) {
			super("Edit");
			add(new CutMenuItem(ctrl));
			add(new CopyMenuItem(ctrl));
			add(new PasteMenuItem(ctrl));
			add(new ClearMenuItem(ctrl));
			addSeparator();
			add(new EditKeyMenuItem(ctrl));
			add(new EditDeadKeyMenuItem(ctrl));
			add(new DeleteDeadKeyMenuItem(ctrl));
			addSeparator();
			add(new BulkActionMenu(ctrl));
			addSeparator();
			add(new FontMapMenuItem());
		}
	}
	
	public static class CutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CutMenuItem(final KeyEditController ctrl) {
			super("Cut");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Key key = ctrl.getSelectedKey();
					if (key == null) return;
					boolean alt = ctrl.isAltSelected();
					boolean shift = ctrl.isShiftSelected();
					int output = ctrl.getOutput(key, alt, shift);
					if (output <= 0) return;
					String content = String.valueOf(Character.toChars(output));
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new StringSelection(content), new ClipboardOwner() {
						public void lostOwnership(Clipboard cb, Transferable t) {}
					});
					ctrl.setOutput(key, alt, shift, -1);
				}
			});
		}
	}
	
	public static class CopyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyMenuItem(final KeyEditController ctrl) {
			super("Copy");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Key key = ctrl.getSelectedKey();
					if (key == null) return;
					boolean alt = ctrl.isAltSelected();
					boolean shift = ctrl.isShiftSelected();
					int output = ctrl.getOutput(key, alt, shift);
					if (output <= 0) return;
					String content = String.valueOf(Character.toChars(output));
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
		public PasteMenuItem(final KeyEditController ctrl) {
			super("Paste");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Key key = ctrl.getSelectedKey();
					if (key == null) return;
					boolean alt = ctrl.isAltSelected();
					boolean shift = ctrl.isShiftSelected();
					try {
						Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
						if (cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
							String content = cb.getData(DataFlavor.stringFlavor).toString();
							int output = (content == null || content.length() == 0)
							           ? (-1) : content.codePointAt(0);
							ctrl.setOutput(key, alt, shift, output);
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
		public ClearMenuItem(final KeyEditController ctrl) {
			super("Clear");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Key key = ctrl.getSelectedKey();
					if (key == null) return;
					boolean alt = ctrl.isAltSelected();
					boolean shift = ctrl.isShiftSelected();
					ctrl.setOutput(key, alt, shift, -1);
				}
			});
		}
	}
	
	public static class EditKeyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public EditKeyMenuItem(final KeyEditController ctrl) {
			super("Edit Key");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Key key = ctrl.getSelectedKey();
					if (key == null) return;
					boolean alt = ctrl.isAltSelected();
					boolean shift = ctrl.isShiftSelected();
					ctrl.getKeyMappingFrame(key, alt, shift).setVisible(true);
				}
			});
		}
	}
	
	public static class EditDeadKeyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public EditDeadKeyMenuItem(final KeyEditController ctrl) {
			super("Edit Dead Key");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Key key = ctrl.getSelectedKey();
					if (key == null) return;
					boolean alt = ctrl.isAltSelected();
					boolean shift = ctrl.isShiftSelected();
					ctrl.getDeadKeyTableFrame(key, alt, shift).setVisible(true);
				}
			});
		}
	}
	
	public static class DeleteDeadKeyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public DeleteDeadKeyMenuItem(final KeyEditController ctrl) {
			super("Delete Dead Key");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Key key = ctrl.getSelectedKey();
					if (key == null) return;
					boolean alt = ctrl.isAltSelected();
					boolean shift = ctrl.isShiftSelected();
					ctrl.setDeadKey(key, alt, shift, null);
				}
			});
		}
	}
	
	public static class BulkActionMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public BulkActionMenu(KeyEditController ctrl) {
			super("Bulk Actions");
			add(new CopyUnshiftedToShiftedMenuItem(ctrl));
			add(new CopyShiftedToUnshiftedMenuItem(ctrl));
			add(new CopyNormalToAltMenuItem(ctrl));
			add(new CopyAltToNormalMenuItem(ctrl));
			add(new ClearNormalMenuItem(ctrl));
			add(new ClearAltMenuItem(ctrl));
			add(new SwapNormalAndAltMenuItem(ctrl));
		}
	}
	
	public static abstract class BulkActionMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public BulkActionMenuItem(final String title, final KeyEditController ctrl) {
			super(title);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean shiftDown = ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0);
					for (Key key : Key.values()) {
						if (shiftDown || key.keyBlock == KeyBlock.ALPHA) {
							BulkActionMenuItem.this.actionPerformed(e, ctrl, key);
						}
					}
				}
			});
		}
		public abstract void actionPerformed(ActionEvent e, KeyEditController ctrl, Key key);
	}
	
	public static class CopyUnshiftedToShiftedMenuItem extends BulkActionMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyUnshiftedToShiftedMenuItem(KeyEditController ctrl) {
			super("Copy Unshifted to Shifted", ctrl);
		}
		public void actionPerformed(ActionEvent e, KeyEditController ctrl, Key key) {
			int nu = ctrl.getOutput(key, false, false);
			int ns = ctrl.getOutput(key, false, true);
			if (ns <= 0) ctrl.setOutput(key, false, true, Character.toUpperCase(nu));
			int au = ctrl.getOutput(key, true, false);
			int as = ctrl.getOutput(key, true, true);
			if (as <= 0) ctrl.setOutput(key, true, true, Character.toUpperCase(au));
		}
	}
	
	public static class CopyShiftedToUnshiftedMenuItem extends BulkActionMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyShiftedToUnshiftedMenuItem(KeyEditController ctrl) {
			super("Copy Shifted to Unshifted", ctrl);
		}
		public void actionPerformed(ActionEvent e, KeyEditController ctrl, Key key) {
			int ns = ctrl.getOutput(key, false, true);
			int nu = ctrl.getOutput(key, false, false);
			if (nu <= 0) ctrl.setOutput(key, false, false, Character.toLowerCase(ns));
			int as = ctrl.getOutput(key, true, true);
			int au = ctrl.getOutput(key, true, false);
			if (au <= 0) ctrl.setOutput(key, true, false, Character.toLowerCase(as));
		}
	}
	
	public static class CopyNormalToAltMenuItem extends BulkActionMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyNormalToAltMenuItem(KeyEditController ctrl) {
			super("Copy Normal to Alt", ctrl);
		}
		public void actionPerformed(ActionEvent e, KeyEditController ctrl, Key key) {
			int nu = ctrl.getOutput(key, false, false);
			int au = ctrl.getOutput(key, true, false);
			if (au <= 0) ctrl.setOutput(key, true, false, nu);
			int ns = ctrl.getOutput(key, false, true);
			int as = ctrl.getOutput(key, true, true);
			if (as <= 0) ctrl.setOutput(key, true, true, ns);
		}
	}
	
	public static class CopyAltToNormalMenuItem extends BulkActionMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyAltToNormalMenuItem(KeyEditController ctrl) {
			super("Copy Alt to Normal", ctrl);
		}
		public void actionPerformed(ActionEvent e, KeyEditController ctrl, Key key) {
			int au = ctrl.getOutput(key, true, false);
			int nu = ctrl.getOutput(key, false, false);
			if (nu <= 0) ctrl.setOutput(key, false, false, au);
			int as = ctrl.getOutput(key, true, true);
			int ns = ctrl.getOutput(key, false, true);
			if (ns <= 0) ctrl.setOutput(key, false, true, as);
		}
	}
	
	public static class ClearNormalMenuItem extends BulkActionMenuItem {
		private static final long serialVersionUID = 1L;
		public ClearNormalMenuItem(KeyEditController ctrl) {
			super("Clear Normal", ctrl);
		}
		public void actionPerformed(ActionEvent e, KeyEditController ctrl, Key key) {
			ctrl.setOutput(key, false, false, -1);
			ctrl.setOutput(key, false, true, -1);
		}
	}
	
	public static class ClearAltMenuItem extends BulkActionMenuItem {
		private static final long serialVersionUID = 1L;
		public ClearAltMenuItem(KeyEditController ctrl) {
			super("Clear Alt", ctrl);
		}
		public void actionPerformed(ActionEvent e, KeyEditController ctrl, Key key) {
			ctrl.setOutput(key, true, false, -1);
			ctrl.setOutput(key, true, true, -1);
		}
	}
	
	public static class SwapNormalAndAltMenuItem extends BulkActionMenuItem {
		private static final long serialVersionUID = 1L;
		public SwapNormalAndAltMenuItem(KeyEditController ctrl) {
			super("Swap Normal and Alt", ctrl);
		}
		public void actionPerformed(ActionEvent e, KeyEditController ctrl, Key key) {
			ctrl.swapAlt(key);
		}
	}
	
	public static class FontMapMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public FontMapMenuItem() {
			super("Font Map");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FontMapController.getInstance().getFrame().setVisible(true);
				}
			});
		}
	}
}
