package com.kreative.unicode.ttflib;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public abstract class TtfBase {
	private int scaler;
	private int count;
	private int searchRange;
	private int entrySelector;
	private int rangeShift;
	private List<TtfTable> tables;
	private Map<Integer,TtfTable> byTag;
	private Map<String,TtfTable> byTagString;
	
	TtfBase() {}
	
	void read(DataInputStream in, Map<Long,byte[]> dataCache) throws IOException {
		this.tables = new ArrayList<TtfTable>();
		this.byTag = new TreeMap<Integer,TtfTable>();
		this.byTagString = new TreeMap<String,TtfTable>();
		
		this.scaler = in.readInt();
		this.count = in.readShort();
		this.searchRange = in.readShort();
		this.entrySelector = in.readShort();
		this.rangeShift = in.readShort();
		
		for (int i = 0; i < this.count; i++) {
			TtfTable t = new TtfTable();
			t.readHead(in);
			this.tables.add(t);
			this.byTag.put(t.getTag(), t);
			this.byTagString.put(t.getTagString(), t);
		}
		
		for (TtfTable t : this.tables) {
			t.readBody(in, dataCache);
		}
		
		this.tables = Collections.unmodifiableList(this.tables);
		this.byTag = Collections.unmodifiableMap(this.byTag);
		this.byTagString = Collections.unmodifiableMap(this.byTagString);
	}
	
	public int getScaler() { return scaler; }
	public int getCount() { return count; }
	public int getSearchRange() { return searchRange; }
	public int getEntrySelector() { return entrySelector; }
	public int getRangeShift() { return rangeShift; }
	public List<TtfTable> getTables() { return tables; }
	public Set<Integer> getTags() { return byTag.keySet(); }
	public Set<String> getTagStrings() { return byTagString.keySet(); }
	public TtfTable getTable(int tag) { return byTag.get(tag); }
	public TtfTable getTable(String tag) { return byTagString.get(tag); }
	
	public <T> T getTableAs(Class<T> cls, int tag) {
		TtfTable t = getTable(tag);
		if (t == null) return null;
		try { return cls.getConstructor(byte[].class).newInstance(t.getData()); }
		catch (Exception e) { return null; }
	}
	
	public <T> T getTableAs(Class<T> cls, String tag) {
		TtfTable t = getTable(tag);
		if (t == null) return null;
		try { return cls.getConstructor(byte[].class).newInstance(t.getData()); }
		catch (Exception e) { return null; }
	}
}
