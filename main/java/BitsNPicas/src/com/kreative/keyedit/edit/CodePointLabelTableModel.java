package com.kreative.keyedit.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import com.kreative.unicode.data.NameDatabase;
import com.kreative.unicode.data.NameResolver;

public class CodePointLabelTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final NameDatabase ndb;
	private final String labelType;
	private final List<Entry> entries;
	
	public CodePointLabelTableModel(Map<Integer,String> map, String labelType) {
		this.ndb = NameDatabase.instance();
		this.labelType = labelType;
		this.entries = new ArrayList<Entry>();
		for (Map.Entry<Integer,String> e : map.entrySet()) {
			Entry entry = new Entry();
			entry.codepoint = e.getKey();
			entry.label = e.getValue();
			this.entries.add(entry);
		}
	}
	
	public void addEntry(int codepoint, String label) {
		int i = entries.size();
		Entry entry = new Entry();
		entry.codepoint = codepoint;
		entry.label = label;
		entries.add(entry);
		fireTableRowsInserted(i, i);
	}
	
	public void deleteEntry(int i) {
		entries.remove(i);
		fireTableRowsDeleted(i, i);
	}
	
	public void toMap(Map<Integer,String> map) {
		map.clear();
		for (Entry e : entries) {
			map.put(e.codepoint, e.label);
		}
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}
	
	@Override
	public int getColumnCount() {
		return 4;
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0: return "Code Point";
			case 1: return "Character";
			case 2: return "Character Name";
			case 3: return labelType;
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
				if (e.codepoint < 0) return null;
				String h = Integer.toHexString(e.codepoint);
				while (h.length() < 4) h = "0" + h;
				return h.toUpperCase();
			case 1:
				if (e.codepoint < 0) return null;
				return String.valueOf(Character.toChars(e.codepoint));
			case 2:
				if (e.codepoint < 0) return null;
				return NameResolver.instance(e.codepoint).getName(e.codepoint);
			case 3:
				return e.label;
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
				try {
					int cp = Integer.parseInt(value.toString().trim(), 16);
					if (cp > 0) e.codepoint = cp;
				} catch (NumberFormatException nfe) {
					// Ignored
				}
				break;
			case 1:
				String s = value.toString();
				if (s.length() > 0) e.codepoint = s.codePointAt(0);
				break;
			case 2:
				NameDatabase.NameEntry ne = ndb.find(value.toString().trim());
				if (ne != null) e.codepoint = ne.codePoint;
				break;
			case 3:
				String a = value.toString().trim();
				if (a.length() > 0) e.label = a;
				break;
			default:
				break;
		}
		fireTableRowsUpdated(row, row);
	}
	
	private static class Entry {
		private int codepoint;
		private String label;
	}
}
