package com.kreative.bitsnpicas.mover;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TrueTypePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public TrueTypePanel(Font font) {
		JLabel l1 = new JLabel("<html>How razorback-jumping frogs can level six piqued gymnasts!</html>");
		JLabel l2 = new JLabel("<html>Cozy lummox gives smart squid who asks for job pen.</html>");
		JLabel l3 = new JLabel("<html>The quick brown fox jumps over the lazy dog.</html>");
		l1.setFont(font.deriveFont(9f));
		l2.setFont(font.deriveFont(12f));
		l3.setFont(font.deriveFont(18f));
		l1.setVerticalAlignment(JLabel.TOP);
		l2.setVerticalAlignment(JLabel.TOP);
		l3.setVerticalAlignment(JLabel.TOP);
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
}
