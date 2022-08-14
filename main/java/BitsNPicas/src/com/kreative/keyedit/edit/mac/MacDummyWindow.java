package com.kreative.keyedit.edit.mac;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import com.kreative.keyedit.edit.KeyEditMenuBar;

public class MacDummyWindow extends JFrame {
	public static final long serialVersionUID = 1L;
	
	public MacDummyWindow() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new KeyEditMenuBar.NewMappingMenuItem());
		fileMenu.add(new KeyEditMenuBar.OpenMappingMenuItem());
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(new KeyEditMenuBar.FontMapMenuItem());
		JMenuBar mb = new JMenuBar();
		mb.add(fileMenu);
		mb.add(editMenu);
		setJMenuBar(mb);
		
		setUndecorated(true);
		setResizable(false);
		setMinimumSize(new Dimension(0,0));
		setPreferredSize(new Dimension(0,0));
		setMaximumSize(new Dimension(0,0));
		setSize(new Dimension(0,0));
		setLocation(-1000000, -1000000);
		setVisible(true);
	}
}
