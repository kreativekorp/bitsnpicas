package com.kreative.bitsnpicas.mover;

import javax.swing.JFrame;
import com.kreative.bitsnpicas.unicode.EncodingTable;
import com.kreative.rsrc.MacResource;

public class KeyboardFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private KeyboardPanel panel;
	
	public KeyboardFrame(MacResource res, EncodingTable encoding) {
		super(res.name);
		this.panel = new KeyboardPanel(res.data, encoding);
		
		setJMenuBar(new KeyboardMenuBar(this));
		setContentPane(panel);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addKeyListener(panel);
	}
}
