package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.IOException;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.mover.MoverFile;
import com.kreative.unicode.ttflib.DfontFile;

public class MergeSuit {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			File outputFile = null;
			
			boolean inRes;
			try { inRes = System.getProperty("os.name").toUpperCase().contains("MAC OS"); }
			catch (Exception e) { inRes = false; }
			
			DfontFile outrp = new DfontFile();
			MoverFile outmf;
			try { outmf = new MoverFile(outrp); }
			catch (IOException e) { e.printStackTrace(); return; }
			
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (processingOptions && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingOptions = false;
					} else if (arg.equals("-o") && argi < args.length) {
						outputFile = new File(args[argi++]);
					} else if (arg.equals("-D")) {
						inRes = false;
					} else if (arg.equals("-R")) {
						inRes = true;
					} else if (arg.equals("--help")) {
						printHelp();
					} else {
						System.err.println("Unknown option: " + arg);
					}
				} else {
					try {
						System.out.print(arg + "...");
						File file = new File(arg);
						File rsrc = new File(new File(file, "..namedfork"), "rsrc");
						DfontFile inrp; int count = 0;
						if ((inrp = open(file)) != null) count += process(outmf, inrp);
						if ((inrp = open(rsrc)) != null) count += process(outmf, inrp);
						System.out.println((count > 0) ? " READ" : " ERROR: No items found.");
					} catch (IOException e) {
						System.out.println(" ERROR: " + e);
					}
				}
			}
			if (outmf.isEmpty()) {
				System.out.println("No items found.");
				return;
			}
			if (outputFile == null) {
				if (outmf.size() == 1) {
					String name = outmf.get(0).name;
					outputFile = new File(inRes ? name : (name + ".dfont"));
				} else {
					String name = "Untitled Suitcase";
					outputFile = new File(inRes ? name : (name + ".dfont"));
				}
			}
			try {
				System.out.print(outputFile.getName() + "...");
				if (inRes) {
					outputFile.createNewFile();
					outrp.write(new File(new File(outputFile, "..namedfork"), "rsrc"));
					MacUtility.setTypeAndCreator(outputFile, "FFIL", "DMOV");
				} else {
					outrp.write(outputFile);
				}
				System.out.println(" DONE");
			} catch (IOException ioe) {
				System.out.println(" ERROR: " + ioe);
			}
		}
	}
	
	private static DfontFile open(File file) {
		try { return new DfontFile(file); }
		catch (IOException ioe) { return null; }
	}
	
	private static int process(MoverFile outmf, DfontFile inrp) throws IOException {
		MoverFile inmf = new MoverFile(inrp);
		for (int i = 0, n = inmf.size(); i < n; i++) {
			outmf.add(inmf.get(i));
		}
		return inmf.size();
	}
	
	private static void printHelp() {
		System.out.println("MergeSuit - Merge Macintosh suitcase and mover files into a single suitcase.");
		System.out.println("  -o <path>     Specify output file.");
		System.out.println("  -D            Write to data fork. (Default on non-Mac OS systems.)");
		System.out.println("  -R            Write to resource fork. (Default on Mac OS systems.)");
	}
}
