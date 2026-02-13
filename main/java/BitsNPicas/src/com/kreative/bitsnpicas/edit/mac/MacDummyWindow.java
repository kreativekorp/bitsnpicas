package com.kreative.bitsnpicas.edit.mac;

import java.awt.Dimension;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.edit.CommonMenuItems;

public class MacDummyWindow extends JFrame {
	public static final long serialVersionUID = 1L;
	
	public MacDummyWindow() {
		setJMenuBar(new CommonMenuItems(null));
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
