package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public abstract class AbstractPropListCodec extends PuaaCodec {
	protected final String fileName;
	protected final List<String> propNames;
	
	protected AbstractPropListCodec(String fileName, List<String> propNames) {
		this.fileName = fileName;
		this.propNames = propNames;
	}
	
	@Override
	public String getFileName() {
		return fileName;
	}
	
	@Override
	public String[] getPropertyNames() {
		return propNames.toArray(new String[propNames.size()]);
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		Map<String,Map<Integer,Boolean>> props = new HashMap<String,Map<Integer,Boolean>>();
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 2) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				String prop = fields[1].trim();
				if (props.containsKey(prop)) {
					Map<Integer,Boolean> p = props.get(prop);
					for (int cp = r[0]; cp <= r[1]; cp++) p.put(cp, true);
				} else {
					Map<Integer,Boolean> p = new HashMap<Integer,Boolean>();
					for (int cp = r[0]; cp <= r[1]; cp++) p.put(cp, true);
					props.put(prop, p);
				}
			} catch (NumberFormatException nfe) {}
		}
		for (Map.Entry<String,Map<Integer,Boolean>> e : props.entrySet()) {
			PuaaSubtable st = puaa.getOrCreateSubtable(e.getKey());
			st.addAll(PuaaUtility.createEntriesFromBooleanMap(e.getValue()));
		}
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		for (String prop : propNames) {
			PuaaSubtable props = puaa.getSubtable(prop);
			if (props == null || props.isEmpty()) continue;
			for (PuaaSubtableEntry.Single e : PuaaUtility.createRunsFromEntries(props)) {
				if ("Y".equalsIgnoreCase(e.value)) {
					StringBuffer sb = new StringBuffer();
					sb.append(PuaaUtility.joinRange(e));
					while (sb.length() < 14) sb.append(" ");
					sb.append("; ");
					sb.append(prop);
					out.println(sb.toString());
				}
			}
		}
	}
}
