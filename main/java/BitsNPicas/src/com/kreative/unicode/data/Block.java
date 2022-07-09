package com.kreative.unicode.data;

import java.util.AbstractList;

public class Block extends AbstractList<Integer> implements Comparable<Block> {
	public final int firstCodePoint;
	public final int lastCodePoint;
	public final String name;
	
	public Block(int first, int last, String name) {
		this.firstCodePoint = first;
		this.lastCodePoint = last;
		this.name = name;
	}
	
	public int size() {
		return lastCodePoint - firstCodePoint + 1;
	}
	
	public Integer get(int index) {
		return firstCodePoint + index;
	}
	
	public boolean contains(Object o) {
		if (o instanceof Integer) {
			int cp = (Integer)o;
			return cp >= firstCodePoint && cp <= lastCodePoint;
		} else {
			return false;
		}
	}
	
	public int indexOf(Object o) {
		if (o instanceof Integer) {
			int cp = (Integer)o;
			if (cp >= firstCodePoint && cp <= lastCodePoint) {
				return cp - firstCodePoint;
			}
		}
		return -1;
	}
	
	public int lastIndexOf(Object o) {
		if (o instanceof Integer) {
			int cp = (Integer)o;
			if (cp >= firstCodePoint && cp <= lastCodePoint) {
				return cp - firstCodePoint;
			}
		}
		return -1;
	}
	
	public int compareTo(Block that) {
		if (this.firstCodePoint != that.firstCodePoint) {
			return this.firstCodePoint - that.firstCodePoint;
		} else if (that.lastCodePoint != this.lastCodePoint) {
			return that.lastCodePoint - this.lastCodePoint;
		} else {
			return UnicodeUtils.naturalCompare(this.name, that.name);
		}
	}
	
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		if (o instanceof Block) {
			Block that = (Block)o;
			return (
				this.firstCodePoint == that.firstCodePoint &&
				this.lastCodePoint == that.lastCodePoint &&
				this.name.equals(that.name)
			);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return firstCodePoint + lastCodePoint + name.hashCode(); 
	}
	
	public String toString() {
		return name;
	}
}
