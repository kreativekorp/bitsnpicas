package com.kreative.bitsnpicas.datatransfer;

import javax.swing.JMenuItem;

public class ClearMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	
	public ClearMenuItem() {
		super("Clear");
		setActionCommand("Clear");
		addActionListener(TransferActionListener.getInstance());
	}
}
