package com.kreative.bitsnpicas.edit.exporter;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class BitmapExportEncodingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JComboBox generalEncoding;
	
	public BitmapExportEncodingPanel() {
		this.generalEncoding = new JComboBox(EncodingList.instance().glyphLists().toArray());
		
		generalEncoding.setEditable(false);
		JPanel encodingInnerPanel = new JPanel(new BorderLayout(8, 8));
		encodingInnerPanel.add(new JLabel("Encoding"), BorderLayout.LINE_START);
		encodingInnerPanel.add(generalEncoding, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(encodingInnerPanel, BorderLayout.PAGE_START);
	}
	
	public GlyphList getSelectedEncoding() {
		return (GlyphList)(generalEncoding.getSelectedItem());
	}
	
	public void setSelectedEncoding(GlyphList enc) {
		generalEncoding.setSelectedItem(enc);
	}
}
