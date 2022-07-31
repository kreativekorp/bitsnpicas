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
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public abstract class CreateGlyphsDialog<G extends FontGlyph> extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private final GlyphList<G> glyphList;
	private JRadioButton byCodePointButton;
	private JRadioButton byIndexButton;
	private JCheckBox overwriteButton;
	private JTextArea specificationField;
	private JButton cancelButton;
	private JButton okButton;
	
	public CreateGlyphsDialog(Dialog parent, GlyphList<G> gl) {
		super(parent, "Create Glyphs");
		this.glyphList = gl;
		setModal(true);
		make();
	}
	
	public CreateGlyphsDialog(Frame parent, GlyphList<G> gl) {
		super(parent, "Create Glyphs");
		this.glyphList = gl;
		setModal(true);
		make();
	}
	
	public CreateGlyphsDialog(Window parent, GlyphList<G> gl) {
		super(parent, "Create Glyphs");
		this.glyphList = gl;
		setModal(true);
		make();
	}
	
	private void make() {
		this.byCodePointButton = new JRadioButton("By Code Point");
		this.byIndexButton = new JRadioButton("By Index");
		this.overwriteButton = new JCheckBox("Overwrite existing glyphs");
		this.specificationField = new JTextArea();
		this.cancelButton = new JButton("Cancel");
		this.okButton = new JButton("OK");
		
		boolean isUnicode = isUnicodeRange(glyphList.getModel());
		this.byCodePointButton.setSelected(isUnicode);
		this.byIndexButton.setSelected(!isUnicode);
		
		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(this.byCodePointButton);
		bg1.add(this.byIndexButton);
		JPanel bp1 = new JPanel(new GridLayout(1, 0, 8, 8));
		bp1.add(this.byCodePointButton);
		bp1.add(this.byIndexButton);
		JPanel bp2 = new JPanel(new GridLayout(0, 1, 4, 4));
		bp2.add(bp1);
		bp2.add(new JLabel("(Non-numeric values will be treated as glyph names.)"));
		bp2.add(this.overwriteButton);
		
		Dimension d = new Dimension(240, 120);
		this.specificationField.setMinimumSize(d);
		this.specificationField.setPreferredSize(d);
		this.specificationField.setLineWrap(true);
		this.specificationField.setWrapStyleWord(true);
		JScrollPane sp = new JScrollPane(
			this.specificationField,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		
		JPanel cp = new JPanel(new BorderLayout(8, 8));
		cp.add(bp2, BorderLayout.PAGE_START);
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
		specificationField.requestFocusInWindow();
		
		specificationField.addKeyListener(new KeyAdapter() {
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
					case KeyEvent.VK_O:
						if (e.isControlDown() || e.isMetaDown()) {
							overwriteButton.doClick();
						}
						break;
				}
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createGlyphs(
					specificationField.getText(),
					byIndexButton.isSelected(),
					overwriteButton.isSelected()
				);
				dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	public abstract G createGlyph();
	
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
	
	private void createGlyphs(String s, boolean byIndex, boolean overwrite) {
		// Get the specified glyph identifiers before creating any glyphs.
		// If we create the glyphs at the same time the indices may change.
		Font<G> font = glyphList.getGlyphFont();
		GlyphListModel model = glyphList.getModel();
		List<GlyphLocator<G>> specifiedLocators = new ArrayList<GlyphLocator<G>>();
		TreeSet<Integer> specifiedCodePoints = new TreeSet<Integer>();
		TreeSet<String> specifiedGlyphNames = new TreeSet<String>();
		String[] parts = s.split("[,;]");
		for (String part : parts) {
			try {
				int[] range = parseRange(part);
				if (byIndex) {
					int n = model.getCellCount();
					for (int i = range[0]; i <= range[1]; i++) {
						if (i >= 0 && i < n) {
							specifiedLocators.add(new GlyphLocator<G>(font, model, i));
						}
					}
				} else {
					for (int cp = range[0]; cp <= range[1]; cp++) {
						specifiedCodePoints.add(cp);
					}
				}
			} catch (NumberFormatException nfe) {
				if ((part = part.trim()).length() > 0) {
					parseGlyphNames(part, specifiedGlyphNames);
				}
			}
		}
		
		// Put the glyphs at the destination glyph names before
		// setting the selection since the indices may change.
		for (GlyphLocator<G> loc : specifiedLocators) {
			if (overwrite || loc.getGlyph() == null) loc.setGlyph(createGlyph());
		}
		for (int cp : specifiedCodePoints) {
			if (overwrite || font.getCharacter(cp) == null) font.putCharacter(cp, createGlyph());
		}
		for (String gn : specifiedGlyphNames) {
			if (overwrite || font.getNamedGlyph(gn) == null) font.putNamedGlyph(gn, createGlyph());
		}
		glyphList.glyphRepertoireChanged();
		
		// Calculate the selected indices now that the font is done changing.
		TreeSet<Integer> selectedIndices = new TreeSet<Integer>();
		for (GlyphLocator<G> loc : specifiedLocators) selectedIndices.add(loc.getGlyphIndex());
		for (int cp : specifiedCodePoints) selectedIndices.add(model.indexOfCodePoint(cp));
		for (String n : specifiedGlyphNames) selectedIndices.add(model.indexOfGlyphName(n));
		glyphList.setSelectedIndices(selectedIndices, true);
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
	
	private static final Pattern RANGE_PATTERN = Pattern.compile("\\{([0-9]+)(-+|:+|\\.\\.+)([0-9]+)\\}");
	private static void parseGlyphNames(String s, Collection<String> names) {
		Matcher m = RANGE_PATTERN.matcher(s);
		if (m.find()) {
			String s1 = m.group(1);
			String s3 = m.group(3);
			int v1 = Integer.parseInt(s1);
			int v3 = Integer.parseInt(s3);
			int length = Math.min(s1.length(), s3.length());
			int start = Math.min(v1, v3);
			int end = Math.max(v1, v3);
			String prefix = s.substring(0, m.start());
			String suffix = s.substring(m.end());
			for (int vi = start; vi <= end; vi++) {
				String si = Integer.toString(vi);
				while (si.length() < length) si = "0" + si;
				parseGlyphNames(prefix + si + suffix, names);
			}
		} else {
			names.add(s);
		}
	}
}
