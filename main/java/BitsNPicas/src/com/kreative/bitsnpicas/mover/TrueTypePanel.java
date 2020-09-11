package com.kreative.bitsnpicas.mover;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TrueTypePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public TrueTypePanel(Font font) {
		JTextArea l1 = make("How razorback-jumping frogs can level six piqued gymnasts!");
		JTextArea l2 = make("Cozy lummox gives smart squid who asks for job pen.");
		JTextArea l3 = make("The quick brown fox jumps over the lazy dog.");
		l1.setFont(font.deriveFont(9f));
		l2.setFont(font.deriveFont(12f));
		l3.setFont(font.deriveFont(18f));
		l1.setBorder(BorderFactory.createTitledBorder("9 point"));
		l2.setBorder(BorderFactory.createTitledBorder("12 point"));
		l3.setBorder(BorderFactory.createTitledBorder("18 point"));
		l1.setMinimumSize(new Dimension(288,72));
		l2.setMinimumSize(new Dimension(288,108));
		l3.setMinimumSize(new Dimension(288,144));
		l1.setPreferredSize(new Dimension(288,72));
		l2.setPreferredSize(new Dimension(288,108));
		l3.setPreferredSize(new Dimension(288,144));
		l1.setMaximumSize(new Dimension(288,72));
		l2.setMaximumSize(new Dimension(288,108));
		l3.setMaximumSize(new Dimension(288,144));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(l1);
		mainPanel.add(l2);
		mainPanel.add(l3);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
		setLayout(new GridLayout());
		add(mainPanel);
	}
	
	private static JTextArea make(String s) {
		JTextArea a = new JTextArea(s);
		a.setWrapStyleWord(true);
		a.setLineWrap(true);
		a.setOpaque(false);
		a.setEditable(false);
		a.setFocusable(false);
		a.setBackground(null);
		return a;
	}
}
