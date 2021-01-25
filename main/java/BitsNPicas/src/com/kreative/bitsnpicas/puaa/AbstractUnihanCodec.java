package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public abstract class AbstractUnihanCodec extends PuaaCodec {
	protected final String fileName;
	protected final List<String> propNames;
	
	protected AbstractUnihanCodec(String fileName, List<String> propNames) {
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
		Map<String,Map<Integer,String>> props = new HashMap<String,Map<Integer,String>>();
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			if (line.length() == 0 || line.startsWith("#")) continue;
			String[] fields = line.split("\\s+", 3);
			if (fields.length < 3) continue;
			try {
				int cp = Integer.parseInt(fields[0].replaceAll("^([Uu][+]|[0][Xx])", ""), 16);
				String prop = fields[1];
				String value = fields[2];
				if (props.containsKey(prop)) {
					Map<Integer,String> p = props.get(prop);
					p.put(cp, value);
				} else {
					Map<Integer,String> p = new HashMap<Integer,String>();
					p.put(cp, value);
					props.put(prop, p);
				}
			} catch (NumberFormatException nfe) {}
		}
		List<PuaaSubtableEntry> elist;
		for (Map.Entry<String,Map<Integer,String>> e : props.entrySet()) {
			PuaaSubtable st = puaa.getOrCreateSubtable(e.getKey());
			if ((elist = toDecimalEntries(e.getValue())) != null) st.addAll(elist);
			else if ((elist = toHexadecimalEntries(e.getValue())) != null) st.addAll(elist);
			else st.addAll(PuaaUtility.createEntriesFromNameMap(e.getValue()));
		}
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		Map<Integer,Map<String,String>> props = new TreeMap<Integer,Map<String,String>>();
		for (String prop : propNames) {
			PuaaSubtable st = puaa.getSubtable(prop);
			if (st == null || st.isEmpty()) continue;
			for (Map.Entry<Integer,String> e : PuaaUtility.createMapFromEntries(st).entrySet()) {
				if (props.containsKey(e.getKey())) {
					Map<String,String> p = props.get(e.getKey());
					p.put(prop, e.getValue());
				} else {
					Map<String,String> p = new TreeMap<String,String>();
					p.put(prop, e.getValue());
					props.put(e.getKey(), p);
				}
			}
		}
		for (Map.Entry<Integer,Map<String,String>> e : props.entrySet()) {
			String cps = "U+" + PuaaUtility.toHexString(e.getKey());
			for (String prop : propNames) {
				String value = e.getValue().get(prop);
				if (value == null || value.length() == 0) continue;
				out.println(cps + "\t" + prop + "\t" + value);
			}
		}
	}
	
	private static List<PuaaSubtableEntry> toDecimalEntries(Map<Integer,String> smap) {
		Map<Integer,Integer> dmap = new HashMap<Integer,Integer>();
		for (Map.Entry<Integer,String> e : smap.entrySet()) {
			try {
				int iv = Integer.parseInt(e.getValue(), 10);
				if (Integer.toString(iv).equals(e.getValue())) {
					dmap.put(e.getKey(), iv);
				} else {
					return null;
				}
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		return PuaaUtility.createEntriesFromDecimalMap(dmap);
	}
	
	private static List<PuaaSubtableEntry> toHexadecimalEntries(Map<Integer,String> smap) {
		Map<Integer,Integer> dmap = new HashMap<Integer,Integer>();
		for (Map.Entry<Integer,String> e : smap.entrySet()) {
			try {
				int iv = Integer.parseInt(e.getValue(), 16);
				if (PuaaUtility.toHexString(iv).equals(e.getValue())) {
					dmap.put(e.getKey(), iv);
				} else {
					return null;
				}
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		return PuaaUtility.createEntriesFromHexadecimalMap(dmap);
	}
}
