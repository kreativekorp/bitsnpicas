package com.kreative.bitsnpicas.edit.exporter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;

public class BitmapExportGEOSPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JRadioButton geosFontIdAuto;
	private final JRadioButton geosFontIdManual;
	private final SpinnerNumberModel geosFontId;
	private final JRadioButton geosFontSizeAutoAny;
	private final JRadioButton geosFontSizeAutoStandard;
	private final JRadioButton geosFontSizeManual;
	private final SpinnerNumberModel geosFontSize;
	private final JRadioButton geosFontSizeMega;
	private final JCheckBox geosKerning;
	private final JCheckBox geosUTF8;
	
	public BitmapExportGEOSPanel() {
		this.geosFontIdAuto = new JRadioButton("Auto");
		this.geosFontIdManual = new JRadioButton("Manual:");
		this.geosFontId = new SpinnerNumberModel(512, 0, 0x3FF, 1);
		this.geosFontSizeAutoAny = new JRadioButton("Auto (Unrestricted)");
		this.geosFontSizeAutoStandard = new JRadioButton("Auto (Standard Sizes)");
		this.geosFontSizeManual = new JRadioButton("Manual:");
		this.geosFontSize = new SpinnerNumberModel(12, 1, 63, 1);
		this.geosFontSizeMega = new JRadioButton("MEGA");
		this.geosKerning = new JCheckBox("Add kerning tables (EXPERIMENTAL)");
		this.geosUTF8 = new JCheckBox("Add UTF-8 tables (EXPERIMENTAL)");
		
		geosFontIdAuto.setSelected(true);
		geosFontSizeAutoAny.setSelected(true);
		ButtonGroup geosFontIdGroup = new ButtonGroup();
		geosFontIdGroup.add(geosFontIdAuto);
		geosFontIdGroup.add(geosFontIdManual);
		ButtonGroup geosFontSizeGroup = new ButtonGroup();
		geosFontSizeGroup.add(geosFontSizeAutoAny);
		geosFontSizeGroup.add(geosFontSizeAutoStandard);
		geosFontSizeGroup.add(geosFontSizeManual);
		geosFontSizeGroup.add(geosFontSizeMega);
		JPanel geosFontIdManualPanel = new JPanel(new BorderLayout(8, 8));
		geosFontIdManualPanel.add(geosFontIdManual, BorderLayout.LINE_START);
		geosFontIdManualPanel.add(new JSpinner(geosFontId), BorderLayout.CENTER);
		JPanel geosFontSizeManualPanel = new JPanel(new BorderLayout(8, 8));
		geosFontSizeManualPanel.add(geosFontSizeManual, BorderLayout.LINE_START);
		geosFontSizeManualPanel.add(new JSpinner(geosFontSize), BorderLayout.CENTER);
		JPanel geosLabelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		geosLabelPanel.add(new JLabel("GEOS Font ID"));
		geosLabelPanel.add(new JLabel(" "));
		geosLabelPanel.add(new JLabel("GEOS Font Size"));
		geosLabelPanel.add(new JLabel(" "));
		geosLabelPanel.add(new JLabel(" "));
		geosLabelPanel.add(new JLabel(" "));
		geosLabelPanel.add(new JLabel("CX16 Extensions"));
		geosLabelPanel.add(new JLabel(" "));
		JPanel geosControlPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		geosControlPanel.add(geosFontIdAuto);
		geosControlPanel.add(geosFontIdManualPanel);
		geosControlPanel.add(geosFontSizeAutoAny);
		geosControlPanel.add(geosFontSizeAutoStandard);
		geosControlPanel.add(geosFontSizeManualPanel);
		geosControlPanel.add(geosFontSizeMega);
		geosControlPanel.add(geosKerning);
		geosControlPanel.add(geosUTF8);
		JPanel geosInnerPanel = new JPanel(new BorderLayout(8, 8));
		geosInnerPanel.add(geosLabelPanel, BorderLayout.LINE_START);
		geosInnerPanel.add(geosControlPanel, BorderLayout.CENTER);
		JPanel geosOuterPanel = new JPanel(new BorderLayout());
		geosOuterPanel.add(geosInnerPanel, BorderLayout.LINE_START);
		
		this.setLayout(new BorderLayout());
		this.add(geosOuterPanel, BorderLayout.PAGE_START);
	}
	
	public IDGenerator getIDGenerator() {
		if (geosFontIdManual.isSelected()) {
			int id = geosFontId.getNumber().intValue();
			return new IDGenerator.Sequential(id, 128, 1024);
		} else {
			return new IDGenerator.HashCode(128, 1024);
		}
	}
	
	public PointSizeGenerator getPointSizeGenerator() {
		if (geosFontSizeMega.isSelected()) {
			return new PointSizeGenerator.Fixed(48);
		} else if (geosFontSizeManual.isSelected()) {
			int size = geosFontSize.getNumber().intValue();
			return new PointSizeGenerator.Fixed(size);
		} else if (geosFontSizeAutoStandard.isSelected()) {
			return new PointSizeGenerator.Standard(9, 10, 12, 14, 18, 24, 36, 48, 60);
		} else {
			return new PointSizeGenerator.Automatic(6, 63);
		}
	}
	
	public boolean getGEOSMega() {
		return geosFontSizeMega.isSelected();
	}
	
	public boolean getGEOSKerning() {
		return geosKerning.isSelected();
	}
	
	public boolean getGEOSUTF8() {
		return geosUTF8.isSelected();
	}
}
