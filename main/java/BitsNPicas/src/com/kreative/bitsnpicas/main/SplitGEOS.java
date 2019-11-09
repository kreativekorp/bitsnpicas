package com.kreative.bitsnpicas.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.kreative.bitsnpicas.geos.GEOSFontFile;

public class SplitGEOS {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			File outputDir = null;
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
							for (int pointSize : inFont.getFontPointSizes()) {
								GEOSFontFile outFont = new GEOSFontFile();
								outFont.setFontName(inFont.getFontName());
								outFont.setClassTextString(inFont.getClassTextString());
								outFont.setDescriptionString(inFont.getDescriptionString());
								outFont.setFontID(inFont.getFontID());
								outFont.setFontPointSize(pointSize, inFont.getFontPointSize(pointSize));
								outFont.recalculate();
								
								File outfile = new File(
									((outputDir != null) ? outputDir : file.getParentFile()),
									(inFont.getFontName() + "." + pointSize + ".cvt")
								);
								DataOutputStream out = new DataOutputStream(new FileOutputStream(outfile));
								outFont.write(out);
								out.flush();
								out.close();
							}
							System.out.println(" DONE");
						} else {
							System.out.println(" ERROR: Not a GEOS font file.");
						}
					} catch (IOException e) {
						System.out.println(" ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
					}
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println("SplitGEOS - Split GEOS font files into individual point sizes.");
		System.out.println("  -d <path>     Specify directory for output files.");
	}
}
