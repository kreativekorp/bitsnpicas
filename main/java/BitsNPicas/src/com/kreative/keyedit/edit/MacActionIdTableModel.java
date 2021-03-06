package com.kreative.keyedit.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import com.kreative.bitsnpicas.unicode.CharacterData;
import com.kreative.bitsnpicas.unicode.CharacterDatabase;

public class MacActionIdTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final List<Entry> entries;
	
	public MacActionIdTableModel(Map<Integer,String> map) {
		this.entries = new ArrayList<Entry>();
		for (Map.Entry<Integer,String> e : map.entrySet()) {
			Entry entry = new Entry();
			entry.input = e.getKey();
			entry.action = e.getValue();
			this.entries.add(entry);
		}
	}
	
	public void addEntry(int input, String action) {
		int i = entries.size();
		Entry entry = new Entry();
		entry.input = input;
		entry.action = action;
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
			map.put(e.input, e.action);
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
			case 3: return "Action ID";
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
				if (e.input < 0) return null;
				String h = Integer.toHexString(e.input);
				while (h.length() < 4) h = "0" + h;
				return h.toUpperCase();
			case 1:
				if (e.input < 0) return null;
				return String.valueOf(Character.toChars(e.input));
			case 2:
				if (e.input < 0) return null;
				CharacterData cd = CharacterDatabase.instance().get(e.input);
				return (cd == null) ? null : cd.toString();
			case 3:
				return e.action;
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
					if (cp > 0) e.input = cp;
				} catch (NumberFormatException nfe) {
					// Ignored
				}
				break;
			case 1:
				String s = value.toString();
				if (s.length() > 0) e.input = s.codePointAt(0);
				break;
			case 2:
				CharacterData cd = CharacterDatabase.instance().find(value.toString().trim());
				if (cd != null) e.input = cd.codePoint;
				break;
			case 3:
				String a = value.toString().trim();
				if (a.length() > 0) e.action = a;
				break;
			default:
				break;
		}
	}
	
	private static class Entry {
		private int input;
		private String action;
	}
}
