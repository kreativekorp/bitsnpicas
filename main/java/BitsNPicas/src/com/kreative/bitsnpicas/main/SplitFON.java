package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.IOException;
import com.kreative.bitsnpicas.fon.FONDirectory;

public class SplitFON {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			File outputDir = null;
			boolean replace = false;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (processingOptions && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingOptions = false;
					} else if (arg.equals("-d") && argi < args.length) {
						outputDir = new File(args[argi++]);
					} else if (arg.equals("-f")) {
						replace = true;
					} else if (arg.equals("--help")) {
						printHelp();
					} else {
						System.err.println("Unknown option: " + arg);
					}
				} else {
					try {
						System.out.print(arg + "...");
						File file = new File(arg);
						File dir = FONDirectory.directoryFor(file, outputDir);
						if (dir.exists() && !replace) {
							System.out.println(" ERROR: " + dir.getPath() + File.separator + " already exists; -f replaces it");
							continue;
						}
						int count = FONDirectory.split(file, dir, replace);
						System.out.println(" DONE: " + count + " fonts in " + dir.getPath() + File.separator);
					} catch (IOException e) {
						System.out.println(" ERROR: " + e.getMessage());
					}
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println("SplitFON - Split Windows FON files into FNT files.");
		System.out.println("  The fonts of <name>.fon go into the directory <name>.fon.files,");
		System.out.println("  with a text file of what else the FON file tells, fon.txt,");
		System.out.println("  for MergeFON to make it again. The directory must not exist.");
		System.out.println("  -d <path>     Specify directory for output directories.");
		System.out.println("  -f            Replace output directories, deleting every file in them.");
	}
}
