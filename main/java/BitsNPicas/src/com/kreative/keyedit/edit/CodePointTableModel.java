package com.kreative.keyedit.edit;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import com.kreative.unicode.data.NameDatabase;
import com.kreative.unicode.data.NameResolver;

public class CodePointTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final NameDatabase ndb;
	private final List<Integer> entries;
	
	public CodePointTableModel() {
		this.ndb = NameDatabase.instance();
		this.entries = new ArrayList<Integer>();
	}
	
	public CodePointTableModel(List<Integer> entries) {
		this(); if (entries != null) this.entries.addAll(entries);
	}
	
	public CodePointTableModel(Integer[] entries) {
		this(); if (entries != null) for (int e : entries) this.entries.add(e);
	}
	
	public CodePointTableModel(int[] entries) {
		this(); if (entries != null) for (int e : entries) this.entries.add(e);
	}
	
	public void addEntry(int codepoint) {
		int i = entries.size();
		entries.add(codepoint);
		fireTableRowsInserted(i, i);
	}
	
	public void deleteEntry(int i) {
		entries.remove(i);
		fireTableRowsDeleted(i, i);
	}
	
	public void moveEntry(int i, int dir) {
		if (dir < 0 && i > 0) {
			Integer e = entries.remove(i);
			i -= 1;
			entries.add(i, e);
			fireTableRowsUpdated(i, i + 1);
		}
		if (dir > 0 && i < (entries.size() - 1)) {
			Integer e = entries.remove(i);
			i += 1;
			entries.add(i, e);
			fireTableRowsUpdated(i - 1, i);
		}
	}
	
	public void addEntries(List<Integer> entries) {
		if (entries != null && entries.size() > 0) {
			int i = this.entries.size();
			int j = i + entries.size() - 1;
			this.entries.addAll(entries);
			fireTableRowsInserted(i, j);
		}
	}
	
	public void addEntries(Integer[] entries) {
		if (entries != null && entries.length > 0) {
			int i = this.entries.size();
			int j = i + entries.length - 1;
			for (int e : entries) this.entries.add(e);
			fireTableRowsInserted(i, j);
		}
	}
	
	public void addEntries(int[] entries) {
		if (entries != null && entries.length > 0) {
			int i = this.entries.size();
			int j = i + entries.length - 1;
			for (int e : entries) this.entries.add(e);
			fireTableRowsInserted(i, j);
		}
	}
	
	public void clearEntries() {
		int i = entries.size();
		if (i > 0) {
			entries.clear();
			fireTableRowsDeleted(0, i - 1);
		}
	}
	
	public boolean isEmpty() {
		return entries.isEmpty();
	}
	
	public int size() {
		return entries.size();
	}
	
	public void toList(List<Integer> entries) {
		entries.clear();
		entries.addAll(this.entries);
	}
	
	public Integer[] toIntegerArray() {
		return entries.toArray(new Integer[entries.size()]);
	}
	
	public int[] toIntArray() {
		Integer[] A = toIntegerArray();
		int[] a = new int[A.length];
		for (int i = 0; i < A.length; i++) a[i] = A[i];
		return a;
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0: return "Code Point";
			case 1: return "Character";
			case 2: return "Character Name";
			default: return null;
		}
	}
	
	@Override
	public int getRowCount() {
		return entries.size();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		int cp = entries.get(row);
		switch (col) {
			case 0:
				if (cp < 0) return null;
				String h = Integer.toHexString(cp);
				while (h.length() < 4) h = "0" + h;
				return h.toUpperCase();
			case 1:
				if (cp < 0) return null;
				return String.valueOf(Character.toChars(cp));
			case 2:
				if (cp < 0) return null;
				return NameResolver.instance(cp).getName(cp);
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
		switch (col) {
			case 0:
				try {
					int cp = Integer.parseInt(value.toString().trim(), 16);
					if (cp > 0) entries.set(row, cp);
				} catch (NumberFormatException nfe) {
					// Ignored
				}
				break;
			case 1:
				String s = value.toString();
				if (s.length() > 0) entries.set(row, s.codePointAt(0));
				break;
			case 2:
				NameDatabase.NameEntry ne = ndb.find(value.toString().trim());
				if (ne != null) entries.set(row, ne.codePoint);
				break;
			default:
				break;
		}
		fireTableRowsUpdated(row, row);
	}
}
