package com.kreative.unicode.ttfbin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.kreative.unicode.ttflib.TtfFile;
import com.kreative.unicode.ttflib.TtfTable;

public class TtfUnpack {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				File file = new File(arg);
				TtfFile ttf = new TtfFile(file);
				for (TtfTable t : ttf.getTables()) {
					String newTag = t.getTagString().replaceAll("[^A-Za-z0-9]", "_");
					String newName = file.getName() + "." + newTag + ".bin";
					System.out.println("\t" + newName);
					File newFile = new File(file.getParentFile(), newName);
					FileOutputStream out = new FileOutputStream(newFile);
					out.write(t.getData());
					out.close();
				}
			} catch (IOException e) {
				System.out.println("\tERROR: " + e);
			}
		}
	}
}
