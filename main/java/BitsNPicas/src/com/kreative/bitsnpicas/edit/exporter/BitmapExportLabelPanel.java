package com.kreative.bitsnpicas.edit.exporter;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BitmapExportLabelPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public BitmapExportLabelPanel(String text) {
		super(new BorderLayout());
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(JLabel.CENTER);
		add(label, BorderLayout.CENTER);
	}
}
