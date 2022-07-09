package com.kreative.unicode.data;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class GlyphList extends AbstractList<Integer> implements Comparable<GlyphList> {
	private final int[] codePoints;
	private final String name;
	private final Set<String> aliases;
	
	public GlyphList(int[] codePoints, String name) {
		this.codePoints = codePoints;
		this.name = name;
		this.aliases = Collections.emptySet();
	}
	
	public GlyphList(int[] codePoints, String name, Set<String> aliases) {
		this.codePoints = codePoints;
		this.name = name;
		this.aliases = aliases;
	}
	
	public int size() {
		return codePoints.length;
	}
	
	public Integer get(int index) {
		return codePoints[index];
	}
	
	public String getName() {
		return name;
	}
	
	public Set<String> getAliases() {
		return aliases;
	}
	
	public int compareTo(GlyphList that) {
		return UnicodeUtils.naturalCompare(this.name, that.name);
	}
	
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		if (o instanceof GlyphList) {
			GlyphList that = (GlyphList)o;
			return (
				Arrays.equals(this.codePoints, that.codePoints) &&
				this.name.equals(that.name) &&
				this.aliases.equals(that.aliases)
			);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return Arrays.hashCode(codePoints) + name.hashCode() + aliases.hashCode();
	}
	
	public String toString() {
		return name;
	}
}
