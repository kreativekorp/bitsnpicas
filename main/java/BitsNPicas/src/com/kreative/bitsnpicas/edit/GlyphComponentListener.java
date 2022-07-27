package com.kreative.bitsnpicas.edit;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public interface GlyphComponentListener<G extends FontGlyph> {
	public Cursor getCursor(MouseEvent e, Point2D p, G glyph, Font<G> font);
	public boolean mouseMoved(MouseEvent e, Point2D p, G glyph, Font<G> font);
	public boolean mousePressed(MouseEvent e, Point2D p, G glyph, Font<G> font);
	public boolean mouseDragged(MouseEvent e, Point2D p, G glyph, Font<G> font);
	public boolean mouseReleased(MouseEvent e, Point2D p, G glyph, Font<G> font);
	public boolean mouseWheelMoved(MouseWheelEvent e, Point2D p, G glyph, Font<G> font);
	public void metricsChanged(G glyph, Font<G> font);
	public void glyphChanged(G glyph, Font<G> font);
}
