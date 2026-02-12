package com.kreative.bitsnpicas.edit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OpacitySlider extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private int value;
	private Dimension minimumSize;
	private Dimension preferredSize;
	private final ArrayList<ChangeListener> listeners;
	
	public OpacitySlider() {
		this.value = 255;
		this.minimumSize = null;
		this.preferredSize = null;
		this.listeners = new ArrayList<ChangeListener>();
		MyMouseListener ml = new MyMouseListener();
		this.addMouseListener(ml);
		this.addMouseMotionListener(ml);
		this.addMouseWheelListener(ml);
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = (value < 0) ? 0 : (value > 255) ? 255 : value;
		this.fireChangeEvent();
		this.repaint();
	}
	
	public Dimension getMinimumSize() {
		if (minimumSize != null) {
			return minimumSize;
		} else {
			Insets i = getInsets();
			int w = 24 + i.left + i.right;
			int h = 272 + i.top + i.bottom;
			return new Dimension(w, h);
		}
	}
	
	public void setMinimumSize(Dimension d) {
		this.minimumSize = d;
	}
	
	public Dimension getPreferredSize() {
		if (preferredSize != null) {
			return preferredSize;
		} else {
			Insets i = getInsets();
			int w = 24 + i.left + i.right;
			int h = 272 + i.top + i.bottom;
			return new Dimension(w, h);
		}
	}
	
	public void setPreferredSize(Dimension d) {
		this.preferredSize = d;
	}
	
	public void addChangeListener(ChangeListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeChangeListener(ChangeListener listener) {
		this.listeners.add(listener);
	}
	
	protected void fireChangeEvent() {
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener l : listeners) l.stateChanged(e);
	}
	
	protected void paintComponent(Graphics g) {
		Insets i = getInsets();
		int w = getWidth() - i.left - i.right - 16;
		int h = getHeight() - i.top - i.bottom - 16;
		if (w < 2 || h < 2) return;
		g.setColor(Color.black);
		g.fillRect(i.left + 7, i.top + 7, w + 2, h + 2);
		for (int y = 0; y < h; y++) {
			int k = 255 * y / (h - 1);
			g.setColor(new Color(k, k, k));
			g.fillRect(i.left + 8, i.top + 8 + y, w, 1);
		}
		int k = 255 - value;
		int y = (h - 1) * k / 255;
		g.setColor(Color.black);
		g.fillRect(i.left + 6, i.top + 5 + y, w + 4, 7);
		g.setColor(Color.white);
		g.fillRect(i.left + 7, i.top + 6 + y, w + 2, 5);
		g.setColor(new Color(k, k, k));
		g.fillRect(i.left + 8, i.top + 7 + y, w, 3);
		
		Font saveFont = g.getFont();
		g.setColor(Color.black);
		g.setFont(Resources.HEX_FONT);
		g.drawString(">", i.left + 1, i.top + y + 12);
		g.drawString("<", i.left + w + 10, i.top + y + 12);
		g.setFont(saveFont);
	}
	
	protected void clickAt(int my) {
		Insets i = getInsets();
		int w = getWidth() - i.left - i.right - 16;
		int h = getHeight() - i.top - i.bottom - 16;
		if (w < 2 || h < 2) return;
		setValue(255 - 255 * (my - i.top - 8) / (h - 1));
	}
	
	private class MyMouseListener extends MouseAdapter {
		public void mouseWheelMoved(MouseWheelEvent e) {
			setValue(value - e.getWheelRotation());
		}
		public void mousePressed(MouseEvent e) {
			clickAt(e.getY());
		}
		public void mouseDragged(MouseEvent e) {
			clickAt(e.getY());
		}
		public void mouseReleased(MouseEvent e) {
			clickAt(e.getY());
		}
	}
}
