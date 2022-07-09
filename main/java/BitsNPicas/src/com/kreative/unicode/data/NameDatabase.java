package com.kreative.unicode.data;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.kreative.unicode.ttflib.PuaaEntry;
import com.kreative.unicode.ttflib.PuaaTable;

public class NameDatabase {
	private static NameDatabase instance = null;
	
	public static NameDatabase instance() {
		if (instance == null) instance = new NameDatabase();
		return instance;
	}
	
	private static class NameKey {
		private final int codePoint;
		private final String category;
		private final String name;
		private NameKey(int codePoint, String category, String name) {
			this.codePoint = codePoint;
			this.category = category;
			this.name = name;
		}
		@Override
		public boolean equals(Object o) {
			if (o instanceof NameKey) {
				NameKey that = (NameKey)o;
				return (
					this.codePoint == that.codePoint &&
					this.category.equals(that.category) &&
					this.name.equals(that.name)
				);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return this.codePoint ^ this.category.hashCode() ^ this.name.hashCode();
		}
	}
	
	private static class NameValue {
		private final Set<String> aliases;
		private final Set<String> fonts;
		private NameValue() {
			this.aliases = new HashSet<String>();
			this.fonts = new HashSet<String>();
		}
	}
	
	public static class NameEntry implements Comparable<NameEntry> {
		public final int codePoint;
		public final String category;
		public final String name;
		public final Set<String> aliases;
		public final Set<String> fonts;
		public final double searchDistance;
		private NameEntry(NameKey key, NameValue value, double searchDistance) {
			this.codePoint = key.codePoint;
			this.category = key.category;
			this.name = key.name;
			this.aliases = Collections.unmodifiableSet(value.aliases);
			this.fonts = Collections.unmodifiableSet(value.fonts);
			this.searchDistance = searchDistance;
		}
		public boolean isPUA() {
			return (codePoint >= 0xE000 && codePoint < 0xF900) || codePoint >= 0xF0000;
		}
		public String shortestFontName() {
			String shortest = null;
			for (String font : fonts) {
				if (shortest == null || font.length() < shortest.length()) {
					shortest = font;
				}
			}
			return shortest;
		}
		@Override
		public int compareTo(NameEntry that) {
			if (this.isPUA() && !that.isPUA()) return +1;
			if (!this.isPUA() && that.isPUA()) return -1;
			if (this.searchDistance < that.searchDistance) return -1;
			if (this.searchDistance > that.searchDistance) return +1;
			if (this.codePoint < that.codePoint) return -1;
			if (this.codePoint > that.codePoint) return +1;
			return 0;
		}
	}
	
	private final NameResolver resolver;
	private final Map<NameKey, NameValue> names;
	private final LoaderThread thread;
	
	private NameDatabase() {
		this.resolver = NameResolver.instance();
		this.names = new HashMap<NameKey, NameValue>();
		this.thread = new LoaderThread();
		this.thread.start();
	}
	
	private synchronized NameValue getOrAdd(int codePoint, String category, String name) {
		NameKey key = new NameKey(codePoint, category, name);
		NameValue value = names.get(key);
		if (value == null) {
			value = new NameValue();
			names.put(key, value);
		}
		return value;
	}
	
	private synchronized void loadPuaaTable(PuaaTable puaa, String font) {
		Map<Integer,String> puaaNameMap = puaa.getPropertyMap("Name");
		if (puaaNameMap == null || puaaNameMap.isEmpty()) return;
		Map<Integer,String> puaaCategoryMap = puaa.getPropertyMap("General_Category");
		if (puaaCategoryMap == null) puaaCategoryMap = new HashMap<Integer,String>();
		Map<Integer,String> puaaUni1NameMap = puaa.getPropertyMap("Unicode_1_Name");
		if (puaaUni1NameMap == null) puaaUni1NameMap = new HashMap<Integer,String>();
		List<PuaaEntry> puaaNameAliases = puaa.getPropertyEntries("Name_Alias");
		if (puaaNameAliases == null) puaaNameAliases = new ArrayList<PuaaEntry>();
		
		for (int codePoint : puaaNameMap.keySet()) {
			String name = puaaNameMap.get(codePoint);
			if (name.startsWith("<") && name.endsWith(">")) {
				if (name.equals("<control>")) {
					name = puaaUni1NameMap.get(codePoint);
					if (name == null) {
						name = Integer.toHexString(codePoint).toUpperCase();
						if (name.length() < 4) name = ("0000" + name).substring(name.length());
						name = "CONTROL-" + name;
					}
				} else {
					continue;
				}
			}
			
			String category = puaaCategoryMap.get(codePoint);
			if (category == null) category = "Cn";
			NameValue value = getOrAdd(codePoint, category, name);
			if (font != null) value.fonts.add(font);
			value.aliases.add(name);
			String uni1Name = puaaUni1NameMap.get(codePoint);
			if (uni1Name != null) value.aliases.add(uni1Name);
			
			for (PuaaEntry e : puaaNameAliases) {
				if (e.contains(codePoint)) {
					Object o = e.getPropertyValue(codePoint);
					if (o instanceof PuaaEntry.NameAlias) {
						PuaaEntry.NameAlias a = (PuaaEntry.NameAlias)o;
						value.aliases.add(a.alias);
					}
				}
			}
		}
	}
	
	private synchronized void loadPropertyMap(Map<Integer,String> propertyMap) {
		if (propertyMap == null || propertyMap.isEmpty()) return;
		for (Map.Entry<Integer,String> e : propertyMap.entrySet()) {
			String c = resolver.getCategory(e.getKey());
			String n = resolver.getName(e.getKey());
			NameValue v = getOrAdd(e.getKey(), c, n);
			if (v.aliases.isEmpty()) v.aliases.add(n);
			v.aliases.add(e.getValue());
		}
	}
	
	private void loadDirectory(File d) {
		for (File f : d.listFiles()) {
			if (f.getName().startsWith(".") || f.getName().endsWith("\r")) {
				continue;
			} else if (f.isDirectory()) {
				loadDirectory(f);
			} else {
				String n = f.getName().toLowerCase();
				if (n.endsWith(".ucd") || n.endsWith(".ttf") || n.endsWith(".otf")) {
					PuaaTable puaa = PuaaCache.getPuaaTable(f);
					if (puaa != null) {
						String name = UnicodeUtils.stripExtension(f.getName());
						loadPuaaTable(puaa, name);
					}
				}
			}
		}
	}
	
	private class LoaderThread extends Thread {
		@Override
		public void run() {
			loadPuaaTable(PuaaCache.getPuaaTable("unidata.ucd"), null);
			
			PuaaTable extras = PuaaCache.getPuaaTable("extras.ucd");
			loadPropertyMap(extras.getPropertyMap("HTML_Entity"));
			loadPropertyMap(extras.getPropertyMap("PostScript_Name"));
			
			String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			for (String fontName : fontNames) {
				Font font = new Font(fontName, 0, 12);
				PuaaTable puaa = PuaaCache.getPuaaTable(font);
				if (puaa != null) loadPuaaTable(puaa, fontName);
			}
			
			loadDirectory(UnicodeUtils.getTableDirectory("UnicodeData"));
		}
	}
	
	private static final Pattern CP_PATTERN = Pattern.compile("^\\s*([Uu][Nn][Ii]|[Uu][+]|[Uu]|[0][Xx]|[$])?([0-9A-Fa-f]{1,6})([Hh])?\\s*$");
	
	public synchronized NameEntry find(String name) {
		String[] query = querySplit(name);
		if (query != null) {
			int codePoint = -1;
			if (query.length == 1) {
				Matcher m = CP_PATTERN.matcher(query[0]);
				if (m.matches()) {
					codePoint = Integer.parseInt(m.group(2), 16);
					if (codePoint < 0x110000) {
						String c = resolver.getCategory(codePoint);
						String n = resolver.getName(codePoint);
						NameValue v = getOrAdd(codePoint, c, n);
						if (v.aliases.isEmpty()) v.aliases.add(n);
					}
				}
			}
			
			List<NameEntry> results = new ArrayList<NameEntry>();
			for (Map.Entry<NameKey,NameValue> e : names.entrySet()) {
				if (e.getKey().codePoint == codePoint) {
					results.add(new NameEntry(e.getKey(), e.getValue(), 0));
					continue;
				}
				if (queryMatch(query, e.getKey(), e.getValue())) {
					double distance = searchDistance(name, e.getKey().name);
					for (String alias : e.getValue().aliases) {
						double d = searchDistance(name, alias);
						if (d < distance) distance = d;
					}
					results.add(new NameEntry(e.getKey(), e.getValue(), distance));
				}
			}
			
			if (!results.isEmpty()) {
				Collections.sort(results);
				return results.get(0);
			}
		}
		
		if (name.length() > 0) {
			int codePoint = name.codePointAt(0);
			String c = resolver.getCategory(codePoint);
			String n = resolver.getName(codePoint);
			NameValue v = getOrAdd(codePoint, c, n);
			if (v.aliases.isEmpty()) v.aliases.add(n);
			return new NameEntry(new NameKey(codePoint, c, n), v, 0);
		}
		
		return null;
	}
	
	private static String[] querySplit(String query) {
		if (query == null) return null;
		query = query.trim().toUpperCase();
		if (query.length() == 0) return null;
		String[] tokens = query.split("\\s+");
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].length() < 4) {
				tokens[i] = " " + tokens[i] + " ";
			}
		}
		return tokens;
	}
	
	private static boolean queryMatch(String[] query, String name) {
		name = " " + name.toUpperCase() + " ";
		for (String token : query) {
			if (!name.contains(token)) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean queryMatch(String[] query, NameKey key, NameValue value) {
		if (queryMatch(query, key.name)) {
			return true;
		}
		for (String alias : value.aliases) {
			if (queryMatch(query, alias)) {
				return true;
			}
		}
		return false;
	}
	
	private static int phraseDistance(String s1, String s2) {
		if (s1.equalsIgnoreCase(s2)) return 0;
		int l1 = s1.length() + 1;
		int l2 = s2.length() + 1;
		int[][] d = new int[l1][l2];
		for (int i = 0; i < l1; i++) d[i][0] = i;
		for (int j = 0; j < l2; j++) d[0][j] = j;
		for (int j = 1; j < l2; j++) {
			for (int i = 1; i < l1; i++) {
				int ci = d[i - 1][j] + 1;
				int cd = d[i][j - 1] + 1;
				int cs = d[i - 1][j - 1];
				int c1 = s1.charAt(i - 1);
				int c2 = s2.charAt(j - 1);
				if (c1 != c2) cs++;
				if (ci <= cd) {
					if (ci <= cs) d[i][j] = ci;
					else d[i][j] = cs;
				} else {
					if (cd <= cs) d[i][j] = cd;
					else d[i][j] = cs;
				}
			}
		}
		return d[l1 - 1][l2 - 1];
	}
	
	private static int wordDistance(String s1, String s2) {
		if (s1.equalsIgnoreCase(s2)) return 0;
		String[] w1 = s1.split("[^A-Za-z0-9]+");
		String[] w2 = s2.split("[^A-Za-z0-9]+");
		int d = 0;
		for (int i1 = 0; i1 < w1.length; i1++) {
			int d1 = s2.length();
			for (int i2 = 0; i2 < w2.length; i2++) {
				int d2 = phraseDistance(w1[i1], w2[i2]);
				if (d2 < d1) d1 = d2;
				if (d2 == 0) break;
			}
			d += d1;
		}
		return d;
	}
	
	private static double searchDistance(String s1, String s2) {
		if (s1.equalsIgnoreCase(s2)) return 0;
		double pd = phraseDistance(s1, s2);
		pd -= 0.8 * Math.abs(s1.length() - s2.length());
		double wd = wordDistance(s1, s2);
		return Math.min(pd, wd) * 0.8 + Math.max(pd, wd) * 0.2;
	}
}
