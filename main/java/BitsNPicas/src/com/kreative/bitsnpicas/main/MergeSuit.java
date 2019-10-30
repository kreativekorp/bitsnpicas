package com.kreative.bitsnpicas.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import com.kreative.ksfl.KSFLConstants;
import com.kreative.rsrc.MacResource;
import com.kreative.rsrc.MacResourceFile;
import com.kreative.rsrc.MacResourceProvider;

public class MergeSuit {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			FONDFamilies families = new FONDFamilies();
			File outputFile = null;
			boolean inRes;
			try { inRes = System.getProperty("os.name").toUpperCase().contains("MAC OS"); }
			catch (Exception e) { inRes = false; }
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
						MacResourceProvider rp; int count = 0;
						if ((rp = open(file)) != null) { count += process(families, rp); rp.close(); }
						if ((rp = open(rsrc)) != null) { count += process(families, rp); rp.close(); }
						System.out.println((count > 0) ? " READ" : " ERROR: No fonts found.");
					} catch (IOException e) {
						System.out.println(" ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
					}
				}
			}
			if (families.fonds.isEmpty()) {
				System.out.println("No fonts found.");
				return;
			}
			if (outputFile == null) {
				if (families.fonds.size() == 1) {
					String name = families.fonds.keySet().iterator().next();
					outputFile = new File(inRes ? name : (name + ".dfont"));
				} else {
					String name = "Untitled Suitcase";
					outputFile = new File(inRes ? name : (name + ".dfont"));
				}
			}
			try {
				System.out.print(outputFile.getName() + "...");
				
				File rsrc;
				if (inRes) {
					outputFile.createNewFile();
					rsrc = new File(new File(outputFile, "..namedfork"), "rsrc");
				} else {
					rsrc = outputFile;
				}
				
				MacResourceProvider rp = new MacResourceFile(rsrc, "rwd", MacResourceFile.CREATE_ALWAYS);
				for (FONDFamily family : families.fonds.values()) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					DataOutputStream fondOut = new DataOutputStream(out);
					fondOut.writeShort(0x6000);
					fondOut.writeShort(family.fontID);
					fondOut.write(new byte[46]);
					fondOut.writeShort(1);
					fondOut.writeShort(family.size() - 1);
					for (Map.Entry<FONDEntryKey,MacResource> e : family.entrySet()) {
						fondOut.writeShort(e.getKey().fontSize);
						fondOut.writeShort(e.getKey().fontStyle);
						fondOut.writeShort(e.getValue().id);
						rp.add(e.getValue());
					}
					fondOut.flush(); fondOut.close();
					out.flush(); out.close();
					rp.add(new MacResource(
						KSFLConstants.FOND, (short)family.fontID,
						family.fontName, out.toByteArray()
					));
				}
				rp.close();
				
				if (inRes) try {
					String[] cmd = {"/usr/bin/SetFile", "-t", "FFIL", "-c", "DMOV", outputFile.getAbsolutePath()};
					Runtime.getRuntime().exec(cmd);
				} catch (IOException ignored) {}
				
				System.out.println(" DONE");
			} catch (IOException ioe) {
				System.out.println(" ERROR: " + ioe.getClass().getSimpleName() + ": " + ioe.getMessage());
			}
		}
	}
	
	private static MacResourceProvider open(File file) {
		try { return new MacResourceFile(file, "r", MacResourceFile.CREATE_NEVER); }
		catch (IOException ioe) { return null; }
	}
	
	private static int process(FONDFamilies families, MacResourceProvider rp) throws IOException {
		int count = 0;
		for (short id : rp.getIDs(KSFLConstants.FOND)) {
			MacResource fond = rp.get(KSFLConstants.FOND, id);
			ByteArrayInputStream in = new ByteArrayInputStream(fond.data);
			DataInputStream fondIn = new DataInputStream(in);
			fondIn.skip(52);
			int n = fondIn.readShort() + 1;
			for (int i = 0; i < n; i++) {
				int fontSize = fondIn.readShort();
				int fontStyle = fondIn.readShort();
				short resId = fondIn.readShort();
				if (fontSize == 0) {
					MacResource font = rp.get(KSFLConstants.sfnt, resId);
					if (font != null) {
						font.id = (short)families.resID(font.type, font.id);
						families.get(fond.id, fond.name).put(new FONDEntryKey(fontSize, fontStyle), font);
						count++;
					}
				} else {
					MacResource font = rp.get(KSFLConstants.NFNT, resId);
					if (font == null) font = rp.get(KSFLConstants.FONT, resId);
					if (font != null) {
						font.type = KSFLConstants.NFNT;
						font.id = (short)families.resID(font.type, font.id);
						families.get(fond.id, fond.name).put(new FONDEntryKey(fontSize, fontStyle), font);
						count++;
					}
				}
			}
			fondIn.close();
			in.close();
		}
		if (count == 0) {
			for (short id : rp.getIDs(KSFLConstants.FONT)) {
				int fontSize = id & 0x7F;
				if (fontSize != 0) {
					int fontId = (id & 0xFFFF) >> 7;
					short fontNameId = (short)(id &~ 0x7F);
					String fontName = rp.getNameFromID(KSFLConstants.FONT, fontNameId);
					MacResource font = rp.get(KSFLConstants.FONT, id);
					font.type = KSFLConstants.NFNT;
					font.id = (short)families.resID(font.type, font.id);
					families.get(fontId, fontName).put(new FONDEntryKey(fontSize, 0), font);
					count++;
				}
			}
		}
		return count;
	}
	
	private static final class FONDFamilies {
		public final Map<String,FONDFamily> fonds = new HashMap<String,FONDFamily>();
		public final Map<Integer,Set<Integer>> resIDs = new HashMap<Integer,Set<Integer>>();
		public FONDFamily get(int fontID, String fontName) {
			if (fonds.containsKey(fontName)) {
				return fonds.get(fontName);
			} else {
				fontID = resID(KSFLConstants.FOND, fontID);
				FONDFamily ff = new FONDFamily(fontID, fontName);
				fonds.put(fontName, ff);
				return ff;
			}
		}
		public int resID(int resType, int resID) {
			Set<Integer> ids = resIDs.get(resType);
			if (ids == null) resIDs.put(resType, (ids = new HashSet<Integer>()));
			while (ids.contains(resID)) resID = random.nextInt(32640) + 128;
			ids.add(resID); return resID;
		}
	}
	
	private static final class FONDFamily extends TreeMap<FONDEntryKey, MacResource> {
		private static final long serialVersionUID = 1L;
		public final int fontID;
		public final String fontName;
		public FONDFamily(int fontID, String fontName) {
			this.fontID = fontID;
			this.fontName = fontName;
		}
	}
	
	private static final class FONDEntryKey implements Comparable<FONDEntryKey> {
		public final int fontSize;
		public final int fontStyle;
		public FONDEntryKey(int fontSize, int fontStyle) {
			this.fontSize = fontSize;
			this.fontStyle = fontStyle;
		}
		public int compareTo(FONDEntryKey that) {
			if (this.fontSize != that.fontSize) {
				return this.fontSize - that.fontSize;
			} else {
				return this.fontStyle - that.fontStyle;
			}
		}
		public boolean equals(Object o) {
			if (o instanceof FONDEntryKey) {
				FONDEntryKey that = (FONDEntryKey)o;
				return (
					this.fontSize == that.fontSize &&
					this.fontStyle == that.fontStyle
				);
			}
			return false;
		}
		public int hashCode() {
			return fontSize | (fontStyle << 16);
		}
	}
	
	private static void printHelp() {
		System.out.println("MergeSuit - Merge Macintosh font suitcases and mover font files");
		System.out.println("            into a single font suitcase.");
		System.out.println("  -o <path>     Specify output file.");
		System.out.println("  -D            Write to data fork. (Default on non-Mac OS systems.)");
		System.out.println("  -R            Write to resource fork. (Default on Mac OS systems.)");
	}
	
	private static final Random random = new Random();
}
