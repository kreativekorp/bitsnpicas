package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import com.kreative.bitsnpicas.exporter.NFNTBitmapFontExporter;
import com.kreative.bitsnpicas.unicode.EncodingList;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class BitmapExportMacPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JRadioButton macFontIdAuto;
	private final JRadioButton macFontIdManual;
	private final SpinnerNumberModel macFontId;
	private final JRadioButton macFontSizeAutoAny;
	private final JRadioButton macFontSizeAutoStandard;
	private final JRadioButton macFontSizeManual;
	private final SpinnerNumberModel macFontSize;
	private final JComboBox macEncoding;
	
	public BitmapExportMacPanel() {
		this.macFontIdAuto = new JRadioButton("Auto");
		this.macFontIdManual = new JRadioButton("Manual:");
		this.macFontId = new SpinnerNumberModel(128, 128, 32767, 1);
		this.macFontSizeAutoAny = new JRadioButton("Auto (Unrestricted)");
		this.macFontSizeAutoStandard = new JRadioButton("Auto (Standard Sizes)");
		this.macFontSizeManual = new JRadioButton("Manual:");
		this.macFontSize = new SpinnerNumberModel(12, 1, 127, 1);
		this.macEncoding = new JComboBox(EncodingList.instance().toArray());
		
		macFontIdAuto.setSelected(true);
		macFontSizeAutoAny.setSelected(true);
		macEncoding.setEditable(false);
		ButtonGroup macFontIdGroup = new ButtonGroup();
		macFontIdGroup.add(macFontIdAuto);
		macFontIdGroup.add(macFontIdManual);
		ButtonGroup macFontSizeGroup = new ButtonGroup();
		macFontSizeGroup.add(macFontSizeAutoAny);
		macFontSizeGroup.add(macFontSizeAutoStandard);
		macFontSizeGroup.add(macFontSizeManual);
		JPanel macFontIdManualPanel = new JPanel(new BorderLayout(8, 8));
		macFontIdManualPanel.add(macFontIdManual, BorderLayout.LINE_START);
		macFontIdManualPanel.add(new JSpinner(macFontId), BorderLayout.CENTER);
		JPanel macFontSizeManualPanel = new JPanel(new BorderLayout(8, 8));
		macFontSizeManualPanel.add(macFontSizeManual, BorderLayout.LINE_START);
		macFontSizeManualPanel.add(new JSpinner(macFontSize), BorderLayout.CENTER);
		JPanel macLabelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		macLabelPanel.add(new JLabel("Macintosh Font ID"));
		macLabelPanel.add(new JLabel(" "));
		macLabelPanel.add(new JLabel("Macintosh Font Size"));
		macLabelPanel.add(new JLabel(" "));
		macLabelPanel.add(new JLabel(" "));
		macLabelPanel.add(new JLabel("Encoding"));
		JPanel macControlPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		macControlPanel.add(macFontIdAuto);
		macControlPanel.add(macFontIdManualPanel);
		macControlPanel.add(macFontSizeAutoAny);
		macControlPanel.add(macFontSizeAutoStandard);
		macControlPanel.add(macFontSizeManualPanel);
		macControlPanel.add(macEncoding);
		JPanel macInnerPanel = new JPanel(new BorderLayout(8, 8));
		macInnerPanel.add(macLabelPanel, BorderLayout.LINE_START);
		macInnerPanel.add(macControlPanel, BorderLayout.CENTER);
		JPanel macOuterPanel = new JPanel(new BorderLayout());
		macOuterPanel.add(macInnerPanel, BorderLayout.LINE_START);
		
		this.setLayout(new BorderLayout());
		this.add(macOuterPanel, BorderLayout.PAGE_START);
	}
	
	public EncodingTable getSelectedEncoding() {
		return (EncodingTable)(macEncoding.getSelectedItem());
	}
	
	public void setSelectedEncoding(EncodingTable enc) {
		macEncoding.setSelectedItem(enc);
	}
	
	public NFNTBitmapFontExporter createNFNTExporter() {
		if (macFontIdManual.isSelected()) {
			if (macFontSizeManual.isSelected()) {
				return new NFNTBitmapFontExporter(
					macFontId.getNumber().intValue(),
					macFontSize.getNumber().intValue(),
					(EncodingTable)(macEncoding.getSelectedItem())
				);
			} else {
				return new NFNTBitmapFontExporter(
					macFontId.getNumber().intValue(),
					macFontSizeAutoStandard.isSelected(),
					(EncodingTable)(macEncoding.getSelectedItem())
				);
			}
		} else {
			if (macFontSizeManual.isSelected()) {
				return new NFNTBitmapFontExporter(
					macFontSize.getNumber().floatValue(),
					(EncodingTable)(macEncoding.getSelectedItem())
				);
			} else {
				return new NFNTBitmapFontExporter(
					macFontSizeAutoStandard.isSelected(),
					(EncodingTable)(macEncoding.getSelectedItem())
				);
			}
		}
	}
}
