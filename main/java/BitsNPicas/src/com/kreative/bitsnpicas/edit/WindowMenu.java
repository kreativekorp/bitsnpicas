package com.kreative.bitsnpicas.edit;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class WindowMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	private final Window owner;
	
	public WindowMenu(Window owner) {
		super("Window");
		this.owner = owner;
		this.addMenuListener(new MenuListener() {
			public void menuSelected(MenuEvent e) { update(); }
			public void menuDeselected(MenuEvent e) { removeAll(); }
			public void menuCanceled(MenuEvent e) { removeAll(); }
		});
	}
	
	private void update() {
		removeAll();
		for (Window window : Window.getWindows()) {
			if (window instanceof Frame) {
				Frame frame = (Frame)window;
				if (frame.isVisible() && !frame.isUndecorated()) {
					WindowMenuItem wmi = new WindowMenuItem(frame);
					if (frame == owner) {
						wmi.setFont(wmi.getFont().deriveFont(Font.BOLD));
					}
					add(wmi);
				}
			}
		}
	}
	
	public static class WindowMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public WindowMenuItem(final Frame frame) {
			String title = frame.getTitle();
			if (title == null || title.length() == 0) title = " ";
			this.setText(title);
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.toFront();
				}
			});
		}
	}
}
