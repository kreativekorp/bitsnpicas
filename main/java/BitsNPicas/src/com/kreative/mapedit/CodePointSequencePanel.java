package com.kreative.mapedit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import com.kreative.unicode.data.NameDatabase;
import com.kreative.unicode.data.NameResolver;

public class CodePointSequencePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final String encodingPrefix;
	private final JLabel encodingLabel;
	private final List<Integer> sequenceData;
	private final MyTableModel sequenceModel;
	private final JTable sequenceTable;
	private final JButton addButton;
	private final JButton removeButton;
	private final JButton upButton;
	private final JButton downButton;
	private final NameDatabase ndb;
	private final List<CodePointSequencePanelListener> listeners;
	
	public CodePointSequencePanel(String encodingPrefix) {
		this.encodingPrefix = encodingPrefix;
		this.encodingLabel = new JLabel(encodingPrefix + "??");
		this.encodingLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.sequenceData = new ArrayList<Integer>();
		this.sequenceModel = new MyTableModel();
		this.sequenceTable = new JTable(sequenceModel);
		setColumnWidth(sequenceTable.getColumnModel().getColumn(0), 80);
		setColumnWidth(sequenceTable.getColumnModel().getColumn(1), 80);
		setColumnWidth(sequenceTable.getColumnModel().getColumn(2), 300);
		squareOff(this.addButton = new JButton("+"));
		squareOff(this.removeButton = new JButton("\u2212"));
		squareOff(this.upButton = new JButton("\u2191"));
		squareOff(this.downButton = new JButton("\u2193"));
		this.ndb = NameDatabase.instance();
		this.listeners = new ArrayList<CodePointSequencePanelListener>();
		
		JPanel topPanel = new JPanel(new BorderLayout(8, 8));
		topPanel.add(new JLabel("Encoding:"), BorderLayout.LINE_START);
		topPanel.add(encodingLabel, BorderLayout.CENTER);
		
		JPanel botPanel = new JPanel(new FlowLayout());
		botPanel.add(addButton);
		botPanel.add(removeButton);
		botPanel.add(upButton);
		botPanel.add(downButton);
		
		JScrollPane sequencePane = new JScrollPane(sequenceTable,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
		);
		
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.add(topPanel, BorderLayout.PAGE_START);
		mainPanel.add(botPanel, BorderLayout.PAGE_END);
		mainPanel.add(sequencePane, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(mainPanel);
		
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = sequenceTable.getSelectedRow();
				index = (index < 0) ? sequenceData.size() : (index + 1);
				sequenceData.add(index, 0);
				sequenceModel.fireTableDataChanged();
				sequenceTable.getSelectionModel().setSelectionInterval(index, index);
				fireListeners();
			}
		});
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = sequenceTable.getSelectedRow();
				if (index < 0) return;
				sequenceData.remove(index);
				sequenceModel.fireTableDataChanged();
				sequenceTable.clearSelection();
				fireListeners();
			}
		});
		upButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = sequenceTable.getSelectedRow();
				if (index <= 0) return;
				int removed = sequenceData.remove(index);
				index--;
				sequenceData.add(index, removed);
				sequenceModel.fireTableDataChanged();
				sequenceTable.getSelectionModel().setSelectionInterval(index, index);
				fireListeners();
			}
		});
		downButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = sequenceTable.getSelectedRow();
				if (index < 0 || index >= (sequenceData.size() - 1)) return;
				int removed = sequenceData.remove(index);
				index++;
				sequenceData.add(index, removed);
				sequenceModel.fireTableDataChanged();
				sequenceTable.getSelectionModel().setSelectionInterval(index, index);
				fireListeners();
			}
		});
	}
	
	public String getEncodingPrefix() {
		return encodingPrefix;
	}
	
	public CodePointSequence getCodePointSequence() {
		if (sequenceData.isEmpty()) return null;
		return new CodePointSequence(sequenceData);
	}
	
	public void setCodePointSequence(CodePointSequence seq, int cp, boolean notify) {
		if (cp >= 0) {
			String h = "00" + Integer.toHexString(cp);
			h = h.substring(h.length() - 2).toUpperCase();
			encodingLabel.setText(encodingPrefix + h);
		} else {
			encodingLabel.setText(encodingPrefix + "??");
		}
		sequenceData.clear();
		if (seq != null) sequenceData.addAll(seq.toList());
		sequenceModel.fireTableDataChanged();
		sequenceTable.clearSelection();
		if (notify) fireListeners();
	}
	
	public void addListener(CodePointSequencePanelListener listener) {
		if (listener != null) listeners.add(listener);
	}
	
	public void removeListener(CodePointSequencePanelListener listener) {
		if (listener != null) listeners.remove(listener);
	}
	
	public CodePointSequencePanelListener[] getListeners() {
		return listeners.toArray(new CodePointSequencePanelListener[listeners.size()]);
	}
	
	private void fireListeners() {
		for (CodePointSequencePanelListener l : listeners) l.codePointSequenceChanged();
	}
	
	private class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		public Class<?> getColumnClass(int col) {
			return String.class;
		}
		public int getColumnCount() {
			return 3;
		}
		public String getColumnName(int col) {
			switch (col) {
				case 0: return "Code Point";
				case 1: return "Character";
				case 2: return "Character Name";
				default: return null;
			}
		}
		public int getRowCount() {
			return sequenceData.size();
		}
		public Object getValueAt(int row, int col) {
			if (row < 0 || row >= sequenceData.size()) return null;
			int e = sequenceData.get(row);
			switch (col) {
				case 0:
					if (Character.isValidCodePoint(e)) {
						String h = Integer.toHexString(e).toUpperCase();
						while (h.length() < 4) h = "0" + h;
						return h;
					} else {
						MappingTag tag = MappingTag.forIntValue(e);
						if (tag != null) return tag.stringValue;
						else return null;
					}
				case 1:
					if ((e >= 32 && e < 127) || (e >= 160 && e < 0xD800) || (e >= 0xE000 && e < 0x110000)) {
						return String.valueOf(Character.toChars(e));
					} else {
						return null;
					}
				case 2:
					if (Character.isValidCodePoint(e)) {
						return NameResolver.instance(e).getName(e);
					} else {
						MappingTag tag = MappingTag.forIntValue(e);
						if (tag != null) return tag.description;
						else return null;
					}
				default:
					return null;
			}
		}
		public boolean isCellEditable(int row, int col) {
			return true;
		}
		public void setValueAt(Object value, int row, int col) {
			if (row < 0 || row >= sequenceData.size()) return;
			String val = value.toString();
			switch (col) {
				case 0:
					try {
						sequenceData.set(row, Integer.parseInt(val, 16));
					} catch (NumberFormatException e) {
						MappingTag tag = MappingTag.forStringValue(val);
						if (tag != null) sequenceData.set(row, tag.intValue);
					}
					break;
				case 1:
					if (val.length() > 0) sequenceData.set(row, val.codePointAt(0));
					break;
				case 2:
					MappingTag tag = MappingTag.forDescription(val);
					if (tag != null) { sequenceData.set(row, tag.intValue); break; }
					NameDatabase.NameEntry ne = ndb.find(val);
					if (ne != null) sequenceData.set(row, ne.codePoint);
					break;
			}
			fireTableRowsUpdated(row, row);
			fireListeners();
		}
	}
	
	private static void setColumnWidth(TableColumn col, int width) {
		col.setMinWidth(width);
		col.setWidth(width);
		col.setMaxWidth(width);
	}
	
	private static void squareOff(JComponent c) {
		int h = c.getPreferredSize().height + 8;
		c.setMinimumSize(new Dimension(h, h));
		c.setPreferredSize(new Dimension(h, h));
		c.setMaximumSize(new Dimension(h, h));
		c.putClientProperty("JButton.buttonType", "bevel");
	}
}
