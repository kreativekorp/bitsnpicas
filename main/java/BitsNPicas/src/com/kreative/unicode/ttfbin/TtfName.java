package com.kreative.unicode.ttfbin;

import java.io.File;
import java.io.IOException;
import com.kreative.unicode.ttflib.NameTable;
import com.kreative.unicode.ttflib.TtfFile;

public class TtfName {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				TtfFile ttf = new TtfFile(new File(arg));
				NameTable names = ttf.getTableAs(NameTable.class, "name");
				if (names != null) {
					for (int i = 0; i < 256; i++) {
						String name = names.getName(i);
						if (name != null) {
							System.out.println("\t" + i + "\t" + name);
						}
					}
				}
			} catch (IOException e) {
				System.out.println("\tERROR: " + e);
			}
		}
	}
}
