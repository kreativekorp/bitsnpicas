package com.kreative.keyedit.edit;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import com.kreative.keyedit.CapsLockMapping;
import com.kreative.keyedit.DeadKeyTable;
import com.kreative.keyedit.Key;
import com.kreative.keyedit.KeyMapping;
import com.kreative.keyedit.KeyboardMapping;

public class KeyEditController {
	private final KeyEditFrame parent;
	private final KeyboardMappingPanel kmp;
	private final List<KeyEditListener> listeners;
	private Key key = null;
	private boolean alt = false;
	private boolean shift = false;
	
	public KeyEditController(KeyEditFrame parent, KeyboardMappingPanel kmp) {
		this.parent = parent;
		(this.kmp = kmp).addListener(new MyKeyboardMappingPanelListener());
		this.listeners = new ArrayList<KeyEditListener>();
	}
	
	public Key getSelectedKey() {
		return key;
	}
	
	public boolean isAltSelected() {
		return alt;
	}
	
	public boolean isShiftSelected() {
		return shift;
	}
	
	public void setSelectedKey(Key key, boolean alt, boolean shift) {
		kmp.setSelectedKey(key, alt, shift);
	}
	
	public int getOutput(Key key, boolean alt, boolean shift) {
		return kmp.getKeyMappingPanel(key).getOutput(alt, shift);
	}
	
	public void setOutput(Key key, boolean alt, boolean shift, int output) {
		KeyMapping km = kmp.getKeyMappingPanel(key).getKeyMapping();
		if (alt) {
			if (shift) km.altShiftedOutput = output;
			else km.altUnshiftedOutput = output;
			km.altCapsLockMapping = CapsLockMapping.AUTO;
		} else {
			if (shift) km.shiftedOutput = output;
			else km.unshiftedOutput = output;
			km.capsLockMapping = CapsLockMapping.AUTO;
		}
		DeadKeyTable dead;
		if (alt) {
			if (shift) dead = km.altShiftedDeadKey;
			else dead = km.altUnshiftedDeadKey;
		} else {
			if (shift) dead = km.shiftedDeadKey;
			else dead = km.unshiftedDeadKey;
		}
		if (dead != null) dead.setTerminator(output);
		for (KeyEditListener l : listeners) l.keyMappingChanged();
	}
	
	public KeyInfoFrame getKeyMappingFrame(Key key, boolean alt, boolean shift) {
		KeyMapping km = kmp.getKeyMappingPanel(key).getKeyMapping();
		KeyInfoFrame f = new KeyInfoFrame(parent, key, km, this);
		f.addListener(new MyKeyEditListener());
		return f;
	}
	
	public DeadKeyTable getDeadKey(Key key, boolean alt, boolean shift) {
		return kmp.getKeyMappingPanel(key).getDeadKey(alt, shift);
	}
	
	public void setDeadKey(Key key, boolean alt, boolean shift, DeadKeyTable dead) {
		KeyMapping km = kmp.getKeyMappingPanel(key).getKeyMapping();
		if (alt) {
			if (shift) km.altShiftedDeadKey = dead;
			else km.altUnshiftedDeadKey = dead;
		} else {
			if (shift) km.shiftedDeadKey = dead;
			else km.unshiftedDeadKey = dead;
		}
		for (KeyEditListener l : listeners) l.keyMappingChanged();
	}
	
	public DeadKeyTableFrame getDeadKeyTableFrame(Key key, boolean alt, boolean shift) {
		DeadKeyTable dead = getDeadKey(key, alt, shift);
		if (dead == null) {
			int output = getOutput(key, alt, shift);
			dead = new DeadKeyTable(output);
			dead.keyMap.put(32, output);
			setDeadKey(key, alt, shift, dead);
		}
		DeadKeyTableFrame f = new DeadKeyTableFrame(parent, dead);
		f.addListener(new MyKeyEditListener());
		return f;
	}
	
	public LayoutInfoFrame getKeyboardMappingInfoFrame() {
		KeyboardMapping km = kmp.getKeyboardMapping();
		LayoutInfoFrame f = new LayoutInfoFrame(parent, km);
		f.addListener(new MyKeyEditListener());
		return f;
	}
	
	public void addListener(KeyEditListener listener) {
		if (listener != null) listeners.add(listener);
	}
	
	public void removeListener(KeyEditListener listener) {
		if (listener != null) listeners.remove(listener);
	}
	
	public KeyEditListener[] getListeners() {
		return listeners.toArray(new KeyEditListener[listeners.size()]);
	}
	
	private class MyKeyboardMappingPanelListener implements KeyboardMappingPanelListener {
		public void focusGained(FocusEvent e, Key key, boolean alt, boolean shift) {
			KeyEditController.this.key = key;
			KeyEditController.this.alt = alt;
			KeyEditController.this.shift = shift;
		}
		public void keyPressed(KeyEvent e, Key key, boolean alt, boolean shift) {
			KeyEditController.this.key = key;
			KeyEditController.this.alt = alt;
			KeyEditController.this.shift = shift;
			switch (e.getKeyCode()) {
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_DELETE:
					if (e.isMetaDown() || e.isControlDown()) {
						setDeadKey(key, alt, shift, null);
					} else {
						setOutput(key, alt, shift, -1);
					}
					break;
				case KeyEvent.VK_ENTER:
					if (e.isMetaDown() || e.isControlDown()) {
						getDeadKeyTableFrame(key, alt, shift).setVisible(true);
					} else {
						getKeyMappingFrame(key, alt, shift).setVisible(true);
					}
					break;
			}
		}
		public void keyTyped(KeyEvent e, Key key, boolean alt, boolean shift) {
			KeyEditController.this.key = key;
			KeyEditController.this.alt = alt;
			KeyEditController.this.shift = shift;
			if (e.isMetaDown() || e.isControlDown()) return;
			char ch = e.getKeyChar();
			if (ch == KeyEvent.CHAR_UNDEFINED) return;
			if (ch < 32 || (ch >= 127 && ch < 160)) return;
			if (ch >= 0xD800 && ch < 0xE000) return;
			setOutput(key, alt, shift, ch);
		}
		public void mouseClicked(MouseEvent e, Key key, boolean alt, boolean shift) {
			KeyEditController.this.key = key;
			KeyEditController.this.alt = alt;
			KeyEditController.this.shift = shift;
			if (e.getClickCount() > 1) {
				getKeyMappingFrame(key, alt, shift).setVisible(true);
			}
		}
		public void focusLost(FocusEvent e, Key key, boolean alt, boolean shift) {}
		public void keyReleased(KeyEvent e, Key key, boolean alt, boolean shift) {}
		public void mouseEntered(MouseEvent e, Key key, boolean alt, boolean shift) {}
		public void mouseExited(MouseEvent e, Key key, boolean alt, boolean shift) {}
		public void mousePressed(MouseEvent e, Key key, boolean alt, boolean shift) {}
		public void mouseReleased(MouseEvent e, Key key, boolean alt, boolean shift) {}
	}
	
	private class MyKeyEditListener implements KeyEditListener {
		public void metadataChanged() {
			for (KeyEditListener l : listeners) l.metadataChanged();
		}
		public void keyMappingChanged() {
			for (KeyEditListener l : listeners) l.keyMappingChanged();
		}
	}
}
