package com.kreative.bitsnpicas.main;

import java.io.*;
import com.kreative.bitsnpicas.*;
import com.kreative.bitsnpicas.exporter.NFNTBitmapFontExporter;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.S10BitmapFontImporter;
import com.kreative.bitsnpicas.importer.SFDBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SRFontBitmapFontImporter;
import com.kreative.bitsnpicas.transformer.BoldBitmapFontGlyphTransformer;

public class MakeNFNT {
	private static int id = 0;
	private static int size = 0;
	private static boolean snapSize = false;
	private static boolean resfork = false;
	private static String search = null, replace = null;
	private static boolean embolden = false;
	
	public static void main(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("-")) {
				if (arg.startsWith("-i")) {
					try {
						id = Integer.parseInt(arg.substring(2));
					} catch (NumberFormatException nfe) {
						id = 0;
					}
				}
				else if (arg.startsWith("-z")) {
					try {
						size = Integer.parseInt(arg.substring(2));
					} catch (NumberFormatException nfe) {
						size = 0;
					}
				}
				else if (arg.startsWith("-E")) {
					snapSize = false;
				}
				else if (arg.startsWith("-S")) {
					snapSize = true;
				}
				else if (arg.startsWith("-D")) {
					resfork = false;
				}
				else if (arg.startsWith("-R")) {
					resfork = System.getProperty("os.name").toUpperCase().contains("MAC OS");
				}
				else if (arg.startsWith("-s") || arg.startsWith("-f")) {
					search = arg.substring(2);
				}
				else if (arg.startsWith("-r")) {
					replace = arg.substring(2);
				}
				else if (arg.startsWith("-b")) {
					embolden = true;
				}
			} else try {
				System.out.print(arg+"...");
				if (arg.toLowerCase().endsWith(".sfd")) {
					BitmapFont[] myFonts = new SFDBitmapFontImporter().importFont(new File(arg));
					convert(myFonts, Font.NAME_POSTSCRIPT);
					System.out.println(" done.");
				}
				else if (arg.toLowerCase().endsWith(".s10")) {
					BitmapFont[] myFonts = new S10BitmapFontImporter().importFont(new File(arg));
					convert(myFonts, Font.NAME_FAMILY_AND_STYLE);
					System.out.println(" done.");
				}
				else if (arg.toLowerCase().endsWith(".png")) {
					BitmapFont[] myFonts = new SRFontBitmapFontImporter().importFont(new File(arg));
					convert(myFonts, Font.NAME_FAMILY_AND_STYLE);
					System.out.println(" done.");
				}
				else if (arg.toLowerCase().endsWith(".dsf")) {
					BitmapFont[] myFonts = new DSFBitmapFontImporter().importFont(new File(arg));
					convert(myFonts, Font.NAME_FAMILY);
					System.out.println(" done.");
				}
				else {
					System.out.println(" unknown type.");
				}
			} catch (IOException e) {
				System.out.println(" could not convert.");
			}
		}
	}
	
	public static void convert(BitmapFont[] myFonts, int nameType) throws IOException {
		if (search != null && search.length() > 0 && replace != null) {
			for (BitmapFont myFont : myFonts) {
				int[] nt = myFont.nameTypes();
				for (int n : nt) {
					String s = myFont.getName(n);
					if (s.contains(search)) {
						myFont.setName(n, s.replaceAll(search, replace));
					}
				}
			}
		}
		for (BitmapFont myFont : myFonts) {
			if (embolden) {
				myFont.transform(new BoldBitmapFontGlyphTransformer());
				myFont.setName(Font.NAME_STYLE, "Bold");
			}
			myFont.autoFillNames();
			File out;
			if (resfork) {
				out = makefile(myFont.getName(nameType), ".suit");
				out.createNewFile();
				out = new File(out, "..namedfork");
				out = new File(out, "rsrc");
			} else {
				out = makefile(myFont.getName(nameType), ".dfont");
			}
			if (id != 0) {
				if (size > 0) {
					new NFNTBitmapFontExporter(id, size, snapSize).exportFontToFile(myFont, out);
				} else {
					new NFNTBitmapFontExporter(id, snapSize).exportFontToFile(myFont, out);
				}
			} else {
				if (size > 0) {
					new NFNTBitmapFontExporter((float)size, snapSize).exportFontToFile(myFont, out);
				} else {
					new NFNTBitmapFontExporter(snapSize).exportFontToFile(myFont, out);
				}
			}
			if (resfork) {
				try {
					int e = Runtime.getRuntime().exec(new String[] {
							"/usr/bin/SetFile", "-t", "FFIL", "-c", "DMOV", out.getParentFile().getParentFile().getAbsolutePath()
					}).waitFor();
					if (e != 0) {
						System.out.print(" (failed to set codes: "+e+")");
					}
				} catch (InterruptedException ie) {}
			}
		}
	}
	
	public static File makefile(String basename, String ext) {
		if (!ext.startsWith(".")) ext = "." + ext;
		File f = new File(basename + ext);
		if (f.exists()) {
			int i = 2;
			do {
				f = new File(basename + "-" + (i++) + ext);
			} while (f.exists());
		}
		return f;
	}
}
