package com.kreative.mapedit;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface MappingTablePanelListener {
	public void focusGained(FocusEvent e, int i);
	public void focusLost(FocusEvent e, int i);
	public void keyPressed(KeyEvent e, int i);
	public void keyReleased(KeyEvent e, int i);
	public void keyTyped(KeyEvent e, int i);
	public void mouseClicked(MouseEvent e, int i);
	public void mouseEntered(MouseEvent e, int i);
	public void mouseExited(MouseEvent e, int i);
	public void mousePressed(MouseEvent e, int i);
	public void mouseReleased(MouseEvent e, int i);
}
