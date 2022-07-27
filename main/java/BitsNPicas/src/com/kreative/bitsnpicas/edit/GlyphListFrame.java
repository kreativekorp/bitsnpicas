package com.kreative.bitsnpicas.edit;

import java.io.File;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontExporter;
import com.kreative.bitsnpicas.FontGlyph;

public class GlyphListFrame<G extends FontGlyph> extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final SaveManager sm;
	private final GlyphListPanel<G> panel;
	private final GlyphListMenuBar mb;
	
	public GlyphListFrame(File fontFile, FontExporter<? extends Font<G>> format, Font<G> font) {
		super(font.toString());
		this.sm = new SaveManager(this, fontFile, format, font);
		this.panel = new GlyphListPanel<G>(font, sm);
		this.mb = new GlyphListMenuBar(this, sm, font, panel.getGlyphList());
		makeUI();
	}
	
	public GlyphListFrame(SaveRoutine routine, Font<G> font) {
		super(font.toString());
		this.sm = new SaveManager(this, routine, font);
		this.panel = new GlyphListPanel<G>(font, sm);
		this.mb = new GlyphListMenuBar(this, sm, font, panel.getGlyphList());
		makeUI();
	}
	
	public void makeUI() {
		setJMenuBar(mb);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(sm);
		panel.getGlyphList().requestFocusInWindow();
	}
}
