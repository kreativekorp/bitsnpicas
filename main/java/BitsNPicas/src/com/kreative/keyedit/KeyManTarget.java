package com.kreative.keyedit;

public enum KeyManTarget {
	ANY, WINDOWS, MACOSX, LINUX, WEB, IPHONE, IPAD,
	ANDROID_PHONE, ANDROID_TABLET, MOBILE, DESKTOP, TABLET;
	private final String stringValue;
	private KeyManTarget() { stringValue = name().replace("_", "").toLowerCase(); }
	public String toString() { return stringValue; }
	public static KeyManTarget forString(String s) {
		if (s != null) {
			s = s.replace("_", "").toLowerCase();
			for (KeyManTarget t : values()) {
				if (t.stringValue.equals(s)) {
					return t;
				}
			}
		}
		return null;
	}
}
