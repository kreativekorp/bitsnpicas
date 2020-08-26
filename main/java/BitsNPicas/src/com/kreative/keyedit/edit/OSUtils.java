package com.kreative.keyedit.edit;

public class OSUtils {
	public static final boolean IS_MAC_OS;
	public static final boolean IS_WINDOWS;
	static {
		boolean isMacOS;
		boolean isWindows;
		
		try {
			String os = System.getProperty("os.name").toUpperCase();
			isMacOS = os.contains("MAC OS");
			isWindows = os.contains("WINDOWS");
		} catch (Exception e) {
			isMacOS = false;
			isWindows = false;
		}
		
		try {
			String os = System.getProperty("com.kreative.keyedit.os.name");
			if (os != null) {
				os = os.trim().toUpperCase();
				if (os.length() > 0) {
					isMacOS = os.contains("MAC OS");
					isWindows = os.contains("WINDOWS");
				}
			}
		} catch (Exception e) {
			// Ignored
		}
		
		IS_MAC_OS = isMacOS;
		IS_WINDOWS = isWindows;
	}
}
