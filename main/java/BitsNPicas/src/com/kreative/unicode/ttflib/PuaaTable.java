package com.kreative.unicode.ttflib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class PuaaTable {
	private SortedMap<String,List<PuaaEntry>> entries;
	
	public PuaaTable(InputStream in) throws IOException { read(in); }
	public PuaaTable(byte[] data) throws IOException { read(data); }
	
	private void read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[65536]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.close();
		read(out.toByteArray());
	}
	
	private void read(byte[] data) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(bin);
		readData(in);
		in.close();
		bin.close();
	}
	
	private void readData(DataInputStream in) throws IOException {
		this.entries = new TreeMap<String,List<PuaaEntry>>();
		
		in.readUnsignedShort();
		int count = in.readUnsignedShort();
		int[] pno = new int[count];
		int[] sho = new int[count];
		
		for (int i = 0; i < count; i++) {
			pno[i] = in.readInt();
			sho[i] = in.readInt();
		}
		
		for (int i = 0; i < count; i++) {
			String prop = PuaaEntry.readString(in, 0, pno[i]);
			List<PuaaEntry> list = readEntryList(in, 0, sho[i]);
			this.entries.put(prop, list);
		}
		
		this.entries = Collections.unmodifiableSortedMap(this.entries);
	}
	
	public Set<String> getProperties() {
		return entries.keySet();
	}
	
	public List<PuaaEntry> getPropertyEntries(String prop) {
		return entries.get(prop);
	}
	
	public Map<Integer,String> getPropertyMap(String prop) {
		return mapFromEntries(entries.get(prop));
	}
	
	public SortedMap<Integer,String> getPropertySortedMap(String prop) {
		return sortedMapFromEntries(entries.get(prop));
	}
	
	public List<PuaaEntry> getPropertyRuns(String prop) {
		return runsFromEntries(entries.get(prop));
	}
	
	static List<PuaaEntry> readEntryList(DataInputStream in, int to, int sho) throws IOException {
		if (sho > 0) {
			in.reset();
			in.skipBytes(to + sho);
			int l = in.readUnsignedShort();
			PuaaEntry[] d = new PuaaEntry[l];
			for (int i = 0; i < l; i++) d[i] = new PuaaEntry();
			for (int i = 0; i < l; i++) d[i].readHead(in);
			for (int i = 0; i < l; i++) d[i].readBody(in, to);
			return Collections.unmodifiableList(Arrays.asList(d));
		}
		return null;
	}
	
	static Map<Integer,String> mapFromEntries(List<PuaaEntry> entries) {
		if (entries == null) return null;
		Map<Integer,String> m = new HashMap<Integer,String>();
		putEntriesIntoMap(entries, m);
		return Collections.unmodifiableMap(m);
	}
	
	static SortedMap<Integer,String> sortedMapFromEntries(List<PuaaEntry> entries) {
		if (entries == null) return null;
		SortedMap<Integer,String> m = new TreeMap<Integer,String>();
		putEntriesIntoMap(entries, m);
		return Collections.unmodifiableSortedMap(m);
	}
	
	static void putEntriesIntoMap(List<PuaaEntry> entries, Map<Integer,String> map) {
		for (PuaaEntry entry : entries) {
			int fcp = entry.getFirstCodePoint();
			int lcp = entry.getLastCodePoint();
			for (int cp = fcp; cp <= lcp; cp++) {
				String ov = map.get(cp);
				String nv = entry.getPropertyString(cp);
				map.put(cp, (ov == null) ? nv : (ov + nv));
			}
		}
	}
	
	static List<PuaaEntry> runsFromEntries(List<PuaaEntry> entries) {
		if (entries == null) return null;
		SortedMap<Integer,String> m = sortedMapFromEntries(entries);
		List<PuaaEntry> runs = new ArrayList<PuaaEntry>();
		PuaaEntry run = null;
		for (Map.Entry<Integer,String> e : m.entrySet()) {
			if (run == null || !run.extendData(e.getKey(), e.getValue())) {
				run = new PuaaEntry();
				run.setData(e.getKey(), e.getValue());
				runs.add(run);
			}
		}
		return Collections.unmodifiableList(runs);
	}
}
