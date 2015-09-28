import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import com.kreative.bitsnpicas.*;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.S10BitmapFontImporter;
import com.kreative.bitsnpicas.importer.SFDBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SRFontBitmapFontImporter;


public class viewii extends JFrame {
	private static final long serialVersionUID = 1;
	
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				if (arg.endsWith(".sfd")) {
					File n = new File(arg);
					File m = new File(arg.substring(0,arg.length()-4)+"Mask.sfd");
					if (m.exists()) new viewii(new SFDBitmapFontImporter().importFont(n)[0], new SFDBitmapFontImporter().importFont(m)[0]);
					else view.open(new SFDBitmapFontImporter(), n);
				}
				else if (arg.toLowerCase().endsWith(".s10")) {
					view.open(new S10BitmapFontImporter(), arg);
				}
				else if (arg.toLowerCase().endsWith(".png")) {
					view.open(new SRFontBitmapFontImporter(), arg);
				}
				else if (arg.toLowerCase().endsWith(".dsf")) {
					view.open(new DSFBitmapFontImporter(), arg);
				}
				else {
					System.err.println("Unknown type: "+arg);
				}
			} catch (IOException e) {
				System.err.println("Could not load "+arg);
			}
		}
	}
	
	private static final Color BACKGROUND = new Color(0xFFCC66FF);
	private static final Color INSIDE = new Color(0xFFFFFFFF);
	private static final Color OUTSIDE = new Color(0xFF660066);
	
	private BitmapFont myFont;
	private BitmapFont myMaskFont;
	private JComponent alphaPanel;
	private JTextArea textArea;
	private JComponent textPanel;

	public viewii(BitmapFont bm, BitmapFont bmmask) {
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
