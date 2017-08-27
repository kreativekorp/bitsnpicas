package com.kreative.bitsnpicas.edit;

import javax.swing.AbstractListModel;
import com.kreative.bitsnpicas.unicode.BlockList;
import com.kreative.bitsnpicas.unicode.EncodingList;

public class CodePointListListModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	
	private final BlockList blocks;
	private final EncodingList encodings;
	
	public CodePointListListModel() {
		this.blocks = BlockList.instance();
		this.encodings = EncodingList.instance();
	}
	
	public Object getElementAt(int index) {
		int th = blocks.size();
		if (index < th) return blocks.get(index);
		else return encodings.get(index - th);
	}
	
	public int getSize() {
		return blocks.size() + encodings.size();
	}
}
