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
	
	protected SortedMap<Integer,String> names = new TreeMap<Integer,String>();
	protected SortedMap<Integer,T> characters = new TreeMap<Integer,T>();
	protected SortedMap<String,T> namedGlyphs = new TreeMap<String,T>();
	protected SortedMap<GlyphPair,Integer> kernPairs = new TreeMap<GlyphPair,Integer>();
	
	public abstract int getEmAscent();
	public abstract int getEmDescent();
	public abstract int getLineAscent();
	public abstract int getLineDescent();
	public abstract int getXHeight();
	public abstract int getCapHeight();
	public abstract int getLineGap();
	
	public abstract double getEmAscent2D();
	public abstract double getEmDescent2D();
	public abstract double getLineAscent2D();
	public abstract double getLineDescent2D();
	public abstract double getXHeight2D();
	public abstract double getCapHeight2D();
	public abstract double getLineGap2D();
	
	public abstract void setEmAscent(int v);
	public abstract void setEmDescent(int v);
	public abstract void setLineAscent(int v);
	public abstract void setLineDescent(int v);
	public abstract void setXHeight(int v);
	public abstract void setCapHeight(int v);
	public abstract void setLineGap(int v);
	
	public abstract void setEmAscent2D(double v);
	public abstract void setEmDescent2D(double v);
	public abstract void setLineAscent2D(double v);
	public abstract void setLineDescent2D(double v);
	public abstract void setXHeight2D(double v);
	public abstract void setCapHeight2D(double v);
	public abstract void setLineGap2D(double v);
	
	public boolean isEmpty() {
		return characters.isEmpty() && namedGlyphs.isEmpty();
	}
	
	public boolean containsCharacter(int ch) {
		return characters.containsKey(ch);
	}
	
	public T getCharacter(int ch) {
		return characters.get(ch);
	}
	
	public T putCharacter(int ch, T fc) {
		if (fc == null) return characters.remove(ch);
		return characters.put(ch, fc);
	}
	
	public T removeCharacter(int ch) {
		return characters.remove(ch);
	}
	
	public SortedMap<Integer,T> characters(boolean copy) {
		if (copy) return new TreeMap<Integer,T>(characters);
		return Collections.unmodifiableSortedMap(characters);
	}
	
	public boolean containsNamedGlyph(String name) {
		if (name == null) return false;
		return namedGlyphs.containsKey(name);
	}
	
	public T getNamedGlyph(String name) {
		if (name == null) return null;
		return namedGlyphs.get(name);
	}
	
	public T putNamedGlyph(String name, T fc) {
		if (name == null) return null;
		if (fc == null) return namedGlyphs.remove(name);
		return namedGlyphs.put(name, fc);
	}
	
	public T removeNamedGlyph(String name) {
		if (name == null) return null;
		return namedGlyphs.remove(name);
	}
	
	public SortedMap<String,T> namedGlyphs(boolean copy) {
		if (copy) return new TreeMap<String,T>(namedGlyphs);
		return Collections.unmodifiableSortedMap(namedGlyphs);
	}
	
	public boolean containsName(int nametype) {
		return names.containsKey(nametype);
	}
	
	public String getName(int nametype) {
		return names.get(nametype);
	}
	
	public String setName(int nametype, String name) {
		if (name == null) return names.remove(nametype);
		return names.put(nametype, name);
	}
	
	public String removeName(int nametype) {
		return names.remove(nametype);
	}
	
	public SortedMap<Integer,String> names(boolean copy) {
		if (copy) return new TreeMap<Integer,String>(names);
		return Collections.unmodifiableSortedMap(names);
	}
	
	public boolean containsKernPair(GlyphPair gp) {
		if (gp == null) return false;
		return kernPairs.containsKey(gp);
	}
	
	public int getKernPair(GlyphPair gp) {
		if (gp == null) return 0;
		Integer o = kernPairs.get(gp);
		return (o != null) ? o.intValue() : 0;
	}
	
	public int setKernPair(GlyphPair gp, int offset) {
		if (gp == null) return 0;
		Integer o = kernPairs.put(gp, offset);
		return (o != null) ? o.intValue() : 0;
	}
	
	public int removeKernPair(GlyphPair gp) {
		if (gp == null) return 0;
		Integer o = kernPairs.remove(gp);
		return (o != null) ? o.intValue() : 0;
	}
	
	public SortedMap<GlyphPair,Integer> kernPairs(boolean copy) {
		if (copy) return new TreeMap<GlyphPair,Integer>(kernPairs);
		return Collections.unmodifiableSortedMap(kernPairs);
	}
	
	private static final char[] BASELINE_CHARS = "HXxZzAMNTYilmnEFIKLPRhkrvwVWBDbduftCGJOSUaceos2147035689ÞÆæÐØø¥£ß&!?.%@±".toCharArray();
	private static final char[] CAP_HEIGHT_CHARS = "HXTZAMNUVWYEFIJKLBDPRCGOQS5714023689ÞÆÐØbdhklþft¥!?&£ß%@".toCharArray();
	private static final char[] X_HEIGHT_CHARS = "xzuvwymnracegopqsµæø".toCharArray();
	
	public int guessBaselineAdjustment() {
		for (int ch : BASELINE_CHARS) {
			T fc = characters.get(ch);
			if (fc != null) return fc.getGlyphHeight() - fc.getGlyphAscent();
		}
		return 0;
	}
	
	public double guessBaselineAdjustment2D() {
		for (int ch : BASELINE_CHARS) {
			T fc = characters.get(ch);
			if (fc != null) return fc.getGlyphHeight2D() - fc.getGlyphAscent2D();
		}
		return 0;
	}
	
	public int guessCapHeight() {
		for (int ch : CAP_HEIGHT_CHARS) {
			T fc = characters.get(ch);
			if (fc != null) return fc.getGlyphAscent();
		}
		return 0;
	}
	
	public double guessCapHeight2D() {
		for (int ch : CAP_HEIGHT_CHARS) {
			T fc = characters.get(ch);
			if (fc != null) return fc.getGlyphAscent2D();
		}
		return 0;
	}
	
	public int guessXHeight() {
		for (int ch : X_HEIGHT_CHARS) {
			T fc = characters.get(ch);
			if (fc != null) return fc.getGlyphAscent();
		}
		return 0;
	}
	
	public double guessXHeight2D() {
		for (int ch : X_HEIGHT_CHARS) {
			T fc = characters.get(ch);
			if (fc != null) return fc.getGlyphAscent2D();
		}
		return 0;
	}
	
	private static final List<Integer> MONOSPACED_CLASSES = Arrays.asList(
		Integer.valueOf(Character.UPPERCASE_LETTER),
		Integer.valueOf(Character.LOWERCASE_LETTER),
		Integer.valueOf(Character.TITLECASE_LETTER),
		Integer.valueOf(Character.MODIFIER_LETTER),
		Integer.valueOf(Character.OTHER_LETTER),
		Integer.valueOf(Character.DECIMAL_DIGIT_NUMBER),
		Integer.valueOf(Character.LETTER_NUMBER),
		Integer.valueOf(Character.OTHER_NUMBER),
		Integer.valueOf(Character.DASH_PUNCTUATION),
		Integer.valueOf(Character.START_PUNCTUATION),
		Integer.valueOf(Character.END_PUNCTUATION),
		Integer.valueOf(Character.CONNECTOR_PUNCTUATION),
		Integer.valueOf(Character.OTHER_PUNCTUATION),
		Integer.valueOf(Character.MATH_SYMBOL),
		Integer.valueOf(Character.CURRENCY_SYMBOL),
		Integer.valueOf(Character.MODIFIER_SYMBOL),
		Integer.valueOf(Character.OTHER_SYMBOL),
		Integer.valueOf(Character.INITIAL_QUOTE_PUNCTUATION),
		Integer.valueOf(Character.FINAL_QUOTE_PUNCTUATION)
	);
	
	public boolean isMonospaced() {
		T space = characters.get(32);
		int monoWidth = (space != null) ? space.getCharacterWidth() : 0;
		for (Map.Entry<Integer,T> e : characters.entrySet()) {
			if (e.getValue().getCharacterWidth() != monoWidth) {
				Integer cc = Integer.valueOf(Character.getType(e.getKey()));
				if (MONOSPACED_CLASSES.contains(cc)) return false;
			}
		}
		return true;
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
			    || s.contains("SLANT")
			    || s.contains("ROTALIC");
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
	
	public void subsetRemap(Collection<GlyphPair> sr) {
		Map<Integer,T> oldChars = this.characters;
		Map<String,T> oldGlyphs = this.namedGlyphs;
		this.characters = new TreeMap<Integer,T>();
		this.namedGlyphs = new TreeMap<String,T>();
		Map<Integer,T> newChars = this.characters;
		Map<String,T> newGlyphs = this.namedGlyphs;
		for (GlyphPair gp : sr) {
			Object oldKey = gp.getLeft();
			Object newKey = gp.getRight();
			if (oldKey instanceof Integer) {
				T glyph = oldChars.get(oldKey);
				if (glyph == null) continue;
				if (newKey instanceof Integer) newChars.put((Integer)newKey, glyph);
				if (newKey instanceof String) newGlyphs.put((String)newKey, glyph);
			}
			if (oldKey instanceof String) {
				T glyph = oldGlyphs.get(oldKey);
				if (glyph == null) continue;
				if (newKey instanceof Integer) newChars.put((Integer)newKey, glyph);
				if (newKey instanceof String) newGlyphs.put((String)newKey, glyph);
			}
		}
	}
	
	public void transform(FontGlyphTransformer<T> tx) {
		for (Map.Entry<Integer,T> e : characters(true).entrySet()) {
			T glyph = tx.transformGlyph(e.getValue());
			if (glyph != null) characters.put(e.getKey(), glyph);
		}
		for (Map.Entry<String,T> e : namedGlyphs(true).entrySet()) {
			T glyph = tx.transformGlyph(e.getValue());
			if (glyph != null) namedGlyphs.put(e.getKey(), glyph);
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
