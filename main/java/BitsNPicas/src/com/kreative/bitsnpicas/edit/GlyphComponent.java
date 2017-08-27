package com.kreative.bitsnpicas.edit;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public class GlyphComponent extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private static final int MARGIN_WIDTH = 60;
	private static final int MARGIN_HEIGHT = 24;
	
	private static final Color GRID_COLOR = new Color(0xFFCCCCCC);
	private static final Color X_HEIGHT_COLOR = new Color(0xFFFF9900);
	private static final Color LINE_GAP_COLOR = new Color(0xFFFFCC00);
	private static final Color LINE_ASCENT_COLOR = new Color(0xFFFF00CC);
	private static final Color LINE_DESCENT_COLOR = new Color(0xFF00CCFF);
	private static final Color EM_ASCENT_COLOR = Color.red;
	private static final Color EM_DESCENT_COLOR = Color.blue;
	private static final Color ADVANCE_COLOR = new Color(0xFF00CC00);
	private static final Color BOUNDS_COLOR = new Color(0xFF9900FF);
	
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
	
	private final Font<?> font;
	private final FontGlyph glyph;
	private double scale;
	private double tx;
	private double ty;
	private boolean fit;
	private boolean showGlyphBounds;
	private Dimension minimumSize;
	private Dimension preferredSize;
	private final List<GlyphComponentListener> listeners;
	
	public GlyphComponent(Font<?> font, FontGlyph glyph) {
		this.font = font;
		this.glyph = glyph;
		this.fit = true;
		this.showGlyphBounds = false;
		this.listeners = new ArrayList<GlyphComponentListener>();
		MyMouseListener ml = new MyMouseListener();
		this.addMouseListener(ml);
		this.addMouseMotionListener(ml);
		this.addMouseWheelListener(ml);
		MyComponentListener cl = new MyComponentListener();
		this.addComponentListener(cl);
	}
	
	public FontGlyph getGlyph() {
		return this.glyph;
	}
	
	public double getScale() {
		return this.scale;
	}
	
	public void setScale(double scale) {
		if (this.scale != scale && scale > 0) {
			this.scale = scale;
			this.repaint();
		}
	}
	
	public double getTranslateX() {
		return this.tx;
	}
	
	public double getTranslateY() {
		return this.ty;
	}
	
	public void setTranslate(double tx, double ty) {
		if (this.tx != tx || this.ty != ty) {
			this.tx = tx;
			this.ty = ty;
			this.repaint();
		}
	}
	
	public void setBestScaleAndTranslate() {
		Insets insets = getInsets();
		int w = getWidth() - insets.left - insets.right;
		int h = getHeight() - insets.top - insets.bottom;
		int iw = w - MARGIN_WIDTH * 2;
		int ih = h - MARGIN_HEIGHT * 2;
		double fa = font.getEmAscent2D();
		double fh = fa + font.getEmDescent2D();
		double ns = (fh <= 0) ? (ih / 20.0) : (ih / fh / 2.0);
		if (ns <= 0) ns = 1;
		if (ns >= 1) ns = Math.floor(ns);
		double nx = Math.round((iw - glyph.getCharacterWidth2D() * ns) / 2);
		double ny = Math.round((ih - fh * ns) / 2 + fa * ns);
		if (this.scale != ns || this.tx != nx || this.ty != ny) {
			this.scale = ns;
			this.tx = nx;
			this.ty = ny;
			this.repaint();
		}
	}
	
	public boolean getFit() {
		return this.fit;
	}
	
	public void setFit(boolean fit) {
		this.fit = fit;
		if (fit) setBestScaleAndTranslate();
	}
	
	public boolean getShowGlyphBounds() {
		return this.showGlyphBounds;
	}
	
	public void setShowGlyphBounds(boolean showGlyphBounds) {
		this.showGlyphBounds = showGlyphBounds;
		repaint();
	}
	
	public Dimension getMinimumSize() {
		if (minimumSize != null) {
			return minimumSize;
		} else {
			Insets i = getInsets();
			int w = 20 + MARGIN_WIDTH * 2 + i.left + i.right;
			int h = 20 + MARGIN_HEIGHT * 2 + i.top + i.bottom;
			return new Dimension(w, h);
		}
	}
	
	public void setMinimumSize(Dimension d) {
		this.minimumSize = d;
	}
	
	public Dimension getPreferredSize() {
		if (preferredSize != null) {
			return preferredSize;
		} else {
			Insets i = getInsets();
			int w = 400 + MARGIN_WIDTH * 2 + i.left + i.right;
			int h = 400 + MARGIN_HEIGHT * 2 + i.top + i.bottom;
			return new Dimension(w, h);
		}
	}
	
	public void setPreferredSize(Dimension d) {
		this.preferredSize = d;
	}
	
	public void addGlyphComponentListener(GlyphComponentListener l) {
		this.listeners.add(l);
	}
	
	public void removeGlyphComponentListener(GlyphComponentListener l) {
		this.listeners.remove(l);
	}
	
	private void metricsChanged() {
		for (GlyphComponentListener l : listeners) {
			l.metricsChanged(glyph, font);
		}
		repaint();
	}
	
	public void glyphChanged() {
		for (GlyphComponentListener l : listeners) {
			l.glyphChanged(glyph, font);
		}
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
		if (scale <= 0) setBestScaleAndTranslate();
		Insets insets = getInsets();
		int x = insets.left;
		int y = insets.top;
		int w = getWidth() - insets.left - insets.right;
		int h = getHeight() - insets.top - insets.bottom;
		int ix = x + MARGIN_WIDTH;
		int iy = y + MARGIN_HEIGHT;
		int iw = w - MARGIN_WIDTH * 2;
		int ih = h - MARGIN_HEIGHT * 2;
		int origin = ix + (int)Math.round(tx);
		int baseline = iy + (int)Math.round(ty);
		int emAscent = iy + (int)Math.round(ty - font.getEmAscent2D() * scale);
		int emDescent = iy + (int)Math.round(ty + font.getEmDescent2D() * scale);
		int lineAscent = iy + (int)Math.round(ty - font.getLineAscent2D() * scale);
		int lineDescent = iy + (int)Math.round(ty + font.getLineDescent2D() * scale);
		int lineGap = iy + (int)Math.round(ty + (font.getLineDescent2D() + font.getLineGap2D()) * scale);
		int xHeight = iy + (int)Math.round(ty - font.getXHeight2D() * scale);
		int advance = ix + (int)Math.round(tx + glyph.getCharacterWidth2D() * scale);
		
		g.setColor(Color.black);
		g.fillRect(ix - 1, iy - 1, iw + 2, ih + 2);
		
		Shape saveClip = g.getClip();
		g.clipRect(ix, iy, iw, ih);
		g.setColor(Color.white);
		g.fillRect(ix, iy, iw, ih);
		g.setColor(Color.black);
		glyph.paint(g, ix + tx, iy + ty, scale);
		if (scale >= 5) {
			g.setColor(GRID_COLOR);
			for (int i = 1; ty + i * scale <= ih; i++) {
				int ly = iy + (int)Math.round(ty + i * scale);
				g.drawLine(ix, ly, ix + iw, ly);
			}
			for (int i = 1; ty - i * scale >= 0; i++) {
				int ly = iy + (int)Math.round(ty - i * scale);
				g.drawLine(ix, ly, ix + iw, ly);
			}
			for (int i = 1; tx + i * scale <= iw; i++) {
				int lx = ix + (int)Math.round(tx + i * scale);
				g.drawLine(lx, iy, lx, iy + ih);
			}
			for (int i = 1; tx - i * scale >= 0; i++) {
				int lx = ix + (int)Math.round(tx - i * scale);
				g.drawLine(lx, iy, lx, iy + ih);
			}
		}
		g.setColor(Color.black);
		g.drawLine(ix, baseline, ix + iw, baseline);
		g.drawLine(origin, iy, origin, iy + ih);
		g.setColor(X_HEIGHT_COLOR);
		g.drawLine(ix, xHeight, ix + iw, xHeight);
		g.setColor(LINE_GAP_COLOR);
		g.drawLine(ix, lineGap, ix + iw, lineGap);
		g.setColor(LINE_ASCENT_COLOR);
		g.drawLine(ix, lineAscent, ix + iw, lineAscent);
		g.setColor(LINE_DESCENT_COLOR);
		g.drawLine(ix, lineDescent, ix + iw, lineDescent);
		g.setColor(EM_ASCENT_COLOR);
		g.drawLine(ix, emAscent, ix + iw, emAscent);
		g.setColor(EM_DESCENT_COLOR);
		g.drawLine(ix, emDescent, ix + iw, emDescent);
		g.setColor(ADVANCE_COLOR);
		g.drawLine(advance, iy, advance, iy + ih);
		if (showGlyphBounds) {
			g.setColor(BOUNDS_COLOR);
			int bx1 = ix + (int)Math.round(tx + glyph.getGlyphOffset2D() * scale);
			int by1 = iy + (int)Math.round(ty - glyph.getGlyphAscent2D() * scale);
			int bx2 = ix + (int)Math.round(tx + (glyph.getGlyphOffset2D() + glyph.getGlyphWidth2D()) * scale);
			int by2 = iy + (int)Math.round(ty + (-glyph.getGlyphAscent2D() + glyph.getGlyphHeight2D()) * scale);
			g.drawRect(bx1, by1, bx2 - bx1, by2 - by1);
		}
		g.setClip(saveClip);
		
		java.awt.Font saveFont = g.getFont();
		g.setColor(Color.black);
		if (HEX_FONT != null) g.setFont(HEX_FONT);
		FontMetrics fm = g.getFontMetrics();
		if (emAscent >= iy && emAscent < iy + ih) {
			String mas = toString(font.getEmAscent2D(), true) + " >";
			g.drawString(mas, ix - fm.stringWidth(mas) - 1, emAscent + 4);
		}
		if (emDescent >= iy && emDescent < iy + ih) {
			String mds = toString(-font.getEmDescent2D(), true) + " >";
			g.drawString(mds, ix - fm.stringWidth(mds) - 1, emDescent + 4);
		}
		if (xHeight >= iy && xHeight < iy + ih && xHeight != emAscent && xHeight != emDescent) {
			String xhs = toString(font.getXHeight2D(), false) + " >";
			g.drawString(xhs, ix - fm.stringWidth(xhs) - 1, xHeight + 4);
		}
		if (lineAscent >= iy && lineAscent < iy + ih) {
			String las = "< " + toString(font.getLineAscent2D(), true);
			g.drawString(las, ix + iw + 2, lineAscent + 4);
		}
		if (lineDescent >= iy && lineDescent < iy + ih) {
			String lds = "< " + toString(-font.getLineDescent2D(), true);
			g.drawString(lds, ix + iw + 2, lineDescent + 4);
		}
		if (lineGap >= iy && lineGap < iy + ih && lineGap != lineAscent && lineGap != lineDescent) {
			String lgs = "< " + toString(font.getLineGap2D(), false);
			g.drawString(lgs, ix + iw + 2, lineGap + 4);
		}
		if (advance >= ix && advance < ix + iw) {
			String aws = toString(glyph.getCharacterWidth2D(), false);
			g.drawString(aws, advance - fm.stringWidth(aws) / 2, iy - 11);
			g.drawString("~", advance - 4, iy);
			g.drawString("^", advance - 4, iy + ih + 7);
			g.drawString(aws, advance - fm.stringWidth(aws) / 2, iy + ih + 18);
		}
		g.setFont(saveFont);
	}
	
	private String toString(double d, boolean signed) {
		String s = (d == (int)d) ? Integer.toString((int)d) : Double.toString(d);
		return (d > 0 && signed) ? ("+" + s) : s;
	}
	
	private class MyComponentListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			if (fit) setBestScaleAndTranslate();
		}
	}
	
	private class MyMouseListener extends MouseAdapter {
		private MouseRegion inRegion = MouseRegion.NONE;
		private int baseX, baseY;
		private double baseValue;
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.isControlDown()) {
				MouseRegion region = getRegion(e);
				Point2D p = (region == MouseRegion.INTERIOR) ? getInteriorPoint(e) : null;
				boolean changed = false;
				for (GlyphComponentListener l : listeners) {
					if (l.mouseWheelMoved(e, p, glyph, font)) {
						changed = true;
					}
				}
				if (changed) glyphChanged();
			} else if (e.isAltDown()) {
				if (e.getWheelRotation() < 0) {
					scale *= 1.5;
					if (scale <= 0) scale = 1;
					if (scale >= 1) scale = Math.ceil(scale);
					repaint();
				}
				if (e.getWheelRotation() > 0) {
					scale /= 1.5;
					if (scale <= 0) scale = 1;
					if (scale >= 1) scale = Math.floor(scale);
					repaint();
				}
			} else if (e.isShiftDown()) {
				tx -= e.getWheelRotation();
				repaint();
			} else {
				ty -= e.getWheelRotation();
				repaint();
			}
		}
		public void mouseMoved(MouseEvent e) {
			if (inRegion == MouseRegion.NONE) {
				MouseRegion region = getRegion(e);
				Point2D p = (region == MouseRegion.INTERIOR) ? getInteriorPoint(e) : null;
				switch (region) {
					case EM_ASCENT:
						setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
						setToolTipText("Em Ascent");
						break;
					case EM_DESCENT:
						setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
						setToolTipText("Em Descent");
						break;
					case EX_HEIGHT:
						setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
						setToolTipText("X Height");
						break;
					case LINE_ASCENT:
						setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
						setToolTipText("Line Ascent");
						break;
					case LINE_DESCENT:
						setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
						setToolTipText("Line Descent");
						break;
					case LINE_GAP:
						setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
						setToolTipText("Line Gap");
						break;
					case ADVANCE_WIDTH:
						setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
						setToolTipText("Glyph Width");
						break;
					case NONE:
						setCursor(null);
						setToolTipText(null);
						break;
					case INTERIOR:
						Cursor cursor = null;
						for (GlyphComponentListener l : listeners) {
							Cursor lc = l.getCursor(e, p, glyph, font);
							if (lc != null) cursor = lc;
						}
						setCursor(cursor);
						setToolTipText(null);
						break;
				}
				boolean changed = false;
				for (GlyphComponentListener l : listeners) {
					if (l.mouseMoved(e, p, glyph, font)) {
						changed = true;
					}
				}
				if (changed) glyphChanged();
			}
		}
		public void mousePressed(MouseEvent e) {
			inRegion = getRegion(e);
			if (inRegion == MouseRegion.INTERIOR) {
				Point2D p = getInteriorPoint(e);
				boolean changed = false;
				for (GlyphComponentListener l : listeners) {
					if (l.mousePressed(e, p, glyph, font)) {
						changed = true;
					}
				}
				if (changed) glyphChanged();
			} else if (inRegion != MouseRegion.NONE) {
				baseX = e.getX();
				baseY = e.getY();
				switch (inRegion) {
					case EM_ASCENT: baseValue = font.getEmAscent2D(); break;
					case EM_DESCENT: baseValue = font.getEmDescent2D(); break;
					case EX_HEIGHT: baseValue = font.getXHeight2D(); break;
					case LINE_ASCENT: baseValue = font.getLineAscent2D(); break;
					case LINE_DESCENT: baseValue = font.getLineDescent2D(); break;
					case LINE_GAP: baseValue = font.getLineGap2D(); break;
					case ADVANCE_WIDTH: baseValue = glyph.getCharacterWidth2D(); break;
					default: break;
				}
			}
		}
		public void mouseDragged(MouseEvent e) {
			if (inRegion == MouseRegion.INTERIOR) {
				Point2D p = getInteriorPoint(e);
				boolean changed = false;
				for (GlyphComponentListener l : listeners) {
					if (l.mouseDragged(e, p, glyph, font)) {
						changed = true;
					}
				}
				if (changed) glyphChanged();
			} else if (inRegion != MouseRegion.NONE) {
				double valueX = Math.round(baseValue + (e.getX() - baseX) / scale);
				double valueY = Math.round(baseValue + (e.getY() - baseY) / scale);
				double valueY2 = Math.round(baseValue - (e.getY() - baseY) / scale);
				switch (inRegion) {
					case EM_ASCENT: font.setEmAscent2D((valueY2 > 0) ? valueY2 : 0); break;
					case EM_DESCENT: font.setEmDescent2D((valueY > 0) ? valueY : 0); break;
					case EX_HEIGHT: font.setXHeight2D((valueY2 > 0) ? valueY2 : 0); break;
					case LINE_ASCENT: font.setLineAscent2D((valueY2 > 0) ? valueY2 : 0); break;
					case LINE_DESCENT: font.setLineDescent2D((valueY > 0) ? valueY : 0); break;
					case LINE_GAP: font.setLineGap2D((valueY > 0) ? valueY : 0); break;
					case ADVANCE_WIDTH: glyph.setCharacterWidth2D((valueX > 0) ? valueX : 0); break;
					default: break;
				}
				metricsChanged();
			}
		}
		public void mouseReleased(MouseEvent e) {
			if (inRegion == MouseRegion.INTERIOR) {
				Point2D p = getInteriorPoint(e);
				boolean changed = false;
				for (GlyphComponentListener l : listeners) {
					if (l.mouseReleased(e, p, glyph, font)) {
						changed = true;
					}
				}
				if (changed) glyphChanged();
			} else if (inRegion != MouseRegion.NONE) {
				double valueX = Math.round(baseValue + (e.getX() - baseX) / scale);
				double valueY = Math.round(baseValue + (e.getY() - baseY) / scale);
				double valueY2 = Math.round(baseValue - (e.getY() - baseY) / scale);
				switch (inRegion) {
					case EM_ASCENT: font.setEmAscent2D((valueY2 > 0) ? valueY2 : 0); break;
					case EM_DESCENT: font.setEmDescent2D((valueY > 0) ? valueY : 0); break;
					case EX_HEIGHT: font.setXHeight2D((valueY2 > 0) ? valueY2 : 0); break;
					case LINE_ASCENT: font.setLineAscent2D((valueY2 > 0) ? valueY2 : 0); break;
					case LINE_DESCENT: font.setLineDescent2D((valueY > 0) ? valueY : 0); break;
					case LINE_GAP: font.setLineGap2D((valueY > 0) ? valueY : 0); break;
					case ADVANCE_WIDTH: glyph.setCharacterWidth2D((valueX > 0) ? valueX : 0); break;
					default: break;
				}
				metricsChanged();
			}
			inRegion = MouseRegion.NONE;
		}
		private MouseRegion getRegion(MouseEvent e) {
			if (scale <= 0) setBestScaleAndTranslate();
			Insets insets = getInsets();
			int x = insets.left;
			int y = insets.top;
			int w = getWidth() - insets.left - insets.right;
			int h = getHeight() - insets.top - insets.bottom;
			int ix = x + MARGIN_WIDTH;
			int iy = y + MARGIN_HEIGHT;
			int iw = w - MARGIN_WIDTH * 2;
			int ih = h - MARGIN_HEIGHT * 2;
			if (e.getY() < iy || e.getY() >= iy + ih) {
				if (e.getX() >= ix && e.getX() < ix + iw) {
					int advance = ix + (int)Math.round(tx + glyph.getCharacterWidth2D() * scale);
					if (e.getX() > advance - 10 && e.getX() < advance + 10) {
						return MouseRegion.ADVANCE_WIDTH;
					}
				}
			} else if (e.getX() < ix) {
				if (e.getY() >= iy && e.getY() < iy + ih) {
					int emAscent = iy + (int)Math.round(ty - font.getEmAscent2D() * scale);
					int emDescent = iy + (int)Math.round(ty + font.getEmDescent2D() * scale);
					int exHeight = iy + (int)Math.round(ty - font.getXHeight2D() * scale);
					if (e.getY() > emAscent - 10 && e.getY() < emAscent + 10) {
						return MouseRegion.EM_ASCENT;
					} else if (e.getY() > emDescent - 10 && e.getY() < emDescent + 10) {
						return MouseRegion.EM_DESCENT;
					} else if (e.getY() > exHeight - 10 && e.getY() < exHeight + 10) {
						return MouseRegion.EX_HEIGHT;
					}
				}
			} else if (e.getX() >= ix + iw) {
				if (e.getY() >= iy && e.getY() < iy + ih) {
					int lineAscent = iy + (int)Math.round(ty - font.getLineAscent2D() * scale);
					int lineDescent = iy + (int)Math.round(ty + font.getLineDescent2D() * scale);
					int lineGap = iy + (int)Math.round(ty + (font.getLineDescent2D() + font.getLineGap2D()) * scale);
					if (e.getY() > lineAscent - 10 && e.getY() < lineAscent + 10) {
						return MouseRegion.LINE_ASCENT;
					} else if (e.getY() > lineDescent - 10 && e.getY() < lineDescent + 10) {
						return MouseRegion.LINE_DESCENT;
					} else if (e.getY() > lineGap - 10 && e.getY() < lineGap + 10) {
						return MouseRegion.LINE_GAP;
					}
				}
			} else {
				return MouseRegion.INTERIOR;
			}
			return MouseRegion.NONE;
		}
		private Point2D getInteriorPoint(MouseEvent e) {
			if (scale <= 0) setBestScaleAndTranslate();
			Insets insets = getInsets();
			int ix = insets.left + MARGIN_WIDTH;
			int iy = insets.top + MARGIN_HEIGHT;
			int origin = ix + (int)Math.round(tx);
			int baseline = iy + (int)Math.round(ty);
			double px = (e.getX() - origin) / scale;
			double py = (e.getY() - baseline) / scale;
			return new Point2D.Double(px, py);
		}
	}
	
	private static enum MouseRegion {
		NONE, INTERIOR, EM_ASCENT, EM_DESCENT, EX_HEIGHT,
		LINE_ASCENT, LINE_DESCENT, LINE_GAP, ADVANCE_WIDTH;
	}
}
