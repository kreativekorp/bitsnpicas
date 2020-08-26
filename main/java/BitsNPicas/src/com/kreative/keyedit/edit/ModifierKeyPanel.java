package com.kreative.keyedit.edit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.keyedit.KeyboardMapping;
import com.kreative.keyedit.XkbAltGrKey;
import com.kreative.keyedit.XkbComposeKey;

public class ModifierKeyPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Color BG = new Color(0xFFCCCCCC);
	private static final Color FG = Color.black;
	private static final Color BORDER = new Color(0xFFBBBBBB);
	
	private final KeyboardMapping km;
	private final JLabel top;
	private final JLabel bot;
	private final String label;
	private final XkbComposeKey compose;
	private final XkbAltGrKey[] altgr;
	
	public ModifierKeyPanel(KeyboardMapping km, int w, String label, XkbComposeKey compose, XkbAltGrKey... altgr) {
		this.km = km;
		this.top = new JLabel(" ");
		this.bot = new JLabel(label);
		this.label = label;
		this.compose = compose;
		this.altgr = altgr;
		top.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
		bot.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
		top.setHorizontalAlignment(JLabel.CENTER);
		bot.setHorizontalAlignment(JLabel.CENTER);
		top.setOpaque(true);
		bot.setOpaque(true);
		top.setBackground(BG);
		bot.setBackground(BG);
		top.setForeground(FG);
		bot.setForeground(FG);
		JPanel innerPanel = new JPanel(new GridLayout(2, 1, 0, 0));
		innerPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, BORDER));
		innerPanel.add(top);
		innerPanel.add(bot);
		update();
		JPanel panel = new JPanel(new GridLayout(1, 1, 0, 0));
		panel.add(innerPanel);
		panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		panel.setPreferredSize(new Dimension(w * 15, 60));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(panel);
	}
	
	public void update() {
		if (OSUtils.IS_MAC_OS) {
			bot.setText(label);
		} else if (OSUtils.IS_WINDOWS) {
			boolean isRightAlt = (compose == XkbComposeKey.ralt);
			boolean isAltGr = isRightAlt && km.winAltGrEnable;
			bot.setText(isAltGr ? "alt gr" : label);
		} else {
			if (compose != null && km.xkbComposeKey == compose) {
				bot.setText("compose");
			} else {
				boolean isAltGr = false;
				for (XkbAltGrKey a : altgr) {
					if (km.xkbAltGrKey == a) {
						isAltGr = true;
					}
				}
				bot.setText(isAltGr ? "alt gr" : label);
			}
		}
	}
}
