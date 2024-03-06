package com.kreative.keyedit.edit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.keyedit.DeadKeyTable;
import com.kreative.keyedit.KeyMapping;
import com.kreative.unicode.data.NameResolver;
import com.kreative.unicode.fontmap.FontMapController;
import com.kreative.unicode.fontmap.FontMapEntry;

public class KeyMappingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Color BG_LD = new Color(0xFFFFCC00);
	private static final Color FG_LD = Color.black;
	private static final Color BG_DK = new Color(0xFFFFDD55);
	private static final Color FG_DK = Color.black;
	private static final Color BG_LP = new Color(0xFFFFEEAA);
	private static final Color FG_LP = Color.black;
	private static final Color BG_NG = new Color(0xFFDDDDDD);
	private static final Color FG_NG = new Color(0xFFDDDDDD);
	private static final Color BG_CC = new Color(204, 255, 204);
	private static final Color FG_CC = Color.black;
	private static final Color BG_PU = new Color(238, 204, 255);
	private static final Color FG_PU = Color.black;
	private static final Color BG_OK = Color.white;
	private static final Color FG_OK = Color.black;
	private static final Color BLURRED = new Color(0xFFBBBBBB);
	private static final Color FOCUSED = new Color(0xFF6699CC);
	private static final Color INFOCUS = new Color(0xFF99CCFF);
	
	private final KeyMapping km;
	private final JLabel unshifted;
	private final JLabel shifted;
	private final JLabel altUnshifted;
	private final JLabel altShifted;
	private final JPanel innerPanel;
	private final Font defaultFont;
	private final List<KeyMappingPanelListener> listeners;
	
	public KeyMappingPanel(KeyMapping keymapping) {
		km = keymapping;
		unshifted = makeField("unshifted", false, false);
		shifted = makeField("shifted", false, true);
		altUnshifted = makeField("alt, unshifted", true, false);
		altShifted = makeField("alt, shifted", true, true);
		innerPanel = new JPanel(new GridLayout(2, 2, 0, 0));
		innerPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, BLURRED));
		innerPanel.add(shifted);
		innerPanel.add(altShifted);
		innerPanel.add(unshifted);
		innerPanel.add(altUnshifted);
		defaultFont = unshifted.getFont();
		listeners = new ArrayList<KeyMappingPanelListener>();
		update();
		JPanel panel = new JPanel(new GridLayout(1, 1, 0, 0));
		panel.add(innerPanel);
		panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		panel.setPreferredSize(new Dimension(60, 60));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(panel);
	}
	
	private JLabel makeField(String tooltip, boolean alt, boolean shift) {
		JLabel f = new JLabel();
		f.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		f.setFocusable(true);
		f.setHorizontalAlignment(JLabel.CENTER);
		f.setOpaque(true);
		f.setToolTipText(tooltip);
		MyListener ml = new MyListener(f, alt, shift);
		f.addFocusListener(ml);
		f.addKeyListener(ml);
		f.addMouseListener(ml);
		return f;
	}
	
	public void update() {
		update(false, false);
		update(false, true);
		update(true, false);
		update(true, true);
	}
	
	public void update(boolean alt, boolean shift) {
		JLabel field = getTextField(alt, shift);
		int output = getOutput(alt, shift);
		
		if (output > 0) {
			field.setText(cpString(output));
			FontMapEntry fme = FontMapController.getInstance().entryForCodePoint(output);
			field.setFont((fme != null) ? fme.getFont() : defaultFont);
		} else {
			field.setText(" ");
			field.setFont(defaultFont);
		}
		
		int[] lpo = getLongPressOutput(alt, shift);
		boolean hasLongPress = (lpo != null && lpo.length > 0);
		boolean hasDeadKey = (getDeadKey(alt, shift) != null);
		
		if (hasDeadKey && hasLongPress) {
			field.setBackground(BG_LD);
			field.setForeground(FG_LD);
		} else if (hasDeadKey) {
			field.setBackground(BG_DK);
			field.setForeground(FG_DK);
		} else if (hasLongPress) {
			field.setBackground(BG_LP);
			field.setForeground(FG_LP);
		} else if (output <= 0) {
			field.setBackground(BG_NG);
			field.setForeground(FG_NG);
		} else if (output < 0x20 || (output >= 0x7F && output < 0xA0)) {
			field.setBackground(BG_CC);
			field.setForeground(FG_CC);
		} else if ((output >= 0xE000 && output < 0xF900) || output >= 0xF0000) {
			field.setBackground(BG_PU);
			field.setForeground(FG_PU);
		} else {
			field.setBackground(BG_OK);
			field.setForeground(FG_OK);
		}
	}
	
	public KeyMapping getKeyMapping() {
		return km;
	}
	
	public JLabel getTextField(boolean alt, boolean shift) {
		return alt ? (shift ? altShifted : altUnshifted)
		           : (shift ? shifted : unshifted);
	}
	
	public int getOutput(boolean alt, boolean shift) {
		int output = alt ? (shift ? km.altShiftedOutput : km.altUnshiftedOutput)
		                 : (shift ? km.shiftedOutput : km.unshiftedOutput);
		
		DeadKeyTable dead = getDeadKey(alt, shift);
		if (dead != null) {
			if      (dead.macTerminator > 0) output = dead.macTerminator;
			else if (dead.winTerminator > 0) output = dead.winTerminator;
			else if (dead.xkbOutput     > 0) output = dead.xkbOutput;
		}
		
		return output;
	}
	
	public DeadKeyTable getDeadKey(boolean alt, boolean shift) {
		return alt ? (shift ? km.altShiftedDeadKey : km.altUnshiftedDeadKey)
		           : (shift ? km.shiftedDeadKey : km.unshiftedDeadKey);
	}
	
	public int[] getLongPressOutput(boolean alt, boolean shift) {
		return alt ? (shift ? km.altShiftedLongPressOutput : km.altUnshiftedLongPressOutput)
		           : (shift ? km.shiftedLongPressOutput : km.unshiftedLongPressOutput);
	}
	
	public void addListener(KeyMappingPanelListener listener) {
		if (listener != null) listeners.add(listener);
	}
	
	public void removeListener(KeyMappingPanelListener listener) {
		if (listener != null) listeners.remove(listener);
	}
	
	public KeyMappingPanelListener[] getListeners() {
		return listeners.toArray(new KeyMappingPanelListener[listeners.size()]);
	}
	
	private class MyListener implements FocusListener, KeyListener, MouseListener {
		private final JLabel field;
		private final boolean alt;
		private final boolean shift;
		public MyListener(JLabel field, boolean alt, boolean shift) {
			this.field = field;
			this.alt = alt;
			this.shift = shift;
		}
		public void focusGained(FocusEvent e) {
			innerPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, FOCUSED));
			field.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, INFOCUS));
			for (KeyMappingPanelListener l : listeners) l.focusGained(e, alt, shift);
		}
		public void focusLost(FocusEvent e) {
			innerPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, BLURRED));
			field.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			for (KeyMappingPanelListener l : listeners) l.focusLost(e, alt, shift);
		}
		public void keyPressed(KeyEvent e) {
			for (KeyMappingPanelListener l : listeners) l.keyPressed(e, alt, shift);
		}
		public void keyReleased(KeyEvent e) {
			for (KeyMappingPanelListener l : listeners) l.keyReleased(e, alt, shift);
		}
		public void keyTyped(KeyEvent e) {
			for (KeyMappingPanelListener l : listeners) l.keyTyped(e, alt, shift);
		}
		public void mouseClicked(MouseEvent e) {
			for (KeyMappingPanelListener l : listeners) l.mouseClicked(e, alt, shift);
		}
		public void mouseEntered(MouseEvent e) {
			for (KeyMappingPanelListener l : listeners) l.mouseEntered(e, alt, shift);
		}
		public void mouseExited(MouseEvent e) {
			for (KeyMappingPanelListener l : listeners) l.mouseExited(e, alt, shift);
		}
		public void mousePressed(MouseEvent e) {
			field.requestFocusInWindow();
			for (KeyMappingPanelListener l : listeners) l.mousePressed(e, alt, shift);
		}
		public void mouseReleased(MouseEvent e) {
			for (KeyMappingPanelListener l : listeners) l.mouseReleased(e, alt, shift);
		}
	}
	
	private static String cpString(int output) {
		if (output >= 0xFE00 && output <= 0xFE0F) return "vs" + (output - 0xFE00 + 1);
		if (output >= 0xE0100 && output <= 0xE01EF) return "vs" + (output - 0xE0100 + 17);
		switch (output) {
			case 0x00: return "nul";
			case 0x01: return "soh";
			case 0x02: return "stx";
			case 0x03: return "etx";
			case 0x04: return "eot";
			case 0x05: return "enq";
			case 0x06: return "ack";
			case 0x07: return "bel";
			case 0x08: return "bs";
			case 0x09: return "ht";
			case 0x0A: return "lf";
			case 0x0B: return "vt";
			case 0x0C: return "ff";
			case 0x0D: return "cr";
			case 0x0E: return "so";
			case 0x0F: return "si";
			case 0x10: return "dle";
			case 0x11: return "dc1";
			case 0x12: return "dc2";
			case 0x13: return "dc3";
			case 0x14: return "dc4";
			case 0x15: return "nak";
			case 0x16: return "syn";
			case 0x17: return "etb";
			case 0x18: return "can";
			case 0x19: return "em";
			case 0x1A: return "sub";
			case 0x1B: return "esc";
			case 0x1C: return "fs";
			case 0x1D: return "gs";
			case 0x1E: return "rs";
			case 0x1F: return "us";
			case 0x20: return "space";
			case 0x7F: return "del";
			case 0x80: return "pad";
			case 0x81: return "hop";
			case 0x82: return "bph";
			case 0x83: return "nbh";
			case 0x84: return "ind";
			case 0x85: return "nel";
			case 0x86: return "ssa";
			case 0x87: return "esa";
			case 0x88: return "hts";
			case 0x89: return "htj";
			case 0x8A: return "vts";
			case 0x8B: return "pld";
			case 0x8C: return "plu";
			case 0x8D: return "ri";
			case 0x8E: return "ss2";
			case 0x8F: return "ss3";
			case 0x90: return "dcs";
			case 0x91: return "pu1";
			case 0x92: return "pu2";
			case 0x93: return "sts";
			case 0x94: return "cch";
			case 0x95: return "mw";
			case 0x96: return "spa";
			case 0x97: return "epa";
			case 0x98: return "sos";
			case 0x99: return "sgc";
			case 0x9A: return "sci";
			case 0x9B: return "csi";
			case 0x9C: return "st";
			case 0x9D: return "osc";
			case 0x9E: return "pm";
			case 0x9F: return "apc";
			case 0xA0: return "nbsp";
			case 0xAD: return "-";
			case 0x02DE: return "◌˞";
			case 0x034F: return "cgj";
			case 0x061C: return "alm";
			case 0x070F: return "sam";
			case 0x115F: return "hcf";
			case 0x1160: return "hjf";
			case 0x17B4: return "kivaq";
			case 0x17B5: return "kivaa";
			case 0x180B: return "fvs1";
			case 0x180C: return "fvs2";
			case 0x180D: return "fvs3";
			case 0x180E: return "mvs";
			case 0x180F: return "fvs4";
			case 0x2000: return "nqsp";
			case 0x2001: return "mqsp";
			case 0x2002: return "ensp";
			case 0x2003: return "emsp";
			case 0x2004: return "3/msp";
			case 0x2005: return "4/msp";
			case 0x2006: return "6/msp";
			case 0x2007: return "fsp";
			case 0x2008: return "psp";
			case 0x2009: return "thsp";
			case 0x200A: return "hsp";
			case 0x200B: return "zwsp";
			case 0x200C: return "zwnj";
			case 0x200D: return "zwj";
			case 0x200E: return "lrm";
			case 0x200F: return "rlm";
			case 0x2028: return "lsep";
			case 0x2029: return "psep";
			case 0x202A: return "lre";
			case 0x202B: return "rle";
			case 0x202C: return "pdf";
			case 0x202D: return "lro";
			case 0x202E: return "rlo";
			case 0x202F: return "nnbsp";
			case 0x205F: return "mmsp";
			case 0x2060: return "wj";
			case 0x2066: return "lri";
			case 0x2067: return "rli";
			case 0x2068: return "fsi";
			case 0x2069: return "pdi";
			case 0x206A: return "iss";
			case 0x206B: return "ass";
			case 0x206C: return "iafs";
			case 0x206D: return "aafs";
			case 0x206E: return "nads";
			case 0x206F: return "nods";
			case 0x3000: return "idsp";
			case 0x3164: return "hf";
			case 0xFEFF: return "zwnbsp";
			case 0xFFA0: return "hwhf";
			case 0xFFF0: return "cd";
			case 0xFFF1: return "rd";
			case 0xFFF2: return "pd";
			case 0xFFF3: return "sd";
			case 0xFFF9: return "iaa";
			case 0xFFFA: return "ias";
			case 0xFFFB: return "iat";
			case 0xFFFC: return "obj";
			case 0x1107F: return "bnj";
			case 0x16FE4: return "kssf";
			case 0x1BC9D: return "dtls";
			case 0x1D159: return "msnn";
			case 0x1D173: return "msbb";
			case 0x1D174: return "mseb";
			case 0x1D175: return "msbt";
			case 0x1D176: return "mset";
			case 0x1D177: return "msbs";
			case 0x1D178: return "mses";
			case 0x1D179: return "msbp";
			case 0x1D17A: return "msep";
			case 0x1DA9B: return "swf2";
			case 0x1DA9C: return "swf3";
			case 0x1DA9D: return "swf4";
			case 0x1DA9E: return "swf5";
			case 0x1DA9F: return "swf6";
			case 0x1DAA1: return "swr2";
			case 0x1DAA2: return "swr3";
			case 0x1DAA3: return "swr4";
			case 0x1DAA4: return "swr5";
			case 0x1DAA5: return "swr6";
			case 0x1DAA6: return "swr7";
			case 0x1DAA7: return "swr8";
			case 0x1DAA8: return "swr9";
			case 0x1DAA9: return "swr10";
			case 0x1DAAA: return "swr11";
			case 0x1DAAB: return "swr12";
			case 0x1DAAC: return "swr13";
			case 0x1DAAD: return "swr14";
			case 0x1DAAE: return "swr15";
			case 0x1DAAF: return "swr16";
		}
		String s = String.valueOf(Character.toChars(output));
		NameResolver r = NameResolver.instance(output);
		if (r.getCategory(output).startsWith("M")) {
			// Combining Mark
			s = "◌" + s;
			String ccc = r.getCombiningClass(output);
			if (ccc.equals("233") || ccc.equals("234")) {
				// Double Combining Mark
				s = s + "◌";
			}
		}
		return s;
	}
}
