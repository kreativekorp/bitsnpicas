package com.kreative.bitsnpicas.mover;

import java.awt.Window;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import com.kreative.bitsnpicas.datatransfer.ClearMenuItem;
import com.kreative.bitsnpicas.datatransfer.CopyMenuItem;
import com.kreative.bitsnpicas.datatransfer.CutMenuItem;
import com.kreative.bitsnpicas.datatransfer.PasteMenuItem;
import com.kreative.bitsnpicas.edit.CommonMenuItems;

public class MoverMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public MoverMenuBar(final Window window, final SaveManager sm) {
		add(new FileMenu(window, sm));
		add(new EditMenu());
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
		public EditMenu() {
			super("Edit");
			add(new CutMenuItem());
			add(new CopyMenuItem());
			add(new PasteMenuItem());
			add(new ClearMenuItem());
		}
	}
}
