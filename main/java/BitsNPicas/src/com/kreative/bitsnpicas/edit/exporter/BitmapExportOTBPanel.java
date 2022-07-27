package com.kreative.bitsnpicas.edit.exporter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class BitmapExportOTBPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JRadioButton winMetricsLineHeight;
	private final JRadioButton winMetricsYminYmax;
	
	public BitmapExportOTBPanel() {
		this.winMetricsLineHeight = new JRadioButton("<html>typoAscent/typoDescent<br>(Force line height. More compatible.)</html>");
		this.winMetricsYminYmax = new JRadioButton("<html>yMin/yMax<br>(Prevent clipping. More conformant.)</html>");
		
		JPanel winMetricsPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		winMetricsPanel.add(new JLabel("Set winAscent/winDescent to:"));
		winMetricsPanel.add(winMetricsLineHeight);
		winMetricsPanel.add(winMetricsYminYmax);
		winMetricsLineHeight.setSelected(true);
		winMetricsYminYmax.setSelected(false);
		ButtonGroup bg = new ButtonGroup();
		bg.add(winMetricsLineHeight);
		bg.add(winMetricsYminYmax);
		
		this.setLayout(new BorderLayout());
		this.add(winMetricsPanel, BorderLayout.PAGE_START);
	}
	
	public boolean getExtendWinMetrics() {
		return winMetricsYminYmax.isSelected();
	}
}
