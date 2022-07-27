package com.kreative.bitsnpicas.edit;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import com.kreative.bitsnpicas.Font;
import com.kreative.unicode.data.Block;
import com.kreative.unicode.data.BlockList;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;
import com.kreative.unicode.data.GlyphLists;

public class GlyphListModelList extends JList {
	private static final long serialVersionUID = 1L;
	
	// TODO: upgrade to a JTree, add subtables of multibyte encodings
	
	public static List<GlyphListModel> createModelList(Font<?> font) {
		List<GlyphListModel> models = new ArrayList<GlyphListModel>();
		models.add(new GlyphListFontModel(font, true, true, "All Glyphs in Font"));
		models.add(new GlyphListFontModel(font, true, false, "All Code Points in Font"));
		models.add(new GlyphListFontModel(font, false, true, "All Named Glyphs in Font"));
		for (Block block : BlockList.instance()) models.add(new GlyphListCodePointModel(block));
		for (GlyphList gl : GlyphLists.instance()) models.add(new GlyphListCodePointModel(gl, gl.getName(), null));
		for (GlyphList gl : EncodingList.instance().glyphLists()) models.add(new GlyphListCodePointModel(gl, gl.getName(), null));
		return models;
	}
	
	public GlyphListModelList(Font<?> font) {
		super(createModelList(font).toArray());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public GlyphListModel getSelectedModel() {
		return (GlyphListModel)getSelectedValue();
	}
	
	public void setSelectedModel(GlyphListModel model, boolean shouldScroll) {
		setSelectedValue(model, shouldScroll);
	}
}
