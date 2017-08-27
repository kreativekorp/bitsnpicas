package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontExporter;

public class FontListPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final File fontFile;
	private final FontExporter<?> format;
	private final JList list;
	private final JLabel label;
	private final JButton openButton;
	
	public FontListPanel(File fontFile, FontExporter<?> format, Font<?>[] fonts) {
		this.fontFile = fontFile;
		this.format = format;
		this.list = new JList(fonts);
		this.label = new JLabel("This file contains multiple fonts. Please select one.");
		this.openButton = new JButton("Open");
		
		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(openButton);
		
		JPanel main = new JPanel(new BorderLayout(4, 4));
		main.add(label, BorderLayout.PAGE_START);
		main.add(list, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.PAGE_END);
		main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
		
		list.addMouseListener(new MyMouseListener());
		openButton.addActionListener(new MyActionListener());
	}
	
	private class MyMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				Font<?> font = (Font<?>)list.getSelectedValue();
				if (font != null) Main.openFont(fontFile, format, font);
			}
		}
	}
	
	private class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Font<?> font = (Font<?>)list.getSelectedValue();
			if (font != null) Main.openFont(fontFile, format, font);
		}
	}
}
