package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class BidiBracketsCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "BidiBrackets.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{
			"Bidi_Paired_Bracket",
			"Bidi_Paired_Bracket_Type",
		};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		HashMap<Integer,Integer> values = new HashMap<Integer,Integer>();
		HashMap<Integer,String> types = new HashMap<Integer,String>();
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 3) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				int v = Integer.parseInt(fields[1].trim(), 16);
				String t = fields[2].trim();
				for (int cp = r[0]; cp <= r[1]; cp++) {
					values.put(cp, v);
					types.put(cp, t);
				}
			} catch (NumberFormatException nfe) {}
		}
		PuaaSubtable st = puaa.getOrCreateSubtable("Bidi_Paired_Bracket");
		st.addAll(PuaaUtility.createEntriesFromHexadecimalMap(values));
		st = puaa.getOrCreateSubtable("Bidi_Paired_Bracket_Type");
		st.addAll(PuaaUtility.createEntriesFromStringMap(types));
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable values = puaa.getSubtable("Bidi_Paired_Bracket");
		PuaaSubtable types = puaa.getSubtable("Bidi_Paired_Bracket_Type");
		TreeMap<Integer,String[]> lines = new TreeMap<Integer,String[]>();
		if (values != null) for (PuaaSubtableEntry e : values) addLine(lines, e, 1);
		if (types != null) for (PuaaSubtableEntry e : types) addLine(lines, e, 2);
		for (String[] line : lines.values()) out.println(PuaaUtility.joinLine(line, "; "));
	}
	
	private static void addLine(TreeMap<Integer,String[]> lines, PuaaSubtableEntry e, int i) {
		for (int cp = e.firstCodePoint; cp <= e.lastCodePoint; cp++) {
			String value = e.getPropertyValue(cp);
			if (value == null || value.length() == 0) continue;
			if (lines.containsKey(cp)) {
				String[] line = lines.get(cp);
				if (line[i] == null) line[i] = value;
				else line[i] += value;
			} else {
				String[] line = new String[3];
				line[0] = PuaaUtility.toHexString(cp);
				line[i] = value;
				lines.put(cp, line);
			}
		}
	}
}
