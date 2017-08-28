package com.kreative.bitsnpicas.edit;

import javax.swing.JFrame;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class BitmapEditFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private final BitmapEditPanel panel;
	private final BitmapEditMenuBar mb;
	
	public BitmapEditFrame(BitmapFont font, BitmapFontGlyph glyph, int codePoint, GlyphList gl, SaveManager sm) {
		this.panel = new BitmapEditPanel(font, glyph, gl);
		this.mb = new BitmapEditMenuBar(this, sm, font, panel.toolHandler, panel.glyphComponent, glyph, codePoint);
		setTitle(GlyphEditFrame.getTitle(font, codePoint));
		setJMenuBar(mb);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
