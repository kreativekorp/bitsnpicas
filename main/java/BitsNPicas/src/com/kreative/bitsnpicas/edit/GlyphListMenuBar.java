package com.kreative.bitsnpicas.edit;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.Font;

public class GlyphListMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public GlyphListMenuBar(final Window window, final SaveManager sm, final Font<?> font, final GlyphList gl) {
		add(new FileMenu(window, sm, font));
		add(new EditMenu(font, gl, sm));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final SaveManager sm, final Font<?> font) {
			super("File");
			add(new CommonMenuItems.NewBitmapFontMenuItem());
			// add(new CommonMenuItems.NewVectorFontMenuItem());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			addSeparator();
			add(new CommonMenuItems.SaveMenuItem(sm));
			add(new CommonMenuItems.SaveAsMenuItem(sm));
			addSeparator();
			add(new CommonMenuItems.FontInfoMenuItem(font));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
	
	public static class EditMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public EditMenu(final Font<?> font, final GlyphList gl, final SaveManager sm) {
			super("Edit");
			add(new EditMenuItem(font, gl, sm));
			add(new DeleteMenuItem(font, gl));
		}
	}
	
	public static class EditMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public EditMenuItem(final Font<?> font, final GlyphList gl, final SaveManager sm) {
			super("Edit Glyph");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int cp : gl.getSelectedCodePoints()) {
						Main.openGlyph(font, cp, gl, sm);
					}
				}
			});
		}
	}
	
	public static class DeleteMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public DeleteMenuItem(final Font<?> font, final GlyphList gl) {
			super("Delete Glyph");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int cp : gl.getSelectedCodePoints()) {
						font.removeCharacter(cp);
					}
					gl.repaint();
				}
			});
		}
	}
}
