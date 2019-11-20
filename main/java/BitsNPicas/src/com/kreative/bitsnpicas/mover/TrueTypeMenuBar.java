package com.kreative.bitsnpicas.mover;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.edit.CommonMenuItems;
import com.kreative.bitsnpicas.edit.Main;

public class TrueTypeMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public TrueTypeMenuBar(final Window window, final byte[] fontData) {
		add(new FileMenu(window, fontData));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final byte[] fontData) {
			super("File");
			add(new CommonMenuItems.NewMenu());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			addSeparator();
			add(new SaveMenuItem(fontData));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
	
	public static class SaveMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SaveMenuItem(final byte[] fontData) {
			super("Save as TTF...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File file = Main.getSaveFile(".ttf");
					if (file == null) return;
					try {
						FileOutputStream out = new FileOutputStream(file);
						out.write(fontData);
						out.flush();
						out.close();
					} catch (IOException ioe) {
						JOptionPane.showMessageDialog(
							null, "An error occurred while saving this file.",
							"Save", JOptionPane.ERROR_MESSAGE
						);
					}
				}
			});
		}
	}
}
