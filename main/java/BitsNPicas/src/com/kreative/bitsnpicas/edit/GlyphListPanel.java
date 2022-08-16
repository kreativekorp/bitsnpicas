package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.SortedSet;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;
import com.kreative.bitsnpicas.edit.GlyphListModelList.GlyphListModelTreeNode;

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
		left.add(setWidth(modelPane, 240), BorderLayout.CENTER);
		left.add(setWidth(urlPanel, 240), BorderLayout.PAGE_END);
		JPanel right = new JPanel(new BorderLayout());
		right.add(glyphPane, BorderLayout.CENTER);
		right.add(dataPanel, BorderLayout.PAGE_END);
		JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		main.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
		
		modelList.setSelectedModel(glyphList.getModel(), false);
		modelList.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				glyphList.setModel(modelList.getSelectedModel());
			}
		});
		
		glyphList.addGlyphListListener(new GlyphListListener<G>() {
			public void selectionChanged(GlyphList<G> gl, Font<G> font) {}
			public void selectionOpened(GlyphList<G> gl, Font<G> font) {
				List<GlyphLocator<G>> selectedGlyphs = gl.getSelection();
				if (
					(selectedGlyphs.size() < 5) ||
					(JOptionPane.showConfirmDialog(
						GlyphListPanel.this,
						"Are you sure you want to edit " + selectedGlyphs.size() + " glyphs?",
						"Edit Glyphs",
						JOptionPane.OK_CANCEL_OPTION
					) == JOptionPane.OK_OPTION)
				) {
					for (GlyphLocator<G> loc : selectedGlyphs) {
						Main.openGlyph(font, loc, gl, sm);
					}
				}
				SortedSet<Integer> selectedIndices = gl.getSelectedIndices();
				if (selectedIndices.size() == 1) {
					int i = selectedIndices.first();
					Integer cp = gl.getModel().getCodePoint(i);
					if (cp != null && cp.intValue() == GlyphListModelList.SUBTABLE_MARKER) {
						String name = "Subtable " + Integer.toHexString(0xFF00 | i).substring(2).toUpperCase();
						TreePath path = modelList.getSelectionPath();
						GlyphListModelTreeNode node = (GlyphListModelTreeNode)path.getLastPathComponent();
						for (GlyphListModelTreeNode child : node.getChildren()) {
							if (name.equals(child.toString())) {
								path = path.pathByAddingChild(child);
								modelList.setSelectionPath(path);
								modelList.scrollPathToVisible(path);
								JViewport vp = modelPane.getViewport();
								Point p = vp.getViewPosition();
								p.x = 0;
								vp.setViewPosition(p);
								return;
							}
						}
					}
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
