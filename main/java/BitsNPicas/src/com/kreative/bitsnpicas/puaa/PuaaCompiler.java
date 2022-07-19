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
		List<File> inputFiles = new ArrayList<File>();
		List<File> outputFiles = new ArrayList<File>();
		List<File> defaultList = dataFiles;
		boolean parsingOptions = true;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parsingOptions && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parsingOptions = false;
				} else if (arg.equals("-d") && argi < args.length) {
					dataFiles.add(new File(args[argi++]));
				} else if (arg.equals("-i") && argi < args.length) {
					inputFiles.add(new File(args[argi++]));
				} else if (arg.equals("-o") && argi < args.length) {
					outputFiles.add(new File(args[argi++]));
				} else if (arg.equals("-D")) {
					defaultList = dataFiles;
				} else if (arg.equals("-I")) {
					defaultList = inputFiles;
				} else if (arg.equals("-O")) {
					defaultList = outputFiles;
				} else if (arg.equals("--help")) {
					printHelp();
				} else {
					System.err.println("Unknown option: " + arg);
				}
			} else {
				defaultList.add(new File(arg));
			}
		}
		
		if (dataFiles.isEmpty()) return;
		PuaaTable puaa = read(dataFiles);
		
		if (inputFiles.isEmpty() && outputFiles.isEmpty()) {
			File file = new File("puaa.out");
			write(puaa, file, file);
			return;
		}
		if (inputFiles.isEmpty() || outputFiles.isEmpty()) {
			for (File file : inputFiles) write(puaa, file, file);
			for (File file : outputFiles) write(puaa, file, file);
			return;
		}
		if (inputFiles.size() == 1 && outputFiles.size() == 1) {
			write(puaa, inputFiles.get(0), outputFiles.get(0));
			return;
		}
		
		if (inputFiles.size() > 1) System.err.println("Too many input files.");
		if (outputFiles.size() > 1) System.err.println("Too many output files.");
	}
	
	private static PuaaTable read(List<File> dataFiles) {
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
		return puaa;
	}
	
	private static void write(PuaaTable puaa, File inputFile, File outputFile) {
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
		System.out.println("  -d <path>     Specify UCD data file or directory.");
		System.out.println("  -i <path>     Specify source TrueType file.");
		System.out.println("  -o <path>     Specify destination TrueType file.");
		System.out.println("  -D            Process arguments as UCD data files.");
		System.out.println("  -I            Process arguments as source files.");
		System.out.println("  -O            Process arguments as destination files.");
		System.out.println("  --            Process remaining arguments as file names.");
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
				Scanner in = new Scanner(new FileInputStream(src), "UTF-8");
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
