package com.kreative.mapedit;

import javax.swing.event.DocumentEvent;

public interface MappingNamePanelListener {
	public void nameChanged(DocumentEvent e);
	public void dateChanged(DocumentEvent e);
	public void authorChanged(DocumentEvent e);
}
