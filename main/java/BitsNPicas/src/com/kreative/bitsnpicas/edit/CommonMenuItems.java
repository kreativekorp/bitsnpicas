package com.kreative.bitsnpicas.edit;

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.geos.mover.GEOSMoverFrame;
import com.kreative.bitsnpicas.mover.MoverFrame;
import com.kreative.unicode.fontmap.FontMapController;

public class CommonMenuItems extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public static final int SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	public static final boolean IS_MAC_OS;
	static {
		boolean isMacOS;
		try { isMacOS = System.getProperty("os.name").toUpperCase().contains("MAC OS"); }
		catch (Exception e) { isMacOS = false; }
		IS_MAC_OS = isMacOS;
	}
	
	public CommonMenuItems(final Window window) {
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new NewMenu());
		fileMenu.add(new OpenMenuItem());
		if (window != null) {
			fileMenu.add(new CloseMenuItem(window));
		}
		if (!IS_MAC_OS) {
			fileMenu.addSeparator();
			fileMenu.add(new ExitMenuItem());
		}
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(new FontMapMenuItem());
		add(fileMenu);
		add(editMenu);
	}
	
	public static class NewMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public NewMenu() {
			super("New");
			add(new NewBitmapFontMenuItem());
			// add(new NewVectorFontMenuItem());
			add(new NewFontSuitcaseMenuItem());
			add(new NewGEOSFontMenuItem());
		}
	}
	
	public static class NewBitmapFontMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public NewBitmapFontMenuItem() {
			super("New Bitmap Font...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Main.newBitmapFont();
				}
			});
		}
	}
	
	public static class NewVectorFontMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public NewVectorFontMenuItem() {
			super("New Vector Font...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Main.newVectorFont();
				}
			});
		}
	}
	
	public static class NewFontSuitcaseMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public NewFontSuitcaseMenuItem() {
			super("New Font Suitcase");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try { MoverFrame.forNewFile().setVisible(true); }
					catch (IOException ioe) { ioe.printStackTrace(); }
				}
			});
		}
	}
	
	public static class NewGEOSFontMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public NewGEOSFontMenuItem() {
			super("New GEOS Font");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GEOSMoverFrame.forNewFile().setVisible(true);
				}
			});
		}
	}
	
	public static class OpenMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public OpenMenuItem() {
			super("Open...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Main.openFonts();
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
		public SaveMenuItem(final SaveInterface sm) {
			super("Save");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sm.save();
				}
			});
		}
	}
	
	public static class SaveAsMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SaveAsMenuItem(final SaveInterface sm) {
			super("Save As...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sm.saveAs();
				}
			});
		}
	}
	
	public static class FontInfoMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public FontInfoMenuItem(final Font<?> font, final SaveManager sm) {
			super("Font Info");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new FontInfoFrame(font, sm).setVisible(true);
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
