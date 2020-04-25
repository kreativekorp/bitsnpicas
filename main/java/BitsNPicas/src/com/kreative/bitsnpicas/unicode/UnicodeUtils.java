package com.kreative.bitsnpicas.unicode;

import java.io.File;

public class UnicodeUtils {
	public static File getTableDirectory(String subdirName) {
		File root;
		if (System.getProperty("os.name").toUpperCase().contains("MAC OS")) {
			File u = new File(System.getProperty("user.home"));
			File l = new File(u, "Library"); if (!l.exists()) l.mkdir();
			File p = new File(l, "Preferences"); if (!p.exists()) p.mkdir();
			root = new File(p, "com.kreative.unicode");
		} else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
			File u = new File(System.getProperty("user.home"));
			File a = new File(u, "Application Data"); if (!a.exists()) a.mkdir();
			File k = new File(a, "Kreative"); if (!k.exists()) k.mkdir();
			root = new File(k, "Unicode");
		} else {
			File u = new File(System.getProperty("user.home"));
			root = new File(u, ".com.kreative.unicode");
		}
		if (!root.exists()) root.mkdir();
		
		File subdir = new File(root, subdirName);
		if (!subdir.exists()) subdir.mkdir();
		return subdir;
	}
	
	public static String stripExtension(String fileName) {
		int o = fileName.lastIndexOf('.');
		return (o > 0) ? fileName.substring(0, o) : fileName;
	}
}
