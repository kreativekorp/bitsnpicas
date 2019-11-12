package com.kreative.bitsnpicas.datatransfer;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class CopyMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	
	public CopyMenuItem() {
		super("Copy");
		int skm = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, skm));
		setActionCommand("Copy");
		addActionListener(TransferActionListener.getInstance());
	}
}
