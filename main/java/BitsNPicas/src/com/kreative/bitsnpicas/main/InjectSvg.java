package com.kreative.bitsnpicas.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.BitSet;
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
					ByteArrayOutputStream inData = new ByteArrayOutputStream();
					copyAndClose(new FileInputStream(file), inData);
					TrueTypeFile ttf = new TrueTypeFile();
					ttf.decompile(inData.toByteArray());
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
						BitSet indices = getGlyphIndices(name, cmapsub, post);
						if (indices.isEmpty()) continue;
						ByteArrayOutputStream svgData = new ByteArrayOutputStream();
						copyAndClose(new FileInputStream(inputFile), svgData);
						byte[] document = null;
						int fromIndex = indices.nextSetBit(0);
						while (fromIndex >= 0) {
							int toIndex = indices.nextClearBit(fromIndex);
							SvgTableEntry e = new SvgTableEntry();
							e.startGlyphID = fromIndex;
							e.endGlyphID = toIndex - 1;
							if (document == null) {
								OutputStream out = e.getOutputStream(compressed);
								out.write(rewriteEntryData(svgData.toByteArray(), indices, cmapsub, post));
								out.flush();
								out.close();
								document = e.svgDocument;
							} else {
								e.svgDocument = document;
							}
							svg.add(e);
							fromIndex = indices.nextSetBit(toIndex);
						}
					}
					Collections.sort(svg);
					byte[] outData = ttf.compile();
					String ext = ttf.isOpenType() ? ".otf" : ".ttf";
					File outputFile = new File(file.getParent(), file.getName() + ".svg" + ext);
					FileOutputStream out = new FileOutputStream(outputFile);
					out.write(outData);
					out.flush();
					out.close();
					System.out.println("done.");
				}
			} catch (Exception e) {
				System.out.println("failed (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ").");
			}
		}
	}
	
	private static final Pattern GLYPH_ID_PATTERN =
		Pattern.compile("([\"']glyph)\\{\\{\\{([A-Za-z0-9_.:+-]+)\\}\\}\\}([\"'])");
	
	private static byte[] rewriteEntryData(byte[] data, BitSet indices, CmapSubtable cmap, PostTable post) {
		try {
			StringBuffer rs = new StringBuffer();
			String ss = new String(data, "UTF-8");
			Matcher m = GLYPH_ID_PATTERN.matcher(ss);
			while (m.find()) {
				int n = getGlyphIndex(m.group(2), indices, cmap, post);
				String r = (n > 0) ? (m.group(1) + n + m.group(3)) : m.group();
				m.appendReplacement(rs, r);
			}
			m.appendTail(rs);
			return rs.toString().getBytes("UTF-8");
		} catch (IOException e) {
			return data;
		}
	}
	
	private static final Pattern G_PATTERN = Pattern.compile("^[Gg][:+]");
	private static final Pattern C_PATTERN = Pattern.compile("^[CcUu][:+]");
	private static final Pattern N_PATTERN = Pattern.compile("^[NnPp][:+]");
	
	private static int getGlyphIndex(String s, BitSet indices, CmapSubtable cmap, PostTable post) {
		if (s.startsWith("glyph_")) {
			try { return Integer.parseInt(s.substring(6)); }
			catch (NumberFormatException nfe) { return 0; }
		}
		if (s.startsWith("char_")) {
			if (cmap == null) return 0;
			try { return cmap.getGlyphIndex(Integer.parseInt(s.substring(5), 16)); }
			catch (NumberFormatException nfe) { return 0; }
		}
		if (G_PATTERN.matcher(s).find()) {
			try { return Integer.parseInt(s.substring(2)); }
			catch (NumberFormatException nfe) { return 0; }
		}
		if (C_PATTERN.matcher(s).find()) {
			if (cmap == null) return 0;
			try { return cmap.getGlyphIndex(Integer.parseInt(s.substring(2), 16)); }
			catch (NumberFormatException nfe) { return 0; }
		}
		if (N_PATTERN.matcher(s).find()) {
			if (post == null) return 0;
			PostTableEntry pe = PostTableEntry.forCharacterName(s.substring(2));
			return post.contains(pe) ? post.indexOf(pe) : 0;
		}
		try {
			int i = Integer.parseInt(s);
			if (i < 0) return 0;
			int fromIndex = indices.nextSetBit(0);
			while (fromIndex >= 0) {
				int toIndex = indices.nextClearBit(fromIndex);
				int length = toIndex - fromIndex;
				if (i < length) return fromIndex + i;
				else i -= length;
				fromIndex = indices.nextSetBit(toIndex);
			}
			return 0;
		}
		catch (NumberFormatException nfe) {
			if (post == null) return 0;
			PostTableEntry pe = PostTableEntry.forCharacterName(s);
			return post.contains(pe) ? post.indexOf(pe) : 0;
		}
	}
	
	private static BitSet getGlyphIndices(String s, CmapSubtable cmap, PostTable post) {
		if (s.startsWith("glyph_")) {
			BitSet indices = new BitSet();
			String[] ranges = s.substring(6).split("[+]");
			for (String range : ranges) {
				try {
					String[] r = range.split("[-_]", 2);
					int a = Integer.parseInt(r[0]);
					int b = (r.length > 1) ? Integer.parseInt(r[1]) : a;
					int fromIndex = Math.min(a, b);
					int toIndex = Math.max(a, b) + 1;
					indices.set(fromIndex, toIndex);
				} catch (NumberFormatException nfe) {
					continue;
				}
			}
			return indices;
		}
		if (s.startsWith("char_")) {
			BitSet indices = new BitSet();
			if (cmap != null) {
				String[] ranges = s.substring(5).split("[+]");
				for (String range : ranges) {
					try {
						String[] r = range.split("[-_]", 2);
						int a = Integer.parseInt(r[0], 16);
						int b = (r.length > 1) ? Integer.parseInt(r[1], 16) : a;
						int fromIndex = Math.min(a, b);
						int toIndex = Math.max(a, b) + 1;
						for (int i = fromIndex; i < toIndex; i++) {
							indices.set(cmap.getGlyphIndex(i));
						}
					} catch (NumberFormatException nfe) {
						continue;
					}
				}
			}
			return indices;
		}
		BitSet indices = new BitSet();
		if (post != null) {
			PostTableEntry pe = PostTableEntry.forCharacterName(unescape(s));
			if (post.contains(pe)) indices.set(post.indexOf(pe));
		}
		return indices;
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
	
	private static void copyAndClose(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[65536]; int len;
		while ((len = in.read(buf)) >= 0) out.write(buf, 0, len);
		out.flush(); out.close(); in.close();
	}
}
