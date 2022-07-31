package com.kreative.bitsnpicas.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import com.kreative.bitsnpicas.Font;
import com.kreative.unicode.data.Block;
import com.kreative.unicode.data.BlockList;
import com.kreative.unicode.data.Encoding;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.EncodingTable;
import com.kreative.unicode.data.GlyphList;
import com.kreative.unicode.data.GlyphLists;

public class GlyphListModelList extends JTree {
	private static final long serialVersionUID = 1L;
	public static final int SEQUENCE_MARKER = 0xFFFF2B16;
	public static final int SUBTABLE_MARKER = 0xFFFF1EAD;
	public static final int UNDEFINED_MARKER = 0xFFFFBAD1;
	
	public GlyphListModelList(Font<?> font) {
		super(new GlyphListModelRootNode(font));
		setRootVisible(false);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	
	public GlyphListModel getSelectedModel() {
		TreePath path = getSelectionPath();
		if (path == null) return null;
		Object o = path.getLastPathComponent();
		if (!(o instanceof GlyphListModelTreeNode)) return null;
		return ((GlyphListModelTreeNode)o).getModel();
	}
	
	public void setSelectedModel(GlyphListModel model, boolean shouldScroll) {
		Object root = getModel().getRoot();
		if (!(root instanceof GlyphListModelTreeNode)) return;
		GlyphListModelTreeNode r = (GlyphListModelTreeNode)root;
		TreePath path = findGlyphListModel(model, r, new TreePath(r));
		if (path != null) {
			setSelectionPath(path);
			if (shouldScroll) scrollPathToVisible(path);
		}
	}
	
	private TreePath findGlyphListModel(GlyphListModel model, GlyphListModelTreeNode node, TreePath parent) {
		if (hasModel(model, node)) return parent;
		for (GlyphListModelTreeNode child : node.getChildren()) {
			TreePath path = findGlyphListModel(model, child, parent.pathByAddingChild(child));
			if (path != null) return path;
		}
		return null;
	}
	
	private static boolean hasModel(GlyphListModel model, GlyphListModelTreeNode node) {
		GlyphListModel other = node.getModel();
		if (model == null) return (other == null);
		if (other == null) return (model == null);
		return model.equals(other);
	}
	
	private static class GlyphListModelRootNode extends GlyphListModelTreeNode {
		public GlyphListModelRootNode(Font<?> font) {
			super(null, null);
			
			GlyphListModelTreeNode allGlyphs = new GlyphListModelTreeNode(this, new GlyphListFontModel(font, true, true, "All Glyphs in Font"));
			allGlyphs.children.add(new GlyphListModelTreeNode(this, new GlyphListFontModel(font, true, false, "All Code Points in Font")));
			allGlyphs.children.add(new GlyphListModelTreeNode(this, new GlyphListFontModel(font, false, true, "All Named Glyphs in Font")));
			children.add(allGlyphs);
			
			Iterator<Block> bi = BlockList.instance().iterator();
			if (bi.hasNext()) {
				GlyphListModelTreeNode unicode = new GlyphListModelTreeNode(this, new GlyphListCodePointModel(bi.next()));
				while (bi.hasNext()) {
					unicode.children.add(new GlyphListModelTreeNode(this, new GlyphListCodePointModel(bi.next())));
				}
				children.add(unicode);
			}
			for (GlyphList gl : GlyphLists.instance()) {
				children.add(new GlyphListModelTreeNode(this, new GlyphListCodePointModel(gl, gl.getName(), null)));
			}
			for (Encoding enc : EncodingList.instance().encodings()) {
				children.add(new GlyphListModelEncodingNode(this, enc));
			}
		}
	}
	
	private static class GlyphListModelEncodingNode extends GlyphListModelTreeNode {
		public GlyphListModelEncodingNode(GlyphListModelTreeNode parent, Encoding encoding) {
			super(parent, fromEncodingTable(encoding, false, encoding.getName()));
			for (int i = 0; i < 256; i++) {
				EncodingTable s = encoding.getSubtable(i);
				if (s != null) {
					String n = "Subtable " + Integer.toHexString(0xFF00 | i).substring(2).toUpperCase();
					GlyphListCodePointModel m = fromEncodingTable(s, true, n);
					if (m != null) children.add(new GlyphListModelEncodingNode(this, s, m));
				}
			}
		}
		private GlyphListModelEncodingNode(GlyphListModelTreeNode parent, EncodingTable table, GlyphListCodePointModel model) {
			super(parent, model);
			for (int i = 0; i < 256; i++) {
				EncodingTable s = table.getSubtable(i);
				if (s != null) {
					String n = model.toString() + " " + Integer.toHexString(0xFF00 | i).substring(2).toUpperCase();
					GlyphListCodePointModel m = fromEncodingTable(s, true, n);
					if (m != null) children.add(new GlyphListModelEncodingNode(this, s, m));
				}
			}
		}
		private static GlyphListCodePointModel fromEncodingTable(EncodingTable table, boolean nullOnEmpty, String name) {
			Integer[] codePoints = new Integer[256];
			for (int i = 0; i < 256; i++) {
				String s = table.getSequence(i);
				if (s != null && s.length() > 0) {
					if (s.codePointCount(0, s.length()) == 1) {
						codePoints[i] = s.codePointAt(0);
					} else if (s.length() == 2) {
						int ch0 = s.charAt(0) & 0xFFFF;
						int ch1 = s.charAt(1) & 0xFFFF;
						codePoints[i] = (
							(ch0 < 0x20 || ch0 > 0xFFFD || ch1 < 0x20 || ch1 > 0xFFFD)
							? SEQUENCE_MARKER : ((ch0 << 16) | ch1)
						);
					} else {
						codePoints[i] = SEQUENCE_MARKER;
					}
					nullOnEmpty = false;
				} else if (table.getSubtable(i) != null) {
					codePoints[i] = SUBTABLE_MARKER;
				} else {
					codePoints[i] = UNDEFINED_MARKER;
				}
			}
			if (nullOnEmpty) return null;
			return new GlyphListCodePointModel(Arrays.asList(codePoints), name, null);
		}
	}
	
	private static class GlyphListModelTreeNode implements TreeNode {
		protected final GlyphListModelTreeNode parent;
		protected final GlyphListModel model;
		protected final List<GlyphListModelTreeNode> children;
		public GlyphListModelTreeNode(GlyphListModelTreeNode parent, GlyphListModel model) {
			this.parent = parent;
			this.model = model;
			this.children = new ArrayList<GlyphListModelTreeNode>();
		}
		public final GlyphListModelTreeNode getParent() { return parent; }
		public final GlyphListModel getModel() { return model; }
		public final List<GlyphListModelTreeNode> getChildren() { return children; }
		public final Enumeration<GlyphListModelTreeNode> children() {
			return new Enumeration<GlyphListModelTreeNode>() {
				private final Iterator<GlyphListModelTreeNode> iter = children.iterator();
				public boolean hasMoreElements() { return iter.hasNext(); }
				public GlyphListModelTreeNode nextElement() { return iter.next(); }
			};
		}
		public final boolean getAllowsChildren() { return !children.isEmpty(); }
		public final GlyphListModelTreeNode getChildAt(int index) { return children.get(index); }
		public final int getChildCount() { return children.size(); }
		public final int getIndex(TreeNode node) { return children.indexOf(node); }
		public final boolean isLeaf() { return children.isEmpty(); }
		public final String toString() { return (model != null) ? model.toString() : null; }
	}
}
