package com.kreative.bitsnpicas.edit;

import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;

public class ImageBitmapFontImporterFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final ImageBitmapFontImporterPanel panel;
	
	public ImageBitmapFontImporterFrame(File fontFile) throws IOException {
		this.panel = new ImageBitmapFontImporterPanel(fontFile);
		setTitle("Create From Image");
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
