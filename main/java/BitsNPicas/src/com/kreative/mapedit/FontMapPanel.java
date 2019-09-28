package com.kreative.mapedit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class FontMapPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final FontMapTableModel model;
	private final JTable table;
	private final JButton addButton;
	private final JButton removeButton;
	private final JButton upButton;
	private final JButton downButton;
	
	public FontMapPanel() {
		this(new FontMapTableModel());
	}
	
	public FontMapPanel(Collection<? extends FontMapEntry> c) {
		this(new FontMapTableModel(c));
	}
	
	private FontMapPanel(FontMapTableModel model) {
		this.model = model;
		this.table = new JTable(model);
		squareOff(this.addButton = new JButton("+"));
		squareOff(this.removeButton = new JButton("\u2212"));
		squareOff(this.upButton = new JButton("\u2191"));
		squareOff(this.downButton = new JButton("\u2193"));
		
		JPanel botPanel = new JPanel(new FlowLayout());
		botPanel.add(addButton);
		botPanel.add(removeButton);
		botPanel.add(upButton);
		botPanel.add(downButton);
		
		JScrollPane pane = new JScrollPane(table,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
		);
		
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.add(botPanel, BorderLayout.PAGE_END);
		mainPanel.add(pane, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(mainPanel);
		
		addActionListeners();
	}
	
	private void addActionListeners() {
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				index = (index < 0) ? model.size() : (index + 1);
				model.add(index, new FontMapEntry());
				table.getSelectionModel().setSelectionInterval(index, index);
			}
		});
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				if (index < 0) return;
				model.remove(index);
				table.clearSelection();
			}
		});
		upButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				if (index <= 0) return;
				FontMapEntry removed = model.remove(index);
				index--;
				model.add(index, removed);
				table.getSelectionModel().setSelectionInterval(index, index);
			}
		});
		downButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				if (index < 0 || index >= (model.size() - 1)) return;
				FontMapEntry removed = model.remove(index);
				index++;
				model.add(index, removed);
				table.getSelectionModel().setSelectionInterval(index, index);
			}
		});
	}
	
	public FontMapTableModel getFontMap() {
		return model;
	}
	
	private static void squareOff(JComponent c) {
		int h = c.getPreferredSize().height + 8;
		c.setMinimumSize(new Dimension(h, h));
		c.setPreferredSize(new Dimension(h, h));
		c.setMaximumSize(new Dimension(h, h));
		c.putClientProperty("JButton.buttonType", "bevel");
	}
}
