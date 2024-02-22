package com.kreative.bitsnpicas.edit;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.edit.exporter.BitmapExportFrame;
import com.kreative.bitsnpicas.main.ViewFont;

public class BitmapListMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public BitmapListMenuBar(final Frame frame, final SaveManager sm, final BitmapFont font, final GlyphList<BitmapFontGlyph> gl) {
		add(new FileMenu(frame, sm, font, gl));
		add(new EditMenu(frame, gl));
		add(new GlyphListMenuBar.ViewMenu(frame, gl));
		add(new TransformMenu(gl));
	}
	
	public static final class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Frame frame, final SaveManager sm, final BitmapFont font, final GlyphList<BitmapFontGlyph> gl) {
			super("File");
			add(new CommonMenuItems.NewMenu());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(frame));
			addSeparator();
			add(new CommonMenuItems.SaveMenuItem(sm));
			add(new CommonMenuItems.SaveAsMenuItem(sm));
			add(new ExportMenuItem(font));
			addSeparator();
			add(new ImportMenuItem(frame, gl));
			addSeparator();
			add(new CommonMenuItems.FontInfoMenuItem(font, sm));
			add(new PreviewMenuItem(font, gl));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
	
	public static final class ExportMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ExportMenuItem(final BitmapFont font) {
			super("Export...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new BitmapExportFrame(font).setVisible(true);
				}
			});
		}
	}
	
	public static final class ImportMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		private String lastDirectory = null;
		public ImportMenuItem(final Frame frame, final GlyphList<BitmapFontGlyph> gl) {
			super("Import Image...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					List<GlyphLocator<BitmapFontGlyph>> locators = gl.getSelection();
					if (locators.isEmpty()) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					FileDialog fd = new FileDialog(frame, "Import Image", FileDialog.LOAD);
					if (lastDirectory != null) fd.setDirectory(lastDirectory);
					fd.setVisible(true);
					String ds = fd.getDirectory(), fs = fd.getFile();
					fd.dispose();
					if (ds == null || fs == null) return;
					File file = new File((lastDirectory = ds), fs);
					try {
						BufferedImage image = ImageIO.read(file);
						if (image != null) {
							for (GlyphLocator<BitmapFontGlyph> loc : locators) {
								BitmapFontGlyph glyph = new BitmapFontGlyph();
								glyph.setToImage(0, -loc.getGlyphFont().getLineAscent(), image);
								glyph.setCharacterWidth(image.getWidth());
								loc.setGlyph(glyph);
							}
							gl.glyphContentChanged();
						} else {
							JOptionPane.showMessageDialog(
								null, "The selected file was not recognized as an image file.",
								"Import Image", JOptionPane.ERROR_MESSAGE
							);
						}
					} catch (IOException ioe) {
						JOptionPane.showMessageDialog(
							null, "An error occurred while reading the selected file.",
							"Import Image", JOptionPane.ERROR_MESSAGE
						);
					}
				}
			});
		}
	}
	
	public static final class PreviewMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PreviewMenuItem(final BitmapFont font, final GlyphList<BitmapFontGlyph> gl) {
			super("Preview");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final ViewFont vf = new ViewFont(font);
					final GlyphListListener<BitmapFontGlyph> gll = new GlyphListListener<BitmapFontGlyph>() {
						public void selectionChanged(GlyphList<BitmapFontGlyph> gl, Font<BitmapFontGlyph> font) {}
						public void selectionOpened(GlyphList<BitmapFontGlyph> gl, Font<BitmapFontGlyph> font) {}
						public void metricsChanged(GlyphList<BitmapFontGlyph> gl, Font<BitmapFontGlyph> font) {
							vf.fontChanged();
						}
						public void glyphsChanged(GlyphList<BitmapFontGlyph> gl, Font<BitmapFontGlyph> font) {
							vf.fontChanged();
						}
					};
					vf.setVisible(true);
					vf.addWindowListener(new WindowAdapter() {
						public void windowClosed(WindowEvent e) {
							gl.removeGlyphListListener(gll);
						}
					});
					gl.addGlyphListListener(gll);
				}
			});
		}
	}
	
	public static final class EditMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public EditMenu(final Frame frame, final GlyphList<BitmapFontGlyph> gl) {
			super("Edit");
			add(new CutMenuItem(gl));
			add(new CopyMenuItem(gl));
			add(new PasteMenuItem(gl, false));
			add(new PasteMenuItem(gl, true));
			add(new ClearMenuItem(gl));
			addSeparator();
			add(new GlyphListMenuBar.SelectAllMenuItem(gl));
			add(new GlyphListMenuBar.SelectNoneMenuItem(gl));
			add(new GlyphListMenuBar.SetSelectionMenuItem(frame, gl));
			addSeparator();
			add(new CreateMenuItem(frame, gl));
			add(new MoveMenuItem(frame, gl, false));
			add(new MoveMenuItem(frame, gl, true));
			add(new GlyphListMenuBar.EditMenuItem(gl));
			add(new GlyphListMenuBar.DeleteMenuItem(gl));
			addSeparator();
			add(new GenerateMenu(gl));
			addSeparator();
			add(new CommonMenuItems.FontMapMenuItem());
		}
	}
	
	public static final class CutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CutMenuItem(final GlyphList<BitmapFontGlyph> gl) {
			super("Cut");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					List<GlyphLocator<BitmapFontGlyph>> locators = gl.getSelection();
					if (locators.isEmpty()) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					BitmapGlyphListSelection sel = new BitmapGlyphListSelection(locators);
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(sel, sel);
					gl.deleteSelection();
				}
			});
		}
	}
	
	public static final class CopyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyMenuItem(final GlyphList<BitmapFontGlyph> gl) {
			super("Copy");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					List<GlyphLocator<BitmapFontGlyph>> locators = gl.getSelection();
					if (locators.isEmpty()) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					BitmapGlyphListSelection sel = new BitmapGlyphListSelection(locators);
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(sel, sel);
				}
			});
		}
	}
	
	public static final class PasteMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PasteMenuItem(final GlyphList<BitmapFontGlyph> gl, final boolean into) {
			super(into ? "Paste Into" : "Paste");
			setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_V,
				CommonMenuItems.SHORTCUT_KEY | (into ? KeyEvent.SHIFT_MASK : 0)
			));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					List<GlyphLocator<BitmapFontGlyph>> locators = gl.getSelection();
					if (locators.isEmpty()) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					try {
						Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
						if (cb.isDataFlavorAvailable(BitmapGlyphListSelection.flavor)) {
							BitmapGlyphState[] content = (BitmapGlyphState[])cb.getData(BitmapGlyphListSelection.flavor);
							if (content.length > 0) {
								for (int i = 0, n = locators.size(); i < n; i++) {
									BitmapFontGlyph glyph = new BitmapFontGlyph();
									content[i % content.length].apply(glyph);
									if (into) glyph = BitmapFontGlyph.compose(locators.get(i).getGlyph(), glyph);
									locators.get(i).setGlyph(glyph);
								}
								if (locators.size() < content.length) {
									HashSet<Integer> indices = new HashSet<Integer>();
									for (GlyphLocator<BitmapFontGlyph> loc : locators) indices.add(loc.getGlyphIndex());
									GlyphLocator<BitmapFontGlyph> last = locators.get(locators.size() - 1);
									for (int i = locators.size(); i < content.length; i++) {
										last = last.getNext();
										if (last == null) break;
										int index = last.getGlyphIndex();
										if (index <= 0) break;
										indices.add(index);
										BitmapFontGlyph glyph = new BitmapFontGlyph();
										content[i].apply(glyph);
										if (into) glyph = BitmapFontGlyph.compose(last.getGlyph(), glyph);
										last.setGlyph(glyph);
									}
									gl.setSelectedIndices(indices, true);
								}
								gl.glyphContentChanged();
								return;
							}
						} else if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
							Image content = (Image)cb.getData(DataFlavor.imageFlavor);
							BufferedImage image = SwingUtils.toBufferedImage(content);
							if (image != null) {
								for (GlyphLocator<BitmapFontGlyph> loc : locators) {
									BitmapFontGlyph glyph = new BitmapFontGlyph();
									glyph.setToImage(0, -loc.getGlyphFont().getLineAscent(), image);
									glyph.setCharacterWidth(image.getWidth());
									if (into) glyph = BitmapFontGlyph.compose(loc.getGlyph(), glyph);
									loc.setGlyph(glyph);
								}
								gl.glyphContentChanged();
								return;
							}
						} else if (cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
							String content = (String)cb.getData(DataFlavor.stringFlavor);
							if (content != null) {
								Font<BitmapFontGlyph> font = gl.getGlyphFont();
								List<BitmapFontGlyph> glyphs = new ArrayList<BitmapFontGlyph>();
								int stringHeight = font.getLineAscent() + font.getLineDescent();
								int stringWidth = 0;
								int i = 0, n = content.length();
								while (i < n) {
									int cp = content.codePointAt(i);
									i += Character.charCount(cp);
									BitmapFontGlyph g = font.getCharacter(cp);
									if (g == null) continue;
									glyphs.add(g);
									stringWidth += g.getCharacterWidth();
									if (stringWidth >= 1024) {
										Toolkit.getDefaultToolkit().beep();
										return;
									}
								}
								BufferedImage image = new BufferedImage(stringWidth, stringHeight, BufferedImage.TYPE_INT_ARGB);
								Graphics2D gr = image.createGraphics();
								gr.setColor(Color.black);
								double x = 0, a = font.getLineAscent();
								for (BitmapFontGlyph g : glyphs) x += g.paint(gr, x, a, 1);
								gr.dispose();
								for (GlyphLocator<BitmapFontGlyph> loc : locators) {
									BitmapFontGlyph glyph = new BitmapFontGlyph();
									glyph.setToImage(0, -loc.getGlyphFont().getLineAscent(), image);
									glyph.setCharacterWidth(image.getWidth());
									if (into) glyph = BitmapFontGlyph.compose(loc.getGlyph(), glyph);
									loc.setGlyph(glyph);
								}
								gl.glyphContentChanged();
								return;
							}
						}
					} catch (IOException ioe) {
						ioe.printStackTrace();
					} catch (UnsupportedFlavorException ufe) {
						ufe.printStackTrace();
					}
					Toolkit.getDefaultToolkit().beep();
				}
			});
		}
	}
	
	public static final class ClearMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ClearMenuItem(final GlyphList<BitmapFontGlyph> gl) {
			super("Clear");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (gl.getSelection().isEmpty()) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					gl.deleteSelection();
				}
			});
		}
	}
	
	public static final class CreateMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CreateMenuItem(final Frame frame, final GlyphList<BitmapFontGlyph> gl) {
			super("Create Glyphs...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new CreateGlyphsDialog<BitmapFontGlyph>(frame, gl) {
						private static final long serialVersionUID = 1L;
						public BitmapFontGlyph createGlyph() {
							return new BitmapFontGlyph();
						}
					}.setVisible(true);
				}
			});
		}
	}
	
	public static final class MoveMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public MoveMenuItem(final Frame frame, final GlyphList<BitmapFontGlyph> gl, final boolean copy) {
			super(copy ? "Copy Glyphs..." : "Move Glyphs...");
			setAccelerator(KeyStroke.getKeyStroke(
				copy ? KeyEvent.VK_C : KeyEvent.VK_X,
				CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK
			));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (gl.getSelection().isEmpty()) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					new MoveGlyphsDialog<BitmapFontGlyph, BitmapGlyphState>(frame, copy, gl) {
						private static final long serialVersionUID = 1L;
						public BitmapGlyphState serializeGlyph(BitmapFontGlyph g) {
							return new BitmapGlyphState(g);
						}
						public BitmapFontGlyph deserializeGlyph(BitmapGlyphState s) {
							BitmapFontGlyph g = new BitmapFontGlyph();
							s.apply(g);
							return g;
						}
					}.setVisible(true);
				}
			});
		}
	}
	
	public static final class GenerateMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public GenerateMenu(final GlyphList<BitmapFontGlyph> gl) {
			super("Generate");
			add(new GenerateMenuItem(new UnifontHexGlyphGenerator(), gl));
			add(new GenerateMenuItem(new TimestampGlyphGenerator(), gl));
			add(new GenerateMenuItem(new SitelenPonaCartoucheGlyphGenerator(), gl));
		}
	}
	
	public static final class GenerateMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public GenerateMenuItem(final GlyphGenerator<BitmapFontGlyph> gen, final GlyphList<BitmapFontGlyph> gl) {
			super(gen.getName());
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switch (gen.generate(gl.getGlyphFont(), gl.getSelection())) {
						case NO_CHANGE: Toolkit.getDefaultToolkit().beep(); return;
						case CONTENT_CHANGED: gl.glyphContentChanged(); return;
						case REPERTOIRE_CHANGED: gl.glyphRepertoireChanged(); return;
					}
				}
			});
		}
	}
	
	public static final class TransformMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public TransformMenu(final GlyphList<BitmapFontGlyph> gl) {
			super("Transform");
			for (BitmapGlyphTransform tx : BitmapGlyphTransform.TRANSFORMS) {
				if (tx == null) addSeparator();
				else add(new TransformMenuItem(tx, gl));
			}
		}
	}
	
	public static final class TransformMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public TransformMenuItem(final BitmapGlyphTransform tx, final GlyphList<BitmapFontGlyph> gl) {
			super(tx.name);
			setAccelerator(tx.keystroke);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					List<GlyphLocator<BitmapFontGlyph>> locators = gl.getSelection();
					if (locators.isEmpty()) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					IdentityHashMap<BitmapFontGlyph,BitmapFontGlyph> processed;
					processed = new IdentityHashMap<BitmapFontGlyph,BitmapFontGlyph>();
					for (GlyphLocator<BitmapFontGlyph> loc : locators) {
						Font<BitmapFontGlyph> font = loc.getGlyphFont();
						BitmapFontGlyph glyph = loc.getGlyph();
						if (glyph != null && !processed.containsKey(glyph)) {
							tx.transform(font, glyph);
							processed.put(glyph, glyph);
						}
					}
					gl.glyphContentChanged();
				}
			});
		}
	}
}
