package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyphTransformer;
import com.kreative.bitsnpicas.transformer.BoldBitmapFontGlyphTransformer;
import com.kreative.bitsnpicas.unicode.EncodingList;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class ConvertBitmap {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			Options o = new Options();
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (processingOptions && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingOptions = false;
					} else if (arg.equals("-s") && argi < args.length) {
						String s = args[argi++];
						if (s.length() == 0) {
							o.nameSearchPattern = null;
						} else try {
							o.nameSearchPattern = Pattern.compile(s);
						} catch (PatternSyntaxException e) {
							o.nameSearchPattern = Pattern.compile(Pattern.quote(s));
						}
					} else if (arg.equals("-r") && argi < args.length) {
						o.nameReplacePattern = args[argi++];
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
					} else if (arg.equals("-p") && argi < args.length) {
						String s = args[argi++];
						boolean done = loadPreset(o, s);
						if (!done) System.err.println("Unknown preset: " + s);
					} else if (arg.equals("-i") && argi < args.length) {
						o.oo.macID = parseInt(args[argi++], 0);
					} else if (arg.equals("-z") && argi < args.length) {
						o.oo.macSize = parseInt(args[argi++], 0);
					} else if (arg.equals("-E")) {
						o.oo.macSnapSize = false;
					} else if (arg.equals("-S")) {
						o.oo.macSnapSize = true;
					} else if (arg.equals("-e") && argi < args.length) {
						o.io.encodingName = o.oo.encodingName = args[argi++];
					} else if (arg.equals("-ie") && argi < args.length) {
						o.io.encodingName = args[argi++];
					} else if (arg.equals("-oe") && argi < args.length) {
						o.oo.encodingName = args[argi++];
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
		System.out.println("  -i <number>   Macintosh font ID number (for nfnt).");
		System.out.println("  -z <number>   Macintosh font size (for nfnt).");
		System.out.println("  -E            Use any font size (for nfnt).");
		System.out.println("  -S            Use only standard font sizes (for nfnt).");
		System.out.println("                    (9, 10, 12, 14, 18, 24, 36, 48, and 72.)");
		System.out.println("  -e <enc>      Use the specified encoding (for nfnt, fzx, sbf).");
		System.out.println("  -ie <enc>     Use the specified encoding for reading only.");
		System.out.println("  -oe <enc>     Use the specified encoding for writing only.");
		List<String> encs = new ArrayList<String>();
		for (EncodingTable e : EncodingList.instance()) {
			encs.add(e.name);
		}
		printHelpList(encs);
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
		public Pattern nameSearchPattern = null;
		public String nameReplacePattern = "";
		public List<BitmapFontGlyphTransformer> transform = new ArrayList<BitmapFontGlyphTransformer>();
		public File dest = null;
		public String format = "ttf";
	}
	
	private static int parseInt(String s, int def) {
		try { return Integer.parseInt(s); }
		catch (NumberFormatException e) { return def; }
	}
	
	private static boolean loadPreset(Options o, String name) {
		name = name.toLowerCase();
		if (name.equals("") || name.equals("none")) {
			o.nameSearchPattern = null;
			o.nameReplacePattern = null;
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("apple2") || name.equals("apple2-40col")) {
			o.nameSearchPattern = Pattern.compile("Apple II");
			o.nameReplacePattern = "Print Char 21";
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("apple2-80col")) {
			o.nameSearchPattern = Pattern.compile("Apple II");
			o.nameReplacePattern = "PR Number 3";
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("apple2-hgr")) {
			o.nameSearchPattern = Pattern.compile("II");
			o.nameReplacePattern = "II HGR";
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("apple2-dhr")) {
			o.nameSearchPattern = Pattern.compile("II");
			o.nameReplacePattern = "II DHR";
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("lisa") || name.equals("lisa-2x3y")) {
			o.nameSearchPattern = Pattern.compile("Raw");
			o.nameReplacePattern = "2X3Y";
			o.oo.xSize = 80; o.oo.ySize = 120;
			return true;
		} else if (name.equals("lisa-raw")) {
			o.nameSearchPattern = Pattern.compile("2X3Y");
			o.nameReplacePattern = "Raw";
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("iigs-320")) {
			o.nameSearchPattern = Pattern.compile("Shaston");
			o.nameReplacePattern = "Shaston 320";
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("iigs-640")) {
			o.nameSearchPattern = Pattern.compile("Shaston");
			o.nameReplacePattern = "Shaston 640";
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("iigs-320h")) {
			o.nameSearchPattern = Pattern.compile("Shaston");
			o.nameReplacePattern = "Shaston Hi 320";
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("iigs-640h")) {
			o.nameSearchPattern = Pattern.compile("Shaston");
			o.nameReplacePattern = "Shaston Hi 640";
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("atari") || name.equals("atari-40col")) {
			o.nameSearchPattern = Pattern.compile("Colleen");
			o.nameReplacePattern = "Candy";
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("atari-80col")) {
			o.nameSearchPattern = Pattern.compile("Candy");
			o.nameReplacePattern = "Colleen";
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("commodore") || name.equals("commodore-40col")) {
			o.nameSearchPattern = null;
			o.nameReplacePattern = null;
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("commodore-20col")) {
			o.nameSearchPattern = Pattern.compile("Pet.*$");
			o.nameReplacePattern = "$0 2X";
			o.oo.xSize = 200; o.oo.ySize = 100;
			return true;
		} else if (name.equals("commodore-80col")) {
			o.nameSearchPattern = Pattern.compile("Pet.*$");
			o.nameReplacePattern = "$0 2Y";
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("trs80") || name.equals("trs80-64col") || name.equals("trs80-m1") || name.equals("trs80-m1-64col")) {
			o.nameSearchPattern = Pattern.compile("Raw");
			o.nameReplacePattern = "64C 2X3Y";
			o.oo.xSize = 80; o.oo.ySize = 120;
			return true;
		} else if (name.equals("trs80-32col") || name.equals("trs80-m1-32col")) {
			o.nameSearchPattern = Pattern.compile("Raw");
			o.nameReplacePattern = "32C 4X3Y";
			o.oo.xSize = 160; o.oo.ySize = 120;
			return true;
		} else if (name.equals("trs80-m2")) {
			o.nameSearchPattern = Pattern.compile("Raw");
			o.nameReplacePattern = "2Y";
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("trs80-m3") || name.equals("trs80-m3-64col")) {
			o.nameSearchPattern = Pattern.compile("Raw");
			o.nameReplacePattern = "64C";
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("trs80-m3-32col")) {
			o.nameSearchPattern = Pattern.compile("Raw");
			o.nameReplacePattern = "32C";
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else if (name.equals("trs80-m4") || name.equals("trs80-m4-64col")) {
			o.nameSearchPattern = Pattern.compile("Raw");
			o.nameReplacePattern = "64C";
			o.oo.xSize = 100; o.oo.ySize = 160;
			return true;
		} else if (name.equals("trs80-m4-32col")) {
			o.nameSearchPattern = Pattern.compile("Raw");
			o.nameReplacePattern = "32C";
			o.oo.xSize = 100; o.oo.ySize = 80;
			return true;
		} else if (name.equals("trs80-m4-80col")) {
			o.nameSearchPattern = Pattern.compile("Raw");
			o.nameReplacePattern = "80C";
			o.oo.xSize = 100; o.oo.ySize = 200;
			return true;
		} else if (name.equals("trs80-m4-40col")) {
			o.nameSearchPattern = Pattern.compile("Raw");
			o.nameReplacePattern = "40C";
			o.oo.xSize = 100; o.oo.ySize = 100;
			return true;
		} else {
			return false;
		}
	}
	
	private static void transformFont(BitmapFont font, Options o) {
		if (o.nameSearchPattern != null && o.nameReplacePattern != null) {
			for (int key : font.nameTypes()) {
				Matcher m = o.nameSearchPattern.matcher(font.getName(key));
				if (m.find()) font.setName(key, m.replaceAll(o.nameReplacePattern));
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
