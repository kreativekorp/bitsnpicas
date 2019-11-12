package com.kreative.bitsnpicas.geos.mover;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import com.kreative.bitsnpicas.geos.GEOSFontFile;

public class GEOSMoverPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final GEOSFontInfoPanel infoPanel;
	private final GEOSFontPointSizeTableModel tableModel;
	private final GEOSFontPointSizeTable table;
	
	public GEOSMoverPanel(JFrame parent, GEOSFontFile gff, final SaveManager sm) {
		this.infoPanel = new GEOSFontInfoPanel(gff);
		this.tableModel = new GEOSFontPointSizeTableModel(gff);
		this.table = new GEOSFontPointSizeTable(tableModel);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		infoPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		mainPanel.add(infoPanel, BorderLayout.PAGE_START);
		mainPanel.add(new JScrollPane(
			table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
		));
		
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		
		infoPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sm.setChanged();
			}
		});
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
}
