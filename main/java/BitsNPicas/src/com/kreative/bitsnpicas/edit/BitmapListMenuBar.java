package com.kreative.bitsnpicas.edit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import javax.imageio.ImageIO;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.edit.BitmapGlyphTransform.BitmapGlyphTransformInfo;
import com.kreative.bitsnpicas.edit.MoveGlyphsDialog.Result;
import com.kreative.bitsnpicas.main.ViewFont;
import com.kreative.bitsnpicas.unicode.Block;

public class BitmapListMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	
	public BitmapListMenuBar(final Window window, final SaveManager sm, final BitmapFont font, final GlyphList gl) {
		add(new FileMenu(window, sm, font, gl));
		add(new EditMenu(font, gl, window, sm));
		add(new GlyphListMenuBar.ViewMenu(window, gl));
		add(new TransformMenu(font, gl));
	}
	
	public static class FileMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public FileMenu(final Window window, final SaveManager sm, final BitmapFont font, final GlyphList gl) {
			super("File");
			add(new CommonMenuItems.NewMenu());
			add(new CommonMenuItems.OpenMenuItem());
			add(new CommonMenuItems.CloseMenuItem(window));
			addSeparator();
			add(new CommonMenuItems.SaveMenuItem(sm));
			add(new CommonMenuItems.SaveAsMenuItem(sm));
			add(new ExportMenuItem(font));
			addSeparator();
			add(new ImportMenuItem(font, gl));
			addSeparator();
			add(new CommonMenuItems.FontInfoMenuItem(font, sm));
			add(new PreviewMenuItem(font, gl));
			if (!CommonMenuItems.IS_MAC_OS) {
				addSeparator();
				add(new CommonMenuItems.ExitMenuItem());
			}
		}
	}
	
	public static class ExportMenuItem extends JMenuItem {
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
	
	public static class ImportMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ImportMenuItem(final BitmapFont font, final GlyphList gl) {
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
							for (int cp : gl.getSelectedCodePoints()) {
								BitmapFontGlyph glyph = font.getCharacter(cp);
								if (glyph == null) {
									glyph = new BitmapFontGlyph();
									font.putCharacter(cp, glyph);
								}
								glyph.setToImage(0, -font.getLineAscent(), image);
								glyph.setCharacterWidth(image.getWidth());
							}
							font.glyphsChanged();
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
	
	public static class PreviewMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PreviewMenuItem(final BitmapFont font, final GlyphList gl) {
			super("Preview");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new ViewFont(font, gl).setVisible(true);
				}
			});
		}
	}
	
	public static class EditMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public EditMenu(final BitmapFont font, final GlyphList gl, final Window window, final SaveManager sm) {
			super("Edit");
			add(new CutMenuItem(font, gl));
			add(new CopyMenuItem(font, gl));
			add(new PasteMenuItem(font, gl));
			add(new ClearMenuItem(font, gl));
			addSeparator();
			add(new GlyphListMenuBar.SelectAllMenuItem(gl));
			add(new GlyphListMenuBar.SelectNoneMenuItem(gl));
			add(new GlyphListMenuBar.SetSelectionMenuItem(window, gl));
			addSeparator();
			add(new MoveMenuItem(window, font, gl, false));
			add(new MoveMenuItem(window, font, gl, true));
			add(new GlyphListMenuBar.EditMenuItem(font, gl, sm));
			add(new GlyphListMenuBar.DeleteMenuItem(font, gl));
		}
	}
	
	public static class CutMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CutMenuItem(final BitmapFont font, final GlyphList gl) {
			super("Cut");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new BitmapGlyphListSelection(font, gl.getSelectedCodePoints()), new ClipboardOwner() {
						public void lostOwnership(Clipboard cb, Transferable t) {}
					});
					for (int cp : gl.getSelectedCodePoints()) {
						font.removeCharacter(cp);
					}
					font.glyphsChanged();
				}
			});
		}
	}
	
	public static class CopyMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public CopyMenuItem(final BitmapFont font, final GlyphList gl) {
			super("Copy");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					cb.setContents(new BitmapGlyphListSelection(font, gl.getSelectedCodePoints()), new ClipboardOwner() {
						public void lostOwnership(Clipboard cb, Transferable t) {}
					});
				}
			});
		}
	}
	
	public static class PasteMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public PasteMenuItem(final BitmapFont font, final GlyphList gl) {
			super("Paste");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, CommonMenuItems.SHORTCUT_KEY));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
						if (cb.isDataFlavorAvailable(BitmapGlyphListSelection.flavor)) {
							BitmapGlyphState[] content = (BitmapGlyphState[])cb.getData(BitmapGlyphListSelection.flavor);
							if (content.length > 0) {
								List<Integer> cps = gl.getSelectedCodePoints();
								for (int i = 0, n = cps.size(); i < n; i++) {
									BitmapFontGlyph glyph = new BitmapFontGlyph();
									content[i % content.length].apply(glyph);
									font.putCharacter(cps.get(i), glyph);
								}
							}
							font.glyphsChanged();
						} else if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
							Image content = (Image)cb.getData(DataFlavor.imageFlavor);
							BufferedImage image = SwingUtils.toBufferedImage(content);
							if (image != null) {
								for (int cp : gl.getSelectedCodePoints()) {
									BitmapFontGlyph glyph = new BitmapFontGlyph();
									glyph.setToImage(0, -font.getLineAscent(), image);
									glyph.setCharacterWidth(image.getWidth());
									font.putCharacter(cp, glyph);
								}
							}
							font.glyphsChanged();
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
	
	public static class ClearMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ClearMenuItem(final BitmapFont font, final GlyphList gl) {
			super("Clear");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int cp : gl.getSelectedCodePoints()) {
						font.removeCharacter(cp);
					}
					font.glyphsChanged();
				}
			});
		}
	}
	
	public static class MoveMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public MoveMenuItem(final Window window, final BitmapFont font, final GlyphList gl, final boolean copy) {
			super(copy ? "Copy Glyphs..." : "Move Glyphs...");
			setAccelerator(KeyStroke.getKeyStroke(
				copy ? KeyEvent.VK_C : KeyEvent.VK_X,
				CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK
			));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					List<Integer> codePointList = gl.getCodePointList();
					boolean byIndex = !(codePointList instanceof Block);
					Result result = new MoveGlyphsDialog(window, copy, byIndex).showDialog();
					if (result == null) return;
					if (result.byIndex) {
						SortedSet<Integer> indices = gl.getSelectedIndices();
						if (indices.isEmpty()) return;
						List<BitmapFontGlyph> glyphs = new ArrayList<BitmapFontGlyph>();
						for (int i : indices) glyphs.add(font.getCharacter(codePointList.get(i)));
						if (!copy) for (int i : indices) font.removeCharacter(codePointList.get(i));
						List<Integer> newSel = new ArrayList<Integer>();
						int start = indices.first(), limit = codePointList.size();
						Iterator<Integer> indexIter = indices.iterator();
						Iterator<BitmapFontGlyph> glyphIter = glyphs.iterator();
						while (indexIter.hasNext() && glyphIter.hasNext()) {
							int index = indexIter.next();
							BitmapFontGlyph glyph = glyphIter.next();
							if (glyph == null) continue;
							if (copy) {
								BitmapGlyphState state = new BitmapGlyphState(glyph);
								glyph = new BitmapFontGlyph();
								state.apply(glyph);
							}
							index += result.offset;
							if (!result.relative) index -= start;
							if (index < 0 || index >= limit) continue;
							int cp = codePointList.get(index);
							if (cp < 0 || cp >= 0x110000) continue;
							font.putCharacter(cp, glyph);
							newSel.add(index);
						}
						font.glyphsChanged();
						gl.setSelectedIndices(newSel);
					} else {
						List<Integer> codePoints = gl.getSelectedCodePoints();
						if (codePoints.isEmpty()) return;
						List<BitmapFontGlyph> glyphs = new ArrayList<BitmapFontGlyph>();
						for (int cp : codePoints) glyphs.add(font.getCharacter(cp));
						if (!copy) for (int cp : codePoints) font.removeCharacter(cp);
						List<Integer> newSel = new ArrayList<Integer>();
						int start = codePoints.get(0);
						Iterator<Integer> cpIter = codePoints.iterator();
						Iterator<BitmapFontGlyph> glyphIter = glyphs.iterator();
						while (cpIter.hasNext() && glyphIter.hasNext()) {
							int cp = cpIter.next();
							BitmapFontGlyph glyph = glyphIter.next();
							if (glyph == null) continue;
							if (copy) {
								BitmapGlyphState state = new BitmapGlyphState(glyph);
								glyph = new BitmapFontGlyph();
								state.apply(glyph);
							}
							cp += result.offset;
							if (!result.relative) cp -= start;
							if (cp < 0 || cp >= 0x110000) continue;
							font.putCharacter(cp, glyph);
							newSel.add(cp);
						}
						font.glyphsChanged();
						gl.setSelectedCodePoints(newSel);
					}
				}
			});
		}
	}
	
	public static class TransformMenu extends JMenu {
		private static final long serialVersionUID = 1L;
		public TransformMenu(final BitmapFont font, final GlyphList gl) {
			super("Transform");
			for (BitmapGlyphTransformInfo txi : BitmapGlyphTransform.TRANSFORMS) {
				if (txi == null) addSeparator();
				else add(new TransformMenuItem(font, gl, txi.transform, txi.name, txi.keystroke));
			}
		}
	}
	
	public static class TransformMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public TransformMenuItem(
			final BitmapFont font, final GlyphList gl,
			final BitmapGlyphTransform tx, final String name, final KeyStroke ks
		) {
			super(name);
			setAccelerator(ks);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int cp : gl.getSelectedCodePoints()) {
						BitmapFontGlyph glyph = font.getCharacter(cp);
						if (glyph != null) tx.transform(font, glyph);
					}
					font.glyphsChanged();
				}
			});
		}
	}
}
