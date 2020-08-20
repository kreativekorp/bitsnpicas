package com.kreative.keyedit;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class KkbLint {
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				KeyboardMapping km = KkbReader.read(new File(arg));
				OutputStreamWriter ow = new OutputStreamWriter(System.out, "UTF-8");
				PrintWriter pw = new PrintWriter(ow, true);
				KkbWriter.write(pw, km);
			} catch (IOException e) {
				System.err.println("Error: Failed to parse " + arg + ": " + e.getMessage());
			}
		}
	}
}
