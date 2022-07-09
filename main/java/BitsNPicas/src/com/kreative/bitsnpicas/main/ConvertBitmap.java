package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontGlyphTransformer;
import com.kreative.bitsnpicas.IDGenerator;
import com.kreative.bitsnpicas.PointSizeGenerator;
import com.kreative.bitsnpicas.transformer.BoldBitmapFontGlyphTransformer;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class ConvertBitmap {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			Options o = new Options();
			String sp = null;
			String rp = null;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (processingOptions && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingOptions = false;
					} else if (arg.equals("-s") && argi < args.length) {
						sp = args[argi++];
						if (rp != null) {
							o.nameSearchReplace.add(new SearchReplacePattern(sp, rp));
							sp = null;
							rp = null;
						}
					} else if (arg.equals("-r") && argi < args.length) {
						rp = args[argi++];
						if (sp != null) {
							o.nameSearchReplace.add(new SearchReplacePattern(sp, rp));
							sp = null;
							rp = null;
						}
					} else if (arg.equals("-t") && argi < args.length) {
						o.timestampCodepoint = parseInt16(args[argi++]);
					} else if (arg.equals("-sr") && argi < args.length) {
						File srFile = new File(args[argi++]);
						try {
							Scanner scan = new Scanner(srFile, "utf-8");
							while (scan.hasNextLine()) {
								String line = scan.nextLine().trim();
								int offset = line.indexOf('#');
								if (offset >= 0) line = line.substring(0, offset).trim();
								if (line.length() == 0) continue;
								String[] a = line.split("\\s+");
								Integer ocp = (a.length > 0) ? parseInt16(a[0]) : null;
								Integer ncp = (a.length > 1) ? parseInt16(a[1]) : null;
								if (ocp != null) o.subsetRemap.put(ocp, ((ncp == null) ? ocp : ncp));
							}
							scan.close();
						} catch (IOException e) {
							System.err.println("Failed to read subsetRemap: " + srFile.getName());
						}
					} else if (arg.equals("-d") && argi < args.length) {
						String[] a = args[argi++].split("-", 2);
						Integer rs = (a.length > 0) ? parseInt16(a[0]) : null;
						Integer re = (a.length > 1) ? parseInt16(a[1]) : null;
						if (rs != null && re != null) o.glyphsToRemove.set(Math.min(rs,re), Math.max(rs,re)+1);
						else if (rs != null) o.glyphsToRemove.set(rs);
						else if (re != null) o.glyphsToRemove.set(re);
					} else if (arg.equals("-D") && argi < args.length) {
						String[] a = args[argi++].split("-", 2);
						Integer rs = (a.length > 0) ? parseInt16(a[0]) : null;
						Integer re = (a.length > 1) ? parseInt16(a[1]) : null;
						if (rs != null && re != null) o.glyphsToRemove.clear(Math.min(rs,re), Math.max(rs,re)+1);
						else if (rs != null) o.glyphsToRemove.clear(rs);
						else if (re != null) o.glyphsToRemove.clear(re);
					} else if (arg.equals("-c")) {
						o.strictMonospace = true;
					} else if (arg.equals("-C")) {
						o.strictMonospace = false;
					} else if (arg.equals("-n")) {
						o.transform.clear();
					} else if (arg.equals("-b")) {
						o.transform.add(new BoldBitmapFontGlyphTransformer());
					} else if (arg.equals("-o") && argi < args.length) {
						o.dest = new File(args[argi++]);
					} else if (arg.equals("-f") && argi < args.length) {
						o.format = args[argi++];
					} else if ((arg.equals("-w") || arg.equals("-x")) && argi < args.length) {
						o.oo.xSize = parseInt(args[argi++], 100);
					} else if ((arg.equals("-h") || arg.equals("-y")) && argi < args.length) {
						o.oo.ySize = parseInt(args[argi++], 100);
					} else if (arg.equals("-T")) {
						o.oo.extendWinMetrics = false;
					} else if (arg.equals("-Y")) {
						o.oo.extendWinMetrics = true;
					} else if (arg.equals("-p") && argi < args.length) {
						String s = args[argi++];
						boolean done = loadPreset(o, s);
						if (!done) System.err.println("Unknown preset: " + s);
					} else if (arg.equals("-i") && argi < args.length) {
						int id = parseInt(args[argi++], 0);
						o.oo.idgen = new IDGenerator.Sequential(id, 128, 32768);
					} else if (arg.equals("-R")) {
						o.oo.idgen = new IDGenerator.Random(128, 32768);
					} else if (arg.equals("-H")) {
						o.oo.idgen = new IDGenerator.HashCode(128, 32768);
					} else if (arg.equals("-z") && argi < args.length) {
						int size = parseInt(args[argi++], 0);
						o.oo.sizegen = new PointSizeGenerator.Fixed(size);
					} else if (arg.equals("-E")) {
						o.oo.sizegen = new PointSizeGenerator.Automatic(4, 127);
					} else if (arg.equals("-S")) {
						o.oo.sizegen = new PointSizeGenerator.Standard(9, 10, 12, 14, 18, 24, 36, 48, 72);
					} else if (arg.equals("-e") && argi < args.length) {
						o.io.encodingName = o.oo.encodingName = args[argi++];
						o.oo.fontxDoubleByte = false;
					} else if (arg.equals("-ie") && argi < args.length) {
						o.io.encodingName = args[argi++];
					} else if (arg.equals("-oe") && argi < args.length) {
						o.oo.encodingName = args[argi++];
						o.oo.fontxDoubleByte = false;
					} else if (arg.equals("-ee") && argi < args.length) {
						o.io.fontxDoubleByteEncoding = o.oo.fontxDoubleByteEncoding = args[argi++];
						o.oo.fontxDoubleByte = true;
					} else if (arg.equals("-iee") && argi < args.length) {
						o.io.fontxDoubleByteEncoding = args[argi++];
					} else if (arg.equals("-oee") && argi < args.length) {
						o.oo.fontxDoubleByteEncoding = args[argi++];
						o.oo.fontxDoubleByte = true;
					} else if (arg.equals("-le") && argi < args.length) {
						o.io.psfLowEncoding = o.oo.psfLowEncoding = args[argi++];
						o.oo.psfUseLowEncoding = (o.oo.psfLowEncoding.length() > 0);
					} else if (arg.equals("-he") && argi < args.length) {
						o.io.psfHighEncoding = o.oo.psfHighEncoding = args[argi++];
						o.oo.psfUseHighEncoding = (o.oo.psfHighEncoding.length() > 0);
					} else if (arg.equals("-ile") && argi < args.length) {
						o.io.psfLowEncoding = args[argi++];
					} else if (arg.equals("-ihe") && argi < args.length) {
						o.io.psfHighEncoding = args[argi++];
					} else if (arg.equals("-ole") && argi < args.length) {
						o.oo.psfLowEncoding = args[argi++];
						o.oo.psfUseLowEncoding = (o.oo.psfLowEncoding.length() > 0);
					} else if (arg.equals("-ohe") && argi < args.length) {
						o.oo.psfHighEncoding = args[argi++];
						o.oo.psfUseHighEncoding = (o.oo.psfHighEncoding.length() > 0);
					} else if (arg.equals("-pb") && argi < args.length) {
						o.io.psfPuaBase = parseInt16(args[argi++]);
					} else if (arg.equals("-a") && argi < args.length) {
						o.oo.u8mLoadAddress = parseInt16(args[argi++]);
					} else if (arg.equals("-m")) {
						o.oo.geosMega = true;
					} else if (arg.equals("-M")) {
						o.oo.geosMega = false;
					} else if (arg.equals("-k")) {
						o.oo.geosKerning = true;
					} else if (arg.equals("-K")) {
						o.oo.geosKerning = false;
					} else if (arg.equals("-u")) {
						o.oo.geosUTF8 = true;
					} else if (arg.equals("-U")) {
						o.oo.geosUTF8 = false;
					} else if (arg.equals("-pa")) {
						o.oo.psfUseAllGlyphs = true;
					} else if (arg.equals("-PA")) {
						o.oo.psfUseAllGlyphs = false;
					} else if (arg.equals("-pu")) {
						o.oo.psfUnicodeTable = true;
					} else if (arg.equals("-PU")) {
						o.oo.psfUnicodeTable = false;
					} else if (arg.equals("--help")) {
						printHelp();
					} else {
						System.err.println("Unknown option: " + arg);
					}
				} else {
					try {
						System.out.print(arg + "...");
						File file = new File(arg);
						BitmapInputFormat format = BitmapInputFormat.forFile(file);
						if (format == null) {
							System.out.println(" FAILED: Unknown input format.");
						} else {
							if (format.macResFork) {
								file = new File(file, "..namedfork");
								file = new File(file, "rsrc");
							}
							BitmapFont[] fonts = format.createImporter(o.io).importFont(file);
							if (fonts == null || fonts.length == 0) {
								System.out.println(" FAILED: No fonts found.");
							} else {
								boolean anyDone = false;
								for (BitmapFont font : fonts) {
									transformFont(font, o);
									font.contractGlyphs();
									font.autoFillNames();
									String name = font.getName(format.nameType);
									boolean done = exportFont(font, name, o);
									if (done) anyDone = true;
								}
								if (anyDone) System.out.println(" DONE");
								else System.out.println(" FAILED: Unknown output format.");
							}
						}
					} catch (IOException e) {
						System.out.println(" FAILED: " + e.getClass().getSimpleName() + ": " + e.getMessage());
					}
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("Usage:");
		System.out.println("  java -jar BitsNPicas.jar convertbitmap <options> <files>");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -s <regexp>   Regular expression to search for in the font name.");
		System.out.println("  -r <string>   Replacement text for <regexp> in the font name.");
		System.out.println("  -t <codept>   Generate a timestamp glyph at the specified code point.");
		System.out.println("  -sr <path>    Create a subset/remap glyphs according to a mapping file.");
		System.out.println("  -d <range>    Do not include characters in the specified range.");
		System.out.println("  -D <range>    Include characters in the specified range.");
		System.out.println("  -c            Strict monospace: remove glyphs not the width of a space.");
		System.out.println("  -C            Loose monospace or proportional; the opposite of -c.");
		System.out.println("  -n            Do not transform the font (the default).");
		System.out.println("  -b            Transform the font using faux bold.");
		System.out.println("  -o <path>     Write output to the specified file or directory.");
		System.out.println("  -f <format>   Set the output format. One of:");
		List<String> ids = new ArrayList<String>();
		for (BitmapOutputFormat f : BitmapOutputFormat.values()) {
			for (String id : f.ids) ids.add(id);
		}
		printHelpList(ids);
		System.out.println("  -w <number>   Pixel width in em units (for ttf). Default: 100.");
		System.out.println("  -h <number>   Pixel height in em units (for ttf). Default: 100.");
		System.out.println("  -T            Use typo metrics for winAscent/winDescent (for ttf).");
		System.out.println("                (Force line height; more compatible. The default.)");
		System.out.println("  -Y            Use yMin/yMax for winAscent/winDescent (for ttf).");
		System.out.println("                (Prevent clipping; more conformant to the TT/OT spec.)");
		System.out.println("  -p <preset>   Use a preset for -s, -r, -w, and -h. One of:");
		System.out.println("                    none, apple2, apple2-40col, apple2-80col,");
		System.out.println("                    apple2-hgr, apple2-dhr, lisa, lisa-raw, lisa-2x3y,");
		System.out.println("                    iigs-320, iigs-640, iigs-320h, iigs-640h, atari,");
		System.out.println("                    atari-40col, atari-80col, commodore, commodore-20col,");
		System.out.println("                    commodore-40col, commodore-80col, trs80, trs80-64col,");
		System.out.println("                    trs80-32col, trs80-m1, trs80-m1-64col, trs80-m1-32col,");
		System.out.println("                    trs80-m2, trs80-m3, trs80-m3-64col, trs80-m3-32col,");
		System.out.println("                    trs80-m4, trs80-m4-64col, trs80-m4-32col,");
		System.out.println("                    trs80-m4-80col, trs80-m4-40col");
		System.out.println("  -i <number>   Font ID number (for nfnt or geos).");
		System.out.println("  -R            Use random ID number (for nfnt or geos).");
		System.out.println("  -H            Use ID number derived from font name (for nfnt or geos).");
		System.out.println("  -z <number>   Font point size (for nfnt or geos).");
		System.out.println("  -E            Use any point size (for nfnt or geos).");
		System.out.println("  -S            Use only standard point sizes (for nfnt or geos).");
		System.out.println("                    (9, 10, 12, 14, 18, 24, 36, 48, and 72 for nfnt,)");
		System.out.println("                    (9, 10, 12, 14, 18, 24, 36, 48, and 60 for geos.)");
		System.out.println("  -m / -M       Generate a MEGA / standard font (for geos).");
		System.out.println("  -k / -K       Generate / don't generate kerning tables (for geos).");
		System.out.println("  -u / -U       Generate / don't generate UTF-8 tables (for geos).");
		System.out.println("  -a <addr>     Add a loading address to the file (for u8m).");
		System.out.println("  -e <enc>      Use the specified encoding (for nfnt, fzx, sbf). One of:");
		System.out.println("  -ie <enc>     Use the specified encoding for reading only. One of:");
		System.out.println("  -oe <enc>     Use the specified encoding for writing only. One of:");
		List<String> encs = new ArrayList<String>();
		for (GlyphList e : EncodingList.instance().glyphLists()) {
			encs.add(e.getName());
		}
		printHelpList(encs);
		System.out.println("  -ee <enc>     Use the specified double-byte encoding (for fontx).");
		System.out.println("  -iee <enc>    Use the specified double-byte encoding for reading only.");
		System.out.println("  -oee <enc>    Use the specified double-byte encoding for writing only.");
		System.out.println("  -le <enc>     Use the specified encoding for chars 000-0FF (for psf).");
		System.out.println("  -he <enc>     Use the specified encoding for chars 100-1FF (for psf).");
		System.out.println("  -ile <enc>    Use the specified encoding for chars 000-0FF for reading.");
		System.out.println("  -ihe <enc>    Use the specified encoding for chars 100-1FF for reading.");
		System.out.println("  -ole <enc>    Use the specified encoding for chars 000-0FF for writing.");
		System.out.println("  -ohe <enc>    Use the specified encoding for chars 100-1FF for writing.");
		System.out.println("  -pb <cp>      Read all glyphs in order starting at code point (for psf).");
		System.out.println("  -pa / -PA     Write / don't write other characters (for psf).");
		System.out.println("  -pu / -PU     Write / don't write Unicode mapping table (for psf).");
		System.out.println("  --            Process remaining arguments as file names.");
		System.out.println();
	}
	
	private static void printHelpList(List<String> list) {
		int col = 0;
		for (int i = 0, n = list.size(); i < n; i++) {
			String item = " " + list.get(i);
			if (i < n - 1) item += ",";
			if (col + item.length() > 78) {
				System.out.println();
				col = 0;
			}
			if (col == 0) {
				System.out.print("                   ");
				col = 19;
			}
			System.out.print(item);
			col += item.length();
		}
		System.out.println();
	}
	
	private static class Options {
		public final BitmapInputOptions io = new BitmapInputOptions();
		public final BitmapOutputOptions oo = new BitmapOutputOptions();
		public List<SearchReplacePattern> nameSearchReplace = new ArrayList<SearchReplacePattern>();
		public Integer timestampCodepoint = null;
		public Map<Integer,Integer> subsetRemap = new HashMap<Integer,Integer>();
		public BitSet glyphsToRemove = new BitSet();
		public boolean strictMonospace = false;
		public List<BitmapFontGlyphTransformer> transform = new ArrayList<BitmapFontGlyphTransformer>();
		public File dest = null;
		public String format = "ttf";
	}
	
	private static class SearchReplacePattern {
		private final Pattern searchPattern;
		private final String replacePattern;
		public SearchReplacePattern(String s, String r) {
			Pattern sp;
			try {
				sp = Pattern.compile(s);
			} catch (PatternSyntaxException e) {
				sp = Pattern.compile(Pattern.quote(s));
			}
			this.searchPattern = sp;
			this.replacePattern = r;
		}
		public String replaceAll(String s) {
			return searchPattern.matcher(s).replaceAll(replacePattern);
		}
	}
	
	private static int parseInt(String s, int def) {
		try { return Integer.parseInt(s); }
		catch (NumberFormatException e) { return def; }
	}
	
	private static Integer parseInt16(String s) {
		try {
			if (s.startsWith("U+") || s.startsWith("u+")) return Integer.parseInt(s.substring(2), 16);
			if (s.startsWith("0x") || s.startsWith("0X")) return Integer.parseInt(s.substring(2), 16);
			if (s.startsWith("$")) return Integer.parseInt(s.substring(1), 16);
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private static boolean loadPreset(Options o, String name) {
		name = name.toLowerCase();
		if (name.equals("") || name.equals("none")) {
			o.nameSearchReplace.clear();
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("apple2") || name.equals("apple2-40col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Apple II", "Print Char 21"));
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("apple2-80col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Apple II", "PR Number 3"));
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("apple2-hgr")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("II", "II HGR"));
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("apple2-dhr")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("II", "II DHR"));
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("lisa") || name.equals("lisa-2x3y")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Raw", "2X3Y"));
			o.oo.xSize = 80; o.oo.ySize = 120;
			return true;
		} else if (name.equals("lisa-raw")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("2X3Y", "Raw"));
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("iigs-320")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Shaston", "Shaston 320"));
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("iigs-640")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Shaston", "Shaston 640"));
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("iigs-320h")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Shaston", "Shaston Hi 320"));
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("iigs-640h")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Shaston", "Shaston Hi 640"));
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("atari") || name.equals("atari-40col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Colleen", "Candy"));
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("atari-80col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Candy", "Colleen"));
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("commodore") || name.equals("commodore-40col")) {
			o.nameSearchReplace.clear();
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("commodore-20col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Pet.*$", "$0 2X"));
			o.oo.xSize = 200; o.oo.ySize = 100;
			return true;
		} else if (name.equals("commodore-80col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Pet.*$", "$0 2Y"));
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("trs80") || name.equals("trs80-64col") || name.equals("trs80-m1") || name.equals("trs80-m1-64col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Raw", "64C 2X3Y"));
			o.oo.xSize = 80; o.oo.ySize = 120;
			return true;
		} else if (name.equals("trs80-32col") || name.equals("trs80-m1-32col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Raw", "32C 4X3Y"));
			o.oo.xSize = 160; o.oo.ySize = 120;
			return true;
		} else if (name.equals("trs80-m2")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Raw", "2Y"));
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("trs80-m3") || name.equals("trs80-m3-64col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Raw", "64C"));
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("trs80-m3-32col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Raw", "32C"));
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("trs80-m4") || name.equals("trs80-m4-64col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Raw", "64C"));
			o.oo.xSize = 100; o.oo.ySize = 160;
			return true;
		} else if (name.equals("trs80-m4-32col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Raw", "32C"));
			o.oo.xSize = 100; o.oo.ySize = 80;
			return true;
		} else if (name.equals("trs80-m4-80col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Raw", "80C"));
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("trs80-m4-40col")) {
			o.nameSearchReplace.clear();
			o.nameSearchReplace.add(new SearchReplacePattern("Raw", "40C"));
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else {
			return false;
		}
	}
	
	private static void transformFont(BitmapFont font, Options o) {
		if (!o.nameSearchReplace.isEmpty()) {
			for (int key : font.nameTypes()) {
				String s = font.getName(key);
				for (SearchReplacePattern p : o.nameSearchReplace) {
					s = p.replaceAll(s);
				}
				font.setName(key, s);
			}
		}
		if (o.timestampCodepoint != null) {
			GregorianCalendar now = new GregorianCalendar();
			int y = now.get(GregorianCalendar.YEAR);
			int m = now.get(GregorianCalendar.MONTH) + 1;
			int d = now.get(GregorianCalendar.DAY_OF_MONTH);
			BitmapFontGlyph[] glyphs = {
				font.getCharacter(0x10FF40 + ((y / 1000) % 10)),
				font.getCharacter(0x10FF50 + ((y /  100) % 10)),
				font.getCharacter(0x10FF60 + ((y /   10) % 10)),
				font.getCharacter(0x10FF70 + ((y /    1) % 10)),
				font.getCharacter(0x10FF80 + ((m /   10) % 10)),
				font.getCharacter(0x10FF90 + ((m /    1) % 10)),
				font.getCharacter(0x10FFA0 + ((d /   10) % 10)),
				font.getCharacter(0x10FFB0 + ((d /    1) % 10)),
				font.getCharacter(0x10FFC0)
			};
			BitmapFontGlyph glyph = BitmapFontGlyph.compose(glyphs);
			if (glyph != null) font.putCharacter(o.timestampCodepoint, glyph);
		}
		if (!o.subsetRemap.isEmpty()) {
			font.subsetRemap(o.subsetRemap);
		}
		if (!o.glyphsToRemove.isEmpty()) {
			for (
				int i = o.glyphsToRemove.nextSetBit(0);
				i >= 0;
				i = o.glyphsToRemove.nextSetBit(i + 1)
			) {
				font.removeCharacter(i);
			}
		}
		if (o.strictMonospace) {
			BitmapFontGlyph space = font.getCharacter(32);
			int monoWidth = (space != null) ? space.getCharacterWidth() : 0;
			for (int cp : font.codePoints()) {
				if (font.getCharacter(cp).getCharacterWidth() != monoWidth) {
					font.removeCharacter(cp);
				}
			}
		}
		for (BitmapFontGlyphTransformer tx : o.transform) {
			font.transform(tx);
		}
	}
	
	private static boolean exportFont(BitmapFont font, String name, Options o) throws IOException {
		String format = o.format.toLowerCase();
		for (BitmapOutputFormat f : BitmapOutputFormat.values()) {
			for (String id : f.ids) {
				if (format.equals(id)) {
					File out = getOutputFile(o.dest, name, f.suffix);
					if (f.macResFork) {
						out.createNewFile();
						out = new File(out, "..namedfork");
						out = new File(out, "rsrc");
					}
					BitmapFontExporter exporter = f.createExporter(o.oo);
					exporter.exportFontToFile(font, out);
					if (f.macResFork) out = out.getParentFile().getParentFile();
					f.postProcess(out);
					return true;
				}
			}
		}
		return false;
	}
	
	private static File getOutputFile(File dest, String name, String ext) {
		if (dest == null || dest.isDirectory()) {
			ext = normalizeExtension(ext);
		} else {
			name = dest.getName();
			int o = name.lastIndexOf('.');
			if (o > 0) {
				ext = name.substring(o);
				name = name.substring(0, o);
			} else {
				ext = "";
			}
			dest = dest.getParentFile();
		}
		File f = new File(dest, name + ext);
		if (f.exists()) {
			int i = 2;
			do { f = new File(dest, name + "-" + (i++) + ext); }
			while (f.exists());
		}
		return f;
	}
	
	private static String normalizeExtension(String ext) {
		if (ext == null) return "";
		if (ext.equals("") || ext.startsWith(".")) return ext;
		return "." + ext;
	}
}
