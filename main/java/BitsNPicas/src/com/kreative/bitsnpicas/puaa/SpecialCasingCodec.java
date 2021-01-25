package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class SpecialCasingCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "SpecialCasing.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{
			"Lowercase_Mapping",
			"Titlecase_Mapping",
			"Uppercase_Mapping",
		};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		PuaaSubtable lower = puaa.getOrCreateSubtable("Lowercase_Mapping");
		PuaaSubtable title = puaa.getOrCreateSubtable("Titlecase_Mapping");
		PuaaSubtable upper = puaa.getOrCreateSubtable("Uppercase_Mapping");
		PuaaSubtableEntry.CaseMapping e;
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 4) continue;
			if ((e = createEntry(fields, 1)) != null) lower.add(e);
			if ((e = createEntry(fields, 2)) != null) title.add(e);
			if ((e = createEntry(fields, 3)) != null) upper.add(e);
		}
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable lower = puaa.getSubtable("Lowercase_Mapping");
		PuaaSubtable title = puaa.getSubtable("Titlecase_Mapping");
		PuaaSubtable upper = puaa.getSubtable("Uppercase_Mapping");
		LinkedHashMap<String,String[]> lines = new LinkedHashMap<String,String[]>();
		if (lower != null) for (PuaaSubtableEntry e : lower) addLine(lines, e, 1);
		if (title != null) for (PuaaSubtableEntry e : title) addLine(lines, e, 2);
		if (upper != null) for (PuaaSubtableEntry e : upper) addLine(lines, e, 3);
		for (String[] line : lines.values()) out.println(joinLine(line));
	}
	
	private static PuaaSubtableEntry.CaseMapping createEntry(String[] fields, int i) {
		try {
			PuaaSubtableEntry.CaseMapping e = new PuaaSubtableEntry.CaseMapping();
			
			int[] r = PuaaUtility.splitRange(fields[0]);
			e.firstCodePoint = r[0];
			e.lastCodePoint = r[1];
			
			String[] words = fields[i].trim().split("\\s+");
			e.values = new int[words.length];
			for (i = 0; i < words.length; i++) {
				e.values[i] = Integer.parseInt(words[i], 16);
			}
			
			if (fields.length > 4) {
				String c = fields[4].trim();
				if (c.length() > 0) e.condition = c;
			}
			
			return e;
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
	
	private static void addLine(LinkedHashMap<String,String[]> lines, PuaaSubtableEntry e, int i) {
		for (int cp = e.firstCodePoint; cp <= e.lastCodePoint; cp++) {
			String key = Integer.toHexString(0xC0000000 + cp);
			String value = e.getPropertyValue(cp);
			String condition = null;
			
			int o = value.indexOf(";");
			if (o >= 0) {
				condition = value.substring(o + 1).trim();
				value = value.substring(0, o).trim();
				key += condition;
			}
			
			if (lines.containsKey(key)) {
				String[] line = lines.get(key);
				line[i] = value;
			} else {
				String[] line = new String[5];
				line[0] = PuaaUtility.toHexString(cp);
				line[i] = value;
				line[4] = condition;
				lines.put(key, line);
			}
		}
	}
	
	private static String joinLine(String[] line) {
		StringBuffer sb = new StringBuffer();
		if (line[0] != null) sb.append(line[0]);
		sb.append("; ");
		if (line[1] != null) sb.append(line[1]);
		sb.append("; ");
		if (line[2] != null) sb.append(line[2]);
		sb.append("; ");
		if (line[3] != null) sb.append(line[3]);
		sb.append(";");
		if (line[4] != null && line[4].length() > 0) {
			sb.append(" ");
			sb.append(line[4]);
			sb.append(";");
		}
		return sb.toString();
	}
}
