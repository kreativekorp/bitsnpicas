package com.kreative.bitsnpicas.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import com.kreative.bitsnpicas.truetype.CmapSubtable;
import com.kreative.bitsnpicas.truetype.CmapTable;
import com.kreative.bitsnpicas.truetype.GlyfTable;
import com.kreative.bitsnpicas.truetype.HeadTable;
import com.kreative.bitsnpicas.truetype.LocaTable;
import com.kreative.bitsnpicas.truetype.MaxpTable;
import com.kreative.bitsnpicas.truetype.PostTable;
import com.kreative.bitsnpicas.truetype.PostTableEntry;
import com.kreative.bitsnpicas.truetype.SbixEntry;
import com.kreative.bitsnpicas.truetype.SbixSubtable;
import com.kreative.bitsnpicas.truetype.SbixTable;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class InjectSbix {
	public static void main(String[] args) {
		for (String arg : args) {
			File file = new File(arg);
			System.out.print("Processing " + file.getAbsolutePath() + "... ");
			try {
				File inputRoot = new File(file.getParent(), file.getName() + ".sbix.d");
				if (!(inputRoot.exists() && inputRoot.isDirectory())) {
					System.out.println("no sbix directory found.");
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
					MaxpTable maxp = (MaxpTable)ttf.getByTableName("maxp");
					HeadTable head = (HeadTable)ttf.getByTableName("head");
					GlyfTable glyf = (GlyfTable)ttf.getByTableName("glyf");
					LocaTable loca = (LocaTable)ttf.getByTableName("loca");
					SbixTable sbix = (SbixTable)ttf.getByTableName("sbix");
					if (sbix == null) {
						sbix = new SbixTable();
						ttf.add(sbix);
					} else {
						sbix.clear();
					}
					for (File inputSubdir : inputRoot.listFiles()) {
						try {
							int ppem = Integer.parseInt(inputSubdir.getName());
							SbixSubtable subtable = new SbixSubtable();
							subtable.ppem = ppem;
							sbix.add(subtable);
							Map<Integer,SbixEntry> entries = new HashMap<Integer,SbixEntry>();
							for (File inputFile : inputSubdir.listFiles()) {
								String name = inputFile.getName();
								int o = name.lastIndexOf('.');
								if (o > 0) {
									String extension = name.substring(o + 1);
									name = name.substring(0, o);
									int glyphIndex = getGlyphIndex(name, cmapsub, post);
									if (glyphIndex > 0) {
										byte[] inputData = new byte[(int)inputFile.length()];
										FileInputStream inIn = new FileInputStream(inputFile);
										inIn.read(inputData);
										inIn.close();
										SbixEntry e = new SbixEntry();
										e.setImageTypeString(extension);
										e.imageData = inputData;
										entries.put(glyphIndex, e);
									}
								}
							}
							for (int i = 0; i < maxp.numGlyphs; i++) {
								if (entries.containsKey(i)) {
									SbixEntry e = entries.get(i);
									if (glyf != null) {
										GlyfInfo g = rewriteGlyf(glyf.get(i));
										e.offsetX = g.xMin * ppem / head.unitsPerEm;
										e.offsetY = g.yMin * ppem / head.unitsPerEm;
										glyf.set(i, g.data);
									}
									subtable.add(e);
								} else {
									subtable.add(new SbixEntry());
								}
							}
						} catch (NumberFormatException nfe) {
							continue;
						}
					}
					if (glyf != null && loca != null) {
						loca.clear();
						int currentLocation = 0;
						for (int i = 0; i < maxp.numGlyphs; i++) {
							loca.add(currentLocation);
							currentLocation += glyf.get(i).length;
						}
						loca.add(currentLocation);
					}
					data = ttf.compile();
					File outputFile = new File(file.getParent(), file.getName() + ".sbix.ttf");
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
	
	private static class GlyfInfo {
		public int xMin, yMin, xMax, yMax;
		public byte[] data;
	}
	
	private static GlyfInfo rewriteGlyf(byte[] data) {
		GlyfInfo info = new GlyfInfo();
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			DataInputStream din = new DataInputStream(in);
			din.readShort();
			info.xMin = din.readShort();
			info.yMin = din.readShort();
			info.xMax = din.readShort();
			info.yMax = din.readShort();
			din.close();
			in.close();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(out);
			dout.writeShort(0);
			dout.writeShort(info.xMin);
			dout.writeShort(info.yMin);
			dout.writeShort(info.xMax);
			dout.writeShort(info.yMax);
			dout.writeShort(0);
			dout.flush();
			out.flush();
			dout.close();
			out.close();
			info.data = out.toByteArray();
		} catch (IOException ioe) {
			info.data = data;
		}
		return info;
	}
	
	private static int getGlyphIndex(String s, CmapSubtable cmap, PostTable post) {
		if (s.startsWith("glyph_")) {
			try {
				return Integer.parseInt(s.substring(6));
			} catch (NumberFormatException nfe) {
				return 0;
			}
		}
		if (s.startsWith("char_")) {
			if (cmap == null) {
				return 0;
			} else try {
				int ch = Integer.parseInt(s.substring(5), 16);
				return cmap.getGlyphIndex(ch);
			} catch (NumberFormatException nfe) {
				return 0;
			}
		}
		if (post == null) {
			return 0;
		} else {
			PostTableEntry pe = PostTableEntry.forCharacterName(unescape(s));
			if (post.contains(pe)) {
				return post.indexOf(pe);
			} else {
				return 0;
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
