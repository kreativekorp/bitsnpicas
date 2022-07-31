package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.PatternSyntaxException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public abstract class MoveGlyphsDialog<G extends FontGlyph, S> extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private final boolean copy;
	private final GlyphList<G> glyphList;
	private JRadioButton byCodePointButton;
	private JRadioButton byIndexButton;
	private JTextField offsetField;
	private JButton cancelButton;
	private JButton okButton;
	
	public MoveGlyphsDialog(Dialog parent, boolean copy, GlyphList<G> gl) {
		super(parent, copy ? "Copy Glyphs" : "Move Glyphs");
		this.copy = copy;
		this.glyphList = gl;
		setModal(true);
		make();
	}
	
	public MoveGlyphsDialog(Frame parent, boolean copy, GlyphList<G> gl) {
		super(parent, copy ? "Copy Glyphs" : "Move Glyphs");
		this.copy = copy;
		this.glyphList = gl;
		setModal(true);
		make();
	}
	
	public MoveGlyphsDialog(Window parent, boolean copy, GlyphList<G> gl) {
		super(parent, copy ? "Copy Glyphs" : "Move Glyphs");
		this.copy = copy;
		this.glyphList = gl;
		setModal(true);
		make();
	}
	
	private void make() {
		// The order in which we do things here is very important!
		// If we get it wrong we can completely screw it up.
		
		// Get the locators for the selected glyphs.
		// Must be done before calling tracksFont()
		// in case that changes the model's glyph list.
		final List<GlyphLocator<G>> locators = glyphList.getSelection();
		
		// Disable moving by index if the model tracks
		// the font because what does that even mean?
		final GlyphListModel model = glyphList.getModel();
		Boolean byIndex = (
			model.tracksFont() ?
			(copy ? Boolean.FALSE : null) :
			Boolean.valueOf(!isUnicodeRange(model))
		);
		
		this.byCodePointButton = new JRadioButton("By Code Point");
		this.byIndexButton = new JRadioButton("By Index");
		this.offsetField = new JTextField("+0");
		this.cancelButton = new JButton("Cancel");
		this.okButton = new JButton("OK");
		
		if (byIndex == null) {
			this.byCodePointButton.setSelected(true);
			this.byIndexButton.setSelected(false);
			this.byIndexButton.setEnabled(false);
		} else {
			this.byCodePointButton.setSelected(!byIndex);
			this.byIndexButton.setSelected(byIndex);
		}
		
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
		
		offsetField.addKeyListener(new KeyAdapter() {
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
				String offsetString = offsetField.getText().trim();
				boolean byIndex = byIndexButton.isSelected();
				try {
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
					
					// Create a copy of the selected glyphs.
					List<S> states = serializeGlyphs(locators);
					
					// If this is a move operation, remove the
					// selected glyphs from their current location.
					// This also calls tracksFont() again afterwards.
					if (!copy) glyphList.deleteSelection();
					
					// Put the glyphs back in the destination location.
					if (byIndex) moveByIndex(locators, states, relative, offset);
					else moveByCodePoint(locators, states, relative, offset);
					
				} catch (NumberFormatException nfe) {
					if (offsetString.length() > 0) {
						List<S> states = serializeGlyphs(locators);
						if (!copy) glyphList.deleteSelection();
						moveByGlyphName(locators, states, offsetString);
					} else {
						Toolkit.getDefaultToolkit().beep();
					}
				}
				dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	public abstract S serializeGlyph(G g);
	public abstract G deserializeGlyph(S s);
	
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
	
	private static int parseInt(String s) {
		if (s.startsWith("0x")) return Integer.parseInt(s.substring(2), 16);
		if (s.startsWith("0X")) return Integer.parseInt(s.substring(2), 16);
		if (s.startsWith("U+")) return Integer.parseInt(s.substring(2), 16);
		if (s.startsWith("u+")) return Integer.parseInt(s.substring(2), 16);
		if (s.startsWith("$")) return Integer.parseInt(s.substring(1), 16);
		if (s.startsWith("#")) return Integer.parseInt(s.substring(1), 10);
		return Integer.parseInt(s, 10);
	}
	
	private List<S> serializeGlyphs(List<GlyphLocator<G>> locators) {
		// Create a copy of the selected glyphs.
		List<S> states = new ArrayList<S>();
		for (GlyphLocator<G> loc : locators) {
			G glyph = loc.getGlyph();
			if (glyph == null) states.add(null);
			else states.add(serializeGlyph(glyph));
		}
		return states;
	}
	
	private void moveByIndex(List<GlyphLocator<G>> locators, List<S> states, boolean relative, int offset) {
		Font<G> font = glyphList.getGlyphFont();
		TreeSet<Integer> selectedIndices = new TreeSet<Integer>();
		GlyphListModel model = glyphList.getModel();
		int gn = model.getCellCount();
		for (int i = 0, n = locators.size(); i < n; i++) {
			int gi = locators.get(i).getGlyphIndex();
			if (gi < 0) continue;
			if (relative) gi += offset;
			else gi = offset + i;
			if (gi < 0 || gi >= gn) continue;
			selectedIndices.add(gi);
			S state = states.get(i);
			if (state == null) continue;
			new GlyphLocator<G>(font, model, gi).setGlyph(deserializeGlyph(state));
		}
		glyphList.glyphRepertoireChanged();
		glyphList.setSelectedIndices(selectedIndices, true);
	}
	
	private void moveByCodePoint(List<GlyphLocator<G>> locators, List<S> states, boolean relative, int offset) {
		// Put the glyphs at the destination code points before
		// setting the selection since the indices may change.
		Font<G> font = glyphList.getGlyphFont();
		TreeSet<Integer> selectedCodePoints = new TreeSet<Integer>();
		List<GlyphLocator<G>> selectedLocators = new ArrayList<GlyphLocator<G>>();
		for (int i = 0, n = locators.size(); i < n; i++) {
			int cp;
			if (relative) {
				GlyphLocator<G> loc = locators.get(i);
				if (loc.isCodePoint()) {
					cp = loc.getCodePoint() + offset;
				} else {
					selectedLocators.add(loc);
					S state = states.get(i);
					if (state == null) continue;
					loc.setGlyph(deserializeGlyph(state));
					continue;
				}
			} else {
				cp = offset + i;
			}
			if (Character.isValidCodePoint(cp)) {
				selectedCodePoints.add(cp);
				S state = states.get(i);
				if (state == null) continue;
				font.putCharacter(cp, deserializeGlyph(state));
			}
		}
		glyphList.glyphRepertoireChanged();
		
		// Calculate the selected indices now that the font is done changing.
		GlyphListModel model = glyphList.getModel();
		TreeSet<Integer> selectedIndices = new TreeSet<Integer>();
		for (int cp : selectedCodePoints) selectedIndices.add(model.indexOfCodePoint(cp));
		for (GlyphLocator<G> loc : selectedLocators) selectedIndices.add(loc.getGlyphIndex());
		glyphList.setSelectedIndices(selectedIndices, true);
	}
	
	private void moveByGlyphName(List<GlyphLocator<G>> locators, List<S> states, String baseName) {
		// Put the glyphs at the destination glyph names before
		// setting the selection since the indices may change.
		Font<G> font = glyphList.getGlyphFont();
		TreeSet<String> selectedGlyphNames = new TreeSet<String>();
		GlyphListModel model = glyphList.getModel();
		int gn = model.getCellCount();
		for (int i = 0, n = locators.size(); i < n; i++) {
			String glyphName = formatGlyphName(baseName, i, n, locators.get(i), gn);
			if (glyphName.length() == 0) continue;
			selectedGlyphNames.add(glyphName);
			S state = states.get(i);
			if (state == null) continue;
			font.putNamedGlyph(glyphName, deserializeGlyph(state));
		}
		glyphList.glyphRepertoireChanged();
		
		// Calculate the selected indices now that the font is done changing.
		TreeSet<Integer> selectedIndices = new TreeSet<Integer>();
		for (String n : selectedGlyphNames) selectedIndices.add(model.indexOfGlyphName(n));
		glyphList.setSelectedIndices(selectedIndices, true);
	}
	
	private String formatGlyphName(String baseName, int li, int lim, GlyphLocator<G> loc, int gim) {
		int gi = loc.getGlyphIndex();
		StringBuffer name = new StringBuffer();
		char[] chars = baseName.toCharArray();
		int ci = 0, cn = chars.length;
		while (ci < cn) {
			char ch = chars[ci++];
			if (ch == '%' && ci < cn) {
				StringBuffer vws = new StringBuffer();
				int vw = 0;
				char vc = chars[ci++];
				int vd = Character.digit(vc, 10);
				while (vd >= 0 && ci < cn) {
					vws.append(vc);
					vw = vw * 10 + vd;
					vc = chars[ci++];
					vd = Character.digit(vc, 10);
				}
				switch (vc) {
					case '%':
						// literal %
						name.append('%');
						break;
					case 'u': case 'U':
						// Unicode code point, decimal
						if (loc.isCodePoint()) {
							String s = Integer.toString(loc.getCodePoint());
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					case 'x': case 'X':
						// Unicode code point, hexadecimal
						if (loc.isCodePoint()) {
							String s = Integer.toHexString(loc.getCodePoint());
							if (vc == 'x') s = s.toLowerCase();
							if (vc == 'X') s = s.toUpperCase();
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					case 's': case 'S':
						// glyph name
						{
							String s;
							if (loc.isCodePoint()) {
								int cp = loc.getCodePoint();
								s = Integer.toHexString(cp).toUpperCase();
								while (s.length() < 4) s = "0" + s;
								if (cp < 0x10000) s = "uni" + s;
								else s = "u" + s;
							} else if (loc.isGlyphName()) {
								s = loc.getGlyphName();
							} else {
								s = "";
							}
							if (ci < cn && chars[ci] == '/') {
								ci++;
								StringBuffer pb = new StringBuffer();
								while (ci < cn) {
									char pc = chars[ci++];
									if (pc == '/') break;
									if (pc == '\\' && ci < cn) {
										pc = chars[ci++];
										if (pc != '/') pb.append('\\');
									}
									pb.append(pc);
								}
								StringBuffer rb = new StringBuffer();
								while (ci < cn) {
									char rc = chars[ci++];
									if (rc == '/') break;
									if (rc == '\\' && ci < cn) {
										rc = chars[ci++];
										if (rc != '/') rb.append('\\');
									}
									rb.append(rc);
								}
								try {
									s = s.replaceAll(pb.toString(), rb.toString());
								} catch (PatternSyntaxException e) {
									s = s.replace(pb.toString(), rb.toString());
								}
							}
							name.append(s);
						}
						break;
					case 'd': case 'D':
						// glyph index, decimal
						if (gi >= 0) {
							String s = Integer.toString(gi);
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					case 'o': case 'O':
						// glyph index, octal
						if (gi >= 0) {
							String s = Integer.toString(gi, 8);
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					case 'h': case 'H':
						// glyph index, hexadecimal
						if (gi >= 0) {
							String s = Integer.toHexString(gi);
							if (vc == 'h') s = s.toLowerCase();
							if (vc == 'H') s = s.toUpperCase();
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					case 'i':
						// selection index, zero based
						if (li >= 0) {
							String s = Integer.toString(li);
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					case 'j':
						// selection index, one based
						if (li >= 0) {
							String s = Integer.toString(li + 1);
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					case 'n':
						// number of glyphs in selection
						if (lim >= 0) {
							String s = Integer.toString(lim);
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					case 'I':
						// glyph index, zero based
						if (gi >= 0) {
							String s = Integer.toString(gi);
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					case 'J':
						// glyph index, one based
						if (gi >= 0) {
							String s = Integer.toString(gi + 1);
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					case 'N':
						// number of glyphs in glyph list
						if (gim >= 0) {
							String s = Integer.toString(gim);
							for (int x = s.length(); x < vw; x++) name.append('0');
							name.append(s);
						}
						break;
					default:
						// invalid specifier
						name.append('%');
						name.append(vws);
						name.append(vc);
						break;
				}
			} else {
				name.append(ch);
			}
		}
		return name.toString().trim();
	}
}
