package com.kreative.mapedit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MappingNamePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy MMMM d");
	
	private final JTextField nameField;
	private final JTextField dateField;
	private final JTextField authorField;
	private final List<MappingNamePanelListener> listeners;
	
	public MappingNamePanel(final Mapping mapping) {
		this.nameField = new JTextField(mapping.name);
		this.dateField = new JTextField(mapping.date);
		this.authorField = new JTextField(mapping.author);
		this.listeners = new ArrayList<MappingNamePanelListener>();
		
		JPanel labelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		labelPanel.add(new JLabel("Name:"));
		labelPanel.add(new JLabel("Date:"));
		labelPanel.add(new JLabel("Author:"));
		
		JPanel fieldPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		fieldPanel.add(nameField);
		fieldPanel.add(dateField);
		fieldPanel.add(authorField);
		
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.add(labelPanel, BorderLayout.LINE_START);
		mainPanel.add(fieldPanel, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(mainPanel);
		
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				mapping.name = nameField.getText();
				if (mapping.name.length() == 0) mapping.name = null;
				for (MappingNamePanelListener l : listeners) l.nameChanged(e);
			}
			public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
			public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
		});
		dateField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				mapping.date = dateField.getText();
				if (mapping.date.length() == 0) mapping.date = null;
				for (MappingNamePanelListener l : listeners) l.dateChanged(e);
			}
			public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
			public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
		});
		authorField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				mapping.author = authorField.getText();
				if (mapping.author.length() == 0) mapping.author = null;
				for (MappingNamePanelListener l : listeners) l.authorChanged(e);
			}
			public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
			public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
		});
	}
	
	public void updateDate() {
		dateField.setText(DATE_FORMAT.format(new Date()));
	}
	
	public void addListener(MappingNamePanelListener listener) {
		if (listener != null) listeners.add(listener);
	}
	
	public void removeListener(MappingNamePanelListener listener) {
		if (listener != null) listeners.remove(listener);
	}
	
	public MappingNamePanelListener[] getListeners() {
		return listeners.toArray(new MappingNamePanelListener[listeners.size()]);
	}
}
