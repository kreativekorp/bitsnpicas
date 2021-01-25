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

public class DerivedAgeCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "DerivedAge.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{"Age"};
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
		PuaaSubtable st = puaa.getOrCreateSubtable("Age");
		st.addAll(PuaaUtility.createEntriesFromStringMap(values));
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable values = puaa.getSubtable("Age");
		if (values == null || values.isEmpty()) return;
		List<PuaaSubtableEntry.Single> runs = PuaaUtility.createRunsFromEntries(values);
		Collections.sort(runs, BY_AGE);
		for (PuaaSubtableEntry.Single e : runs) {
			StringBuffer sb = new StringBuffer();
			sb.append(PuaaUtility.joinRange(e));
			while (sb.length() < 14) sb.append(" ");
			sb.append("; ");
			sb.append(e.value);
			out.println(sb.toString());
		}
	}
	
	private static final Comparator<PuaaSubtableEntry.Single> BY_AGE = new Comparator<PuaaSubtableEntry.Single>() {
		@Override
		public int compare(PuaaSubtableEntry.Single a, PuaaSubtableEntry.Single b) {
			int cmp = PuaaUtility.naturalCompare(a.value, b.value); if (cmp != 0) return cmp;
			if (a.firstCodePoint != b.firstCodePoint) return a.firstCodePoint - b.firstCodePoint;
			if (a.lastCodePoint != b.lastCodePoint) return a.lastCodePoint - b.lastCodePoint;
			return 0;
		}
	};
}
