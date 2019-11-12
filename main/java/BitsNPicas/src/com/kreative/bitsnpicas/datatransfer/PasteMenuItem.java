package com.kreative.bitsnpicas.datatransfer;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class PasteMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	
	public PasteMenuItem() {
		super("Paste");
		int skm = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, skm));
		setActionCommand("Paste");
		addActionListener(TransferActionListener.getInstance());
	}
}
