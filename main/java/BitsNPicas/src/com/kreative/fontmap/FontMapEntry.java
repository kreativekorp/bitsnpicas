package com.kreative.fontmap;

import java.awt.Font;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import javax.swing.JLabel;

public class FontMapEntry {
	private BitSet codePoints;
	private Font font;
	
	public FontMapEntry() {
		this.codePoints = new BitSet();
		this.font = new JLabel().getFont();
	}
	
	public BitSet codePoints() {
		return this.codePoints;
	}
	
	public String getCodePointsString() {
		int[] lastRange = null;
		List<int[]> ranges = new ArrayList<int[]>();
		for (int i = 0; (i = codePoints.nextSetBit(i)) >= 0; i++) {
			if (lastRange != null && lastRange[1] == (i - 1)) lastRange[1]++;
			else ranges.add(lastRange = new int[]{i, i});
		}
		boolean first = true;
		StringBuffer sb = new StringBuffer();
		for (int[] range : ranges) {
			if (first) first = false;
			else sb.append(", ");
			sb.append(toHexString(range[0]));
			if (range[0] != range[1]) {
				sb.append("-");
				sb.append(toHexString(range[1]));
			}
		}
		return sb.toString();
	}
	
	public void setCodePointsString(String s) {
		codePoints.clear();
		for (String r : s.split(",")) {
			if (r.contains("-")) {
				String[] p = r.split("-", 2);
				try {
					int i = parseHexInt(p[0]);
					int j = parseHexInt(p[1]);
					codePoints.set(i, j + 1);
				} catch (NumberFormatException nfe) {
					// ignored
				}
			} else {
				try {
					int i = parseHexInt(r);
					codePoints.set(i);
				} catch (NumberFormatException nfe) {
					// ignored
				}
			}
		}
	}
	
	public Font getFont() {
		return this.font;
	}
	
	public void setFont(Font font) {
		this.font = font;
	}
	
	public String getFontString() {
		return font.getFamily();
	}
	
	public void setFontString(String name) {
		font = new Font(name, font.getStyle(), font.getSize());
	}
	
	public boolean containsAllCodePoints(String s) {
		int i = 0, n = s.length();
		while (i < n) {
			int cp = s.codePointAt(i);
			if (!codePoints.get(cp)) return false;
			i += Character.charCount(cp);
		}
		return true;
	}
	
	public boolean containsAnyCodePoints(String s) {
		int i = 0, n = s.length();
		while (i < n) {
			int cp = s.codePointAt(i);
			if (codePoints.get(cp)) return true;
			i += Character.charCount(cp);
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FontMapEntry) {
			FontMapEntry that = (FontMapEntry)o;
			return this.codePoints.equals(that.codePoints)
			    && this.font.equals(that.font);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return codePoints.hashCode() ^ font.hashCode();
	}
	
	@Override
	public String toString() {
		return getCodePointsString() + " -> " + getFontString();
	}
	
	private int parseHexInt(String s) {
		s = s.trim();
		if (s.startsWith("U+") || s.startsWith("u+")) {
			s = s.substring(2).trim();
		}
		return Integer.parseInt(s, 16);
	}
	
	private String toHexString(int i) {
		String h = Integer.toHexString(i).toUpperCase();
		while (h.length() < 4) h = "0" + h;
		return h;
	}
}
