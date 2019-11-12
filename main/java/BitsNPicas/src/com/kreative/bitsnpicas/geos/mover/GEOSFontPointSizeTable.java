package com.kreative.bitsnpicas.geos.mover;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.edit.Main;
import com.kreative.bitsnpicas.geos.GEOSFontFile;
import com.kreative.bitsnpicas.geos.GEOSFontPointSize;
import com.kreative.bitsnpicas.importer.GEOSBitmapFontImporter;

public class GEOSFontPointSizeTable extends JTable {
	private static final long serialVersionUID = 1L;
	
	public GEOSFontPointSizeTable(GEOSFontPointSizeTableModel model) {
		super(model);
		setColumnWidth(0, 40);
		setColumnWidth(1, 100);
		setColumnWidth(2, 100);
		setDefaultEditor(Integer.class, new MyCellEditor());
		setDefaultRenderer(Integer.class, new MyCellRenderer());
		setDefaultRenderer(String.class, new MyCellRenderer());
		setIntercellSpacing(new Dimension(0,1));
		setRowHeight(26);
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					doOpen();
				}
			}
		});
		
		InputMap im = getInputMap();
		int skm = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, skm), "Cut");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, skm), "Copy");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, skm), "Paste");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, skm), "Clear");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, skm), "Clear");
		ActionMap am = getActionMap();
		am.put("Cut", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) { doCut(); }
		});
		am.put("Copy", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) { doCopy(); }
		});
		am.put("Paste", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) { doPaste(); }
		});
		am.put("Clear", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) { doClear(); }
		});
	}
	
	public GEOSFontPointSizeTableModel getPointSizeModel() {
		return (GEOSFontPointSizeTableModel)getModel();
	}
	
	public List<Integer> getSelectedPointSizes() {
		List<Integer> list = new ArrayList<Integer>();
		GEOSFontPointSizeTableModel model = getPointSizeModel();
		for (int i : getSelectedRows()) list.add(model.getPointSize(i));
		return list;
	}
	
	public List<GEOSFontPointSize> getSelectedPointSizeObjects() {
		List<GEOSFontPointSize> list = new ArrayList<GEOSFontPointSize>();
		GEOSFontFile gff = getPointSizeModel().getFontFile();
		for (int i : getSelectedPointSizes()) list.add(gff.getFontPointSize(i));
		return list;
	}
	
	public void doOpen() {
		GEOSBitmapFontImporter importer = new GEOSBitmapFontImporter();
		GEOSFontFile gff = getPointSizeModel().getFontFile();
		for (int i : getSelectedPointSizes()) {
			BitmapFont font = importer.importFont(gff, i);
			Main.openFont(null, null, font);
		}
	}
	
	public void doCut() {
		doCopy();
		doClear();
	}
	
	public void doCopy() {
		List<GEOSFontPointSize> gfps = getSelectedPointSizeObjects();
		if (gfps.isEmpty()) return;
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new GEOSFontPointSizeSelection(gfps), new ClipboardOwner() {
			public void lostOwnership(Clipboard cb, Transferable t) {}
		});
	}
	
	public void doPaste() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		DataFlavor flavor = GEOSFontPointSizeSelection.geosFontPointSizeFlavor;
		if (cb.isDataFlavorAvailable(flavor)) {
			try {
				List<?> gfpsl = (List<?>)cb.getData(flavor);
				if (gfpsl == null || gfpsl.isEmpty()) return;
				GEOSFontPointSizeTableModel model = getPointSizeModel();
				GEOSFontFile gff = model.getFontFile();
				for (Object gfpso : gfpsl) {
					GEOSFontPointSize gfps = (GEOSFontPointSize)gfpso;
					gff.setFontPointSize(gfps.pointSize, gfps);
				}
				model.refresh();
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void doClear() {
		GEOSFontPointSizeTableModel model = getPointSizeModel();
		GEOSFontFile gff = model.getFontFile();
		for (int i : getSelectedPointSizes()) gff.removeFontPointSize(i);
		model.refresh();
	}
	
	private void setColumnWidth(int i, int width) {
		TableColumn col = getColumnModel().getColumn(i);
		col.setWidth(width);
		col.setMinWidth(width);
		col.setMaxWidth(width);
	}
	
	private static class MyCellEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;
		public MyCellEditor() {
			super(new JTextField());
		}
	}
	
	private static class MyCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final Border padding = BorderFactory.createEmptyBorder(0, 4, 0, 4);
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JComponent c = (JComponent)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBorder(BorderFactory.createCompoundBorder(c.getBorder(), padding));
			return c;
	    }
	}
}
