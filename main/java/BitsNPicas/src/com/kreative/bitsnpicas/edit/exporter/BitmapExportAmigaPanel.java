package com.kreative.bitsnpicas.edit.exporter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.edit.glmlicon.GLMLListCellRenderer;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class BitmapExportAmigaPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JComboBox amigaEncoding;
	private final JComboBox amigaProportional;
	
	public BitmapExportAmigaPanel() {
		this.amigaEncoding = new JComboBox(EncodingList.instance().glyphLists().toArray());
		this.amigaProportional = new JComboBox(new String[]{"Auto", "No (Monospaced)", "Yes (Proportional)"});
		
		amigaEncoding.setEditable(false);
		new GLMLListCellRenderer("encoding").apply(amigaEncoding);
		JPanel amigaLabelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		amigaLabelPanel.add(new JLabel("Encoding"));
		amigaLabelPanel.add(new JLabel("Proportional"));
		JPanel amigaControlPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		amigaControlPanel.add(amigaEncoding);
		amigaControlPanel.add(amigaProportional);
		JPanel amigaInnerPanel = new JPanel(new BorderLayout(8, 8));
		amigaInnerPanel.add(amigaLabelPanel, BorderLayout.LINE_START);
		amigaInnerPanel.add(amigaControlPanel, BorderLayout.CENTER);
		JPanel amigaOuterPanel = new JPanel(new BorderLayout());
		amigaOuterPanel.add(amigaInnerPanel, BorderLayout.LINE_START);
		
		this.setLayout(new BorderLayout());
		this.add(amigaOuterPanel, BorderLayout.PAGE_START);
	}
	
	public GlyphList getSelectedEncoding() {
		return (GlyphList)(amigaEncoding.getSelectedItem());
	}
	
	public void setSelectedEncoding(GlyphList enc) {
		amigaEncoding.setSelectedItem(enc);
	}
	
	public Boolean getAmigaProportional() {
		switch (amigaProportional.getSelectedIndex()) {
			default: return null;
			case 1: return false;
			case 2: return true;
		}
	}
}
