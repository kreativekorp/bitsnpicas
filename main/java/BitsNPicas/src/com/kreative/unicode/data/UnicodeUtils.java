package com.kreative.unicode.data;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

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
	
	public static int naturalCompare(String a, String b) {
		List<String> na = naturalTokenize(a.trim());
		List<String> nb = naturalTokenize(b.trim());
		for (int i = 0; i < na.size() && i < nb.size(); i++) {
			try {
				double va = Double.parseDouble(na.get(i));
				double vb = Double.parseDouble(nb.get(i));
				int cmp = Double.compare(va, vb);
				if (cmp != 0) return cmp;
			} catch (NumberFormatException e) {
				int cmp = na.get(i).compareToIgnoreCase(nb.get(i));
				if (cmp != 0) return cmp;
			}
		}
		return na.size() - nb.size();
	}
	
	private static List<String> naturalTokenize(String s) {
		List<String> tokens = new ArrayList<String>();
		StringBuffer token = new StringBuffer();
		int tokenType = 0;
		CharacterIterator iter = new StringCharacterIterator(s);
		for (char ch = iter.first(); ch != CharacterIterator.DONE; ch = iter.next()) {
			int tt = Character.isDigit(ch) ? 1 : Character.isLetter(ch) ? 2 : 3;
			if (tt != tokenType) {
				if (token.length() > 0) {
					tokens.add(token.toString());
					token = new StringBuffer();
				}
				tokenType = tt;
			}
			token.append(ch);
		}
		if (token.length() > 0) tokens.add(token.toString());
		return tokens;
	}
}
