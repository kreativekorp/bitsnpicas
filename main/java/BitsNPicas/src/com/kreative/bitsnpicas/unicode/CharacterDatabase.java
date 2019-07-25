package com.kreative.bitsnpicas.unicode;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class CharacterDatabase extends AbstractMap<Integer,CharacterData> {
	private static final String[] DATABASE_FILES = new String[]{
		"UnicodeData.txt",
		"UnicodeDataUCSUR.txt"
	};
	
	private static CharacterDatabase instance = null;
	
	public static CharacterDatabase instance() {
		if (instance == null) instance = new CharacterDatabase();
		return instance;
	}
	
	private final Map<Integer,CharacterData> database;
	
	private CharacterDatabase() {
		Map<Integer,CharacterData> database = new HashMap<Integer,CharacterData>();
		for (String fileName : DATABASE_FILES) {
			Scanner scan = new Scanner(CharacterDatabase.class.getResourceAsStream(fileName));
			while (scan.hasNextLine()) {
				String line = scan.nextLine().trim();
				if (line.length() > 0 && line.charAt(0) != '#') {
					String[] f = line.split(";", -1);
					if (f.length > 14) {
						String name = f[1].trim();
						if (name.endsWith(", First>") || name.endsWith(", Last>")) {
							continue;
						} else try {
							CharacterData cd = new CharacterData(f);
							database.put(cd.codePoint, cd);
						} catch (NumberFormatException e) {
							continue;
						}
					}
				}
			}
			scan.close();
		}
		this.database = Collections.unmodifiableMap(database);
	}
	
	public boolean containsKey(Object key) {
		return database.containsKey(key);
	}
	
	public boolean containsValue(Object value) {
		return database.containsValue(value);
	}
	
	public Set<Map.Entry<Integer,CharacterData>> entrySet() {
		return database.entrySet();
	}
	
	public CharacterData get(Object key) {
		return database.get(key);
	}
	
	public boolean isEmpty() {
		return database.isEmpty();
	}
	
	public Set<Integer> keySet() {
		return database.keySet();
	}
	
	public int size() {
		return database.size();
	}
	
	public Collection<CharacterData> values() {
		return database.values();
	}
	
	public CharacterData find(String name) {
		double distance = Double.POSITIVE_INFINITY;
		CharacterData closest = null;
		for (CharacterData data : database.values()) {
			double d = searchDistance(name, data.toString());
			if (d < distance) {
				distance = d;
				closest = data;
			}
		}
		return closest;
	}
	
	private int phraseDistance(String s1, String s2) {
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
				int c1 = Character.toUpperCase(s1.charAt(i - 1));
				int c2 = Character.toUpperCase(s2.charAt(j - 1));
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
	
	private int wordDistance(String s1, String s2) {
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
	
	private double searchDistance(String s1, String s2) {
		if (s1.equalsIgnoreCase(s2)) return 0;
		double pd = phraseDistance(s1, s2);
		pd -= 0.8 * Math.abs(s1.length() - s2.length());
		double wd = wordDistance(s1, s2);
		return Math.min(pd, wd) * 0.8 + Math.max(pd, wd) * 0.2;
	}
}
