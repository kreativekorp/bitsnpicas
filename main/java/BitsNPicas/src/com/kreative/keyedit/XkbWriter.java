package com.kreative.keyedit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class XkbWriter {
	public static void write(File dir, KeyboardMapping km) throws IOException {
		dir.mkdir();
		appendEvdev(dir, km);
		writeSymbols(dir, km);
		if (km.icon != null) {
			File icon = unixNewFile(dir, km.getXkbPathNotEmpty() + ".png");
			ImageIO.write(km.icon, "png", icon);
		}
		writeInstall(new File(dir, "install.py"), km.getXkbPathNotEmpty(), km.getNameNotEmpty());
	}
	
	public static void appendEvdev(File dir, KeyboardMapping km) throws IOException {
		File evdev = new File(dir, "evdev.xml");
		if (evdev.isFile()) {
			List<String> preLines = new ArrayList<String>();
			List<String> postLines = new ArrayList<String>();
			boolean foundLayoutListEnd = false;
			Scanner scan = new Scanner(evdev, "UTF-8");
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				if (line.trim().equalsIgnoreCase("</layoutList>")) foundLayoutListEnd = true;
				(foundLayoutListEnd ? postLines : preLines).add(line);
			}
			scan.close();
			
			FileOutputStream fos = new FileOutputStream(evdev);
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
			for (String line : preLines) pw.print(line + "\n");
			writeEvdevLayout(pw, km);
			for (String line : postLines) pw.print(line + "\n");
			pw.flush();
			pw.close();
			fos.close();
		} else {
			FileOutputStream fos = new FileOutputStream(evdev);
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
			writeEvdev(pw, km);
			pw.flush();
			pw.close();
			fos.close();
		}
	}
	
	public static void writeEvdev(PrintWriter out, KeyboardMapping km) {
		out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		out.print("<!DOCTYPE xkbConfigRegistry SYSTEM \"xkb.dtd\">\n");
		out.print("<xkbConfigRegistry version=\"1.1\">\n");
		out.print("  <layoutList>\n");
		writeEvdevLayout(out, km);
		out.print("  </layoutList>\n");
		out.print("</xkbConfigRegistry>\n");
	}
	
	public static void writeEvdevLayout(PrintWriter out, KeyboardMapping km) {
		out.print("    <layout>\n");
		out.print("      <configItem>\n");
		out.print("        <name>" + xmlEncode(km.getXkbPathNotEmpty()) + "</name>\n");
		out.print("        <shortDescription>" + xmlEncode(km.getXkbLabelNotEmpty()) + "</shortDescription>\n");
		out.print("        <description>" + xmlEncode(km.getNameNotEmpty()) + "</description>\n");
		out.print("      </configItem>\n");
		out.print("      <variantList/>\n");
		out.print("    </layout>\n");
	}
	
	public static void writeSymbols(File dir, KeyboardMapping km) throws IOException {
		FileOutputStream fos = new FileOutputStream(unixNewFile(dir, km.getXkbPathNotEmpty()));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
		writeSymbols(pw, km);
		pw.flush();
		pw.close();
		fos.close();
	}
	
	public static void writeSymbols(PrintWriter out, KeyboardMapping km) {
		out.print("default partial alphanumeric_keys modifier_keys\n");
		out.print("xkb_symbols \"intl\" {\n");
		out.print("\n");
		out.print("  name[Group1]=\"" + km.getNameNotEmpty() + "\";\n");
		out.print("\n");
		
		if (km.xkbComment != null && km.xkbComment.length() > 0) {
			String[] lines = km.xkbComment.replaceAll("\r\n","\n").replaceAll("\r","\n").split("\n");
			for (String line : lines) out.print("  // " + line + "\n");
			out.print("\n");
		}
		
		List<String> unshifted = new ArrayList<String>();
		List<String> shifted = new ArrayList<String>();
		List<String> altUnshifted = new ArrayList<String>();
		List<String> altShifted = new ArrayList<String>();
		for (XkbKey key : XkbKey.KEYS) {
			if (key == null) {
				unshifted.add(null);
				shifted.add(null);
				altUnshifted.add(null);
				altShifted.add(null);
			} else {
				KeyMapping k = km.map.get(key.key);
				String du = keysym(key.key.defaultUnshifted, null, km.xkbUseKeySym, null);
				String ds = keysym(key.key.defaultShifted, null, km.xkbUseKeySym, null);
				String u = keysym(k.unshiftedOutput, k.unshiftedDeadKey, km.xkbUseKeySym, du);
				String s = keysym(k.shiftedOutput, k.shiftedDeadKey, km.xkbUseKeySym, ds);
				String au = keysym(k.altUnshiftedOutput, k.altUnshiftedDeadKey, km.xkbUseKeySym, u);
				String as = keysym(k.altShiftedOutput, k.altShiftedDeadKey, km.xkbUseKeySym, s);
				unshifted.add(u);
				shifted.add(s);
				altUnshifted.add(au);
				altShifted.add(as);
			}
		}
		
		int ul = maxLength(unshifted);
		int sl = maxLength(shifted);
		int aul = maxLength(altUnshifted);
		int asl = maxLength(altShifted);
		for (int i = 0, n = XkbKey.KEYS.size(); i < n; i++) {
			XkbKey key = XkbKey.KEYS.get(i);
			if (key == null) {
				out.print("\n");
			} else {
				String u = unshifted.get(i);
				String s = shifted.get(i);
				String au = altUnshifted.get(i);
				String as = altShifted.get(i);
				out.print("  key <" + key.id + "> { [ ");
				out.print(u + ", " + spaces(ul - u.length()));
				out.print(s + ", " + spaces(sl - s.length()));
				out.print(au + ", " + spaces(aul - au.length()));
				out.print(as + spaces(asl - as.length()));
				out.print(" ] };\n");
			}
		}
		
		boolean altgr = (km.xkbAltGrKey != null && km.xkbAltGrKey != XkbAltGrKey.none);
		boolean compose = (km.xkbComposeKey != null && km.xkbComposeKey != XkbComposeKey.none);
		if (altgr || compose) {
			out.print("\n");
			if (altgr) out.print("  include \"level3(" + km.xkbAltGrKey.name() + ")\"\n");
			if (compose) out.print("  include \"compose(" + km.xkbComposeKey.name() + ")\"\n");
		}
		
		out.print("\n");
		out.print("};\n");
	}
	
	public static void writeInstall(File install, String name, String dispName) throws IOException {
		FileOutputStream fos = new FileOutputStream(install);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
		writeInstall(pw, name, dispName);
		pw.flush();
		pw.close();
		fos.close();
		try {
			String[] cmd = {"chmod", "+x", install.getAbsolutePath()};
			Process p = Runtime.getRuntime().exec(cmd);
			try { p.waitFor(); }
			catch (InterruptedException e) {}
		} catch (IOException e) {
			// Ignored.
		}
	}
	
	public static void writeInstall(PrintWriter out, String name, String dispName) {
		Scanner scan = new Scanner(XkbWriter.class.getResourceAsStream("install.py"), "UTF-8");
		while (scan.hasNextLine()) out.println(scan.nextLine());
		scan.close();
		out.println();
		out.println();
		out.println("main(" + quote(name) + ", " + quote(dispName) + ")");
		out.println();
		out.println();
	}
	
	private static String quote(String s) {
		StringBuffer sb = new StringBuffer();
		sb.append('\'');
		for (char ch : s.toCharArray()) {
			if (ch == '\\' || ch == '\'') sb.append('\\');
			sb.append(ch);
		}
		sb.append('\'');
		return sb.toString();
	}
	
	private static String keysym(int output, DeadKeyTable dead, boolean useKeySym, String def) {
		if (dead != null) {
			if (dead.xkbDeadKey != null) {
				return dead.xkbDeadKey.name();
			}
			if (dead.xkbOutput > 0) {
				if (useKeySym || (dead.xkbOutput <= 0xA0)) {
					return XkbKeySym.MAP.getKeySym(dead.xkbOutput);
				} else {
					return XkbKeySym.MAP.getUKeySym(dead.xkbOutput);
				}
			}
		}
		if (output > 0) {
			if (useKeySym || output <= 0xA0) {
				return XkbKeySym.MAP.getKeySym(output);
			} else {
				return XkbKeySym.MAP.getUKeySym(output);
			}
		}
		return def;
	}
	
	private static int maxLength(Iterable<String> c) {
		int maxLength = 0;
		if (c != null) {
			for (String s : c) {
				if (s != null && s.length() > maxLength) {
					maxLength = s.length();
				}
			}
		}
		return maxLength;
	}
	
	private static String spaces(int n) {
		String s = " ";
		while (s.length() < n) s += s;
		return s.substring(0, n);
	}
	
	private static String xmlEncode(String s) {
		if (s == null) return "";
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (i < s.length()) {
			int ch = s.codePointAt(i);
			if (
				ch < 0x20 || ch >= 0x7F ||
				ch == '"' || ch == '\'' ||
				ch == '&' || ch == '<' || ch == '>'
			) {
				String h = Integer.toHexString(ch).toUpperCase();
				while (h.length() < 4) h = "0" + h;
				sb.append("&#x" + h + ";");
			} else {
				sb.append((char)ch);
			}
			i += Character.charCount(ch);
		}
		return sb.toString();
	}
	
	private static File unixNewFile(File dir, String path) {
		for (String name : path.split("/")) {
			dir.mkdir();
			dir = new File(dir, name);
		}
		return dir;
	}
}
