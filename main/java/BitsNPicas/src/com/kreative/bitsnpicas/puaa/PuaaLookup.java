package com.kreative.bitsnpicas.puaa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class PuaaLookup {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
			return;
		}
		
		List<PuaaSubtable> tables = new ArrayList<PuaaSubtable>();
		List<String> properties = new ArrayList<String>();
		List<Integer> codePoints = new ArrayList<Integer>();
		boolean parsingOptions = true;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parsingOptions && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parsingOptions = false;
				} else if (arg.equals("-i") && argi < args.length) {
					PuaaTable puaa = read(new File(args[argi++]));
					if (puaa != null) tables.addAll(puaa);
				} else if (arg.equals("-p") && argi < args.length) {
					String p = args[argi++].trim().toLowerCase();
					if (p.length() > 0) properties.add(p);
				} else if (arg.equals("-c") && argi < args.length) {
					int cp = parseCodePoint(args[argi++]);
					if (cp >= 0) codePoints.add(cp);
				} else if (arg.equals("--help")) {
					printHelp();
				} else {
					System.err.println("Unknown option: " + arg);
				}
			} else {
				int cp = parseCodePoint(arg);
				if (cp >= 0) codePoints.add(cp);
			}
		}
		
		if (tables.isEmpty()) return;
		
		if (codePoints.isEmpty()) {
			if (properties.isEmpty()) {
				System.out.println("Properties:");
				for (PuaaSubtable table : tables) {
					System.out.println("  " + table.property);
				}
				return;
			}
			
			for (PuaaSubtable table : tables) {
				if (properties.contains(table.property.toLowerCase())) {
					System.out.println(table.property + ":");
					for (PuaaSubtableEntry.Single e : PuaaUtility.createRunsFromEntries(table)) {
						String range = PuaaUtility.joinRange(e);
						StringBuffer sb = new StringBuffer();
						sb.append("  ");
						sb.append(range);
						sb.append(": ");
						for (int i = range.length(); i < 14; i++) sb.append(" ");
						sb.append(e.value);
						System.out.println(sb.toString());
					}
				}
			}
			return;
		}
		
		int nameWidth = 0;
		for (PuaaSubtable table : tables) {
			int w = table.property.length();
			if (w > nameWidth) nameWidth = w;
		}
		
		for (int cp : codePoints) {
			System.out.println("U+" + PuaaUtility.toHexString(cp) + ":");
			for (PuaaSubtable table : tables) {
				if (properties.isEmpty() || properties.contains(table.property.toLowerCase())) {
					String value = table.getPropertyValue(cp);
					if (value == null) continue;
					String prop = table.property;
					StringBuffer sb = new StringBuffer();
					sb.append("  ");
					sb.append(prop);
					sb.append(": ");
					for (int i = prop.length(); i < nameWidth; i++) sb.append(" ");
					sb.append(value);
					System.out.println(sb.toString());
				}
			}
		}
	}
	
	private static PuaaTable read(File src) {
		System.out.print("Reading " + src.getName() + "...");
		try {
			PuaaTable puaa = extract(src);
			if (puaa == null) {
				System.out.println(" ERROR: table not found");
			} else {
				System.out.println(" DONE");
			}
			return puaa;
		} catch (IOException e) {
			System.out.println(" ERROR: " + e);
			return null;
		}
	}
	
	private static int parseCodePoint(String s) {
		if (s.codePointCount(0, s.length()) == 1) {
			return s.codePointAt(0);
		} else try {
			s = s.replaceAll("[Uu][+]|[0][Xx]|\\s", "");
			int cp = Integer.parseInt(s, 16);
			if (Character.isValidCodePoint(cp)) return cp;
			s = Integer.toHexString(cp).toUpperCase();
			System.err.println("Invalid code point: " + s);
			return -1;
		} catch (NumberFormatException nfe) {
			System.err.println("Invalid code point: " + s);
			return -1;
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("PuaaLookup - Look up Unicode Character Database properties in TrueType files.");
		System.out.println();
		System.out.println("  -i <path>     Specify source TrueType file.");
		System.out.println("  -p <prop>     Specify properties to look up.");
		System.out.println("  -c <cp>       Specify code points to look up.");
		System.out.println("  --            Process remaining arguments as code points.");
		System.out.println();
	}
	
	public static PuaaTable extract(File src) throws IOException {
		long length = src.length();
		if (length < 12) throw new IOException("file too small");
		if (length > Integer.MAX_VALUE) throw new IOException("file too large");
		
		byte[] data = new byte[(int)length];
		FileInputStream in = new FileInputStream(src);
		in.read(data);
		in.close();
		
		TrueTypeFile ttf = new TrueTypeFile();
		ttf.decompile(data);
		return (PuaaTable)ttf.getByTableName("PUAA");
	}
}
