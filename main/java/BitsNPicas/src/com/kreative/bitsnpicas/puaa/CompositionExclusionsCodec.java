package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class CompositionExclusionsCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "CompositionExclusions.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{"Composition_Exclusion"};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		HashMap<Integer,Boolean> values = new HashMap<Integer,Boolean>();
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 1) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				for (int cp = r[0]; cp <= r[1]; cp++) values.put(cp, true);
			} catch (NumberFormatException nfe) {}
		}
		PuaaSubtable st = puaa.getOrCreateSubtable("Composition_Exclusion");
		st.addAll(PuaaUtility.createEntriesFromBooleanMap(values));
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable values = puaa.getSubtable("Composition_Exclusion");
		if (values == null || values.isEmpty()) return;
		for (Map.Entry<Integer,String> e : PuaaUtility.createMapFromEntries(values).entrySet()) {
			if ("Y".equalsIgnoreCase(e.getValue())) {
				out.println(PuaaUtility.toHexString(e.getKey()));
			}
		}
	}
}
