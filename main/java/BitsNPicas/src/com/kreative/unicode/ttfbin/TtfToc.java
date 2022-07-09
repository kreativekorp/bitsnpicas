package com.kreative.unicode.ttfbin;

import java.io.File;
import java.io.IOException;
import com.kreative.unicode.ttflib.TtfFile;
import com.kreative.unicode.ttflib.TtfTable;

public class TtfToc {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				TtfFile ttf = new TtfFile(new File(arg));
				System.out.println("\tTag\tOffset\tLength");
				for (TtfTable t : ttf.getTables()) {
					System.out.println(
						"\t" + t.getTagString() +
						"\t" + t.getOffset() +
						"\t" + t.getLength()
					);
				}
			} catch (IOException e) {
				System.out.println("\tERROR: " + e);
			}
		}
	}
}
