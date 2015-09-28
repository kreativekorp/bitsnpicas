package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import com.kreative.bitsnpicas.truetype.SbixEntry;
import com.kreative.bitsnpicas.truetype.SbixSubtable;
import com.kreative.bitsnpicas.truetype.SbixTable;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class ExtractSbix {
	public static void main(String[] args) {
		for (String arg : args) {
			File file = new File(arg);
			System.out.print("Processing " + file.getAbsolutePath() + "... ");
			try {
				byte[] data = new byte[(int)file.length()];
				FileInputStream in = new FileInputStream(file);
				in.read(data);
				in.close();
				TrueTypeFile ttf = new TrueTypeFile();
				ttf.decompile(data);
				SbixTable sbix = (SbixTable)ttf.getByTableName("sbix");
				if (sbix == null) {
					System.out.println("no sbix table found.");
				} else {
					File outputRoot = new File(file.getParent(), file.getName() + ".sbix.d");
					if (!outputRoot.exists()) outputRoot.mkdir();
					for (SbixSubtable subtable : sbix) {
						File outputSubdir = new File(outputRoot, Integer.toString(subtable.height));
						if (!outputSubdir.exists()) outputSubdir.mkdir();
						for (int i = 0; i < subtable.size(); i++) {
							SbixEntry entry = subtable.get(i);
							if (entry.imageData != null && entry.imageData.length > 0) {
								File outputFile = new File(outputSubdir, "glyph_" + Integer.toString(i) + ".png");
								FileOutputStream out = new FileOutputStream(outputFile);
								out.write(entry.imageData);
								out.flush();
								out.close();
							}
						}
					}
					System.out.println("done.");
				}
			} catch (Exception e) {
				System.out.println("failed (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ").");
			}
		}
	}
}
