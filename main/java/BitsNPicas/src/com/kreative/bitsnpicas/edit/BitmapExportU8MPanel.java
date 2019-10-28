package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.kreative.bitsnpicas.unicode.EncodingList;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class BitmapExportU8MPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JComboBox u8mEncoding;
	private final JCheckBox u8mHasLoadAddress;
	private final JTextField u8mLoadAddress;
	
	public BitmapExportU8MPanel() {
		this.u8mEncoding = new JComboBox(EncodingList.instance().toArray());
		this.u8mHasLoadAddress = new JCheckBox("Load Address:");
		this.u8mLoadAddress = new JTextField("$A000");
		
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
		
		this.setLayout(new BorderLayout());
		this.add(u8mInnerPanel, BorderLayout.PAGE_START);
		
		u8mHasLoadAddress.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				u8mLoadAddress.setEnabled(u8mHasLoadAddress.isSelected());
			}
		});
	}
	
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
	
	public EncodingTable getSelectedEncoding() {
		return (EncodingTable)(u8mEncoding.getSelectedItem());
	}
	
	public void setSelectedEncoding(EncodingTable enc) {
		u8mEncoding.setSelectedItem(enc);
	}
}
