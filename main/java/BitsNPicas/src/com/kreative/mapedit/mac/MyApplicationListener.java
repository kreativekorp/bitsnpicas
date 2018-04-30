package com.kreative.mapedit.mac;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.io.File;
import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;
import com.kreative.mapedit.Main;

@SuppressWarnings("deprecation")
public class MyApplicationListener implements ApplicationListener {
	public MyApplicationListener() {
		Application a = Application.getApplication();
		a.addApplicationListener(this);
	}
	
	public void handleOpenFile(final ApplicationEvent e) {
		new Thread() {
			public void run() {
				Main.openMapping(new File(e.getFilename()));
			}
		}.start();
		e.setHandled(true);
	}
	
	public void handlePrintFile(final ApplicationEvent e) {
		new Thread() {
			public void run() {
				Main.openMapping(new File(e.getFilename()));
			}
		}.start();
		e.setHandled(true);
	}
	
	public void handleQuit(ApplicationEvent e) {
		new Thread() {
			public void run() {
				System.gc();
				for (Window window : Window.getWindows()) {
					if (window.isVisible()) {
						window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
						if (window.isVisible()) return;
					}
				}
				System.exit(0);
			}
		}.start();
		e.setHandled(false);
	}
	
	public void handleAbout(ApplicationEvent e) {}
	public void handleOpenApplication(ApplicationEvent e) {}
	public void handlePreferences(ApplicationEvent e) {}
	public void handleReOpenApplication(ApplicationEvent e) {}
}
