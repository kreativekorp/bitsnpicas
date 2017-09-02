package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class MoveGlyphsDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public static class Result {
		public final boolean byIndex;
		public final boolean relative;
		public final int offset;
		public Result(boolean byIndex, boolean relative, int offset) {
			this.byIndex = byIndex;
			this.relative = relative;
			this.offset = offset;
		}
	}
	
	private JRadioButton byCodePointButton;
	private JRadioButton byIndexButton;
	private JTextField offsetField;
	private JButton cancelButton;
	private JButton okButton;
	private Result result;
	
	public MoveGlyphsDialog(Dialog parent, boolean copy, boolean byIndex) {
		super(parent, copy ? "Copy Glyphs" : "Move Glyphs");
		setModal(true);
		make(byIndex);
	}
	
	public MoveGlyphsDialog(Frame parent, boolean copy, boolean byIndex) {
		super(parent, copy ? "Copy Glyphs" : "Move Glyphs");
		setModal(true);
		make(byIndex);
	}
	
	public MoveGlyphsDialog(Window parent, boolean copy, boolean byIndex) {
		super(parent, copy ? "Copy Glyphs" : "Move Glyphs");
		setModal(true);
		make(byIndex);
	}
	
	private void make(boolean byIndex) {
		this.byCodePointButton = new JRadioButton("By Code Point");
		this.byIndexButton = new JRadioButton("By Index");
		this.offsetField = new JTextField("+0");
		this.cancelButton = new JButton("Cancel");
		this.okButton = new JButton("OK");
		
		this.byCodePointButton.setSelected(!byIndex);
		this.byIndexButton.setSelected(byIndex);
		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(this.byCodePointButton);
		bg1.add(this.byIndexButton);
		JPanel bp1 = new JPanel(new GridLayout(1, 0, 8, 8));
		bp1.add(this.byCodePointButton);
		bp1.add(this.byIndexButton);
		JPanel cp = new JPanel(new GridLayout(0, 1, 4, 4));
		cp.add(bp1);
		cp.add(this.offsetField);
		JPanel bp = new JPanel(new FlowLayout());
		bp.add(this.cancelButton);
		bp.add(this.okButton);
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.add(cp, BorderLayout.CENTER);
		mainPanel.add(bp, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		setContentPane(mainPanel);
		SwingUtils.setCancelButton(getRootPane(), cancelButton);
		SwingUtils.setDefaultButton(getRootPane(), okButton);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		offsetField.requestFocusInWindow();
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String offsetString = offsetField.getText().trim();
					boolean byIndex = byIndexButton.isSelected();
					boolean relative;
					int offset;
					if (offsetString.startsWith("+")) {
						relative = true;
						offset = +parseInt(offsetString.substring(1));
					} else if (offsetString.startsWith("-")) {
						relative = true;
						offset = -parseInt(offsetString.substring(1));
					} else if (offsetString.startsWith("@")) {
						relative = false;
						offset = parseInt(offsetString.substring(1));
					} else {
						relative = false;
						offset = parseInt(offsetString);
					}
					result = new Result(byIndex, relative, offset);
				} catch (NumberFormatException nfe) {
					result = null;
				}
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				result = null;
				dispose();
			}
		});
	}
	
	public Result showDialog() {
		result = null;
		setVisible(true);
		return result;
	}
	
	private static int parseInt(String s) {
		if (s.startsWith("0x")) return Integer.parseInt(s.substring(2), 16);
		if (s.startsWith("0X")) return Integer.parseInt(s.substring(2), 16);
		if (s.startsWith("U+")) return Integer.parseInt(s.substring(2), 16);
		if (s.startsWith("u+")) return Integer.parseInt(s.substring(2), 16);
		if (s.startsWith("$")) return Integer.parseInt(s.substring(1), 16);
		if (s.startsWith("#")) return Integer.parseInt(s.substring(1), 10);
		return Integer.parseInt(s, 10);
	}
}
