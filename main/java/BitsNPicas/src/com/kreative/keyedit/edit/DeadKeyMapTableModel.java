package com.kreative.keyedit.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import com.kreative.bitsnpicas.unicode.CharacterData;
import com.kreative.bitsnpicas.unicode.CharacterDatabase;

public class DeadKeyMapTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final List<Entry> entries;
	
	public DeadKeyMapTableModel(Map<Integer,Integer> map) {
		this.entries = new ArrayList<Entry>();
		for (Map.Entry<Integer,Integer> e : map.entrySet()) {
			Entry entry = new Entry();
			entry.input = e.getKey();
			entry.output = e.getValue();
			this.entries.add(entry);
		}
	}
	
	public void addEntry(int input, int output) {
		int i = entries.size();
		Entry entry = new Entry();
		entry.input = input;
		entry.output = output;
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
	
	public void toMap(Map<Integer,Integer> map) {
		map.clear();
		for (Entry e : entries) {
			map.put(e.input, e.output);
		}
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}
	
	@Override
	public int getColumnCount() {
		return 6;
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0: return "Input Code";
			case 1: return "Input Char";
			case 2: return "Input Character Name";
			case 3: return "Output Code";
			case 4: return "Output Char";
			case 5: return "Output Character Name";
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
				String ih = Integer.toHexString(e.input);
				while (ih.length() < 4) ih = "0" + ih;
				return ih.toUpperCase();
			case 1:
				if (e.input < 0) return null;
				return String.valueOf(Character.toChars(e.input));
			case 2:
				if (e.input < 0) return null;
				CharacterData icd = CharacterDatabase.instance().get(e.input);
				return (icd == null) ? null : icd.toString();
			case 3:
				if (e.output < 0) return null;
				String oh = Integer.toHexString(e.output);
				while (oh.length() < 4) oh = "0" + oh;
				return oh.toUpperCase();
			case 4:
				if (e.output < 0) return null;
				return String.valueOf(Character.toChars(e.output));
			case 5:
				if (e.output < 0) return null;
				CharacterData ocd = CharacterDatabase.instance().get(e.output);
				return (ocd == null) ? null : ocd.toString();
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
				String is = value.toString();
				if (is.length() > 0) e.input = is.codePointAt(0);
				break;
			case 2:
				CharacterData icd = CharacterDatabase.instance().find(value.toString().trim());
				if (icd != null) e.input = icd.codePoint;
				break;
			case 3:
				try {
					int cp = Integer.parseInt(value.toString().trim(), 16);
					if (cp > 0) e.output = cp;
				} catch (NumberFormatException nfe) {
					// Ignored
				}
				break;
			case 4:
				String os = value.toString();
				if (os.length() > 0) e.output = os.codePointAt(0);
				break;
			case 5:
				CharacterData ocd = CharacterDatabase.instance().find(value.toString().trim());
				if (ocd != null) e.output = ocd.codePoint;
				break;
			default:
				break;
		}
	}
	
	private static class Entry {
		private int input;
		private int output;
	}
}
