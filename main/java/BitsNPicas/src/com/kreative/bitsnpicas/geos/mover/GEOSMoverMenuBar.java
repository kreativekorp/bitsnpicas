package com.kreative.bitsnpicas.geos.mover;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.datatransfer.ClearMenuItem;
import com.kreative.bitsnpicas.datatransfer.CopyMenuItem;
import com.kreative.bitsnpicas.datatransfer.CutMenuItem;
import com.kreative.bitsnpicas.datatransfer.PasteMenuItem;
import com.kreative.bitsnpicas.edit.CommonMenuItems;

public class GEOSMoverMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public GEOSMoverMenuBar(final Window window, final SaveManager sm, final GEOSFontPointSizeTable table) {
		add(new FileMenu(window, sm));
		add(new EditMenu(table));
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
	
	public static class EditMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public EditMenu(final GEOSFontPointSizeTable table) {
			super("Edit");
			add(new CutMenuItem());
			add(new CopyMenuItem());
			add(new PasteMenuItem());
			add(new ClearMenuItem());
			addSeparator();
			add(new OpenItemsMenuItem(table));
		}
	}
	
	public static class OpenItemsMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public OpenItemsMenuItem(final GEOSFontPointSizeTable table) {
			super("Open Items");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					table.doOpen();
				}
			});
		}
	}
}
