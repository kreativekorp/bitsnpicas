package com.kreative.mapedit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MappingTablePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Color BG_MB = new Color(255, 165, 0);
	private static final Color FG_MB = Color.black;
	private static final Color BG_NG = new Color(0xFFDDDDDD);
	private static final Color FG_NG = new Color(0xFFDDDDDD);
	private static final Color BG_RV = Color.black;
	private static final Color FG_RV = Color.white;
	private static final Color BG_RL = new Color(255, 255, 204);
	private static final Color FG_RL = Color.black;
	private static final Color BG_LR = new Color(204, 238, 255);
	private static final Color FG_LR = Color.black;
	private static final Color BG_CC = new Color(204, 255, 204);
	private static final Color FG_CC = Color.black;
	private static final Color BG_PU = new Color(238, 204, 255);
	private static final Color FG_PU = Color.black;
	private static final Color BG_OK = Color.white;
	private static final Color FG_OK = Color.black;
	private static final Color BLURRED = new Color(0xFFBBBBBB);
	private static final Color FOCUSED = new Color(0xFF6699CC);
	
	private final MappingTable mt;
	private final JLabel[] fields;
	private final List<MappingTablePanelListener> listeners;
	
	public MappingTablePanel(MappingTable table) {
		mt = table;
		fields = new JLabel[256];
		listeners = new ArrayList<MappingTablePanelListener>();
		JPanel panel = new JPanel(new GridLayout(16, 16, 4, 4));
		for (int i = 0; i < 256; i++) {
			fields[i] = new JLabel();
			fields[i].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, BLURRED));
			fields[i].setFocusable(true);
			fields[i].setHorizontalAlignment(JLabel.CENTER);
			fields[i].setOpaque(true);
			panel.add(fields[i]);
			MyListener ml = new MyListener(i);
			fields[i].addFocusListener(ml);
			fields[i].addKeyListener(ml);
			fields[i].addMouseListener(ml);
		}
		update();
		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		panel.setPreferredSize(new Dimension(484, 484));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(panel);
	}
	
	public void update() {
		for (int i = 0; i < 256; i++) {
			update(i);
		}
	}
	
	public void update(int i) {
		CodePointSequence cs = mt.getSequence(i);
		if (cs == null) {
			fields[i].setText(" ");
		} else {
			String css = cs.toString().trim();
			if (css.length() == 0) css = " ";
			fields[i].setText(css);
		}
		if (mt.getSubtable(i) != null) {
			fields[i].setBackground(BG_MB);
			fields[i].setForeground(FG_MB);
		} else if (cs == null) {
			fields[i].setBackground(BG_NG);
			fields[i].setForeground(FG_NG);
		} else if (cs.contains(MappingTag.RV.intValue)) {
			fields[i].setBackground(BG_RV);
			fields[i].setForeground(FG_RV);
		} else if (cs.contains(MappingTag.RL.intValue)) {
			fields[i].setBackground(BG_RL);
			fields[i].setForeground(FG_RL);
		} else if (cs.contains(MappingTag.LR.intValue)) {
			fields[i].setBackground(BG_LR);
			fields[i].setForeground(FG_LR);
		} else if (cs.containsC0C1()) {
			fields[i].setBackground(BG_CC);
			fields[i].setForeground(FG_CC);
		} else if (cs.containsPUA()) {
			fields[i].setBackground(BG_PU);
			fields[i].setForeground(FG_PU);
		} else {
			fields[i].setBackground(BG_OK);
			fields[i].setForeground(FG_OK);
		}
	}
	
	public MappingTable getMappingTable() {
		return mt;
	}
	
	public JLabel getTextField(int i) {
		return fields[i];
	}
	
	public void addListener(MappingTablePanelListener listener) {
		if (listener != null) listeners.add(listener);
	}
	
	public void removeListener(MappingTablePanelListener listener) {
		if (listener != null) listeners.remove(listener);
	}
	
	public MappingTablePanelListener[] getListeners() {
		return listeners.toArray(new MappingTablePanelListener[listeners.size()]);
	}
	
	private class MyListener implements FocusListener, KeyListener, MouseListener {
		private final int i;
		public MyListener(int i) {
			this.i = i;
		}
		public void focusGained(FocusEvent e) {
			fields[i].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, FOCUSED));
			for (MappingTablePanelListener l : listeners) l.focusGained(e, i);
		}
		public void focusLost(FocusEvent e) {
			fields[i].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, BLURRED));
			for (MappingTablePanelListener l : listeners) l.focusLost(e, i);
		}
		public void keyPressed(KeyEvent e) {
			for (MappingTablePanelListener l : listeners) l.keyPressed(e, i);
		}
		public void keyReleased(KeyEvent e) {
			for (MappingTablePanelListener l : listeners) l.keyReleased(e, i);
		}
		public void keyTyped(KeyEvent e) {
			for (MappingTablePanelListener l : listeners) l.keyTyped(e, i);
		}
		public void mouseClicked(MouseEvent e) {
			for (MappingTablePanelListener l : listeners) l.mouseClicked(e, i);
		}
		public void mouseEntered(MouseEvent e) {
			for (MappingTablePanelListener l : listeners) l.mouseEntered(e, i);
		}
		public void mouseExited(MouseEvent e) {
			for (MappingTablePanelListener l : listeners) l.mouseExited(e, i);
		}
		public void mousePressed(MouseEvent e) {
			fields[i].requestFocusInWindow();
			for (MappingTablePanelListener l : listeners) l.mousePressed(e, i);
		}
		public void mouseReleased(MouseEvent e) {
			for (MappingTablePanelListener l : listeners) l.mouseReleased(e, i);
		}
	}
}
