package com.kreative.bitsnpicas.edit;

import java.io.File;
import javax.swing.JFrame;

public class FormatListFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final FormatListPanel panel;
	
	public FormatListFrame(File fontFile) {
		this.panel = new FormatListPanel(fontFile);
		setTitle("Open");
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
