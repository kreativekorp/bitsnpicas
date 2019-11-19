package com.kreative.bitsnpicas.mover;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class MoverPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final MoverInfoPanel infoPanel;
	private final MoverTableModel tableModel;
	private final MoverTable table;
	private final JScrollPane tablePane;
	
	public MoverPanel(JFrame parent, File file, MoverFile mf, final SaveManager sm) {
		this.infoPanel = new MoverInfoPanel(file);
		this.tableModel = new MoverTableModel(mf);
		this.table = new MoverTable(tableModel, infoPanel);
		this.tablePane = new JScrollPane(
			table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
		);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		infoPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
		mainPanel.add(infoPanel, BorderLayout.PAGE_START);
		mainPanel.add(tablePane);
		table.createDropTarget(tablePane);
		
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		
		tableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				sm.setChanged();
			}
		});
		parent.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				table.requestFocusInWindow();
			}
		});
	}
	
	public MoverTableModel getTableModel() {
		return tableModel;
	}
	
	public MoverTable getTable() {
		return table;
	}
}
