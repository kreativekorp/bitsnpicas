package com.kreative.bitsnpicas.mover;

import javax.swing.JFrame;
import com.kreative.unicode.ttflib.DfontResource;

public class SoundFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public SoundFrame(DfontResource snd) {
		super(snd.getName());
		setJMenuBar(new SoundMenuBar(this, snd));
		setContentPane(new SoundPanel(snd));
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
