package com.kreative.bitsnpicas.mover;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.rsrc.SoundResource;

public class SoundPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public SoundPanel(final SoundResource snd) {
		JLabel iconLabel = new JLabel(new ImageIcon(SoundPanel.class.getResource("Sound.png")));
		iconLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		JButton playButton = new JButton("Play Sound");
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(playButton);
		buttonPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(Box.createGlue());
		mainPanel.add(iconLabel);
		mainPanel.add(buttonPanel);
		mainPanel.add(Box.createGlue());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		setLayout(new GridLayout());
		add(mainPanel);
		
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				byte[] stuff;
				if ((stuff = snd.toAiff()) != null) {
					try { playSound(stuff); return; }
					catch (UnsupportedAudioFileException ex) {}
				}
				if ((stuff = snd.toWav()) != null) {
					try { playSound(stuff); return; }
					catch (UnsupportedAudioFileException ex) {}
				}
			}
		});
	}
	
	private static void playSound(byte[] stuff) throws UnsupportedAudioFileException {
		if (stuff != null) try {
			AudioInputStream st = AudioSystem.getAudioInputStream(new ByteArrayInputStream(stuff));
			AudioFormat fm = st.getFormat();
			DataLine.Info inf = new DataLine.Info(Clip.class, fm, ((int)st.getFrameLength()*fm.getFrameSize()));
			Clip c = (Clip)AudioSystem.getLine(inf);
			c.open(st);
			c.start();
			c.drain();
			c.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
