package com.kreative.bitsnpicas.mover;

import javax.swing.JFrame;
import com.kreative.rsrc.SoundResource;

public class SoundFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public SoundFrame(SoundResource snd) {
		super(snd.name);
		setJMenuBar(new SoundMenuBar(this, snd));
		setContentPane(new SoundPanel(snd));
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
