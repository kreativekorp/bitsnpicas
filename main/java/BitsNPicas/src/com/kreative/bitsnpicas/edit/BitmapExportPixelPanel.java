package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class BitmapExportPixelPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final SpinnerNumberModel pixelWidth;
	private final SpinnerNumberModel pixelHeight;
	private final JRadioButton winMetricsLineHeight;
	private final JRadioButton winMetricsYminYmax;
	
	public BitmapExportPixelPanel() {
		this.pixelWidth = new SpinnerNumberModel(100, 1, 1000, 1);
		this.pixelHeight = new SpinnerNumberModel(100, 1, 1000, 1);
		this.winMetricsLineHeight = new JRadioButton("<html>typoAscent/typoDescent<br>(Force line height. More compatible.)</html>");
		this.winMetricsYminYmax = new JRadioButton("<html>yMin/yMax<br>(Prevent clipping. More conformant.)</html>");
		
		JPanel pixelLabelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		pixelLabelPanel.add(new JLabel("Pixel Width"));
		pixelLabelPanel.add(new JLabel("Pixel Height"));
		JPanel pixelSpinnerPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		pixelSpinnerPanel.add(new JSpinner(pixelWidth));
		pixelSpinnerPanel.add(new JSpinner(pixelHeight));
		JPanel pixelUnitPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		pixelUnitPanel.add(new JLabel("em units"));
		pixelUnitPanel.add(new JLabel("em units"));
		JPanel pixelInnerPanel1 = new JPanel(new BorderLayout(8, 8));
		pixelInnerPanel1.add(pixelLabelPanel, BorderLayout.LINE_START);
		pixelInnerPanel1.add(pixelSpinnerPanel, BorderLayout.CENTER);
		JPanel pixelInnerPanel2 = new JPanel(new BorderLayout(8, 8));
		pixelInnerPanel2.add(pixelInnerPanel1, BorderLayout.LINE_START);
		pixelInnerPanel2.add(pixelUnitPanel, BorderLayout.CENTER);
		JPanel pixelOuterPanel = new JPanel(new BorderLayout());
		pixelOuterPanel.add(pixelInnerPanel2, BorderLayout.LINE_START);
		
		JPanel winMetricsPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		winMetricsPanel.add(new JLabel("Set winAscent/winDescent to:"));
		winMetricsPanel.add(winMetricsLineHeight);
		winMetricsPanel.add(winMetricsYminYmax);
		winMetricsLineHeight.setSelected(true);
		winMetricsYminYmax.setSelected(false);
		ButtonGroup bg = new ButtonGroup();
		bg.add(winMetricsLineHeight);
		bg.add(winMetricsYminYmax);
		
		JPanel outerPanel = new JPanel(new BorderLayout(8, 8));
		outerPanel.add(pixelOuterPanel, BorderLayout.PAGE_START);
		outerPanel.add(winMetricsPanel, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(outerPanel, BorderLayout.PAGE_START);
	}
	
	public Dimension getPixelDimension() {
		return new Dimension(
			pixelWidth.getNumber().intValue(),
			pixelHeight.getNumber().intValue()
		);
	}
	
	public boolean getExtendWinMetrics() {
		return winMetricsYminYmax.isSelected();
	}
}
