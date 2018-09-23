package com.kreative.mapedit;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
}
