package com.kreative.bitsnpicas.main;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import com.kreative.bitsnpicas.*;
import com.kreative.bitsnpicas.importer.*;

public class ViewFont extends JFrame {
	private static final long serialVersionUID = 1;
	
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				File file = new File(arg);
				String lname = file.getName().toLowerCase();
				if (lname.endsWith(".kbits")) {
					open(new KBnPBitmapFontImporter(), file);
				} else if (lname.endsWith(".sfd")) {
					open(new SFDBitmapFontImporter(), file);
				} else if (lname.endsWith(".bdf")) {
					open(new BDFBitmapFontImporter(), file);
				} else if (lname.endsWith(".suit")) {
					file = new File(file, "..namedfork");
					file = new File(file, "rsrc");
					open(new NFNTBitmapFontImporter(), file);
				} else if (lname.endsWith(".dfont")) {
					open(new NFNTBitmapFontImporter(), file);
				} else if (lname.endsWith(".png")) {
					open(new SRFontBitmapFontImporter(), file);
				} else if (lname.endsWith(".fzx")) {
					open(new FZXBitmapFontImporter(), file);
				} else if (lname.endsWith(".dsf")) {
					open(new DSFBitmapFontImporter(), file);
				} else if (lname.endsWith(".s10")) {
					open(new S10BitmapFontImporter(), file);
				} else {
					System.err.println("Unknown type: " + arg);
				}
			} catch (IOException e) {
				System.err.println("Could not load " + arg);
			}
		}
	}
	
	public static void open(BitmapFontImporter im, File f) throws IOException {
		open(im.importFont(f));
	}
	
	public static void open(BitmapFont[] fonts) {
		for (BitmapFont font : fonts) new ViewFont(font);
	}
	
	private BitmapFont myFont;
	private JComponent alphaPanel;
	private JTextArea textArea;
	private JComponent textPanel;

	public ViewFont(BitmapFont bm) {
		myFont = bm;
		if (bm.getName(BitmapFont.NAME_FAMILY_AND_STYLE) != null) {
			setTitle(bm.getName(BitmapFont.NAME_FAMILY_AND_STYLE));
		}
		else if (bm.getName(BitmapFont.NAME_FAMILY) != null) {
			setTitle(bm.getName(BitmapFont.NAME_FAMILY));
		}

		alphaPanel = new JComponent() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				Insets i = getInsets();
				int x = i.left;
				int y = i.top;
				int w = getWidth()-i.left-i.right;
				int h = getHeight()-i.top-i.bottom;
				g.setColor(Color.white);
				g.fillRect(x, y, w, h);
				if (myFont != null) {
					g.setColor(Color.black);
					myFont.drawAlphabet(g, x, y+myFont.getLineAscent(), w);
				}
			}
		};
		textArea = new JTextArea("The quick brown fox jumped over the lazy dogs with a razorback-jumping frog that could level six piqued gymnasts who speak Latin: Lorem ipsum dolor sit amet...");
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
				if (textPanel != null) textPanel.repaint();
			}
			public void insertUpdate(DocumentEvent arg0) {
				if (textPanel != null) textPanel.repaint();
			}
			public void removeUpdate(DocumentEvent arg0) {
				if (textPanel != null) textPanel.repaint();
			}
		});
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textPanel = new JComponent() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				Insets i = getInsets();
				int x = i.left;
				int y = i.top;
				int w = getWidth()-i.left-i.right;
				int h = getHeight()-i.top-i.bottom;
				g.setColor(Color.white);
				g.fillRect(x, y, w, h);
				if (myFont != null && textArea != null) {
					g.setColor(Color.black);
					myFont.draw(g, textArea.getText(), x, y+myFont.getLineAscent(), w);
				}
			}
		};
		
		JPanel main = new JPanel(new GridLayout(3,1,10,10));
		main.add(alphaPanel);
		main.add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		main.add(textPanel);
		
		JPanel main2 = new JPanel(new BorderLayout(10,10));
		main2.add(main, BorderLayout.CENTER);
		main2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(main2);

		setSize(512, 342);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
