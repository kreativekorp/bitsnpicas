package com.kreative.fontmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.table.AbstractTableModel;

public class FontMapTableModel extends AbstractTableModel implements List<FontMapEntry> {
	private static final long serialVersionUID = 1L;
	private final List<FontMapEntry> entries;
	
	public FontMapTableModel() {
		entries = new ArrayList<FontMapEntry>();
	}
	
	public FontMapTableModel(Collection<? extends FontMapEntry> c) {
		entries = new ArrayList<FontMapEntry>();
		entries.addAll(c);
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
			case 0: return "Code Point Range";
			case 1: return "Font Name";
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
			case 0: return entries.get(row).getCodePointsString();
			case 1: return entries.get(row).getFontString();
			default: return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return true;
	}
	
	@Override
	public void setValueAt(Object val, int row, int col) {
		switch (col) {
			case 0: entries.get(row).setCodePointsString(val.toString()); break;
			case 1: entries.get(row).setFontString(val.toString()); break;
		}
	}
	
	@Override
	public boolean add(FontMapEntry e) {
		int len = entries.size();
		if (entries.add(e)) {
			fireTableRowsInserted(len, len);
			return true;
		}
		return false;
	}
	
	@Override
	public void add(int i, FontMapEntry e) {
		entries.add(i, e);
		fireTableRowsInserted(i, i);
	}
	
	@Override
	public boolean addAll(Collection<? extends FontMapEntry> c) {
		int len = entries.size();
		if (entries.addAll(c)) {
			fireTableRowsInserted(len, len + c.size() - 1);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addAll(int i, Collection<? extends FontMapEntry> c) {
		if (entries.addAll(i, c)) {
			fireTableRowsInserted(i, i + c.size() - 1);
			return true;
		}
		return false;
	}
	
	@Override
	public void clear() {
		if (entries.isEmpty()) return;
		int len = entries.size();
		entries.clear();
		fireTableRowsDeleted(0, len - 1);
	}
	
	@Override
	public boolean contains(Object e) {
		return entries.contains(e);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return entries.containsAll(c);
	}
	
	@Override
	public FontMapEntry get(int i) {
		return entries.get(i);
	}
	
	@Override
	public int indexOf(Object e) {
		return entries.indexOf(e);
	}
	
	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}
	
	@Override
	public Iterator<FontMapEntry> iterator() {
		return Collections.unmodifiableList(entries).iterator();
	}
	
	@Override
	public int lastIndexOf(Object e) {
		return entries.lastIndexOf(e);
	}
	
	@Override
	public ListIterator<FontMapEntry> listIterator() {
		return Collections.unmodifiableList(entries).listIterator();
	}
	
	@Override
	public ListIterator<FontMapEntry> listIterator(int i) {
		return Collections.unmodifiableList(entries).listIterator(i);
	}
	
	@Override
	public boolean remove(Object e) {
		int i = entries.indexOf(e);
		if (entries.remove(e)) {
			fireTableRowsDeleted(i, i);
			return true;
		}
		return false;
	}
	
	@Override
	public FontMapEntry remove(int i) {
		FontMapEntry e = entries.remove(i);
		fireTableRowsDeleted(i, i);
		return e;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		if (entries.removeAll(c)) {
			fireTableDataChanged();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		if (entries.retainAll(c)) {
			fireTableDataChanged();
			return true;
		}
		return false;
	}
	
	@Override
	public FontMapEntry set(int i, FontMapEntry e) {
		e = entries.set(i, e);
		fireTableRowsUpdated(i, i);
		return e;
	}
	
	@Override
	public int size() {
		return entries.size();
	}
	
	@Override
	public List<FontMapEntry> subList(int i, int j) {
		return Collections.unmodifiableList(entries).subList(i, j);
	}
	
	@Override
	public Object[] toArray() {
		return entries.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return entries.toArray(a);
	}
}
