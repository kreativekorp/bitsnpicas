package com.kreative.bitsnpicas.datatransfer;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public class TransferActionListener implements ActionListener, PropertyChangeListener {
	private static TransferActionListener instance = null;
	
	public static TransferActionListener getInstance() {
		if (instance == null) instance = new TransferActionListener();
		return instance;
	}
	
	private JComponent focusOwner = null;
	
	public TransferActionListener() {
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addPropertyChangeListener("permanentFocusOwner", this);
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		Object o = e.getNewValue();
		if (o instanceof JComponent) {
			focusOwner = (JComponent)o;
		} else {
			focusOwner = null;
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (focusOwner == null) return;
		String actionName = e.getActionCommand();
		if (focusOwner instanceof JTextComponent) {
			JTextComponent c = (JTextComponent)focusOwner;
			if ("Cut".equals(actionName)) { c.cut(); return; }
			if ("Copy".equals(actionName)) { c.copy(); return; }
			if ("Paste".equals(actionName)) { c.paste(); return; }
			if ("Clear".equals(actionName)) { c.replaceSelection(""); return; }
		}
		Action action = focusOwner.getActionMap().get(actionName);
		if (action != null) {
			action.actionPerformed(new ActionEvent(
				focusOwner, ActionEvent.ACTION_PERFORMED, null
			));
		}
	}
}
