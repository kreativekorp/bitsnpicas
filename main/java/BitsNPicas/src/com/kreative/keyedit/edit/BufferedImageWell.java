package com.kreative.keyedit.edit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
			cb.setContents(new ImageSelection(image), new ClipboardOwner() {
				public void lostOwnership(Clipboard cb, Transferable t) {}
			});
		}
	}
	
	public void paste() {
		try {
			long start = System.currentTimeMillis();
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
				Object data = cb.getData(DataFlavor.imageFlavor);
				if (data instanceof Image) {
					Image image = (Image)data;
					int width = image.getWidth(null);
					int height = image.getHeight(null);
					while (width < 0 || height < 0) {
						if ((System.currentTimeMillis() - start) > 1000) return;
						width = image.getWidth(null);
						height = image.getHeight(null);
					}
					BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = bi.createGraphics();
					while (!g.drawImage(image, 0, 0, null)) {
						if ((System.currentTimeMillis() - start) > 1000) return;
					}
					g.dispose();
					setImage(bi);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
