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
		"UnicodeData.txt", "UnicodeDataUCSUR.txt"
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
}
