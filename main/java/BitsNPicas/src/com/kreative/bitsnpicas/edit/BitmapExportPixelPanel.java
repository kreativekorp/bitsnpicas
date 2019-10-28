package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class BitmapExportPixelPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final SpinnerNumberModel pixelWidth;
	private final SpinnerNumberModel pixelHeight;
	
	public BitmapExportPixelPanel() {
		this.pixelWidth = new SpinnerNumberModel(100, 1, 1000, 1);
		this.pixelHeight = new SpinnerNumberModel(100, 1, 1000, 1);
		
		JPanel pixelLabelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		pixelLabelPanel.add(new JLabel("Pixel Width"));
		pixelLabelPanel.add(new JLabel("Pixel Height"));
		JPanel pixelSpinnerPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		pixelSpinnerPanel.add(new JSpinner(pixelWidth));
		pixelSpinnerPanel.add(new JSpinner(pixelHeight));
		JPanel pixelInnerPanel = new JPanel(new BorderLayout(8, 8));
		pixelInnerPanel.add(pixelLabelPanel, BorderLayout.LINE_START);
		pixelInnerPanel.add(pixelSpinnerPanel, BorderLayout.CENTER);
		JPanel pixelOuterPanel = new JPanel(new BorderLayout());
		pixelOuterPanel.add(pixelInnerPanel, BorderLayout.LINE_START);
		
		this.setLayout(new BorderLayout());
		this.add(pixelOuterPanel, BorderLayout.PAGE_START);
	}
	
	public Dimension getPixelDimension() {
		return new Dimension(
			pixelWidth.getNumber().intValue(),
			pixelHeight.getNumber().intValue()
		);
	}
}
