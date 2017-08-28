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
		this.sm = new SaveManager(fontFile, format, font);
		this.panel = new GlyphListPanel(font, sm);
		this.mb = new GlyphListMenuBar(this, sm, font, panel.getGlyphList());
		setTitle(font.toString());
		setJMenuBar(mb);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(sm);
	}
}
