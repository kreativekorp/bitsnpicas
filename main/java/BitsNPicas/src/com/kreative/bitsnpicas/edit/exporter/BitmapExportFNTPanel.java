package com.kreative.bitsnpicas.edit.exporter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import com.kreative.bitsnpicas.edit.glmlicon.GLMLListCellRenderer;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class BitmapExportFNTPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JComboBox fntEncoding;
	private final SpinnerNumberModel fntAverageWidth;
	
	public BitmapExportFNTPanel() {
		this.fntEncoding = new JComboBox(EncodingList.instance().glyphLists().toArray());
		this.fntAverageWidth = new SpinnerNumberModel(0, 0, 65535, 1);
		
		fntEncoding.setEditable(false);
		new GLMLListCellRenderer("encoding").apply(fntEncoding);
		JPanel fntLabelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		fntLabelPanel.add(new JLabel("Encoding"));
		fntLabelPanel.add(new JLabel("Average Width (0 = Auto)"));
		JPanel fntControlPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		fntControlPanel.add(fntEncoding);
		fntControlPanel.add(new JSpinner(fntAverageWidth));
		JPanel fntInnerPanel = new JPanel(new BorderLayout(8, 8));
		fntInnerPanel.add(fntLabelPanel, BorderLayout.LINE_START);
		fntInnerPanel.add(fntControlPanel, BorderLayout.CENTER);
		JPanel fntOuterPanel = new JPanel(new BorderLayout());
		fntOuterPanel.add(fntInnerPanel, BorderLayout.LINE_START);
		
		this.setLayout(new BorderLayout());
		this.add(fntOuterPanel, BorderLayout.PAGE_START);
	}
	
	public GlyphList getSelectedEncoding() {
		return (GlyphList)(fntEncoding.getSelectedItem());
	}
	
	public void setSelectedEncoding(GlyphList enc) {
		fntEncoding.setSelectedItem(enc);
	}
	
	public int getAverageWidth() {
		return fntAverageWidth.getNumber().intValue();
	}
}
