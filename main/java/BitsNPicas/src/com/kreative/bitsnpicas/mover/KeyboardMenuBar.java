package com.kreative.bitsnpicas.mover;

import java.awt.Window;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import com.kreative.bitsnpicas.edit.CommonMenuItems;
import com.kreative.bitsnpicas.edit.WindowMenu;

public class KeyboardMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public KeyboardMenuBar(final Window window) {
		add(new FileMenu(window));
		add(new WindowMenu(window));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window) {
			super("File");
			add(new CommonMenuItems.NewMenu());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
}
