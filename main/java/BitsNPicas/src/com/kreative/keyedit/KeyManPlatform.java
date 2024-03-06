package com.kreative.keyedit;

public enum KeyManPlatform {
	TABLET, PHONE, DESKTOP;
	private final String stringValue;
	private KeyManPlatform() { stringValue = name().toLowerCase(); }
	public String toString() { return stringValue; }
	public static KeyManPlatform forString(String s) {
		if (s != null) {
			s = s.toLowerCase();
			for (KeyManPlatform p : values()) {
				if (p.stringValue.equals(s)) {
					return p;
				}
			}
		}
		return null;
	}
}
