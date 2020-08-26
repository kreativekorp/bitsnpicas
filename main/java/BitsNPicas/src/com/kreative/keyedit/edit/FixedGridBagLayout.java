package com.kreative.keyedit.edit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.io.Serializable;
import java.util.IdentityHashMap;

public class FixedGridBagLayout implements LayoutManager2, Serializable {
	private static final long serialVersionUID = 1L;
	
	private IdentityHashMap<Component,FixedGridBagConstraints> cons;
	private int totalGridWidth;
	private int totalGridHeight;
	private int maxMinW;
	private int maxMinH;
	private int maxPrefW;
	private int maxPrefH;
	private int minMaxW;
	private int minMaxH;
	
	public FixedGridBagLayout() {
		this.cons = new IdentityHashMap<Component,FixedGridBagConstraints>();
		this.totalGridWidth = -1;
		this.totalGridHeight = -1;
		this.maxMinW = -1;
		this.maxMinH = -1;
		this.maxPrefW = -1;
		this.maxPrefH = -1;
		this.minMaxW = -1;
		this.minMaxH = -1;
	}
	
	@Override
	public void addLayoutComponent(String name, Component c) {
		this.cons.remove(c);
	}
	
	@Override
	public void addLayoutComponent(Component c, Object con) {
		this.cons.remove(c);
		if (con instanceof FixedGridBagConstraints) {
			this.cons.put(c, ((FixedGridBagConstraints)con).clone());
		}
	}
	
	@Override
	public void removeLayoutComponent(Component c) {
		this.cons.remove(c);
	}
	
	@Override
	public void invalidateLayout(Container parent) {
		this.totalGridWidth = -1;
		this.totalGridHeight = -1;
		this.maxMinW = -1;
		this.maxMinH = -1;
		this.maxPrefW = -1;
		this.maxPrefH = -1;
		this.minMaxW = -1;
		this.minMaxH = -1;
	}
	
	private int scale64(int m, int n, int d) {
		long q = (long)m * (long)n / (long)d;
		return (q < Integer.MAX_VALUE) ? (int)q : Integer.MAX_VALUE;
	}
	
	private void check(Container parent) {
		if (totalGridWidth < 0 || totalGridHeight < 0) {
			for (FixedGridBagConstraints con : cons.values()) {
				int maxX = con.gridx + con.gridwidth;
				int maxY = con.gridy + con.gridheight;
				if (maxX > totalGridWidth) totalGridWidth = maxX;
				if (maxY > totalGridHeight) totalGridHeight = maxY;
			}
		}
		if (maxMinW < 0 || maxMinH < 0 || maxPrefW < 0 || maxPrefH < 0 || minMaxW < 0 || minMaxH < 0) {
			maxMinW = Math.max(totalGridWidth, 0);
			maxMinH = Math.max(totalGridHeight, 0);
			maxPrefW = Math.max(totalGridWidth, 0);
			maxPrefH = Math.max(totalGridHeight, 0);
			minMaxW = Integer.MAX_VALUE;
			minMaxH = Integer.MAX_VALUE;
			for (int i = 0, n = parent.getComponentCount(); i < n; i++) {
				Component c = parent.getComponent(i);
				Dimension min = c.getMinimumSize();
				Dimension pref = c.getPreferredSize();
				Dimension max = c.getMaximumSize();
				if (totalGridWidth > 0 && totalGridHeight > 0) {
					FixedGridBagConstraints con = cons.get(c);
					if (con != null) {
						min.width = scale64(min.width, totalGridWidth, con.gridwidth);
						min.height = scale64(min.height, totalGridHeight, con.gridheight);
						pref.width = scale64(pref.width, totalGridWidth, con.gridwidth);
						pref.height = scale64(pref.height, totalGridHeight, con.gridheight);
						max.width = scale64(max.width, totalGridWidth, con.gridwidth);
						max.height = scale64(max.height, totalGridHeight, con.gridheight);
					}
				}
				if (min.width > maxMinW) maxMinW = min.width;
				if (min.height > maxMinH) maxMinH = min.height;
				if (pref.width > maxPrefW) maxPrefW = pref.width;
				if (pref.height > maxPrefH) maxPrefH = pref.height;
				if (max.width < minMaxW) minMaxW = max.width;
				if (max.height < minMaxH) minMaxH = max.height;
			}
		}
	}
	
	@Override
	public Dimension minimumLayoutSize(Container parent) {
		check(parent);
		Dimension d = new Dimension(maxMinW, maxMinH);
		Insets insets = parent.getInsets();
		d.width += insets.left + insets.right; if (d.width < 0) d.width = Integer.MAX_VALUE;
		d.height += insets.top + insets.bottom; if (d.height < 0) d.height = Integer.MAX_VALUE;
		return d;
	}
	
	@Override
	public Dimension preferredLayoutSize(Container parent) {
		check(parent);
		Dimension d = new Dimension(maxPrefW, maxPrefH);
		Insets insets = parent.getInsets();
		d.width += insets.left + insets.right; if (d.width < 0) d.width = Integer.MAX_VALUE;
		d.height += insets.top + insets.bottom; if (d.height < 0) d.height = Integer.MAX_VALUE;
		return d;
	}
	
	@Override
	public Dimension maximumLayoutSize(Container parent) {
		check(parent);
		Dimension d = new Dimension(minMaxW, minMaxH);
		Insets insets = parent.getInsets();
		d.width += insets.left + insets.right; if (d.width < 0) d.width = Integer.MAX_VALUE;
		d.height += insets.top + insets.bottom; if (d.height < 0) d.height = Integer.MAX_VALUE;
		return d;
	}
	
	@Override
	public void layoutContainer(Container parent) {
		check(parent);
		Insets insets = parent.getInsets();
		int width = parent.getWidth() - insets.left - insets.right;
		int height = parent.getHeight() - insets.top - insets.bottom;
		for (int i = 0, n = parent.getComponentCount(); i < n; i++) {
			Component c = parent.getComponent(i);
			if (c.isVisible()) {
				int x = insets.left, y = insets.top, w = width, h = height;
				if (totalGridWidth > 0 && totalGridHeight > 0) {
					FixedGridBagConstraints con = cons.get(c);
					if (con != null) {
						x += width * con.gridx / totalGridWidth;
						y += height * con.gridy / totalGridHeight;
						w = width * con.gridwidth / totalGridWidth;
						h = height * con.gridheight / totalGridHeight;
					}
				}
				c.setBounds(x, y, w, h);
			}
		}
	}
	
	@Override
	public float getLayoutAlignmentX(Container parent) {
		return 0.5f;
	}
	
	@Override
	public float getLayoutAlignmentY(Container parent) {
		return 0.5f;
	}
}
