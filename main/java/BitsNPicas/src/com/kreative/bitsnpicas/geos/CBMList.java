package com.kreative.bitsnpicas.geos;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class CBMList {
	public static void main(String[] args) {
		PrintWriter out;
		try {
			out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
		} catch (UnsupportedEncodingException e) {
			// Should not happen.
			throw new RuntimeException(e);
		}
		
		boolean recursive = false;
		boolean alt = false;
		boolean geos = false;
		
		if (args.length == 0) args = new String[]{"."};
		for (String arg : args) {
			if (arg.equals("-r")) {
				recursive = true;
			} else if (arg.equals("-p")) {
				alt = false;
			} else if (arg.equals("-a")) {
				alt = true;
			} else if (arg.equals("-c")) {
				geos = false;
			} else if (arg.equals("-g")) {
				geos = true;
			} else {
				process(out, new File(arg), recursive, alt, geos);
			}
		}
	}
	
	private static void process(PrintWriter out, File f, boolean recursive, boolean alt, boolean geos) {
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				if (!ff.getName().startsWith(".")) {
					if (recursive || ff.isFile()) {
						process(out, ff, recursive, alt, geos);
					}
				}
			}
		} else if (f.isFile()) {
			if (f.getName().toLowerCase().endsWith(".cvt")) {
				try {
					CBMDirectoryBlock db = new CBMDirectoryBlock();
					FileInputStream in = new FileInputStream(f);
					db.read(new DataInputStream(in));
					in.close();
					out.println(db.toString(alt, geos));
				} catch (IOException e) {
					// Ignored
				}
			}
		}
	}
}
