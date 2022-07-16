package com.kreative.bitsnpicas;

public class GlyphPair implements Comparable<GlyphPair> {
	private final Object left;
	private final Object right;
	
	public GlyphPair(Integer left, Integer right) {
		if (left == null || right == null) throw new IllegalArgumentException("null");
		this.left = left;
		this.right = right;
	}
	
	public GlyphPair(Integer left, String right) {
		if (left == null || right == null) throw new IllegalArgumentException("null");
		this.left = left;
		this.right = right;
	}
	
	public GlyphPair(String left, Integer right) {
		if (left == null || right == null) throw new IllegalArgumentException("null");
		this.left = left;
		this.right = right;
	}
	
	public GlyphPair(String left, String right) {
		if (left == null || right == null) throw new IllegalArgumentException("null");
		this.left = left;
		this.right = right;
	}
	
	public Object getLeft() {
		return left;
	}
	
	public Object getRight() {
		return right;
	}
	
	public int compareTo(GlyphPair that) {
		int c;
		if ((c = compare(this.left, that.left)) != 0) return c;
		if ((c = compare(this.right, that.right)) != 0) return c;
		return 0;
	}
	
	private static int compare(Object a, Object b) {
		if (a instanceof Integer) {
			if (b instanceof Integer) {
				return ((Integer)a).compareTo((Integer)b);
			} else {
				return -1;
			}
		} else {
			if (b instanceof Integer) {
				return +1;
			} else {
				return a.toString().compareTo(b.toString());
			}
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof GlyphPair) {
			GlyphPair that = (GlyphPair)o;
			return this.left.equals(that.left) && this.right.equals(that.right);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return left.hashCode() ^ right.hashCode();
	}
	
	public String toString() {
		return left.toString() + "," + right.toString();
	}
}
