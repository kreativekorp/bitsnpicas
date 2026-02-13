package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.VectorFont;

public class NewVectorFontFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final NewVectorFontPanel panel;
	private final JButton okButton;
	private final JButton cancelButton;
	
	public NewVectorFontFrame() {
		this.panel = new NewVectorFontPanel();
		this.okButton = new JButton("OK");
		this.cancelButton = new JButton("Cancel");
		
		JPanel buttonPanel1 = new JPanel(new GridLayout(0, 1, 4, 4));
		buttonPanel1.add(okButton);
		buttonPanel1.add(cancelButton);
		
		JPanel buttonPanel2 = new JPanel(new BorderLayout());
		buttonPanel2.add(buttonPanel1, BorderLayout.PAGE_START);
		
		JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
		mainPanel.add(panel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel2, BorderLayout.LINE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		setTitle("New Vector Font");
		setJMenuBar(new CommonMenuItems(this));
		setContentPane(mainPanel);
		SwingUtils.setDefaultButton(getRootPane(), okButton);
		SwingUtils.setCancelButton(getRootPane(), cancelButton);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VectorFont f = panel.createVectorFont();
				Main.openFont(null, Main.getSaveFormat(f), f);
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
}
