package com.kreative.bitsnpicas.edit;

import java.io.File;
import javax.swing.JFrame;

public class PSFEncodingSelectionFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final PSFEncodingSelectionPanel panel;
	
	public PSFEncodingSelectionFrame(File file, PSFEncodingSelectionImporter importer) {
		this.panel = new PSFEncodingSelectionPanel(file, importer);
		setTitle("Open " + file.getName());
		setContentPane(panel);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
