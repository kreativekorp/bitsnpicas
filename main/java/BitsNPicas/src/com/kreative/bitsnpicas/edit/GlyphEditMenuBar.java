package com.kreative.bitsnpicas.edit;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.Font;

public class GlyphEditMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public GlyphEditMenuBar(final Window window, final SaveManager sm, final Font<?> font, final GlyphComponent gc) {
		add(new FileMenu(window, sm, font));
		add(new ViewMenu(gc));
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
			add(new CommonMenuItems.FontInfoMenuItem(font, sm));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
	
	public static class ViewMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public ViewMenu(final GlyphComponent gc) {
			super("View");
			FitToEmMenuItem fitToEm = new FitToEmMenuItem(gc);
			add(fitToEm);
			add(new ZoomOutMenuItem(gc, fitToEm));
			add(new ZoomInMenuItem(gc, fitToEm));
			addSeparator();
			add(new ShowBoundingBoxMenuItem(gc));
		}
	}
	
	public static class FitToEmMenuItem extends JCheckBoxMenuItem {
		private static final long serialVersionUID = 1L;
		public FitToEmMenuItem(final GlyphComponent gc) {
			super("Fit to Em");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, CommonMenuItems.SHORTCUT_KEY));
			setSelected(gc.getFit());
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gc.setFit(true);
					setSelected(true);
				}
			});
		}
	}
	
	public static class ZoomInMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ZoomInMenuItem(final GlyphComponent gc, final FitToEmMenuItem fitToEm) {
			super("Zoom In");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gc.setFit(false);
					fitToEm.setSelected(false);
					double scale = gc.getScale() * 1.5;
					if (scale <= 0) scale = 1;
					if (scale >= 1) scale = Math.ceil(scale);
					gc.setScale(scale);
				}
			});
		}
	}
	
	public static class ZoomOutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ZoomOutMenuItem(final GlyphComponent gc, final FitToEmMenuItem fitToEm) {
			super("Zoom Out");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gc.setFit(false);
					fitToEm.setSelected(false);
					double scale = gc.getScale() / 1.5;
					if (scale <= 0) scale = 1;
					if (scale >= 1) scale = Math.floor(scale);
					gc.setScale(scale);
				}
			});
		}
	}
	
	public static class ShowBoundingBoxMenuItem extends JCheckBoxMenuItem {
		private static final long serialVersionUID = 1L;
		public ShowBoundingBoxMenuItem(final GlyphComponent gc) {
			super("Show Bounding Box");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, CommonMenuItems.SHORTCUT_KEY));
			setSelected(gc.getShowGlyphBounds());
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gc.setShowGlyphBounds(isSelected());
				}
			});
		}
	}
}
