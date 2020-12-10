package com.kreative.mapedit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.SortedMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class NewMappingFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final SortedMap<String,Charset> charsets;
	private final JList charsetList;
	private final JButton createButton;
	
	public NewMappingFrame(String title) {
		super(title);
		this.charsets = Charset.availableCharsets();
		this.charsetList = new JList(charsets.keySet().toArray());
		this.createButton = new JButton("Create");
		
		charsetList.setVisibleRowCount(12);
		JScrollPane listPanel = new JScrollPane(
			charsetList,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(createButton);
		
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.add(listPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(mainPanel);
		
		pack();
		setSize(500, getHeight());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Object o : charsetList.getSelectedValues()) {
					new NewMappingThread(charsets.get(o)).start();
				}
			}
		});
	}
	
	private static final class NewMappingThread extends Thread {
		private final Charset cs;
		public NewMappingThread(Charset cs) { this.cs = cs; }
		@Override public void run() { Main.newMapping(cs); }
	}
}
