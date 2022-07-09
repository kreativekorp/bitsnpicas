package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.Font;
import com.kreative.unicode.data.NameResolver;

public class GlyphListCharacterDataPanel extends JPanel implements GlyphListListener {
	private static final long serialVersionUID = 1L;
	private static final String T = "    ";
	
	private final JLabel label;
	
	public GlyphListCharacterDataPanel(GlyphList gl) {
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
			int cp = cps.get(0);
			String h = Integer.toHexString(cp).toUpperCase();
			while (h.length() < 4) h = "0" + h;
			NameResolver r = NameResolver.instance(cp);
			String c = r.getCategory(cp), n = r.getName(cp);
			label.setText("U+" + h + T + "#" + cp + T + c + T + n);
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
