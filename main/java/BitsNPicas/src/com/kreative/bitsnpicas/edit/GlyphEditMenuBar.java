package com.kreative.bitsnpicas.edit;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public class GlyphEditMenuBar<G extends FontGlyph> extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public GlyphEditMenuBar(
		final Frame frame, final SaveManager sm,
		final Font<G> font, final GlyphEditPanel<G> panel,
		final Class<G> glyphClass
	) {
		add(new FileMenu(frame, sm, font));
		add(new ViewMenu<G>(frame, panel, glyphClass));
	}
	
	public static final class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Frame frame, final SaveManager sm, final Font<?> font) {
			super("File");
			add(new CommonMenuItems.NewMenu());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(frame));
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
	
	public static final class ViewMenu<G extends FontGlyph> extends JMenu {
		private static final long serialVersionUID = 1L;
		public ViewMenu(final Frame frame, final GlyphEditPanel<G> panel, final Class<G> glyphClass) {
			super("View");
			GlyphComponent<G> gc = panel.getGlyphComponent();
			FitToEmMenuItem fitToEm = new FitToEmMenuItem(gc);
			add(fitToEm);
			add(new ZoomOutMenuItem(gc, fitToEm));
			add(new ZoomInMenuItem(gc, fitToEm));
			addSeparator();
			add(new ShowBoundingBoxMenuItem(gc));
			addSeparator();
			add(new PreviousGlyphMenuItem<G>(frame, panel, glyphClass));
			add(new NextGlyphMenuItem<G>(frame, panel, glyphClass));
			add(new PreviousDefinedGlyphMenuItem<G>(frame, panel));
			add(new NextDefinedGlyphMenuItem<G>(frame, panel));
		}
	}
	
	public static final class FitToEmMenuItem extends JCheckBoxMenuItem {
		private static final long serialVersionUID = 1L;
		public FitToEmMenuItem(final GlyphComponent<?> gc) {
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
	
	public static final class ZoomInMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ZoomInMenuItem(final GlyphComponent<?> gc, final FitToEmMenuItem fitToEm) {
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
	
	public static final class ZoomOutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ZoomOutMenuItem(final GlyphComponent<?> gc, final FitToEmMenuItem fitToEm) {
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
	
	public static final class ShowBoundingBoxMenuItem extends JCheckBoxMenuItem {
		private static final long serialVersionUID = 1L;
		public ShowBoundingBoxMenuItem(final GlyphComponent<?> gc) {
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
	
	public static final class PreviousGlyphMenuItem<G extends FontGlyph> extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PreviousGlyphMenuItem(final Frame frame, final GlyphEditPanel<G> panel, final Class<G> glyphClass) {
			super("Previous Glyph");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GlyphLocator<G> loc = panel.getGlyphLocator().getPrevious();
					if (loc == null) return;
					if (loc.getGlyph() == null) {
						try {
							G g = glyphClass.newInstance();
							loc.setGlyph(g);
							panel.getGlyphList().glyphRepertoireChanged();
						} catch (Exception ex) {
							return;
						}
					}
					panel.setGlyph(loc);
					frame.setTitle(loc.toString());
				}
			});
		}
	}
	
	public static final class NextGlyphMenuItem<G extends FontGlyph> extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public NextGlyphMenuItem(final Frame frame, final GlyphEditPanel<G> panel, final Class<G> glyphClass) {
			super("Next Glyph");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GlyphLocator<G> loc = panel.getGlyphLocator().getNext();
					if (loc == null) return;
					if (loc.getGlyph() == null) {
						try {
							G g = glyphClass.newInstance();
							loc.setGlyph(g);
							panel.getGlyphList().glyphRepertoireChanged();
						} catch (Exception ex) {
							return;
						}
					}
					panel.setGlyph(loc);
					frame.setTitle(loc.toString());
				}
			});
		}
	}
	
	public static final class PreviousDefinedGlyphMenuItem<G extends FontGlyph> extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PreviousDefinedGlyphMenuItem(final Frame frame, final GlyphEditPanel<G> panel) {
			super("Previous Defined Glyph");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GlyphLocator<G> loc = panel.getGlyphLocator().getPreviousDefined();
					if (loc == null) return;
					panel.setGlyph(loc);
					frame.setTitle(loc.toString());
				}
			});
		}
	}
	
	public static final class NextDefinedGlyphMenuItem<G extends FontGlyph> extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public NextDefinedGlyphMenuItem(final Frame frame, final GlyphEditPanel<G> panel) {
			super("Next Defined Glyph");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GlyphLocator<G> loc = panel.getGlyphLocator().getNextDefined();
					if (loc == null) return;
					panel.setGlyph(loc);
					frame.setTitle(loc.toString());
				}
			});
		}
	}
}
