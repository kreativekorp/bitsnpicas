package com.kreative.keyedit.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.kreative.unicode.data.NameDatabase;
import com.kreative.unicode.data.NameResolver;

public class OutputPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final NameDatabase ndb;
	private final List<OutputPanelListener> listeners;
	private final JTextField codeField;
	private final JTextField charField;
	private final JTextField nameField;
	private boolean lock = false;
	private int output = -1;
	
	public OutputPanel(int output) {
		this.ndb = NameDatabase.instance();
		this.listeners = new ArrayList<OutputPanelListener>();
		this.codeField = new JTextField(8);
		this.charField = new JTextField(4);
		this.nameField = new JTextField(20);
		this.internalSetOutput(null, output);
		
		JPanel innerPanel = new JPanel(new BorderLayout(4,4));
		innerPanel.add(nameField, BorderLayout.CENTER);
		innerPanel.add(charField, BorderLayout.LINE_START);
		JPanel outerPanel = new JPanel(new BorderLayout(4,4));
		outerPanel.add(innerPanel, BorderLayout.CENTER);
		outerPanel.add(codeField, BorderLayout.LINE_START);
		
		setLayout(new BorderLayout(0,0));
		add(outerPanel, BorderLayout.CENTER);
		
		new CodeDocumentListener().on(codeField);
		new CharDocumentListener().on(charField);
		new NameDocumentListener().on(nameField);
	}
	
	public void addListener(OutputPanelListener l) {
		this.listeners.add(l);
	}
	
	public void removeListener(OutputPanelListener l) {
		this.listeners.remove(l);
	}
	
	public OutputPanelListener[] getListeners() {
		return this.listeners.toArray(new OutputPanelListener[this.listeners.size()]);
	}
	
	public int getOutput() {
		return this.output;
	}
	
	public void setOutput(int output) {
		this.internalSetOutput(null, output);
	}
	
	private void internalSetOutput(Component src, int output) {
		this.lock = true;
		if (src != codeField) {
			if (output < 0) codeField.setText(null);
			else {
				String h = Integer.toHexString(output);
				while (h.length() < 4) h = "0" + h;
				codeField.setText(h.toUpperCase());
			}
		}
		if (src != charField) {
			if (output < 0) charField.setText(null);
			else {
				char[] ch = Character.toChars(output);
				charField.setText(String.valueOf(ch));
			}
		}
		if (src != nameField) {
			if (output < 0) nameField.setText(null);
			else nameField.setText(NameResolver.instance(output).getName(output));
		}
		this.lock = false;
		if (this.output != output) {
			this.output = output;
			for (OutputPanelListener l : this.listeners) l.outputChanged(output);
		}
	}
	
	private class CodeDocumentListener implements DocumentListener, FocusListener {
		public void changedUpdate(DocumentEvent e) { doIt(codeField); }
		public void insertUpdate(DocumentEvent e) { doIt(codeField); }
		public void removeUpdate(DocumentEvent e) { doIt(codeField); }
		public void focusGained(FocusEvent e) {}
		public void focusLost(FocusEvent e) { doIt(null); }
		private void doIt(Component c) {
			if (lock) return;
			String s = codeField.getText().trim();
			if (s.length() == 0) {
				internalSetOutput(c, -1);
			} else {
				try { internalSetOutput(c, Integer.parseInt(s, 16)); }
				catch (NumberFormatException nfe) {}
			}
		}
		public void on(JTextField f) {
			f.getDocument().addDocumentListener(this);
			f.addFocusListener(this);
		}
	}
	
	private class CharDocumentListener implements DocumentListener, FocusListener {
		public void changedUpdate(DocumentEvent e) { doIt(charField); }
		public void insertUpdate(DocumentEvent e) { doIt(charField); }
		public void removeUpdate(DocumentEvent e) { doIt(charField); }
		public void focusGained(FocusEvent e) {}
		public void focusLost(FocusEvent e) { doIt(null); }
		private void doIt(Component c) {
			if (lock) return;
			String s = charField.getText();
			if (s.length() == 0) {
				internalSetOutput(c, -1);
			} else {
				internalSetOutput(c, s.codePointAt(0));
			}
		}
		public void on(JTextField f) {
			f.getDocument().addDocumentListener(this);
			f.addFocusListener(this);
		}
	}
	
	private class NameDocumentListener implements DocumentListener, FocusListener {
		public void changedUpdate(DocumentEvent e) { /* doIt(nameField); */ }
		public void insertUpdate(DocumentEvent e) { /* doIt(nameField); */ }
		public void removeUpdate(DocumentEvent e) { /* doIt(nameField); */ }
		public void focusGained(FocusEvent e) {}
		public void focusLost(FocusEvent e) { doIt(null); }
		private void doIt(Component c) {
			if (lock) return;
			String s = nameField.getText().trim();
			if (s.length() == 0) {
				internalSetOutput(c, -1);
			} else {
				NameDatabase.NameEntry ne = ndb.find(s);
				if (ne != null) internalSetOutput(c, ne.codePoint);
			}
		}
		public void on(JTextField f) {
			f.getDocument().addDocumentListener(this);
			f.addFocusListener(this);
		}
	}
}
