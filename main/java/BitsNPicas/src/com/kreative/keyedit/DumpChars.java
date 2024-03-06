package com.kreative.keyedit;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

public class DumpChars {
	public static void main(String[] args) throws IOException {
		KeyboardFormat inFormat = null;
		ArrayList<File> inFiles = new ArrayList<File>();
		boolean includeDeadKeys = true;
		boolean includeLongPress = false;
		boolean parseOptions = true;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parseOptions && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parseOptions = false;
				} else if (arg.equalsIgnoreCase("-f") && argi < args.length) {
					inFormat = KeyboardFormat.forInputFormatName(args[argi++]);
				} else if (arg.equalsIgnoreCase("-i") && argi < args.length) {
					inFiles.add(new File(args[argi++]));
				} else if (arg.equals("-d")) {
					includeDeadKeys = true;
				} else if (arg.equals("-D")) {
					includeDeadKeys = false;
				} else if (arg.equals("-l")) {
					includeLongPress = true;
				} else if (arg.equals("-L")) {
					includeLongPress = false;
				} else {
					System.err.println("Unknown option: "+ arg);
					return;
				}
			} else {
				inFiles.add(new File(arg));
			}
		}
		
		TreeSet<Integer> all = new TreeSet<Integer>();
		for (File file : inFiles) {
			KeyboardFormat format = inFormat;
			if (format == null) {
				format = KeyboardFormat.forInputFile(file);
				if (format == null) {
					System.err.println("Error reading " + file + ": Unknown format");
					continue;
				}
			}
			try {
				KeyboardMapping km = format.read(file);
				km.getAllOutputs(all, includeDeadKeys, includeLongPress);
			} catch (IOException e) {
				System.err.println("Error reading " + file + ": " + e);
				continue;
			}
		}
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
		for (int output : all) if (output >= 32) out.print(Character.toChars(output));
		out.println();
	}
}
