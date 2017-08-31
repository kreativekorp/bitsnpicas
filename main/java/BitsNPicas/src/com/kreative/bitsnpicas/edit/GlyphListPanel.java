package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.kreative.bitsnpicas.Font;

public class GlyphListPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final CodePointListListModel cpllm;
	private final JList cpllv;
	private final JScrollPane cpllp;
	private final CodePointListListURLPanel cplls;
	private final GlyphList gl;
	private final JScrollPane glp;
	private final GlyphListCharacterDataPanel gls;
	
	public GlyphListPanel(Font<?> font, final SaveManager sm) {
		cpllm = new CodePointListListModel();
		cpllv = new JList(cpllm);
		cpllv.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cpllp = new JScrollPane(cpllv, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		cplls = new CodePointListListURLPanel(cpllv);
		gl = new GlyphList(font);
		glp = new JScrollPane(gl, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		gls = new GlyphListCharacterDataPanel(gl);
		JPanel left = new JPanel(new BorderLayout());
		left.add(setWidth(cpllp, 200), BorderLayout.CENTER);
		left.add(setWidth(cplls, 200), BorderLayout.PAGE_END);
		JPanel right = new JPanel(new BorderLayout());
		right.add(glp, BorderLayout.CENTER);
		right.add(gls, BorderLayout.PAGE_END);
		JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		main.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
		cpllv.setSelectedValue(gl.getCodePointList(), false);
		cpllv.addListSelectionListener(new ListSelectionListener() {
			@SuppressWarnings("unchecked")
			public void valueChanged(ListSelectionEvent e) {
				gl.setCodePointList((List<Integer>)cpllv.getSelectedValue());
			}
		});
		gl.addGlyphListListener(new GlyphListListener() {
			public void codePointsSelected(GlyphList gl, Font<?> font) {}
			public void codePointsOpened(GlyphList gl, Font<?> font) {
				for (int cp : gl.getSelectedCodePoints()) {
					Main.openGlyph(font, cp, gl, sm);
				}
			}
		});
	}
	
	public GlyphList getGlyphList() {
		return gl;
	}
	
	private static <C extends JComponent> C setWidth(C c, int width) {
		Dimension d = c.getPreferredSize();
		d.width = width;
		c.setMinimumSize(d);
		c.setPreferredSize(d);
		c.setMaximumSize(d);
		return c;
	}
}
