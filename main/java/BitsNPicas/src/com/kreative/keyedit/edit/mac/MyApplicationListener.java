package com.kreative.keyedit.edit.mac;

import java.awt.Window;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.awt.desktop.PrintFilesEvent;
import java.awt.desktop.PrintFilesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Method;
import com.kreative.keyedit.edit.Main;

public class MyApplicationListener {
	private static final String[][] classAndMethodNames = {
		{ "java.awt.Desktop", "getDesktop" },
		{ "com.kreative.ual.eawt.NewApplicationAdapter", "getInstance" },
		{ "com.kreative.ual.eawt.OldApplicationAdapter", "getInstance" },
	};
	
	public MyApplicationListener() {
		for (String[] classAndMethodName : classAndMethodNames) {
			try {
				Class<?> cls = Class.forName(classAndMethodName[0]);
				Method getInstance = cls.getMethod(classAndMethodName[1]);
				Object instance = getInstance.invoke(null);
				cls.getMethod("setOpenFileHandler", OpenFilesHandler.class).invoke(instance, open);
				cls.getMethod("setPrintFileHandler", PrintFilesHandler.class).invoke(instance, print);
				cls.getMethod("setQuitHandler", QuitHandler.class).invoke(instance, quit);
				System.out.println("Registered app event handlers through " + classAndMethodName[0]);
				return;
			} catch (Exception e) {
				System.out.println("Failed to register app event handlers through " + classAndMethodName[0] + ": " + e);
			}
		}
	}
	
	private final OpenFilesHandler open = new OpenFilesHandler() {
		@Override
		public void openFiles(final OpenFilesEvent e) {
			new Thread() {
				public void run() {
					for (Object o : e.getFiles()) {
						Main.openMapping((File)o);
					}
				}
			}.start();
		}
	};
	
	private final PrintFilesHandler print = new PrintFilesHandler() {
		@Override
		public void printFiles(final PrintFilesEvent e) {
			new Thread() {
				public void run() {
					for (Object o : e.getFiles()) {
						Main.openMapping((File)o);
					}
				}
			}.start();
		}
	};
	
	private final QuitHandler quit = new QuitHandler() {
		@Override
		public void handleQuitRequestWith(final QuitEvent e, final QuitResponse r) {
			new Thread() {
				public void run() {
					System.gc();
					for (Window window : Window.getWindows()) {
						if (window.isVisible()) {
							window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
							if (window.isVisible()) {
								r.cancelQuit();
								return;
							}
						}
					}
					r.performQuit();
					System.exit(0);
				}
			}.start();
		}
	};
}
