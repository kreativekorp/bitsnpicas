package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.kreative.bitsnpicas.Font;

public class FontInfoPropertiesPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JTextArea properties;
	
	public FontInfoPropertiesPanel() {
		properties = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(
			properties,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.add(new JLabel("One property to a line, as name=value"), BorderLayout.PAGE_START);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		SwingUtils.setOpaque(mainPanel, false);
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
	}
	
	public void readFrom(Font<?> font) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String,String> e : font.properties(false).entrySet()) {
			sb.append(e.getKey());
			sb.append("=");
			sb.append(e.getValue());
			sb.append("\n");
		}
		properties.setText(sb.toString());
		properties.setCaretPosition(0);
	}
	
	public void writeTo(Font<?> font) {
		// A line without = or without a name is not a property.
		SortedMap<String,String> props = new TreeMap<String,String>();
		for (String line : properties.getText().split("\r\n|\r|\n")) {
			int o = line.indexOf('=');
			if (o <= 0) continue;
			String key = line.substring(0, o).trim();
			if (key.length() > 0) props.put(key, line.substring(o + 1).trim());
		}
		for (String key : font.properties(true).keySet()) {
			if (!props.containsKey(key)) font.removeProperty(key);
		}
		for (Map.Entry<String,String> e : props.entrySet()) {
			font.setProperty(e.getKey(), e.getValue());
		}
	}
}
