package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.exporter.NFNTBitmapFontExporter;
import com.kreative.bitsnpicas.unicode.EncodingList;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class BitmapExportPanel extends JPanel implements BitmapExportOptions {
	private static final long serialVersionUID = 1L;
	
	private final BitmapFont font;
	private final JComboBox format;
	private final SpinnerNumberModel pixelWidth;
	private final SpinnerNumberModel pixelHeight;
	private final JRadioButton macFontIdAuto;
	private final JRadioButton macFontIdManual;
	private final SpinnerNumberModel macFontId;
	private final JRadioButton macFontSizeAutoAny;
	private final JRadioButton macFontSizeAutoStandard;
	private final JRadioButton macFontSizeManual;
	private final SpinnerNumberModel macFontSize;
	private final JComboBox macEncoding;
	private final JComboBox generalEncoding;
	private final JComboBox u8mEncoding;
	private final JCheckBox u8mHasLoadAddress;
	private final JTextField u8mLoadAddress;
	private final SpinnerNumberModel pngColorRed;
	private final SpinnerNumberModel pngColorGreen;
	private final SpinnerNumberModel pngColorBlue;
	private final JButton exportButton;
	
	public BitmapExportPanel(BitmapFont font) {
		this.font = font;
		this.format = new JComboBox(BitmapExportFormat.values());
		this.pixelWidth = new SpinnerNumberModel(100, 1, 1000, 1);
		this.pixelHeight = new SpinnerNumberModel(100, 1, 1000, 1);
		this.macFontIdAuto = new JRadioButton("Auto");
		this.macFontIdManual = new JRadioButton("Manual:");
		this.macFontId = new SpinnerNumberModel(128, 128, 32767, 1);
		this.macFontSizeAutoAny = new JRadioButton("Auto (Unrestricted)");
		this.macFontSizeAutoStandard = new JRadioButton("Auto (Standard Sizes)");
		this.macFontSizeManual = new JRadioButton("Manual:");
		this.macFontSize = new SpinnerNumberModel(12, 1, 127, 1);
		this.macEncoding = new JComboBox(EncodingList.instance().toArray());
		this.generalEncoding = new JComboBox(EncodingList.instance().toArray());
		this.u8mEncoding = new JComboBox(EncodingList.instance().toArray());
		this.u8mHasLoadAddress = new JCheckBox("Load Address:");
		this.u8mLoadAddress = new JTextField("$A000");
		this.pngColorRed = new SpinnerNumberModel(0, 0, 255, 1);
		this.pngColorGreen = new SpinnerNumberModel(0, 0, 255, 1);
		this.pngColorBlue = new SpinnerNumberModel(0, 0, 255, 1);
		this.exportButton = new JButton("Export");
		
		JPanel pixelLabelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		pixelLabelPanel.add(new JLabel("Pixel Width"));
		pixelLabelPanel.add(new JLabel("Pixel Height"));
		JPanel pixelSpinnerPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		pixelSpinnerPanel.add(new JSpinner(pixelWidth));
		pixelSpinnerPanel.add(new JSpinner(pixelHeight));
		JPanel pixelInnerPanel = new JPanel(new BorderLayout(8, 8));
		pixelInnerPanel.add(pixelLabelPanel, BorderLayout.LINE_START);
		pixelInnerPanel.add(pixelSpinnerPanel, BorderLayout.CENTER);
		JPanel pixelOuterPanel = new JPanel(new BorderLayout());
		pixelOuterPanel.add(pixelInnerPanel, BorderLayout.LINE_START);
		JPanel pixelPanel = new JPanel(new BorderLayout());
		pixelPanel.add(pixelOuterPanel, BorderLayout.PAGE_START);
		
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
		JPanel macPanel = new JPanel(new BorderLayout());
		macPanel.add(macOuterPanel, BorderLayout.PAGE_START);
		
		generalEncoding.setEditable(false);
		JPanel encodingInnerPanel = new JPanel(new BorderLayout(8, 8));
		encodingInnerPanel.add(new JLabel("Encoding"), BorderLayout.LINE_START);
		encodingInnerPanel.add(generalEncoding, BorderLayout.CENTER);
		JPanel encodingPanel = new JPanel(new BorderLayout());
		encodingPanel.add(encodingInnerPanel, BorderLayout.PAGE_START);
		
		u8mEncoding.setEditable(false);
		u8mLoadAddress.setEnabled(false);
		JPanel u8mLabelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		u8mLabelPanel.add(u8mHasLoadAddress);
		u8mLabelPanel.add(new JLabel("Native Encoding:"));
		JPanel u8mLoadAddressPanel = new JPanel(new BorderLayout());
		u8mLoadAddressPanel.add(u8mLoadAddress, BorderLayout.LINE_START);
		JPanel u8mControlPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		u8mControlPanel.add(u8mLoadAddressPanel);
		u8mControlPanel.add(u8mEncoding);
		JPanel u8mInnerPanel = new JPanel(new BorderLayout(8, 8));
		u8mInnerPanel.add(u8mLabelPanel, BorderLayout.LINE_START);
		u8mInnerPanel.add(u8mControlPanel, BorderLayout.CENTER);
		JPanel u8mPanel = new JPanel(new BorderLayout());
		u8mPanel.add(u8mInnerPanel, BorderLayout.PAGE_START);
		
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
		JPanel pngColorPanel = new JPanel(new BorderLayout());
		pngColorPanel.add(pngColorOuterPanel, BorderLayout.PAGE_START);
		
		JLabel noneLabel = new JLabel("This format has no options.");
		noneLabel.setHorizontalAlignment(JLabel.CENTER);
		JPanel nonePanel = new JPanel(new BorderLayout());
		nonePanel.add(noneLabel, BorderLayout.CENTER);
		
		final CardLayout formatOptionsLayout = new CardLayout();
		final JPanel formatOptionsPanel = new JPanel(formatOptionsLayout);
		formatOptionsPanel.add(pixelPanel, "pixel");
		formatOptionsPanel.add(macPanel, "mac");
		formatOptionsPanel.add(encodingPanel, "encoding");
		formatOptionsPanel.add(u8mPanel, "u8m");
		formatOptionsPanel.add(pngColorPanel, "color");
		formatOptionsPanel.add(nonePanel, "none");
		
		format.setEditable(false);
		format.setMaximumRowCount(BitmapExportFormat.values().length);
		format.setSelectedItem(BitmapExportFormat.TTF);
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(exportButton);
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.add(format, BorderLayout.PAGE_START);
		mainPanel.add(formatOptionsPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		
		format.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				BitmapExportFormat f = (BitmapExportFormat)format.getSelectedItem();
				formatOptionsLayout.show(formatOptionsPanel, f.cardName);
				EncodingTable enc = (
					(f.defaultEncodingName == null) ? null :
					EncodingList.instance().get(f.defaultEncodingName)
				);
				macEncoding.setSelectedItem(enc);
				generalEncoding.setSelectedItem(enc);
				u8mEncoding.setSelectedItem(enc);
				Window c = getMyContainingWindow();
				if (c != null) c.pack();
			}
		});
		
		u8mHasLoadAddress.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				u8mLoadAddress.setEnabled(u8mHasLoadAddress.isSelected());
			}
		});
		
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BitmapExportFormat f = (BitmapExportFormat)format.getSelectedItem();
				if (f == null) return;
				BitmapFontExporter exporter = f.createExporter(BitmapExportPanel.this);
				if (exporter == null) return;
				File file = Main.getSaveFile(f.suffix);
				if (file == null) return;
				if (f.macResFork) {
					try { file.createNewFile(); }
					catch (IOException ioe) {}
					file = new File(file, "..namedfork");
					file = new File(file, "rsrc");
				}
				if (Main.saveFont(file, exporter, BitmapExportPanel.this.font)) {
					if (f.macResFork) file = file.getParentFile().getParentFile();
					try { f.postProcess(file); }
					catch (IOException ioe) {}
					Window c = getMyContainingWindow();
					if (c != null) c.dispose();
				}
			}
		});
	}
	
	private Window getMyContainingWindow() {
		Component c = getParent();
		while (c != null) {
			if (c instanceof Window) {
				return ((Window)c);
			} else {
				c = c.getParent();
			}
		}
		return null;
	}
	
	@Override
	public Dimension getPixelDimension() {
		return new Dimension(
			pixelWidth.getNumber().intValue(),
			pixelHeight.getNumber().intValue()
		);
	}
	
	@Override
	public int getSelectedColor() {
		return (
			(pngColorRed.getNumber().intValue() << 16) |
			(pngColorGreen.getNumber().intValue() << 8) |
			(pngColorBlue.getNumber().intValue() << 0)
		);
	}
	
	@Override
	public Integer getLoadAddress() {
		if (u8mHasLoadAddress.isSelected()) {
			String s = u8mLoadAddress.getText();
			try {
				if (s.startsWith("0X") || s.startsWith("0x")) return Integer.parseInt(s.substring(2), 16);
				if (s.startsWith("$")) return Integer.parseInt(s.substring(1), 16);
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {}
		}
		return null;
	}
	
	@Override
	public EncodingTable getSelectedEncoding() {
		BitmapExportFormat f = (BitmapExportFormat)format.getSelectedItem();
		if (f.cardName.equals("mac")) return (EncodingTable)(macEncoding.getSelectedItem());
		if (f.cardName.equals("u8m")) return (EncodingTable)(u8mEncoding.getSelectedItem());
		return (EncodingTable)(generalEncoding.getSelectedItem());
	}
	
	@Override
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
