package com.kreative.bitsnpicas.edit;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import com.kreative.bitsnpicas.Font;

public class FontCodePointList extends AbstractList<Integer> {
	private final Font<?> font;
	
	public FontCodePointList(Font<?> font) {
		this.font = font;
	}
	
	@Override
	public boolean contains(Object e) {
		return font.characters(false).keySet().contains(e);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return font.characters(false).keySet().containsAll(c);
	}
	
	@Override
	public Integer get(int i) {
		return new ArrayList<Integer>(font.characters(false).keySet()).get(i);
	}
	
	@Override
	public int indexOf(Object e) {
		return new ArrayList<Integer>(font.characters(false).keySet()).indexOf(e);
	}
	
	@Override
	public boolean isEmpty() {
		return font.characters(false).keySet().isEmpty();
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return font.characters(false).keySet().iterator();
	}
	
	@Override
	public int lastIndexOf(Object e) {
		return new ArrayList<Integer>(font.characters(false).keySet()).lastIndexOf(e);
	}
	
	@Override
	public ListIterator<Integer> listIterator() {
		return new ArrayList<Integer>(font.characters(false).keySet()).listIterator();
	}
	
	@Override
	public ListIterator<Integer> listIterator(int i) {
		return new ArrayList<Integer>(font.characters(false).keySet()).listIterator(i);
	}
	
	@Override
	public int size() {
		return font.characters(false).keySet().size();
	}
	
	@Override
	public Object[] toArray() {
		return font.characters(false).keySet().toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return font.characters(false).keySet().toArray(a);
	}
	
	@Override
	public String toString() {
		return "Characters in Font";
	}
}
