package com.kreative.bitsnpicas.edit.mac;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import com.kreative.bitsnpicas.edit.CommonMenuItems;

public class MacDummyWindow extends JFrame {
	public static final long serialVersionUID = 1L;
	
	public MacDummyWindow() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new CommonMenuItems.NewMenu());
		fileMenu.add(new CommonMenuItems.OpenMenuItem());
		JMenu editMenu = new JMenu("Edit");
		editMenu.add(new CommonMenuItems.FontMapMenuItem());
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
