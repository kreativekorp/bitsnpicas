package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class BitmapExportPanel extends JPanel implements BitmapExportOptions {
	private static final long serialVersionUID = 1L;
	
	private final BitmapFont font;
	private final JComboBox format;
	private final BitmapExportPixelPanel pixelPanel;
	private final BitmapExportGEOSPanel geosPanel;
	private final BitmapExportMacPanel macPanel;
	private final BitmapExportEncodingPanel encodingPanel;
	private final BitmapExportFONTXPanel fontxPanel;
	private final BitmapExportU8MPanel u8mPanel;
	private final BitmapExportColorPanel colorPanel;
	private final BitmapExportPSFPanel psfPanel;
	private final BitmapExportPlaydatePanel playdatePanel;
	private final JButton exportButton;
	
	public BitmapExportPanel(BitmapFont font) {
		this.font = font;
		this.format = new JComboBox(BitmapExportFormat.values());
		this.pixelPanel = new BitmapExportPixelPanel();
		this.geosPanel = new BitmapExportGEOSPanel();
		this.macPanel = new BitmapExportMacPanel();
		this.encodingPanel = new BitmapExportEncodingPanel();
		this.fontxPanel = new BitmapExportFONTXPanel();
		this.u8mPanel = new BitmapExportU8MPanel();
		this.colorPanel = new BitmapExportColorPanel();
		this.psfPanel = new BitmapExportPSFPanel();
		this.playdatePanel = new BitmapExportPlaydatePanel();
		this.exportButton = new JButton("Export");
		
		JLabel noneLabel = new JLabel("This format has no options.");
		noneLabel.setHorizontalAlignment(JLabel.CENTER);
		JPanel nonePanel = new JPanel(new BorderLayout());
		nonePanel.add(noneLabel, BorderLayout.CENTER);
		
		final CardLayout formatOptionsLayout = new CardLayout();
		final JPanel formatOptionsPanel = new JPanel(formatOptionsLayout);
		formatOptionsPanel.add(pixelPanel, "pixel");
		formatOptionsPanel.add(geosPanel, "geos");
		formatOptionsPanel.add(macPanel, "mac");
		formatOptionsPanel.add(encodingPanel, "encoding");
		formatOptionsPanel.add(fontxPanel, "fontx");
		formatOptionsPanel.add(u8mPanel, "u8m");
		formatOptionsPanel.add(colorPanel, "color");
		formatOptionsPanel.add(psfPanel, "psf");
		formatOptionsPanel.add(playdatePanel, "playdate");
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
				GlyphList enc = (
					(f.defaultEncodingName == null) ? null :
					EncodingList.instance().getGlyphList(f.defaultEncodingName)
				);
				macPanel.setSelectedEncoding(enc);
				encodingPanel.setSelectedEncoding(enc);
				u8mPanel.setSelectedEncoding(enc);
				Window c = getMyContainingWindow();
				if (c != null) c.pack();
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
		return pixelPanel.getPixelDimension();
	}
	
	@Override
	public boolean getExtendWinMetrics() {
		return pixelPanel.getExtendWinMetrics();
	}
	
	@Override
	public int getSelectedColor() {
		return colorPanel.getSelectedColor();
	}
	
	@Override
	public Integer getLoadAddress() {
		return u8mPanel.getLoadAddress();
	}
	
	@Override
	public GlyphList getSelectedEncoding() {
		BitmapExportFormat f = (BitmapExportFormat)format.getSelectedItem();
		if (f.cardName.equals("mac")) return macPanel.getSelectedEncoding();
		if (f.cardName.equals("u8m")) return u8mPanel.getSelectedEncoding();
		if (f.cardName.equals("fontx")) return fontxPanel.getSelectedSingleByteEncoding();
		return encodingPanel.getSelectedEncoding();
	}
	
	@Override
	public IDGenerator getIDGenerator() {
		BitmapExportFormat f = (BitmapExportFormat)format.getSelectedItem();
		if (f.cardName.equals("geos")) return geosPanel.getIDGenerator();
		return macPanel.getIDGenerator();
	}
	
	@Override
	public PointSizeGenerator getPointSizeGenerator() {
		BitmapExportFormat f = (BitmapExportFormat)format.getSelectedItem();
		if (f.cardName.equals("geos")) return geosPanel.getPointSizeGenerator();
		return macPanel.getPointSizeGenerator();
	}
	
	@Override
	public boolean getGEOSMega() {
		return geosPanel.getGEOSMega();
	}
	
	@Override
	public boolean getGEOSKerning() {
		return geosPanel.getGEOSKerning();
	}
	
	@Override
	public boolean getGEOSUTF8() {
		return geosPanel.getGEOSUTF8();
	}
	
	@Override
	public boolean getFONTXDoubleByte() {
		return fontxPanel.getSelectedDoubleByte();
	}
	
	@Override
	public String getFONTXDoubleByteEncoding() {
		return fontxPanel.getSelectedDoubleByteEncoding();
	}
	
	@Override
	public int getPSFVersion() {
		return psfPanel.getVersion();
	}
	
	@Override
	public GlyphList getPSFLowEncoding() {
		return psfPanel.getLowEncoding();
	}
	
	@Override
	public GlyphList getPSFHighEncoding() {
		return psfPanel.getHighEncoding();
	}
	
	@Override
	public boolean getPSFUseLowEncoding() {
		return psfPanel.getUseLowEncoding();
	}
	
	@Override
	public boolean getPSFUseHighEncoding() {
		return psfPanel.getUseHighEncoding();
	}
	
	@Override
	public boolean getPSFUseAllGlyphs() {
		return psfPanel.getUseAllGlyphs();
	}
	
	@Override
	public boolean getPSFUnicodeTable() {
		return psfPanel.getUnicodeTable();
	}
	
	@Override
	public boolean getPlaydateSeparate() {
		return playdatePanel.getSeparate();
	}
}
