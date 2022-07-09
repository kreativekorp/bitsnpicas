package com.kreative.bitsnpicas.mover;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.unicode.data.GlyphList;

public class KeyboardPanel extends JPanel implements KeyListener {
	private static final long serialVersionUID = 1L;
	
	private final byte[] kchr;
	private final JLabel[][] labels;
	private final GlyphList encoding;
	
	public KeyboardPanel(byte[] kchr, GlyphList encoding) {
		this.kchr = kchr;
		this.labels = new JLabel[4][];
		this.encoding = encoding;
		
		JPanel keyboard = new JPanel();
		keyboard.setLayout(new BoxLayout(keyboard, BoxLayout.PAGE_AXIS));
		for (int y = 0; y < keyWidths.length; y++) {
			labels[y] = new JLabel[keyWidths[y].length];
			JPanel row = new JPanel();
			row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
			for (int x = 0; x < keyWidths[y].length; x++) {
				int w = keyWidths[y][x] * 6 + ((x == 0) ? 2 : 1);
				int h = 4 * 6 + ((y == 0) ? 2 : 1);
				if (y >= 2 && x == 0) w++; // hack
				JLabel label = new JLabel(" ");
				label.setBorder(BorderFactory.createMatteBorder(
					((y == 0) ? 1 : 0),
					((x == 0) ? 1 : 0),
					1, 1, Color.gray
				));
				label.setBackground(Color.white);
				label.setOpaque(true);
				label.setHorizontalAlignment(JLabel.CENTER);
				label.setVerticalAlignment(JLabel.CENTER);
				Dimension d = new Dimension(w, h);
				label.setMinimumSize(d);
				label.setPreferredSize(d);
				label.setMaximumSize(d);
				labels[y][x] = label;
				row.add(label);
			}
			keyboard.add(row);
		}
		keyboard.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
		
		setLayout(new GridLayout());
		add(keyboard);
		refresh(null);
	}
	
	public void refresh(KeyEvent e) {
		int mods = getModifiers(e);
		int table = kchr[mods + 2] & 0xFF;
		int base = table * 128 + 260;
		int numTables = ((kchr[258] & 0xFF) << 8) | (kchr[259] & 0xFF);
		int deadBase = numTables * 128 + 262;
		int numDead = ((kchr[deadBase-2] & 0xFF) << 8) | (kchr[deadBase-1] & 0xFF);
		for (int y = 0; y < labels.length; y++) {
			for (int x = 0; x < labels[y].length; x++) {
				int code = keyCodes[y][x];
				boolean dead = false;
				int ch = kchr[base + code] & 0xFF;
				if (ch == 0) {
					for (int b = deadBase, i = 0; i < numDead; i++) {
						int numRec = ((kchr[b+2] & 0xFF) << 8) | (kchr[b+3] & 0xFF);
						int recLen = numRec * 2 + 6;
						if ((kchr[b+0] & 0xFF) == table) {
							if ((kchr[b+1] & 0xFF) == code) {
								dead = true;
								ch = kchr[b + recLen - 1] & 0xFF;
								break;
							}
						}
						b += recLen;
					}
				}
				Integer uch = encoding.get(ch);
				if (uch == null || uch < 32) {
					labels[y][x].setText(" ");
				} else {
					labels[y][x].setText(new String(Character.toChars(uch)));
				}
				labels[y][x].setBackground(dead ? deadKeyColor : Color.white);
			}
		}
	}
	
	public void keyPressed(KeyEvent e) { refresh(e); }
	public void keyReleased(KeyEvent e) { refresh(e); }
	public void keyTyped(KeyEvent e) { refresh(e); }
	
	private static final Color deadKeyColor = new Color(0xFFDD55);
	
	private static final int[][] keyWidths = new int[][] {
		{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 8  },
		{  6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6 },
		{   7, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 9   },
		{    9, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 7  },
	};
	
	private static final int[][] keyCodes = new int[][] {
		{ 50, 18, 19, 20, 21, 23, 22, 26, 28, 25, 29, 27, 24, 51  },
		{  48, 12, 13, 14, 15, 17, 16, 32, 34, 31, 35, 33, 30, 42 },
		{   57,  0,  1,  2,  3,  5,  4, 38, 40, 37, 41, 39, 36    },
		{    56,  6,  7,  8,  9, 11, 45, 46, 43, 47, 44, 10, 56   },
	};
	
	private static boolean isCapsLockDown = false;
	private static int getModifiers(KeyEvent e) {
		if (e == null) return 0;
		if (e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
			switch (e.getID()) {
				case KeyEvent.KEY_PRESSED: isCapsLockDown = true; break;
				case KeyEvent.KEY_RELEASED: isCapsLockDown = false; break;
			}
		}
		int mods = 0;
		if (e.isMetaDown()) mods |= 0x01;
		if (e.isShiftDown()) mods |= 0x02;
		if (isCapsLockDown) mods |= 0x04;
		if (e.isAltDown()) mods |= 0x08;
		if (e.isControlDown()) mods |= 0x10;
		return mods;
	}
}
