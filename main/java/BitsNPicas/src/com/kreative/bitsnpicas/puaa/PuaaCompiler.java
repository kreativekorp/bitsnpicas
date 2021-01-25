package com.kreative.bitsnpicas.puaa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaTable;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class PuaaCompiler {
	private static final PuaaCodecRegistry registry = PuaaCodecRegistry.instance;
	
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
			return;
		}
		
		List<File> dataFiles = new ArrayList<File>();
		File inputFile = null;
		File outputFile = null;
		boolean parsingOptions = true;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parsingOptions && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parsingOptions = false;
				} else if (arg.equals("-i") && argi < args.length) {
					inputFile = new File(args[argi++]);
				} else if (arg.equals("-o") && argi < args.length) {
					outputFile = new File(args[argi++]);
				} else if (arg.equals("--help")) {
					printHelp();
				} else {
					System.err.println("Unknown option: " + arg);
				}
			} else {
				dataFiles.add(new File(arg));
			}
		}
		
		if (dataFiles.isEmpty()) return;
		
		if (inputFile == null && outputFile == null) {
			inputFile = outputFile = new File("puaa.out");
		} else if (inputFile == null) {
			inputFile = outputFile;
		} else if (outputFile == null) {
			outputFile = inputFile;
		}
		
		PuaaTable puaa = new PuaaTable();
		for (File dataFile : dataFiles) {
			System.out.print("Reading " + dataFile.getName() + "...");
			try {
				compile(puaa, dataFile);
				System.out.println(" DONE");
			} catch (IOException e) {
				System.out.println(" ERROR: " + e);
			}
		}
		
		System.out.print("Writing " + outputFile.getName() + "...");
		try {
			inject(puaa, inputFile, outputFile);
			System.out.println(" DONE");
		} catch (IOException e) {
			System.out.println(" ERROR: " + e);
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("PuaaCompiler - Add Unicode Character Database properties to TrueType files.");
		System.out.println();
		System.out.println("  -i <path>     Specify source TrueType file.");
		System.out.println("  -o <path>     Specify destination TrueType file.");
		System.out.println("  --            Process remaining arguments as UCD data files.");
		System.out.println();
		System.out.println("Source and destination may be the same file.");
		System.out.println("Other files specified must be in the format of the");
		System.out.println("Unicode Character Database and be named accordingly:");
		System.out.println();
		registry.printFileNames();
		System.out.println();
		System.out.println("Files other than those listed above will be ignored.");
		System.out.println();
	}
	
	public static void compile(PuaaTable puaa, File src) throws IOException {
		if (src.isDirectory()) {
			for (File child : src.listFiles()) {
				if (!child.getName().startsWith(".")) {
					compile(puaa, child);
				}
			}
		} else {
			PuaaCodec codec = registry.getCodec(src.getName());
			if (codec != null) {
				Scanner in = new Scanner(src, "UTF-8");
				codec.compile(puaa, in);
				in.close();
			}
		}
	}
	
	public static void inject(PuaaTable puaa, File src, File dst) throws IOException {
		TrueTypeFile ttf = new TrueTypeFile();
		
		long length = src.length();
		if (length > 0) {
			if (length < 12) throw new IOException("file too small");
			if (length > Integer.MAX_VALUE) throw new IOException("file too large");
			
			byte[] data = new byte[(int)length];
			FileInputStream in = new FileInputStream(src);
			in.read(data);
			in.close();
			
			ttf.interpret = false;
			ttf.decompile(data);
			ttf.remove(ttf.getByTableName("PUAA"));
		} else {
			ttf.scaler = 0x50554141;
		}
		
		ttf.add(puaa);
		byte[] data = ttf.compile();
		
		FileOutputStream out = new FileOutputStream(dst);
		out.write(data);
		out.flush();
		out.close();
	}
}
