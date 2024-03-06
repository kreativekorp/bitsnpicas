package com.kreative.keyedit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class KeyManWriterUtility {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	private KeyManWriterUtility() {}
	
	public static Scanner getTemplate(String name) {
		return new Scanner(KeyManWriterUtility.class.getResourceAsStream(name), "UTF-8");
	}
	
	private static Pattern FIELD_PATTERN = Pattern.compile("[{][{]([^}]+)[}][}]");
	public static String replaceFields(String s, KeyboardMapping km) {
		StringBuffer sb = new StringBuffer();
		Matcher m = FIELD_PATTERN.matcher(s);
		while (m.find()) {
			String field = m.group(1).trim();
			String repl = m.group();
			if (field.equalsIgnoreCase("id")) repl = km.getKeymanIdentifierNotEmpty();
			if (field.equalsIgnoreCase("name")) repl = km.getKeymanNameNotEmpty();
			if (field.equalsIgnoreCase("copyright")) repl = km.getKeymanCopyrightNotEmpty();
			if (field.equalsIgnoreCase("message")) repl = notNull(km.keymanMessage);
			if (field.equalsIgnoreCase("webhelptext")) repl = notNull(km.keymanWebHelpText);
			if (field.equalsIgnoreCase("version")) repl = km.getKeymanVersionNotEmpty();
			if (field.equalsIgnoreCase("comments")) repl = notNull(km.keymanComments);
			if (field.equalsIgnoreCase("author")) repl = km.getKeymanAuthorNotEmpty();
			if (field.equalsIgnoreCase("emailaddress")) repl = notNull(km.keymanEmailAddress);
			if (field.equalsIgnoreCase("website")) repl = notNull(km.keymanEmailAddress);
			if (field.equalsIgnoreCase("description")) repl = km.getKeymanDescriptionNotEmpty();
			if (field.equalsIgnoreCase("date")) repl = DATE_FORMAT.format(new Date());
			if (field.equalsIgnoreCase("targets")) {
				StringBuffer tsb = new StringBuffer();
				addTarget(km, KeyManTarget.WINDOWS, tsb, "Windows");
				addTarget(km, KeyManTarget.MACOSX, tsb, "Mac OS X"); // No, I will not use Apple's new stupid capitalization.
				addTarget(km, KeyManTarget.LINUX, tsb, "Linux");
				addTarget(km, KeyManTarget.WEB, tsb, "Web");
				addTarget(km, KeyManTarget.IPHONE, tsb, "iPhone");
				addTarget(km, KeyManTarget.IPAD, tsb, "iPad");
				addTarget(km, KeyManTarget.ANDROID_PHONE, tsb, "Android phone");
				addTarget(km, KeyManTarget.ANDROID_TABLET, tsb, "Android tablet");
				addTarget(km, KeyManTarget.MOBILE, tsb, "Mobile devices");
				addTarget(km, KeyManTarget.DESKTOP, tsb, "Desktop devices");
				addTarget(km, KeyManTarget.TABLET, tsb, "Tablet devices");
				repl = tsb.toString();
			}
			m.appendReplacement(sb, repl);
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	private static String notNull(String s) {
		return (s != null) ? s : "";
	}
	
	private static void addTarget(KeyboardMapping km, KeyManTarget t, StringBuffer sb, String s) {
		if (km.keymanTargets != null) {
			if (km.keymanTargets.contains(KeyManTarget.ANY) || km.keymanTargets.contains(t)) {
				sb.append(" * " + s + "\n");
			}
		}
	}
}
