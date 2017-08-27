package com.kreative.bitsnpicas.edit;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public interface GlyphComponentListener {
	public Cursor getCursor(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font);
	public boolean mouseMoved(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font);
	public boolean mousePressed(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font);
	public boolean mouseDragged(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font);
	public boolean mouseReleased(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font);
	public boolean mouseWheelMoved(MouseWheelEvent e, Point2D p, FontGlyph glyph, Font<?> font);
	public void metricsChanged(FontGlyph glyph, Font<?> font);
	public void glyphChanged(FontGlyph glyph, Font<?> font);
}
