package com.kreative.bitsnpicas.edit.glmlicon;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.DefaultTreeCellRenderer;
import com.kreative.bitsnpicas.edit.GlyphListModel;
import com.kreative.bitsnpicas.edit.GlyphListModelList.GlyphListModelTreeNode;

public class GLMLTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	
	public Component getTreeCellRendererComponent(
		JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean focus
	) {
		Component c = super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, false);
		if (c instanceof JLabel) {
			JLabel label = (JLabel)c;
			Dimension d = label.getPreferredSize();
			int tw = getViewportWidth(tree);
			if (d.width < tw) d.width = tw;
			if (d.height < 24) d.height = 24;
			label.setPreferredSize(d);
			label.setIcon(new ImageIcon(getImageForTreeCell(value)));
		}
		return c;
	}
	
	private static int getViewportWidth(Component c) {
		while (c != null) {
			if (c instanceof JViewport) {
				return c.getWidth();
			}
			c = c.getParent();
		}
		return -1;
	}
	
	private static Image getImageForTreeCell(Object value) {
		if (value instanceof GlyphListModelTreeNode) {
			GlyphListModel model = ((GlyphListModelTreeNode)value).getModel();
			if (model != null) {
				String group = model.getIconGroup();
				String name = value.toString();
				return GLMLResources.getImage(group, name);
			}
		}
		return GLMLResources.getImage(null, null);
	}
}
