package com.kreative.bitsnpicas.edit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import javax.swing.JComponent;
import javax.swing.Scrollable;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;
import com.kreative.unicode.data.Block;
import com.kreative.unicode.data.NameResolver;
import com.kreative.unicode.fontmap.FontMapController;
import com.kreative.unicode.fontmap.FontMapEntry;

public class GlyphList<G extends FontGlyph> extends JComponent implements Scrollable {
	private static final long serialVersionUID = 1L;
	private static final int LABEL_HEIGHT = 18;
	private static final Color SUBTABLE_BG = new Color(0xFFAA00);
	private static final Color SUBTABLE_SELBG = new Color(0xCC7700);
	
	private final FontMapController fontMap;
	private final Font<G> font;
	private int cellSize;
	private int columnCount;
	private int rowCount;
	private boolean antiAlias;
	private boolean bufferedLabels;
	private boolean bufferedGlyphs;
	private Dimension preferredSize;
	private GlyphListModel model;
	private final GlyphListSelection selection;
	private final List<GlyphListListener<G>> listeners;
	
	public GlyphList(Font<G> font) {
		this.fontMap = FontMapController.getInstance();
		this.font = font;
		this.cellSize = 36;
		this.columnCount = 16;
		this.rowCount = 8;
		this.antiAlias = CommonMenuItems.IS_MAC_OS;
		this.bufferedLabels = false;
		this.bufferedGlyphs = false;
		this.preferredSize = null;
		this.model = new GlyphListCodePointModel(new Block(0, 127, "Basic Latin"));
		this.selection = new GlyphListSelection();
		this.listeners = new ArrayList<GlyphListListener<G>>();
		MyMouseListener ml = new MyMouseListener();
		this.addMouseListener(ml);
		this.addMouseMotionListener(ml);
		MyKeyListener kl = new MyKeyListener();
		this.addKeyListener(kl);
		this.setFocusable(true);
		MyComponentListener cl = new MyComponentListener();
		this.addComponentListener(cl);
	}
	
	public Font<G> getGlyphFont() {
		return this.font;
	}
	
	public int getCellSize() {
		return this.cellSize;
	}
	
	public void setCellSize(int cellSize) {
		if (this.cellSize != cellSize && cellSize >= 8) {
			this.cellSize = cellSize;
			this.revalidate();
			this.repaint();
		}
	}
	
	public int getColumnCount() {
		return this.columnCount;
	}
	
	public void setColumnCount(int columnCount) {
		if (this.columnCount != columnCount && columnCount > 0) {
			this.columnCount = columnCount;
			this.revalidate();
			this.repaint();
		}
	}
	
	public void setBestColumnCount() {
		Insets insets = getInsets();
		int w = getWidth() - insets.left - insets.right;
		int cc = w / cellSize;
		if (cc > 8) cc &=~ 7;
		if (this.columnCount != cc && cc > 0) {
			this.columnCount = cc;
			this.revalidate();
			this.repaint();
		}
	}
	
	public int getRowCount() {
		return this.rowCount;
	}
	
	public void setRowCount(int rowCount) {
		if (this.rowCount != rowCount && rowCount > 0) {
			this.rowCount = rowCount;
			this.revalidate();
			this.repaint();
		}
	}
	
	public boolean getAntiAlias() {
		return this.antiAlias;
	}
	
	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
		this.repaint();
	}
	
	public boolean getBufferedLabels() {
		return this.bufferedLabels;
	}
	
	public void setBufferedLabels(boolean bufferedLabels) {
		this.bufferedLabels = bufferedLabels;
		this.repaint();
	}
	
	public boolean getBufferedGlyphs() {
		return this.bufferedGlyphs;
	}
	
	public void setBufferedGlyphs(boolean bufferedGlyphs) {
		this.bufferedGlyphs = bufferedGlyphs;
		this.repaint();
	}
	
	public GlyphListModel getModel() {
		return this.model;
	}
	
	public void setModel(GlyphListModel model) {
		if (this.model != model && model != null) {
			this.model = model;
			this.model.tracksFont();
			this.selection.clear();
			for (GlyphListListener<G> l : listeners) l.selectionChanged(this, font);
			this.revalidate();
			this.repaint();
		}
	}
	
	public void clearSelection() {
		selection.clear();
		for (GlyphListListener<G> l : listeners) l.selectionChanged(this, font);
		repaint();
	}
	
	public void selectAll() {
		selection.clear();
		selection.add(0, model.getCellCount() - 1);
		for (GlyphListListener<G> l : listeners) l.selectionChanged(this, font);
		repaint();
	}
	
	public SortedSet<Integer> getSelectedIndices() {
		return selection.toSet();
	}
	
	public void setSelectedIndices(Collection<Integer> indices, boolean shouldScroll) {
		selection.clear();
		int n = model.getCellCount();
		for (Integer i : indices) {
			if (i != null && i >= 0 && i < n) {
				selection.add(i);
			}
		}
		if (shouldScroll) {
			Rectangle r = getSelectionRect();
			if (r != null) scrollRectToVisible(r);
		}
		for (GlyphListListener<G> l : listeners) l.selectionChanged(this, font);
		repaint();
	}
	
	public List<GlyphLocator<G>> getSelection() {
		List<GlyphLocator<G>> locators = new ArrayList<GlyphLocator<G>>();
		for (int i : selection.toSet()) {
			GlyphLocator<G> loc = new GlyphLocator<G>(font, model, i);
			if (loc.isValid()) locators.add(loc);
		}
		return locators;
	}
	
	public void openSelection() {
		if (selection.isEmpty()) return;
		for (GlyphListListener<G> l : listeners) l.selectionOpened(this, font);
	}
	
	public void deleteSelection() {
		if (selection.isEmpty()) return;
		for (GlyphLocator<G> loc : getSelection()) loc.removeGlyph();
		glyphRepertoireChanged();
	}
	
	public Dimension getPreferredSize() {
		if (preferredSize != null) {
			return preferredSize;
		} else {
			int rows = (model.getCellCount() + columnCount - 1) / columnCount;
			int w = cellSize * columnCount + 1;
			int h = (cellSize + LABEL_HEIGHT) * rows + 1;
			Insets i = getInsets();
			return new Dimension(w + i.left + i.right, h + i.top + i.bottom);
		}
	}
	
	public void setPreferredSize(Dimension d) {
		this.preferredSize = d;
	}
	
	public void addGlyphListListener(GlyphListListener<G> l) {
		this.listeners.add(l);
	}
	
	public void removeGlyphListListener(GlyphListListener<G> l) {
		this.listeners.remove(l);
	}
	
	public void metricsChanged() {
		for (GlyphListListener<G> l : listeners) l.metricsChanged(this, font);
		repaint();
	}
	
	public void glyphContentChanged() {
		for (GlyphListListener<G> l : listeners) l.glyphsChanged(this, font);
		repaint();
	}
	
	public void glyphRepertoireChanged() {
		if (model.tracksFont()) selection.clear();
		for (GlyphListListener<G> l : listeners) l.glyphsChanged(this, font);
		revalidate();
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
		SortedSet<Integer> sel = selection.toSet();
		Rectangle vr = getVisibleRect();
		Insets insets = getInsets();
		int w = getWidth() - insets.left - insets.right - 1;
		int bufw = (w / columnCount) + 1;
		BufferedImage lbuf = bufferedLabels ? new BufferedImage(bufw, LABEL_HEIGHT, BufferedImage.TYPE_INT_ARGB) : null;
		BufferedImage gbuf = bufferedGlyphs ? new BufferedImage(bufw, cellSize, BufferedImage.TYPE_INT_ARGB) : null;
		double fa = font.getEmAscent2D();
		double fh = fa + font.getEmDescent2D();
		double scale = (fh <= 0) ? ((cellSize - 3) / 10.0) : ((cellSize - 3) / fh);
		if (scale <= 0) scale = 1;
		if (scale >= 1) scale = Math.floor(scale);
		double ascent = Math.round(((cellSize - 3) - fh * scale) / 2 + fa * scale + 1);
		java.awt.Font markerFont = new java.awt.Font("Monospaced", 0, cellSize / 2);
		for (int i = 0, n = model.getCellCount(); i < n; i++) {
			int x1 = insets.left + w * (i % columnCount) / columnCount;
			int x2 = insets.left + w * ((i % columnCount) + 1) / columnCount;
			int y = insets.top + (cellSize + LABEL_HEIGHT) * (i / columnCount);
			if (vr.intersects(x1, y, x2 - x1 + 1, cellSize + LABEL_HEIGHT + 1)) {
				// Get glyph. If this index is undefined, skip this cell.
				String label;
				FontGlyph glyph;
				java.awt.Font labelFont;
				boolean labelAntiAlias;
				if (model.isCodePoint(i)) {
					int cp = model.getCodePoint(i);
					glyph = font.getCharacter(cp);
					label = getCharacterLabel(cp);
					if (label.length() < 4) {
						FontMapEntry e = fontMap.entryForCodePoint(cp);
						labelFont = (e != null) ? e.getFont() : g.getFont();
						labelAntiAlias = antiAlias;
					} else {
						labelFont = (Resources.HEX_FONT != null) ? Resources.HEX_FONT : g.getFont();
						labelAntiAlias = (Resources.HEX_FONT != null) ? false : antiAlias;
					}
				} else if (model.isGlyphName(i)) {
					label = model.getGlyphName(i);
					glyph = font.getNamedGlyph(label);
					labelFont = (Resources.PSNAME_FONT != null) ? Resources.PSNAME_FONT : g.getFont();
					labelAntiAlias = (Resources.PSNAME_FONT != null) ? false : antiAlias;
				} else {
					Integer marker = model.getCodePoint(i);
					if (marker != null) {
						Color selbg, bg, fg;
						String ms;
						switch (marker.intValue()) {
							case GlyphListModelList.SEQUENCE_MARKER:
								selbg = Color.gray;
								bg = Color.lightGray;
								fg = Color.black;
								label = "Sequence";
								ms = Integer.toHexString(0xFF00 | i).substring(2).toUpperCase();
								break;
							case GlyphListModelList.SUBTABLE_MARKER:
								selbg = SUBTABLE_SELBG;
								bg = SUBTABLE_BG;
								fg = Color.black;
								label = "Subtable";
								ms = Integer.toHexString(0xFF00 | i).substring(2).toUpperCase();
								break;
							case GlyphListModelList.UNDEFINED_MARKER:
								selbg = Color.black;
								bg = Color.darkGray;
								fg = Color.white;
								label = "Undefined";
								ms = Integer.toHexString(0xFF00 | i).substring(2).toUpperCase();
								break;
							default:
								int ch0 = (marker.intValue() >>> 16);
								int ch1 = (marker.intValue() & 0xFFFF);
								if (ch0 < 0x20 || ch0 > 0xFFFD || ch1 < 0x20 || ch1 > 0xFFFD) continue;
								String cs0 = Integer.toHexString(0xFF0000 | ch0).substring(2).toUpperCase();
								String cs1 = Integer.toHexString(0xFF0000 | ch1).substring(2).toUpperCase();
								selbg = Color.gray;
								bg = Color.lightGray;
								fg = Color.black;
								label = cs0 + "." + cs1;
								ms = String.valueOf(new char[]{(char)ch0, (char)ch1});
								break;
						}
						labelFont = (Resources.PSNAME_FONT != null) ? Resources.PSNAME_FONT : g.getFont();
						labelAntiAlias = (Resources.PSNAME_FONT != null) ? false : antiAlias;
						paintCellBackground(g, bg, x1, x2, y);
						paintCellLabel(g, bg, fg, label, labelFont, labelAntiAlias, x1, x2, y, lbuf);
						paintCellMarker(g, (sel.contains(i) ? selbg : bg), fg, ms, markerFont, antiAlias, x1, x2, y, gbuf);
					}
					continue;
				}
				paintCellBackground(g, Color.gray, x1, x2, y);
				paintCellLabel(g, SystemColor.text, SystemColor.textText, label, labelFont, labelAntiAlias, x1, x2, y, lbuf);
				paintCellGlyph(g, glyph, sel.contains(i), x1, x2, y, gbuf, ascent, scale);
			}
		}
	}
	
	private void paintCellBackground(Graphics g, Color c, int x1, int x2, int y) {
		g.setColor(Color.black);
		g.fillRect(x1, y, x2 - x1 + 1, cellSize + LABEL_HEIGHT + 1);
		g.setColor(c);
		g.fillRect(x1 + 1, y + 1, x2 - x1 - 1, cellSize + LABEL_HEIGHT - 1);
	}
	
	private void paintCellLabel(
		Graphics g, Color bg, Color fg, String label, java.awt.Font labelFont,
		boolean labelAntiAlias, int x1, int x2, int y, BufferedImage lbuf
	) {
		if (lbuf != null) {
			Graphics2D lg = lbuf.createGraphics();
			lg.setColor(bg);
			lg.fillRect(0, 0, lbuf.getWidth(), lbuf.getHeight());
			lg.setColor(fg);
			lg.setFont(labelFont);
			lg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, labelAntiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
			lg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, labelAntiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			FontMetrics fm = lg.getFontMetrics();
			int sw = fm.stringWidth(label);
			if (labelFont == Resources.PSNAME_FONT) sw--;
			int lx = ((x2 - x1 - 1) - sw) / 2;
			int ly = (LABEL_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
			lg.drawString(label, lx, ly);
			lg.dispose();
			g.drawImage(
				lbuf, x1 + 1, y + 1, x2, y + LABEL_HEIGHT,
				0, 0, x2 - x1 - 1, LABEL_HEIGHT - 1, null
			);
		} else {
			java.awt.Font oldFont = g.getFont();
			java.awt.Shape oldClip = g.getClip();
			g.setColor(bg);
			g.fillRect(x1 + 1, y + 1, x2 - x1 - 1, LABEL_HEIGHT - 1);
			g.clipRect(x1 + 1, y + 1, x2 - x1 - 1, LABEL_HEIGHT - 1);
			g.setColor(fg);
			g.setFont(labelFont);
			if (g instanceof Graphics2D) {
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, labelAntiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, labelAntiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}
			FontMetrics fm = g.getFontMetrics();
			int sw = fm.stringWidth(label);
			if (labelFont == Resources.PSNAME_FONT) sw--;
			int lx = ((x2 - x1 - 1) - sw) / 2;
			int ly = (LABEL_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
			g.drawString(label, x1 + 1 + lx, y + 1 + ly);
			g.setFont(oldFont);
			g.setClip(oldClip);
		}
	}
	
	private void paintCellGlyph(
		Graphics g, FontGlyph glyph, boolean sel, int x1, int x2,
		int y, BufferedImage gbuf, double ascent, double scale
	) {
		if (glyph == null) {
			g.setColor(sel ? SystemColor.textHighlight : SystemColor.text);
			g.fillRect(x1 + 1, y + LABEL_HEIGHT + 1, x2 - x1 - 1, cellSize - 1);
			g.setColor(Color.gray);
			g.drawLine(x1 + 1, y + LABEL_HEIGHT + 1, x2 - 1, y + cellSize + LABEL_HEIGHT - 1);
			g.drawLine(x2 - 1, y + LABEL_HEIGHT + 1, x1 + 1, y + cellSize + LABEL_HEIGHT - 1);
		} else if (gbuf != null) {
			Graphics2D gg = gbuf.createGraphics();
			gg.setColor(sel ? SystemColor.textHighlight : SystemColor.text);
			gg.fillRect(0, 0, gbuf.getWidth(), gbuf.getHeight());
			gg.setColor(sel ? SystemColor.textHighlightText : SystemColor.textText);
			double gx = Math.round(((x2 - x1 - 2) - glyph.getCharacterWidth2D() * scale) / 2);
			glyph.paint(gg, gx, ascent, scale);
			gg.dispose();
			g.drawImage(
				gbuf, x1 + 1, y + LABEL_HEIGHT + 1,
				x2, y + LABEL_HEIGHT + cellSize,
				0, 0, x2 - x1 - 1, cellSize - 1, null
			);
		} else {
			java.awt.Shape oldClip = g.getClip();
			g.setColor(sel ? SystemColor.textHighlight : SystemColor.text);
			g.fillRect(x1 + 1, y + LABEL_HEIGHT + 1, x2 - x1 - 1, cellSize - 1);
			g.clipRect(x1 + 1, y + LABEL_HEIGHT + 1, x2 - x1 - 1, cellSize - 1);
			g.setColor(sel ? SystemColor.textHighlightText : SystemColor.textText);
			double gx = Math.round(((x2 - x1 - 2) - glyph.getCharacterWidth2D() * scale) / 2);
			glyph.paint(g, x1 + 1 + gx, y + LABEL_HEIGHT + 1 + ascent, scale);
			g.setClip(oldClip);
		}
	}
	
	private void paintCellMarker(
		Graphics g, Color bg, Color fg, String label, java.awt.Font labelFont,
		boolean labelAntiAlias, int x1, int x2, int y, BufferedImage gbuf
	) {
		if (gbuf != null) {
			Graphics2D gg = gbuf.createGraphics();
			gg.setColor(bg);
			gg.fillRect(0, 0, gbuf.getWidth(), gbuf.getHeight());
			gg.setColor(fg);
			gg.setFont(labelFont);
			gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, labelAntiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
			gg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, labelAntiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			FontMetrics fm = gg.getFontMetrics();
			int gx = ((x2 - x1 - 1) - fm.stringWidth(label)) / 2;
			int gy = ((cellSize - 3) - fm.getHeight()) / 2 + fm.getAscent();
			gg.drawString(label, gx, gy);
			g.drawImage(
				gbuf, x1 + 1, y + LABEL_HEIGHT + 1,
				x2, y + LABEL_HEIGHT + cellSize,
				0, 0, x2 - x1 - 1, cellSize - 1, null
			);
		} else {
			java.awt.Font oldFont = g.getFont();
			java.awt.Shape oldClip = g.getClip();
			g.setColor(bg);
			g.fillRect(x1 + 1, y + LABEL_HEIGHT + 1, x2 - x1 - 1, cellSize - 1);
			g.clipRect(x1 + 1, y + LABEL_HEIGHT + 1, x2 - x1 - 1, cellSize - 1);
			g.setColor(fg);
			g.setFont(labelFont);
			if (g instanceof Graphics2D) {
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, labelAntiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, labelAntiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}
			FontMetrics fm = g.getFontMetrics();
			int gx = ((x2 - x1 - 1) - fm.stringWidth(label)) / 2;
			int gy = ((cellSize - 3) - fm.getHeight()) / 2 + fm.getAscent();
			g.drawString(label, x1 + 1 + gx, y + LABEL_HEIGHT + 1 + gy);
			g.setFont(oldFont);
			g.setClip(oldClip);
		}
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		if (preferredSize != null) {
			return preferredSize;
		} else {
			int w = cellSize * columnCount + 1;
			int h = (cellSize + LABEL_HEIGHT) * rowCount + 1;
			Insets i = getInsets();
			return new Dimension(w + i.left + i.right, h + i.top + i.bottom);
		}
	}
	
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}
	
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	public int getScrollableUnitIncrement(Rectangle vr, int or, int dir) {
		return cellSize + LABEL_HEIGHT;
	}
	
	public int getScrollableBlockIncrement(Rectangle vr, int or, int dir) {
		return (vr.height / (cellSize + LABEL_HEIGHT)) * (cellSize + LABEL_HEIGHT);
	}
	
	private String getCharacterLabel(int cp) {
		String c = NameResolver.instance(cp).getCategory(cp);
		if ("Zs Zl Zp Cc Cf Cs Cn".contains(c)) {
			String h = Integer.toHexString(cp).toUpperCase();
			while (h.length() < 4) h = "0" + h;
			return h;
		} else {
			return String.valueOf(Character.toChars(cp));
		}
	}
	
	private Rectangle getSelectionRect() {
		SortedSet<Integer> sel = selection.toSet();
		if (sel.isEmpty()) return null;
		Insets insets = getInsets();
		int w = getWidth() - insets.left - insets.right - 1;
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		for (int i : sel) {
			int x1 = insets.left + w * (i % columnCount) / columnCount;
			int x2 = insets.left + w * ((i % columnCount) + 1) / columnCount;
			int y1 = insets.top + (cellSize + LABEL_HEIGHT) * (i / columnCount);
			int y2 = y1 + cellSize + LABEL_HEIGHT;
			if (x1 < minX) minX = x1;
			if (y1 < minY) minY = y1;
			if (x2 > maxX) maxX = x2;
			if (y2 > maxY) maxY = y2;
		}
		return new Rectangle(minX, minY, maxX-minX+1, maxY-minY+1);
	}
	
	private Rectangle getLastSelectionRect() {
		int i = selection.getLast();
		if (i < 0) return null;
		Insets insets = getInsets();
		int w = getWidth() - insets.left - insets.right - 1;
		int x1 = insets.left + w * (i % columnCount) / columnCount;
		int x2 = insets.left + w * ((i % columnCount) + 1) / columnCount;
		int y1 = insets.top + (cellSize + LABEL_HEIGHT) * (i / columnCount);
		int y2 = y1 + cellSize + LABEL_HEIGHT;
		return new Rectangle(x1, y1, x2-x1+1, y2-y1+1);
	}
	
	private void startSelection(InputEvent e, int i) {
		if (e.isShiftDown()) {
			selection.extend(i);
		} else if (e.isControlDown() || e.isMetaDown()) {
			selection.add(i);
		} else {
			selection.clear();
			selection.add(i);
		}
		Rectangle r = getLastSelectionRect();
		if (r != null) scrollRectToVisible(r);
		for (GlyphListListener<G> l : listeners) l.selectionChanged(GlyphList.this, font);
		repaint();
	}
	
	private void continueSelection(InputEvent e, int i) {
		selection.extend(i);
		Rectangle r = getLastSelectionRect();
		if (r != null) scrollRectToVisible(r);
		for (GlyphListListener<G> l : listeners) l.selectionChanged(GlyphList.this, font);
		repaint();
	}
	
	private class MyMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			requestFocusInWindow();
			int i = getClickedIndex(e);
			startSelection(e, i);
		}
		public void mouseDragged(MouseEvent e) {
			int i = getClickedIndex(e);
			continueSelection(e, i);
		}
		public void mouseReleased(MouseEvent e) {
			int i = getClickedIndex(e);
			continueSelection(e, i);
		}
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) openSelection();
		}
		private int getClickedIndex(MouseEvent e) {
			Insets insets = getInsets();
			int w = getWidth() - insets.left - insets.right;
			int x = (e.getX() - insets.left) * columnCount / w;
			if (x < 0) x = 0;
			if (x >= columnCount) x = columnCount - 1;
			int y = (e.getY() - insets.top) / (cellSize + LABEL_HEIGHT);
			int i = y * columnCount + x;
			if (i < 0) i = 0;
			if (i >= model.getCellCount()) i = model.getCellCount() - 1;
			return i;
		}
	}
	
	private class MyKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if ((e.isMetaDown() || e.isControlDown()) && e.isShiftDown()) return;
			switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
				case KeyEvent.VK_CLEAR:
					clearSelection();
					break;
				case KeyEvent.VK_LEFT:
					if (!selection.isEmpty()) {
						int i = selection.getLast() - 1;
						if (i < 0) i = 0;
						if (i >= model.getCellCount()) i = model.getCellCount() - 1;
						startSelection(e, i);
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (!selection.isEmpty()) {
						int i = selection.getLast() + 1;
						if (i < 0) i = 0;
						if (i >= model.getCellCount()) i = model.getCellCount() - 1;
						startSelection(e, i);
					}
					break;
				case KeyEvent.VK_UP:
					if (!selection.isEmpty()) {
						int i = selection.getLast() - columnCount;
						if (i < 0) i = 0;
						if (i >= model.getCellCount()) i = model.getCellCount() - 1;
						startSelection(e, i);
					}
					break;
				case KeyEvent.VK_DOWN:
					if (!selection.isEmpty()) {
						int i = selection.getLast() + columnCount;
						if (i < 0) i = 0;
						if (i >= model.getCellCount()) i = model.getCellCount() - 1;
						startSelection(e, i);
					}
					break;
				case KeyEvent.VK_HOME:
					startSelection(e, 0);
					break;
				case KeyEvent.VK_END:
					startSelection(e, model.getCellCount() - 1);
					break;
				case KeyEvent.VK_ENTER:
					openSelection();
					break;
				case KeyEvent.VK_DELETE:
					deleteSelection();
					break;
				case KeyEvent.VK_INSERT:
					setAntiAlias(!antiAlias);
					break;
			}
		}
		public void keyTyped(KeyEvent e) {
			if (e.isMetaDown() || e.isControlDown()) return;
			int cp = e.getKeyChar();
			if ((cp >= 32 && cp < 127) || cp >= 160) {
				int i = model.indexOfCodePoint(cp);
				if (i >= 0) {
					selection.clear();
					selection.add(i);
					Rectangle r = getLastSelectionRect();
					if (r != null) scrollRectToVisible(r);
					for (GlyphListListener<G> l : listeners) l.selectionChanged(GlyphList.this, font);
					repaint();
				}
			}
		}
	}
	
	private class MyComponentListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			setBestColumnCount();
		}
	}
}
