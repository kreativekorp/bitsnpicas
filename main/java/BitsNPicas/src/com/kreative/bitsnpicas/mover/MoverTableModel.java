package com.kreative.bitsnpicas.mover;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

public class MoverTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private MoverFile mf;
	
	public MoverTableModel(MoverFile mf) {
		this.mf = mf;
	}
	
	public MoverFile getMoverFile() {
		return mf;
	}
	
	public void refresh() {
		fireTableDataChanged();
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		switch (col) {
			case 0: return ImageIcon.class;
			case 1: return String.class;
			case 2: return String.class;
			case 3: return String.class;
			case 4: return Integer.class;
			default: return null;
		}
	}
	
	@Override
	public int getColumnCount() {
		return 5;
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0: return "";
			case 1: return "Name";
			case 2: return "Size";
			case 3: return "Kind";
			case 4: return "ID";
			default: return null;
		}
	}
	
	@Override
	public int getRowCount() {
		return mf.size();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		switch (col) {
			case 0: return mf.get(row).icon;
			case 1: return mf.get(row).name;
			case 2: return ((mf.get(row).length() + 1023) / 1024) + "K";
			case 3: return mf.get(row).icon.getDescription();
			case 4: return mf.get(row).id;
			default: return null;
		}
	}
}
