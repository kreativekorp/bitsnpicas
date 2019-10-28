package com.kreative.bitsnpicas.geos;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class geols {
	public static void main(String[] args) {
		PrintWriter out;
		try {
			out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
		} catch (UnsupportedEncodingException e) {
			// Should not happen.
			throw new RuntimeException(e);
		}
		
		if (args.length == 0) args = new String[]{"."};
		for (String arg : args) process(out, new File(arg));
	}
	
	private static void process(PrintWriter out, File f) {
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				if (!ff.getName().startsWith(".")) {
					process(out, ff);
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
						out.println("Class: " + ib.getClassTextString());
						if (ib.hasAuthorString()) out.println("Author: " + ib.getAuthorString());
						if (ib.hasCreatorString()) out.println("Creator: " + ib.getCreatorString());
						out.println("Description: " + ib.getDescriptionString());
						if (ib.geosFileType == CBMConstants.GEOS_FILE_TYPE_FONT) {
							out.println("Font ID: " + ib.getFontID());
							out.println("Point Sizes: " + join(ib.getFontPointSizes()));
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
