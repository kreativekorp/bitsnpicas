package com.kreative.bitsnpicas.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import com.kreative.ksfl.KSFLConstants;
import com.kreative.rsrc.MacResource;
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
						System.out.println((count > 0) ? " DONE" : " ERROR: No fonts found.");
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
						export(parent, inRes, fond.id, fond.name, fontSize, fontStyle, font);
						count++;
					}
				} else {
					MacResource font = rp.get(KSFLConstants.NFNT, resId);
					if (font == null) font = rp.get(KSFLConstants.FONT, resId);
					if (font != null) {
						font.type = KSFLConstants.NFNT;
						export(parent, inRes, fond.id, fond.name, fontSize, fontStyle, font);
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
					export(parent, inRes, fontId, fontName, fontSize, 0, font);
					count++;
				}
			}
		}
		return count;
	}
	
	private static void export(File parent, boolean inRes, int fondId, String fondName, int fontSize, int fontStyle, MacResource font) throws IOException {
		String fileName = (fondName != null && fondName.length() > 0) ? fondName : "Untitled";
		if (fontStyle != 0) fileName += " (" + styleString(fontStyle) + ")";
		if (fontSize != 0) fileName += " " + fontSize;
		File file, rsrc;
		if (inRes) {
			file = new File(parent, fileName);
			file.createNewFile();
			rsrc = new File(new File(file, "..namedfork"), "rsrc");
		} else {
			file = new File(parent, fileName + ".dfont");
			rsrc = file;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutputStream fondOut = new DataOutputStream(out);
		fondOut.writeShort(0x6000);
		fondOut.writeShort(fondId);
		fondOut.write(new byte[46]);
		fondOut.writeShort(1);
		fondOut.writeShort(0);
		fondOut.writeShort(fontSize);
		fondOut.writeShort(fontStyle);
		fondOut.writeShort(font.id);
		fondOut.flush(); fondOut.close();
		out.flush(); out.close();
		MacResourceProvider rp = new MacResourceFile(rsrc, "rwd", MacResourceFile.CREATE_ALWAYS);
		rp.add(new MacResource(KSFLConstants.FOND, (short)fondId, fondName, out.toByteArray()));
		rp.add(font);
		rp.close();
		if (inRes) try {
			String type = ((fontSize == 0) ? "tfil" : "ffil");
			String[] cmd = {"/usr/bin/SetFile", "-t", type, "-c", "movr", "-a", "S", file.getAbsolutePath()};
			Runtime.getRuntime().exec(cmd);
		} catch (IOException ignored) {}
	}
	
	private static String styleString(int fontStyle) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		if ((fontStyle & 0x01) != 0) { if (first) first = false; else sb.append(", "); sb.append("bold"); }
		if ((fontStyle & 0x02) != 0) { if (first) first = false; else sb.append(", "); sb.append("italic"); }
		if ((fontStyle & 0x04) != 0) { if (first) first = false; else sb.append(", "); sb.append("underline"); }
		if ((fontStyle & 0x08) != 0) { if (first) first = false; else sb.append(", "); sb.append("outline"); }
		if ((fontStyle & 0x10) != 0) { if (first) first = false; else sb.append(", "); sb.append("shadow"); }
		if ((fontStyle & 0x20) != 0) { if (first) first = false; else sb.append(", "); sb.append("condensed"); }
		if ((fontStyle & 0x40) != 0) { if (first) first = false; else sb.append(", "); sb.append("extended"); }
		if ((fontStyle & 0x80) != 0) { if (first) first = false; else sb.append(", "); sb.append("group"); }
		return sb.toString();
	}
	
	private static void printHelp() {
		System.out.println("SplitSuit - Split Macintosh font suitcases into separate mover files.");
		System.out.println("  -d <path>     Specify directory for output files.");
		System.out.println("  -D            Write to data fork. (Default on non-Mac OS systems.)");
		System.out.println("  -R            Write to resource fork. (Default on Mac OS systems.)");
	}
}
