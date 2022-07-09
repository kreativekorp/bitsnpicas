package com.kreative.unicode.data;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import com.kreative.unicode.fontmap.FontMapController;
import com.kreative.unicode.fontmap.FontMapEntry;
import com.kreative.unicode.ttflib.PuaaTable;

public class NameResolver {
	private static NameResolver baseInstance = null;
	private static Map<Font,NameResolver> fontInstances = null;
	
	public static NameResolver instance() {
		if (baseInstance == null) baseInstance = new NameResolver();
		return baseInstance;
	}
	
	public static NameResolver instance(Font font) {
		if (font == null) return instance();
		if (fontInstances == null) fontInstances = new HashMap<Font,NameResolver>();
		if (fontInstances.containsKey(font)) return fontInstances.get(font);
		NameResolver instance = new NameResolver(instance(), font);
		fontInstances.put(font, instance);
		return instance;
	}
	
	public static NameResolver instance(String codePoints) {
		FontMapEntry e = FontMapController.getInstance().entryForString(codePoints);
		return (e == null) ? instance() : instance(e.getFont());
	}
	
	public static NameResolver instance(int codePoint) {
		FontMapEntry e = FontMapController.getInstance().entryForCodePoint(codePoint);
		return (e == null) ? instance() : instance(e.getFont());
	}
	
	private final Map<Integer,String> baseCategoryMap;
	private final Map<Integer,String> baseCombClassMap;
	private final SortedMap<Integer,String> baseNameMap;
	private final Map<Integer,String> baseUni1NameMap;
	private final Map<Integer,String> categoryMap;
	private final Map<Integer,String> combClassMap;
	private final Map<Integer,String> nameMap;
	private final Map<Integer,String> uni1NameMap;
	
	private NameResolver() {
		PuaaTable base = PuaaCache.getPuaaTable("unidata.ucd");
		this.baseCategoryMap = base.getPropertyMap("General_Category");
		this.baseCombClassMap = base.getPropertyMap("Canonical_Combining_Class");
		this.baseNameMap = base.getPropertySortedMap("Name");
		this.baseUni1NameMap = base.getPropertyMap("Unicode_1_Name");
		this.categoryMap = this.baseCategoryMap;
		this.combClassMap = this.baseCombClassMap;
		this.nameMap = this.baseNameMap;
		this.uni1NameMap = this.baseUni1NameMap;
	}
	
	private NameResolver(NameResolver base, Font font) {
		this.baseCategoryMap = base.baseCategoryMap;
		this.baseCombClassMap = base.baseCombClassMap;
		this.baseNameMap = base.baseNameMap;
		this.baseUni1NameMap = base.baseUni1NameMap;
		
		PuaaTable puaa = PuaaCache.getPuaaTable(font);
		if (puaa == null) {
			this.categoryMap = this.baseCategoryMap;
			this.combClassMap = this.baseCombClassMap;
			this.nameMap = this.baseNameMap;
			this.uni1NameMap = this.baseUni1NameMap;
		} else {
			this.categoryMap = new HashMap<Integer,String>();
			this.combClassMap = new HashMap<Integer,String>();
			this.nameMap = new HashMap<Integer,String>();
			this.uni1NameMap = new HashMap<Integer,String>();
			if (baseCategoryMap != null) categoryMap.putAll(baseCategoryMap);
			if (baseCombClassMap != null) combClassMap.putAll(baseCombClassMap);
			if (baseNameMap != null) nameMap.putAll(baseNameMap);
			if (baseUni1NameMap != null) uni1NameMap.putAll(baseUni1NameMap);
			Map<Integer,String> puaaCategoryMap = puaa.getPropertyMap("General_Category");
			Map<Integer,String> puaaCombClassMap = puaa.getPropertyMap("Canonical_Combining_Class");
			Map<Integer,String> puaaNameMap = puaa.getPropertyMap("Name");
			Map<Integer,String> puaaUni1NameMap = puaa.getPropertyMap("Unicode_1_Name");
			if (puaaCategoryMap != null) categoryMap.putAll(puaaCategoryMap);
			if (puaaCombClassMap != null) combClassMap.putAll(puaaCombClassMap);
			if (puaaNameMap != null) nameMap.putAll(puaaNameMap);
			if (puaaUni1NameMap != null) uni1NameMap.putAll(puaaUni1NameMap);
		}
	}
	
	public String getCategory(int cp) {
		if (categoryMap == null) return null;
		String category = categoryMap.get(cp);
		if (category != null) return category;
		try {
			int prevcp = baseNameMap.headMap(cp).lastKey();
			int nextcp = baseNameMap.tailMap(cp).firstKey();
			String prevName = baseNameMap.get(prevcp);
			String nextName = baseNameMap.get(nextcp);
			if (isRangePair(prevName, nextName)) {
				String prevcat = baseCategoryMap.get(prevcp);
				String nextcat = baseCategoryMap.get(nextcp);
				if (prevcat.equals(nextcat)) return prevcat;
			}
			return "Cn";
		} catch (Exception e) {
			return "Cn";
		}
	}
	
	public String getCombiningClass(int cp) {
		if (combClassMap == null) return null;
		String combClass = combClassMap.get(cp);
		if (combClass != null) return combClass;
		try {
			int prevcp = baseNameMap.headMap(cp).lastKey();
			int nextcp = baseNameMap.tailMap(cp).firstKey();
			String prevName = baseNameMap.get(prevcp);
			String nextName = baseNameMap.get(nextcp);
			if (isRangePair(prevName, nextName)) {
				String prevccc = baseCombClassMap.get(prevcp);
				String nextccc = baseCombClassMap.get(nextcp);
				if (prevccc.equals(nextccc)) return prevccc;
			}
			return "0";
		} catch (Exception e) {
			return "0";
		}
	}
	
	public String getName(int cp) {
		if (nameMap == null) return null;
		String name = nameMap.get(cp);
		if (name != null) {
			if (!(name.startsWith("<") && name.endsWith(">"))) return name;
			if (name.equals("<control>")) {
				name = uni1NameMap.get(cp);
				if (name != null) return name;
				return "CONTROL-" + toHexString(cp);
			}
		} else {
			try {
				int prevcp = baseNameMap.headMap(cp).lastKey();
				int nextcp = baseNameMap.tailMap(cp).firstKey();
				String prevName = baseNameMap.get(prevcp);
				String nextName = baseNameMap.get(nextcp);
				if (isRangePair(prevName, nextName)) {
					name = prevName;
				} else {
					return "UNDEFINED-" + toHexString(cp);
				}
			} catch (Exception e) {
				return "UNDEFINED-" + toHexString(cp);
			}
		}
		if (name.contains("CJK Ideograph")) return "CJK UNIFIED IDEOGRAPH-" + toHexString(cp);
		if (name.contains("Tangut Ideograph")) return "TANGUT IDEOGRAPH-" + toHexString(cp);
		if (name.contains("High Surrogate")) return "HIGH SURROGATE-" + toHexString(cp);
		if (name.contains("Private Use")) return "PRIVATE USE-" + toHexString(cp);
		return name.replaceAll("^<|, (Fir|La)st>$", "").toUpperCase() + "-" + toHexString(cp);
	}
	
	private static boolean isRangePair(String a, String b) {
		return (
			a.startsWith("<") && a.endsWith(", First>") &&
			b.startsWith("<") && b.endsWith(", Last>") &&
			a.substring(0, a.length() - 6).equals(b.substring(0, b.length() - 5))
		);
	}
	
	private static String toHexString(int cp) {
		String h = Integer.toHexString(cp).toUpperCase();
		if (h.length() < 4) h = ("0000" + h).substring(h.length());
		return h;
	}
}
