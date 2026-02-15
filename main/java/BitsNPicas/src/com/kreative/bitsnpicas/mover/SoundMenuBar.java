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
import com.kreative.bitsnpicas.edit.WindowMenu;
import com.kreative.unicode.ttflib.DfontResource;

public class SoundMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public SoundMenuBar(final Window window, final DfontResource snd) {
		add(new FileMenu(window, snd));
		add(new WindowMenu(window));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final DfontResource snd) {
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
		public SaveWavMenuItem(final DfontResource snd) {
			super("Save as WAV...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, CommonMenuItems.SHORTCUT_KEY));
			try {
				SoundResource sr = new SoundResource(snd.getData());
				final byte[] wavData = sr.toWav();
				if (wavData != null) {
					addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							File file = Main.getSaveFile(".wav");
							if (file == null) return;
							try {
								FileOutputStream out = new FileOutputStream(file);
								out.write(wavData);
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
				} else {
					setEnabled(false);
				}
			} catch (IOException ioe) {
				setEnabled(false);
			}
		}
	}
	
	public static class SaveAiffMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public SaveAiffMenuItem(final DfontResource snd) {
			super("Save as AIFF...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			try {
				SoundResource sr = new SoundResource(snd.getData());
				final byte[] aiffData = sr.toAiff();
				if (aiffData != null) {
					addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							File file = Main.getSaveFile(".aiff");
							if (file == null) return;
							try {
								FileOutputStream out = new FileOutputStream(file);
								out.write(aiffData);
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
				} else {
					setEnabled(false);
				}
			} catch (IOException ioe) {
				setEnabled(false);
			}
		}
	}
}
