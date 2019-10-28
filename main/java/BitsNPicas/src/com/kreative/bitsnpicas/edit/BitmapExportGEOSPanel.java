package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import com.kreative.bitsnpicas.exporter.GEOSBitmapFontExporter;

public class BitmapExportGEOSPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JRadioButton geosFontIdAuto;
	private final JRadioButton geosFontIdManual;
	private final SpinnerNumberModel geosFontId;
	private final JRadioButton geosFontSizeAutoAny;
	private final JRadioButton geosFontSizeAutoStandard;
	private final JRadioButton geosFontSizeManual;
	private final SpinnerNumberModel geosFontSize;
	
	public BitmapExportGEOSPanel() {
		this.geosFontIdAuto = new JRadioButton("Auto");
		this.geosFontIdManual = new JRadioButton("Manual:");
		this.geosFontId = new SpinnerNumberModel(512, 0, 0x3FF, 1);
		this.geosFontSizeAutoAny = new JRadioButton("Auto (Unrestricted)");
		this.geosFontSizeAutoStandard = new JRadioButton("Auto (Standard Sizes)");
		this.geosFontSizeManual = new JRadioButton("Manual:");
		this.geosFontSize = new SpinnerNumberModel(12, 1, 63, 1);
		
		geosFontIdAuto.setSelected(true);
		geosFontSizeAutoAny.setSelected(true);
		ButtonGroup geosFontIdGroup = new ButtonGroup();
		geosFontIdGroup.add(geosFontIdAuto);
		geosFontIdGroup.add(geosFontIdManual);
		ButtonGroup geosFontSizeGroup = new ButtonGroup();
		geosFontSizeGroup.add(geosFontSizeAutoAny);
		geosFontSizeGroup.add(geosFontSizeAutoStandard);
		geosFontSizeGroup.add(geosFontSizeManual);
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
		JPanel geosControlPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		geosControlPanel.add(geosFontIdAuto);
		geosControlPanel.add(geosFontIdManualPanel);
		geosControlPanel.add(geosFontSizeAutoAny);
		geosControlPanel.add(geosFontSizeAutoStandard);
		geosControlPanel.add(geosFontSizeManualPanel);
		JPanel geosInnerPanel = new JPanel(new BorderLayout(8, 8));
		geosInnerPanel.add(geosLabelPanel, BorderLayout.LINE_START);
		geosInnerPanel.add(geosControlPanel, BorderLayout.CENTER);
		JPanel geosOuterPanel = new JPanel(new BorderLayout());
		geosOuterPanel.add(geosInnerPanel, BorderLayout.LINE_START);
		
		this.setLayout(new BorderLayout());
		this.add(geosOuterPanel, BorderLayout.PAGE_START);
	}
	
	public GEOSBitmapFontExporter createGEOSExporter() {
		if (geosFontIdManual.isSelected()) {
			if (geosFontSizeManual.isSelected()) {
				return new GEOSBitmapFontExporter(
					geosFontId.getNumber().intValue(),
					geosFontSize.getNumber().intValue()
				);
			} else {
				return new GEOSBitmapFontExporter(
					geosFontId.getNumber().intValue(),
					geosFontSizeAutoStandard.isSelected()
				);
			}
		} else {
			if (geosFontSizeManual.isSelected()) {
				return new GEOSBitmapFontExporter(
					geosFontSize.getNumber().floatValue()
				);
			} else {
				return new GEOSBitmapFontExporter(
					geosFontSizeAutoStandard.isSelected()
				);
			}
		}
	}
}
