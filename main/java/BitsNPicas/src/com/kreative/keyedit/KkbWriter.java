package com.kreative.keyedit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class KkbWriter {
	public static void write(File file, KeyboardMapping km) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
		write(pw, km);
		pw.flush();
		pw.close();
		fos.close();
	}
	
	public static void write(PrintWriter out, KeyboardMapping km) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE keyboardMapping PUBLIC \"-//Kreative//DTD KreativeKeyboard 1.0//EN\" \"http://www.kreativekorp.com/dtd/kkbx.dtd\">");
		out.println("<keyboardMapping>");
		out.println(wrap("\t", "name", km.name));
		out.println(wrap("\t", "winIdentifier", km.winIdentifier));
		out.println(wrap("\t", "winCopyright", km.winCopyright));
		out.println(wrap("\t", "winCompany", km.winCompany));
		out.println(wrap("\t", "winLocale", "tag", km.getWinLocaleNotNull().tag));
		out.println(wrap("\t", "winAltGrEnable", "altgr", (km.winAltGrEnable ? "true" : "false")));
		out.println(wrap("\t", "winShiftLock", "shiftlock", (km.winShiftLock ? "true" : "false")));
		out.println(wrap("\t", "winLrmRlm", "lrmrlm", (km.winLrmRlm ? "true" : "false")));
		out.println(wrap("\t", "macGroupNumber", "group", Integer.toString(km.macGroupNumber)));
		out.println(wrap("\t", "macIdNumber", "id", Integer.toString(km.macIdNumber)));
		out.println(wrap("\t", "xkbPath", km.xkbPath));
		out.println(wrap("\t", "xkbLabel", km.xkbLabel));
		
		if (km.xkbComment != null) {
			out.println("\t<xkbComment>");
			for (String line : km.xkbComment.split("\r\n|\r|\n")) {
				out.println("\t\t" + xmlEncode(line));
			}
			out.println("\t</xkbComment>");
		}
		
		out.println(wrap("\t", "xkbUseKeySym", "for", (km.xkbUseKeySym ? "unicode" : "ascii")));
		
		if (km.xkbAltGrKey != null && km.xkbAltGrKey != XkbAltGrKey.none) {
			String include = "level3(" + km.xkbAltGrKey.name() + ")";
			out.println(wrap("\t", "xkbAltGrKey", "include", include));
		}
		
		if (km.xkbComposeKey != null && km.xkbComposeKey != XkbComposeKey.none) {
			String include = "compose(" + km.xkbComposeKey.name() + ")";
			out.println(wrap("\t", "xkbComposeKey", "include", include));
		}
		
		if (km.icon != null) {
			try {
				StringBuffer sb = new StringBuffer();
				Base64OutputStream b64 = new Base64OutputStream(sb);
				ImageIO.write(km.icon, "png", b64);
				b64.flush();
				b64.close();
				out.println(wrap("\t", "icon", sb.toString()));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		if (km.macIconVersion != null) {
			out.println(wrap("\t", "macIconVersion", "version", hex(km.macIconVersion,8)));
		}
		
		if (!km.map.isEmpty()) {
			out.println("\t<keyMappings>");
			for (Map.Entry<Key,KeyMapping> e : km.map.entrySet()) {
				write(out, e.getKey(), e.getValue());
			}
			out.println("\t</keyMappings>");
		}
		
		if (!km.macActionIds.isEmpty()) {
			out.println("\t<macActionIds>");
			for (Map.Entry<Integer,String> e : km.macActionIds.entrySet()) {
				out.println(wrap("\t\t", "macActionId", "input", hex(e.getKey(),4), "action", e.getValue()));
			}
			out.println("\t</macActionIds>");
		}
		
		writeHTMLConfig(out, km);
		out.println("</keyboardMapping>");
	}
	
	private static void write(PrintWriter out, Key k, KeyMapping km) {
		out.println(wrap("\t\t", "keyMapping", "key", k.name().toLowerCase(), false));
		write(out, "unshifted", km.unshiftedOutput, km.unshiftedDeadKey);
		write(out, "shifted", km.shiftedOutput, km.shiftedDeadKey);
		out.println(wrap("\t\t\t", "capsLock", "mapsTo", caps(km.capsLockMapping, "unshifted", "shifted", "auto")));
		write(out, "altUnshifted", km.altUnshiftedOutput, km.altUnshiftedDeadKey);
		write(out, "altShifted", km.altShiftedOutput, km.altShiftedDeadKey);
		out.println(wrap("\t\t\t", "altCapsLock", "mapsTo", caps(km.altCapsLockMapping, "altUnshifted", "altShifted", "auto")));
		write(out, "ctrl", km.ctrlOutput, km.ctrlDeadKey);
		write(out, "command", km.commandOutput, km.commandDeadKey);
		out.println("\t\t</keyMapping>");
	}
	
	private static void write(PrintWriter out, String state, int output, DeadKeyTable dkt) {
		if (output > 0) {
			out.println(wrap("\t\t\t", state, "output", hex(output,4), dkt == null));
		} else {
			out.println(wrap("\t\t\t", state, dkt == null));
		}
		if (dkt != null) {
			write(out, dkt);
			out.println("\t\t\t</" + state + ">");
		}
	}
	
	private static void write(PrintWriter out, DeadKeyTable dkt) {
		out.println("\t\t\t\t<deadKey>");
		if (dkt.winTerminator > 0) {
			out.println(wrap("\t\t\t\t\t", "winTerminator", "output", hex(dkt.winTerminator,4)));
		}
		if (dkt.macTerminator > 0) {
			out.println(wrap("\t\t\t\t\t", "macTerminator", "output", hex(dkt.macTerminator,4)));
		}
		if (dkt.macStateId != null && dkt.macStateId.length() > 0) {
			out.println(wrap("\t\t\t\t\t", "macStateId", "state", dkt.macStateId));
		}
		if (dkt.xkbOutput > 0) {
			out.println(wrap("\t\t\t\t\t", "xkbOutput", "output", hex(dkt.xkbOutput,4)));
		}
		if (dkt.xkbDeadKey != null && dkt.xkbDeadKey != XkbDeadKey.none) {
			out.println(wrap("\t\t\t\t\t", "xkbDeadKey", "keysym", dkt.xkbDeadKey.name()));
		}
		if (!dkt.keyMap.isEmpty()) {
			out.println("\t\t\t\t\t<deadKeyMap>");
			for (Map.Entry<Integer,Integer> e : dkt.keyMap.entrySet()) {
				out.println(wrap("\t\t\t\t\t\t", "deadKeyEntry", "input", hex(e.getKey(),4), "output", hex(e.getValue(),4)));
			}
			out.println("\t\t\t\t\t</deadKeyMap>");
		}
		out.println("\t\t\t\t</deadKey>");
	}
	
	private static void writeHTMLConfig(PrintWriter out, KeyboardMapping km) {
		boolean wroteHeader = false;
		if (km.htmlTitle != null && km.htmlTitle.length() > 0) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			out.println(wrap("\t\t", "title", km.htmlTitle));
		}
		if (km.htmlStyle != null && km.htmlStyle.length() > 0) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			writeCDATA(out, "\t\t", "style", km.htmlStyle);
		}
		if (km.htmlH1 != null && km.htmlH1.length() > 0) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			out.println(wrap("\t\t", "h1", km.htmlH1));
		}
		if (km.htmlH2 != null && km.htmlH2.length() > 0) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			out.println(wrap("\t\t", "h2", km.htmlH2));
		}
		if (km.htmlBody1 != null && km.htmlBody1.length() > 0) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			writeCDATA(out, "\t\t", "body1", km.htmlBody1);
		}
		if (km.htmlBody2 != null && km.htmlBody2.length() > 0) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			writeCDATA(out, "\t\t", "body2", km.htmlBody2);
		}
		if (km.htmlBody3 != null && km.htmlBody3.length() > 0) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			writeCDATA(out, "\t\t", "body3", km.htmlBody3);
		}
		if (km.htmlBody4 != null && km.htmlBody4.length() > 0) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			writeCDATA(out, "\t\t", "body4", km.htmlBody4);
		}
		if (km.htmlInstall != null && km.htmlInstall.length() > 0) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			writeCDATA(out, "\t\t", "install", km.htmlInstall);
		}
		if (km.htmlSquareChars != null && !km.htmlSquareChars.isEmpty()) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			out.println(wrap("\t\t", "square", "chars", formatRanges(km.htmlSquareChars)));
		}
		if (km.htmlOutlineChars != null && !km.htmlOutlineChars.isEmpty()) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			out.println(wrap("\t\t", "outline", "chars", formatRanges(km.htmlOutlineChars)));
		}
		
		boolean hasTdClasses = (km.htmlTdClasses != null && !km.htmlTdClasses.isEmpty());
		boolean hasSpanClasses = (km.htmlSpanClasses != null && !km.htmlSpanClasses.isEmpty());
		if (hasTdClasses || hasSpanClasses) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			out.println("\t\t<cpClasses>");
			if (hasTdClasses) {
				for (Map.Entry<String,BitSet> e : km.htmlTdClasses.entrySet()) {
					out.println(wrap("\t\t\t", "td", "class", e.getKey(), "chars", formatRanges(e.getValue())));
				}
			}
			if (hasSpanClasses) {
				for (Map.Entry<String,BitSet> e : km.htmlSpanClasses.entrySet()) {
					out.println(wrap("\t\t\t", "span", "class", e.getKey(), "chars", formatRanges(e.getValue())));
				}
			}
			out.println("\t\t</cpClasses>");
		}
		
		if (km.htmlCpLabels != null && !km.htmlCpLabels.isEmpty()) {
			if (!wroteHeader) { out.println("\t<html>"); wroteHeader = true; }
			out.println("\t\t<cpLabels>");
			for (Map.Entry<Integer,String> e : km.htmlCpLabels.entrySet()) {
				out.println(wrap("\t\t\t", "cpLabel", "cp", hex(e.getKey(),4), "label", e.getValue()));
			}
			out.println("\t\t</cpLabels>");
		}
		
		if (wroteHeader) out.println("\t</html>");
	}
	
	private static void writeCDATA(PrintWriter out, String prefix, String tag, String content) {
		out.println(prefix + "<" + tag + "><![CDATA[");
		for (String line : content.split("\r\n|\r|\n")) {
			out.println(prefix + "\t" + line);
		}
		out.println(prefix + "]]></" + tag + ">");
	}
	
	public static String formatRanges(BitSet bs) {
		if (bs == null || bs.isEmpty()) return null;
		int[] lastRange = null;
		List<int[]> ranges = new ArrayList<int[]>();
		for (int i = 0; (i = bs.nextSetBit(i)) >= 0; i++) {
			if (lastRange != null && lastRange[1] == (i - 1)) lastRange[1]++;
			else ranges.add(lastRange = new int[]{i, i});
		}
		boolean first = true;
		StringBuffer sb = new StringBuffer();
		for (int[] range : ranges) {
			if (first) first = false;
			else sb.append(", ");
			sb.append(hex(range[0], 4).toUpperCase());
			if (range[0] != range[1]) {
				sb.append("-");
				sb.append(hex(range[1], 4).toUpperCase());
			}
		}
		return sb.toString();
	}
	
	private static String hex(int v, int n) {
		String h = Integer.toHexString(v);
		while (h.length() < n) h = "0" + h;
		return h;
	}
	
	private static String caps(CapsLockMapping clm, String unshifted, String shifted, String auto) {
		if (clm == null) return auto;
		if (clm == CapsLockMapping.UNSHIFTED) return unshifted;
		if (clm == CapsLockMapping.SHIFTED) return shifted;
		return auto;
	}
	
	private static String wrap(String prefix, String tag, boolean close) {
		return prefix + "<" + tag + (close ? "/>" : ">");
	}
	
	private static String wrap(String prefix, String tag, String s) {
		return prefix + "<" + tag + ">" + xmlEncode(s) + "</" + tag + ">";
	}
	
	private static String wrap(String prefix, String tag, String attr, String s) {
		return prefix + "<" + tag + " " + attr + "=\"" + xmlEncode(s) + "\"/>";
	}
	
	private static String wrap(String prefix, String tag, String attr, String s, boolean close) {
		return prefix + "<" + tag + " " + attr + "=\"" + xmlEncode(s) + "\"" + (close ? "/>" : ">");
	}
	
	private static String wrap(String prefix, String tag, String attr1, String s1, String attr2, String s2) {
		return prefix + "<" + tag + " " + attr1 + "=\"" + xmlEncode(s1) + "\" " + attr2 + "=\"" + xmlEncode(s2) + "\"/>";
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
				sb.append("&#" + ch + ";");
			} else {
				sb.append((char)ch);
			}
			i += Character.charCount(ch);
		}
		return sb.toString();
	}
}
