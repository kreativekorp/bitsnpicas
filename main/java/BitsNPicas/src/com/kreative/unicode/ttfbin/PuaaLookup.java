package com.kreative.unicode.ttfbin;

import java.awt.Font;
import java.awt.font.OpenType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.unicode.ttflib.FindOpenType;
import com.kreative.unicode.ttflib.PuaaEntry;
import com.kreative.unicode.ttflib.PuaaTable;
import com.kreative.unicode.ttflib.TtfFile;

public class PuaaLookup {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		if (args.length == 0) {
			printHelp();
			return;
		}
		
		PuaaTable table = null;
		List<String> properties = new ArrayList<String>();
		List<Integer> codePoints = new ArrayList<Integer>();
		boolean parsingOptions = true;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parsingOptions && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parsingOptions = false;
				} else if (arg.equals("-f") && argi < args.length) {
					PuaaTable t = readFromFont(args[argi++]);
					if (t != null) table = t;
				} else if (arg.equals("-i") && argi < args.length) {
					PuaaTable t = readFromFile(args[argi++]);
					if (t != null) table = t;
				} else if (arg.equals("-p") && argi < args.length) {
					properties.add(args[argi++]);
				} else if (arg.equals("-c") && argi < args.length) {
					codePoints.add(parseCP(args[argi++]));
				} else if (arg.equals("--help")) {
					printHelp();
				} else {
					System.out.println("Unknown option: " + arg);
				}
			} else {
				codePoints.add(parseCP(arg));
			}
		}
		
		if (table == null) return;
		printChars(table, properties, codePoints);
	}
	
	private static PuaaTable readFromFont(String arg) {
		try {
			Font font = new Font(arg, 0, 1);
			OpenType ot = FindOpenType.forFont(font);
			if (ot != null) {
				byte[] d = ot.getFontTable("PUAA");
				if (d != null) return new PuaaTable(d);
				System.out.println("Error: Table not found.");
				return null;
			}
			System.out.println("Error: Not an OpenType font.");
			return null;
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return null;
		}
	}
	
	private static PuaaTable readFromFile(String arg) {
		try {
			File file = new File(arg);
			TtfFile ttf = new TtfFile(file);
			PuaaTable t = ttf.getTableAs(PuaaTable.class, "PUAA");
			if (t != null) return t;
			System.out.println("Error: Table not found.");
			return null;
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return null;
		}
	}
	
	private static void printChars(PuaaTable t, List<String> p, List<Integer> c) {
		if (c == null || c.isEmpty()) {
			printProps(t, p);
			return;
		}
		
		int nameWidth = 0;
		for (String prop : t.getProperties()) {
			if (prop.length() > nameWidth) nameWidth = prop.length();
		}
		nameWidth += 4;
		
		Map<String,Map<Integer,String>> tables = new HashMap<String,Map<Integer,String>>();
		for (String prop : t.getProperties()) tables.put(prop, t.getPropertyMap(prop));
		
		for (int cp : c) {
			System.out.println("U+" + toHexString(cp) + ":");
			for (String prop : t.getProperties()) {
				if (p.isEmpty() || p.contains(prop)) {
					if (tables.get(prop).containsKey(cp)) {
						StringBuffer sb = new StringBuffer("  ");
						sb.append(prop);
						sb.append(":");
						while (sb.length() < nameWidth) sb.append(" ");
						sb.append(tables.get(prop).get(cp));
						System.out.println(sb.toString());
					}
				}
			}
		}
	}
	
	private static void printProps(PuaaTable t, List<String> p) {
		if (p == null || p.isEmpty()) {
			printTOC(t);
			return;
		}
		for (String prop : t.getProperties()) {
			if (p.contains(prop)) {
				System.out.println(prop + ":");
				for (PuaaEntry e : t.getPropertyRuns(prop)) {
					StringBuffer sb = new StringBuffer("  ");
					sb.append(toHexString(e.getFirstCodePoint()));
					if (e.getFirstCodePoint() != e.getLastCodePoint()) {
						sb.append("..");
						sb.append(toHexString(e.getLastCodePoint()));
					}
					sb.append(":");
					while (sb.length() < 18) sb.append(" ");
					sb.append(e.getPropertyString(e.getFirstCodePoint()));
					System.out.println(sb.toString());
				}
			}
		}
	}
	
	private static void printTOC(PuaaTable t) {
		System.out.println("Properties:");
		for (String prop : t.getProperties()) {
			System.out.println("  " + prop);
		}
	}
	
	private static String toHexString(int v) {
		String s = Integer.toHexString(v).toUpperCase();
		if (s.length() < 4) s = ("0000" + s).substring(s.length());
		return s;
	}
	
	private static int parseCP(String s) {
		s = s.replaceAll("[Uu][+]|[0][Xx]|\\s+", "");
		return Integer.parseInt(s, 16);
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("PuaaLookup - Look up Unicode Character Database properties in TrueType files.");
		System.out.println();
		System.out.println("  -f <name>     Specify source TrueType font name.");
		System.out.println("  -i <path>     Specify source TrueType file.");
		System.out.println("  -p <prop>     Specify properties to look up.");
		System.out.println("  -c <cp>       Specify code points to look up.");
		System.out.println("  --            Process remaining arguments as code points.");
		System.out.println();
	}
}
