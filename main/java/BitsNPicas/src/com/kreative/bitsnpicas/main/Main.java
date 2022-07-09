package com.kreative.bitsnpicas.main;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.kreative.bitsnpicas.puaa.PuaaCompiler;
import com.kreative.bitsnpicas.puaa.PuaaDecompiler;
import com.kreative.bitsnpicas.puaa.PuaaLookup;

public class Main {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		while (scan.hasNextLine()) if (scan.nextLine().equalsIgnoreCase("ok")) break;
		if (args.length == 0) {
			com.kreative.bitsnpicas.edit.Main.main(args);
		} else {
			String arg0 = args[0].toLowerCase();
			List<String> arga = Arrays.asList(args);
			arga = arga.subList(1, arga.size());
			args = arga.toArray(new String[arga.size()]);
			if (arg0.equals("edit")) {
				com.kreative.bitsnpicas.edit.Main.main(args);
			} else if (arg0.equals("convertbitmap")) {
				ConvertBitmap.main(args);
			} else if (arg0.equals("viewbitmap")) {
				ViewFont2.main(args);
			} else if (arg0.equals("extractcbdt")) {
				ExtractCbdt.main(args);
			} else if (arg0.equals("injectcbdt")) {
				InjectCbdt.main(args);
			} else if (arg0.equals("extractsbix")) {
				ExtractSbix.main(args);
			} else if (arg0.equals("injectsbix")) {
				InjectSbix.main(args);
			} else if (arg0.equals("extractsvg")) {
				ExtractSvg.main(args);
			} else if (arg0.equals("injectsvg")) {
				InjectSvg.main(args);
			} else if (arg0.equals("imagetosvg")) {
				ImageToSvg.main(args);
			} else if (arg0.equals("splitsuit")) {
				SplitSuit.main(args);
			} else if (arg0.equals("mergesuit")) {
				MergeSuit.main(args);
			} else if (arg0.equals("splitgeos")) {
				SplitGEOS.main(args);
			} else if (arg0.equals("mergegeos")) {
				MergeGEOS.main(args);
			} else if (arg0.equals("injectpuaa")) {
				PuaaCompiler.main(args);
			} else if (arg0.equals("extractpuaa")) {
				PuaaDecompiler.main(args);
			} else if (arg0.equals("lookuppuaa")) {
				PuaaLookup.main(args);
			} else if (arg0.equals("debugttf")) {
				DebugTTF.main(args);
			} else {
				printHelp();
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("Usage:");
		System.out.println("  java -jar BitsNPicas.jar edit <files>");
		System.out.println("  java -jar BitsNPicas.jar convertbitmap <options> <files>");
		System.out.println("  java -jar BitsNPicas.jar viewbitmap <files>");
		System.out.println("  java -jar BitsNPicas.jar extractcbdt <files>");
		System.out.println("  java -jar BitsNPicas.jar injectcbdt <files>");
		System.out.println("  java -jar BitsNPicas.jar extractsbix <files>");
		System.out.println("  java -jar BitsNPicas.jar injectsbix <files>");
		System.out.println("  java -jar BitsNPicas.jar extractsvg <files>");
		System.out.println("  java -jar BitsNPicas.jar injectsvg <files>");
		System.out.println("  java -jar BitsNPicas.jar imagetosvg <options> <files>");
		System.out.println("  java -jar BitsNPicas.jar splitsuit <options> <files>");
		System.out.println("  java -jar BitsNPicas.jar mergesuit <options> <files>");
		System.out.println("  java -jar BitsNPicas.jar splitgeos <options> <files>");
		System.out.println("  java -jar BitsNPicas.jar mergegeos <options> <files>");
		System.out.println("  java -jar BitsNPicas.jar extractpuaa <options> <files>");
		System.out.println("  java -jar BitsNPicas.jar injectpuaa <options> <files>");
		System.out.println("  java -jar BitsNPicas.jar lookuppuaa <options> <files>");
		System.out.println("  java -jar BitsNPicas.jar debugttf <files>");
		System.out.println();
	}
}
