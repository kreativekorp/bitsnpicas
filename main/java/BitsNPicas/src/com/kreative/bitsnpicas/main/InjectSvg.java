package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.kreative.bitsnpicas.truetype.CmapSubtable;
import com.kreative.bitsnpicas.truetype.CmapTable;
import com.kreative.bitsnpicas.truetype.PostTable;
import com.kreative.bitsnpicas.truetype.PostTableEntry;
import com.kreative.bitsnpicas.truetype.SvgTable;
import com.kreative.bitsnpicas.truetype.SvgTableEntry;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class InjectSvg {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		// Note: Microsoft claims the SVG-in-OpenType specification supports
		// gzip-compressed SVG glyphs, and the availability of a version of
		// EmojiOne with gzip-compressed SVG glyphs appears to back this up.
		// However, I could not find any mention of gzip support in the W3C
		// specification, and I was unable to get fonts with gzip-compressed
		// SVG glyphs to work even in the latest version of Firefox (50.1.0).
		boolean compressed = false;
		for (String arg : args) {
			if (arg.equals("-z")) { compressed = true; continue; }
			if (arg.equals("-Z")) { compressed = false; continue; }
			File file = new File(arg);
			System.out.print("Processing " + file.getAbsolutePath() + "... ");
			try {
				File inputRoot = new File(file.getParent(), file.getName() + ".svg.d");
				if (!(inputRoot.exists() && inputRoot.isDirectory())) {
					System.out.println("no svg directory found.");
				} else {
					byte[] data = new byte[(int)file.length()];
					FileInputStream in = new FileInputStream(file);
					in.read(data);
					in.close();
					TrueTypeFile ttf = new TrueTypeFile();
					ttf.decompile(data);
					CmapTable cmap = (CmapTable)ttf.getByTableName("cmap");
					CmapSubtable cmapsub = (cmap == null) ? null : cmap.getBestSubtable();
					PostTable post = (PostTable)ttf.getByTableName("post");
					SvgTable svg = (SvgTable)ttf.getByTableName("SVG ");
					if (svg == null) {
						svg = new SvgTable();
						ttf.add(svg);
					} else {
						svg.clear();
					}
					for (File inputFile : inputRoot.listFiles()) {
						String name = inputFile.getName();
						int o = name.lastIndexOf('.');
						if (o <= 0) continue;
						String extension = name.substring(o + 1);
						if (!extension.equalsIgnoreCase("svg")) continue;
						name = name.substring(0, o);
						int[] gi = getGlyphIndex(name, cmapsub, post);
						if (gi == null || gi[0] <= 0 || gi[1] <= 0) continue;
						byte[] inputData = new byte[(int)inputFile.length()];
						FileInputStream inIn = new FileInputStream(inputFile);
						inIn.read(inputData);
						inIn.close();
						SvgTableEntry e = new SvgTableEntry();
						e.startGlyphID = gi[0];
						e.endGlyphID = gi[1];
						OutputStream out = e.getOutputStream(compressed);
						out.write(rewriteEntryData(inputData, gi));
						out.flush();
						out.close();
						svg.add(e);
					}
					Collections.sort(svg);
					data = ttf.compile();
					String ext = (ttf.getByTableName("CFF ") != null) ? ".otf" : ".ttf";
					File outputFile = new File(file.getParent(), file.getName() + ".svg" + ext);
					FileOutputStream out = new FileOutputStream(outputFile);
					out.write(data);
					out.flush();
					out.close();
					System.out.println("done.");
				}
			} catch (Exception e) {
				System.out.println("failed (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ").");
			}
		}
	}
	
	private static final Pattern ELEMENT_ID_GLYPH_NUMBER_PATTERN =
		Pattern.compile("\\b(id=\"glyph)\\{\\{\\{([0-9]+)\\}\\}\\}(\")");
	
	private static byte[] rewriteEntryData(byte[] data, int[] glyphIndex) {
		try {
			StringBuffer rs = new StringBuffer();
			String ss = new String(data, "UTF-8");
			Matcher m = ELEMENT_ID_GLYPH_NUMBER_PATTERN.matcher(ss);
			while (m.find()) {
				int n = glyphIndex[0] + Integer.parseInt(m.group(2));
				m.appendReplacement(rs, m.group(1) + n + m.group(3));
			}
			m.appendTail(rs);
			return rs.toString().getBytes("UTF-8");
		} catch (IOException e) {
			return data;
		}
	}
	
	private static final Pattern FILENAME_GLYPH_NUMBER_PATTERN =
		Pattern.compile("^glyph_([0-9]+)(_([0-9]+))?$");
	
	private static int[] getGlyphIndex(String s, CmapSubtable cmap, PostTable post) {
		Matcher m = FILENAME_GLYPH_NUMBER_PATTERN.matcher(s);
		if (m.matches()) {
			try {
				int n1 = Integer.parseInt(m.group(1));
				if (m.group(3) == null) return new int[]{n1,n1};
				if (m.group(3).length() == 0) return new int[]{n1,n1};
				int n2 = Integer.parseInt(m.group(3));
				return new int[]{n1,n2};
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		if (s.startsWith("char_")) {
			if (cmap == null) {
				return null;
			} else try {
				int ch = Integer.parseInt(s.substring(5), 16);
				int n = cmap.getGlyphIndex(ch);
				return new int[]{n,n};
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
		if (post == null) {
			return null;
		} else {
			PostTableEntry pe = PostTableEntry.forCharacterName(unescape(s));
			if (post.contains(pe)) {
				int n = post.indexOf(pe);
				return new int[]{n,n};
			} else {
				return null;
			}
		}
	}
	
	private static String unescape(String s) {
		StringBuffer sb = new StringBuffer();
		CharacterIterator i = new StringCharacterIterator(s);
		for (char ch = i.first(); ch != CharacterIterator.DONE; ch = i.next()) {
			switch (ch) {
			case '!':
				ch = i.next();
				if (ch == CharacterIterator.DONE) sb.append('!');
				else sb.append(Character.toLowerCase(ch));
				break;
			case '^':
				ch = i.next();
				if (ch == CharacterIterator.DONE) sb.append('^');
				else sb.append(Character.toUpperCase(ch));
				break;
			case '#':
				ch = i.next();
				if (ch == CharacterIterator.DONE) sb.append('#');
				else sb.append(Character.toTitleCase(ch));
				break;
			case '=':
				ch = i.next();
				switch (ch) {
					case CharacterIterator.DONE: sb.append('='); break;
					case 'A': case 'a': sb.append('\''); break;
					case 'B': case 'b': sb.append('\\'); break;
					case 'C': case 'c': sb.append(':'); break;
					case 'D': case 'd': sb.append('$'); break;
					case 'E': case 'e': sb.append('='); break;
					case 'F': case 'f': sb.append('/'); break;
					case 'G': case 'g': sb.append('>'); break;
					case 'H': case 'h': sb.append('?'); break;
					case 'I': case 'i': sb.append('['); break;
					case 'J': case 'j': sb.append(']'); break;
					case 'K': case 'k': sb.append(';'); break;
					case 'L': case 'l': sb.append('<'); break;
					case 'M': case 'm': sb.append('&'); break;
					case 'N': case 'n': sb.append('+'); break;
					case 'O': case 'o': sb.append('#'); break;
					case 'P': case 'p': sb.append('%'); break;
					case 'Q': case 'q': sb.append('"'); break;
					case 'R': case 'r': sb.append('^'); break;
					case 'S': case 's': sb.append('*'); break;
					case 'T': case 't': sb.append('~'); break;
					case 'U': case 'u': sb.append('_'); break;
					case 'V': case 'v': sb.append('|'); break;
					case 'W': case 'w': sb.append('`'); break;
					case 'X': case 'x': sb.append('!'); break;
					case 'Y': case 'y': sb.append('{'); break;
					case 'Z': case 'z': sb.append('}'); break;
					default: sb.append(ch); break;
				}
				break;
			default:
				sb.append(ch);
				break;
			}
		}
		return sb.toString();
	}
}
