package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class ArabicShapingCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "ArabicShaping.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{
			"Joining_Type",
			"Joining_Group",
		};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		HashMap<Integer,String> types = new HashMap<Integer,String>();
		HashMap<Integer,String> groups = new HashMap<Integer,String>();
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 4) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				String t = fields[2].trim();
				String g = fields[3].trim();
				for (int cp = r[0]; cp <= r[1]; cp++) {
					types.put(cp, t);
					groups.put(cp, g);
				}
			} catch (NumberFormatException nfe) {}
		}
		PuaaSubtable st = puaa.getOrCreateSubtable("Joining_Type");
		st.addAll(PuaaUtility.createEntriesFromStringMap(types));
		st = puaa.getOrCreateSubtable("Joining_Group");
		st.addAll(PuaaUtility.createEntriesFromNameMap(groups));
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable names = puaa.getSubtable("Name");
		PuaaSubtable types = puaa.getSubtable("Joining_Type");
		PuaaSubtable groups = puaa.getSubtable("Joining_Group");
		TreeMap<Integer,String[]> lines = new TreeMap<Integer,String[]>();
		if (types != null) for (PuaaSubtableEntry e : types) addLine(names, lines, e, 2);
		if (groups != null) for (PuaaSubtableEntry e : groups) addLine(names, lines, e, 3);
		for (String[] line : lines.values()) out.println(PuaaUtility.joinLine(line, "; "));
	}
	
	private static void addLine(PuaaSubtable names, TreeMap<Integer,String[]> lines, PuaaSubtableEntry e, int i) {
		for (int cp = e.firstCodePoint; cp <= e.lastCodePoint; cp++) {
			String value = e.getPropertyValue(cp);
			if (value == null || value.length() == 0) continue;
			if (lines.containsKey(cp)) {
				String[] line = lines.get(cp);
				if (line[i] == null) line[i] = value;
				else line[i] += value;
			} else {
				String[] line = new String[4];
				line[0] = PuaaUtility.toHexString(cp);
				if (names != null) line[1] = names.getPropertyValue(cp);
				line[i] = value;
				lines.put(cp, line);
			}
		}
	}
}
