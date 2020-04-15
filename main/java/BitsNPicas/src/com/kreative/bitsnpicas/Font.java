package com.kreative.bitsnpicas;

import java.text.*;
import java.util.*;

public abstract class Font<T extends FontGlyph> {
	/*
	 *          line , ------------------------------------------------------------ <- previous line's line descent
	 *           gap ` ------------------------------------------------------------ <- line ascent
	 *        /        ------------------------------------------------------------ <- em ascent
	 *       |       ^   A         i              M   M           k
	 *       |       | __A________________________MM_MM___________k________________ <- x-height
	 *       |    em |  A A  mm m  i  ggg  aaa    M M M  oo  nnn  k k  eee y  y  ss
	 *       |  size |  AAA  m m m i g  g a  a    M   M o  o n  n kk  e e  y  y  s
	 * line <        | A   A m m m i  ggg  aaa    M   M  oo  n  n k k  eee  yyy ss
	 * height|       | =================g=====================================y==== <- baseline
	 *       |       v                gg                                    yy
	 *       |         ------------------------------------------------------------ <- em descent
	 *       |  line , ------------------------------------------------------------ <- line descent
	 *        \  gap ` ------------------------------------------------------------ <- next line's line ascent
	 */
	
	public static final int NAME_COPYRIGHT = 0;
	public static final int NAME_FAMILY = 1;
	public static final int NAME_STYLE = 2;
	public static final int NAME_UNIQUE_ID = 3;
	public static final int NAME_FAMILY_AND_STYLE = 4;
	public static final int NAME_VERSION = 5;
	public static final int NAME_POSTSCRIPT = 6;
	public static final int NAME_TRADEMARK = 7;
	public static final int NAME_MANUFACTURER = 8;
	public static final int NAME_DESIGNER = 9;
	public static final int NAME_DESCRIPTION = 10;
	public static final int NAME_VENDOR_URL = 11;
	public static final int NAME_DESIGNER_URL = 12;
	public static final int NAME_LICENSE_DESCRIPTION = 13;
	public static final int NAME_LICENSE_URL = 14;
	public static final int NAME_WINDOWS_FAMILY = 16;
	public static final int NAME_WINDOWS_STYLE = 17;
	public static final int NAME_MACOS_FAMILY_AND_STYLE = 18;
	public static final int NAME_SAMPLE_TEXT = 19;
	public static final int NAME_POSTSCRIPT_CID = 20;
	public static final int NAME_WWS_FAMILY = 21;
	public static final int NAME_WWS_STYLE = 22;
	
	protected Map<Integer,String> names = new HashMap<Integer,String>();
	protected Map<Integer,T> characters = new HashMap<Integer,T>();
	
	public abstract int getEmAscent();
	public abstract int getEmDescent();
	public abstract int getLineAscent();
	public abstract int getLineDescent();
	public abstract int getXHeight();
	public abstract int getLineGap();
	
	public abstract double getEmAscent2D();
	public abstract double getEmDescent2D();
	public abstract double getLineAscent2D();
	public abstract double getLineDescent2D();
	public abstract double getXHeight2D();
	public abstract double getLineGap2D();
	
	public abstract void setEmAscent(int v);
	public abstract void setEmDescent(int v);
	public abstract void setLineAscent(int v);
	public abstract void setLineDescent(int v);
	public abstract void setXHeight(int v);
	public abstract void setLineGap(int v);
	
	public abstract void setEmAscent2D(double v);
	public abstract void setEmDescent2D(double v);
	public abstract void setLineAscent2D(double v);
	public abstract void setLineDescent2D(double v);
	public abstract void setXHeight2D(double v);
	public abstract void setLineGap2D(double v);
	
	public boolean isEmpty() {
		return characters.isEmpty();
	}
	
	public boolean containsCharacter(int ch) {
		return characters.containsKey(ch);
	}
	
	public T getCharacter(int ch) {
		return characters.get(ch);
	}
	
	public T putCharacter(int ch, T fc) {
		return characters.put(ch, fc);
	}
	
	public T removeCharacter(int ch) {
		return characters.remove(ch);
	}
	
	public int[] codePoints() {
		int[] arr = new int[characters.size()]; int i = 0;
		for (int cp : characters.keySet()) arr[i++] = cp;
		return arr;
	}
	
	public List<Integer> codePointList() {
		List<Integer> list = new ArrayList<Integer>();
		for (int cp : characters.keySet()) list.add(cp);
		return list;
	}
	
	public Iterator<Integer> codePointIterator() {
		return characters.keySet().iterator();
	}
	
	public Iterator<Map.Entry<Integer,T>> characterIterator() {
		return characters.entrySet().iterator();
	}
	
	public boolean containsName(int nametype) {
		return names.containsKey(nametype);
	}
	
	public String getName(int nametype) {
		return names.get(nametype);
	}
	
	public void setName(int nametype, String name) {
		names.put(nametype, name);
	}
	
	public void removeName(int nametype) {
		names.remove(nametype);
	}
	
	public int[] nameTypes() {
		Integer[] nt = names.keySet().toArray(new Integer[0]);
		int[] nt2 = new int[nt.length];
		for (int i = 0; i < nt.length; i++) nt2[i] = nt[i];
		return nt2;
	}
	
	public Iterator<Integer> nameTypeIterator() {
		return names.keySet().iterator();
	}
	
	public Iterator<Map.Entry<Integer,String>> nameIterator() {
		return names.entrySet().iterator();
	}
	
	public boolean isBoldStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).toUpperCase();
			return s.contains("BOLD")
			    || s.contains("BLACK")
			    || s.contains("HEAVY");
		} else {
			return false;
		}
	}
	
	public boolean isItalicStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).toUpperCase();
			return s.contains("ITALIC")
			    || s.contains("OBLIQUE")
			    || s.contains("SLANT");
		} else {
			return false;
		}
	}
	
	public boolean isUnderlineStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).toUpperCase();
			return s.contains("UNDERLINE")
			    || s.contains("UNDERSCORE");
		} else {
			return false;
		}
	}
	
	public boolean isOutlineStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).toUpperCase();
			return s.contains("OUTLINE");
		} else {
			return false;
		}
	}
	
	public boolean isShadowStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).toUpperCase();
			return s.contains("SHADOW");
		} else {
			return false;
		}
	}
	
	public boolean isCondensedStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).toUpperCase();
			return s.contains("CONDENSE")
			    || s.contains("NARROW");
		} else {
			return false;
		}
	}
	
	public boolean isExtendedStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).toUpperCase();
			return s.contains("EXTEND")
			    || s.contains("EXPAND")
			    || s.contains("WIDE");
		} else {
			return false;
		}
	}
	
	public boolean isNegativeStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).toUpperCase();
			return s.contains("NEGATIVE")
			    || s.contains("REVERSE")
			    || s.contains("INVERSE")
			    || s.contains("INVERT");
		} else {
			return false;
		}
	}
	
	public boolean isStrikeoutStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).toUpperCase();
			return s.contains("STRIKEOUT")
			    || s.contains("STRIKETHR");
		} else {
			return false;
		}
	}
	
	public boolean isRegularStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).trim();
			return s.equalsIgnoreCase("")
			    || s.equalsIgnoreCase("PLAIN")
			    || s.equalsIgnoreCase("REGULAR")
			    || s.equalsIgnoreCase("NORMAL")
			    || s.equalsIgnoreCase("MEDIUM");
		} else {
			return false;
		}
	}
	
	public boolean isObliqueStyle() {
		if (names.containsKey(NAME_STYLE)) {
			String s = names.get(NAME_STYLE).toUpperCase();
			return s.contains("OBLIQUE")
			    || s.contains("SLANT");
		} else {
			return false;
		}
	}
	
	public int getMacStyle() {
		int s = 0;
		if (isBoldStyle())      s |= 0x01;
		if (isItalicStyle())    s |= 0x02;
		if (isUnderlineStyle()) s |= 0x04;
		if (isOutlineStyle())   s |= 0x08;
		if (isShadowStyle())    s |= 0x10;
		if (isCondensedStyle()) s |= 0x20;
		if (isExtendedStyle())  s |= 0x40;
		return s;
	}
	
	public int getFsSelection() {
		int s = 0;
		if (isItalicStyle())    s |= 0x0001;
		if (isUnderlineStyle()) s |= 0x0002;
		if (isNegativeStyle())  s |= 0x0004;
		if (isOutlineStyle())   s |= 0x0008;
		if (isStrikeoutStyle()) s |= 0x0010;
		if (isBoldStyle())      s |= 0x0020;
		if (isRegularStyle())   s |= 0x0040;
		// useTypoMetrics       s |= 0x0080;
		// weightWidthSlope     s |= 0x0100;
		if (isObliqueStyle())   s |= 0x0200;
		return s;
	}
	
	public void autoFillNames() {
		String fname = names.containsKey(NAME_FAMILY) ? names.get(NAME_FAMILY) : "Untitled";
		if (names.containsKey(NAME_STYLE)) {
			String sname = names.get(NAME_STYLE).trim();
			if (!(
					sname.equalsIgnoreCase("") ||
					sname.equalsIgnoreCase("Plain") ||
					sname.equalsIgnoreCase("Regular") ||
					sname.equalsIgnoreCase("Normal") ||
					sname.equalsIgnoreCase("Medium")
			)) fname += " " + sname;
		}
		StringBuffer pnb = new StringBuffer();
		CharacterIterator ci = new StringCharacterIterator(fname);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			if (ch > 0x20 && ch < 0x7F && Character.isLetterOrDigit(ch)) {
				pnb.append(ch);
			}
		}
		String pname = pnb.toString();

		if (!names.containsKey(NAME_FAMILY)) {
			names.put(NAME_FAMILY, "Untitled");
		}
		if (!names.containsKey(NAME_STYLE)) {
			names.put(NAME_STYLE, "Regular");
		}
		if (!names.containsKey(NAME_UNIQUE_ID)) {
			names.put(NAME_UNIQUE_ID, "BitsNPicas: "+pname+": "+(new GregorianCalendar().get(Calendar.YEAR)));
		}
		if (!names.containsKey(NAME_FAMILY_AND_STYLE)) {
			names.put(NAME_FAMILY_AND_STYLE, fname);
		}
		if (!names.containsKey(NAME_VERSION)) {
			names.put(NAME_VERSION, "Version 1.0");
		}
		if (!names.containsKey(NAME_POSTSCRIPT)) {
			names.put(NAME_POSTSCRIPT, pname);
		}
		if (!names.containsKey(NAME_MANUFACTURER)) {
			names.put(NAME_MANUFACTURER, "Made with Bits'n'Picas by Kreative Software");
		}
		if (!names.containsKey(NAME_VENDOR_URL)) {
			names.put(NAME_VENDOR_URL, "http://www.kreativekorp.com/software/bitsnpicas/");
		}
	}
	
	public void transform(FontGlyphTransformer<T> tx) {
		List<Integer> v = new Vector<Integer>();
		v.addAll(characters.keySet());
		for (int ch : v) {
			T glyph = characters.get(ch);
			glyph = tx.transformGlyph(glyph);
			if (glyph != null)
				characters.put(ch, glyph);
		}
	}
	
	public String toString() {
		if (names.containsKey(NAME_FAMILY_AND_STYLE)) {
			return names.get(NAME_FAMILY_AND_STYLE);
		}
		if (names.containsKey(NAME_FAMILY)) {
			if (names.containsKey(NAME_STYLE)) {
				return names.get(NAME_FAMILY) + " " + names.get(NAME_STYLE);
			} else {
				return names.get(NAME_FAMILY);
			}
		}
		if (names.containsKey(NAME_WINDOWS_FAMILY)) {
			if (names.containsKey(NAME_WINDOWS_STYLE)) {
				return names.get(NAME_WINDOWS_FAMILY) + " " + names.get(NAME_WINDOWS_STYLE);
			} else {
				return names.get(NAME_WINDOWS_FAMILY);
			}
		}
		if (names.containsKey(NAME_MACOS_FAMILY_AND_STYLE)) {
			return names.get(NAME_MACOS_FAMILY_AND_STYLE);
		}
		if (names.containsKey(NAME_WWS_FAMILY)) {
			if (names.containsKey(NAME_WWS_STYLE)) {
				return names.get(NAME_WWS_FAMILY) + " " + names.get(NAME_WWS_STYLE);
			} else {
				return names.get(NAME_WWS_FAMILY);
			}
		}
		if (names.containsKey(NAME_POSTSCRIPT)) {
			return names.get(NAME_POSTSCRIPT);
		}
		return "Untitled";
	}
}
