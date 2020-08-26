package com.kreative.keyedit;

import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			com.kreative.keyedit.edit.Main.main(args);
		} else {
			String arg0 = args[0].toLowerCase();
			List<String> arga = Arrays.asList(args);
			arga = arga.subList(1, arga.size());
			args = arga.toArray(new String[arga.size()]);
			if (arg0.equals("edit")) {
				com.kreative.keyedit.edit.Main.main(args);
			} else if (arg0.equals("convert")) {
				ConvertKeyboard.main(args);
			} else if (arg0.equals("kkbcopy")) {
				KkbCopy.main(args);
			} else if (arg0.equals("kkblint")) {
				KkbLint.main(args);
			} else if (arg0.equals("maccopy")) {
				MacCopy.main(args);
			} else if (arg0.equals("macdiff")) {
				MacKeyboardDiff.main(args);
			} else if (arg0.equals("maclint")) {
				MacKeyboardLint.main(args);
			} else if (arg0.equals("wincopy")) {
				WinCopy.main(args);
			} else if (arg0.equals("xkbcopy")) {
				XkbCopy.main(args);
			} else {
				printHelp();
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("Usage:");
		System.out.println("  java -jar KeyEdit.jar edit <files>");
		System.out.println("  java -jar KeyEdit.jar convert [-if <fmt>] [-of <fmt>] <in-path> <out-path>");
		System.out.println("  java -jar KeyEdit.jar kkbcopy <input-paths> <output-path>");
		System.out.println("  java -jar KeyEdit.jar kkblint <files>");
		System.out.println("  java -jar KeyEdit.jar maccopy <input-paths> <output-path>");
		System.out.println("  java -jar KeyEdit.jar macdiff <file1> <file2>");
		System.out.println("  java -jar KeyEdit.jar maclint <files>");
		System.out.println("  java -jar KeyEdit.jar wincopy <input-paths> <output-path>");
		System.out.println("  java -jar KeyEdit.jar xkbcopy [-k|-u] <input-paths> <output-path>");
		System.out.println();
	}
}
