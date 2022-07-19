package com.kreative.mapedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Remap {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingArgs = true;
			boolean loadedRemapping = false;
			Map<Integer,Integer> map = new HashMap<Integer,Integer>();
			File outputFile = null;
			File outputDir = null;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (processingArgs && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingArgs = false;
					} else if (arg.equals("-m") && argi < args.length) {
						arg = args[argi++];
						loadedRemapping = true;
						try {
							if (readRemapping(map, arg)) {
								System.out.println("Loaded " + arg);
							} else {
								System.err.println("No mappings found in " + arg);
							}
						} catch (IOException e) {
							System.err.println("Could not read " + arg + ": " + e.getMessage());
						}
					} else if (arg.equals("-o") && argi < args.length) {
						arg = args[argi++];
						outputFile = new File(arg);
					} else if (arg.equals("-d") && argi < args.length) {
						arg = args[argi++];
						outputDir = new File(arg);
					} else if (arg.equals("--help")) {
						printHelp();
					} else {
						System.err.println("Unknown option: " + arg);
					}
				} else if (!loadedRemapping) {
					loadedRemapping = true;
					try {
						if (readRemapping(map, arg)) {
							System.out.println("Loaded " + arg);
						} else {
							System.err.println("No mappings found in " + arg);
						}
					} catch (IOException e) {
						System.err.println("Could not read " + arg + ": " + e.getMessage());
					}
				} else {
					File inFile = new File(arg);
					File outFile;
					if (outputFile != null) {
						outFile = outputFile;
						outputFile = null;
					} else if (outputDir != null) {
						String outName = inFile.getName();
						outFile = new File(outputDir, outName);
					} else {
						File parent = inFile.getParentFile();
						String outName = inFile.getName() + ".REMAP.TXT";
						outFile = new File(parent, outName);
					}
					try {
						if (rewriteMapping(map, inFile, outFile)) {
							System.out.println("Rewrote " + arg);
						} else {
							System.out.println("No changes made to " + arg);
						}
					} catch (IOException e) {
						System.err.println("Could not rewrite " + arg + ": " + e.getMessage());
					}
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -m <path>     Specify a remapping file. Specify column numbers using colon:");
		System.out.println("                    map.txt           - remap from column 0 to column 1");
		System.out.println("                    map.txt:-         - remap from column 1 to column 0");
		System.out.println("                    map.txt:-<n>      - remap from column 0 to column <n>");
		System.out.println("                    map.txt:<n>-      - remap from column <n> to column 0");
		System.out.println("                    map.txt:<n>-<m>   - remap from column <n> to column <m>");
		System.out.println("  -o <path>     Specify output file.");
		System.out.println("  -d <path>     Specify output directory.");
		System.out.println("  --            Process remaining arguments as file names.");
		System.out.println();
	}
	
	private static final Pattern codePointPattern = Pattern.compile("\\b([0][Xx]|[Uu][+])?([0-9A-Fa-f]{4,8})\\b");
	
	private static boolean rewriteMapping(Map<Integer,Integer> map, File inFile, File outFile) throws IOException {
		boolean changed = false;
		Scanner scan = new Scanner(new FileInputStream(inFile), "UTF-8");
		FileOutputStream outStream = new FileOutputStream(outFile);
		OutputStreamWriter outWriter = new OutputStreamWriter(outStream, "UTF-8");
		PrintWriter out = new PrintWriter(outWriter, true);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			StringBuffer sb = new StringBuffer();
			Matcher m = codePointPattern.matcher(line);
			while (m.find()) {
				if (m.start() >= 4) {
					Integer cp = Integer.parseInt(m.group(2), 16);
					if (map.containsKey(cp)) {
						String r = toHexString(map.get(cp));
						if (m.group(1) != null) r = m.group(1) + r;
						m.appendReplacement(sb, r);
						changed = true;
						continue;
					}
				}
				m.appendReplacement(sb, m.group());
			}
			m.appendTail(sb);
			sb.append("\r\n");
			out.print(sb.toString());
		}
		out.flush();
		out.close();
		scan.close();
		return changed;
	}
	
	private static String toHexString(int value) {
		String h = Integer.toHexString(value).toUpperCase();
		while (h.length() < 4) h = "0" + h;
		return h;
	}
	
	private static final Pattern remappingParam = Pattern.compile(":([0-9]*)-([0-9]*)$");
	
	private static boolean readRemapping(Map<Integer,Integer> map, String path) throws IOException {
		Matcher m = remappingParam.matcher(path);
		if (m.find()) {
			File file = new File(path.substring(0, m.start()));
			Integer from = parseInt(m.group(1));
			Integer to = parseInt(m.group(2));
			if (from == null && to == null) {
				return readRemapping(map, file, 1, 0);
			} else {
				int fromInt = (from == null) ? 0 : from.intValue();
				int toInt = (to == null) ? 0 : to.intValue();
				return readRemapping(map, file, fromInt, toInt);
			}
		} else {
			File file = new File(path);
			return readRemapping(map, file, 0, 1);
		}
	}
	
	private static boolean readRemapping(Map<Integer,Integer> map, File file, int from, int to) throws IOException {
		boolean changed = false;
		Scanner scan = new Scanner(new FileInputStream(file), "UTF-8");
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			int o = line.indexOf("#");
			if (o >= 0) line = line.substring(0, o).trim();
			String[] fields = line.split("\\s+");
			if (fields.length > from && fields.length > to) {
				Integer fcp = parseInt16(fields[from]);
				Integer tcp = parseInt16(fields[to]);
				if (fcp != null && tcp != null) {
					map.put(fcp, tcp);
					changed = true;
				}
			}
		}
		scan.close();
		return changed;
	}
	
	private static Integer parseInt(String s) {
		if (s == null || s.length() == 0) return null;
		try { return Integer.parseInt(s, 10); }
		catch (NumberFormatException nfe) { return null; }
	}
	
	private static Integer parseInt16(String s) {
		if (s == null || s.length() == 0) return null;
		if (
			s.startsWith("0X") || s.startsWith("0x") ||
			s.startsWith("U+") || s.startsWith("u+")
		) {
			s = s.substring(2);
		}
		try { return Integer.parseInt(s, 16); }
		catch (NumberFormatException nfe) { return null; }
	}
}
