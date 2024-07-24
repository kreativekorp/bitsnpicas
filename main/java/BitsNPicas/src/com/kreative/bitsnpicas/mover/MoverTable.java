package com.kreative.bitsnpicas.mover;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.edit.Main;
import com.kreative.bitsnpicas.importer.NFNTBitmapFontImporter;
import com.kreative.unicode.ttflib.DfontFile;
import com.kreative.unicode.ttflib.DfontResource;

public class MoverTable extends JTable {
	private static final long serialVersionUID = 1L;
	
	private final MoverInfoPanel ip;
	
	public MoverTable(MoverTableModel model, MoverInfoPanel ip) {
		super(model);
		setColumnWidth(0, 40);
		setColumnWidth(2, 100);
		setColumnWidth(4, 100);
		setDefaultRenderer(ImageIcon.class, new MyIconRenderer());
		setDefaultRenderer(Integer.class, new MyCellRenderer());
		setDefaultRenderer(String.class, new MyCellRenderer());
		setIntercellSpacing(new Dimension(0,1));
		setShowGrid(false);
		setRowHeight(19);
		this.ip = ip;
		
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
		
		DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
			this, DnDConstants.ACTION_COPY, new DragGestureListener() {
				public void dragGestureRecognized(DragGestureEvent e) {
					if (e.getDragOrigin().x < 40) {
						List<ResourceBundle> rb = getSelectedItems();
						if (rb.isEmpty()) return;
						e.startDrag(null, new ResourceSelection(rb));
					}
				}
			}
		);
	}
	
	public void createDropTarget(JComponent c) {
		new DropTarget(c, new DropTargetListener() {
			public void dragEnter(DropTargetDragEvent e) {}
			public void dragExit(DropTargetEvent e) {}
			public void dragOver(DropTargetDragEvent e) {}
			public void dropActionChanged(DropTargetDragEvent e) {}
			public void drop(DropTargetDropEvent e) {
				if (ip.readOnly()) return;
				try {
					e.acceptDrop(e.getDropAction());
					Transferable t = e.getTransferable();
					DataFlavor fl = ResourceSelection.resourceFlavor;
					if (t.isDataFlavorSupported(fl)) {
						List<?> list = (List<?>)t.getTransferData(fl);
						if (list != null && !list.isEmpty()) {
							MoverTableModel model = getMoverModel();
							MoverFile mf = model.getMoverFile();
							for (Object o : list) mf.add((ResourceBundle)o);
							model.refresh();
							e.dropComplete(true);
						} else {
							e.dropComplete(false);
						}
					} else if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						List<?> list = (List<?>)t.getTransferData(DataFlavor.javaFileListFlavor);
						if (list != null && !list.isEmpty()) {
							for (Object o : list) {
								try { addFromFile(MacUtility.getResourceFork((File)o)); }
								catch (Exception ex2) { /* ignored */ }
								try { addFromFile((File)o); }
								catch (Exception ex2) { /* ignored */ }
							}
							e.dropComplete(true);
						} else {
							e.dropComplete(false);
						}
					} else {
						e.dropComplete(false);
					}
				} catch (Exception ex) {
					e.dropComplete(false);
				}
			}
		});
	}
	
	public MoverTableModel getMoverModel() {
		return (MoverTableModel)getModel();
	}
	
	public List<ResourceBundle> getSelectedItems() {
		List<ResourceBundle> list = new ArrayList<ResourceBundle>();
		MoverFile mf = getMoverModel().getMoverFile();
		for (int i : getSelectedRows()) list.add(mf.get(i));
		return list;
	}
	
	public void addFromFile(File f) throws IOException {
		if (ip.readOnly()) return;
		MoverTableModel model = getMoverModel();
		MoverFile outmf = model.getMoverFile();
		DfontFile rsrc = new DfontFile(f);
		MoverFile inmf = new MoverFile(rsrc);
		for (int i = 0, n = inmf.size(); i < n; i++) outmf.add(inmf.get(i));
		model.refresh();
	}
	
	public void doOpen() {
		List<ResourceBundle> list = getSelectedItems();
		if (list.isEmpty()) return;
		for (ResourceBundle rb : list) {
			if (rb.moverType.equals("ffil")) {
				try {
					BitmapFont[] fonts = new NFNTBitmapFontImporter.ResourceFile(
						ip.getSelectedEncoding()).importFont(rb);
					Main.openFonts(null, null, fonts);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			if (rb.moverType.equals("tfil")) {
				for (DfontResource res : rb.resources) {
					try {
						new TrueTypeFrame(res.getData()).setVisible(true);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (FontFormatException e) {
						e.printStackTrace();
					}
				}
			}
			if (rb.moverType.equals("sfil")) {
				for (DfontResource res : rb.resources) {
					new SoundFrame(res).setVisible(true);
				}
			}
			if (rb.moverType.equals("kfil")) {
				for (DfontResource res : rb.resources) {
					if (res.getTypeString().equals("KCHR")) {
						new KeyboardFrame(res, ip.getSelectedEncoding()).setVisible(true);
					}
				}
			}
		}
	}
	
	public void doCut() {
		if (ip.readOnly()) return;
		doCopy();
		doClear();
	}
	
	public void doCopy() {
		List<ResourceBundle> list = getSelectedItems();
		if (list.isEmpty()) return;
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new ResourceSelection(list), new ClipboardOwner() {
			public void lostOwnership(Clipboard cb, Transferable t) {}
		});
	}
	
	public void doPaste() {
		if (ip.readOnly()) return;
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		DataFlavor flavor = ResourceSelection.resourceFlavor;
		if (cb.isDataFlavorAvailable(flavor)) {
			try {
				List<?> list = (List<?>)cb.getData(flavor);
				if (list == null || list.isEmpty()) return;
				MoverTableModel model = getMoverModel();
				MoverFile mf = model.getMoverFile();
				for (Object o : list) mf.add((ResourceBundle)o);
				model.refresh();
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void doClear() {
		if (ip.readOnly()) return;
		MoverTableModel model = getMoverModel();
		MoverFile mf = model.getMoverFile();
		for (ResourceBundle rb : getSelectedItems()) mf.remove(rb);
		model.refresh();
	}
	
	private void setColumnWidth(int i, int width) {
		TableColumn col = getColumnModel().getColumn(i);
		col.setWidth(width);
		col.setMinWidth(width);
		col.setMaxWidth(width);
	}
	
	private static class MyIconRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final Border padding = BorderFactory.createEmptyBorder(0, 12, 0, 0);
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JComponent c = (JComponent)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setBorder(BorderFactory.createCompoundBorder(c.getBorder(), padding));
			if (c instanceof JLabel) {
				JLabel label = (JLabel)c;
				label.setHorizontalAlignment(JLabel.CENTER);
				label.setIcon((Icon)value);
				label.setText(null);
			}
			return c;
	    }
	}
	
	private static class MyCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final Border padding = BorderFactory.createEmptyBorder(0, 4, 0, 4);
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JComponent c = (JComponent)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (c instanceof JLabel) ((JLabel)c).setHorizontalAlignment((column == 2) ? JLabel.RIGHT : JLabel.LEFT);
			c.setBorder(BorderFactory.createCompoundBorder(c.getBorder(), padding));
			return c;
	    }
	}
}
