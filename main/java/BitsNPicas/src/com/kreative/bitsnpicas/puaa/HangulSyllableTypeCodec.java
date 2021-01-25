package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class HangulSyllableTypeCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "HangulSyllableType.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{"Hangul_Syllable_Type"};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		HashMap<Integer,String> values = new HashMap<Integer,String>();
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 2) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				String v = fields[1].trim();
				for (int cp = r[0]; cp <= r[1]; cp++) values.put(cp, v);
			} catch (NumberFormatException nfe) {}
		}
		PuaaSubtable st = puaa.getOrCreateSubtable("Hangul_Syllable_Type");
		st.addAll(PuaaUtility.createEntriesFromStringMap(values));
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable values = puaa.getSubtable("Hangul_Syllable_Type");
		if (values == null || values.isEmpty()) return;
		List<PuaaSubtableEntry.Single> runs = PuaaUtility.createRunsFromEntries(values);
		
		// Find the first code point of each type.
		final HashMap<String,Integer> firstOfType = new HashMap<String,Integer>();
		for (PuaaSubtableEntry.Single e : runs) {
			if (firstOfType.containsKey(e.value)) {
				if (firstOfType.get(e.value) < e.firstCodePoint) {
					continue;
				}
			}
			firstOfType.put(e.value, e.firstCodePoint);
		}
		
		// Sort by the first code point of the type.
		Collections.sort(runs, new Comparator<PuaaSubtableEntry.Single>() {
			@Override
			public int compare(PuaaSubtableEntry.Single a, PuaaSubtableEntry.Single b) {
				int foa = firstOfType.get(a.value);
				int fob = firstOfType.get(b.value);
				if (foa != fob) return foa - fob;
				int cmp = a.value.compareTo(b.value);
				if (cmp != 0) return cmp;
				if (a.firstCodePoint != b.firstCodePoint) return a.firstCodePoint - b.firstCodePoint;
				if (a.lastCodePoint != b.lastCodePoint) return a.lastCodePoint - b.lastCodePoint;
				return 0;
			}
		});
		
		for (PuaaSubtableEntry.Single e : runs) {
			StringBuffer sb = new StringBuffer();
			sb.append(PuaaUtility.joinRange(e));
			while (sb.length() < 14) sb.append(" ");
			sb.append("; ");
			sb.append(e.value);
			out.println(sb.toString());
		}
	}
}
