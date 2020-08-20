package com.kreative.keyedit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XkbCopy {
	public static void main(String[] args) throws IOException {
		List<File> files = new ArrayList<File>();
		boolean useKeySym = false;
		boolean parsingOptions = true;
		for (String arg : args) {
			if (parsingOptions && arg.startsWith("-")) {
				if (arg.equals("--")) parsingOptions = false;
				else if (arg.equals("-k")) useKeySym = true;
				else if (arg.equals("-u")) useKeySym = false;
				else System.err.println("Invalid option: " + arg);
			} else {
				files.add(new File(arg));
			}
		}
		if (files.size() < 2) {
			System.out.println("Usage: XkbCopy [-k|-u] <input-files> <output-directory>");
			System.out.println("  -k  Output keysym constants for non-ASCII characters.");
			System.out.println("  -u  Output keysym constants for ASCII characters only.");
		} else {
			File output = files.remove(files.size() - 1);
			for (File input : files) {
				try {
					KeyboardMapping km = XkbReader.read(input);
					km.xkbUseKeySym = useKeySym;
					XkbWriter.write(output, km);
				} catch (IOException e) {
					System.err.println("Failed copying " + input.getName() + ": " + e.getMessage());
				}
			}
		}
	}
}
