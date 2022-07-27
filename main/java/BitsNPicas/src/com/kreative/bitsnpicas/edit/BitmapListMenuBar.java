package com.kreative.bitsnpicas.edit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
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
import java.util.GregorianCalendar;
import java.util.HashSet;
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
import com.kreative.bitsnpicas.edit.BitmapGlyphTransform.BitmapGlyphTransformInfo;
import com.kreative.bitsnpicas.edit.MoveGlyphsDialog.Result;
import com.kreative.bitsnpicas.edit.exporter.BitmapExportFrame;
import com.kreative.bitsnpicas.main.ViewFont;

public class BitmapListMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public BitmapListMenuBar(final Window window, final SaveManager sm, final BitmapFont font, final GlyphList<BitmapFontGlyph> gl) {
		add(new FileMenu(window, sm, font, gl));
		add(new EditMenu(window, gl));
		add(new GlyphListMenuBar.ViewMenu(window, gl));
		add(new TransformMenu(gl));
	}
	
	public static final class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final SaveManager sm, final BitmapFont font, final GlyphList<BitmapFontGlyph> gl) {
			super("File");
			add(new CommonMenuItems.NewMenu());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			addSeparator();
			add(new CommonMenuItems.SaveMenuItem(sm));
			add(new CommonMenuItems.SaveAsMenuItem(sm));
			add(new ExportMenuItem(font));
			addSeparator();
			add(new ImportMenuItem(gl));
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
		public ImportMenuItem(final GlyphList<BitmapFontGlyph> gl) {
			super("Import Image...");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FileDialog fd = new FileDialog(new Frame(), "Import Image", FileDialog.LOAD);
					fd.setVisible(true);
					if (fd.getDirectory() == null || fd.getFile() == null) return;
					File file = new File(fd.getDirectory(), fd.getFile());
					try {
						BufferedImage image = ImageIO.read(file);
						if (image != null) {
							for (GlyphLocator<BitmapFontGlyph> loc : gl.getSelection()) {
								BitmapFontGlyph glyph = new BitmapFontGlyph();
								glyph.setToImage(0, -loc.getGlyphFont().getLineAscent(), image);
								glyph.setCharacterWidth(image.getWidth());
								loc.setGlyph(glyph);
							}
							gl.glyphsChanged();
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
		public EditMenu(final Window window, final GlyphList<BitmapFontGlyph> gl) {
			super("Edit");
			add(new CutMenuItem(gl));
			add(new CopyMenuItem(gl));
			add(new PasteMenuItem(gl));
			add(new ClearMenuItem(gl));
			addSeparator();
			add(new GlyphListMenuBar.SelectAllMenuItem(gl));
			add(new GlyphListMenuBar.SelectNoneMenuItem(gl));
			add(new GlyphListMenuBar.SetSelectionMenuItem(window, gl));
			addSeparator();
			add(new MoveMenuItem(window, gl, false));
			add(new MoveMenuItem(window, gl, true));
			add(new GlyphListMenuBar.EditMenuItem(gl));
			add(new GlyphListMenuBar.DeleteMenuItem(gl));
			addSeparator();
			add(new GenerateUnifontHexGlyphMenuItem(gl));
			add(new GenerateTimestampGlyphMenuItem(gl));
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
					BitmapGlyphListSelection sel = new BitmapGlyphListSelection(gl.getSelection());
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
					BitmapGlyphListSelection sel = new BitmapGlyphListSelection(gl.getSelection());
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(sel, sel);
				}
			});
		}
	}
	
	public static final class PasteMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PasteMenuItem(final GlyphList<BitmapFontGlyph> gl) {
			super("Paste");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
						if (cb.isDataFlavorAvailable(BitmapGlyphListSelection.flavor)) {
							BitmapGlyphState[] content = (BitmapGlyphState[])cb.getData(BitmapGlyphListSelection.flavor);
							if (content.length > 0) {
								List<GlyphLocator<BitmapFontGlyph>> sel = gl.getSelection();
								if (sel.isEmpty()) return;
								for (int i = 0, n = sel.size(); i < n; i++) {
									BitmapFontGlyph glyph = new BitmapFontGlyph();
									content[i % content.length].apply(glyph);
									sel.get(i).setGlyph(glyph);
								}
								GlyphLocator<BitmapFontGlyph> last = sel.get(sel.size() - 1);
								for (int i = sel.size(); i < content.length; i++) {
									last = last.getNext();
									if (last == null) break;
									BitmapFontGlyph glyph = new BitmapFontGlyph();
									content[i].apply(glyph);
									last.setGlyph(glyph);
								}
							}
							gl.glyphsChanged();
						} else if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
							Image content = (Image)cb.getData(DataFlavor.imageFlavor);
							BufferedImage image = SwingUtils.toBufferedImage(content);
							if (image != null) {
								for (GlyphLocator<BitmapFontGlyph> loc : gl.getSelection()) {
									BitmapFontGlyph glyph = new BitmapFontGlyph();
									glyph.setToImage(0, -loc.getGlyphFont().getLineAscent(), image);
									glyph.setCharacterWidth(image.getWidth());
									loc.setGlyph(glyph);
								}
							}
							gl.glyphsChanged();
						}
					} catch (IOException ioe) {
						ioe.printStackTrace();
					} catch (UnsupportedFlavorException ufe) {
						ufe.printStackTrace();
					}
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
					gl.deleteSelection();
				}
			});
		}
	}
	
	public static final class MoveMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public MoveMenuItem(final Window window, final GlyphList<BitmapFontGlyph> gl, final boolean copy) {
			super(copy ? "Copy Glyphs..." : "Move Glyphs...");
			setAccelerator(KeyStroke.getKeyStroke(
				copy ? KeyEvent.VK_C : KeyEvent.VK_X,
				CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK
			));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean isUnicode = isUnicodeRange(gl.getModel());
					Result result = new MoveGlyphsDialog(window, copy, !isUnicode).showDialog();
					if (result == null) return;
					
					List<GlyphLocator<BitmapFontGlyph>> locators = gl.getSelection();
					if (locators.isEmpty()) return;
					
					List<BitmapGlyphState> states = new ArrayList<BitmapGlyphState>();
					for (GlyphLocator<BitmapFontGlyph> loc : locators) {
						BitmapFontGlyph glyph = loc.getGlyph();
						if (glyph == null) states.add(null);
						else states.add(new BitmapGlyphState(glyph));
					}
					
					if (!copy) gl.deleteSelection();
					
					Font<BitmapFontGlyph> font = gl.getGlyphFont();
					GlyphListModel model = gl.getModel();
					HashSet<Integer> selectedIndices = new HashSet<Integer>();
					if (result.byIndex) {
						int gn = model.getCellCount();
						for (int i = 0, n = locators.size(); i < n; i++) {
							int gi = locators.get(i).getGlyphIndex();
							if (gi < 0) continue;
							if (result.relative) gi += result.offset;
							else gi = result.offset + i;
							if (gi >= 0 && gi < gn) {
								selectedIndices.add(gi);
								BitmapGlyphState state = states.get(i);
								if (state != null) {
									BitmapFontGlyph g = new BitmapFontGlyph();
									state.apply(g);
									new GlyphLocator<BitmapFontGlyph>(font, model, gi).setGlyph(g);
								}
							}
						}
					} else {
						for (int i = 0, n = locators.size(); i < n; i++) {
							GlyphLocator<BitmapFontGlyph> loc = locators.get(i);
							BitmapGlyphState state = states.get(i);
							int cp;
							if (result.relative) {
								if (loc.isCodePoint()) {
									cp = loc.getCodePoint() + result.offset;
								} else {
									int gi = loc.getGlyphIndex();
									if (gi >= 0) selectedIndices.add(gi);
									if (states.get(i) != null) {
										BitmapFontGlyph g = new BitmapFontGlyph();
										state.apply(g);
										loc.setGlyph(g);
									}
									continue;
								}
							} else {
								cp = result.offset + i;
							}
							if (Character.isValidCodePoint(cp)) {
								int gi = model.indexOfCodePoint(cp);
								if (gi >= 0) selectedIndices.add(gi);
								if (states.get(i) != null) {
									BitmapFontGlyph g = new BitmapFontGlyph();
									state.apply(g);
									font.putCharacter(cp, g);
								}
							}
						}
					}
					gl.glyphsChanged();
					gl.setSelectedIndices(selectedIndices);
				}
			});
		}
		private static boolean isUnicodeRange(GlyphListModel model) {
			for (int lastCP = -1, i = 0, n = model.getCellCount(); i < n; i++) {
				if (model.isCodePoint(i)) {
					int cp = model.getCodePoint(i);
					if (lastCP < 0 || (lastCP + 1) == cp) {
						lastCP = cp;
						continue;
					}
				}
				return false;
			}
			return true;
		}
	}
	
	public static final class GenerateUnifontHexGlyphMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public GenerateUnifontHexGlyphMenuItem(final GlyphList<BitmapFontGlyph> gl) {
			super("Generate Unifont Hex Glyph");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (GlyphLocator<BitmapFontGlyph> loc : gl.getSelection()) {
						if (loc.isCodePoint()) {
							loc.setGlyph(UnifontHexGlyphGenerator.createGlyph(loc.getCodePoint()));
						}
					}
					gl.glyphsChanged();
				}
			});
		}
	}
	
	public static final class GenerateTimestampGlyphMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public GenerateTimestampGlyphMenuItem(final GlyphList<BitmapFontGlyph> gl) {
			super("Generate Timestamp Glyph");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Font<BitmapFontGlyph> font = gl.getGlyphFont();
					GregorianCalendar now = new GregorianCalendar();
					int y = now.get(GregorianCalendar.YEAR);
					int m = now.get(GregorianCalendar.MONTH) + 1;
					int d = now.get(GregorianCalendar.DAY_OF_MONTH);
					BitmapFontGlyph[] namedGlyphs = {
						font.getNamedGlyph("timestamp.ch" + ((y / 1000) % 10)),
						font.getNamedGlyph("timestamp.cl" + ((y /  100) % 10)),
						font.getNamedGlyph("timestamp.yh" + ((y /   10) % 10)),
						font.getNamedGlyph("timestamp.yl" + ((y /    1) % 10)),
						font.getNamedGlyph("timestamp.mh" + ((m /   10) % 10)),
						font.getNamedGlyph("timestamp.ml" + ((m /    1) % 10)),
						font.getNamedGlyph("timestamp.dh" + ((d /   10) % 10)),
						font.getNamedGlyph("timestamp.dl" + ((d /    1) % 10)),
						font.getNamedGlyph("timestamp"),
					};
					for (BitmapFontGlyph g : namedGlyphs) {
						if (g != null) {
							for (GlyphLocator<BitmapFontGlyph> loc : gl.getSelection()) {
								loc.setGlyph(BitmapFontGlyph.compose(namedGlyphs));
							}
							gl.glyphsChanged();
							return;
						}
					}
					BitmapFontGlyph[] mappedGlyphs = {
						font.getCharacter(0x10FF40 + ((y / 1000) % 10)),
						font.getCharacter(0x10FF50 + ((y /  100) % 10)),
						font.getCharacter(0x10FF60 + ((y /   10) % 10)),
						font.getCharacter(0x10FF70 + ((y /    1) % 10)),
						font.getCharacter(0x10FF80 + ((m /   10) % 10)),
						font.getCharacter(0x10FF90 + ((m /    1) % 10)),
						font.getCharacter(0x10FFA0 + ((d /   10) % 10)),
						font.getCharacter(0x10FFB0 + ((d /    1) % 10)),
						font.getCharacter(0x10FFC0)
					};
					for (BitmapFontGlyph g : mappedGlyphs) {
						if (g != null) {
							for (GlyphLocator<BitmapFontGlyph> loc : gl.getSelection()) {
								loc.setGlyph(BitmapFontGlyph.compose(mappedGlyphs));
							}
							gl.glyphsChanged();
							return;
						}
					}
				}
			});
		}
	}
	
	public static final class TransformMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public TransformMenu(final GlyphList<BitmapFontGlyph> gl) {
			super("Transform");
			for (BitmapGlyphTransformInfo txi : BitmapGlyphTransform.TRANSFORMS) {
				if (txi == null) addSeparator();
				else add(new TransformMenuItem(txi, gl));
			}
		}
	}
	
	public static final class TransformMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public TransformMenuItem(final BitmapGlyphTransformInfo txi, final GlyphList<BitmapFontGlyph> gl) {
			super(txi.name);
			setAccelerator(txi.keystroke);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (GlyphLocator<BitmapFontGlyph> loc : gl.getSelection()) {
						Font<BitmapFontGlyph> font = loc.getGlyphFont();
						BitmapFontGlyph glyph = loc.getGlyph();
						if (glyph != null) txi.transform.transform(font, glyph);
					}
					gl.glyphsChanged();
				}
			});
		}
	}
}
