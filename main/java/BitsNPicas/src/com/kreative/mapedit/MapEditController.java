package com.kreative.mapedit;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class MapEditController {
	private final MapEditFrame parent;
	private final MappingTablePanel mtp;
	private final CodePointSequencePanel csp;
	private final MapEditSubtableFrame[] subtableWindows;
	private final List<MapEditListener> listeners;
	private int index = -1;
	
	public MapEditController(MapEditFrame parent, MappingTablePanel mtp, CodePointSequencePanel csp) {
		this.parent = parent;
		(this.mtp = mtp).addListener(new MyMappingTableListener());
		(this.csp = csp).addListener(new MyCodePointSequenceListener());
		this.subtableWindows = new MapEditSubtableFrame[256];
		this.listeners = new ArrayList<MapEditListener>();
	}
	
	public int getSelectedIndex() {
		return index;
	}
	
	public void setSelectedIndex(int i) {
		mtp.getTextField(i).requestFocusInWindow();
	}
	
	public CodePointSequence getSequence(int i) {
		if (i < 0 || i >= 256) return null;
		return mtp.getMappingTable().getSequence(i);
	}
	
	public String getSequenceString(int i) {
		if (i < 0 || i >= 256) return null;
		CodePointSequence cs = mtp.getMappingTable().getSequence(i);
		return (cs != null) ? cs.toString() : null;
	}
	
	public void setSequence(int i, CodePointSequence seq) {
		if (i < 0 || i >= 256 || seq == null) return;
		if (index == i) csp.setCodePointSequence(seq, i, false);
		mtp.getMappingTable().setSequence(seq, i);
		mtp.update(i);
		for (MapEditListener l : listeners) l.codePointSequenceChanged();
	}
	
	public void setSequenceString(int i, String s) {
		if (i < 0 || i >= 256 || s == null) return;
		CodePointSequence seq = new CodePointSequence(s);
		if (index == i) csp.setCodePointSequence(seq, i, false);
		mtp.getMappingTable().setSequence(seq, i);
		mtp.update(i);
		for (MapEditListener l : listeners) l.codePointSequenceChanged();
	}
	
	public void deleteSequence(int i) {
		if (i < 0 || i >= 256) return;
		if (index == i) csp.setCodePointSequence(null, i, false);
		mtp.getMappingTable().setSequence(null, i);
		mtp.update(i);
		for (MapEditListener l : listeners) l.codePointSequenceChanged();
	}
	
	public MapEditSubtableFrame createSubtableFrame(int i) {
		if (i < 0 || i >= 256) return null;
		if (subtableWindows[i] == null) {
			MappingTable subtable = mtp.getMappingTable().getSubtable(i);
			if (subtable == null) {
				subtable = new MappingTable();
				mtp.getMappingTable().setSubtable(subtable, i);
				mtp.update(i);
				for (MapEditListener l : listeners) l.mappingSubtableChanged();
			}
			String h = "00" + Integer.toHexString(i);
			h = h.substring(h.length() - 2).toUpperCase();
			h = csp.getEncodingPrefix() + h;
			subtableWindows[i] = new MapEditSubtableFrame(parent, subtable, h);
			subtableWindows[i].addWindowListener(new MyWindowListener(i));
			subtableWindows[i].controller.addListener(new MyMapEditListener());
		}
		return subtableWindows[i];
	}
	
	public void disposeSubtableFrame(int i) {
		if (i < 0 || i >= 256) return;
		if (subtableWindows[i] == null) return;
		subtableWindows[i].dispose();
		subtableWindows[i] = null;
	}
	
	public void deleteSubtable(int i) {
		if (i < 0 || i >= 256) return;
		disposeSubtableFrame(i);
		mtp.getMappingTable().setSubtable(null, i);
		mtp.update(i);
		for (MapEditListener l : listeners) l.mappingSubtableChanged();
	}
	
	public void addListener(MapEditListener listener) {
		if (listener != null) listeners.add(listener);
	}
	
	public void removeListener(MapEditListener listener) {
		if (listener != null) listeners.remove(listener);
	}
	
	public MapEditListener[] getListeners() {
		return listeners.toArray(new MapEditListener[listeners.size()]);
	}
	
	private class MyMappingTableListener implements MappingTablePanelListener {
		public void focusGained(FocusEvent e, int i) {
			CodePointSequence cs = mtp.getMappingTable().getSequence(i);
			csp.setCodePointSequence(cs, index = i, false);
		}
		public void keyPressed(KeyEvent e, int i) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					if (i >= 1) mtp.getTextField(i - 1).requestFocusInWindow();
					break;
				case KeyEvent.VK_RIGHT:
					if (i < 255) mtp.getTextField(i + 1).requestFocusInWindow();
					break;
				case KeyEvent.VK_UP:
					if (i >= 16) mtp.getTextField(i - 16).requestFocusInWindow();
					break;
				case KeyEvent.VK_DOWN:
					if (i < 240) mtp.getTextField(i + 16).requestFocusInWindow();
					break;
				case KeyEvent.VK_ESCAPE:
				case KeyEvent.VK_CLEAR:
					csp.setCodePointSequence(null, index = -1, false);
					Component c = mtp.getTextField(i);
					while (c != null) {
						if (c instanceof Window) {
							c.requestFocusInWindow();
							break;
						} else {
							c = c.getParent();
						}
					}
					break;
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_DELETE:
					if (e.isMetaDown() || e.isControlDown()) {
						deleteSubtable(index = i);
					} else {
						csp.setCodePointSequence(null, index = i, true);
					}
					break;
				case KeyEvent.VK_ENTER:
					if (e.isMetaDown() || e.isControlDown()) {
						createSubtableFrame(index = i).setVisible(true);
					} else if (e.isShiftDown()) {
						if (i >= 16) mtp.getTextField(i - 16).requestFocusInWindow();
					} else {
						if (i < 240) mtp.getTextField(i + 16).requestFocusInWindow();
					}
					break;
			}
		}
		public void keyTyped(KeyEvent e, int i) {
			if (e.isMetaDown() || e.isControlDown()) return;
			char ch = e.getKeyChar();
			if (ch == KeyEvent.CHAR_UNDEFINED) return;
			if (ch < 32 || (ch >= 127 && ch < 160)) return;
			if (ch >= 0xD800 && ch < 0xE000) return;
			CodePointSequence cs = new CodePointSequence(ch);
			csp.setCodePointSequence(cs, index = i, true);
		}
		public void focusLost(FocusEvent e, int i) {}
		public void keyReleased(KeyEvent e, int i) {}
		public void mouseClicked(MouseEvent e, int i) {}
		public void mouseEntered(MouseEvent e, int i) {}
		public void mouseExited(MouseEvent e, int i) {}
		public void mousePressed(MouseEvent e, int i) {}
		public void mouseReleased(MouseEvent e, int i) {}
	}
	
	private class MyCodePointSequenceListener implements CodePointSequencePanelListener {
		public void codePointSequenceChanged() {
			if (index < 0) return;
			CodePointSequence cs = csp.getCodePointSequence();
			mtp.getMappingTable().setSequence(cs, index);
			mtp.update(index);
			for (MapEditListener l : listeners) l.codePointSequenceChanged();
		}
	}
	
	private class MyWindowListener extends WindowAdapter {
		private final int i;
		public MyWindowListener(int i) {
			this.i = i;
		}
		public void windowClosed(WindowEvent e) {
			subtableWindows[i] = null;
		}
	}
	
	private class MyMapEditListener implements MapEditListener {
		public void codePointSequenceChanged() {
			for (MapEditListener l : listeners) l.mappingSubtableChanged();
		}
		public void mappingSubtableChanged() {
			for (MapEditListener l : listeners) l.mappingSubtableChanged();
		}
	}
}
