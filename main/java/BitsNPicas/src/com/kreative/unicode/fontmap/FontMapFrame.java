package com.kreative.unicode.fontmap;

import java.awt.BorderLayout;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FontMapFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final FontMapPanel panel;
	
	public FontMapFrame() {
		this(new FontMapPanel());
	}
	
	public FontMapFrame(Collection<? extends FontMapEntry> c) {
		this(new FontMapPanel(c));
	}
	
	private FontMapFrame(FontMapPanel panel) {
		super("Font Map");
		this.panel = panel;
		
		JPanel outerPanel = new JPanel(new BorderLayout(8, 8));
		outerPanel.add(panel, BorderLayout.CENTER);
		outerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(outerPanel);
		
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
	}
	
	public FontMapTableModel getFontMap() {
		return panel.getFontMap();
	}
}
