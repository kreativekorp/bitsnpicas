package com.kreative.bitsnpicas;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MacUtility {
	public static File getDataFork(File file) {
		if (file.getName().equals("rsrc")) {
			File parent = file.getParentFile();
			if (parent != null && parent.getName().equals("..namedfork")) {
				File gparent = parent.getParentFile();
				if (gparent != null) return gparent;
			}
		}
		return file;
	}
	
	public static File getResourceFork(File file) {
		if (getDataFork(file) != file) return file;
		return new File(new File(file, "..namedfork"), "rsrc");
	}
	
	public static void setTypeAndCreator(File file, String type, String creator) {
		try {
			String[] cmd = {"/usr/bin/SetFile", "-t", type, "-c", creator, file.getAbsolutePath()};
			Process p = Runtime.getRuntime().exec(cmd);
			try { p.waitFor(); }
			catch (InterruptedException e) {}
		} catch (IOException e) {
			// Ignored.
		}
	}
	
	public static String getType(File file) {
		try {
			String[] cmd = {"/usr/bin/GetFileInfo", "-t", file.getAbsolutePath()};
			Process p = Runtime.getRuntime().exec(cmd);
			Scanner scan = new Scanner(p.getInputStream());
			if (scan.hasNextLine()) {
				String type = scan.nextLine().trim();
				scan.close();
				try { p.waitFor(); }
				catch (InterruptedException e) {}
				return unescape(type);
			}
			scan.close();
			try { p.waitFor(); }
			catch (InterruptedException e) {}
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static String getCreator(File file) {
		try {
			String[] cmd = {"/usr/bin/GetFileInfo", "-c", file.getAbsolutePath()};
			Process p = Runtime.getRuntime().exec(cmd);
			Scanner scan = new Scanner(p.getInputStream());
			if (scan.hasNextLine()) {
				String creator = scan.nextLine().trim();
				scan.close();
				try { p.waitFor(); }
				catch (InterruptedException e) {}
				return unescape(creator);
			}
			scan.close();
			try { p.waitFor(); }
			catch (InterruptedException e) {}
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	private static String unescape(String s) {
		StringBuffer sb = new StringBuffer();
		char[] chars = s.toCharArray();
		int i = 0, n = chars.length;
		if (chars[i] == '"' && chars[n-1] == '"') { i++; n--; }
		while (i < n) {
			char ch = chars[i++];
			if (ch == '\\' && i < n) {
				ch = chars[i++];
				switch (ch) {
					case '0': ch = 0; break;
					case 'a': ch = 7; break;
					case 'b': ch = 8; break;
					case 't': ch = 9; break;
					case 'n': ch = 10; break;
					case 'v': ch = 11; break;
					case 'f': ch = 12; break;
					case 'r': ch = 13; break;
					case 'e': ch = 27; break;
				}
			}
			sb.append(ch);
		}
		return sb.toString();
	}
}
