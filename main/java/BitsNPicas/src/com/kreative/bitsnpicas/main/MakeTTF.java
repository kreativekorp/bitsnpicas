package com.kreative.bitsnpicas.main;

import java.io.*;
import java.util.regex.Pattern;
import com.kreative.bitsnpicas.*;
import com.kreative.bitsnpicas.exporter.TTFBitmapFontExporter;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.S10BitmapFontImporter;
import com.kreative.bitsnpicas.importer.SFDBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SRFontBitmapFontImporter;
import com.kreative.bitsnpicas.transformer.BoldBitmapFontGlyphTransformer;

public class MakeTTF {
	private static int xsize = 100, ysize = 100;
	private static String search = null, replace = null;
	private static boolean embolden = false;
	
	public static void main(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("-")) {
				if (arg.startsWith("-w") || arg.startsWith("-x")) {
					try {
						xsize = Integer.parseInt(arg.substring(2));
					} catch (NumberFormatException nfe) {}
				}
				else if (arg.startsWith("-h") || arg.startsWith("-y")) {
					try {
						ysize = Integer.parseInt(arg.substring(2));
					} catch (NumberFormatException nfe) {}
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
				else if (arg.startsWith("-D")) {
					xsize = 100; ysize = 100; search = null; replace = null;
				}
				else if (arg.startsWith("-LR")) {
					xsize = 100; ysize = 100; search = "2X3Y"; replace = "Raw";
				}
				else if (arg.startsWith("-L")) {
					xsize = 80; ysize = 120; search = "Raw"; replace = "2X3Y";
				}
				else if (arg.startsWith("-A4")) {
					xsize = 100; ysize = 100; search = "Apple II"; replace = "Print Char 21";
				}
				else if (arg.startsWith("-A8")) {
					xsize = 100; ysize = 200; search = "Apple II"; replace = "PR Number 3";
				}
				else if (arg.startsWith("-AB4")) {
					xsize = 100; ysize = 100; search = "II"; replace = "II HGR";
				}
				else if (arg.startsWith("-AB8")) {
					xsize = 100; ysize = 200; search = "II"; replace = "II DHR";
				}
				else if (arg.startsWith("-AS4")) {
					xsize = 100; ysize = 100; search = "Shaston"; replace = "Shaston 320";
				}
				else if (arg.startsWith("-AS8")) {
					xsize = 100; ysize = 200; search = "Shaston"; replace = "Shaston 640";
				}
				else if (arg.startsWith("-ASH4")) {
					xsize = 100; ysize = 100; search = "Shaston"; replace = "Shaston Hi 320";
				}
				else if (arg.startsWith("-ASH8")) {
					xsize = 100; ysize = 200; search = "Shaston"; replace = "Shaston Hi 640";
				}
				else if (arg.startsWith("-At4")) {
					xsize = 100; ysize = 100; search = "Colleen"; replace = "Candy";
				}
				else if (arg.startsWith("-At8")) {
					xsize = 100; ysize = 200; search = "Candy"; replace = "Colleen";
				}
				else if (arg.startsWith("-C2")) {
					xsize = 200; ysize = 100; search = "Pet.*$"; replace = "$0 2X";
				}
				else if (arg.startsWith("-C4")) {
					xsize = 100; ysize = 100; search = null; replace = null;
				}
				else if (arg.startsWith("-C8")) {
					xsize = 100; ysize = 200; search = "Pet.*$"; replace = "$0 2Y";
				}
				else if (arg.startsWith("-T64")) {
					xsize = 80; ysize = 120; search = "Raw"; replace = "64C 2X3Y";
				}
				else if (arg.startsWith("-T32")) {
					xsize = 160; ysize = 120; search = "Raw"; replace = "32C 4X3Y";
				}
				else if (arg.startsWith("-T2")) {
					xsize = 100; ysize = 200; search = "Raw"; replace = "2Y";
				}
				else if (arg.startsWith("-T364")) {
					xsize = 100; ysize = 200; search = "Raw"; replace = "64C";
				}
				else if (arg.startsWith("-T332")) {
					xsize = 100; ysize = 100; search = "Raw"; replace = "32C";
				}
				else if (arg.startsWith("-T464")) {
					xsize = 100; ysize = 160; search = "Raw"; replace = "64C";
				}
				else if (arg.startsWith("-T432")) {
					xsize = 100; ysize = 80; search = "Raw"; replace = "32C";
				}
				else if (arg.startsWith("-T480")) {
					xsize = 100; ysize = 200; search = "Raw"; replace = "80C";
				}
				else if (arg.startsWith("-T440")) {
					xsize = 100; ysize = 100; search = "Raw"; replace = "40C";
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
			Pattern p = Pattern.compile(search);
			for (BitmapFont myFont : myFonts) {
				int[] nt = myFont.nameTypes();
				for (int n : nt) {
					String s = myFont.getName(n);
					if (p.matcher(s).find()) {
						myFont.setName(n, p.matcher(s).replaceAll(replace));
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
			File out = makefile(myFont.getName(nameType), ".ttf");
			new TTFBitmapFontExporter(xsize, ysize).exportFontToFile(myFont, out);
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
