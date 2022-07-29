package com.kreative.bitsnpicas.edit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
	
	private final FontMapController fontMap;
	private final Font<G> font;
	private int cellSize;
	private int columnCount;
	private int rowCount;
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
		double fa = font.getEmAscent2D();
		double fh = fa + font.getEmDescent2D();
		double scale = (fh <= 0) ? ((cellSize - 3) / 10.0) : ((cellSize - 3) / fh);
		if (scale <= 0) scale = 1;
		if (scale >= 1) scale = Math.floor(scale);
		double ascent = ((cellSize - 3) - fh * scale) / 2 + fa * scale;
		for (int i = 0, n = model.getCellCount(); i < n; i++) {
			int x1 = insets.left + w * (i % columnCount) / columnCount;
			int x2 = insets.left + w * ((i % columnCount) + 1) / columnCount;
			int y = insets.top + (cellSize + LABEL_HEIGHT) * (i / columnCount);
			if (vr.intersects(x1, y, x2 - x1 + 1, cellSize + LABEL_HEIGHT + 1)) {
				if (model.isCodePoint(i) || model.isGlyphName(i)) {
					g.setColor(Color.black);
					g.fillRect(x1, y, x2 - x1 + 1, cellSize + LABEL_HEIGHT + 1);
					g.setColor(Color.gray);
					g.fillRect(x1 + 1, y + 1, x2 - x1 - 1, cellSize + LABEL_HEIGHT - 1);
					g.setColor(SystemColor.text);
					g.fillRect(x1 + 1, y + 1, x2 - x1 - 1, LABEL_HEIGHT - 1);
					if (sel.contains(i)) g.setColor(SystemColor.textHighlight);
					g.fillRect(x1 + 1, y + LABEL_HEIGHT + 1, x2 - x1 - 1, cellSize - 1);
					g.setColor(SystemColor.textText);
				}
				if (model.isCodePoint(i)) {
					int cp = model.getCodePoint(i);
					String cps = getCharacterLabel(cp);
					if (Resources.HEX_FONT == null || cps.length() < 4) {
						FontMapEntry e = fontMap.entryForCodePoint(cp);
						if (e == null) {
							FontMetrics fm = g.getFontMetrics();
							int cpsx = (x1 + x2 - fm.stringWidth(cps) + 1) / 2;
							int cpsy = y + (LABEL_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
							g.drawString(cps, cpsx, cpsy);
						} else {
							java.awt.Font sf = g.getFont();
							g.setFont(e.getFont());
							FontMetrics fm = g.getFontMetrics();
							int cpsx = (x1 + x2 - fm.stringWidth(cps) + 1) / 2;
							int cpsy = y + (LABEL_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
							g.drawString(cps, cpsx, cpsy);
							g.setFont(sf);
						}
					} else {
						java.awt.Font sf = g.getFont();
						g.setFont(Resources.HEX_FONT);
						int cpsx = (x1 + x2 - cps.length() * 5 + 1) / 2;
						int cpsy = y + (LABEL_HEIGHT - 8) / 2 + 8;
						g.drawString(cps, cpsx, cpsy);
						g.setFont(sf);
					}
				} else if (model.isGlyphName(i)) {
					String name = model.getGlyphName(i);
					if (Resources.PSNAME_FONT == null) {
						FontMetrics fm = g.getFontMetrics();
						int nx = (x1 + x2 - fm.stringWidth(name) + 1) / 2;
						int ny = y + (LABEL_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
						g.drawString(name, nx, ny);
					} else {
						java.awt.Font sf = g.getFont();
						g.setFont(Resources.PSNAME_FONT);
						FontMetrics fm = g.getFontMetrics();
						int nx = (x1 + x2 - fm.stringWidth(name) + 1) / 2;
						int ny = y + (LABEL_HEIGHT - 8) / 2 + 8;
						g.drawString(name, nx, ny);
						g.setFont(sf);
					}
				}
				if (model.isCodePoint(i) && font.containsCharacter(model.getCodePoint(i))) {
					if (sel.contains(i)) g.setColor(SystemColor.textHighlightText);
					FontGlyph glyph = font.getCharacter(model.getCodePoint(i));
					double gx = Math.round((x1 + x2 - glyph.getCharacterWidth2D() * scale) / 2);
					double gy = Math.round(y + LABEL_HEIGHT + 2 + ascent);
					glyph.paint(g, gx, gy, scale);
				} else if (model.isGlyphName(i) && font.containsNamedGlyph(model.getGlyphName(i))) {
					if (sel.contains(i)) g.setColor(SystemColor.textHighlightText);
					FontGlyph glyph = font.getNamedGlyph(model.getGlyphName(i));
					double gx = Math.round((x1 + x2 - glyph.getCharacterWidth2D() * scale) / 2);
					double gy = Math.round(y + LABEL_HEIGHT + 2 + ascent);
					glyph.paint(g, gx, gy, scale);
				} else if (model.isCodePoint(i) || model.isGlyphName(i)) {
					g.setColor(Color.gray);
					g.drawLine(x1 + 1, y + LABEL_HEIGHT + 1, x2 - 1, y + cellSize + LABEL_HEIGHT - 1);
					g.drawLine(x2 - 1, y + LABEL_HEIGHT + 1, x1 + 1, y + cellSize + LABEL_HEIGHT - 1);
				}
			}
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
				case KeyEvent.VK_ENTER:
					openSelection();
					break;
				case KeyEvent.VK_DELETE:
					deleteSelection();
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
