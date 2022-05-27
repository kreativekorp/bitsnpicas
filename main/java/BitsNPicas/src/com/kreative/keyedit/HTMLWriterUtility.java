package com.kreative.keyedit;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HTMLWriterUtility {
	private HTMLWriterUtility() {}
	
	public static Scanner getTemplate(String name) {
		return new Scanner(HTMLWriterUtility.class.getResourceAsStream(name), "UTF-8");
	}
	
	private static Pattern FIELD_PATTERN = Pattern.compile("[{][{]([^}]+)[}][}]");
	public static String replaceFields(String s, KeyboardMapping km) {
		StringBuffer sb = new StringBuffer();
		Matcher m = FIELD_PATTERN.matcher(s);
		while (m.find()) {
			String field = m.group(1).trim();
			String repl = m.group();
			if (field.equalsIgnoreCase("name")) repl = htmlSpecialChars(km.getNameNotEmpty());
			if (field.equalsIgnoreCase("shortname")) repl = htmlSpecialChars(km.getWinIdentifierNotEmpty());
			if (field.equalsIgnoreCase("copyright")) repl = htmlSpecialChars(km.getWinCopyrightNotEmpty());
			if (field.equalsIgnoreCase("company")) repl = htmlSpecialChars(km.getWinCompanyNotEmpty());
			if (field.equalsIgnoreCase("lang")) repl = htmlSpecialChars(km.getWinLocaleNotNull().tag);
			if (field.equalsIgnoreCase("locale")) repl = htmlSpecialChars(km.getWinLocaleNotNull().name);
			if (field.equalsIgnoreCase("path")) repl = htmlSpecialChars(km.getXkbPathNotEmpty());
			if (field.equalsIgnoreCase("label")) repl = htmlSpecialChars(km.getXkbLabelNotEmpty());
			if (field.equalsIgnoreCase("comment")) repl = htmlSpecialChars(km.xkbComment);
			m.appendReplacement(sb, repl);
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	public static String htmlSpecialChars(String s) {
		if (s == null) return "";
		StringBuffer sb = new StringBuffer();
		for (char ch : s.toCharArray()) {
			switch (ch) {
				case '&': sb.append("&amp;"); break;
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				case '"': sb.append("&quot;"); break;
				case '\'': sb.append("&#39;"); break;
				case '\n': sb.append("<br>"); break;
				default: sb.append(ch);
			}
		}
		return sb.toString();
	}
}
