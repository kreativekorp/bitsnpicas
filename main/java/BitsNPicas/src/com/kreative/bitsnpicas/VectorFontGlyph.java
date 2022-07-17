package com.kreative.bitsnpicas;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VectorFontGlyph extends FontGlyph {
	protected List<VectorPath> contours;
	protected double advance;
	
	public VectorFontGlyph() {
		this.contours = new ArrayList<VectorPath>();
		this.advance = 0;
	}
	
	public VectorFontGlyph(Collection<? extends VectorPath> c) {
		this.contours = new ArrayList<VectorPath>();
		this.contours.addAll(c);
		if (c.isEmpty()) {
			this.advance = 0;
		} else {
			VectorPath p1 = c.iterator().next();
			Rectangle2D b1 = p1.toGeneralPath().getBounds2D();
			double left = b1.getMinX();
			double right = b1.getMaxX();
			for (VectorPath p : c) {
				Rectangle2D b = p.toGeneralPath().getBounds2D();
				double pleft = b.getMinX();
				double pright = b.getMaxX();
				if (pleft < left) left = pleft;
				if (pright > right) right = pright;
			}
			this.advance = left+right;
		}
	}
	
	public VectorFontGlyph(Collection<? extends VectorPath> c, double width) {
		this.contours = new ArrayList<VectorPath>();
		this.contours.addAll(c);
		this.advance = width;
	}
	
	public Collection<VectorPath> getContours() {
		return this.contours;
	}
	
	public int getGlyphOffset() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			VectorPath p1 = contours.iterator().next();
			Rectangle b1 = p1.toGeneralPath().getBounds();
			int left = b1.x;
			for (VectorPath p : contours) {
				Rectangle b = p.toGeneralPath().getBounds();
				int pleft = b.x;
				if (pleft < left) left = pleft;
			}
			return left;
		}
	}
	
	public double getGlyphOffset2D() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			VectorPath p1 = contours.iterator().next();
			Rectangle2D b1 = p1.toGeneralPath().getBounds2D();
			double left = b1.getMinX();
			for (VectorPath p : contours) {
				Rectangle2D b = p.toGeneralPath().getBounds2D();
				double pleft = b.getMinX();
				if (pleft < left) left = pleft;
			}
			return left;
		}
	}
	
	public int getGlyphWidth() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			VectorPath p1 = contours.iterator().next();
			Rectangle b1 = p1.toGeneralPath().getBounds();
			int left = b1.x;
			int right = b1.x + b1.width;
			for (VectorPath p : contours) {
				Rectangle b = p.toGeneralPath().getBounds();
				int pleft = b.x;
				int pright = b.x + b.width;
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
			VectorPath p1 = contours.iterator().next();
			Rectangle2D b1 = p1.toGeneralPath().getBounds2D();
			double left = b1.getMinX();
			double right = b1.getMaxX();
			for (VectorPath p : contours) {
				Rectangle2D b = p.toGeneralPath().getBounds2D();
				double pleft = b.getMinX();
				double pright = b.getMaxX();
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
			VectorPath p1 = contours.iterator().next();
			Rectangle b1 = p1.toGeneralPath().getBounds();
			int top = b1.y;
			int bottom = b1.y + b1.height;
			for (VectorPath p : contours) {
				Rectangle b = p.toGeneralPath().getBounds();
				int ptop = b.y;
				int pbottom = b.y + b.height;
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
			VectorPath p1 = contours.iterator().next();
			Rectangle2D b1 = p1.toGeneralPath().getBounds2D();
			double top = b1.getMinY();
			double bottom = b1.getMaxY();
			for (VectorPath p : contours) {
				Rectangle2D b = p.toGeneralPath().getBounds2D();
				double ptop = b.getMinY();
				double pbottom = b.getMaxY();
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
			VectorPath p1 = contours.iterator().next();
			Rectangle b1 = p1.toGeneralPath().getBounds();
			int top = b1.y;
			for (VectorPath p : contours) {
				Rectangle b = p.toGeneralPath().getBounds();
				int ptop = b.y;
				if (ptop < top) top = ptop;
			}
			return -top;
		}
	}
	
	public double getGlyphAscent2D() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			VectorPath p1 = contours.iterator().next();
			Rectangle2D b1 = p1.toGeneralPath().getBounds2D();
			double top = b1.getMinY();
			for (VectorPath p : contours) {
				Rectangle2D b = p.toGeneralPath().getBounds2D();
				double ptop = b.getMinY();
				if (ptop < top) top = ptop;
			}
			return -top;
		}
	}
	
	public int getGlyphDescent() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			VectorPath p1 = contours.iterator().next();
			Rectangle b1 = p1.toGeneralPath().getBounds();
			int bottom = b1.y + b1.height;
			for (VectorPath p : contours) {
				Rectangle b = p.toGeneralPath().getBounds();
				int pbottom = b.y + b.height;
				if (pbottom > bottom) bottom = pbottom;
			}
			return bottom;
		}
	}
	
	public double getGlyphDescent2D() {
		if (contours.isEmpty()) {
			return 0;
		} else {
			VectorPath p1 = contours.iterator().next();
			Rectangle2D b1 = p1.toGeneralPath().getBounds2D();
			double bottom = b1.getMaxY();
			for (VectorPath p : contours) {
				Rectangle2D b = p.toGeneralPath().getBounds2D();
				double pbottom = b.getMaxY();
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
		for (VectorPath contour : contours) {
			GeneralPath path = contour.toGeneralPath();
			Shape ss = sx.createTransformedShape(path);
			Shape ts = tx.createTransformedShape(ss);
			((Graphics2D)g).draw(ts);
		}
		return advance * scale;
	}
}
