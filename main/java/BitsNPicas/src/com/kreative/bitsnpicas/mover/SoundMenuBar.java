package com.kreative.bitsnpicas.mover;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.edit.CommonMenuItems;
import com.kreative.bitsnpicas.edit.Main;
import com.kreative.rsrc.SoundResource;

public class SoundMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public SoundMenuBar(final Window window, final SoundResource snd) {
		add(new FileMenu(window, snd));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final SoundResource snd) {
			super("File");
			add(new CommonMenuItems.NewMenu());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			addSeparator();
			add(new SaveWavMenuItem(snd));
			add(new SaveAiffMenuItem(snd));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
	
	public static class SaveWavMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SaveWavMenuItem(final SoundResource snd) {
			super("Save as WAV...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File file = Main.getSaveFile(".wav");
					if (file == null) return;
					try {
						FileOutputStream out = new FileOutputStream(file);
						out.write(snd.toWav());
						out.flush();
						out.close();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(
							null, "An error occurred while saving this file.",
							"Save", JOptionPane.ERROR_MESSAGE
						);
					}
				}
			});
		}
	}
	
	public static class SaveAiffMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SaveAiffMenuItem(final SoundResource snd) {
			super("Save as AIFF...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File file = Main.getSaveFile(".aiff");
					if (file == null) return;
					try {
						FileOutputStream out = new FileOutputStream(file);
						out.write(snd.toAiff());
						out.flush();
						out.close();
					} catch (Exception ex) {
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
