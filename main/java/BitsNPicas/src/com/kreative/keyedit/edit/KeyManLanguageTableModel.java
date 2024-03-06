package com.kreative.keyedit.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import com.kreative.keyedit.WinLocale;

public class KeyManLanguageTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final List<Entry> entries;
	
	public KeyManLanguageTableModel(Map<String,String> map) {
		this.entries = new ArrayList<Entry>();
		for (Map.Entry<String,String> e : map.entrySet()) {
			Entry entry = new Entry();
			entry.tag = e.getKey();
			entry.name = e.getValue();
			this.entries.add(entry);
		}
	}
	
	public void addEntry(String tag, String name) {
		int i = entries.size();
		Entry entry = new Entry();
		entry.tag = tag;
		entry.name = name;
		entries.add(entry);
		fireTableRowsInserted(i, i);
	}
	
	public void deleteEntry(int i) {
		entries.remove(i);
		fireTableRowsDeleted(i, i);
	}
	
	public void moveEntry(int i, int dir) {
		if (dir < 0 && i > 0) {
			Entry e = entries.remove(i);
			i -= 1;
			entries.add(i, e);
			fireTableRowsUpdated(i, i + 1);
		}
		if (dir > 0 && i < (entries.size() - 1)) {
			Entry e = entries.remove(i);
			i += 1;
			entries.add(i, e);
			fireTableRowsUpdated(i - 1, i);
		}
	}
	
	public void toMap(Map<String,String> map) {
		map.clear();
		for (Entry e : entries) {
			map.put(e.tag, e.name);
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
			case 0: return "ID";
			case 1: return "Name";
			default: return null;
		}
	}
	
	@Override
	public int getRowCount() {
		return entries.size();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		switch (col) {
			case 0: return entries.get(row).tag;
			case 1: return entries.get(row).name;
			default: return null;
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
				e.tag = value.toString().trim();
				WinLocale loc0 = WinLocale.forTag(e.tag, null);
				e.name = (loc0 != null) ? loc0.name : e.tag;
				break;
			case 1:
				e.name = value.toString().trim();
				WinLocale loc1 = WinLocale.forName(e.name, null);
				if (loc1 != null) e.tag = loc1.tag;
				break;
		}
		fireTableRowsUpdated(row, row);
	}
	
	private static class Entry {
		private String tag;
		private String name;
	}
}
