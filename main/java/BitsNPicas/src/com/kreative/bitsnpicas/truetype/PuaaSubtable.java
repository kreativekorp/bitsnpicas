package com.kreative.bitsnpicas.truetype;

import java.util.ArrayList;
import java.util.BitSet;

public class PuaaSubtable extends ArrayList<PuaaSubtableEntry> {
	private static final long serialVersionUID = 1L;
	
	public String property = null;
	
	public String getPropertyValue(int cp) {
		boolean found = false;
		StringBuffer sb = new StringBuffer();
		for (PuaaSubtableEntry e : this) {
			if (e.contains(cp)) {
				String value = e.getPropertyValue(cp);
				if (value != null) {
					found = true;
					sb.append(value);
				}
			}
		}
		return found ? sb.toString() : null;
	}
	
	public boolean isSortable() {
		BitSet codePoints = new BitSet();
		for (PuaaSubtableEntry e : this) {
			if (codePoints.get(e.firstCodePoint, e.lastCodePoint + 1).isEmpty()) {
				codePoints.set(e.firstCodePoint, e.lastCodePoint + 1);
			} else {
				return false;
			}
		}
		return true;
	}
}
