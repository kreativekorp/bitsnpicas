package com.kreative.keyedit.edit;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.keyedit.Key;
import com.kreative.keyedit.KeyboardMapping;
import com.kreative.keyedit.XkbAltGrKey;
import com.kreative.keyedit.XkbComposeKey;

public class KeyboardMappingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final KeyboardMapping km;
	private final Map<Key,KeyMappingPanel> keyPanels;
	private final Set<ModifierKeyPanel> modifierPanels;
	private final List<KeyboardMappingPanelListener> listeners;
	
	public KeyboardMappingPanel(KeyboardMapping keyboard) {
		km = keyboard;
		keyPanels = new HashMap<Key,KeyMappingPanel>();
		modifierPanels = new HashSet<ModifierKeyPanel>();
		listeners = new ArrayList<KeyboardMappingPanelListener>();
		
		JPanel panel = new JPanel(new FixedGridBagLayout());
		FixedGridBagConstraints gbc = new FixedGridBagConstraints();
		gbc.gridheight = 4;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		addKeys(
			panel, gbc, Key.GRAVE_TILDE, Key.NUMROW_1, Key.NUMROW_2, Key.NUMROW_3,
			Key.NUMROW_4, Key.NUMROW_5, Key.NUMROW_6, Key.NUMROW_7, Key.NUMROW_8,
			Key.NUMROW_9, Key.NUMROW_0, Key.HYPHEN_UNDERSCORE, Key.EQUALS_PLUS
		);
		gbc.gridwidth = 8;
		addModifier(panel, gbc, (OSUtils.IS_MAC_OS ? "delete" : "backspace"), null);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 6;
		addModifier(panel, gbc, "tab", null);
		gbc.gridwidth = 4;
		addKeys(
			panel, gbc, Key.Q, Key.W, Key.E, Key.R, Key.T, Key.Y, Key.U,
			Key.I, Key.O, Key.P, Key.LEFT_BRACKET, Key.RIGHT_BRACKET
		);
		gbc.gridwidth = 6;
		addKeys(panel, gbc, Key.BACKSLASH);
		
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 7;
		addModifier(panel, gbc, "caps lock", XkbComposeKey.caps, XkbAltGrKey.caps_switch);
		gbc.gridwidth = 4;
		addKeys(
			panel, gbc, Key.A, Key.S, Key.D, Key.F, Key.G, Key.H,
			Key.J, Key.K, Key.L, Key.SEMICOLON, Key.QUOTE
		);
		gbc.gridwidth = 9;
		addModifier(panel, gbc, (OSUtils.IS_MAC_OS ? "return" : "enter"), null);
		
		gbc.gridx = 0;
		gbc.gridy = 12;
		gbc.gridwidth = 9;
		addModifier(panel, gbc, "shift", null);
		gbc.gridwidth = 4;
		addKeys(
			panel, gbc, Key.Z, Key.X, Key.C, Key.V, Key.B,
			Key.N, Key.M, Key.COMMA, Key.PERIOD, Key.SLASH
		);
		gbc.gridwidth = 11;
		addModifier(panel, gbc, "shift", null);
		
		gbc.gridx = 0;
		gbc.gridy = 16;
		if (OSUtils.IS_MAC_OS) {
			gbc.gridwidth = 6;
			addModifier(panel, gbc, "control", null);
			gbc.gridwidth = 5;
			addModifier(panel, gbc, "option", null);
			gbc.gridwidth = 6;
			addModifier(panel, gbc, "command", null);
			gbc.gridwidth = 26;
			addKeys(panel, gbc, Key.SPACE);
			gbc.gridwidth = 6;
			addModifier(panel, gbc, "command", null);
			gbc.gridwidth = 5;
			addModifier(panel, gbc, "option", null);
			gbc.gridwidth = 6;
			addModifier(panel, gbc, "control", null);
		} else {
			gbc.gridwidth = 6;
			addModifier(panel, gbc, "ctrl", XkbComposeKey.lctrl);
			gbc.gridwidth = 5;
			addModifier(panel, gbc, "❖",    XkbComposeKey.lwin, XkbAltGrKey.lwin_switch, XkbAltGrKey.win_switch);
			addModifier(panel, gbc, "alt",  null,               XkbAltGrKey.lalt_switch, XkbAltGrKey.alt_switch);
			gbc.gridwidth = 23;
			addKeys(panel, gbc, Key.SPACE);
			gbc.gridwidth = 5;
			addModifier(panel, gbc, "alt",  XkbComposeKey.ralt, XkbAltGrKey.ralt_switch, XkbAltGrKey.alt_switch);
			addModifier(panel, gbc, "❖",    XkbComposeKey.rwin, XkbAltGrKey.rwin_switch, XkbAltGrKey.win_switch);
			addModifier(panel, gbc, "▤",    XkbComposeKey.menu, XkbAltGrKey.menu_switch);
			gbc.gridwidth = 6;
			addModifier(panel, gbc, "ctrl", XkbComposeKey.rctrl);
		}

		gbc.gridwidth = 4;
		gbc.gridx = 62;
		gbc.gridy = 0;
		addModifier(panel, gbc, (OSUtils.IS_MAC_OS ? "clear" : "numlk"), null);
		addKeys(panel, gbc, Key.NUMPAD_EQUALS, Key.NUMPAD_DIVIDE, Key.NUMPAD_TIMES);
		gbc.gridx = 62;
		gbc.gridy = 4;
		addKeys(panel, gbc, Key.NUMPAD_7, Key.NUMPAD_8, Key.NUMPAD_9, Key.NUMPAD_MINUS);
		gbc.gridx = 62;
		gbc.gridy = 8;
		addKeys(panel, gbc, Key.NUMPAD_4, Key.NUMPAD_5, Key.NUMPAD_6, Key.NUMPAD_PLUS);
		gbc.gridx = 62;
		gbc.gridy = 12;
		addKeys(panel, gbc, Key.NUMPAD_1, Key.NUMPAD_2, Key.NUMPAD_3);
		gbc.gridheight = 8;
		addModifier(panel, gbc, "enter", null, XkbAltGrKey.enter_switch);
		gbc.gridheight = 4;
		gbc.gridx = 62;
		gbc.gridy = 16;
		addKeys(panel, gbc, Key.NUMPAD_0, Key.NUMPAD_COMMA, Key.NUMPAD_PERIOD);
		
		gbc.gridy = 22;
		gbc.gridx = 0;
		addKeys(panel, gbc, Key.SECTION);
		gbc.gridx = 5;
		addKeys(panel, gbc, Key.BACKSLASH_102);
		gbc.gridx = 52;
		addKeys(panel, gbc, Key.YEN);
		addKeys(panel, gbc, Key.UNDERSCORE);
		
		gbc.gridheight = 2;
		gbc.gridy = 20;
		gbc.gridx = 0;
		addLabel(panel, gbc, "Mac ISO");
		gbc.gridx = 5;
		addLabel(panel, gbc, "Win 102");
		gbc.gridx = 52;
		gbc.gridwidth = 8;
		addLabel(panel, gbc, "Mac JIS");
		
		update();
		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(panel);
	}
	
	private void addKeys(JPanel panel, FixedGridBagConstraints gbc, Key... keys) {
		for (Key key : keys) {
			KeyMappingPanel kmp = new KeyMappingPanel(km.map.get(key));
			kmp.addListener(new MyListener(key));
			keyPanels.put(key, kmp);
			Dimension d = new Dimension(gbc.gridwidth*15, gbc.gridheight*15);
			kmp.setMinimumSize(d);
			kmp.setPreferredSize(d);
			kmp.setMaximumSize(d);
			panel.add(kmp, gbc);
			gbc.gridx += gbc.gridwidth;
		}
	}
	
	private void addModifier(JPanel panel, FixedGridBagConstraints gbc, String label, XkbComposeKey compose, XkbAltGrKey... altgr) {
		ModifierKeyPanel mkp = new ModifierKeyPanel(km, gbc.gridwidth, label, compose, altgr);
		modifierPanels.add(mkp);
		Dimension d = new Dimension(gbc.gridwidth*15, gbc.gridheight*15);
		mkp.setMinimumSize(d);
		mkp.setPreferredSize(d);
		mkp.setMaximumSize(d);
		panel.add(mkp, gbc);
		gbc.gridx += gbc.gridwidth;
	}
	
	private void addLabel(JPanel panel, FixedGridBagConstraints gbc, String text) {
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(JLabel.CENTER);
		Dimension d = new Dimension(gbc.gridwidth*15, gbc.gridheight*15);
		label.setMinimumSize(d);
		label.setPreferredSize(d);
		label.setMaximumSize(d);
		panel.add(label, gbc);
		gbc.gridx += gbc.gridwidth;
	}
	
	public void update() {
		updateKeys();
		updateModifiers();
	}
	
	public void updateKeys() {
		for (KeyMappingPanel p : keyPanels.values()) p.update();
	}
	
	public void updateModifiers() {
		for (ModifierKeyPanel p : modifierPanels) p.update();
	}
	
	public KeyboardMapping getKeyboardMapping() {
		return km;
	}
	
	public KeyMappingPanel getKeyMappingPanel(Key key) {
		return keyPanels.get(key);
	}
	
	public void setSelectedKey(Key key, boolean alt, boolean shift) {
		keyPanels.get(key).getTextField(alt, shift).requestFocusInWindow();
	}
	
	public void addListener(KeyboardMappingPanelListener listener) {
		if (listener != null) listeners.add(listener);
	}
	
	public void removeListener(KeyboardMappingPanelListener listener) {
		if (listener != null) listeners.remove(listener);
	}
	
	public KeyboardMappingPanelListener[] getListeners() {
		return listeners.toArray(new KeyboardMappingPanelListener[listeners.size()]);
	}
	
	private class MyListener implements KeyMappingPanelListener {
		private final Key key;
		public MyListener(Key key) {
			this.key = key;
		}
		public void focusGained(FocusEvent e, boolean alt, boolean shift) {
			for (KeyboardMappingPanelListener l : listeners) l.focusGained(e, key, alt, shift);
		}
		public void focusLost(FocusEvent e, boolean alt, boolean shift) {
			for (KeyboardMappingPanelListener l : listeners) l.focusLost(e, key, alt, shift);
		}
		public void keyPressed(KeyEvent e, boolean alt, boolean shift) {
			Key key = MyListener.this.key;
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					alt = !alt;
					if (alt) key = getKeyLeftwards(key);
					if (key == null) return;
					setSelectedKey(key, alt, shift);
					return;
				case KeyEvent.VK_RIGHT:
					alt = !alt;
					if (!alt) key = getKeyRightwards(key);
					if (key == null) return;
					setSelectedKey(key, alt, shift);
					return;
				case KeyEvent.VK_UP:
					shift = !shift;
					if (!shift) {
						KeyAndAlt kaa = getKeyAbove(key, alt);
						if (kaa == null) return;
						key = kaa.key;
						alt = kaa.alt;
					}
					setSelectedKey(key, alt, shift);
					return;
				case KeyEvent.VK_DOWN:
					shift = !shift;
					if (shift) {
						KeyAndAlt kaa = getKeyBelow(key, alt);
						if (kaa == null) return;
						key = kaa.key;
						alt = kaa.alt;
					}
					setSelectedKey(key, alt, shift);
					return;
				case KeyEvent.VK_ESCAPE:
				case KeyEvent.VK_CLEAR:
					Component c = KeyboardMappingPanel.this;
					while (c != null) {
						if (c instanceof Window) {
							c.requestFocusInWindow();
							return;
						} else {
							c = c.getParent();
						}
					}
					return;
			}
			for (KeyboardMappingPanelListener l : listeners) l.keyPressed(e, key, alt, shift);
		}
		public void keyReleased(KeyEvent e, boolean alt, boolean shift) {
			for (KeyboardMappingPanelListener l : listeners) l.keyReleased(e, key, alt, shift);
		}
		public void keyTyped(KeyEvent e, boolean alt, boolean shift) {
			for (KeyboardMappingPanelListener l : listeners) l.keyTyped(e, key, alt, shift);
		}
		public void mouseClicked(MouseEvent e, boolean alt, boolean shift) {
			for (KeyboardMappingPanelListener l : listeners) l.mouseClicked(e, key, alt, shift);
		}
		public void mouseEntered(MouseEvent e, boolean alt, boolean shift) {
			for (KeyboardMappingPanelListener l : listeners) l.mouseEntered(e, key, alt, shift);
		}
		public void mouseExited(MouseEvent e, boolean alt, boolean shift) {
			for (KeyboardMappingPanelListener l : listeners) l.mouseExited(e, key, alt, shift);
		}
		public void mousePressed(MouseEvent e, boolean alt, boolean shift) {
			for (KeyboardMappingPanelListener l : listeners) l.mousePressed(e, key, alt, shift);
		}
		public void mouseReleased(MouseEvent e, boolean alt, boolean shift) {
			for (KeyboardMappingPanelListener l : listeners) l.mouseReleased(e, key, alt, shift);
		}
	}
	
	private static final List<Key> LEFT_RIGHT_ORDER = Arrays.asList(
		// NUM ROW
		null, Key.GRAVE_TILDE, Key.NUMROW_1, Key.NUMROW_2, Key.NUMROW_3,
		Key.NUMROW_4, Key.NUMROW_5, Key.NUMROW_6, Key.NUMROW_7, Key.NUMROW_8,
		Key.NUMROW_9, Key.NUMROW_0, Key.HYPHEN_UNDERSCORE, Key.EQUALS_PLUS,
		Key.NUMPAD_EQUALS, Key.NUMPAD_DIVIDE, Key.NUMPAD_TIMES,
		// TOP ROW
		null, Key.Q, Key.W, Key.E, Key.R, Key.T, Key.Y, Key.U, Key.I,
		Key.O, Key.P, Key.LEFT_BRACKET, Key.RIGHT_BRACKET, Key.BACKSLASH,
		Key.NUMPAD_7, Key.NUMPAD_8, Key.NUMPAD_9, Key.NUMPAD_MINUS,
		// HOME ROW
		null, Key.A, Key.S, Key.D, Key.F, Key.G, Key.H, Key.J,
		Key.K, Key.L, Key.SEMICOLON, Key.QUOTE, Key.NUMPAD_4,
		Key.NUMPAD_5, Key.NUMPAD_6, Key.NUMPAD_PLUS,
		// BOT ROW
		null, Key.Z, Key.X, Key.C, Key.V, Key.B, Key.N, Key.M, Key.COMMA,
		Key.PERIOD, Key.SLASH, Key.NUMPAD_1, Key.NUMPAD_2, Key.NUMPAD_3,
		// SPACE ETC
		null, Key.SPACE, Key.NUMPAD_0, Key.NUMPAD_COMMA, Key.NUMPAD_PERIOD,
		null, Key.SECTION, Key.BACKSLASH_102, Key.YEN, Key.UNDERSCORE,
		null
	);
	
	private static Key getKeyLeftwards(Key key) {
		if (key == null) return null;
		int i = LEFT_RIGHT_ORDER.indexOf(key);
		return LEFT_RIGHT_ORDER.get(i - 1);
	}
	
	private static Key getKeyRightwards(Key key) {
		if (key == null) return null;
		int i = LEFT_RIGHT_ORDER.indexOf(key);
		return LEFT_RIGHT_ORDER.get(i + 1);
	}
	
	private static final class KeyAndAlt {
		private final Key key;
		private final boolean alt;
		private KeyAndAlt(Key key, boolean alt) {
			this.key = key;
			this.alt = alt;
		}
		public boolean equals(Object o) {
			if (o instanceof KeyAndAlt) {
				KeyAndAlt that = (KeyAndAlt)o;
				return this.key == that.key
				    && this.alt == that.alt;
			}
			return false;
		}
		public int hashCode() {
			int hash = key.hashCode();
			if (alt) hash = ~hash;
			return hash;
		}
	}
	
	private static KeyAndAlt P(Key key) { return new KeyAndAlt(key, false); }
	private static KeyAndAlt A(Key key) { return new KeyAndAlt(key, true); }
	private static final List<KeyAndAlt> UP_DOWN_ORDER = Arrays.asList(
		// These must come first for navigation away from space, alt space,
		// and alt backslash to align with the keys along their centers.
		null, P(Key.NUMROW_5), A(Key.R), A(Key.F), P(Key.V), P(Key.SPACE),
		null, P(Key.NUMROW_8), A(Key.U), A(Key.J), P(Key.M), A(Key.SPACE),
		null, A(Key.BACKSLASH), P(Key.UNDERSCORE),
		// LEFT HALF
		null, P(Key.GRAVE_TILDE), P(Key.SECTION),
		null, A(Key.GRAVE_TILDE), A(Key.SECTION),
		null, P(Key.NUMROW_1), P(Key.BACKSLASH_102),
		null, A(Key.NUMROW_1), P(Key.Q), P(Key.A), A(Key.BACKSLASH_102),
		null, P(Key.NUMROW_2), A(Key.Q), A(Key.A), P(Key.Z),
		null, A(Key.NUMROW_2), P(Key.W), P(Key.S), A(Key.Z),
		null, P(Key.NUMROW_3), A(Key.W), A(Key.S), P(Key.X),
		null, A(Key.NUMROW_3), P(Key.E), P(Key.D), A(Key.X),
		null, P(Key.NUMROW_4), A(Key.E), A(Key.D), P(Key.C), P(Key.SPACE),
		null, A(Key.NUMROW_4), P(Key.R), P(Key.F), A(Key.C), P(Key.SPACE),
		// CENTER
		null, A(Key.NUMROW_5), P(Key.T), P(Key.G), A(Key.V), P(Key.SPACE),
		null, P(Key.NUMROW_6), A(Key.T), A(Key.G), P(Key.B), P(Key.SPACE),
		null, A(Key.NUMROW_6), P(Key.Y), P(Key.H), A(Key.B), P(Key.SPACE),
		null, P(Key.NUMROW_7), A(Key.Y), A(Key.H), P(Key.N), A(Key.SPACE),
		null, A(Key.NUMROW_7), P(Key.U), P(Key.J), A(Key.N), A(Key.SPACE),
		// RIGHT HALF
		null, A(Key.NUMROW_8), P(Key.I), P(Key.K), A(Key.M), A(Key.SPACE),
		null, P(Key.NUMROW_9), A(Key.I), A(Key.K), P(Key.COMMA), A(Key.SPACE),
		null, A(Key.NUMROW_9), P(Key.O), P(Key.L), A(Key.COMMA), A(Key.SPACE),
		null, P(Key.NUMROW_0), A(Key.O), A(Key.L), P(Key.PERIOD),
		null, A(Key.NUMROW_0), P(Key.P), P(Key.SEMICOLON), A(Key.PERIOD),
		null, P(Key.HYPHEN_UNDERSCORE), A(Key.P), A(Key.SEMICOLON), P(Key.SLASH),
		null, A(Key.HYPHEN_UNDERSCORE), P(Key.LEFT_BRACKET), P(Key.QUOTE), A(Key.SLASH),
		null, P(Key.EQUALS_PLUS), A(Key.LEFT_BRACKET), A(Key.QUOTE),
		null, A(Key.EQUALS_PLUS), P(Key.RIGHT_BRACKET),
		null, A(Key.RIGHT_BRACKET), P(Key.YEN),
		null, P(Key.BACKSLASH), A(Key.YEN),
		null, A(Key.BACKSLASH), A(Key.UNDERSCORE),
		// NUM PAD
		null, P(Key.NUMPAD_7), P(Key.NUMPAD_4), P(Key.NUMPAD_1), P(Key.NUMPAD_0),
		null, A(Key.NUMPAD_7), A(Key.NUMPAD_4), A(Key.NUMPAD_1), A(Key.NUMPAD_0),
		null, P(Key.NUMPAD_EQUALS), P(Key.NUMPAD_8), P(Key.NUMPAD_5), P(Key.NUMPAD_2), P(Key.NUMPAD_COMMA),
		null, A(Key.NUMPAD_EQUALS), A(Key.NUMPAD_8), A(Key.NUMPAD_5), A(Key.NUMPAD_2), A(Key.NUMPAD_COMMA),
		null, P(Key.NUMPAD_DIVIDE), P(Key.NUMPAD_9), P(Key.NUMPAD_6), P(Key.NUMPAD_3), P(Key.NUMPAD_PERIOD),
		null, A(Key.NUMPAD_DIVIDE), A(Key.NUMPAD_9), A(Key.NUMPAD_6), A(Key.NUMPAD_3), A(Key.NUMPAD_PERIOD),
		null, P(Key.NUMPAD_TIMES), P(Key.NUMPAD_MINUS), P(Key.NUMPAD_PLUS),
		null, A(Key.NUMPAD_TIMES), A(Key.NUMPAD_MINUS), A(Key.NUMPAD_PLUS),
		null
	);
	
	private static KeyAndAlt getKeyAbove(Key key, boolean alt) {
		if (key == null) return null;
		int i = UP_DOWN_ORDER.indexOf(new KeyAndAlt(key, alt));
		return UP_DOWN_ORDER.get(i - 1);
	}
	
	private static KeyAndAlt getKeyBelow(Key key, boolean alt) {
		if (key == null) return null;
		int i = UP_DOWN_ORDER.indexOf(new KeyAndAlt(key, alt));
		return UP_DOWN_ORDER.get(i + 1);
	}
}
