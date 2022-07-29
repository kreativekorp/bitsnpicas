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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SetSelectionDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private GlyphList<?> gl;
	private JRadioButton byCodePointButton;
	private JRadioButton byIndexButton;
	private JTextArea selectionField;
	private JButton cancelButton;
	private JButton okButton;
	
	public SetSelectionDialog(Dialog parent, GlyphList<?> gl) {
		super(parent, "Set Selection");
		setModal(true);
		this.gl = gl;
		make();
	}
	
	public SetSelectionDialog(Frame parent, GlyphList<?> gl) {
		super(parent, "Set Selection");
		setModal(true);
		this.gl = gl;
		make();
	}
	
	public SetSelectionDialog(Window parent, GlyphList<?> gl) {
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
		
		boolean isUnicode = isUnicodeRange(gl.getModel());
		this.byCodePointButton.setSelected(isUnicode);
		this.byIndexButton.setSelected(!isUnicode);
		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(this.byCodePointButton);
		bg1.add(this.byIndexButton);
		JPanel bp1 = new JPanel(new GridLayout(1, 0, 8, 8));
		bp1.add(this.byCodePointButton);
		bp1.add(this.byIndexButton);
		
		Dimension d = new Dimension(240, 120);
		this.selectionField.setMinimumSize(d);
		this.selectionField.setPreferredSize(d);
		this.selectionField.setLineWrap(true);
		this.selectionField.setWrapStyleWord(true);
		this.selectionField.setText(selectionToString(gl.getSelection(), !isUnicode));
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
				selectionField.setText(selectionToString(gl.getSelection(), false));
			}
		});
		byIndexButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectionField.setText(selectionToString(gl.getSelection(), true));
			}
		});
		
		selectionField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_ENTER:
						okButton.doClick();
						break;
					case KeyEvent.VK_ESCAPE:
						cancelButton.doClick();
						break;
					case KeyEvent.VK_U:
						if (e.isControlDown() || e.isMetaDown()) {
							byCodePointButton.doClick();
						}
						break;
					case KeyEvent.VK_I:
						if (e.isControlDown() || e.isMetaDown()) {
							byIndexButton.doClick();
						}
						break;
				}
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gl.setSelectedIndices(stringToIndices(
					selectionField.getText(),
					gl.getModel(),
					byIndexButton.isSelected()
				), true);
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	private static boolean isUnicodeRange(GlyphListModel model) {
		for (int lastCP = -1, i = 0, n = model.getCellCount(); i < n; i++) {
			if (model.isCodePoint(i)) {
				int cp = model.getCodePoint(i);
				if (lastCP < 0 || (lastCP + 1) == cp) {
					lastCP = cp;
					continue;
				}
			}
			return false;
		}
		return true;
	}
	
	private static String selectionToString(Collection<? extends GlyphLocator<?>> locators, boolean byIndex) {
		List<Integer> intValues = new ArrayList<Integer>();
		List<String> stringValues = new ArrayList<String>();
		for (GlyphLocator<?> loc : locators) {
			if (byIndex) intValues.add(loc.getGlyphIndex());
			else if (loc.isCodePoint()) intValues.add(loc.getCodePoint());
			else if (loc.isGlyphName()) stringValues.add(loc.getGlyphName());
		}
		Collections.sort(intValues);
		Collections.sort(stringValues);
		StringBuffer sb = new StringBuffer();
		List<int[]> runs = new ArrayList<int[]>();
		for (int i : intValues) {
			if (runs.isEmpty()) {
				runs.add(new int[]{i, i});
			} else if (i == runs.get(runs.size() - 1)[1] + 1) {
				runs.get(runs.size() - 1)[1] = i;
			} else {
				runs.add(new int[]{i, i});
			}
		}
		for (int[] run : runs) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			if (byIndex) {
				sb.append(run[0]);
			} else {
				sb.append("U+");
				String h = Integer.toHexString(run[0]).toUpperCase();
				for (int i = h.length(); i < 4; i++) sb.append("0");
				sb.append(h);
			}
			if (run[0] != run[1]) {
				sb.append("-");
				if (byIndex) {
					sb.append(run[1]);
				} else {
					sb.append("U+");
					String h = Integer.toHexString(run[1]).toUpperCase();
					for (int i = h.length(); i < 4; i++) sb.append("0");
					sb.append(h);
				}
			}
		}
		for (String s : stringValues) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(s);
		}
		return sb.toString();
	}
	
	private static Collection<Integer> stringToIndices(String s, GlyphListModel model, boolean byIndex) {
		HashSet<Integer> indices = new HashSet<Integer>();
		String[] parts = s.split("[,;]");
		for (String part : parts) {
			try {
				int[] range = parseRange(part);
				if (byIndex) {
					int n = model.getCellCount();
					for (int i = range[0]; i <= range[1]; i++) {
						if (i >= 0 && i < n) indices.add(i);
					}
				} else {
					for (int cp = range[0]; cp <= range[1]; cp++) {
						int i = model.indexOfCodePoint(cp);
						if (i >= 0) indices.add(i);
					}
				}
			} catch (NumberFormatException nfe) {
				if ((part = part.trim()).length() > 0) {
					int i = model.indexOfGlyphName(part);
					if (i >= 0) indices.add(i);
				}
			}
		}
		return indices;
	}
	
	private static int[] parseRange(String s) {
		String[] p = s.split("-+|:+|\\.\\.+", 2);
		if (p.length == 2) {
			int p0 = parseInt(p[0].trim());
			int p1 = parseInt(p[1].trim());
			int start = Math.min(p0, p1);
			int end = Math.max(p0, p1);
			return new int[]{ start, end };
		}
		int i = parseInt(s.trim());
		return new int[]{ i, i };
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
