package com.kreative.keyedit;

import java.io.File;
import java.io.IOException;

public class ConvertKeyboard {
	public static void main(String[] args) {
		KeyboardFormat inFormat = null, outFormat = null;
		File inFile = null, outFile = null;
		boolean parseOptions = true;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parseOptions && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parseOptions = false;
				} else if (arg.equalsIgnoreCase("-if") && argi < args.length) {
					inFormat = KeyboardFormat.forFormatName(args[argi++]);
				} else if (arg.equalsIgnoreCase("-of") && argi < args.length) {
					outFormat = KeyboardFormat.forFormatName(args[argi++]);
				} else if (arg.equalsIgnoreCase("-i") && argi < args.length) {
					inFile = new File(args[argi++]);
				} else if (arg.equalsIgnoreCase("-o") && argi < args.length) {
					outFile = new File(args[argi++]);
				} else {
					printHelp(); return;
				}
			} else if (inFile == null) {
				inFile = new File(arg);
			} else if (outFile == null) {
				outFile = new File(arg);
			} else {
				printHelp(); return;
			}
		}
		
		if (inFile == null || outFile == null) { printHelp(); return; }
		if (inFormat == null) inFormat = KeyboardFormat.forInputFile(inFile);
		if (outFormat == null) outFormat = KeyboardFormat.forOutputFile(outFile);
		if (inFormat == null || outFormat == null) { printHelp(); return; }
		
		KeyboardMapping km;
		try { km = inFormat.read(inFile); }
		catch (IOException e) { System.err.println("Error reading: " + e.getMessage()); return; }
		
		km.autoFill();
		try { outFormat.write(outFile, km); }
		catch (IOException e) { System.err.println("Error writing: " + e.getMessage()); return; }
	}
	
	private static void printHelp() {
		System.err.println("Usage: ConvertKeyboard [-if <input-format>] [-of <output-format>]");
		System.err.println("                       [-i] <input-file> [-o] <output-file>");
	}
}
