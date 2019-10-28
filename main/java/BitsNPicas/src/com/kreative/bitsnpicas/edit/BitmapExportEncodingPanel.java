package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.unicode.EncodingList;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class BitmapExportEncodingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JComboBox generalEncoding;
	
	public BitmapExportEncodingPanel() {
		this.generalEncoding = new JComboBox(EncodingList.instance().toArray());
		
		generalEncoding.setEditable(false);
		JPanel encodingInnerPanel = new JPanel(new BorderLayout(8, 8));
		encodingInnerPanel.add(new JLabel("Encoding"), BorderLayout.LINE_START);
		encodingInnerPanel.add(generalEncoding, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(encodingInnerPanel, BorderLayout.PAGE_START);
	}
	
	public EncodingTable getSelectedEncoding() {
		return (EncodingTable)(generalEncoding.getSelectedItem());
	}
	
	public void setSelectedEncoding(EncodingTable enc) {
		generalEncoding.setSelectedItem(enc);
	}
}
