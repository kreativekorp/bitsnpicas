package com.kreative.mapedit;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MapEditSubtableFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public final MappingTable table;
	public final MappingTablePanel tablePanel;
	public final CodePointSequencePanel sequencePanel;
	public final MapEditController controller;
	
	public MapEditSubtableFrame(MapEditFrame parent, MappingTable table, String encodingPrefix) {
		super("Subtable " + encodingPrefix);
		this.table = table;
		this.tablePanel = new MappingTablePanel(table);
		this.sequencePanel = new CodePointSequencePanel(encodingPrefix + " ");
		this.controller = new MapEditController(parent, tablePanel, sequencePanel);
		
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.add(tablePanel, BorderLayout.LINE_START);
		mainPanel.add(sequencePanel, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(mainPanel);
		setJMenuBar(new MapEditMenuBar(parent, this, controller));
		
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
