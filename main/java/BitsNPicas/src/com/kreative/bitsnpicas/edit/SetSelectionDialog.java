package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.kreative.bitsnpicas.unicode.Block;

public class SetSelectionDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private GlyphList gl;
	private JRadioButton byCodePointButton;
	private JRadioButton byIndexButton;
	private JTextArea selectionField;
	private JButton cancelButton;
	private JButton okButton;
	
	public SetSelectionDialog(Dialog parent, GlyphList gl) {
		super(parent, "Set Selection");
		setModal(true);
		this.gl = gl;
		make();
	}
	
	public SetSelectionDialog(Frame parent, GlyphList gl) {
		super(parent, "Set Selection");
		setModal(true);
		this.gl = gl;
		make();
	}
	
	public SetSelectionDialog(Window parent, GlyphList gl) {
		super(parent, "Set Selection");
		setModal(true);
		this.gl = gl;
		make();
	}
	
	private void make() {
		this.byCodePointButton = new JRadioButton("By Code Point");
		this.byIndexButton = new JRadioButton("By Index");
		this.selectionField = new JTextArea();
		this.cancelButton = new JButton("Cancel");
		this.okButton = new JButton("OK");
		
		List<Integer> codePointList = gl.getCodePointList();
		boolean byIndex = !(codePointList instanceof Block);
		this.byCodePointButton.setSelected(!byIndex);
		this.byIndexButton.setSelected(byIndex);
		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(this.byCodePointButton);
		bg1.add(this.byIndexButton);
		JPanel bp1 = new JPanel(new GridLayout(1, 0, 8, 8));
		bp1.add(this.byCodePointButton);
		bp1.add(this.byIndexButton);
		
		Dimension d = new Dimension(240, 120);
		this.selectionField.setMinimumSize(d);
		this.selectionField.setPreferredSize(d);
		this.selectionField.setText(
			byIndex ?
			intsToString(gl.getSelectedIndices(), false) :
			intsToString(gl.getSelectedCodePoints(), true)
		);
		JScrollPane sp = new JScrollPane(
			this.selectionField,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		JPanel cp = new JPanel(new BorderLayout(8, 8));
		cp.add(bp1, BorderLayout.PAGE_START);
		cp.add(sp, BorderLayout.CENTER);
		
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
		selectionField.requestFocusInWindow();
		
		byCodePointButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectionField.setText(intsToString(gl.getSelectedCodePoints(), true));
			}
		});
		byIndexButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectionField.setText(intsToString(gl.getSelectedIndices(), false));
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Collection<Integer> c = stringToInts(selectionField.getText());
				if (byIndexButton.isSelected()) gl.setSelectedIndices(c);
				else gl.setSelectedCodePoints(c);
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	private static String intsToString(Collection<Integer> c, boolean hex) {
		Integer[] a = c.toArray(new Integer[c.size()]);
		Arrays.sort(a);
		List<int[]> r = new ArrayList<int[]>();
		for (int i : a) {
			if (r.isEmpty()) {
				r.add(new int[]{i, i});
			} else if (i == r.get(r.size() - 1)[1] + 1) {
				r.get(r.size() - 1)[1] = i;
			} else {
				r.add(new int[]{i, i});
			}
		}
		StringBuffer s = new StringBuffer();
		for (int[] p : r) {
			if (s.length() > 0) {
				s.append(", ");
			}
			if (hex) {
				s.append("U+");
				String h = Integer.toHexString(p[0]).toUpperCase();
				for (int i = h.length(); i < 4; i++) s.append("0");
				s.append(h);
			} else {
				s.append(p[0]);
			}
			if (p[0] != p[1]) {
				s.append("-");
				if (hex) {
					s.append("U+");
					String h = Integer.toHexString(p[1]).toUpperCase();
					for (int i = h.length(); i < 4; i++) s.append("0");
					s.append(h);
				} else {
					s.append(p[1]);
				}
			}
		}
		return s.toString();
	}
	
	private static Collection<Integer> stringToInts(String s) {
		List<Integer> c = new ArrayList<Integer>();
		String[] r = s.split("[.,:;]");
		for (String q : r) {
			String[] p = q.split("-", 2);
			try {
				switch (p.length) {
					case 2:
						int p0 = parseInt(p[0].trim());
						int p1 = parseInt(p[1].trim());
						int start = Math.min(p0, p1);
						int end = Math.max(p0, p1);
						while (start <= end) c.add(start++);
						break;
					case 1:
						c.add(parseInt(p[0].trim()));
						break;
				}
			} catch (NumberFormatException nfe) {
				// Ignored.
			}
		}
		return c;
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
