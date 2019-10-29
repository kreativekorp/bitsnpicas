package com.kreative.bitsnpicas.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import com.kreative.bitsnpicas.geos.CBMConstants;
import com.kreative.bitsnpicas.geos.ConvertFile;

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
						ConvertFile cvt = new ConvertFile();
						cvt.read(in);
						in.close();
						if (
							cvt.infoBlock != null &&
							cvt.infoBlock.geosFileType == CBMConstants.GEOS_FILE_TYPE_FONT &&
							cvt.vlirData != null
						) {
							for (int fontSize = 0; fontSize < cvt.vlirData.size(); fontSize++) {
								byte[] data = cvt.vlirData.get(fontSize);
								if (data.length > 8) {
									ConvertFile outcvt = new ConvertFile(
										CBMConstants.CBM_FILE_TYPE_CLOSED | CBMConstants.CBM_FILE_TYPE_USR,
										CBMConstants.GEOS_FILE_TYPE_FONT, CBMConstants.FILE_STRUCTURE_VLIR
									);
									outcvt.directoryBlock = cvt.directoryBlock;
									outcvt.infoBlock = cvt.infoBlock;
									outcvt.infoBlock.setFontPointSizes(Arrays.asList(fontSize));
									outcvt.infoBlock.setFontRecordLengths(Arrays.asList(data.length));
									for (int i = 0; i < 127; i++) {
										outcvt.vlirData.add((i == fontSize) ? data : new byte[0]);
									}
									outcvt.recalculate();
									
									File outfile = new File(
										((outputDir != null) ? outputDir : file.getParentFile()),
										(cvt.directoryBlock.getFileName(true, true) + "." + fontSize + ".cvt")
									);
									DataOutputStream out = new DataOutputStream(new FileOutputStream(outfile));
									outcvt.write(out);
									out.flush();
									out.close();
								}
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
