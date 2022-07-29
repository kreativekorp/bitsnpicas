package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public class GlyphListPanel<G extends FontGlyph> extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final GlyphListModelList modelList;
	private final JScrollPane modelPane;
	private final GlyphListURLPanel urlPanel;
	private final GlyphList<G> glyphList;
	private final JScrollPane glyphPane;
	private final GlyphListDataPanel<G> dataPanel;
	
	public GlyphListPanel(Font<G> font, final SaveManager sm) {
		modelList = new GlyphListModelList(font);
		modelPane = new JScrollPane(modelList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		urlPanel = new GlyphListURLPanel(modelList);
		glyphList = new GlyphList<G>(font);
		glyphPane = new JScrollPane(glyphList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dataPanel = new GlyphListDataPanel<G>(glyphList);
		
		JPanel left = new JPanel(new BorderLayout());
		left.add(setWidth(modelPane, 200), BorderLayout.CENTER);
		left.add(setWidth(urlPanel, 200), BorderLayout.PAGE_END);
		JPanel right = new JPanel(new BorderLayout());
		right.add(glyphPane, BorderLayout.CENTER);
		right.add(dataPanel, BorderLayout.PAGE_END);
		JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		main.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
		
		modelList.setSelectedModel(glyphList.getModel(), false);
		modelList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				glyphList.setModel(modelList.getSelectedModel());
			}
		});
		
		glyphList.addGlyphListListener(new GlyphListListener<G>() {
			public void selectionChanged(GlyphList<G> gl, Font<G> font) {}
			public void selectionOpened(GlyphList<G> gl, Font<G> font) {
				for (GlyphLocator<G> loc : gl.getSelection()) {
					Main.openGlyph(font, loc, gl, sm);
				}
			}
			public void metricsChanged(GlyphList<G> gl, Font<G> font) {
				sm.setChanged();
			}
			public void glyphsChanged(GlyphList<G> gl, Font<G> font) {
				sm.setChanged();
			}
		});
		
		InputMap im = glyphPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke("UP"), "none");
		im.put(KeyStroke.getKeyStroke("DOWN"), "none");
		im.put(KeyStroke.getKeyStroke("LEFT"), "none");
		im.put(KeyStroke.getKeyStroke("RIGHT"), "none");
	}
	
	public GlyphList<G> getGlyphList() {
		return glyphList;
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
