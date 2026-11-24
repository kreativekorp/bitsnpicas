package com.kreative.bitsnpicas.edit.importer;

import java.io.File;
import javax.swing.JFrame;

public class EncodingSelectionFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final EncodingSelectionPanel panel;
	
	public EncodingSelectionFrame(String encodingName, File file, EncodingSelectionImporter importer) {
		this(encodingName, file, importer, null);
	}
	
	public EncodingSelectionFrame(String encodingName, File file, EncodingSelectionImporter importer, String note) {
		this.panel = new EncodingSelectionPanel(encodingName, file, importer, note);
		setTitle("Open " + file.getName());
		setContentPane(panel);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
