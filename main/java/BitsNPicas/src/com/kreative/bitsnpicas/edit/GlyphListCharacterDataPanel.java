package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.unicode.CharacterData;
import com.kreative.bitsnpicas.unicode.CharacterDatabase;

public class GlyphListCharacterDataPanel extends JPanel implements GlyphListListener {
	private static final long serialVersionUID = 1L;
	
	private final CharacterDatabase cdb;
	private final JLabel label;
	
	public GlyphListCharacterDataPanel(GlyphList gl) {
		this.cdb = CharacterDatabase.instance();
		this.label = new JLabel(" ");
		this.label.setFont(this.label.getFont().deriveFont(10f));
		this.label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
		setLayout(new BorderLayout());
		add(this.label, BorderLayout.LINE_START);
		if (gl != null) gl.addGlyphListListener(this);
	}
	
	public void codePointsSelected(GlyphList gl, Font<?> font) {
		List<Integer> cps = gl.getSelectedCodePoints();
		if (cps.size() == 1) {
			int codePoint = cps.get(0);
			String h = Integer.toHexString(codePoint).toUpperCase();
			while (h.length() < 4) h = "0" + h;
			String s = "U+" + h + "    #" + codePoint;
			CharacterData cd = cdb.get(codePoint);
			if (cd != null) s += "    " + cd.category + "    " + cd;
			label.setText(s);
		} else if (cps.size() > 1) {
			label.setText(cps.size() + " characters selected");
		} else {
			label.setText(" ");
		}
	}
	
	public void codePointsOpened(GlyphList gl, Font<?> font) {}
	public void metricsChanged(GlyphList gl, Font<?> font) {}
	public void glyphsChanged(GlyphList gl, Font<?> font) {}
}
