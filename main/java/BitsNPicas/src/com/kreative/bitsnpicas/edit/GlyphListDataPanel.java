package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;
import com.kreative.unicode.data.NameResolver;

public class GlyphListDataPanel<G extends FontGlyph> extends JPanel implements GlyphListListener<G> {
	private static final long serialVersionUID = 1L;
	private static final String T = "    ";
	
	private final JLabel label;
	
	public GlyphListDataPanel(GlyphList<G> gl) {
		this.label = new JLabel(" ");
		this.label.setFont(this.label.getFont().deriveFont(10f));
		this.label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
		setLayout(new BorderLayout());
		add(this.label, BorderLayout.LINE_START);
		if (gl != null) gl.addGlyphListListener(this);
	}
	
	public void selectionChanged(GlyphList<G> gl, Font<G> font) {
		List<GlyphLocator<G>> locators = gl.getSelection();
		if (locators.isEmpty()) {
			label.setText(" ");
		} else if (locators.size() > 1) {
			label.setText(locators.size() + " glyphs selected");
		} else if (locators.get(0).isCodePoint()) {
			int cp = locators.get(0).getCodePoint();
			String h = Integer.toHexString(cp).toUpperCase();
			while (h.length() < 4) h = "0" + h;
			NameResolver r = NameResolver.instance(cp);
			String c = r.getCategory(cp), n = r.getName(cp);
			label.setText("U+" + h + T + "#" + cp + T + c + T + n);
		} else if (locators.get(0).isGlyphName()) {
			label.setText(locators.get(0).getGlyphName());
		} else {
			label.setText("1 glyph selected");
		}
	}
	
	public void selectionOpened(GlyphList<G> gl, Font<G> font) {}
	public void metricsChanged(GlyphList<G> gl, Font<G> font) {}
	public void glyphsChanged(GlyphList<G> gl, Font<G> font) {}
}
