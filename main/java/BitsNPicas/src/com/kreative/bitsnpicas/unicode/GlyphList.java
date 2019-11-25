package com.kreative.bitsnpicas.unicode;

import java.util.AbstractList;
import java.util.Arrays;

public class GlyphList extends AbstractList<Integer> implements Comparable<GlyphList> {
	private final int[] codePoints;
	public final String name;
	
	public GlyphList(int[] codePoints, String name) {
		this.codePoints = codePoints;
		this.name = name;
	}
	
	public int size() {
		return codePoints.length;
	}
	
	public Integer get(int index) {
		return codePoints[index];
	}
	
	public int compareTo(GlyphList that) {
		return this.name.compareTo(that.name);
	}
	
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		if (o instanceof GlyphList) {
			GlyphList that = (GlyphList)o;
			return (
				Arrays.equals(this.codePoints, that.codePoints) &&
				this.name.equals(that.name)
			);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return Arrays.hashCode(codePoints) + name.hashCode();
	}
	
	public String toString() {
		return name;
	}
}
