package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.edit.importer.ImportFormat;

public class FormatListPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final File fontFile;
	private final JComboBox list;
	private final JLabel label;
	private final JButton openButton;
	
	public FormatListPanel(File fontFile) {
		this.fontFile = fontFile;
		this.list = new JComboBox(ImportFormat.listedValues());
		this.label = new JLabel("<html>The selected file was not recognized as a font file readable by Bits'n'Picas.<br><br>If you know the file format, select it below. Or, try importing as a binary file.</html>");
		this.openButton = new JButton("Open");
		
		list.setEditable(false);
		list.setMaximumRowCount(list.getItemCount());
		list.setSelectedItem(ImportFormat.BINARY);
		
		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(openButton);
		
		JPanel main = new JPanel(new BorderLayout(4, 4));
		main.add(label, BorderLayout.PAGE_START);
		main.add(list, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.PAGE_END);
		main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
		
		openButton.addActionListener(new MyActionListener());
	}
	
	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ImportFormat format = (ImportFormat)list.getSelectedItem();
			if (format != null) Main.openFonts(fontFile, format);
		}
	}
}
