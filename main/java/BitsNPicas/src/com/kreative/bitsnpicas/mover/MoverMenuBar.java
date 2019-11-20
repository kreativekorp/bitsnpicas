package com.kreative.bitsnpicas.mover;

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

public class MoverMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public MoverMenuBar(final Window window, final SaveManager sm, final MoverTable table) {
		add(new FileMenu(window, sm));
		add(new EditMenu(table));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final SaveManager sm) {
			super("File");
			add(new CommonMenuItems.NewMenu());
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
		public EditMenu(final MoverTable table) {
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
		public OpenItemsMenuItem(final MoverTable table) {
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
