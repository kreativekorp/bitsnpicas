package com.kreative.bitsnpicas.edit.exporter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class BitmapExportPSFPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JRadioButton version1;
	private final JRadioButton version2;
	private final JComboBox lowEncoding;
	private final JComboBox highEncoding;
	private final JCheckBox useLowEncoding;
	private final JCheckBox useHighEncoding;
	private final JCheckBox useAllGlyphs;
	private final JCheckBox unicodeTable;
	
	public BitmapExportPSFPanel() {
		this.version1 = new JRadioButton("PSF1");
		this.version2 = new JRadioButton("PSF2");
		this.version1.setSelected(false);
		this.version2.setSelected(true);
		ButtonGroup vbg = new ButtonGroup();
		vbg.add(version1);
		vbg.add(version2);
		JPanel vp1 = new JPanel(new GridLayout(1, 0, 8, 8));
		vp1.add(version1);
		vp1.add(version2);
		JPanel vp2 = new JPanel(new BorderLayout());
		vp2.add(vp1, BorderLayout.LINE_START);
		
		ArrayList<Object> lea = new ArrayList<Object>();
		lea.add("U+0000 - U+00FF");
		lea.addAll(EncodingList.instance().glyphLists());
		this.lowEncoding = new JComboBox(lea.toArray());
		this.lowEncoding.setEditable(false);
		
		ArrayList<Object> hea = new ArrayList<Object>();
		hea.add("U+0100 - U+01FF");
		hea.addAll(EncodingList.instance().glyphLists());
		this.highEncoding = new JComboBox(hea.toArray());
		this.highEncoding.setEditable(false);
		
		this.useLowEncoding = new JCheckBox("Include $000-$0FF");
		this.useHighEncoding = new JCheckBox("Include $100-$1FF");
		this.useAllGlyphs = new JCheckBox("Include all characters");
		this.unicodeTable = new JCheckBox("Add Unicode mapping table");
		this.useLowEncoding.setSelected(true);
		this.useHighEncoding.setSelected(false);
		this.useAllGlyphs.setSelected(true);
		this.unicodeTable.setSelected(true);
		
		JPanel labelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		labelPanel.add(new JLabel("Version"));
		labelPanel.add(useLowEncoding);
		labelPanel.add(useHighEncoding);
		JPanel popupPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		popupPanel.add(vp2);
		popupPanel.add(lowEncoding);
		popupPanel.add(highEncoding);
		JPanel upperPanel = new JPanel(new BorderLayout(8, 8));
		upperPanel.add(labelPanel, BorderLayout.LINE_START);
		upperPanel.add(popupPanel, BorderLayout.CENTER);
		JPanel lowerPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		lowerPanel.add(useAllGlyphs);
		lowerPanel.add(unicodeTable);
		lowerPanel.add(new JLabel());
		JPanel mainPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		mainPanel.add(upperPanel);
		mainPanel.add(lowerPanel);
		
		this.setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.PAGE_START);
		
		version1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				useLowEncoding.setSelected(true);
				useLowEncoding.setEnabled(false);
				useAllGlyphs.setSelected(false);
				useAllGlyphs.setEnabled(false);
			}
		});
		version2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				useLowEncoding.setSelected(true);
				useLowEncoding.setEnabled(true);
				useAllGlyphs.setSelected(true);
				useAllGlyphs.setEnabled(true);
			}
		});
		lowEncoding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				useLowEncoding.setSelected(true);
			}
		});
		highEncoding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				useHighEncoding.setSelected(true);
			}
		});
	}
	
	public int getVersion() {
		return version1.isSelected() ? 1 : 2;
	}
	
	public GlyphList getLowEncoding() {
		if (lowEncoding.getSelectedIndex() <= 0) return null;
		return (GlyphList)lowEncoding.getSelectedItem();
	}
	
	public GlyphList getHighEncoding() {
		if (highEncoding.getSelectedIndex() <= 0) return null;
		return (GlyphList)highEncoding.getSelectedItem();
	}
	
	public boolean getUseLowEncoding() {
		return useLowEncoding.isSelected();
	}
	
	public boolean getUseHighEncoding() {
		return useHighEncoding.isSelected();
	}
	
	public boolean getUseAllGlyphs() {
		return useAllGlyphs.isSelected();
	}
	
	public boolean getUnicodeTable() {
		return unicodeTable.isSelected();
	}
}
