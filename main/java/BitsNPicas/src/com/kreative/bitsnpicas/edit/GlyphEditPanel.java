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
	
	private final GlyphComponent<G> glyphComponent;
	private final GlyphList<G> glyphList;
	private GlyphLocator<G> locator;
	
	public GlyphEditPanel(GlyphLocator<G> locator, GlyphList<G> gl) {
		this.glyphComponent = new GlyphComponent<G>(locator.getGlyphFont(), locator.getGlyph());
		this.glyphList = gl;
		this.locator = locator;
		setLayout(new BorderLayout());
		add(glyphComponent, BorderLayout.CENTER);
		glyphComponent.addGlyphComponentListener(new GlyphComponentListener<G>() {
			public void metricsChanged(G glyph, Font<G> font) {
				if (glyphList != null) glyphList.metricsChanged();
			}
			public void glyphChanged(G glyph, Font<G> font) {
				if (glyphList != null) glyphList.glyphContentChanged();
			}
			public Cursor getCursor(MouseEvent e, Point2D p, G glyph, Font<G> font) { return null; }
			public boolean mouseMoved(MouseEvent e, Point2D p, G glyph, Font<G> font) { return false; }
			public boolean mousePressed(MouseEvent e, Point2D p, G glyph, Font<G> font) { return false; }
			public boolean mouseDragged(MouseEvent e, Point2D p, G glyph, Font<G> font) { return false; }
			public boolean mouseReleased(MouseEvent e, Point2D p, G glyph, Font<G> font) { return false; }
			public boolean mouseWheelMoved(MouseWheelEvent e, Point2D p, G glyph, Font<G> font) { return false; }
		});
	}
	
	public GlyphComponent<G> getGlyphComponent() {
		return glyphComponent;
	}
	
	public Font<G> getGlyphFont() {
		return glyphComponent.getGlyphFont();
	}
	
	public G getGlyph() {
		return glyphComponent.getGlyph();
	}
	
	public GlyphList<G> getGlyphList() {
		return glyphList;
	}
	
	public GlyphLocator<G> getGlyphLocator() {
		return locator;
	}
	
	public void setGlyph(GlyphLocator<G> locator, Class<G> glyphClass) {
		if (locator == null) return;
		Font<G> font = locator.getGlyphFont();
		G glyph = locator.getGlyph();
		if (glyph == null) {
			if (glyphClass == null) return;
			try {
				glyph = glyphClass.newInstance();
				locator.setGlyph(glyph);
				this.glyphList.glyphRepertoireChanged();
			} catch (Exception ex) {
				return;
			}
		}
		this.glyphComponent.setGlyph(font, glyph);
		this.locator = locator;
	}
}
