package com.kreative.keyedit.edit;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface KeyMappingPanelListener {
	public void focusGained  (FocusEvent e, boolean alt, boolean shift);
	public void focusLost    (FocusEvent e, boolean alt, boolean shift);
	public void keyPressed   (KeyEvent   e, boolean alt, boolean shift);
	public void keyReleased  (KeyEvent   e, boolean alt, boolean shift);
	public void keyTyped     (KeyEvent   e, boolean alt, boolean shift);
	public void mouseClicked (MouseEvent e, boolean alt, boolean shift);
	public void mouseEntered (MouseEvent e, boolean alt, boolean shift);
	public void mouseExited  (MouseEvent e, boolean alt, boolean shift);
	public void mousePressed (MouseEvent e, boolean alt, boolean shift);
	public void mouseReleased(MouseEvent e, boolean alt, boolean shift);
}
