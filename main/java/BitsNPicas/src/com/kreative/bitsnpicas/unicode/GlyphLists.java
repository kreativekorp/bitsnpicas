package com.kreative.bitsnpicas.unicode;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

public class GlyphLists extends AbstractList<GlyphList> {
	private static final String[] GLYPH_LIST_NAMES = new String[]{
		"Kreative Glyph List 1",
		"Windows Glyph List 4",
	};
	
	private static GlyphLists instance = null;
	
	public static GlyphLists instance() {
		if (instance == null) instance = new GlyphLists();
		return instance;
	}
	
	private final List<GlyphList> glyphLists;
	private final Map<String,GlyphList> glyphListMap;
	
	private GlyphLists() {
		List<GlyphList> glyphLists = new ArrayList<GlyphList>();
		Map<String,GlyphList> glyphListMap = new HashMap<String,GlyphList>();
		for (String glName : GLYPH_LIST_NAMES) {
			SortedSet<Integer> codePoints = new TreeSet<Integer>();
			String fileName = glName.replaceAll("\\s+", "_").replaceAll("[^A-Za-z0-9_]+", "-") + ".txt";
			Scanner scan = new Scanner(EncodingList.class.getResourceAsStream(fileName));
			while (scan.hasNextLine()) {
				String line = scan.nextLine().trim();
				if (line.length() > 0 && line.charAt(0) != '#') {
					String[] f = line.split("\\s+");
					String cps = f[0].trim().toLowerCase();
					if (cps.startsWith("0x")) {
						try {
							int cp = Integer.parseInt(cps.substring(2), 16);
							codePoints.add(cp);
						} catch (NumberFormatException e) {
							continue;
						}
					}
				}
			}
			scan.close();
			int[] cpa = new int[codePoints.size()]; int i = 0;
			for (int cp : codePoints) cpa[i++] = cp;
			GlyphList gl = new GlyphList(cpa, glName);
			glyphLists.add(gl);
			String nn = glName.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
			glyphListMap.put(nn, gl);
		}
		this.glyphLists = Collections.unmodifiableList(glyphLists);
		this.glyphListMap = Collections.unmodifiableMap(glyphListMap);
	}
	
	public boolean contains(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return glyphListMap.containsKey(nn);
	}
	
	public boolean contains(Object o) {
		return glyphLists.contains(o);
	}
	
	public boolean containsAll(Collection<?> c) {
		return glyphLists.containsAll(c);
	}
	
	public GlyphList get(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return glyphListMap.get(nn);
	}
	
	public GlyphList get(int index) {
		return glyphLists.get(index);
	}
	
	public int indexOf(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return glyphListMap.containsKey(nn) ? glyphLists.indexOf(glyphListMap.get(nn)) : -1;
	}
	
	public int indexOf(Object o) {
		return glyphLists.indexOf(o);
	}
	
	public boolean isEmpty() {
		return glyphLists.isEmpty();
	}
	
	public Iterator<GlyphList> iterator() {
		return glyphLists.iterator();
	}
	
	public int lastIndexOf(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return glyphListMap.containsKey(nn) ? glyphLists.lastIndexOf(glyphListMap.get(nn)) : -1;
	}
	
	public int lastIndexOf(Object o) {
		return glyphLists.lastIndexOf(o);
	}
	
	public ListIterator<GlyphList> listIterator() {
		return glyphLists.listIterator();
	}
	
	public ListIterator<GlyphList> listIterator(int index) {
		return glyphLists.listIterator(index);
	}
	
	public int size() {
		return glyphLists.size();
	}
	
	public List<GlyphList> subList(int fromIndex, int toIndex) {
		return glyphLists.subList(fromIndex, toIndex);
	}
	
	public Object[] toArray() {
		return glyphLists.toArray();
	}
	
	public <T> T[] toArray(T[] a) {
		return glyphLists.toArray(a);
	}
}
