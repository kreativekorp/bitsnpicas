package com.kreative.bitsnpicas.edit;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import com.kreative.bitsnpicas.Font;

public class FontCodePointList extends AbstractList<Integer> {
	private final Font<?> font;
	
	public FontCodePointList(Font<?> font) {
		this.font = font;
	}
	
	private List<Integer> codePoints() {
		List<Integer> arr = font.codePointList();
		Collections.sort(arr);
		return arr;
	}
	
	@Override
	public boolean contains(Object e) {
		return codePoints().contains(e);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return codePoints().containsAll(c);
	}
	
	@Override
	public Integer get(int i) {
		return codePoints().get(i);
	}
	
	@Override
	public int indexOf(Object e) {
		return codePoints().indexOf(e);
	}
	
	@Override
	public boolean isEmpty() {
		return font.isEmpty();
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return codePoints().iterator();
	}
	
	@Override
	public int lastIndexOf(Object e) {
		return codePoints().lastIndexOf(e);
	}
	
	@Override
	public ListIterator<Integer> listIterator() {
		return codePoints().listIterator();
	}
	
	@Override
	public ListIterator<Integer> listIterator(int i) {
		return codePoints().listIterator(i);
	}
	
	@Override
	public int size() {
		return codePoints().size();
	}
	
	@Override
	public Object[] toArray() {
		return codePoints().toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return codePoints().toArray(a);
	}
	
	@Override
	public String toString() {
		return "Characters in Font";
	}
}
