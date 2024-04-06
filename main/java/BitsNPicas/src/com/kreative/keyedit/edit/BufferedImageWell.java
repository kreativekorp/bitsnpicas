package com.kreative.keyedit.edit;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class BufferedImageWell extends JLabel {
	private static final long serialVersionUID = 1L;
	private static final Color FOCUSED = new Color(0xFF6699CC);
	
	private BufferedImage image;
	
	public BufferedImageWell(BufferedImage image) {
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setVerticalAlignment(JLabel.CENTER);
		this.setImage(image);
		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		this.setFocusable(true);
		this.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, FOCUSED));
			}
			public void focusLost(FocusEvent e) {
				setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			}
		});
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				requestFocusInWindow();
			}
		});
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.isMetaDown() || e.isControlDown()) {
					switch (e.getKeyCode()) {
						case KeyEvent.VK_X:
						case KeyEvent.VK_CUT:
							cut();
							break;
						case KeyEvent.VK_C:
						case KeyEvent.VK_COPY:
							copy();
							break;
						case KeyEvent.VK_V:
						case KeyEvent.VK_PASTE:
							paste();
							break;
						case KeyEvent.VK_BACK_SPACE:
						case KeyEvent.VK_DELETE:
							setImage(null);
							break;
					}
				}
			}
		});
		new DropTarget(this, new DropTargetListener() {
			public void dragEnter(DropTargetDragEvent e) {}
			public void dragExit(DropTargetEvent e) {}
			public void dragOver(DropTargetDragEvent e) {}
			public void dropActionChanged(DropTargetDragEvent e) {}
			public void drop(DropTargetDropEvent e) {
				e.acceptDrop(e.getDropAction());
				Transferable t = e.getTransferable();
				try {
					if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
						Image image = (Image)t.getTransferData(DataFlavor.imageFlavor);
						if (image != null) {
							setImage(SwingUtils.toBufferedImage(image));
							e.dropComplete(true);
							return;
						}
					}
					if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						List<?> list = (List<?>)t.getTransferData(DataFlavor.javaFileListFlavor);
						if (list != null && list.size() == 1) {
							BufferedImage image = ImageIO.read((File)list.get(0));
							if (image != null) {
								setImage(image);
								e.dropComplete(true);
								return;
							}
						}
					}
				} catch (Exception e2) {}
				e.dropComplete(false);
			}
		});
	}
	
	public BufferedImage getImage() {
		return this.image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
		this.setIcon((image != null) ? new ImageIcon(image) : null);
	}
	
	public void cut() {
		copy();
		setImage(null);
	}
	
	public void copy() {
		if (image != null) {
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			ImageSelection sel = new ImageSelection(image);
			cb.setContents(sel, sel);
		}
	}
	
	public void paste() {
		try {
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
				Object data = cb.getData(DataFlavor.imageFlavor);
				if (data instanceof Image) {
					Image image = (Image)data;
					setImage(SwingUtils.toBufferedImage(image));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
