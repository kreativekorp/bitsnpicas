package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class BitmapExportPlaydatePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JRadioButton allInOne;
	private final JRadioButton separate;
	
	public BitmapExportPlaydatePanel() {
		this.allInOne = new JRadioButton("All-in-one (fnt)");
		this.separate = new JRadioButton("Separate (fnt+png)");
		this.allInOne.setSelected(true);
		this.separate.setSelected(false);
		ButtonGroup bg = new ButtonGroup();
		bg.add(allInOne);
		bg.add(separate);
		JPanel bp = new JPanel(new GridLayout(0, 1, 4, 4));
		bp.add(allInOne);
		bp.add(separate);
		this.setLayout(new BorderLayout());
		this.add(bp, BorderLayout.PAGE_START);
	}
	
	public boolean getSeparate() {
		return separate.isSelected();
	}
}
