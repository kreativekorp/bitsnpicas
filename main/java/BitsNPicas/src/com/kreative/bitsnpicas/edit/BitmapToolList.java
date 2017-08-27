package com.kreative.bitsnpicas.edit;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

public class BitmapToolList extends JList {
	private static final long serialVersionUID = 1L;
	
	public BitmapToolList() {
		super(BitmapTool.values());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setSelectedIndex(0);
		this.setCellRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean sel, boolean focus) {
				BitmapTool tool = (BitmapTool)value;
				JLabel label = new JLabel(new ImageIcon(tool.icon));
				Border ib = BorderFactory.createEmptyBorder(4, 6, 4, 6);
				Border ob = BorderFactory.createMatteBorder(((index == 0) ? 1 : 0), 1, 1, 1, Color.black);
				label.setBorder(BorderFactory.createCompoundBorder(ob, ib));
				label.setBackground(sel ? SystemColor.textHighlight : SystemColor.text);
				label.setForeground(sel ? SystemColor.textHighlightText : SystemColor.textText);
				label.setOpaque(true);
				return label;
			}
		});
	}
}
