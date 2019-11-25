package com.kreative.bitsnpicas.edit;

import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractListModel;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.unicode.BlockList;
import com.kreative.bitsnpicas.unicode.EncodingList;
import com.kreative.bitsnpicas.unicode.GlyphLists;

public class CodePointListListModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	
	private final List<? extends List<? extends List<Integer>>> sources;
	
	@SuppressWarnings("unchecked")
	public CodePointListListModel(Font<?> font) {
		sources = Arrays.asList(
			Arrays.asList(new FontCodePointList(font)),
			BlockList.instance(),
			GlyphLists.instance(),
			EncodingList.instance()
		);
	}
	
	public Object getElementAt(int index) {
		for (List<? extends List<Integer>> source : sources) {
			if (index < source.size()) return source.get(index);
			else index -= source.size();
		}
		return null;
	}
	
	public int getSize() {
		int size = 0;
		for (List<? extends List<Integer>> source : sources) {
			size += source.size();
		}
		return size;
	}
}
