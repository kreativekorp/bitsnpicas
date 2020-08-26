package com.kreative.keyedit.edit;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import com.kreative.keyedit.Key;

public interface KeyboardMappingPanelListener {
	public void focusGained  (FocusEvent e, Key key, boolean alt, boolean shift);
	public void focusLost    (FocusEvent e, Key key, boolean alt, boolean shift);
	public void keyPressed   (KeyEvent   e, Key key, boolean alt, boolean shift);
	public void keyReleased  (KeyEvent   e, Key key, boolean alt, boolean shift);
	public void keyTyped     (KeyEvent   e, Key key, boolean alt, boolean shift);
	public void mouseClicked (MouseEvent e, Key key, boolean alt, boolean shift);
	public void mouseEntered (MouseEvent e, Key key, boolean alt, boolean shift);
	public void mouseExited  (MouseEvent e, Key key, boolean alt, boolean shift);
	public void mousePressed (MouseEvent e, Key key, boolean alt, boolean shift);
	public void mouseReleased(MouseEvent e, Key key, boolean alt, boolean shift);
}
