package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.mover.MoverFile;
import com.kreative.bitsnpicas.mover.ResourceBundle;
import com.kreative.rsrc.MacResourceArray;
import com.kreative.rsrc.MacResourceFile;
import com.kreative.rsrc.MacResourceProvider;

public class SplitSuit {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			File outputDir = null;
			
			boolean inRes;
			try { inRes = System.getProperty("os.name").toUpperCase().contains("MAC OS"); }
			catch (Exception e) { inRes = false; }
			
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (processingOptions && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingOptions = false;
					} else if (arg.equals("-d") && argi < args.length) {
						outputDir = new File(args[argi++]);
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
						File parent = ((outputDir != null) ? outputDir : file.getParentFile());
						MacResourceProvider rp; int count = 0;
						if ((rp = open(file)) != null) { count += process(parent, inRes, rp); rp.close(); }
						if ((rp = open(rsrc)) != null) { count += process(parent, inRes, rp); rp.close(); }
						System.out.println((count > 0) ? " DONE" : " ERROR: No items found.");
					} catch (IOException e) {
						System.out.println(" ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
					}
				}
			}
		}
	}
	
	private static MacResourceProvider open(File file) {
		try { return new MacResourceFile(file, "r", MacResourceFile.CREATE_NEVER); }
		catch (IOException ioe) { return null; }
	}
	
	private static int process(File parent, boolean inRes, MacResourceProvider rp) throws IOException {
		MoverFile mf = new MoverFile(rp);
		for (int i = 0, n = mf.size(); i < n; i++) {
			export(parent, inRes, mf.get(i));
		}
		return mf.size();
	}
	
	private static void export(File parent, boolean inRes, ResourceBundle rb) throws IOException {
		File file, rsrc;
		if (inRes) {
			file = new File(parent, rb.name);
			file.createNewFile();
			rsrc = new File(new File(file, "..namedfork"), "rsrc");
		} else {
			file = new File(parent, rb.name + ".dfont");
			rsrc = file;
		}
		
		MacResourceArray rp = new MacResourceArray();
		MoverFile mf = new MoverFile(rp);
		mf.add(rb);
		rp.close();
		
		FileOutputStream out = new FileOutputStream(rsrc);
		out.write(rp.getBytes());
		out.flush();
		out.close();
		
		if (inRes) MacUtility.setTypeAndCreator(file, rb.moverType, "movr");
	}
	
	private static void printHelp() {
		System.out.println("SplitSuit - Split Macintosh suitcase files into separate mover files.");
		System.out.println("  -d <path>     Specify directory for output files.");
		System.out.println("  -D            Write to data fork. (Default on non-Mac OS systems.)");
		System.out.println("  -R            Write to resource fork. (Default on Mac OS systems.)");
	}
}
