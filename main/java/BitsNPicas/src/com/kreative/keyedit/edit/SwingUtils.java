package com.kreative.keyedit.edit;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class SwingUtils {
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
}
