package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.kreative.bitsnpicas.fon.FONDirectory;

public class MergeFON {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			File outputFile = null;
			List<File> dirs = new ArrayList<File>();
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (processingOptions && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingOptions = false;
					} else if (arg.equals("-o") && argi < args.length) {
						outputFile = new File(args[argi++]);
					} else if (arg.equals("--help")) {
						printHelp();
					} else {
						System.err.println("Unknown option: " + arg);
					}
				} else {
					dirs.add(new File(arg));
				}
			}
			if (outputFile != null && dirs.size() > 1) {
				System.err.println("An output file can be specified for one directory only.");
				return;
			}
			for (File dir : dirs) {
				try {
					System.out.print(dir.getPath() + "...");
					File file = (outputFile != null) ? outputFile : FONDirectory.fileFor(dir);
					List<String> warnings = new ArrayList<String>();
					int count = FONDirectory.merge(dir, file, warnings);
					System.out.println(" DONE: " + count + " fonts in " + file.getPath());
					for (String warning : warnings) System.out.println("  WARNING: " + warning);
				} catch (IOException e) {
					System.out.println(" ERROR: " + e.getMessage());
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println("MergeFON - Merge FNT files into Windows FON files.");
		System.out.println("  Every FNT file of a directory goes into one FON file:");
		System.out.println("  <name>.fon for the directory <name>.fon.files, as SplitFON");
		System.out.println("  makes it, or else the name of the directory with .fon after it.");
		System.out.println("  The text file fon.txt of the directory, if any, tells the rest");
		System.out.println("  of the FON file, a property to a line as name=value, in UTF-8.");
		System.out.println("  -o <path>     Specify output file.");
	}
}
