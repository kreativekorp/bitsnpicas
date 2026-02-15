package com.kreative.bitsnpicas.edit;

import java.awt.AWTEvent;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class WindowMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	private final Window owner;
	private final AWTEventListener updateListener;
	private final WindowListener activationListener;
	
	public WindowMenu(Window owner) {
		super("Window");
		this.owner = owner;
		this.updateListener = new UpdateListener();
		this.activationListener = new ActivationListener();
		owner.addWindowListener(activationListener);
	}
	
	private class ActivationListener extends WindowAdapter {
		public void windowOpened(WindowEvent e) {
			Toolkit.getDefaultToolkit().addAWTEventListener(updateListener, AWTEvent.WINDOW_EVENT_MASK);
			update();
		}
		public void windowClosed(WindowEvent e) {
			Toolkit.getDefaultToolkit().removeAWTEventListener(updateListener);
			removeAll();
		}
	}
	
	private class UpdateListener implements AWTEventListener {
		public void eventDispatched(AWTEvent event) {
			if (event instanceof WindowEvent) {
				int id = event.getID();
				if (id == WindowEvent.WINDOW_OPENED || id == WindowEvent.WINDOW_CLOSED) {
					update();
				}
			}
		}
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
			super(frame.getTitle());
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.toFront();
				}
			});
		}
	}
}
