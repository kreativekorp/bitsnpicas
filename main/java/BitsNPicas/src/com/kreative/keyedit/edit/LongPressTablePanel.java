package com.kreative.keyedit.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

public class LongPressTablePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final Promise<int[]> autoLPO;
	private final JRadioButton autoButton;
	private final JRadioButton noneButton;
	private final JRadioButton customButton;
	private final CodePointTableModel cpModel;
	private final JTable cpTable;
	private final JScrollPane cpPane;
	private final JButton cpAdd;
	private final JButton cpDelete;
	private final JButton cpUp;
	private final JButton cpDown;
	private boolean locked = false;
	
	public LongPressTablePanel(Promise<int[]> autoLPO, int[] lpo) {
		this.autoLPO = autoLPO;
		this.autoButton = new JRadioButton("Auto");
		this.autoButton.setSelected(lpo == null);
		this.autoButton.addActionListener(new AutoButtonActionListener());
		this.noneButton = new JRadioButton("None");
		this.noneButton.setSelected(lpo != null && lpo.length == 0);
		this.noneButton.addActionListener(new NoneButtonActionListener());
		this.customButton = new JRadioButton("Custom");
		this.customButton.setSelected(lpo != null && lpo.length > 0);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(autoButton);
		bg.add(noneButton);
		bg.add(customButton);
		
		if (lpo == null && autoLPO != null) lpo = autoLPO.resolve();
		this.cpModel = new CodePointTableModel(lpo);
		this.cpModel.addTableModelListener(new EditCodePointActionListener());
		this.cpTable = new JTable(this.cpModel);
		this.cpPane = scrollWrap(this.cpTable);
		this.cpAdd = square(new JButton("+"));
		this.cpAdd.addActionListener(new AddCodePointActionListener());
		this.cpDelete = square(new JButton("\u2212"));
		this.cpDelete.addActionListener(new DeleteCodePointActionListener());
		this.cpUp = square(new JButton("\u2191"));
		this.cpUp.addActionListener(new MoveUpCodePointActionListener());
		this.cpDown = square(new JButton("\u2193"));
		this.cpDown.addActionListener(new MoveDownCodePointActionListener());
		setColumnWidth(cpTable, 0, 80);
		setColumnWidth(cpTable, 1, 80);
		
		JPanel radios = horizontalStack(autoButton, noneButton, customButton);
		JPanel button = leftAlign(horizontalStack(cpAdd, cpDelete, cpUp, cpDown));
		JPanel panel = verticalSxS(radios, cpPane, button, 8);
		
		setLayout(new GridLayout(1,1,0,0));
		add(panel);
	}
	
	public int[] getLongPressOutput() {
		if (customButton.isSelected()) return cpModel.toIntArray();
		if (noneButton.isSelected()) return new int[0];
		return null;
	}
	
	private static JPanel horizontalStack(Component... comps) {
		JPanel p = new JPanel(new GridLayout(1,0,4,4));
		for (Component c : comps) p.add(c);
		return p;
	}
	
	private static JPanel leftAlign(Component c) {
		JPanel p = new JPanel(new BorderLayout());
		p.add(c, BorderLayout.LINE_START);
		return p;
	}
	
	private static JPanel verticalSxS(Component t, Component c, Component b, int gap) {
		JPanel p = new JPanel(new BorderLayout(gap, gap));
		p.add(t, BorderLayout.PAGE_START);
		p.add(c, BorderLayout.CENTER);
		p.add(b, BorderLayout.PAGE_END);
		return p;
	}
	
	private static <C extends JComponent> C square(C c) {
		Dimension d = c.getPreferredSize();
		d.width = (d.height += 8);
		c.setMinimumSize(d);
		c.setPreferredSize(d);
		c.setMaximumSize(d);
		c.putClientProperty("JButton.buttonType", "bevel");
		return c;
	}
	
	private static JScrollPane scrollWrap(Component c) {
		return new JScrollPane(
			c,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
	}
	
	private static void setColumnWidth(JTable t, int col, int w) {
		TableColumn column = t.getColumnModel().getColumn(col);
		column.setMinWidth(w);
		column.setPreferredWidth(w);
		column.setMaxWidth(w);
	}
	
	private class AutoButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (locked) return;
			locked = true;
			cpModel.clearEntries();
			if (autoLPO != null) {
				int[] lpo = autoLPO.resolve();
				if (lpo != null) cpModel.addEntries(lpo);
			}
			locked = false;
		}
	}
	
	private class NoneButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (locked) return;
			locked = true;
			cpModel.clearEntries();
			locked = false;
		}
	}
	
	private class EditCodePointActionListener implements TableModelListener {
		public void tableChanged(TableModelEvent e) {
			if (locked) return;
			locked = true;
			autoButton.setSelected(false);
			noneButton.setSelected(cpModel.isEmpty());
			customButton.setSelected(!cpModel.isEmpty());
			locked = false;
		}
	}
	
	private class AddCodePointActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final int i = cpModel.getRowCount();
			cpModel.addEntry(32);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					cpTable.getSelectionModel().setSelectionInterval(i, i);
					JScrollBar vsb = cpPane.getVerticalScrollBar();
					vsb.setValue(vsb.getMaximum());
				}
			});
		}
	}
	
	private class DeleteCodePointActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int[] rows = cpTable.getSelectedRows();
			for (int i = rows.length - 1; i >= 0; i--) {
				cpModel.deleteEntry(rows[i]);
			}
		}
	}
	
	private class MoveUpCodePointActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final int i = cpTable.getSelectedRow();
			if (i < 1) return;
			cpModel.moveEntry(i, -1);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					cpTable.getSelectionModel().setSelectionInterval(i-1, i-1);
				}
			});
		}
	}
	
	private class MoveDownCodePointActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final int i = cpTable.getSelectedRow();
			if (i < 0 || i >= (cpModel.getRowCount()-1)) return;
			cpModel.moveEntry(i, +1);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					cpTable.getSelectionModel().setSelectionInterval(i+1, i+1);
				}
			});
		}
	}
}
