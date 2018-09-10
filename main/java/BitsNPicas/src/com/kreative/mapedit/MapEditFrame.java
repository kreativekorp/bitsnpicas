package com.kreative.mapedit;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;

public class MapEditFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private File file;
	private boolean changed;
	private final Mapping mapping;
	private final MappingNamePanel namePanel;
	private final MappingTablePanel tablePanel;
	private final CodePointSequencePanel sequencePanel;
	private final MapEditController controller;
	
	public MapEditFrame(File file, Mapping mapping) {
		this.file = file;
		this.changed = false;
		this.mapping = mapping;
		this.namePanel = new MappingNamePanel(mapping);
		this.tablePanel = new MappingTablePanel(mapping.root);
		this.sequencePanel = new CodePointSequencePanel("");
		this.controller = new MapEditController(this, tablePanel, sequencePanel);
		
		JPanel innerPanel = new JPanel(new BorderLayout(8, 8));
		innerPanel.add(tablePanel, BorderLayout.LINE_START);
		innerPanel.add(sequencePanel, BorderLayout.CENTER);
		
		JPanel outerPanel = new JPanel(new BorderLayout(8, 8));
		outerPanel.add(namePanel, BorderLayout.PAGE_START);
		outerPanel.add(innerPanel, BorderLayout.CENTER);
		outerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(outerPanel);
		setJMenuBar(new MapEditMenuBar(this, this, controller));
		
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		updateWindow();
		
		namePanel.addListener(new MyNamePanelListener());
		controller.addListener(new MyMapEditListener());
		addWindowListener(new MyWindowListener());
	}
	
	public boolean save() {
		if (file == null) {
			return saveAs();
		} else try {
			FileOutputStream out = new FileOutputStream(file);
			mapping.write(out);
			out.close();
			changed = false;
			updateWindow();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				null, "An error occurred while saving this file.",
				"Save", JOptionPane.ERROR_MESSAGE
			);
			updateWindow();
			return false;
		}
	}
	
	public boolean saveAs() {
		File newFile = Main.getSaveFile(".TXT");
		if (newFile == null) return false;
		file = newFile;
		return save();
	}
	
	private void updateWindow() {
		if (MapEditMenuBar.IS_MAC_OS) {
			getRootPane().putClientProperty("Window.documentFile", file);
			getRootPane().putClientProperty("Window.documentModified", changed);
			setTitle((mapping.name != null) ? mapping.name : "Untitled");
		} else if (changed) {
			setTitle(((mapping.name != null) ? mapping.name : "Untitled") + " \u2022");
		} else {
			setTitle((mapping.name != null) ? mapping.name : "Untitled");
		}
	}
	
	private class MyNamePanelListener implements MappingNamePanelListener {
		public void nameChanged(DocumentEvent e) {
			changed = true;
			updateWindow();
			namePanel.updateDate();
		}
		public void dateChanged(DocumentEvent e) {
			changed = true;
			updateWindow();
		}
		public void authorChanged(DocumentEvent e) {
			changed = true;
			updateWindow();
			namePanel.updateDate();
		}
	}
	
	private class MyMapEditListener implements MapEditListener {
		public void codePointSequenceChanged() {
			changed = true;
			updateWindow();
			namePanel.updateDate();
		}
		public void mappingSubtableChanged() {
			changed = true;
			updateWindow();
			namePanel.updateDate();
		}
	}
	
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			if (changed) {
				String name = ((mapping.name != null) ? mapping.name : "Untitled");
				switch (new SaveChangesDialog(MapEditFrame.this, name).showDialog()) {
					case CANCEL: return;
					case DONT_SAVE: break;
					case SAVE:
						if (save()) break;
						else return;
				}
			}
			dispose();
		}
	}
}
