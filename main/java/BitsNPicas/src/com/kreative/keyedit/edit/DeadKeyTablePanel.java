package com.kreative.keyedit.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import com.kreative.keyedit.DeadKeyTable;
import com.kreative.keyedit.XkbDeadKey;

public class DeadKeyTablePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final DeadKeyTable dead;
	private final OutputPanel winTerminator;
	private final OutputPanel macTerminator;
	private final JTextField macStateId;
	private final OutputPanel xkbOutput;
	private final JComboBox xkbDeadKey;
	private final DeadKeyMapTableModel keyMapModel;
	private final JTable keyMapTable;
	private final JScrollPane keyMapPane;
	private final JButton keyMapAdd;
	private final JButton keyMapDelete;
	private final JButton keyMapUp;
	private final JButton keyMapDown;
	
	public DeadKeyTablePanel(DeadKeyTable dead) {
		this.dead = dead;
		this.winTerminator = new OutputPanel(dead.winTerminator);
		this.macTerminator = new OutputPanel(dead.macTerminator);
		this.macStateId = new JTextField(dead.macStateId);
		this.xkbOutput = new OutputPanel(dead.xkbOutput);
		
		this.xkbDeadKey = new JComboBox(XkbDeadKey.values());
		this.xkbDeadKey.setEditable(false);
		this.xkbDeadKey.setSelectedItem(dead.xkbDeadKey);
		
		this.keyMapModel = new DeadKeyMapTableModel(dead.keyMap);
		this.keyMapTable = new JTable(this.keyMapModel);
		this.keyMapPane = scrollWrap(this.keyMapTable);
		this.keyMapAdd = square(new JButton("+"));
		this.keyMapAdd.addActionListener(new AddKeyMapActionListener());
		this.keyMapDelete = square(new JButton("\u2212"));
		this.keyMapDelete.addActionListener(new DeleteKeyMapActionListener());
		this.keyMapUp = square(new JButton("\u2191"));
		this.keyMapUp.addActionListener(new MoveUpKeyMapActionListener());
		this.keyMapDown = square(new JButton("\u2193"));
		this.keyMapDown.addActionListener(new MoveDownKeyMapActionListener());
		setColumnWidth(keyMapTable, 0, 80);
		setColumnWidth(keyMapTable, 1, 80);
		setColumnWidth(keyMapTable, 3, 80);
		setColumnWidth(keyMapTable, 4, 80);
		
		JPanel labels = verticalStack(
			l("Terminator (Win):", "The default output on Windows. Windows only allows some characters to be used as terminators."),
			l("Terminator (Mac):", "The default output on Mac OS X. Mac OS X allows any character to be used as a terminator."),
			l("State ID (Mac):", "The identifier to be used for this dead key state."),
			l("Xkb Keysym:", "The dead key to engage on Linux. If “none” is selected, use “Xkb Output” instead. Linux ignores all other settings, including the key map."),
			l("Xkb Output:", "The output on Linux, if “none” is selected above.")
		);
		JPanel fields = verticalStack(winTerminator, macTerminator, macStateId, leftAlign(xkbDeadKey), xkbOutput);
		JPanel keymap = topSxS(new JLabel("Key Map:"), keyMapPane, 4);
		JPanel button = leftAlign(horizontalStack(keyMapAdd, keyMapDelete, keyMapUp, keyMapDown));
		JPanel panel = verticalSxS(leftSxS(labels, fields, 8), keymap, button, 8);
		
		setLayout(new GridLayout(1,1,0,0));
		add(panel);
	}
	
	private static JLabel l(String label, String tooltip) {
		JLabel c = new JLabel(label);
		c.setToolTipText(tooltip);
		return c;
	}
	
	private static JPanel verticalStack(Component... comps) {
		JPanel p = new JPanel(new GridLayout(0,1,4,4));
		for (Component c : comps) p.add(c);
		return p;
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
	
	private static JPanel leftSxS(Component l, Component c, int gap) {
		JPanel p = new JPanel(new BorderLayout(gap, gap));
		p.add(l, BorderLayout.LINE_START);
		p.add(c, BorderLayout.CENTER);
		return p;
	}
	
	private static JPanel topSxS(Component t, Component c, int gap) {
		JPanel p = new JPanel(new BorderLayout(gap, gap));
		p.add(t, BorderLayout.PAGE_START);
		p.add(c, BorderLayout.CENTER);
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
	
	public void commit() {
		dead.winTerminator = this.winTerminator.getOutput();
		dead.macTerminator = this.macTerminator.getOutput();
		dead.macStateId = this.macStateId.getText();
		dead.xkbOutput = this.xkbOutput.getOutput();
		dead.xkbDeadKey = (XkbDeadKey)this.xkbDeadKey.getSelectedItem();
		this.keyMapModel.toMap(dead.keyMap);
	}
	
	private class AddKeyMapActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final int i = keyMapModel.getRowCount();
			keyMapModel.addEntry(32, (
				(dead.macTerminator > 0) ? dead.macTerminator :
				(dead.winTerminator > 0) ? dead.winTerminator :
				(dead.xkbOutput > 0) ? dead.xkbOutput : 32
			));
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					keyMapTable.getSelectionModel().setSelectionInterval(i, i);
					JScrollBar vsb = keyMapPane.getVerticalScrollBar();
					vsb.setValue(vsb.getMaximum());
				}
			});
		}
	}
	
	private class DeleteKeyMapActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int[] rows = keyMapTable.getSelectedRows();
			for (int i = rows.length - 1; i >= 0; i--) {
				keyMapModel.deleteEntry(rows[i]);
			}
		}
	}
	
	private class MoveUpKeyMapActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final int i = keyMapTable.getSelectedRow();
			if (i < 1) return;
			keyMapModel.moveEntry(i, -1);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					keyMapTable.getSelectionModel().setSelectionInterval(i-1, i-1);
				}
			});
		}
	}
	
	private class MoveDownKeyMapActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final int i = keyMapTable.getSelectedRow();
			if (i < 0 || i >= (keyMapModel.getRowCount()-1)) return;
			keyMapModel.moveEntry(i, +1);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					keyMapTable.getSelectionModel().setSelectionInterval(i+1, i+1);
				}
			});
		}
	}
}
