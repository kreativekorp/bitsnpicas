package com.kreative.bitsnpicas.puaa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import com.kreative.bitsnpicas.truetype.PuaaTable;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class PuaaDecompiler {
	private static final PuaaCodecRegistry registry = PuaaCodecRegistry.instance;
	
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
			return;
		}
		
		List<File> inputFiles = new ArrayList<File>();
		List<File> outputFiles = new ArrayList<File>();
		List<File> defaultList = inputFiles;
		boolean parsingOptions = true;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parsingOptions && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parsingOptions = false;
				} else if (arg.equals("-i") && argi < args.length) {
					inputFiles.add(new File(args[argi++]));
				} else if (arg.equals("-o") && argi < args.length) {
					outputFiles.add(new File(args[argi++]));
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
		
		if (inputFiles.isEmpty()) return;
		if (outputFiles.isEmpty()) outputFiles.add(new File("puaa.d"));
		
		if (inputFiles.size() == 1) {
			PuaaTable puaa = read(inputFiles.get(0));
			if (puaa != null) {
				for (File outputFile : outputFiles) {
					write(puaa, outputFile);
				}
			}
			return;
		}
		if (outputFiles.size() == 1) {
			for (File inputFile : inputFiles) {
				PuaaTable puaa = read(inputFile);
				if (puaa != null) {
					write(puaa, outputFiles.get(0));
				}
			}
			return;
		}
		
		if (inputFiles.size() > 1) System.err.println("Too many input files.");
		if (outputFiles.size() > 1) System.err.println("Too many output files.");
	}
	
	private static PuaaTable read(File src) {
		System.out.print("Reading " + src.getName() + "...");
		try {
			PuaaTable puaa = extract(src);
			if (puaa == null) {
				System.out.println(" ERROR: table not found");
			} else {
				System.out.println(" DONE");
			}
			return puaa;
		} catch (IOException e) {
			System.out.println(" ERROR: " + e);
			return null;
		}
	}
	
	private static void write(PuaaTable puaa, File dst) {
		System.out.print("Writing " + dst.getName() + "...");
		try {
			dst.mkdirs();
			decompile(puaa, dst);
			System.out.println(" DONE");
		} catch (IOException e) {
			System.out.println(" ERROR: " + e);
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("PuaaDecompiler - Create UCD files from character properties in TrueType files.");
		System.out.println();
		System.out.println("  -i <path>     Specify source TrueType file.");
		System.out.println("  -o <path>     Specify destination directory.");
		System.out.println("  -I            Process arguments as source files.");
		System.out.println("  -O            Process arguments as destination files.");
		System.out.println("  --            Process remaining arguments as file names.");
		System.out.println();
		System.out.println("Output files will be in the format of the Unicode Character Database");
		System.out.println("(although without any comments) and will be named accordingly:");
		System.out.println();
		registry.printFileNames();
		System.out.println();
		System.out.println("Only files for properties present in the source files will be generated.");
		System.out.println();
	}
	
	public static void decompile(PuaaTable puaa, File dst) throws IOException {
		for (PuaaCodec codec : registry.getCodecs()) {
			for (String property : codec.getPropertyNames()) {
				if (puaa.getSubtable(property) != null) {
					File dstFile = new File(dst, codec.getFileName());
					FileOutputStream fos = new FileOutputStream(dstFile);
					OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
					PrintWriter pw = new PrintWriter(osw, true);
					codec.decompile(puaa, pw);
					pw.flush();
					pw.close();
					break;
				}
			}
		}
	}
	
	public static PuaaTable extract(File src) throws IOException {
		long length = src.length();
		if (length < 12) throw new IOException("file too small");
		if (length > Integer.MAX_VALUE) throw new IOException("file too large");
		
		byte[] data = new byte[(int)length];
		FileInputStream in = new FileInputStream(src);
		in.read(data);
		in.close();
		
		TrueTypeFile ttf = new TrueTypeFile();
		ttf.decompile(data);
		return (PuaaTable)ttf.getByTableName("PUAA");
	}
}
