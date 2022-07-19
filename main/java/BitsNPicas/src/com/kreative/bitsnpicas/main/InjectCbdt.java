package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import com.kreative.bitsnpicas.truetype.*;

public class InjectCbdt {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		boolean removeGlyphs = true;
		for (String arg : args) {
			if (arg.equals("-g")) { removeGlyphs = false; continue; }
			if (arg.equals("-G")) { removeGlyphs = true; continue; }
			File file = new File(arg);
			System.out.print("Processing " + file.getAbsolutePath() + "... ");
			try {
				File inputRoot = new File(file.getParent(), file.getName() + ".cbdt.d");
				if (!(inputRoot.exists() && inputRoot.isDirectory())) {
					System.out.println("no cbdt directory found.");
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
					HheaTable hhea = (HheaTable)ttf.getByTableName("hhea");
					EblcTable cblc = (EblcTable)ttf.getByTableName("CBLC");
					if (cblc == null) {
						cblc = new EblcTable(SbitTableType.COLOR);
						ttf.add(cblc); 
					} else {
						cblc.clear();
					}
					EbdtTable cbdt = (EbdtTable)ttf.getByTableName("CBDT");
					if (cbdt == null) {
						cbdt = new EbdtTable(SbitTableType.COLOR);
						ttf.add(cbdt);
					} else {
						cbdt.clear();
					}
					for (File inputSubdir : inputRoot.listFiles()) {
						if (inputSubdir.isDirectory() && !inputSubdir.getName().startsWith(".")) {
							File meta = new File(inputSubdir, "metadata.txt");
							EblcBitmapSize ebs = createBitmapSize(hhea);
							Map<Integer,SbitSmallGlyphMetrics> smallMetrics = new HashMap<Integer,SbitSmallGlyphMetrics>();
							Map<Integer,SbitBigGlyphMetrics> bigMetrics = new HashMap<Integer,SbitBigGlyphMetrics>();
							if (meta.isFile()) parseMetadata(meta, ebs, smallMetrics, bigMetrics, cmapsub, post);
							
							SortedMap<Integer,CbdtEntry> entries = new TreeMap<Integer,CbdtEntry>();
							for (File inputFile : inputSubdir.listFiles()) {
								String name = inputFile.getName();
								int o = name.lastIndexOf('.');
								if (o > 0) {
									String extension = name.substring(o + 1);
									if ("png".equalsIgnoreCase(extension)) {
										name = name.substring(0, o);
										int glyphIndex = getGlyphIndex(name, cmapsub, post);
										if (glyphIndex > 0) {
											byte[] inputData = new byte[(int)inputFile.length()];
											FileInputStream inIn = new FileInputStream(inputFile);
											inIn.read(inputData);
											inIn.close();
											CbdtEntry entry = createCbdtEntry(smallMetrics, bigMetrics, glyphIndex);
											entry.imageData = inputData;
											entries.put(glyphIndex, entry);
										}
									}
								}
							}
							
							EblcIndexSubtable1 st = null;
							for (Map.Entry<Integer,CbdtEntry> e : entries.entrySet()) {
								int gid = e.getKey();
								if (ebs.startGlyphIndex <= 0 || gid < ebs.startGlyphIndex) ebs.startGlyphIndex = gid;
								if (ebs.endGlyphIndex <= 0 || gid > ebs.endGlyphIndex) ebs.endGlyphIndex = gid;
								CbdtEntry entry = e.getValue();
								if (st != null && (st.header.lastGlyphIndex + 1) == gid && st.header.imageFormat == entry.format()) {
									st.header.lastGlyphIndex++;
								} else {
									ebs.add((st = new EblcIndexSubtable1()));
									st.header = new EblcIndexSubtableHeader();
									st.header.firstGlyphIndex = gid;
									st.header.lastGlyphIndex = gid;
									st.header.indexFormat = 1;
									st.header.imageFormat = entry.format();
									st.add(cbdt.getNextKey());
								}
								int key = st.get(st.size() - 1);
								cbdt.put(key, entry);
								st.add(cbdt.getNextKey());
							}
							
							cblc.add(ebs);
						}
					}
					cbdt.recalculate(cblc);
					if (removeGlyphs) {
						ttf.remove(ttf.getByTableName("loca"));
						ttf.remove(ttf.getByTableName("glyf"));
						ttf.remove(ttf.getByTableName("CFF "));
						ttf.remove(ttf.getByTableName("CFF2"));
					}
					data = ttf.compile();
					String ext = ttf.isOpenType() ? ".otf" : ".ttf";
					File outputFile = new File(file.getParent(), file.getName() + ".cbdt" + ext);
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
	
	private static EblcBitmapSize createBitmapSize(HheaTable hhea) {
		EblcBitmapSize ebs = new EblcBitmapSize();
		ebs.hori = new SbitLineMetrics();
		ebs.vert = new SbitLineMetrics();
		if (hhea != null) {
			ebs.hori.ascender = ebs.vert.ascender = hhea.ascent;
			ebs.hori.descender = ebs.vert.descender = hhea.descent;
			ebs.hori.widthMax = ebs.vert.widthMax = hhea.advanceWidthMax;
			ebs.ppemX = ebs.ppemY = hhea.ascent - hhea.descent;
		} else {
			ebs.hori.ascender = ebs.vert.ascender = 101;
			ebs.hori.descender = ebs.vert.descender = -27;
			ebs.hori.widthMax = ebs.vert.widthMax = 136;
			ebs.ppemX = ebs.ppemY = 109;
		}
		ebs.bitDepth = 32;
		ebs.flags = 1;
		return ebs;
	}
	
	private static void parseMetadata(
		File meta, EblcBitmapSize ebs,
		Map<Integer,SbitSmallGlyphMetrics> smallMetrics,
		Map<Integer,SbitBigGlyphMetrics> bigMetrics,
		CmapSubtable cmap, PostTable post
	) throws IOException {
		Scanner scan = new Scanner(new FileInputStream(meta), "UTF-8");
		boolean inGlyph = false;
		int glyphId = 0;
		SbitSmallGlyphMetrics sgm = null;
		SbitBigGlyphMetrics bgm = null;
		boolean useBGM = false;
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			if (line.length() > 0 && !line.startsWith("#")) {
				String[] fields = line.split(":", 2);
				String key = fields[0].trim().toLowerCase();
				String value = (fields.length > 1) ? fields[1].trim() : null;
				if (inGlyph) {
					if ("endglyph".equals(key)) {
						if (useBGM) bigMetrics.put(glyphId, bgm);
						else smallMetrics.put(glyphId, sgm);
						inGlyph = false;
						glyphId = 0;
						sgm = null;
						bgm = null;
						useBGM = false;
					} else if ("height".equals(key)) {
						try { sgm.height = bgm.height = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("width".equals(key)) {
						try { sgm.width = bgm.width = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("bearingx".equals(key)) {
						try { sgm.bearingX = Integer.parseInt(value); useBGM = false; }
						catch (NumberFormatException nfe) {}
					} else if ("bearingy".equals(key)) {
						try { sgm.bearingY = Integer.parseInt(value); useBGM = false; }
						catch (NumberFormatException nfe) {}
					} else if ("advance".equals(key)) {
						try { sgm.advance = Integer.parseInt(value); useBGM = false; }
						catch (NumberFormatException nfe) {}
					} else if ("horibearingx".equals(key)) {
						try { bgm.horiBearingX = Integer.parseInt(value); useBGM = true; }
						catch (NumberFormatException nfe) {}
					} else if ("horibearingy".equals(key)) {
						try { bgm.horiBearingY = Integer.parseInt(value); useBGM = true; }
						catch (NumberFormatException nfe) {}
					} else if ("horiadvance".equals(key)) {
						try { bgm.horiAdvance = Integer.parseInt(value); useBGM = true; }
						catch (NumberFormatException nfe) {}
					} else if ("vertbearingx".equals(key)) {
						try { bgm.vertBearingX = Integer.parseInt(value); useBGM = true; }
						catch (NumberFormatException nfe) {}
					} else if ("vertbearingy".equals(key)) {
						try { bgm.vertBearingY = Integer.parseInt(value); useBGM = true; }
						catch (NumberFormatException nfe) {}
					} else if ("vertadvance".equals(key)) {
						try { bgm.vertAdvance = Integer.parseInt(value); useBGM = true; }
						catch (NumberFormatException nfe) {}
					}
				} else {
					if ("glyph".equals(key)) {
						inGlyph = true;
						try {
							glyphId = Integer.parseInt(value);
						} catch (NumberFormatException nfe) {
							glyphId = getGlyphIndex(value, cmap, post);
						}
						sgm = new SbitSmallGlyphMetrics();
						bgm = new SbitBigGlyphMetrics();
						useBGM = false;
					} else if ("indexsubtablearrayoffset".equals(key)) {
						try { ebs.indexSubTableArrayOffset = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("indextablessize".equals(key)) {
						try { ebs.indexTablesSize = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("numberofindexsubtables".equals(key)) {
						try { ebs.numberOfIndexSubTables = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("colorref".equals(key)) {
						try { ebs.colorRef = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("horiascender".equals(key)) {
						try { ebs.hori.ascender = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("horidescender".equals(key)) {
						try { ebs.hori.descender = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("horiwidthmax".equals(key)) {
						try { ebs.hori.widthMax = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("horicaretslopenumerator".equals(key)) {
						try { ebs.hori.caretSlopeNumerator = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("horicaretslopedenominator".equals(key)) {
						try { ebs.hori.caretSlopeDenominator = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("horicaretoffset".equals(key)) {
						try { ebs.hori.caretOffset = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("horiminoriginsb".equals(key)) {
						try { ebs.hori.minOriginSB = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("horiminadvancesb".equals(key)) {
						try { ebs.hori.minAdvanceSB = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("horimaxbeforebl".equals(key)) {
						try { ebs.hori.maxBeforeBL = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("horiminafterbl".equals(key)) {
						try { ebs.hori.minAfterBL = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("vertascender".equals(key)) {
						try { ebs.vert.ascender = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("vertdescender".equals(key)) {
						try { ebs.vert.descender = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("vertwidthmax".equals(key)) {
						try { ebs.vert.widthMax = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("vertcaretslopenumerator".equals(key)) {
						try { ebs.vert.caretSlopeNumerator = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("vertcaretslopedenominator".equals(key)) {
						try { ebs.vert.caretSlopeDenominator = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("vertcaretoffset".equals(key)) {
						try { ebs.vert.caretOffset = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("vertminoriginsb".equals(key)) {
						try { ebs.vert.minOriginSB = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("vertminadvancesb".equals(key)) {
						try { ebs.vert.minAdvanceSB = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("vertmaxbeforebl".equals(key)) {
						try { ebs.vert.maxBeforeBL = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("vertminafterbl".equals(key)) {
						try { ebs.vert.minAfterBL = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("startglyphindex".equals(key)) {
						try { ebs.startGlyphIndex = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("endglyphindex".equals(key)) {
						try { ebs.endGlyphIndex = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("ppemx".equals(key)) {
						try { ebs.ppemX = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("ppemy".equals(key)) {
						try { ebs.ppemY = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("bitdepth".equals(key)) {
						try { ebs.bitDepth = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					} else if ("flags".equals(key)) {
						try { ebs.flags = Integer.parseInt(value); }
						catch (NumberFormatException nfe) {}
					}
				}
			}
		}
		scan.close();
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
	
	private static CbdtEntry createCbdtEntry(
		Map<Integer,SbitSmallGlyphMetrics> smallMetrics,
		Map<Integer,SbitBigGlyphMetrics> bigMetrics,
		int glyphIndex
	) {
		if (smallMetrics.containsKey(glyphIndex)) {
			CbdtEntryFormat17 e17 = new CbdtEntryFormat17();
			e17.glyphMetrics = smallMetrics.get(glyphIndex);
			return e17;
		} else if (bigMetrics.containsKey(glyphIndex)) {
			CbdtEntryFormat18 e18 = new CbdtEntryFormat18();
			e18.glyphMetrics = bigMetrics.get(glyphIndex);
			return e18;
		} else if (smallMetrics.containsKey(0)) {
			CbdtEntryFormat17 e17 = new CbdtEntryFormat17();
			e17.glyphMetrics = smallMetrics.get(0);
			return e17;
		} else if (bigMetrics.containsKey(0)) {
			CbdtEntryFormat18 e18 = new CbdtEntryFormat18();
			e18.glyphMetrics = bigMetrics.get(0);
			return e18;
		} else {
			return new CbdtEntryFormat19();
		}
	}
}
