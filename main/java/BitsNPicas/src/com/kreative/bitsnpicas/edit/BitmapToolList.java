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
	
	private static BitmapTool currentTool = BitmapTool.BRUSH;
	
	public BitmapToolList() {
		super(BitmapTool.values());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//this.setSelectedIndex(0);
		this.setFocusable(false);
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
		super.setSelectedValue(currentTool, true);
		addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                BitmapTool selected = (BitmapTool)this.getSelectedValue();
                if (selected != null) {
                    currentTool = selected;
                }
            }
        });
	}

	@Override
    public void setSelectedValue(Object tool, boolean scroll) {
		if (tool instanceof BitmapTool) {
	    	currentTool = (BitmapTool) tool;
		}
		super.setSelectedValue(tool, scroll);
	}
    public void setSelectedValue(BitmapTool tool, boolean scroll) {
    	currentTool = (BitmapTool) tool;
		super.setSelectedValue((Object) tool, scroll);
	}
}
