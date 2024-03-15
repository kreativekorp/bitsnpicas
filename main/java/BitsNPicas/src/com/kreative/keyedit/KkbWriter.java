package com.kreative.keyedit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
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
		writeBlock(out, "\t", "xkbComment", km.xkbComment);
		out.println(wrap("\t", "xkbUseKeySym", "for", (km.xkbUseKeySym ? "unicode" : "ascii")));
		
		if (km.xkbAltGrKey != null && km.xkbAltGrKey != XkbAltGrKey.none) {
			String include = "level3(" + km.xkbAltGrKey.name() + ")";
			out.println(wrap("\t", "xkbAltGrKey", "include", include));
		}
		
		if (km.xkbComposeKey != null && km.xkbComposeKey != XkbComposeKey.none) {
			String include = "compose(" + km.xkbComposeKey.name() + ")";
			out.println(wrap("\t", "xkbComposeKey", "include", include));
		}
		
		writeKeymanConfig(out, km);
		
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
		writeAttachments(out, "winAttachments", km.winAttachments);
		writeAttachments(out, "macAttachments", km.macAttachments);
		writeAttachments(out, "xkbAttachments", km.xkbAttachments);
		
		out.println("</keyboardMapping>");
	}
	
	private static void write(PrintWriter out, Key k, KeyMapping km) {
		out.println(wrap("\t\t", "keyMapping", "key", k.name().toLowerCase(), false));
		write(out, "unshifted", km.unshiftedOutput, km.unshiftedDeadKey, km.unshiftedLongPressOutput);
		write(out, "shifted", km.shiftedOutput, km.shiftedDeadKey, km.shiftedLongPressOutput);
		out.println(wrap("\t\t\t", "capsLock", "mapsTo", caps(km.capsLockMapping, "unshifted", "shifted", "auto")));
		write(out, "altUnshifted", km.altUnshiftedOutput, km.altUnshiftedDeadKey, km.altUnshiftedLongPressOutput);
		write(out, "altShifted", km.altShiftedOutput, km.altShiftedDeadKey, km.altShiftedLongPressOutput);
		out.println(wrap("\t\t\t", "altCapsLock", "mapsTo", caps(km.altCapsLockMapping, "altUnshifted", "altShifted", "auto")));
		write(out, "ctrl", km.ctrlOutput, km.ctrlDeadKey, null);
		write(out, "command", km.commandOutput, km.commandDeadKey, null);
		out.println("\t\t</keyMapping>");
	}
	
	private static void write(PrintWriter out, String state, int output, DeadKeyTable dkt, int[] lpo) {
		if (output > 0) {
			out.println(wrap("\t\t\t", state, "output", hex(output,4), dkt == null && lpo == null));
		} else {
			out.println(wrap("\t\t\t", state, dkt == null && lpo == null));
		}
		if (dkt != null || lpo != null) {
			if (dkt != null) write(out, dkt);
			if (lpo != null) write(out, lpo);
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
	
	private static void write(PrintWriter out, int[] lpo) {
		out.println("\t\t\t\t<longPressOutput>");
		for (int output : lpo) {
			out.println(wrap("\t\t\t\t\t", "longPressEntry", "output", hex(output,4)));
		}
		out.println("\t\t\t\t</longPressOutput>");
	}
	
	private static void writeKeymanConfig(PrintWriter out, KeyboardMapping km) {
		if (!any(
			km.keymanIdentifier, km.keymanName, km.keymanCopyright, km.keymanMessage,
			km.keymanWebHelpText, km.keymanVersion, km.keymanComments, km.keymanAuthor,
			km.keymanEmailAddress, km.keymanWebSite, km.keymanRightToLeft, km.keymanKey102,
			km.keymanDisplayUnderlying, km.keymanUseAltGr, km.keymanTargets, km.keymanPlatforms,
			km.keymanLanguages, km.keymanAttachments, km.keymanFileIds, km.keymanCpLabels,
			km.keymanFontFamily, km.keymanOSKFontFile, km.keymanDisplayFontFile, km.keymanDescription,
			km.keymanLicenseType, km.keymanLicenseText, km.keymanReadme, km.keymanHistory
		)) return;
		
		out.println(wrap("\t", "keymanIdentifier", km.keymanIdentifier));
		out.println(wrap("\t", "keymanName", km.keymanName));
		out.println(wrap("\t", "keymanCopyright", km.keymanCopyright));
		out.println(wrap("\t", "keymanMessage", km.keymanMessage));
		out.println(wrap("\t", "keymanWebHelpText", km.keymanWebHelpText));
		out.println(wrap("\t", "keymanVersion", km.keymanVersion));
		writeBlock(out, "\t", "keymanComments", km.keymanComments);
		out.println(wrap("\t", "keymanAuthor", km.keymanAuthor));
		out.println(wrap("\t", "keymanEmailAddress", km.keymanEmailAddress));
		out.println(wrap("\t", "keymanWebSite", km.keymanWebSite));
		out.println(wrap("\t", "keymanRightToLeft", "value", (km.keymanRightToLeft ? "true" : "false")));
		out.println(wrap("\t", "keymanKey102", "value", (km.keymanKey102 ? "true" : "false")));
		out.println(wrap("\t", "keymanDisplayUnderlying", "value", (km.keymanDisplayUnderlying ? "true" : "false")));
		out.println(wrap("\t", "keymanUseAltGr", "value", (km.keymanUseAltGr ? "true" : "false")));
		out.println(wrap("\t", "keymanIgnoreCaps", "value", (km.keymanIgnoreCaps ? "true" : "false")));
		
		out.print("\t<keymanTargets");
		for (KeyManTarget t : KeyManTarget.values()) {
			if (km.keymanTargets.contains(t)) {
				out.print(" " + t + "=\"true\"");
			}
		}
		out.println("/>");
		
		out.print("\t<keymanPlatforms");
		for (KeyManPlatform p : KeyManPlatform.values()) {
			if (km.keymanPlatforms.contains(p)) {
				out.print(" " + p + "=\"true\"");
			}
		}
		out.println("/>");
		
		out.println("\t<keymanLanguages>");
		for (Map.Entry<String,String> e : km.keymanLanguages.entrySet()) {
			out.println(wrap("\t\t", "keymanLanguage", "tag", e.getKey(), "name", e.getValue()));
		}
		out.println("\t</keymanLanguages>");
		
		writeAttachments(out, "keymanAttachments", km.keymanAttachments);
		
		if (km.keymanFileIds != null && !km.keymanFileIds.isEmpty()) {
			out.println("\t<keymanFileIds>");
			for (Map.Entry<String,String> e : km.keymanFileIds.entrySet()) {
				out.println(wrap("\t\t", "fileId", "name", e.getKey(), "id", e.getValue()));
			}
			out.println("\t</keymanFileIds>");
		}
		
		if (km.keymanCpLabels != null && !km.keymanCpLabels.isEmpty()) {
			out.println("\t<keymanCpLabels>");
			for (Map.Entry<Integer,String> e : km.keymanCpLabels.entrySet()) {
				out.println(wrap("\t\t", "cpLabel", "cp", hex(e.getKey(),4), "label", e.getValue()));
			}
			out.println("\t</keymanCpLabels>");
		}
		
		if (km.keymanFontFamily != null && km.keymanFontFamily.length() > 0) {
			out.println(wrap("\t", "keymanFont", "family", km.keymanFontFamily));
		}
		if (km.keymanOSKFontFile != null && km.keymanOSKFontFile.length() > 0) {
			out.println(wrap("\t", "keymanOSKFont", "file", km.keymanOSKFontFile));
		}
		if (km.keymanDisplayFontFile != null && km.keymanDisplayFontFile.length() > 0) {
			out.println(wrap("\t", "keymanDisplayFont", "file", km.keymanDisplayFontFile));
		}
		
		writeBlock(out, "\t", "keymanDescription", km.keymanDescription);
		writeBlock(out, "\t", "keymanLicense", "type", km.keymanLicenseType, km.keymanLicenseText);
		writeBlock(out, "\t", "keymanReadme", km.keymanReadme);
		writeBlock(out, "\t", "keymanHistory", km.keymanHistory);
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
	
	private static void writeAttachments(PrintWriter out, String tag, Map<String,byte[]> attachments) {
		if (attachments != null && !attachments.isEmpty()) {
			out.println("\t<" + tag + ">");
			for (Map.Entry<String,byte[]> e : attachments.entrySet()) {
				try {
					StringBuffer sb = new StringBuffer();
					Base64OutputStream b64 = new Base64OutputStream(sb);
					b64.write(e.getValue());
					b64.flush();
					b64.close();
					out.println(
						wrap("\t\t", "attachment", "name", e.getKey(), false) +
						sb.toString() + "</attachment>"
					);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			out.println("\t</" + tag + ">");
		}
	}
	
	private static void writeCDATA(PrintWriter out, String prefix, String tag, String content) {
		out.println(prefix + "<" + tag + "><![CDATA[");
		for (String line : content.split("\r\n|\r|\n")) {
			out.println(prefix + "\t" + line);
		}
		out.println(prefix + "]]></" + tag + ">");
	}
	
	private static void writeBlock(PrintWriter out, String prefix, String tag, String content) {
		if (content != null && content.length() > 0) {
			out.println(prefix + "<" + tag + ">");
			for (String line : content.split("\r\n|\r|\n")) {
				out.println(prefix + "\t" + xmlEncode(line));
			}
			out.println(prefix + "</" + tag + ">");
		}
	}
	
	private static void writeBlock(PrintWriter out, String prefix, String tag, String attr, String value, String content) {
		if (content != null && content.length() > 0) {
			out.println(wrap(prefix, tag, attr, value, false));
			for (String line : content.split("\r\n|\r|\n")) {
				out.println(prefix + "\t" + xmlEncode(line));
			}
			out.println(prefix + "</" + tag + ">");
		}
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
	
	private static boolean any(Object... objects) {
		for (Object o : objects) {
			if (o == null) continue;
			else if (o instanceof String) { if (((String)o).length() > 0) return true; }
			else if (o instanceof Boolean) { if (((Boolean)o).booleanValue()) return true; }
			else if (o instanceof Collection) { if (((Collection<?>)o).size() > 0) return true; }
			else if (o instanceof Map) { if (((Map<?,?>)o).size() > 0) return true; }
			else throw new IllegalArgumentException();
		}
		return false;
	}
}
