package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.exporter.BDFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.FZXBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.NFNTBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.RFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SBFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TOSBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TTFBitmapFontExporter;

public class BitmapExportPanel extends JPanel {
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
	private final SpinnerNumberModel pngColorRed;
	private final SpinnerNumberModel pngColorGreen;
	private final SpinnerNumberModel pngColorBlue;
	private final JButton exportButton;
	
	public BitmapExportPanel(BitmapFont font) {
		this.font = font;
		this.format = new JComboBox(Format.values());
		this.pixelWidth = new SpinnerNumberModel(100, 1, 1000, 1);
		this.pixelHeight = new SpinnerNumberModel(100, 1, 1000, 1);
		this.macFontIdAuto = new JRadioButton("Auto");
		this.macFontIdManual = new JRadioButton("Manual:");
		this.macFontId = new SpinnerNumberModel(128, 128, 32767, 1);
		this.macFontSizeAutoAny = new JRadioButton("Auto (Unrestricted)");
		this.macFontSizeAutoStandard = new JRadioButton("Auto (Standard Sizes)");
		this.macFontSizeManual = new JRadioButton("Manual:");
		this.macFontSize = new SpinnerNumberModel(12, 1, 127, 1);
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
		JPanel macControlPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		macControlPanel.add(macFontIdAuto);
		macControlPanel.add(macFontIdManualPanel);
		macControlPanel.add(macFontSizeAutoAny);
		macControlPanel.add(macFontSizeAutoStandard);
		macControlPanel.add(macFontSizeManualPanel);
		JPanel macInnerPanel = new JPanel(new BorderLayout(8, 8));
		macInnerPanel.add(macLabelPanel, BorderLayout.LINE_START);
		macInnerPanel.add(macControlPanel, BorderLayout.CENTER);
		JPanel macOuterPanel = new JPanel(new BorderLayout());
		macOuterPanel.add(macInnerPanel, BorderLayout.LINE_START);
		JPanel macPanel = new JPanel(new BorderLayout());
		macPanel.add(macOuterPanel, BorderLayout.PAGE_START);
		
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
		
		final CardLayout formatOptionsLayout = new CardLayout();
		final JPanel formatOptionsPanel = new JPanel(formatOptionsLayout);
		formatOptionsPanel.add(pixelPanel, "pixel");
		formatOptionsPanel.add(macPanel, "mac");
		formatOptionsPanel.add(pngColorPanel, "color");
		formatOptionsPanel.add(new JPanel(), "none");
		
		format.setEditable(false);
		format.setMaximumRowCount(Format.values().length);
		format.setSelectedItem(Format.TTF);
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
				Format f = (Format)format.getSelectedItem();
				formatOptionsLayout.show(formatOptionsPanel, f.cardName);
				Window c = getMyContainingWindow();
				if (c != null) c.pack();
			}
		});
		
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Format f = (Format)format.getSelectedItem();
				if (f == null) return;
				BitmapFontExporter exporter = null;
				switch (f) {
					case TTF:
						exporter = new TTFBitmapFontExporter(
							pixelWidth.getNumber().intValue(),
							pixelHeight.getNumber().intValue()
						);
						break;
					case BDF:
						exporter = new BDFBitmapFontExporter();
						break;
					case SUIT:
					case DFONT:
						if (macFontIdManual.isSelected()) {
							if (macFontSizeManual.isSelected()) {
								exporter = new NFNTBitmapFontExporter(
									macFontId.getNumber().intValue(),
									macFontSize.getNumber().intValue()
								);
							} else {
								exporter = new NFNTBitmapFontExporter(
									macFontId.getNumber().intValue(),
									macFontSizeAutoStandard.isSelected()
								);
							}
						} else {
							if (macFontSizeManual.isSelected()) {
								exporter = new NFNTBitmapFontExporter(
									macFontSize.getNumber().floatValue()
								);
							} else {
								exporter = new NFNTBitmapFontExporter(
									macFontSizeAutoStandard.isSelected()
								);
							}
						}
						break;
					case SFONT:
						exporter = new SFontBitmapFontExporter(
							(pngColorRed.getNumber().intValue() << 16) |
							(pngColorGreen.getNumber().intValue() << 8) |
							(pngColorBlue.getNumber().intValue() << 0)
						);
						break;
					case RFONT:
						exporter = new RFontBitmapFontExporter(
							(pngColorRed.getNumber().intValue() << 16) |
							(pngColorGreen.getNumber().intValue() << 8) |
							(pngColorBlue.getNumber().intValue() << 0)
						);
						break;
					case FZX:
						exporter = new FZXBitmapFontExporter();
						break;
					case SBF:
						exporter = new SBFBitmapFontExporter();
						break;
					case TOS:
						exporter = new TOSBitmapFontExporter();
						break;
				}
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
	
	private static enum Format {
		TTF("TTF (TrueType)", ".ttf", "pixel"),
		BDF("BDF (Bitmap Distribution Format)", ".bdf", "none"),
		SUIT("Mac OS Classic Font Suitcase (Resource Fork)", ".suit", "mac", true),
		DFONT("Mac OS Classic Font Suitcase (Data Fork)", ".dfont", "mac"),
		SFONT("PNG (SDL SFont)", ".png", "color"),
		RFONT("PNG (Kreative RFont)", ".png", "color"),
		FZX("FZX (ZX Spectrum)", ".fzx", "none"),
		SBF("SBF (Sabriel Font)", ".sbf", "none"),
		TOS("TOS Character Set", ".ft", "none");
		
		public final String name;
		public final String suffix;
		public final String cardName;
		public final boolean macResFork;
		
		private Format(String name, String suffix, String cardName) {
			this.name = name;
			this.suffix = suffix;
			this.cardName = cardName;
			this.macResFork = false;
		}
		
		private Format(String name, String suffix, String cardName, boolean macResFork) {
			this.name = name;
			this.suffix = suffix;
			this.cardName = cardName;
			this.macResFork = macResFork;
		}
		
		public String toString() {
			return this.name;
		}
	}
}
