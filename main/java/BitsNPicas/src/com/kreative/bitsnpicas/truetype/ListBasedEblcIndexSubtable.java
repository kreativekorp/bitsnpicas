package com.kreative.bitsnpicas.truetype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class ListBasedEblcIndexSubtable<E> extends EblcIndexSubtable implements List<E> {
	private final List<E> list = new ArrayList<E>();
	
	@Override
	public boolean add(E e) {
		return list.add(e);
	}
	
	@Override
	public void add(int index, E e) {
		list.add(index, e);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return list.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return list.addAll(index, c);
	}
	
	@Override
	public void clear() {
		list.clear();
	}
	
	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}
	
	@Override
	public E get(int index) {
		return list.get(index);
	}
	
	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}
	
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return list.listIterator();
	}
	
	@Override
	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index);
	}
	
	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public E remove(int index) {
		return list.remove(index);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}
	
	@Override
	public E set(int index, E e) {
		return list.set(index, e);
	}
	
	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public List<E> subList(int start, int end) {
		return list.subList(start, end);
	}
	
	@Override
	public Object[] toArray() {
		return list.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}
}
