package com.kreative.keyedit;

import java.util.LinkedHashMap;
import java.util.Map;

public class DeadKeyTable {
	public int winTerminator;
	public int macTerminator;
	public String macStateId;
	public int xkbOutput;
	public XkbDeadKey xkbDeadKey;
	public final Map<Integer,Integer> keyMap;
	
	public DeadKeyTable() {
		this.winTerminator = -1;
		this.macTerminator = -1;
		this.macStateId = null;
		this.xkbOutput = -1;
		this.xkbDeadKey = null;
		this.keyMap = new LinkedHashMap<Integer,Integer>();
	}
	
	public DeadKeyTable(int u) {
		this.winTerminator = u;
		this.macTerminator = u;
		this.macStateId = XkbKeySym.MAP.getKeySym(u);
		this.xkbOutput = u;
		this.xkbDeadKey = XkbDeadKey.forUnicode(u);
		this.keyMap = new LinkedHashMap<Integer,Integer>();
	}
	
	public void setTerminator(int u) {
		this.winTerminator = u;
		this.macTerminator = u;
		this.macStateId = XkbKeySym.MAP.getKeySym(u);
		this.xkbOutput = u;
		this.xkbDeadKey = XkbDeadKey.forUnicode(u);
	}
}
