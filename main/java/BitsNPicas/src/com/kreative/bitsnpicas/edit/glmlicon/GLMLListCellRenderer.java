package com.kreative.bitsnpicas.edit.glmlicon;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import com.kreative.unicode.data.Encoding;
import com.kreative.unicode.data.GlyphList;

public class GLMLListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private final String group;
	
	public GLMLListCellRenderer() {
		this.group = null;
	}
	
	public GLMLListCellRenderer(String group) {
		this.group = group;
	}
	
	public void apply(JComboBox c) {
		Dimension d1 = c.getPreferredSize();
		c.setRenderer(this);
		Dimension d2 = c.getPreferredSize();
		int width = Math.max(d1.width, d2.width);
		int height = Math.max(d1.height, d2.height);
		c.setPreferredSize(new Dimension(width, height));
	}
	
	public Component getListCellRendererComponent(
		JList list, Object value, int index, boolean sel, boolean focus
	) {
		Component c = super.getListCellRendererComponent(list, value, index, sel, focus);
		if (c instanceof JLabel) {
			JLabel label = (JLabel)c;
			label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
			label.setIcon(new ImageIcon(getImageForListCell(value)));
		}
		return c;
	}
	
	private Image getImageForListCell(Object value) {
		if (value instanceof GlyphList) {
			String group = (this.group != null) ? this.group : "glyphlist";
			String name = ((GlyphList)value).getName();
			return GLMLResources.getImage(group, name);
		}
		if (value instanceof Encoding) {
			String group = (this.group != null) ? this.group : "encoding";
			String name = ((Encoding)value).getName();
			return GLMLResources.getImage(group, name);
		}
		return GLMLResources.getImage(this.group, value.toString());
	}
}
