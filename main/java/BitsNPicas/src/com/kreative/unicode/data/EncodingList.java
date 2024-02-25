package com.kreative.unicode.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.kreative.unicode.mappings.BOM;

public class EncodingList {
	private static EncodingList instance = null;
	
	public static EncodingList instance() {
		if (instance == null) instance = new EncodingList();
		return instance;
	}
	
	private final List<Encoding> encodings;
	private final Map<String,Encoding> encodingMap;
	private final List<GlyphList> glyphLists;
	private final Map<String,GlyphList> glyphListMap;
	
	private EncodingList() {
		List<Encoding> encodings = new ArrayList<Encoding>();
		Map<String,Encoding> encodingMap = new HashMap<String,Encoding>();
		List<GlyphList> glyphLists = new ArrayList<GlyphList>();
		Map<String,GlyphList> glyphListMap = new HashMap<String,GlyphList>();
		
		for (Charset cs : Charset.availableCharsets().values()) {
			// Charsets known to not play well with the table generator.
			// Some take a long time to decode and some just break entirely.
			String name = cs.displayName().toUpperCase();
			if (name.equals("CESU-8")) continue; // no astral chars
			if (name.equals("GB18030")) continue; // slow
			if (name.startsWith("ISO-2022-")) continue; // broken
			if (name.startsWith("UTF-")) continue; // 16 slow, 32 broken
			if (name.equals("X-COMPOUND_TEXT")) continue; // so broken it just flat out crashes
			if (name.startsWith("X-EUC")) continue; // slow
			if (name.startsWith("X-IBM93")) continue; // broken
			if (name.equals("X-IBM964")) continue; // slow
			if (name.equals("X-IBM1364")) continue; // broken
			if (name.startsWith("X-ISO-2022-")) continue; // broken
			if (name.equals("X-JISAUTODETECT")) continue; // broken
			if (name.startsWith("X-UTF-")) continue; // broken
			if (name.startsWith("X-WINDOWS-5022")) continue; // broken
			if (name.startsWith("X-WINDOWS-ISO2022")) continue; // broken
			add(new Encoding(cs), encodings, encodingMap, glyphLists, glyphListMap, false);
		}
		
		for (String filename : BOM.getResourceNames()) {
			String name = UnicodeUtils.stripExtension(filename);
			InputStream res = BOM.getResource(filename);
			if (res == null) continue;
			Encoding enc = new Encoding(name, res);
			add(enc, encodings, encodingMap, glyphLists, glyphListMap, true);
		}
		
		readDirectory(
			UnicodeUtils.getTableDirectory("Mappings"),
			encodings, encodingMap, glyphLists, glyphListMap, false
		);
		
		Collections.sort(encodings);
		Collections.sort(glyphLists);
		this.encodings = Collections.unmodifiableList(encodings);
		this.encodingMap = Collections.unmodifiableMap(encodingMap);
		this.glyphLists = Collections.unmodifiableList(glyphLists);
		this.glyphListMap = Collections.unmodifiableMap(glyphListMap);
	}
	
	private static void add(
		Encoding enc,
		List<Encoding> encodings,
		Map<String,Encoding> encodingMap,
		List<GlyphList> glyphLists,
		Map<String,GlyphList> glyphListMap,
		boolean ignoreDupes
	) {
		// Ignore null.
		if (enc == null) return;
		
		// If ignoreDupes and the encoding is already in the list, ignore it.
		String normalizedName = enc.getName().replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		if (ignoreDupes && encodingMap.containsKey(normalizedName)) return;
		
		// Create list of normalized names.
		Set<String> names = new HashSet<String>();
		names.add(normalizedName);
		for (String alias : enc.getAliases()) {
			names.add(alias.replaceAll("[^A-Za-z0-9]", "").toLowerCase());
		}
		
		// Add encoding.
		encodings.add(enc);
		for (String n : names) encodingMap.put(n, enc);
		
		// Don't create glyph lists for multibyte encodings.
		if (enc.isMultiByte()) return;
		
		// Create glyph list from encoding.
		GlyphList gl = enc.toGlyphList();
		
		// Add glyph list.
		glyphLists.add(gl);
		for (String n : names) glyphListMap.put(n, gl);
	}
	
	private static void readDirectory(
		File d,
		List<Encoding> encodings,
		Map<String,Encoding> encodingMap,
		List<GlyphList> glyphLists,
		Map<String,GlyphList> glyphListMap,
		boolean ignoreDupes
	) {
		for (File f : d.listFiles()) {
			if (f.getName().startsWith(".") || f.getName().endsWith("\r")) {
				continue;
			} else if (f.isDirectory()) {
				readDirectory(f, encodings, encodingMap, glyphLists, glyphListMap, ignoreDupes);
			} else try {
				String encName = UnicodeUtils.stripExtension(f.getName());
				Encoding enc = new Encoding(encName, new FileInputStream(f));
				add(enc, encodings, encodingMap, glyphLists, glyphListMap, ignoreDupes);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	public List<Encoding> encodings() {
		return encodings;
	}
	
	public List<GlyphList> glyphLists() {
		return glyphLists;
	}
	
	public boolean containsEncoding(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return encodingMap.containsKey(nn);
	}
	
	public boolean containsGlyphList(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return glyphListMap.containsKey(nn);
	}
	
	public Encoding getEncoding(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return encodingMap.get(nn);
	}
	
	public GlyphList getGlyphList(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return glyphListMap.get(nn);
	}
	
	public int indexOfEncoding(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return encodingMap.containsKey(nn) ? encodings.indexOf(encodingMap.get(nn)) : -1;
	}
	
	public int indexOfGlyphList(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return glyphListMap.containsKey(nn) ? glyphLists.indexOf(glyphListMap.get(nn)) : -1;
	}
	
	public int lastIndexOfEncoding(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return encodingMap.containsKey(nn) ? encodings.lastIndexOf(encodingMap.get(nn)) : -1;
	}
	
	public int lastIndexOfGlyphList(String name) {
		String nn = name.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return glyphListMap.containsKey(nn) ? glyphLists.lastIndexOf(glyphListMap.get(nn)) : -1;
	}
}
