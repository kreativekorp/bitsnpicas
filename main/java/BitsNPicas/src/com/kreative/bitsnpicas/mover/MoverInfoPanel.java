package com.kreative.bitsnpicas.mover;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.unicode.EncodingList;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class MoverInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JLabel iconLabel;
	private final JLabel fileNameLabel;
	private final JLabel kindLabel;
	private final JComboBox encoding;
	
	public MoverInfoPanel(File file) {
		ImageIcon icon = MoverIcons.getFileIcon(file);
		this.iconLabel = new JLabel(icon);
		this.fileNameLabel = new JLabel(file.getName());
		this.kindLabel = new JLabel(icon.getDescription());
		this.encoding = new JComboBox(EncodingList.instance().toArray());
		encoding.setEditable(false);
		encoding.setSelectedItem(EncodingList.instance().get("MacRoman"));
		kindLabel.setFont(kindLabel.getFont().deriveFont(Font.PLAIN));
		fileNameLabel.setFont(fileNameLabel.getFont().deriveFont(Font.BOLD));
		
		JPanel encodingPanel = new JPanel(new BorderLayout(8,8));
		encodingPanel.add(new JLabel("Encoding:"), BorderLayout.LINE_START);
		encodingPanel.add(encoding, BorderLayout.CENTER);
		
		JPanel encodingOuterPanel = new JPanel();
		encodingOuterPanel.setLayout(new BoxLayout(encodingOuterPanel, BoxLayout.PAGE_AXIS));
		encodingOuterPanel.add(Box.createVerticalGlue());
		encodingOuterPanel.add(encodingPanel);
		encodingOuterPanel.add(Box.createVerticalGlue());
		
		JPanel labelPanel = new JPanel(new GridLayout(0,1,4,4));
		labelPanel.add(fileNameLabel);
		labelPanel.add(kindLabel);
		
		JPanel mainPanel = new JPanel(new BorderLayout(12,12));
		mainPanel.add(iconLabel, BorderLayout.LINE_START);
		mainPanel.add(labelPanel, BorderLayout.CENTER);
		mainPanel.add(encodingOuterPanel, BorderLayout.LINE_END);
		
		setLayout(new GridLayout());
		add(mainPanel);
	}
	
	public void setFile(File file) {
		ImageIcon icon = MoverIcons.getFileIcon(file);
		iconLabel.setIcon(icon);
		fileNameLabel.setText(file.getName());
		kindLabel.setText(icon.getDescription());
	}
	
	public EncodingTable getSelectedEncoding() {
		return (EncodingTable)(encoding.getSelectedItem());
	}
	
	public void setSelectedEncoding(EncodingTable enc) {
		encoding.setSelectedItem(enc);
	}
	
	public boolean readOnly() {
		return !((ImageIcon)iconLabel.getIcon()).getDescription().contains("suitcase");
	}
}
