package com.kreative.bitsnpicas.edit;

import java.io.File;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontExporter;

public class GlyphListFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final SaveManager sm;
	private final GlyphListPanel panel;
	private final GlyphListMenuBar mb;
	
	public GlyphListFrame(File fontFile, FontExporter<?> format, Font<?> font) {
		super(font.toString());
		this.sm = new SaveManager(this, fontFile, format, font);
		this.panel = new GlyphListPanel(font, sm);
		this.mb = new GlyphListMenuBar(this, sm, font, panel.getGlyphList());
		makeUI();
	}
	
	public GlyphListFrame(SaveRoutine routine, Font<?> font) {
		super(font.toString());
		this.sm = new SaveManager(this, routine, font);
		this.panel = new GlyphListPanel(font, sm);
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
