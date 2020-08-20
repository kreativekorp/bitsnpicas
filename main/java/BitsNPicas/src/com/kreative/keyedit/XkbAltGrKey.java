package com.kreative.keyedit;

public enum XkbAltGrKey {
	none("None"),
	alt_switch("Either Alt"),
	lalt_switch("Left Alt"),
	ralt_switch("Right Alt"),
	win_switch("Either Windows"),
	lwin_switch("Left Windows"),
	rwin_switch("Right Windows"),
	menu_switch("Menu"),
	caps_switch("Caps Lock"),
	bksl_switch("Backslash"),
	lsgt_switch("Less/Greater"),
	enter_switch("Keypad Enter");
	
	private final String label;
	
	private XkbAltGrKey(String label) {
		this.label = label;
	}
	
	public String toString() {
		return this.label;
	}
	
	public static XkbAltGrKey forInclude(String s) {
		if (s != null) {
			for (XkbAltGrKey key : values()) {
				if (s.equalsIgnoreCase("level3(" + key.name() + ")")) {
					return key;
				}
			}
		}
		return none;
	}
}
