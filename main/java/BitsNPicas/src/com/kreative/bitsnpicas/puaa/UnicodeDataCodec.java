package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class UnicodeDataCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "UnicodeData.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{
			"Name",
			"General_Category",
			"Canonical_Combining_Class",
			"Bidi_Class",
			"Decomposition_Type",
			"Decomposition_Mapping",
			"Numeric_Type",
			"Numeric_Value",
			"Bidi_Mirrored",
			"Unicode_1_Name",
			"ISO_Comment",
			"Simple_Uppercase_Mapping",
			"Simple_Lowercase_Mapping",
			"Simple_Titlecase_Mapping",
		};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		HashMap<Integer,String> names = new HashMap<Integer,String>();
		HashMap<Integer,String> categories = new HashMap<Integer,String>();
		HashMap<Integer,Integer> combClasses = new HashMap<Integer,Integer>();
		HashMap<Integer,String> bidiClasses = new HashMap<Integer,String>();
		HashMap<Integer,String> decompTypes = new HashMap<Integer,String>();
		HashMap<Integer,int[]> decompMappings = new HashMap<Integer,int[]>();
		HashMap<Integer,String> numericTypes = new HashMap<Integer,String>();
		HashMap<Integer,String> numericValues = new HashMap<Integer,String>();
		HashMap<Integer,Boolean> bidiMirrored = new HashMap<Integer,Boolean>();
		HashMap<Integer,String> uni1Names = new HashMap<Integer,String>();
		HashMap<Integer,String> comments = new HashMap<Integer,String>();
		HashMap<Integer,Integer> uppercase = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> lowercase = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> titlecase = new HashMap<Integer,Integer>();
		
		String[] fields; Integer cp;
		Boolean b; Integer i; String s;
		String[] words;
		
		while (in.hasNextLine()) {
			if ((fields = PuaaUtility.splitLine(in.nextLine())) == null) continue;
			if ((cp = parseHex(fields, 0)) == null) continue;
			if ((s = parseString(fields, 1)) != null) names.put(cp, s);
			if ((s = parseString(fields, 2)) != null) categories.put(cp, s);
			if ((i = parseInt(fields, 3)) != null) combClasses.put(cp, i);
			if ((s = parseString(fields, 4)) != null) bidiClasses.put(cp, s);
			
			if ((words = parseWords(fields, 5)) != null) {
				List<String> types = new ArrayList<String>();
				List<Integer> mappings = new ArrayList<Integer>();
				for (String word : words) {
					try { mappings.add(Integer.parseInt(word, 16)); }
					catch (NumberFormatException e) { types.add(word); }
				}
				if (!types.isEmpty()) decompTypes.put(cp, joinWords(types));
				if (!mappings.isEmpty()) decompMappings.put(cp, toIntArray(mappings));
			}
			
			if ((s = parseString(fields, 6)) != null) {
				numericTypes.put(cp, "Decimal");
				numericValues.put(cp, s);
			} else if ((s = parseString(fields, 7)) != null) {
				numericTypes.put(cp, "Digit");
				numericValues.put(cp, s);
			} else if ((s = parseString(fields, 8)) != null) {
				numericTypes.put(cp, "Numeric");
				numericValues.put(cp, s);
			}
			
			if ((b = parseBoolean(fields, 9)) != null) bidiMirrored.put(cp, b);
			if ((s = parseString(fields, 10)) != null) uni1Names.put(cp, s);
			if ((s = parseString(fields, 11)) != null) comments.put(cp, s);
			if ((i = parseHex(fields, 12)) != null) uppercase.put(cp, i);
			if ((i = parseHex(fields, 13)) != null) lowercase.put(cp, i);
			if ((i = parseHex(fields, 14)) != null) titlecase.put(cp, i);
		}
		
		PuaaSubtable st = puaa.getOrCreateSubtable("Name");
		st.addAll(PuaaUtility.createEntriesFromNameMap(names));
		st = puaa.getOrCreateSubtable("General_Category");
		st.addAll(PuaaUtility.createEntriesFromStringMap(categories));
		st = puaa.getOrCreateSubtable("Canonical_Combining_Class");
		st.addAll(PuaaUtility.createEntriesFromDecimalMap(combClasses));
		st = puaa.getOrCreateSubtable("Bidi_Class");
		st.addAll(PuaaUtility.createEntriesFromStringMap(bidiClasses));
		st = puaa.getOrCreateSubtable("Decomposition_Type");
		st.addAll(PuaaUtility.createEntriesFromStringMap(decompTypes));
		st = puaa.getOrCreateSubtable("Decomposition_Mapping");
		st.addAll(PuaaUtility.createEntriesFromHexSequenceMap(decompMappings));
		st = puaa.getOrCreateSubtable("Numeric_Type");
		st.addAll(PuaaUtility.createEntriesFromStringMap(numericTypes));
		st = puaa.getOrCreateSubtable("Numeric_Value");
		st.addAll(PuaaUtility.createEntriesFromStringMap(numericValues));
		st = puaa.getOrCreateSubtable("Bidi_Mirrored");
		st.addAll(PuaaUtility.createEntriesFromBooleanMap(bidiMirrored));
		st = puaa.getOrCreateSubtable("Unicode_1_Name");
		st.addAll(PuaaUtility.createEntriesFromNameMap(uni1Names));
		st = puaa.getOrCreateSubtable("ISO_Comment");
		st.addAll(PuaaUtility.createEntriesFromStringMap(comments));
		st = puaa.getOrCreateSubtable("Simple_Uppercase_Mapping");
		st.addAll(PuaaUtility.createEntriesFromHexadecimalMap(uppercase));
		st = puaa.getOrCreateSubtable("Simple_Lowercase_Mapping");
		st.addAll(PuaaUtility.createEntriesFromHexadecimalMap(lowercase));
		st = puaa.getOrCreateSubtable("Simple_Titlecase_Mapping");
		st.addAll(PuaaUtility.createEntriesFromHexadecimalMap(titlecase));
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable names = puaa.getSubtable("Name");
		PuaaSubtable categories = puaa.getSubtable("General_Category");
		PuaaSubtable combClasses = puaa.getSubtable("Canonical_Combining_Class");
		PuaaSubtable bidiClasses = puaa.getSubtable("Bidi_Class");
		PuaaSubtable decompTypes = puaa.getSubtable("Decomposition_Type");
		PuaaSubtable decompMappings = puaa.getSubtable("Decomposition_Mapping");
		PuaaSubtable numericTypes = puaa.getSubtable("Numeric_Type");
		PuaaSubtable numericValues = puaa.getSubtable("Numeric_Value");
		PuaaSubtable bidiMirrored = puaa.getSubtable("Bidi_Mirrored");
		PuaaSubtable uni1Names = puaa.getSubtable("Unicode_1_Name");
		PuaaSubtable comments = puaa.getSubtable("ISO_Comment");
		PuaaSubtable uppercase = puaa.getSubtable("Simple_Uppercase_Mapping");
		PuaaSubtable lowercase = puaa.getSubtable("Simple_Lowercase_Mapping");
		PuaaSubtable titlecase = puaa.getSubtable("Simple_Titlecase_Mapping");
		
		TreeMap<Integer,String[]> lines = new TreeMap<Integer,String[]>();
		if (names != null) for (PuaaSubtableEntry e : names) addLine(lines, e, 1);
		if (categories != null) for (PuaaSubtableEntry e : categories) addLine(lines, e, 2);
		if (combClasses != null) for (PuaaSubtableEntry e : combClasses) addLine(lines, e, 3);
		if (bidiClasses != null) for (PuaaSubtableEntry e : bidiClasses) addLine(lines, e, 4);
		if (decompTypes != null) for (PuaaSubtableEntry e : decompTypes) addLine(lines, e, 5);
		if (decompMappings != null) for (PuaaSubtableEntry e : decompMappings) addLine(lines, e, 5);
		if (numericTypes != null) for (PuaaSubtableEntry e : numericTypes) addLine(lines, e, 8);
		if (numericValues != null) for (PuaaSubtableEntry e : numericValues) addLine(lines, e, 8);
		if (bidiMirrored != null) for (PuaaSubtableEntry e : bidiMirrored) addLine(lines, e, 9);
		if (uni1Names != null) for (PuaaSubtableEntry e : uni1Names) addLine(lines, e, 10);
		if (comments != null) for (PuaaSubtableEntry e : comments) addLine(lines, e, 11);
		if (uppercase != null) for (PuaaSubtableEntry e : uppercase) addLine(lines, e, 12);
		if (lowercase != null) for (PuaaSubtableEntry e : lowercase) addLine(lines, e, 13);
		if (titlecase != null) for (PuaaSubtableEntry e : titlecase) addLine(lines, e, 14);
		for (String[] line : lines.values()) out.println(PuaaUtility.joinLine(line, ";"));
	}
	
	private static Integer parseInt(String[] fields, int i) {
		if (fields == null || i < 0 || i >= fields.length) return null;
		try { return Integer.parseInt(fields[i].trim(), 10); }
		catch (NumberFormatException e) { return null; }
	}
	
	private static Integer parseHex(String[] fields, int i) {
		if (fields == null || i < 0 || i >= fields.length) return null;
		try { return Integer.parseInt(fields[i].trim(), 16); }
		catch (NumberFormatException e) { return null; }
	}
	
	private static String parseString(String[] fields, int i) {
		if (fields == null || i < 0 || i >= fields.length) return null;
		String s = fields[i].trim();
		if (s.length() == 0) return null;
		return s;
	}
	
	private static String[] parseWords(String[] fields, int i) {
		if (fields == null || i < 0 || i >= fields.length) return null;
		String s = fields[i].trim();
		if (s.length() == 0) return null;
		return s.split("\\s+");
	}
	
	private static Boolean parseBoolean(String[] fields, int i) {
		if (fields == null || i < 0 || i >= fields.length) return null;
		String s = fields[i].trim();
		if (s.length() == 0) return null;
		return s.equalsIgnoreCase("Y");
	}
	
	private static int[] toIntArray(Collection<Integer> c) {
		Integer[] a = c.toArray(new Integer[c.size()]);
		int[] b = new int[a.length];
		for (int i = 0; i < a.length; i++) b[i] = a[i];
		return b;
	}
	
	private static String joinWords(List<String> strings) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (String s : strings) {
			if (s != null && s.length() > 0) {
				if (first) first = false;
				else sb.append(" ");
				sb.append(s);
			}
		}
		return sb.toString();
	}
	
	private static void addLine(TreeMap<Integer,String[]> lines, PuaaSubtableEntry e, int i) {
		for (int cp = e.firstCodePoint; cp <= e.lastCodePoint; cp++) {
			String value = e.getPropertyValue(cp);
			if (value == null || value.length() == 0) continue;
			if (lines.containsKey(cp)) {
				String[] line = lines.get(cp);
				if (line[i] == null) {
					line[i] = value;
				} else if (i == 8) {
					if (line[i].equals("Decimal")) line[6] = line[7] = line[8] = value;
					if (line[i].equals("Digit")) line[7] = line[8] = value;
					if (line[i].equals("Numeric")) line[8] = value;
				} else {
					if (i == 5) line[i] += " ";
					line[i] += value;
				}
			} else {
				String[] line = new String[15];
				line[0] = PuaaUtility.toHexString(cp);
				line[i] = value;
				lines.put(cp, line);
			}
		}
	}
}
