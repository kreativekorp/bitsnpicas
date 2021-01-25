package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class ScriptExtensionsCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "ScriptExtensions.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{"Script_Extensions"};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		Map<String,Map<Integer,String>> values = new TreeMap<String,Map<Integer,String>>();
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 2) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				for (String s : fields[1].trim().split("\\s+")) {
					if (values.containsKey(s)) {
						Map<Integer,String> m = values.get(s);
						for (int cp = r[0]; cp <= r[1]; cp++) m.put(cp, s);
					} else {
						Map<Integer,String> m = new TreeMap<Integer,String>();
						for (int cp = r[0]; cp <= r[1]; cp++) m.put(cp, s);
						values.put(s, m);
					}
				}
			} catch (NumberFormatException nfe) {}
		}
		PuaaSubtable st = puaa.getOrCreateSubtable("Script_Extensions");
		for (Map<Integer,String> m : values.values()) {
			st.addAll(PuaaUtility.createEntriesFromStringMap(m));
		}
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable entries = puaa.getSubtable("Script_Extensions");
		if (entries == null || entries.isEmpty()) return;
		
		Map<Integer,Set<String>> scripts = new TreeMap<Integer,Set<String>>();
		for (PuaaSubtableEntry e : entries) {
			for (int cp = e.firstCodePoint; cp <= e.lastCodePoint; cp++) {
				if (scripts.containsKey(cp)) {
					Set<String> scr = scripts.get(cp);
					for (String s : e.getPropertyValue(cp).trim().split("\\s+")) scr.add(s);
				} else {
					Set<String> scr = new TreeSet<String>();
					for (String s : e.getPropertyValue(cp).trim().split("\\s+")) scr.add(s);
					scripts.put(cp, scr);
				}
			}
		}
		
		List<PuaaSubtableEntry.Single> runs = new ArrayList<PuaaSubtableEntry.Single>();
		for (Map.Entry<Integer,Set<String>> e : scripts.entrySet()) {
			StringBuffer sb = new StringBuffer();
			boolean first = true;
			for (String s : e.getValue()) {
				if (first) first = false;
				else sb.append(" ");
				sb.append(s);
			}
			PuaaSubtableEntry.Single s = new PuaaSubtableEntry.Single();
			s.firstCodePoint = s.lastCodePoint = e.getKey();
			s.value = sb.toString();
			runs.add(s);
		}
		runs = PuaaUtility.createRunsFromEntries(runs);
		Collections.sort(runs, BY_SCRIPTS);
		
		for (PuaaSubtableEntry.Single e : runs) {
			StringBuffer sb = new StringBuffer();
			sb.append(PuaaUtility.joinRange(e));
			while (sb.length() < 14) sb.append(" ");
			sb.append("; ");
			sb.append(e.value);
			out.println(sb.toString());
		}
	}
	
	private static final Comparator<PuaaSubtableEntry.Single> BY_SCRIPTS = new Comparator<PuaaSubtableEntry.Single>() {
		@Override
		public int compare(PuaaSubtableEntry.Single a, PuaaSubtableEntry.Single b) {
			if (a.value.length() != b.value.length()) return a.value.length() - b.value.length();
			int cmp = a.value.compareToIgnoreCase(b.value); if (cmp != 0) return cmp;
			if (a.firstCodePoint != b.firstCodePoint) return a.firstCodePoint - b.firstCodePoint;
			if (a.lastCodePoint != b.lastCodePoint) return a.lastCodePoint - b.lastCodePoint;
			return 0;
		}
	};
}
