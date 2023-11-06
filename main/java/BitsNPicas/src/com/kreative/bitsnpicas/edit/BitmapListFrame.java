package com.kreative.bitsnpicas.edit;

import java.io.File;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class BitmapListFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final SaveManager sm;
	private final GlyphListPanel<BitmapFontGlyph> panel;
	private final BitmapListMenuBar mb;
	
	public BitmapListFrame(File fontFile, BitmapFontExporter format, BitmapFont font) {
		super(font.toString());
		this.sm = new SaveManager(this, fontFile, format, font);
		this.panel = new GlyphListPanel<BitmapFontGlyph>(font, sm);
		this.mb = new BitmapListMenuBar(this, sm, font, panel.getGlyphList());
		makeUI();
	}
	
	public BitmapListFrame(SaveRoutine routine, BitmapFont font) {
		super(font.toString());
		this.sm = new SaveManager(this, routine, font);
		this.panel = new GlyphListPanel<BitmapFontGlyph>(font, sm);
		this.mb = new BitmapListMenuBar(this, sm, font, panel.getGlyphList());
		makeUI();
	}
	
	public GlyphListPanel<BitmapFontGlyph> getPanel() {
		return panel;
	}
	
	private void makeUI() {
		setJMenuBar(mb);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(sm);
		panel.getGlyphList().requestFocusInWindow();
	}
}
