package com.kreative.bitsnpicas;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VectorFontGlyph extends FontGlyph {
	protected List<GeneralPath> contours;
	protected double advance;
	
	public VectorFontGlyph() {
		this.contours = new ArrayList<GeneralPath>();
		this.advance = 0;
	}
	
	public VectorFontGlyph(Collection<? extends GeneralPath> c) {
		this.contours = new ArrayList<GeneralPath>();
		this.contours.addAll(c);
		if (c.isEmpty()) {
			this.advance = 0;
		} else {
			GeneralPath p1 = c.iterator().next();
			double left = p1.getBounds2D().getMinX();
			double right = p1.getBounds2D().getMaxX();
			for (GeneralPath p : c) {
				double pleft = p.getBounds2D().getMinX();
				double pright = p.getBounds2D().getMaxX();
				if (pleft < left) left = pleft;
				if (pright > right) right = pright;
			}
			this.advance = left+right;
		}
	}
	
	public VectorFontGlyph(Collection<? extends GeneralPath> c, double width) {
		this.contours = new ArrayList<GeneralPath>();
		this.contours.addAll(c);
		this.advance = width;
	}
	
	public Collection<? extends GeneralPath> getContours() {
		return this.contours;
	}
	
	public int getGlyphOffset() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			GeneralPath p1 = contours.iterator().next();
			int left = p1.getBounds().x;
			for (GeneralPath p : contours) {
				int pleft = p.getBounds().x;
				if (pleft < left) left = pleft;
			}
			return left;
		}
	}
	
	public double getGlyphOffset2D() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			GeneralPath p1 = contours.iterator().next();
			double left = p1.getBounds2D().getMinX();
			for (GeneralPath p : contours) {
				double pleft = p.getBounds2D().getMinX();
				if (pleft < left) left = pleft;
			}
			return left;
		}
	}
	
	public int getGlyphWidth() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			GeneralPath p1 = contours.iterator().next();
			int left = p1.getBounds().x;
			int right = p1.getBounds().x + p1.getBounds().width;
			for (GeneralPath p : contours) {
				int pleft = p.getBounds().x;
				int pright = p.getBounds().x + p.getBounds().width;
				if (pleft < left) left = pleft;
				if (pright > right) right = pright;
			}
			return right-left;
		}
	}
	
	public double getGlyphWidth2D() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			GeneralPath p1 = contours.iterator().next();
			double left = p1.getBounds2D().getMinX();
			double right = p1.getBounds2D().getMaxX();
			for (GeneralPath p : contours) {
				double pleft = p.getBounds2D().getMinX();
				double pright = p.getBounds2D().getMaxX();
				if (pleft < left) left = pleft;
				if (pright > right) right = pright;
			}
			return right-left;
		}
	}
	
	public int getGlyphHeight() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			GeneralPath p1 = contours.iterator().next();
			int top = p1.getBounds().y;
			int bottom = p1.getBounds().y + p1.getBounds().height;
			for (GeneralPath p : contours) {
				int ptop = p.getBounds().y;
				int pbottom = p.getBounds().y + p.getBounds().height;
				if (ptop < top) top = ptop;
				if (pbottom > bottom) bottom = pbottom;
			}
			return bottom-top;
		}
	}
	
	public double getGlyphHeight2D() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			GeneralPath p1 = contours.iterator().next();
			double top = p1.getBounds2D().getMinY();
			double bottom = p1.getBounds2D().getMaxY();
			for (GeneralPath p : contours) {
				double ptop = p.getBounds2D().getMinY();
				double pbottom = p.getBounds2D().getMaxY();
				if (ptop < top) top = ptop;
				if (pbottom > bottom) bottom = pbottom;
			}
			return bottom-top;
		}
	}
	
	public int getGlyphAscent() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			GeneralPath p1 = contours.iterator().next();
			int top = p1.getBounds().y;
			for (GeneralPath p : contours) {
				int ptop = p.getBounds().y;
				if (ptop < top) top = ptop;
			}
			return -top;
		}
	}
	
	public double getGlyphAscent2D() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			GeneralPath p1 = contours.iterator().next();
			double top = p1.getBounds2D().getMinY();
			for (GeneralPath p : contours) {
				double ptop = p.getBounds2D().getMinY();
				if (ptop < top) top = ptop;
			}
			return -top;
		}
	}
	
	public int getGlyphDescent() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			GeneralPath p1 = contours.iterator().next();
			int bottom = p1.getBounds().y + p1.getBounds().height;
			for (GeneralPath p : contours) {
				int pbottom = p.getBounds().y + p.getBounds().height;
				if (pbottom > bottom) bottom = pbottom;
			}
			return bottom;
		}
	}
	
	public double getGlyphDescent2D() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			GeneralPath p1 = contours.iterator().next();
			double bottom = p1.getBounds2D().getMaxY();
			for (GeneralPath p : contours) {
				double pbottom = p.getBounds2D().getMaxY();
				if (pbottom > bottom) bottom = pbottom;
			}
			return bottom;
		}
	}
	
	public int getCharacterWidth() {
		return (int)Math.ceil(advance);
	}
	
	public double getCharacterWidth2D() {
		return advance;
	}
	
	public void setCharacterWidth(int v) {
		advance = v;
	}
	
	public void setCharacterWidth2D(double v) {
		advance = v;
	}
	
	public double paint(Graphics g, double x, double y, double scale) {
		AffineTransform tx = AffineTransform.getTranslateInstance(x, y);
		AffineTransform sx = AffineTransform.getScaleInstance(scale, scale);
		for (GeneralPath path : contours) {
			Shape ss = sx.createTransformedShape(path);
			Shape ts = tx.createTransformedShape(ss);
			((Graphics2D)g).draw(ts);
		}
		return advance * scale;
	}
}
