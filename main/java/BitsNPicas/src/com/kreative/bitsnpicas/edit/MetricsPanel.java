package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import com.kreative.bitsnpicas.Font;

public class MetricsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final SpinnerNumberModel emAscent;
	private final SpinnerNumberModel emDescent;
	private final SpinnerNumberModel lineAscent;
	private final SpinnerNumberModel lineDescent;
	private final SpinnerNumberModel lineGap;
	private final SpinnerNumberModel xHeight;
	
	public MetricsPanel() {
		JPanel labelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		labelPanel.add(new JLabel("Em Ascent"));
		labelPanel.add(new JLabel("Em Descent"));
		labelPanel.add(new JLabel("Line Ascent"));
		labelPanel.add(new JLabel("Line Descent"));
		labelPanel.add(new JLabel("Line Gap"));
		labelPanel.add(new JLabel("X Height"));
		
		JPanel spinnerPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		spinnerPanel.add(new JSpinner(emAscent = new SpinnerNumberModel()));
		spinnerPanel.add(new JSpinner(emDescent = new SpinnerNumberModel()));
		spinnerPanel.add(new JSpinner(lineAscent = new SpinnerNumberModel()));
		spinnerPanel.add(new JSpinner(lineDescent = new SpinnerNumberModel()));
		spinnerPanel.add(new JSpinner(lineGap = new SpinnerNumberModel()));
		spinnerPanel.add(new JSpinner(xHeight = new SpinnerNumberModel()));
		Dimension d = new Dimension(80, spinnerPanel.getPreferredSize().height);
		spinnerPanel.setMinimumSize(d);
		spinnerPanel.setPreferredSize(d);
		
		JPanel mainPanel1 = new JPanel(new BorderLayout());
		mainPanel1.add(spinnerPanel, BorderLayout.LINE_START);
		JPanel mainPanel2 = new JPanel(new BorderLayout(8, 8));
		mainPanel2.add(labelPanel, BorderLayout.LINE_START);
		mainPanel2.add(mainPanel1, BorderLayout.CENTER);
		mainPanel2.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		setLayout(new BorderLayout());
		add(mainPanel2, BorderLayout.PAGE_START);
	}
	
	public void readFrom(Font<?> font) {
		emAscent.setValue(font.getEmAscent());
		emDescent.setValue(font.getEmDescent());
		lineAscent.setValue(font.getLineAscent());
		lineDescent.setValue(font.getLineDescent());
		lineGap.setValue(font.getLineGap());
		xHeight.setValue(font.getXHeight());
	}
	
	public void writeTo(Font<?> font) {
		font.setEmAscent(emAscent.getNumber().intValue());
		font.setEmDescent(emDescent.getNumber().intValue());
		font.setLineAscent(lineAscent.getNumber().intValue());
		font.setLineDescent(lineDescent.getNumber().intValue());
		font.setLineGap(lineGap.getNumber().intValue());
		font.setXHeight(xHeight.getNumber().intValue());
	}
}
