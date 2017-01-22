package com.kreative.bitsnpicas.main;

import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			String arg0 = args[0].toLowerCase();
			List<String> arga = Arrays.asList(args);
			arga = arga.subList(1, arga.size());
			args = arga.toArray(new String[arga.size()]);
			if (arg0.equals("convertbitmap")) {
				ConvertBitmap.main(args);
			} else if (arg0.equals("viewbitmap")) {
				ViewFont2.main(args);
			} else if (arg0.equals("extractsbix")) {
				ExtractSbix.main(args);
			} else if (arg0.equals("injectsbix")) {
				InjectSbix.main(args);
			} else if (arg0.equals("extractsvg")) {
				ExtractSvg.main(args);
			} else if (arg0.equals("injectsvg")) {
				InjectSvg.main(args);
			} else {
				printHelp();
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("Usage:");
		System.out.println("  java -jar BitsNPicas.jar convertbitmap <options> <files>");
		System.out.println("  java -jar BitsNPicas.jar viewbitmap <files>");
		System.out.println("  java -jar BitsNPicas.jar extractsbix <files>");
		System.out.println("  java -jar BitsNPicas.jar injectsbix <files>");
		System.out.println("  java -jar BitsNPicas.jar extractsvg <files>");
		System.out.println("  java -jar BitsNPicas.jar injectsvg <files>");
		System.out.println();
	}
}
