package com.kreative.bitsnpicas.truetype;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class MapBasedTable<K,V> extends TrueTypeTable implements Map<K,V> {
	private final Map<K,V> map = new HashMap<K,V>();
	
	@Override
	public void clear() {
		map.clear();
	}
	
	@Override
	public boolean containsKey(Object k) {
		return map.containsKey(k);
	}
	
	@Override
	public boolean containsValue(Object v) {
		return map.containsValue(v);
	}
	
	@Override
	public Set<Map.Entry<K,V>> entrySet() {
		return map.entrySet();
	}
	
	@Override
	public V get(Object k) {
		return map.get(k);
	}
	
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	@Override
	public Set<K> keySet() {
		return map.keySet();
	}
	
	@Override
	public V put(K k, V v) {
		return map.put(k, v);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}
	
	@Override
	public V remove(Object k) {
		return map.remove(k);
	}
	
	@Override
	public int size() {
		return map.size();
	}
	
	@Override
	public Collection<V> values() {
		return map.values();
	}
}
