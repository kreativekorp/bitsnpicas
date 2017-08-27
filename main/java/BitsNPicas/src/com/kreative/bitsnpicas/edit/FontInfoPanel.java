package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import com.kreative.bitsnpicas.Font;

public class FontInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final NamesPanel namesPanel;
	private final MetricsPanel metricsPanel;
	
	public FontInfoPanel() {
		JScrollPane namesPane = new JScrollPane(
			namesPanel = new NamesPanel(),
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		JPanel namesPanel2 = new JPanel(new BorderLayout());
		namesPanel2.add(namesPane, BorderLayout.CENTER);
		namesPanel2.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		SwingUtils.setOpaque(namesPanel2, false);
		
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("Names", namesPanel2);
		tabPane.addTab("Metrics", metricsPanel = new MetricsPanel());
		SwingUtils.setOpaque(metricsPanel, false);
		
		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);
	}
	
	public void readFrom(Font<?> font) {
		namesPanel.readFrom(font);
		metricsPanel.readFrom(font);
	}
	
	public void writeTo(Font<?> font) {
		namesPanel.writeTo(font);
		metricsPanel.writeTo(font);
	}
}
