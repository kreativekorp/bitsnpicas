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
import com.kreative.bitsnpicas.unicode.Block;
import com.kreative.bitsnpicas.unicode.CharacterDatabase;

public class GlyphList extends JComponent implements Scrollable {
	private static final long serialVersionUID = 1L;
	
	private static final int LABEL_HEIGHT = 18;
	
	private static final java.awt.Font HEX_FONT;
	static {
		java.awt.Font hexFont;
		try {
			hexFont = java.awt.Font.createFont(
				java.awt.Font.TRUETYPE_FONT,
				GlyphList.class.getResourceAsStream("Hex.ttf")
			).deriveFont(10f);
		} catch (Exception e) {
			hexFont = null;
		}
		HEX_FONT = hexFont;
	}
	
	private final CharacterDatabase cdb;
	private final Font<?> font;
	private int cellSize;
	private int columnCount;
	private List<Integer> codePoints;
	private final GlyphListSelection selection;
	private Dimension preferredSize;
	private final List<GlyphListListener> listeners;
	
	public GlyphList(Font<?> font) {
		this.cdb = CharacterDatabase.instance();
		this.font = font;
		this.cellSize = 36;
		this.columnCount = 16;
		this.codePoints = new Block(0, 255, "Latin-1");
		this.selection = new GlyphListSelection();
		this.preferredSize = null;
		this.listeners = new ArrayList<GlyphListListener>();
		MyMouseListener ml = new MyMouseListener();
		this.addMouseListener(ml);
		this.addMouseMotionListener(ml);
		MyKeyListener kl = new MyKeyListener();
		this.addKeyListener(kl);
		this.setFocusable(true);
		MyComponentListener cl = new MyComponentListener();
		this.addComponentListener(cl);
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
	
	public List<Integer> getCodePointList() {
		return this.codePoints;
	}
	
	public void setCodePointList(List<Integer> codePoints) {
		if (this.codePoints != codePoints) {
			this.codePoints = codePoints;
			this.selection.clear();
			for (GlyphListListener l : listeners) l.codePointsSelected(this, font);
			this.revalidate();
			this.repaint();
		}
	}
	
	public void clearSelection() {
		selection.clear();
		for (GlyphListListener l : listeners) l.codePointsSelected(this, font);
		repaint();
	}
	
	public void selectAll() {
		selection.clear();
		selection.add(0, codePoints.size() - 1);
		for (GlyphListListener l : listeners) l.codePointsSelected(this, font);
		repaint();
	}
	
	public SortedSet<Integer> getSelectedIndices() {
		return selection.toSet();
	}
	
	public void setSelectedIndices(Collection<Integer> c) {
		selection.clear();
		for (int i : c) if (i >= 0 && i < codePoints.size()) selection.add(i);
		for (GlyphListListener l : listeners) l.codePointsSelected(this, font);
		repaint();
	}
	
	public List<Integer> getSelectedCodePoints() {
		List<Integer> cps = new ArrayList<Integer>();
		for (int i : selection.toSet()) {
			int cp = codePoints.get(i);
			if (cp >= 0) cps.add(cp);
		}
		return cps;
	}
	
	public void setSelectedCodePoints(Collection<Integer> c) {
		selection.clear();
		for (int cp : c) {
			int i = codePoints.indexOf(cp);
			if (i >= 0) selection.add(i);
		}
		for (GlyphListListener l : listeners) l.codePointsSelected(this, font);
		repaint();
	}
	
	public Dimension getPreferredSize() {
		if (preferredSize != null) {
			return preferredSize;
		} else {
			int rows = (codePoints.size() + columnCount - 1) / columnCount;
			int w = cellSize * columnCount + 1;
			int h = (cellSize + LABEL_HEIGHT) * rows + 1;
			Insets i = getInsets();
			return new Dimension(w + i.left + i.right, h + i.top + i.bottom);
		}
	}
	
	public void setPreferredSize(Dimension d) {
		this.preferredSize = d;
	}
	
	public void addGlyphListListener(GlyphListListener l) {
		this.listeners.add(l);
	}
	
	public void removeGlyphListListener(GlyphListListener l) {
		this.listeners.remove(l);
	}
	
	public void metricsChanged() {
		for (GlyphListListener l : listeners) {
			l.metricsChanged(this, font);
		}
		repaint();
	}
	
	public void glyphsChanged() {
		for (GlyphListListener l : listeners) {
			l.glyphsChanged(this, font);
		}
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
		for (int i = 0, n = codePoints.size(); i < n; i++) {
			int x1 = insets.left + w * (i % columnCount) / columnCount;
			int x2 = insets.left + w * ((i % columnCount) + 1) / columnCount;
			int y = insets.top + (cellSize + LABEL_HEIGHT) * (i / columnCount);
			if (vr.intersects(x1, y, x2 - x1 + 1, cellSize + LABEL_HEIGHT + 1)) {
				int cp = codePoints.get(i);
				if (cp < 0) continue;
				g.setColor(Color.black);
				g.fillRect(x1, y, x2 - x1 + 1, cellSize + LABEL_HEIGHT + 1);
				g.setColor(Color.gray);
				g.fillRect(x1 + 1, y + 1, x2 - x1 - 1, cellSize + LABEL_HEIGHT - 1);
				g.setColor(SystemColor.text);
				g.fillRect(x1 + 1, y + 1, x2 - x1 - 1, LABEL_HEIGHT - 1);
				if (sel.contains(i)) g.setColor(SystemColor.textHighlight);
				g.fillRect(x1 + 1, y + LABEL_HEIGHT + 1, x2 - x1 - 1, cellSize - 1);
				g.setColor(SystemColor.textText);
				String cps = getCharacterLabel(cp);
				if (HEX_FONT == null || cps.length() < 4) {
					FontMetrics fm = g.getFontMetrics();
					int cpsx = (x1 + x2 - fm.stringWidth(cps) + 1) / 2;
					int cpsy = y + (LABEL_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
					g.drawString(cps, cpsx, cpsy);
				} else {
					java.awt.Font sf = g.getFont();
					g.setFont(HEX_FONT);
					int cpsx = (x1 + x2 - cps.length() * 5 + 1) / 2;
					int cpsy = y + (LABEL_HEIGHT - 8) / 2 + 8;
					g.drawString(cps, cpsx, cpsy);
					g.setFont(sf);
				}
				if (font.containsCharacter(cp)) {
					if (sel.contains(i)) g.setColor(SystemColor.textHighlightText);
					FontGlyph glyph = font.getCharacter(cp);
					double gx = Math.round((x1 + x2 - glyph.getCharacterWidth2D() * scale) / 2);
					double gy = Math.round(y + LABEL_HEIGHT + 2 + ascent);
					glyph.paint(g, gx, gy, scale);
				} else {
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
			int rows = (codePoints.size() + columnCount - 1) / columnCount;
			if (rows > 8) rows = 8;
			int w = cellSize * columnCount + 1;
			int h = (cellSize + LABEL_HEIGHT) * rows + 1;
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
		if (cdb.containsKey(cp)) {
			String c = cdb.get(cp).category;
			if (!(
				c.equals("Zs") || c.equals("Zl") || c.equals("Zp") ||
				c.equals("Cc") || c.equals("Cf") || c.equals("Cs") ||
				c.equals("Cn")
			)) {
				return String.valueOf(Character.toChars(cp));
			}
		}
		String h = Integer.toHexString(cp).toUpperCase();
		while (h.length() < 4) h = "0" + h;
		return h;
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
		for (GlyphListListener l : listeners) l.codePointsSelected(GlyphList.this, font);
		repaint();
	}
	
	private void continueSelection(InputEvent e, int i) {
		selection.extend(i);
		for (GlyphListListener l : listeners) l.codePointsSelected(GlyphList.this, font);
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
			if (e.getClickCount() > 1 && !selection.isEmpty()) {
				for (GlyphListListener l : listeners) {
					l.codePointsOpened(GlyphList.this, font);
				}
			}
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
			if (i >= codePoints.size()) i = codePoints.size() - 1;
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
						if (i >= codePoints.size()) i = codePoints.size() - 1;
						startSelection(e, i);
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (!selection.isEmpty()) {
						int i = selection.getLast() + 1;
						if (i < 0) i = 0;
						if (i >= codePoints.size()) i = codePoints.size() - 1;
						startSelection(e, i);
					}
					break;
				case KeyEvent.VK_UP:
					if (!selection.isEmpty()) {
						int i = selection.getLast() - columnCount;
						if (i < 0) i = 0;
						if (i >= codePoints.size()) i = codePoints.size() - 1;
						startSelection(e, i);
					}
					break;
				case KeyEvent.VK_DOWN:
					if (!selection.isEmpty()) {
						int i = selection.getLast() + columnCount;
						if (i < 0) i = 0;
						if (i >= codePoints.size()) i = codePoints.size() - 1;
						startSelection(e, i);
					}
					break;
				case KeyEvent.VK_ENTER:
					if (!selection.isEmpty()) {
						for (GlyphListListener l : listeners) {
							l.codePointsOpened(GlyphList.this, font);
						}
					}
					break;
				case KeyEvent.VK_DELETE:
					for (int cp : getSelectedCodePoints()) {
						font.removeCharacter(cp);
					}
					glyphsChanged();
					break;
			}
		}
		public void keyTyped(KeyEvent e) {
			if (e.isMetaDown() || e.isControlDown()) return;
			int cp = e.getKeyChar();
			if ((cp >= 32 && cp < 127) || cp >= 160) {
				int i = codePoints.indexOf(cp);
				if (i >= 0) {
					selection.clear();
					selection.add(i);
					for (GlyphListListener l : listeners) l.codePointsSelected(GlyphList.this, font);
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
