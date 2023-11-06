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
import com.kreative.bitsnpicas.edit.glmlicon.GLMLListCellRenderer;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class MoverInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JLabel iconLabel;
	private final JLabel fileNameLabel;
	private final JLabel kindLabel;
	private final JComboBox encoding;
	
	public MoverInfoPanel(File file) {
		ImageIcon icon;
		String fileName;
		if (file == null) {
			icon = MoverIcons.SUITCASE_FONT_32;
			fileName = "Untitled Suitcase";
		} else {
			icon = MoverIcons.getFileIcon(file);
			fileName = file.getName();
		}
		
		this.iconLabel = new JLabel(icon);
		this.fileNameLabel = new JLabel(fileName);
		this.kindLabel = new JLabel(icon.getDescription());
		this.encoding = new JComboBox(EncodingList.instance().glyphLists().toArray());
		encoding.setEditable(false);
		new GLMLListCellRenderer("encoding").apply(encoding);
		encoding.setSelectedItem(EncodingList.instance().getGlyphList("MacRoman"));
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
		ImageIcon icon;
		String fileName;
		if (file == null) {
			icon = MoverIcons.SUITCASE_FONT_32;
			fileName = "Untitled Suitcase";
		} else {
			icon = MoverIcons.getFileIcon(file);
			fileName = file.getName();
		}
		
		iconLabel.setIcon(icon);
		fileNameLabel.setText(fileName);
		kindLabel.setText(icon.getDescription());
	}
	
	public GlyphList getSelectedEncoding() {
		return (GlyphList)(encoding.getSelectedItem());
	}
	
	public void setSelectedEncoding(GlyphList enc) {
		encoding.setSelectedItem(enc);
	}
	
	public boolean readOnly() {
		return !((ImageIcon)iconLabel.getIcon()).getDescription().contains("suitcase");
	}
}
