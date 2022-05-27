package com.kreative.keyedit.edit;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import com.kreative.keyedit.KkbReader;
import com.kreative.keyedit.KkbWriter;

public class CodePointClassTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final List<Entry> entries;
	
	public CodePointClassTableModel(Map<String,BitSet> map) {
		this.entries = new ArrayList<Entry>();
		for (Map.Entry<String,BitSet> e : map.entrySet()) {
			Entry entry = new Entry();
			entry.className = e.getKey();
			entry.codepoints = new BitSet();
			entry.codepoints.or(e.getValue());
			this.entries.add(entry);
		}
	}
	
	public void addEntry(String className, BitSet codepoints) {
		int i = entries.size();
		Entry entry = new Entry();
		entry.className = className;
		entry.codepoints = new BitSet();
		entry.codepoints.or(codepoints);
		entries.add(entry);
		fireTableRowsInserted(i, i);
	}
	
	public void deleteEntry(int i) {
		entries.remove(i);
		fireTableRowsDeleted(i, i);
	}
	
	public void toMap(Map<String,BitSet> map) {
		map.clear();
		for (Entry e : entries) {
			BitSet bs = map.get(e.className);
			if (bs == null) map.put(e.className, (bs = new BitSet()));
			bs.or(e.codepoints);
		}
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0: return "Class Name";
			case 1: return "Code Points";
			default: return null;
		}
	}
	
	@Override
	public int getRowCount() {
		return entries.size();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		Entry e = entries.get(row);
		switch (col) {
			case 0:
				return e.className;
			case 1:
				return KkbWriter.formatRanges(e.codepoints);
			default:
				return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return true;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		Entry e = entries.get(row);
		switch (col) {
			case 0:
				e.className = value.toString().trim();
				break;
			case 1:
				e.codepoints = KkbReader.parseRanges(value.toString());
				break;
			default:
				break;
		}
	}
	
	private static class Entry {
		private String className;
		private BitSet codepoints;
	}
}
