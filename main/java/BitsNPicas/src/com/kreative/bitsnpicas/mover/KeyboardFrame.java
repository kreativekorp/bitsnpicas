package com.kreative.bitsnpicas.mover;

import javax.swing.JFrame;
import com.kreative.unicode.data.GlyphList;
import com.kreative.unicode.ttflib.DfontResource;

public class KeyboardFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private KeyboardPanel panel;
	
	public KeyboardFrame(DfontResource res, GlyphList encoding) {
		super(res.getName());
		this.panel = new KeyboardPanel(res.getData(), encoding);
		
		setJMenuBar(new KeyboardMenuBar(this));
		setContentPane(panel);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addKeyListener(panel);
	}
}
