package com.kreative.keyedit;

public enum XkbComposeKey {
	none("None"),
	ralt("Right Alt"),
	lctrl("Left Control"),
	rctrl("Right Control"),
	lwin("Left Windows"),
	rwin("Right Windows"),
	menu("Menu"),
	caps("Caps Lock"),
	paus("Pause"),
	prsc("Print Screen"),
	sclk("Scroll Lock");
	
	private final String label;
	
	private XkbComposeKey(String label) {
		this.label = label;
	}
	
	public String toString() {
		return this.label;
	}
	
	public static XkbComposeKey forInclude(String s) {
		if (s != null) {
			for (XkbComposeKey key : values()) {
				if (s.equalsIgnoreCase("compose(" + key.name() + ")")) {
					return key;
				}
			}
		}
		return none;
	}
}
