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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.kreative.unicode.ttflib.DfontResource;

public class SoundPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public SoundPanel(final DfontResource snd) {
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
		
		try {
			SoundResource sr = new SoundResource(snd.getData());
			final byte[] aiffData = sr.toAiff();
			final byte[] wavData = sr.toWav();
			if (aiffData != null || wavData != null) {
				playButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (aiffData != null) {
							try { playSound(aiffData); return; }
							catch (UnsupportedAudioFileException ex) {}
						}
						if (wavData != null) {
							try { playSound(wavData); return; }
							catch (UnsupportedAudioFileException ex) {}
						}
						JOptionPane.showMessageDialog(
							null, "Could not produce audio in a supported format.",
							"Play Sound", JOptionPane.ERROR_MESSAGE
						);
					}
				});
			} else {
				playButton.setEnabled(false);
			}
		} catch (IOException e) {
			playButton.setEnabled(false);
		}
	}
	
	private static void playSound(byte[] stuff) throws UnsupportedAudioFileException {
		try {
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
