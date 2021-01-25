package com.kreative.bitsnpicas.truetype;

public abstract class PuaaSubtableEntry {
	public static final int SINGLE = 1;
	public static final int MULTIPLE = 2;
	public static final int BOOLEAN = 3;
	public static final int DECIMAL = 4;
	public static final int HEXADECIMAL = 5;
	public static final int HEXMULTIPLE = 6;
	public static final int HEXSEQUENCE = 7;
	public static final int CASEMAPPING = 8;
	public static final int NAMEALIAS = 9;
	
	public int firstCodePoint = 0;
	public int lastCodePoint = 0;
	
	public boolean contains(int cp) {
		return (cp >= firstCodePoint && cp <= lastCodePoint);
	}
	
	public abstract String getPropertyValue(int cp);
	
	public static class Single extends PuaaSubtableEntry {
		public String value = null;
		
		@Override
		public String getPropertyValue(int cp) {
			return value;
		}
	}
	
	public static class Multiple extends PuaaSubtableEntry {
		public String[] values = new String[0];
		
		@Override
		public String getPropertyValue(int cp) {
			if (values == null) return null;
			return values[cp - firstCodePoint];
		}
	}
	
	public static class Boolean extends PuaaSubtableEntry {
		public boolean value = false;
		
		@Override
		public String getPropertyValue(int cp) {
			return value ? "Y" : "N";
		}
	}
	
	public static class Decimal extends PuaaSubtableEntry {
		public int value = 0;
		
		@Override
		public String getPropertyValue(int cp) {
			return Integer.toString(value);
		}
	}
	
	public static class Hexadecimal extends PuaaSubtableEntry {
		public int value = 0;
		
		@Override
		public String getPropertyValue(int cp) {
			String s = Integer.toHexString(value).toUpperCase();
			while (s.length() < 4) s = "0" + s;
			return s;
		}
	}
	
	public static class HexMultiple extends PuaaSubtableEntry {
		public int[] values = new int[0];
		
		@Override
		public String getPropertyValue(int cp) {
			if (values == null) return null;
			int value = values[cp - firstCodePoint];
			String s = Integer.toHexString(value).toUpperCase();
			while (s.length() < 4) s = "0" + s;
			return s;
		}
	}
	
	public static class HexSequence extends PuaaSubtableEntry {
		public int[] values = new int[0];
		
		@Override
		public String getPropertyValue(int cp) {
			if (values == null) return null;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < values.length; i++) {
				if (i > 0) sb.append(" ");
				String s = Integer.toHexString(values[i]).toUpperCase();
				while (s.length() < 4) s = "0" + s;
				sb.append(s);
			}
			return sb.toString();
		}
	}
	
	public static class CaseMapping extends PuaaSubtableEntry {
		public int[] values = new int[0];
		public String condition = null;
		
		@Override
		public String getPropertyValue(int cp) {
			if (values == null) return null;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < values.length; i++) {
				if (i > 0) sb.append(" ");
				String s = Integer.toHexString(values[i]).toUpperCase();
				while (s.length() < 4) s = "0" + s;
				sb.append(s);
			}
			if (condition != null && condition.length() > 0) {
				sb.append("; ");
				sb.append(condition);
			}
			return sb.toString();
		}
	}
	
	public static class NameAlias extends PuaaSubtableEntry {
		public String alias = null;
		public String type = null;
		
		@Override
		public String getPropertyValue(int cp) {
			if (alias == null || type == null) return null;
			return alias + ";" + type;
		}
	}
}
