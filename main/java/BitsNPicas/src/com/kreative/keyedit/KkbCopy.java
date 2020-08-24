package com.kreative.keyedit;

import java.io.File;
import java.io.IOException;

public class KkbCopy {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: KkbCopy <input-file> <output-file>");
			System.out.println("       KkbCopy <input-files> <output-directory>");
		} else {
			String outPath = args[args.length-1]; File out = new File(outPath);
			if (args.length > 2 || outPath.endsWith("/") || out.isDirectory()) {
				out.mkdir();
				for (int i = 0; i < args.length-1; i++) {
					try {
						KeyboardMapping km = KkbReader.read(new File(args[i]));
						File output = new File(out, km.getNameNotEmpty() + ".kkbx");
						KkbWriter.write(output, km);
					} catch (IOException e) {
						System.err.println("Failed copying " + args[i] + ": " + e.getMessage());
					}
				}
			} else {
				try {
					KeyboardMapping km = KkbReader.read(new File(args[0]));
					KkbWriter.write(new File(args[1]), km);
				} catch (IOException e) {
					System.err.println("Failed copying " + args[0] + ": " + e.getMessage());
				}
			}
		}
	}
}
