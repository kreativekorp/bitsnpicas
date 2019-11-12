package com.kreative.bitsnpicas.datatransfer;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class CutMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	
	public CutMenuItem() {
		super("Cut");
		int skm = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, skm));
		setActionCommand("Cut");
		addActionListener(TransferActionListener.getInstance());
	}
}
