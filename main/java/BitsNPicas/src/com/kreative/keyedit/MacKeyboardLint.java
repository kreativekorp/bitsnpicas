package com.kreative.keyedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MacKeyboardLint {
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				File file = new File(arg);
				FileInputStream in = new FileInputStream(file);
				MacKeyboard kbd = MacKeyboardParser.parse(arg, in);
				String xml = kbd.toString();
				System.out.println(xml);
				in.close();
			} catch (IOException e) {
				System.err.println("Error: Failed to parse " + arg + ": " + e.getMessage());
			}
		}
	}
}
