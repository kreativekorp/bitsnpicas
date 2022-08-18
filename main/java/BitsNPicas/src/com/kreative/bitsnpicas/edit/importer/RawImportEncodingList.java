package com.kreative.bitsnpicas.edit.importer;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import com.kreative.unicode.data.EncodingList;

public class RawImportEncodingList extends JComboBox {
	private static final long serialVersionUID = 1L;
	
	public RawImportEncodingList() {
		super(getEncodingList());
		setEditable(false);
		setSelectedIndex(7);
	}
	
	public List<Integer> getSelectedEncoding() {
		if (getSelectedIndex() <= 0) return null;
		@SuppressWarnings("unchecked")
		List<Integer> in = (List<Integer>)getSelectedItem();
		return in;
	}
	
	public void applySelectedEncoding(List<Integer> out) {
		if (getSelectedIndex() <= 0) return;
		@SuppressWarnings("unchecked")
		List<Integer> in = (List<Integer>)getSelectedItem();
		for (int i = 0, n = out.size(), m = in.size(); i < n; i++) {
			out.set(i, (i < m) ? in.get(i) : -1);
		}
	}
	
	private static final Object[] getEncodingList() {
		List<Object> encodings = new ArrayList<Object>();
		encodings.add(new String("Custom"));
		encodings.add(new StartFrom(0x0000));
		encodings.add(new StartFrom(0x0020));
		encodings.add(new StartFrom(0xE000));
		encodings.add(new StartFrom(0xE020));
		encodings.add(new StartFrom(0xF000));
		encodings.add(new StartFrom(0xF020));
		encodings.add(new StartFrom(0xF0000));
		encodings.add(new StartFrom(0x100000));
		encodings.addAll(EncodingList.instance().glyphLists());
		return encodings.toArray();
	}
	
	private static final class StartFrom extends AbstractList<Integer> {
		private final int start;
		private final String name;
		public StartFrom(int start) {
			this.start = start;
			String h = Integer.toHexString(start).toUpperCase();
			while (h.length() < 4) h = "0" + h;
			this.name = "Start From U+" + h;
		}
		public boolean contains(Object o) {
			return (
				(o instanceof Integer) &&
				(((Integer)o) >= start) &&
				(((Integer)o) < 0x110000)
			);
		}
		public Integer get(int index) {
			return start + index;
		}
		public int indexOf(Object o) {
			return contains(o) ? (((Integer)o) - start) : -1;
		}
		public boolean isEmpty() {
			return false;
		}
		public int lastIndexOf(Object o) {
			return contains(o) ? (((Integer)o) - start) : -1;
		}
		public int size() {
			return 0x110000 - start;
		}
		public String toString() {
			return name;
		}
	}
}
