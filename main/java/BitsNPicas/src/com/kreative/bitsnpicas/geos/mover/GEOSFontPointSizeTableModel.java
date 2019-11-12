package com.kreative.bitsnpicas.geos.mover;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import com.kreative.bitsnpicas.geos.GEOSFontFile;
import com.kreative.bitsnpicas.geos.GEOSFontPointSize;

public class GEOSFontPointSizeTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private static final ImageIcon ICON = new ImageIcon(
		GEOSFontPointSizeTableModel.class.getResource("FontIcon.png")
	);
	
	private GEOSFontFile gff;
	private List<Item> items;
	
	private static final class Item {
		public int pointSize;
		public int sectorCount;
		public String typeString;
	}
	
	public GEOSFontPointSizeTableModel(GEOSFontFile gff) {
		this.gff = gff;
		refresh();
	}
	
	public GEOSFontFile getFontFile() {
		return gff;
	}
	
	public int getPointSize(int row) {
		return items.get(row).pointSize;
	}
	
	public void refresh() {
		items = new ArrayList<Item>();
		for (int pointSize : gff.getFontPointSizes()) {
			Item item = new Item();
			item.pointSize = pointSize;
			item.sectorCount = gff.getFontPointSizeSectorCount(pointSize);
			item.typeString = gff.getFontPointSizeTypeString(pointSize);
			items.add(item);
		}
		fireTableDataChanged();
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		switch (col) {
			case 0: return ImageIcon.class;
			case 1: return Integer.class;
			case 2: return Integer.class;
			case 3: return String.class;
			default: return null;
		}
	}
	
	@Override
	public int getColumnCount() {
		return 4;
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0: return "";
			case 1: return "Point Size";
			case 2: return "Sectors";
			case 3: return "Type";
			default: return null;
		}
	}
	
	@Override
	public int getRowCount() {
		return items.size();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		switch (col) {
			case 0: return ICON;
			case 1: return items.get(row).pointSize;
			case 2: return items.get(row).sectorCount;
			case 3: return items.get(row).typeString;
			default: return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 1) {
			int pointSize = items.get(row).pointSize;
			if (pointSize == 48 && gff.isMega()) return false;
			return true;
		}
		return false;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col == 1) {
			try {
				int oldPointSize = items.get(row).pointSize;
				if (oldPointSize == 48 && gff.isMega()) return;
				int newPointSize = Integer.parseInt(value.toString());
				if (
					newPointSize != oldPointSize &&
					newPointSize > 0 && newPointSize < 64
				) {
					GEOSFontPointSize gfps = gff.getFontPointSize(oldPointSize);
					if (gfps == null) return;
					gff.removeFontPointSize(newPointSize);
					gff.removeFontPointSize(oldPointSize);
					gff.setFontPointSize(newPointSize, gfps);
					refresh();
				}
			} catch (NumberFormatException nfe) {
				return;
			}
		}
	}
}
