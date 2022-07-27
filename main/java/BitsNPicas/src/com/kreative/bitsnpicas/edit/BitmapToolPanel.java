package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BitmapToolPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final BitmapToolList toolList;
	private final JLabel opacityLabel;
	private final JSlider opacitySlider;
	
	public BitmapToolPanel() {
		this.toolList = new BitmapToolList();
		this.opacityLabel = new JLabel("255");
		this.opacitySlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
		
		opacityLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		opacityLabel.setHorizontalAlignment(JLabel.CENTER);
		opacityLabel.setHorizontalTextPosition(JLabel.CENTER);
		opacityLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		opacityLabel.setFont(opacityLabel.getFont().deriveFont(10f));
		
		opacitySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				opacityLabel.setText(Integer.toString(opacitySlider.getValue()));
			}
		});
		
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(opacityLabel, BorderLayout.PAGE_START);
		p1.add(opacitySlider, BorderLayout.CENTER);
		p1.setToolTipText("Opacity");
		
		setLayout(new BorderLayout());
		add(toolList, BorderLayout.PAGE_START);
		add(p1, BorderLayout.CENTER);
	}
	
	public BitmapTool getSelectedTool() {
		return (BitmapTool)toolList.getSelectedValue();
	}
	
	public void setSelectedTool(BitmapTool tool) {
		toolList.setSelectedValue(tool, true);
	}
	
	public int getOpacity() {
		return opacitySlider.getValue();
	}
	
	public void setOpacity(int opacity) {
		opacitySlider.setValue(opacity);
	}
}
