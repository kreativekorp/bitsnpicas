package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class BitmapEditPanel extends GlyphEditPanel<BitmapFontGlyph> {
	private static final long serialVersionUID = 1L;
	
	public final BitmapToolPanel toolPanel;
	public final BitmapToolHandler toolHandler;
	
	public BitmapEditPanel(BitmapFont font, BitmapFontGlyph glyph, GlyphList gl) {
		super(font, glyph, gl);
		this.toolPanel = new BitmapToolPanel();
		this.toolHandler = new BitmapToolHandler(toolPanel, glyphComponent);
		add(toolPanel, BorderLayout.LINE_START);
	}
}
