package com.kreative.bitsnpicas.puaa;

import java.io.UnsupportedEncodingException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;

public class PuaaUtility {
	public static String[] splitLine(String line) {
		int o = line.indexOf("#");
		if (o >= 0) line = line.substring(0, o);
		line = line.trim();
		if (line.length() == 0) return null;
		return line.split(";");
	}
	
	public static int[] splitRange(String range) {
		String[] ep = range.split("[.]+");
		int start = Integer.parseInt(ep[0].trim(), 16);
		if (ep.length < 2) return new int[]{start, start};
		int end = Integer.parseInt(ep[1].trim(), 16);
		return new int[]{start, end};
	}
	
	public static String toHexString(int value) {
		String s = Integer.toHexString(value).toUpperCase();
		while (s.length() < 4) s = "0" + s;
		return s;
	}
	
	public static String joinRange(PuaaSubtableEntry e) {
		if (e.firstCodePoint == e.lastCodePoint) {
			return toHexString(e.firstCodePoint);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(toHexString(e.firstCodePoint));
			sb.append("..");
			sb.append(toHexString(e.lastCodePoint));
			return sb.toString();
		}
	}
	
	public static String joinLine(String[] line, String delimiter) {
		StringBuffer sb = new StringBuffer(line[0]);
		for (int i = 1; i < line.length; i++) {
			sb.append(delimiter);
			if (line[i] != null) sb.append(line[i]);
		}
		return sb.toString();
	}
	
	public static boolean equals(Object a, Object b) {
		if (a == null) return (b == null);
		if (b == null) return (a == null);
		return a.equals(b);
	}
	
	public static boolean appendToEntry(PuaaSubtableEntry.Single e, int cp, String value) {
		if (e == null) return false;
		if ((short)e.lastCodePoint == -1) return false;
		if (e.lastCodePoint+1 != cp) return false;
		if (!equals(e.value, value)) return false;
		e.lastCodePoint++;
		return true;
	}
	
	public static boolean appendToEntry(PuaaSubtableEntry.Multiple e, int cp, String value) {
		if (e == null) return false;
		if ((short)e.lastCodePoint == -1) return false;
		if (e.lastCodePoint+1 != cp) return false;
		List<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(e.values));
		list.add(value);
		e.values = list.toArray(new String[list.size()]);
		e.lastCodePoint++;
		return true;
	}
	
	public static boolean appendToEntry(PuaaSubtableEntry.Boolean e, int cp, boolean value) {
		if (e == null) return false;
		if ((short)e.lastCodePoint == -1) return false;
		if (e.lastCodePoint+1 != cp) return false;
		if (e.value != value) return false;
		e.lastCodePoint++;
		return true;
	}
	
	public static boolean appendToEntry(PuaaSubtableEntry.Decimal e, int cp, int value) {
		if (e == null) return false;
		if ((short)e.lastCodePoint == -1) return false;
		if (e.lastCodePoint+1 != cp) return false;
		if (e.value != value) return false;
		e.lastCodePoint++;
		return true;
	}
	
	public static boolean appendToEntry(PuaaSubtableEntry.Hexadecimal e, int cp, int value) {
		if (e == null) return false;
		if ((short)e.lastCodePoint == -1) return false;
		if (e.lastCodePoint+1 != cp) return false;
		if (e.value != value) return false;
		e.lastCodePoint++;
		return true;
	}
	
	public static boolean appendToEntry(PuaaSubtableEntry.HexMultiple e, int cp, int value) {
		if (e == null) return false;
		if ((short)e.lastCodePoint == -1) return false;
		if (e.lastCodePoint+1 != cp) return false;
		int n = e.values.length;
		int[] newValues = new int[n + 1];
		for (int i = 0; i < n; i++) newValues[i] = e.values[i];
		newValues[n] = value;
		e.values = newValues;
		e.lastCodePoint++;
		return true;
	}
	
	public static boolean appendToEntry(PuaaSubtableEntry.HexSequence e, int cp, int[] values) {
		if (e == null) return false;
		if ((short)e.lastCodePoint == -1) return false;
		if (e.lastCodePoint+1 != cp) return false;
		if (!Arrays.equals(e.values, values)) return false;
		e.lastCodePoint++;
		return true;
	}
	
	private static final class CEFMItem<T> implements Comparable<CEFMItem<T>> {
		public final int cp; public final T value;
		public CEFMItem(int cp, T value) { this.cp = cp; this.value = value; }
		@Override public int compareTo(CEFMItem<T> that) { return this.cp - that.cp; }
	}
	
	private static <T> List<CEFMItem<T>> sorted(Map<Integer,T> map) {
		List<CEFMItem<T>> items = new ArrayList<CEFMItem<T>>();
		for (Map.Entry<Integer,T> e : map.entrySet()) {
			T v = e.getValue(); if (v == null) continue;
			if (v instanceof String && v.toString().length() == 0) continue;
			items.add(new CEFMItem<T>(e.getKey(), v));
		}
		Collections.sort(items);
		return items;
	}
	
	public static List<PuaaSubtableEntry> createEntriesFromStringMap(Map<Integer,String> map) {
		// Sort map entries by code point.
		List<CEFMItem<String>> items = sorted(map);
		
		// Create entries, collapsing runs of the same value into a single entry.
		List<PuaaSubtableEntry.Single> runs = new ArrayList<PuaaSubtableEntry.Single>();
		PuaaSubtableEntry.Single currentRun = null;
		for (CEFMItem<String> item : items) {
			if (!appendToEntry(currentRun, item.cp, item.value)) {
				currentRun = new PuaaSubtableEntry.Single();
				currentRun.firstCodePoint = item.cp;
				currentRun.lastCodePoint = item.cp;
				currentRun.value = item.value;
				runs.add(currentRun);
			}
		}
		
		// Create entries, collapsing runs of length 1 into a single entry.
		List<PuaaSubtableEntry> entries = new ArrayList<PuaaSubtableEntry>();
		PuaaSubtableEntry.Multiple currentEntry = null;
		for (PuaaSubtableEntry.Single run : runs) {
			if (run.firstCodePoint != run.lastCodePoint) {
				currentEntry = null;
				entries.add(run);
			} else if (!appendToEntry(currentEntry, run.firstCodePoint, run.value)) {
				currentEntry = new PuaaSubtableEntry.Multiple();
				currentEntry.firstCodePoint = run.firstCodePoint;
				currentEntry.lastCodePoint = run.lastCodePoint;
				currentEntry.values = new String[]{run.value};
				entries.add(currentEntry);
			}
		}
		
		// Replace Multiples of a single value with a Single.
		for (int i = 0, n = entries.size(); i < n; i++) {
			PuaaSubtableEntry e = entries.get(i);
			if (e.firstCodePoint == e.lastCodePoint) {
				currentRun = new PuaaSubtableEntry.Single();
				currentRun.firstCodePoint = e.firstCodePoint;
				currentRun.lastCodePoint = e.lastCodePoint;
				currentRun.value = e.getPropertyValue(e.firstCodePoint);
				entries.set(i, currentRun);
			}
		}
		
		return entries;
	}
	
	public static List<PuaaSubtableEntry> createEntriesFromBooleanMap(Map<Integer,Boolean> map) {
		List<CEFMItem<Boolean>> items = sorted(map);
		List<PuaaSubtableEntry> entries = new ArrayList<PuaaSubtableEntry>();
		PuaaSubtableEntry.Boolean currentEntry = null;
		for (CEFMItem<Boolean> item : items) {
			if (!appendToEntry(currentEntry, item.cp, item.value)) {
				currentEntry = new PuaaSubtableEntry.Boolean();
				currentEntry.firstCodePoint = item.cp;
				currentEntry.lastCodePoint = item.cp;
				currentEntry.value = item.value;
				entries.add(currentEntry);
			}
		}
		return entries;
	}
	
	public static List<PuaaSubtableEntry> createEntriesFromDecimalMap(Map<Integer,Integer> map) {
		List<CEFMItem<Integer>> items = sorted(map);
		List<PuaaSubtableEntry> entries = new ArrayList<PuaaSubtableEntry>();
		PuaaSubtableEntry.Decimal currentEntry = null;
		for (CEFMItem<Integer> item : items) {
			if (!appendToEntry(currentEntry, item.cp, item.value)) {
				currentEntry = new PuaaSubtableEntry.Decimal();
				currentEntry.firstCodePoint = item.cp;
				currentEntry.lastCodePoint = item.cp;
				currentEntry.value = item.value;
				entries.add(currentEntry);
			}
		}
		return entries;
	}
	
	public static List<PuaaSubtableEntry> createEntriesFromHexadecimalMap(Map<Integer,Integer> map) {
		// Sort map entries by code point.
		List<CEFMItem<Integer>> items = sorted(map);
		
		// Create entries, collapsing runs of the same value into a single entry.
		List<PuaaSubtableEntry.Hexadecimal> runs = new ArrayList<PuaaSubtableEntry.Hexadecimal>();
		PuaaSubtableEntry.Hexadecimal currentRun = null;
		for (CEFMItem<Integer> item : items) {
			if (!appendToEntry(currentRun, item.cp, item.value)) {
				currentRun = new PuaaSubtableEntry.Hexadecimal();
				currentRun.firstCodePoint = item.cp;
				currentRun.lastCodePoint = item.cp;
				currentRun.value = item.value;
				runs.add(currentRun);
			}
		}
		
		// Create entries, collapsing runs of length 1 into a single entry.
		List<PuaaSubtableEntry> entries = new ArrayList<PuaaSubtableEntry>();
		PuaaSubtableEntry.HexMultiple currentEntry = null;
		for (PuaaSubtableEntry.Hexadecimal run : runs) {
			if (run.firstCodePoint != run.lastCodePoint) {
				currentEntry = null;
				entries.add(run);
			} else if (!appendToEntry(currentEntry, run.firstCodePoint, run.value)) {
				currentEntry = new PuaaSubtableEntry.HexMultiple();
				currentEntry.firstCodePoint = run.firstCodePoint;
				currentEntry.lastCodePoint = run.lastCodePoint;
				currentEntry.values = new int[]{run.value};
				entries.add(currentEntry);
			}
		}
		
		// Replace HexMultiples of a single value with a Hexadecimal.
		for (int i = 0, n = entries.size(); i < n; i++) {
			PuaaSubtableEntry e = entries.get(i);
			if (e.firstCodePoint == e.lastCodePoint) {
				currentRun = new PuaaSubtableEntry.Hexadecimal();
				currentRun.firstCodePoint = e.firstCodePoint;
				currentRun.lastCodePoint = e.lastCodePoint;
				currentRun.value = Integer.parseInt(e.getPropertyValue(e.firstCodePoint), 16);
				entries.set(i, currentRun);
			}
		}
		
		return entries;
	}
	
	public static List<PuaaSubtableEntry> createEntriesFromHexSequenceMap(Map<Integer,int[]> map) {
		List<CEFMItem<int[]>> items = sorted(map);
		List<PuaaSubtableEntry> entries = new ArrayList<PuaaSubtableEntry>();
		PuaaSubtableEntry.HexSequence currentEntry = null;
		for (CEFMItem<int[]> item : items) {
			if (!appendToEntry(currentEntry, item.cp, item.value)) {
				currentEntry = new PuaaSubtableEntry.HexSequence();
				currentEntry.firstCodePoint = item.cp;
				currentEntry.lastCodePoint = item.cp;
				currentEntry.values = item.value;
				entries.add(currentEntry);
			}
		}
		return entries;
	}
	
	private static boolean isChunky(char ch) {
		return (
			Character.isLetterOrDigit(ch) ||
			Character.isSurrogate(ch) ||
			(ch >= '"' && ch <= '*') ||
			ch == '<' || ch == '>' || ch == '@' ||
			ch == '[' || ch == ']' || ch == '_' ||
			ch == '{' || ch == '}'
		);
	}
	
	private static List<String> splitName(String name) {
		List<String> pieces = new ArrayList<String>();
		char[] ch = name.toCharArray();
		for (int o = 0, i = 0; o < ch.length; o = i) {
			while (i < ch.length && isChunky(ch[i])) i++;
			while (i < ch.length && !isChunky(ch[i]) && !Character.isWhitespace(ch[i])) i++;
			while (i < ch.length && Character.isWhitespace(ch[i])) i++;
			pieces.add(name.substring(o, i));
		}
		return pieces;
	}
	
	private static String joinName(List<String> pieces) {
		StringBuffer sb = new StringBuffer();
		for (String piece : pieces) sb.append(piece);
		return sb.toString();
	}
	
	private static final class CEFNMItem implements Comparable<CEFNMItem> {
		public final int cp; public final List<String> pieces;
		public CEFNMItem(int cp, String value) { this.cp = cp; this.pieces = splitName(value); }
		@Override public int compareTo(CEFNMItem that) { return this.cp - that.cp; }
		public String getPrefix() { return pieces.isEmpty() ? null : pieces.get(0); }
		public String removePrefix() { return pieces.isEmpty() ? null : pieces.remove(0); }
		public String getSuffix() { return pieces.isEmpty() ? null : pieces.get(pieces.size()-1); }
		public String removeSuffix() { return pieces.isEmpty() ? null : pieces.remove(pieces.size()-1); }
		public String getValue() { return joinName(pieces); }
	}
	
	private static int utf8Length(String s) {
		try { return s.getBytes("UTF-8").length; }
		catch (UnsupportedEncodingException e) { return 0; }
	}
	
	public static List<PuaaSubtableEntry> createEntriesFromNameMap(Map<Integer,String> map) {
		// Sort map entries by code point.
		List<CEFNMItem> items = new ArrayList<CEFNMItem>();
		for (Map.Entry<Integer,String> e : map.entrySet()) {
			if (e.getValue() == null || e.getValue().length() == 0) continue;
			items.add(new CEFNMItem(e.getKey(), e.getValue()));
		}
		Collections.sort(items);
		
		// Create entries for runs of common prefixes.
		List<PuaaSubtableEntry> prefixes = new ArrayList<PuaaSubtableEntry>();
		while (true) {
			List<PuaaSubtableEntry> newPrefixes = new ArrayList<PuaaSubtableEntry>();
			for (int o = 0, i = 0, n = items.size(); o < n; o = i) {
				CEFNMItem firstItem = items.get(i); i++;
				if (firstItem.pieces.isEmpty()) continue;
				
				// Create an entry for the first item's prefix.
				PuaaSubtableEntry.Single entry = new PuaaSubtableEntry.Single();
				entry.firstCodePoint = firstItem.cp;
				entry.lastCodePoint = firstItem.cp;
				entry.value = firstItem.getPrefix();
				
				// Extend the entry for subsequent items with the same prefix.
				while (i < n) {
					CEFNMItem item = items.get(i);
					if (appendToEntry(entry, item.cp, item.getPrefix())) i++;
					else break;
				}
				
				// If there were subsequent items, add an entry and remove the prefix.
				if (entry.firstCodePoint != entry.lastCodePoint) {
					newPrefixes.add(entry);
					while (o < i) { items.get(o).removePrefix(); o++; }
				}
			}
			if (newPrefixes.isEmpty()) break;
			prefixes.addAll(newPrefixes);
		}
		
		// Create entries for runs of common suffixes.
		List<PuaaSubtableEntry> suffixes = new ArrayList<PuaaSubtableEntry>();
		while (true) {
			List<PuaaSubtableEntry> newSuffixes = new ArrayList<PuaaSubtableEntry>();
			for (int o = 0, i = 0, n = items.size(); o < n; o = i) {
				CEFNMItem firstItem = items.get(i); i++;
				if (firstItem.pieces.isEmpty()) continue;
				
				// Create an entry for the first item's suffix.
				PuaaSubtableEntry.Single entry = new PuaaSubtableEntry.Single();
				entry.firstCodePoint = firstItem.cp;
				entry.lastCodePoint = firstItem.cp;
				entry.value = firstItem.getSuffix();
				
				// Extend the entry for subsequent items with the same suffix.
				while (i < n) {
					CEFNMItem item = items.get(i);
					if (appendToEntry(entry, item.cp, item.getSuffix())) i++;
					else break;
				}
				
				// If there were subsequent items, add an entry and remove the suffix.
				if (entry.firstCodePoint != entry.lastCodePoint) {
					newSuffixes.add(entry);
					while (o < i) { items.get(o).removeSuffix(); o++; }
				}
			}
			if (newSuffixes.isEmpty()) break;
			suffixes.addAll(0, newSuffixes);
		}
		
		// Add remaining name fragments.
		// There are two maps here because some values of the kDefinition
		// property in the Unihan database are longer than 255 bytes.
		Map<Integer,String> remainder1 = new HashMap<Integer,String>();
		Map<Integer,String> remainder2 = new HashMap<Integer,String>();
		for (CEFNMItem item : items) {
			if (item.pieces.isEmpty()) continue;
			String value = item.getValue();
			if (utf8Length(value) > 255) {
				int h = value.length() / 2;
				if (Character.isLowSurrogate(value.charAt(h))) h++;
				remainder1.put(item.cp, value.substring(0, h));
				remainder2.put(item.cp, value.substring(h));
			} else {
				remainder1.put(item.cp, value);
			}
		}
		
		List<PuaaSubtableEntry> entries = new ArrayList<PuaaSubtableEntry>();
		entries.addAll(prefixes);
		entries.addAll(createEntriesFromStringMap(remainder1));
		entries.addAll(createEntriesFromStringMap(remainder2));
		entries.addAll(suffixes);
		return entries;
	}
	
	public static SortedMap<Integer,String> createMapFromEntries(List<? extends PuaaSubtableEntry> entries) {
		SortedMap<Integer,String> map = new TreeMap<Integer,String>();
		for (PuaaSubtableEntry e : entries) {
			for (int cp = e.firstCodePoint; cp <= e.lastCodePoint; cp++) {
				String value = e.getPropertyValue(cp);
				if (value == null) continue;
				if (map.containsKey(cp)) {
					map.put(cp, map.get(cp) + value);
				} else {
					map.put(cp, value);
				}
			}
		}
		return map;
	}
	
	public static List<PuaaSubtableEntry.Single> createRunsFromEntries(List<? extends PuaaSubtableEntry> entries) {
		List<CEFMItem<String>> items = sorted(createMapFromEntries(entries));
		List<PuaaSubtableEntry.Single> runs = new ArrayList<PuaaSubtableEntry.Single>();
		PuaaSubtableEntry.Single currentRun = null;
		for (CEFMItem<String> item : items) {
			if (!appendToEntry(currentRun, item.cp, item.value)) {
				currentRun = new PuaaSubtableEntry.Single();
				currentRun.firstCodePoint = item.cp;
				currentRun.lastCodePoint = item.cp;
				currentRun.value = item.value;
				runs.add(currentRun);
			}
		}
		return runs;
	}
	
	public static int naturalCompare(String a, String b) {
		List<String> na = naturalTokenize(a.trim());
		List<String> nb = naturalTokenize(b.trim());
		for (int i = 0; i < na.size() && i < nb.size(); i++) {
			try {
				double va = Double.parseDouble(na.get(i));
				double vb = Double.parseDouble(nb.get(i));
				int cmp = Double.compare(va, vb);
				if (cmp != 0) return cmp;
			} catch (NumberFormatException e) {
				int cmp = na.get(i).compareToIgnoreCase(nb.get(i));
				if (cmp != 0) return cmp;
			}
		}
		return na.size() - nb.size();
	}
	
	private static List<String> naturalTokenize(String s) {
		List<String> tokens = new ArrayList<String>();
		StringBuffer token = new StringBuffer();
		int tokenType = 0;
		CharacterIterator iter = new StringCharacterIterator(s);
		for (char ch = iter.first(); ch != CharacterIterator.DONE; ch = iter.next()) {
			int tt = Character.isDigit(ch) ? 1 : Character.isLetter(ch) ? 2 : 3;
			if (tt != tokenType) {
				if (token.length() > 0) {
					tokens.add(token.toString());
					token = new StringBuffer();
				}
				tokenType = tt;
			}
			token.append(ch);
		}
		if (token.length() > 0) tokens.add(token.toString());
		return tokens;
	}
}
