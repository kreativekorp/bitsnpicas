package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.charset.Charset;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class BitmapExportFONTXPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JRadioButton singleByte;
	private final JRadioButton doubleByte;
	private final JComboBox singleByteEncoding;
	private final JComboBox doubleByteEncoding;
	
	public BitmapExportFONTXPanel() {
		this.singleByte = new JRadioButton("Single-Byte Encoding:");
		this.doubleByte = new JRadioButton("Double-Byte Encoding:");
		this.singleByteEncoding = new JComboBox(EncodingList.instance().glyphLists().toArray());
		this.doubleByteEncoding = new JComboBox(Charset.availableCharsets().keySet().toArray());
		
		singleByte.setSelected(true);
		doubleByte.setSelected(false);
		ButtonGroup bg = new ButtonGroup();
		bg.add(singleByte);
		bg.add(doubleByte);
		JPanel bp = new JPanel(new GridLayout(0, 1, 4, 4));
		bp.add(singleByte);
		bp.add(doubleByte);
		
		singleByteEncoding.setEditable(false);
		doubleByteEncoding.setEditable(false);
		singleByteEncoding.setSelectedItem(EncodingList.instance().getGlyphList("CP437"));
		doubleByteEncoding.setSelectedItem(Charset.forName("CP943").displayName());
		JPanel ep = new JPanel(new GridLayout(0, 1, 4, 4));
		ep.add(singleByteEncoding);
		ep.add(doubleByteEncoding);
		
		JPanel encodingInnerPanel = new JPanel(new BorderLayout(8, 8));
		encodingInnerPanel.add(bp, BorderLayout.LINE_START);
		encodingInnerPanel.add(ep, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(encodingInnerPanel, BorderLayout.PAGE_START);
		
		singleByteEncoding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				singleByte.setSelected(true);
			}
		});
		doubleByteEncoding.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				doubleByte.setSelected(true);
			}
		});
	}
	
	public boolean getSelectedSingleByte() {
		return singleByte.isSelected();
	}
	
	public boolean getSelectedDoubleByte() {
		return doubleByte.isSelected();
	}
	
	public GlyphList getSelectedSingleByteEncoding() {
		return (GlyphList)(singleByteEncoding.getSelectedItem());
	}
	
	public String getSelectedDoubleByteEncoding() {
		return (String)(doubleByteEncoding.getSelectedItem());
	}
}
