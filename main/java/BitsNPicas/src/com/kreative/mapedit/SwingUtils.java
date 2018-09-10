package com.kreative.mapedit;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class SwingUtils {
	public static void setOpaque(JPanel p, boolean opaque) {
		p.setOpaque(opaque);
		for (int i = 0, n = p.getComponentCount(); i < n; i++) {
			Component c = p.getComponent(i);
			if (c instanceof JPanel) setOpaque((JPanel)c, opaque);
		}
	}
	
	public static void setDefaultButton(final JRootPane rp, final JButton b) {
		rp.setDefaultButton(b);
	}
	
	public static void setCancelButton(final JRootPane rp, final JButton b) {
		rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		rp.getActionMap().put("cancel", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent ev) {
				b.doClick();
			}
		});
	}
	
	public static void setDontSaveButton(final JRootPane rp, final JButton b) {
		rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "dontSave");
		rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, rp.getToolkit().getMenuShortcutKeyMask()), "dontSave");
		rp.getActionMap().put("dontSave", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent ev) {
				b.doClick();
			}
		});
	}
	
	public static BufferedImage toBufferedImage(Image image) {
		if (image == null) return null;
		// Prepare
		Toolkit tk = Toolkit.getDefaultToolkit();
		boolean prepared = false;
		for (int attempts = 0; attempts < 10; attempts++) {
			prepared = tk.prepareImage(image, -1, -1, null);
			if (prepared) break;
			try { Thread.sleep(10); }
			catch (InterruptedException ie) { break; }
		}
		if (!prepared) return null;
		// Get Size
		int w = -1, h = -1;
		for (int attempts = 0; attempts < 10; attempts++) {
			w = image.getWidth(null);
			h = image.getHeight(null);
			if (w > 0 && h > 0) break;
			try { Thread.sleep(10); }
			catch (InterruptedException ie) { break; }
		}
		if (w < 1 || h < 1) return null;
		// Render
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		boolean drawn = false;
		for (int attempts = 0; attempts < 10; attempts++) {
			drawn = g.drawImage(image, 0, 0, null);
			if (drawn) break;
			try { Thread.sleep(10); }
			catch (InterruptedException ie) { break; }
		}
		g.dispose();
		if (!drawn) return null;
		// Return
		return bi;
	}
	
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	
	public static final Cursor CURSOR_CROSSHAIR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public static final Cursor CURSOR_MOVE = makeCursor(
			17, 17,
			new int[] {
					0,0,0,0,0,0,0,0,W,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,W,K,K,K,W,0,0,0,0,0,0,
					0,0,0,0,0,W,K,K,K,K,K,W,0,0,0,0,0,
					0,0,0,0,W,W,W,W,K,W,W,W,W,0,0,0,0,
					0,0,0,W,W,0,0,W,K,W,0,0,W,W,0,0,0,
					0,0,W,K,W,0,0,W,K,W,0,0,W,K,W,0,0,
					0,W,K,K,W,W,W,W,K,W,W,W,W,K,K,W,0,
					W,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,W,
					0,W,K,K,W,W,W,W,K,W,W,W,W,K,K,W,0,
					0,0,W,K,W,0,0,W,K,W,0,0,W,K,W,0,0,
					0,0,0,W,W,0,0,W,K,W,0,0,W,W,0,0,0,
					0,0,0,0,W,W,W,W,K,W,W,W,W,0,0,0,0,
					0,0,0,0,0,W,K,K,K,K,K,W,0,0,0,0,0,
					0,0,0,0,0,0,W,K,K,K,W,0,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,0,0,0,0,0,0,0,0,
			},
			8, 8,
			"Move"
	);
	
	public static final Cursor CURSOR_HAND_OPEN = makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					0,0,0,K,K,0,K,W,W,K,K,K,0,0,0,0,
					0,0,K,W,W,K,K,W,W,K,W,W,K,0,0,0,
					0,0,K,W,W,K,K,W,W,K,W,W,K,0,K,0,
					0,0,0,K,W,W,K,W,W,K,W,W,K,K,W,K,
					0,0,0,K,W,W,K,W,W,K,W,W,K,W,W,K,
					0,K,K,0,K,W,W,W,W,W,W,W,K,W,W,K,
					K,W,W,K,K,W,W,W,W,W,W,W,W,W,W,K,
					K,W,W,W,K,W,W,W,W,W,W,W,W,W,K,0,
					0,K,W,W,W,W,W,W,W,W,W,W,W,W,K,0,
					0,0,K,W,W,W,W,W,W,W,W,W,W,W,K,0,
					0,0,K,W,W,W,W,W,W,W,W,W,W,K,0,0,
					0,0,0,K,W,W,W,W,W,W,W,W,W,K,0,0,
					0,0,0,0,K,W,W,W,W,W,W,W,K,0,0,0,
					0,0,0,0,0,K,W,W,W,W,W,W,K,0,0,0,
					0,0,0,0,0,K,W,W,W,W,W,W,K,0,0,0,
			},
			6, 6,
			"Hand"
	);
	
	public static final Cursor CURSOR_HAND_CLOSED = makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,K,K,0,K,K,0,0,0,0,
					0,0,0,K,W,W,K,W,W,K,W,W,K,K,0,0,
					0,0,0,K,W,W,W,W,W,W,W,W,K,W,K,0,
					0,0,0,0,K,W,W,W,W,W,W,W,W,W,K,0,
					0,0,0,K,K,W,W,W,W,W,W,W,W,W,K,0,
					0,0,K,W,W,W,W,W,W,W,W,W,W,W,K,0,
					0,0,K,W,W,W,W,W,W,W,W,W,W,W,K,0,
					0,0,K,W,W,W,W,W,W,W,W,W,W,K,0,0,
					0,0,0,K,W,W,W,W,W,W,W,W,W,K,0,0,
					0,0,0,0,K,W,W,W,W,W,W,W,K,0,0,0,
					0,0,0,0,0,K,W,W,W,W,W,W,K,0,0,0,
					0,0,0,0,0,K,W,W,W,W,W,W,K,0,0,0,
			},
			6, 6,
			"HandClosed"
	);
	
	public static Cursor makeCursor(int width, int height, int[] rgb, int hotx, int hoty, String name) {
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension d = tk.getBestCursorSize(width, height);
			if (d.width <= 0 || d.height <= 0) {
				System.err.println("Notice: System does not support custom cursors. Returning generic cursor.");
				return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			}
			if (d.width < width || d.height < height) {
				d = tk.getBestCursorSize(width*2, height*2);
				if (d.width < width || d.height < height) {
					System.err.println("Notice: Tool requested a cursor larger than possible on this system. Returning generic cursor.");
					return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
				}
			}
			BufferedImage img2 = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
			img2.setRGB(0, 0, width, height, rgb, 0, width);
			return tk.createCustomCursor(img2, new Point(hotx, hoty), name);
		} catch (Exception e) {
			e.printStackTrace();
			return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		}
	}
}
