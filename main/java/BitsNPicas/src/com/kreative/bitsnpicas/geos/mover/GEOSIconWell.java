package com.kreative.bitsnpicas.geos.mover;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.kreative.bitsnpicas.datatransfer.ImageSelection;
import com.kreative.bitsnpicas.geos.GEOSIcons;

public class GEOSIconWell extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private byte[] iconData;
	private BufferedImage iconImage;
	
	public GEOSIconWell() {
		resetIcon();
		init();
	}
	
	public GEOSIconWell(byte[] iconData) {
		setIcon(iconData);
		init();
	}
	
	public GEOSIconWell(Image iconImage) {
		setIcon(iconImage);
		init();
	}
	
	private void init() {
		this.setFocusable(true);
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				requestFocusInWindow();
			}
		});
		this.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) { repaint(); }
			public void focusLost(FocusEvent e) { repaint(); }
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
			public void actionPerformed(ActionEvent e) { resetIcon(); }
		});
		
		DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
			this, DnDConstants.ACTION_COPY, new DragGestureListener() {
				public void dragGestureRecognized(DragGestureEvent e) {
					e.startDrag(null, new ImageSelection(iconImage));
				}
			}
		);
		
		new DropTarget(this, new DropTargetListener() {
			public void dragEnter(DropTargetDragEvent e) {}
			public void dragExit(DropTargetEvent e) {}
			public void dragOver(DropTargetDragEvent e) {}
			public void dropActionChanged(DropTargetDragEvent e) {}
			public void drop(DropTargetDropEvent e) {
				try {
					e.acceptDrop(e.getDropAction());
					Transferable t = e.getTransferable();
					if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
						Image image = (Image)t.getTransferData(DataFlavor.imageFlavor);
						setIcon(image);
						e.dropComplete(true);
					} else {
						e.dropComplete(false);
					}
				} catch (Exception ex) {
					e.dropComplete(false);
				}
			}
		});
	}
	
	public byte[] getIconData() {
		return iconData;
	}
	
	public BufferedImage getIconImage() {
		return iconImage;
	}
	
	public void resetIcon() {
		this.iconData = GEOSIcons.fontIcon();
		this.iconImage = GEOSIcons.toImage(iconData);
		this.repaint();
		this.fireChangeEvent();
	}
	
	public void setIcon(byte[] iconData) {
		this.iconData = iconData;
		this.iconImage = GEOSIcons.toImage(iconData);
		this.repaint();
		this.fireChangeEvent();
	}
	
	public void setIcon(Image image) {
		BufferedImage bi = new BufferedImage(24, 21, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(image, 0, 0, 24, 21, null);
		g.dispose();
		this.iconData = GEOSIcons.fromImage(bi, 0, 0);
		this.iconImage = GEOSIcons.toImage(iconData);
		this.repaint();
		this.fireChangeEvent();
	}
	
	public void doCut() {
		doCopy();
		resetIcon();
	}
	
	public void doCopy() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new ImageSelection(iconImage), new ClipboardOwner() {
			public void lostOwnership(Clipboard cb, Transferable t) {}
		});
	}
	
	public void doPaste() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
			try {
				Image image = (Image)cb.getData(DataFlavor.imageFlavor);
				if (image != null) setIcon(image);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addChangeListener(ChangeListener cl) {
		this.listenerList.add(ChangeListener.class, cl);
	}
	
	public void removeChangeListener(ChangeListener cl) {
		this.listenerList.remove(ChangeListener.class, cl);
	}
	
	public ChangeListener[] getChangeListeners() {
		return this.listenerList.getListeners(ChangeListener.class);
	}
	
	private void fireChangeEvent() {
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener cl : getChangeListeners()) {
			cl.stateChanged(e);
		}
	}
	
	public Dimension getMinimumSize() {
		Insets i = getInsets();
		int w = 52 + i.left + i.right;
		int h = 46 + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	public Dimension getPreferredSize() {
		Insets i = getInsets();
		int w = 52 + i.left + i.right;
		int h = 46 + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	protected void paintComponent(Graphics g) {
		Insets insets = getInsets();
		int w = getWidth() - insets.left - insets.right - 4;
		int h = getHeight() - insets.top - insets.bottom - 4;
		int sf = Math.max(1, Math.min(w / 24, h / 21));
		int x = insets.left + (w - sf * 24) / 2 + 2;
		int y = insets.top + (h - sf * 21) / 2 + 2;
		w = sf * 24; h = sf * 21;
		if (this.hasFocus()) {
			g.setColor(new Color(0x99CCDD));
			g.fillRect(x-2, y-2, w+4, h+4);
		} else {
			g.setColor(new Color(0xEEEEEE));
			g.fillRect(x, y, w, h);
		}
		if (iconImage != null) g.drawImage(iconImage, x, y, w, h, null);
	}
}
