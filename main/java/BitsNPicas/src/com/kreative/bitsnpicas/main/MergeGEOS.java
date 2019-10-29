package com.kreative.bitsnpicas.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.bitsnpicas.geos.CBMConstants;
import com.kreative.bitsnpicas.geos.ConvertFile;

public class MergeGEOS {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			Map<String,ConvertFile> families = new HashMap<String,ConvertFile>();
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
						ConvertFile cvt = new ConvertFile();
						cvt.read(in);
						in.close();
						if (
							cvt.infoBlock != null &&
							cvt.infoBlock.geosFileType == CBMConstants.GEOS_FILE_TYPE_FONT &&
							cvt.vlirData != null
						) {
							String family = cvt.directoryBlock.getFileName(true, true);
							if (families.containsKey(family)) {
								ConvertFile outcvt = families.get(family);
								for (int fontSize = 0; fontSize < cvt.vlirData.size(); fontSize++) {
									byte[] data = cvt.vlirData.get(fontSize);
									if (data.length > 8) outcvt.vlirData.set(fontSize, data);
								}
							} else {
								families.put(family, cvt);
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
			for (Map.Entry<String,ConvertFile> e : families.entrySet()) {
				try {
					System.out.print(e.getKey() + "...");
					ConvertFile outcvt = e.getValue();
					List<Integer> pointSizes = new ArrayList<Integer>();
					List<Integer> recordLengths = new ArrayList<Integer>();
					for (int fontSize = 0; fontSize < outcvt.vlirData.size(); fontSize++) {
						byte[] data = outcvt.vlirData.get(fontSize);
						if (data.length > 8) {
							pointSizes.add(fontSize);
							recordLengths.add(data.length);
						}
					}
					outcvt.infoBlock.setFontPointSizes(pointSizes);
					outcvt.infoBlock.setFontRecordLengths(recordLengths);
					outcvt.recalculate();
					
					File outfile = new File(outputDir, e.getKey() + ".cvt");
					DataOutputStream out = new DataOutputStream(new FileOutputStream(outfile));
					outcvt.write(out);
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
