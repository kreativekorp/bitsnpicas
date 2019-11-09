package com.kreative.bitsnpicas.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.kreative.bitsnpicas.geos.GEOSFontFile;

public class MergeGEOS {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			Map<String,GEOSFontFile> families = new HashMap<String,GEOSFontFile>();
			File outputDir = new File(".");
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (processingOptions && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingOptions = false;
					} else if (arg.equals("-d") && argi < args.length) {
						outputDir = new File(args[argi++]);
					} else if (arg.equals("--help")) {
						printHelp();
					} else {
						System.err.println("Unknown option: " + arg);
					}
				} else {
					try {
						System.out.print(arg + "...");
						File file = new File(arg);
						DataInputStream in = new DataInputStream(new FileInputStream(file));
						GEOSFontFile inFont = new GEOSFontFile(in);
						in.close();
						if (inFont.isValid()) {
							String family = inFont.getFontName();
							if (families.containsKey(family)) {
								GEOSFontFile outFont = families.get(family);
								for (int pointSize : inFont.getFontPointSizes()) {
									outFont.setFontPointSize(pointSize, inFont.getFontPointSize(pointSize));
								}
							} else {
								families.put(family, inFont);
							}
							System.out.println(" READ");
						} else {
							System.out.println(" ERROR: Not a GEOS font file.");
						}
					} catch (IOException e) {
						System.out.println(" ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
					}
				}
			}
			for (Map.Entry<String,GEOSFontFile> e : families.entrySet()) {
				try {
					System.out.print(e.getKey() + "...");
					GEOSFontFile outFont = e.getValue();
					outFont.recalculate();
					
					File outfile = new File(outputDir, e.getKey() + ".cvt");
					DataOutputStream out = new DataOutputStream(new FileOutputStream(outfile));
					outFont.write(out);
					out.flush();
					out.close();
					System.out.println(" DONE");
				} catch (IOException ioe) {
					System.out.println(" ERROR: " + ioe.getClass().getSimpleName() + ": " + ioe.getMessage());
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println("MergeGEOS - Merge GEOS font files of the same family into a single file.");
		System.out.println("  -d <path>     Specify directory for output files.");
	}
}
