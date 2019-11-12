package com.kreative.bitsnpicas.geos.mover;

import java.awt.Window;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import com.kreative.bitsnpicas.edit.CommonMenuItems;

public class GEOSMoverMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public GEOSMoverMenuBar(final Window window, final SaveManager sm) {
		add(new FileMenu(window, sm));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final SaveManager sm) {
			super("File");
			add(new CommonMenuItems.NewBitmapFontMenuItem());
			// add(new CommonMenuItems.NewVectorFontMenuItem());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			addSeparator();
			add(new CommonMenuItems.SaveMenuItem(sm));
			add(new CommonMenuItems.SaveAsMenuItem(sm));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
}
