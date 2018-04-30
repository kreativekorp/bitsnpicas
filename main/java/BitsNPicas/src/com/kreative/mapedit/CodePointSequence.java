package com.kreative.mapedit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodePointSequence {
	private final int[] seq;
	
	public CodePointSequence(int... seq) {
		this.seq = new int[seq.length];
		for (int i = 0; i < seq.length; i++) {
			this.seq[i] = seq[i];
		}
	}
	
	public CodePointSequence(List<Integer> seq) {
		int length = seq.size();
		this.seq = new int[length];
		for (int i = 0; i < length; i++) {
			this.seq[i] = seq.get(i);
		}
	}
	
	public CodePointSequence(String seq) {
		int length = seq.length();
		int[] tempSeq = new int[length];
		int di = 0, si = 0;
		while (di < length && si < length) {
			tempSeq[di] = seq.codePointAt(si);
			si += Character.charCount(tempSeq[di]);
			di++;
		}
		this.seq = new int[di];
		for (si = 0; si < di; si++) {
			this.seq[si] = tempSeq[si];
		}
	}
	
	public static CodePointSequence parse(String seq) {
		List<Integer> codePoints = new ArrayList<Integer>();
		String[] parts = seq.split("[+]");
		for (String part : parts) {
			if (part.startsWith("0x") || part.startsWith("0X")) {
				codePoints.add(Integer.parseInt(part.substring(2), 16));
			} else try {
				codePoints.add(Integer.parseInt(part, 16));
			} catch (NumberFormatException e) {
				MappingTag tag = MappingTag.forStringValue(part);
				if (tag != null) codePoints.add(tag.intValue);
				else throw e;
			}
		}
		return new CodePointSequence(codePoints);
	}
	
	public int[] toArray() {
		int[] out = new int[seq.length];
		for (int i = 0; i < seq.length; i++) {
			out[i] = seq[i];
		}
		return out;
	}
	
	public List<Integer> toList() {
		List<Integer> out = new ArrayList<Integer>();
		for (int ch : seq) {
			out.add(ch);
		}
		return out;
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer(seq.length);
		for (int ch : seq) {
			if (Character.isValidCodePoint(ch)) {
				out.append(Character.toChars(ch));
			}
		}
		return out.toString();
	}
	
	public static String format(CodePointSequence seq) {
		StringBuffer out = new StringBuffer(seq.seq.length);
		for (int i = 0; i < seq.seq.length; i++) {
			if (i > 0) out.append("+");
			if (Character.isValidCodePoint(seq.seq[i])) {
				String h = Integer.toHexString(seq.seq[i]).toUpperCase();
				while (h.length() < 4) h = "0" + h;
				out.append("0x" + h);
			} else {
				MappingTag tag = MappingTag.forIntValue(seq.seq[i]);
				if (tag != null) out.append(tag.stringValue);
				else throw new IllegalArgumentException();
			}
		}
		return out.toString();
	}
	
	public boolean contains(int ch) {
		for (int sch : seq) {
			if (sch == ch) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsC0C1() {
		for (int ch : seq) {
			if ((ch >= 0 && ch < 32) || (ch >= 127 && ch < 160)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsPUA() {
		for (int ch : seq) {
			if ((ch >= 0xE000 && ch < 0xF900) || (ch >= 0xF0000 && ch < 0x110000)) {
				return true;
			}
		}
		return false;
	}
	
	public int get(int i) {
		return seq[i];
	}
	
	public int length() {
		return seq.length;
	}
	
	public boolean equals(Object o) {
		if (o instanceof CodePointSequence) {
			CodePointSequence that = (CodePointSequence)o;
			return Arrays.equals(this.seq, that.seq);
		}
		return false;
	}
	
	public int hashCode() {
		return Arrays.hashCode(seq);
	}
}
