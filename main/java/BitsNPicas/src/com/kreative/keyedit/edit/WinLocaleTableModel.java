package com.kreative.keyedit.edit;

import javax.swing.table.AbstractTableModel;
import com.kreative.keyedit.WinLocale;

public class WinLocaleTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private static final WinLocale[] LOCALES = WinLocale.values();
	
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
			case 0: return "LCID";
			case 1: return "Tag";
			case 2: return "Locale";
			default: return null;
		}
	}
	
	@Override
	public int getRowCount() {
		return LOCALES.length;
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		WinLocale locale = LOCALES[row];
		switch (col) {
			case 0:
				String h = "0000" + Integer.toHexString(locale.lcid);
				return h.substring(h.length() - 4).toUpperCase();
			case 1: return locale.tag;
			case 2: return locale.name;
			default: return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}
