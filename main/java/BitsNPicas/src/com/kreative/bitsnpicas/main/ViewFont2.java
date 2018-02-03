package com.kreative.bitsnpicas.main;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import com.kreative.bitsnpicas.*;

public class ViewFont2 extends JFrame {
	private static final long serialVersionUID = 1;
	
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				File file = new File(arg);
				BitmapInputFormat format = BitmapInputFormat.forFile(file);
				if (format == null) {
					System.err.println("Unknown type: " + arg);
				} else {
					String ext = format.extensions[0];
					File mfile = sibling(file, ext.length(), "Mask" + ext);
					if (!mfile.exists()) {
						if (format.macResFork) {
							file = new File(new File(file, "..namedfork"), "rsrc");
						}
						BitmapFont[] fonts = format.createImporter().importFont(file);
						if (fonts == null || fonts.length == 0) {
							System.err.println("No fonts found: " + arg);
						} else {
							for (BitmapFont font : fonts) new ViewFont(font);
						}
					} else {
						if (format.macResFork) {
							file = new File(new File(file, "..namedfork"), "rsrc");
							mfile = new File(new File(mfile, "..namedfork"), "rsrc");
						}
						BitmapFont[] fonts = format.createImporter().importFont(file);
						BitmapFont[] mfonts = format.createImporter().importFont(mfile);
						if (fonts == null || mfonts == null || fonts.length != 1 || mfonts.length != 1) {
							System.err.println("Bad number of fonts found: " + arg);
						} else {
							new ViewFont2(fonts[0], mfonts[0]);
						}
					}
				}
			} catch (IOException e) {
				System.err.println("Could not load " + arg);
			}
		}
	}
	
	private static File sibling(File file, int extLength, String suffix) {
		File parent = file.getParentFile();
		String name = file.getName();
		name = name.substring(0, name.length() - extLength);
		return new File(parent, name + suffix);
	}
	
	private static final Color BACKGROUND = new Color(0xFFCC66FF);
	private static final Color INSIDE = new Color(0xFFFFFFFF);
	private static final Color OUTSIDE = new Color(0xFF660066);
	
	private BitmapFont myFont;
	private BitmapFont myMaskFont;
	private JComponent alphaPanel;
	private JTextArea textArea;
	private JComponent textPanel;

	public ViewFont2(BitmapFont bm, BitmapFont bmmask) {
		myFont = bm;
		myMaskFont = bmmask;
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
				g.setColor(BACKGROUND);
				g.fillRect(x, y, w, h);
				if (myMaskFont != null) {
					g.setColor(INSIDE);
					myMaskFont.drawAlphabet(g, x, y+myMaskFont.getLineAscent(), w);
				}
				if (myFont != null) {
					g.setColor(OUTSIDE);
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
				g.setColor(BACKGROUND);
				g.fillRect(x, y, w, h);
				if (myMaskFont != null && textArea != null) {
					g.setColor(INSIDE);
					myMaskFont.draw(g, textArea.getText(), x, y+myMaskFont.getLineAscent(), w);
				}
				if (myFont != null && textArea != null) {
					g.setColor(OUTSIDE);
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
