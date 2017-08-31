package com.kreative.bitsnpicas.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class GlyphListSelection {
	private final List<int[]> ranges = new ArrayList<int[]>();
	
	public void clear() {
		ranges.clear();
	}
	
	public void add(int i) {
		ranges.add(new int[]{i, i});
	}
	
	public void add(int i, int j) {
		ranges.add(new int[]{i, j});
	}
	
	public void extend(int i) {
		if (ranges.isEmpty()) ranges.add(new int[]{i, i});
		else ranges.get(ranges.size() - 1)[1] = i;
	}
	
	public int getLast() {
		if (ranges.isEmpty()) return -1;
		else return ranges.get(ranges.size() - 1)[1];
	}
	
	public boolean isEmpty() {
		return ranges.isEmpty();
	}
	
	public boolean contains(int i) {
		boolean contains = false;
		for (int[] range : ranges) {
			int j = Math.min(range[0], range[1]);
			int k = Math.max(range[0], range[1]);
			if (i >= j && i <= k) contains = !contains;
		}
		return contains;
	}
	
	public SortedSet<Integer> toSet() {
		SortedSet<Integer> s = new TreeSet<Integer>();
		for (int[] range : ranges) {
			int j = Math.min(range[0], range[1]);
			int k = Math.max(range[0], range[1]);
			for (int i = j; i <= k; i++) {
				if (s.contains(i)) s.remove(i);
				else s.add(i);
			}
		}
		return s;
	}
}
