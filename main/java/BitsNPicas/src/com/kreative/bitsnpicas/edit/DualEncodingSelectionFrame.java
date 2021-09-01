package com.kreative.bitsnpicas.edit;

import java.io.File;
import javax.swing.JFrame;

public class DualEncodingSelectionFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final DualEncodingSelectionPanel panel;
	
	public DualEncodingSelectionFrame(String sben, String dben, File file, DualEncodingSelectionImporter importer) {
		this.panel = new DualEncodingSelectionPanel(sben, dben, file, importer);
		setTitle("Open " + file.getName());
		setContentPane(panel);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
