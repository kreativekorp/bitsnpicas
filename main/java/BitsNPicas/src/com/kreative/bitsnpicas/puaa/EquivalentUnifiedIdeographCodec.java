package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class EquivalentUnifiedIdeographCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "EquivalentUnifiedIdeograph.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{"Equivalent_Unified_Ideograph"};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		HashMap<Integer,Integer> values = new HashMap<Integer,Integer>();
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 2) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				int v = Integer.parseInt(fields[1].trim(), 16);
				for (int cp = r[0]; cp <= r[1]; cp++) values.put(cp, v);
			} catch (NumberFormatException nfe) {}
		}
		PuaaSubtable st = puaa.getOrCreateSubtable("Equivalent_Unified_Ideograph");
		st.addAll(PuaaUtility.createEntriesFromHexadecimalMap(values));
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable values = puaa.getSubtable("Equivalent_Unified_Ideograph");
		if (values == null || values.isEmpty()) return;
		for (PuaaSubtableEntry.Single e : PuaaUtility.createRunsFromEntries(values)) {
			StringBuffer sb = new StringBuffer();
			sb.append(PuaaUtility.joinRange(e));
			while (sb.length() < 11) sb.append(" ");
			sb.append("; ");
			sb.append(e.value);
			out.println(sb.toString());
		}
	}
}
