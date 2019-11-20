package com.kreative.bitsnpicas.mover;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.swing.JFrame;

public class TrueTypeFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public TrueTypeFrame(byte[] fontData) throws IOException, FontFormatException {
		Font font = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(fontData));
		setTitle(font.getFontName());
		setJMenuBar(new TrueTypeMenuBar(this, fontData));
		setContentPane(new TrueTypePanel(font));
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
