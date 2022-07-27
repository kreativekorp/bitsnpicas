package com.kreative.bitsnpicas.edit.exporter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class BitmapExportColorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final SpinnerNumberModel pngColorRed;
	private final SpinnerNumberModel pngColorGreen;
	private final SpinnerNumberModel pngColorBlue;
	
	public BitmapExportColorPanel() {
		this.pngColorRed = new SpinnerNumberModel(0, 0, 255, 1);
		this.pngColorGreen = new SpinnerNumberModel(0, 0, 255, 1);
		this.pngColorBlue = new SpinnerNumberModel(0, 0, 255, 1);
		
		JPanel pngColorLabelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		pngColorLabelPanel.add(new JLabel("Red"));
		pngColorLabelPanel.add(new JLabel("Green"));
		pngColorLabelPanel.add(new JLabel("Blue"));
		JPanel pngColorSpinnerPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		pngColorSpinnerPanel.add(new JSpinner(pngColorRed));
		pngColorSpinnerPanel.add(new JSpinner(pngColorGreen));
		pngColorSpinnerPanel.add(new JSpinner(pngColorBlue));
		JPanel pngColorInnerPanel = new JPanel(new BorderLayout(8, 8));
		pngColorInnerPanel.add(pngColorLabelPanel, BorderLayout.LINE_START);
		pngColorInnerPanel.add(pngColorSpinnerPanel, BorderLayout.CENTER);
		JPanel pngColorOuterPanel = new JPanel(new BorderLayout());
		pngColorOuterPanel.add(pngColorInnerPanel, BorderLayout.LINE_START);
		
		this.setLayout(new BorderLayout());
		this.add(pngColorOuterPanel, BorderLayout.PAGE_START);
	}
	
	public int getSelectedColor() {
		return (
			(pngColorRed.getNumber().intValue() << 16) |
			(pngColorGreen.getNumber().intValue() << 8) |
			(pngColorBlue.getNumber().intValue() << 0)
		);
	}
}
