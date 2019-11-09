package com.kreative.bitsnpicas.geos;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GEOSList {
	public static void main(String[] args) {
		PrintWriter out;
		try {
			out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
		} catch (UnsupportedEncodingException e) {
			// Should not happen.
			throw new RuntimeException(e);
		}
		
		boolean recursive = false;
		
		if (args.length == 0) args = new String[]{"."};
		for (String arg : args) {
			if (arg.equals("-r")) {
				recursive = true;
			} else {
				process(out, new File(arg), recursive);
			}
		}
	}
	
	private static void process(PrintWriter out, File f, boolean recursive) {
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				if (!ff.getName().startsWith(".")) {
					if (recursive || ff.isFile()) {
						process(out, ff, recursive);
					}
				}
			}
		} else if (f.isFile()) {
			if (f.getName().toLowerCase().endsWith(".cvt")) {
				try {
					FileInputStream in = new FileInputStream(f);
					CBMDirectoryBlock db = new CBMDirectoryBlock();
					db.read(new DataInputStream(in));
					if (db.geosFileType != 0) {
						GEOSInfoBlock ib = new GEOSInfoBlock();
						ib.read(new DataInputStream(in));
						out.print(ib.getIconString());
						out.println(db.getFileName(true, true));
						out.println("Class: " + safe(ib.getClassTextString()));
						if (ib.hasAuthorString()) out.println("Author: " + safe(ib.getAuthorString()));
						if (ib.hasCreatorString()) out.println("Creator: " + safe(ib.getCreatorString()));
						out.println("Description: " + safe(ib.getDescriptionString()));
						if (ib.geosFileType == CBMConstants.GEOS_FILE_TYPE_FONT) {
							out.println("Font ID: " + getFontID(ib));
							out.println("Point Sizes: " + join(getFontPointSizes(ib)));
						}
						if (ib.fileStructure == CBMConstants.FILE_STRUCTURE_VLIR) {
							VLIRRecordBlock rb = new VLIRRecordBlock();
							rb.read(new DataInputStream(in));
							out.println("VLIR Records: " + join(listRecords(rb)));
						}
						out.println();
					}
					in.close();
				} catch (IOException e) {
					// Ignored
				}
			}
		}
	}
	
	private static String safe(String s) {
		return s.replaceAll("[^!-~]+", " ");
	}
	
	private static int getFontID(GEOSInfoBlock infoBlock) {
		return (
			(infoBlock.getByte(0x80) & 0xFF) |
			((infoBlock.getByte(0x81) & 0xFF) << 8)
		);
	}
	
	private static List<Integer> getFontPointSizes(GEOSInfoBlock infoBlock) {
		List<Integer> indices = new ArrayList<Integer>();
		for (int a = 0x82, i = 0; i < 15; i++, a += 2) {
			int index = infoBlock.getByte(a) & 0x3F;
			if (index == 0) break;
			indices.add(index);
		}
		if (indices.containsAll(Arrays.asList(48,49,50,51,52,53,54))) {
			indices.removeAll(Arrays.asList(49,50,51,52,53,54));
		}
		return indices;
	}
	
	private static List<String> listRecords(VLIRRecordBlock rb) {
		List<String> strings = new ArrayList<String>();
		for (int i = 0; i < rb.size(); i++) {
			VLIRRecordBlock.Entry e = rb.get(i);
			if (e != null && e.length > 0) {
				strings.add("#" + i + ": " + e);
			}
		}
		return strings;
	}
	
	private static String join(List<?> list) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (Object o : list) {
			if (first) first = false;
			else sb.append(", ");
			sb.append(o);
		}
		return sb.toString();
	}
}
