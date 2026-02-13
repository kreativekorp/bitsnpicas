package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.kreative.bitsnpicas.VectorFont;

public class NewVectorFontPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JTextField familyNameField;
	private final JTextField styleNameField;
	private final String[] presetNames;
	private final double[][] presetValues;
	private final JComboBox presetPopup;
	private final FontInfoMetricsPanel metrics;
	private boolean eventLock;
	
	public NewVectorFontPanel() {
		this.familyNameField = new JTextField("Untitled");
		this.styleNameField = new JTextField("Regular");
		this.metrics = new FontInfoMetricsPanel();
		this.eventLock = false;
		
		ArrayList<String> presetNames = new ArrayList<String>();
		ArrayList<double[]> presetValues = new ArrayList<double[]>();
		Scanner scanner = new Scanner(NewVectorFontPanel.class.getResourceAsStream("NewVectorFontPresets.txt"), "UTF-8");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if (line.length() > 0) {
				if (line.equals("-")) {
					presetNames.add(line);
					presetValues.add(null);
				} else {
					String[] fields = line.split("\t+");
					String name = fields[0].trim();
					double[] values = new double[8];
					for (int i = 0; (i < 8) && ((i + 1) < fields.length); i++) {
						try { values[i] = Double.parseDouble(fields[i + 1].trim()); }
						catch (NumberFormatException e) { values[i] = 0; }
					}
					presetNames.add(name);
					presetValues.add(values);
				}
			}
		}
		scanner.close();
		presetNames.add("-");
		presetValues.add(null);
		presetNames.add("Custom");
		presetValues.add(null);
		
		this.presetNames = presetNames.toArray(new String[presetNames.size()]);
		this.presetValues = presetValues.toArray(new double[presetValues.size()][]);
		this.presetPopup = new JComboBox(this.presetNames);
		this.presetPopup.setEditable(false);
		this.presetPopup.setMaximumRowCount(this.presetNames.length);
		this.presetPopup.setRenderer(new MyListCellRenderer());
		this.presetPopup.addItemListener(new MyItemListener());
		this.setPresetValues(this.presetValues[0]);
		this.metrics.addChangeListener(new MyChangeListener());
		
		JPanel labelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		labelPanel.add(new JLabel("Family Name:"));
		labelPanel.add(new JLabel("Style Name:"));
		labelPanel.add(new JLabel("Profile:"));
		
		JPanel fieldPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		fieldPanel.add(familyNameField);
		fieldPanel.add(styleNameField);
		fieldPanel.add(presetPopup);
		
		JPanel topPanel = new JPanel(new BorderLayout(8, 8));
		topPanel.add(labelPanel, BorderLayout.LINE_START);
		topPanel.add(fieldPanel, BorderLayout.CENTER);
		
		setLayout(new BorderLayout(12, 12));
		add(topPanel, BorderLayout.PAGE_START);
		add(metrics, BorderLayout.CENTER);
	}
	
	private void setPresetValues(double[] values) {
		VectorFont f = new VectorFont();
		f.setEmAscent2D(values[0]);
		f.setEmDescent2D(values[1]);
		f.setLineAscent2D(values[2]);
		f.setLineDescent2D(values[3]);
		f.setXHeight2D(values[4]);
		f.setCapHeight2D(values[5]);
		f.setLineGap2D(values[6]);
		f.setNewGlyphWidth2D(values[7]);
		metrics.readFrom(f);
	}
	
	public VectorFont createVectorFont() {
		VectorFont f = new VectorFont();
		f.setName(VectorFont.NAME_FAMILY, familyNameField.getText());
		f.setName(VectorFont.NAME_STYLE, styleNameField.getText());
		f.autoFillNames();
		metrics.writeTo(f);
		return f;
	}
	
	private class MyListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList l, Object v, int i, boolean s, boolean f) {
			if (v.equals("-")) return new JSeparator(JSeparator.HORIZONTAL);
			return super.getListCellRendererComponent(l, v, i, s, f);
		}
	}
	
	private class MyItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (eventLock) return;
			eventLock = true;
			int i = presetPopup.getSelectedIndex();
			if (i >= 0 && i < presetValues.length) {
				double[] values = presetValues[i];
				if (values != null) {
					setPresetValues(values);
				} else {
					presetPopup.setSelectedIndex(presetValues.length - 1);
				}
			} else {
				presetPopup.setSelectedIndex(presetValues.length - 1);
			}
			eventLock = false;
		}
	}
	
	private class MyChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			if (eventLock) return;
			eventLock = true;
			presetPopup.setSelectedIndex(presetValues.length - 1);
			eventLock = false;
		}
	}
}
