package com.kreative.bitsnpicas.edit;

import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;

public class BinaryBitmapFontImporterFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final BinaryBitmapFontImporterPanel panel;
	
	public BinaryBitmapFontImporterFrame(File fontFile) throws IOException {
		this.panel = new BinaryBitmapFontImporterPanel(fontFile);
		setTitle("Create From Binary File");
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
