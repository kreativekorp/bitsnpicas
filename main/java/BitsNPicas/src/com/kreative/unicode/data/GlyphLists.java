package com.kreative.unicode.data;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.Arrays;
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
	private static GlyphLists instance = null;
	
	public static GlyphLists instance() {
		if (instance == null) instance = new GlyphLists();
		return instance;
	}
	
	private final List<GlyphList> glyphLists;
	private final Map<String,GlyphList> glyphListMap;
	
	private GlyphLists() {
		TreeSet<GlyphList> glyphLists = new TreeSet<GlyphList>();
		Map<String,GlyphList> glyphListMap = new HashMap<String,GlyphList>();
		add(readBinaryStream(GlyphLists.class.getResourceAsStream("wgl4.pchgl")), glyphLists, glyphListMap);
		add(readBinaryStream(GlyphLists.class.getResourceAsStream("kgl1.pchgl")), glyphLists, glyphListMap);
		readDirectory(UnicodeUtils.getTableDirectory("GlyphLists"), glyphLists, glyphListMap);
		this.glyphLists = Arrays.asList(glyphLists.toArray(new GlyphList[glyphLists.size()]));
		this.glyphListMap = Collections.unmodifiableMap(glyphListMap);
	}
	
	private static void add(GlyphList gl, Collection<GlyphList> glyphLists, Map<String,GlyphList> glyphListMap) {
		if (gl != null) {
			String nn = gl.getName().replaceAll("[^A-Za-z0-9]", "").toLowerCase();
			glyphLists.add(gl);
			glyphListMap.put(nn, gl);
		}
	}
	
	private static void readDirectory(File d, Collection<GlyphList> glyphLists, Map<String,GlyphList> glyphListMap) {
		for (File f : d.listFiles()) {
			if (f.getName().startsWith(".") || f.getName().endsWith("\r")) {
				continue;
			} else if (f.isDirectory()) {
				readDirectory(f, glyphLists, glyphListMap);
			} else try {
				String n = f.getName().toLowerCase();
				if (n.endsWith(".pchgl")) {
					FileInputStream in = new FileInputStream(f);
					add(readBinaryStream(in), glyphLists, glyphListMap);
					in.close();
				} else if (n.endsWith(".txt")) {
					FileInputStream in = new FileInputStream(f);
					String name = UnicodeUtils.stripExtension(f.getName());
					add(readTextStream(in, name), glyphLists, glyphListMap);
					in.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	private static GlyphList readTextStream(InputStream in, String glName) {
		SortedSet<Integer> codePoints = new TreeSet<Integer>();
		Scanner scan = new Scanner(in);
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
		if (codePoints.isEmpty()) return null;
		int[] cpa = new int[codePoints.size()]; int i = 0;
		for (int cp : codePoints) cpa[i++] = cp;
		return new GlyphList(cpa, glName);
	}
	
	private static GlyphList readBinaryStream(InputStream in) {
		if (in == null) {
			return null;
		} else try {
			DataInputStream din = new DataInputStream(in);
			String name = din.readUTF();
			int[] codePoints = new int[din.readUnsignedShort()];
			for (int i = 0; i < codePoints.length; i++) {
				int cp = din.readUnsignedShort();
				if (cp < 0x20) {
					cp <<= 16;
					cp |= din.readUnsignedShort();
				}
				codePoints[i] = cp;
			}
			return new GlyphList(codePoints, name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
