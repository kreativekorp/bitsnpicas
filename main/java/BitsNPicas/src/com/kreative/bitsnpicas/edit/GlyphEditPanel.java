package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public class GlyphEditPanel<G extends FontGlyph> extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public final GlyphComponent glyphComponent;
	public final GlyphList glyphList;
	
	public GlyphEditPanel(Font<G> font, G glyph, GlyphList gl) {
		this.glyphComponent = new GlyphComponent(font, glyph);
		this.glyphList = gl;
		setLayout(new BorderLayout());
		add(glyphComponent, BorderLayout.CENTER);
		glyphComponent.addGlyphComponentListener(new GlyphComponentListener() {
			public void metricsChanged(FontGlyph glyph, Font<?> font) {
				if (glyphList != null) glyphList.metricsChanged();
			}
			public void glyphChanged(FontGlyph glyph, Font<?> font) {
				if (glyphList != null) glyphList.glyphsChanged();
			}
			public Cursor getCursor(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font) { return null; }
			public boolean mouseMoved(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font) { return false; }
			public boolean mousePressed(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font) { return false; }
			public boolean mouseDragged(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font) { return false; }
			public boolean mouseReleased(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font) { return false; }
			public boolean mouseWheelMoved(MouseWheelEvent e, Point2D p, FontGlyph glyph, Font<?> font) { return false; }
		});
	}
}
