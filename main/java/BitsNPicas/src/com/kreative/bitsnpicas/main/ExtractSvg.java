package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import com.kreative.bitsnpicas.truetype.SvgTableEntry;
import com.kreative.bitsnpicas.truetype.SvgTable;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class ExtractSvg {
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
				SvgTable svg = (SvgTable)ttf.getByTableName("SVG ");
				if (svg == null) {
					System.out.println("no svg table found.");
				} else {
					File outputRoot = new File(file.getParent(), file.getName() + ".svg.d");
					if (!outputRoot.exists()) outputRoot.mkdir();
					for (SvgTableEntry entry : svg) {
						if (entry.svgDocument.length > 0) {
							String name = "glyph_" + Integer.toString(entry.startGlyphID);
							if (entry.endGlyphID != entry.startGlyphID) {
								name += "_" + Integer.toString(entry.endGlyphID);
							}
							File outputFile = new File(outputRoot, name + ".svg");
							FileOutputStream out = new FileOutputStream(outputFile);
							InputStream ein = entry.getInputStream();
							byte[] buf = new byte[65536]; int len;
							while ((len = ein.read(buf)) >= 0) out.write(buf, 0, len);
							ein.close();
							out.flush();
							out.close();
						}
					}
					System.out.println("done.");
				}
			} catch (Exception e) {
				System.out.println("failed (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ").");
				e.printStackTrace();
			}
		}
	}
}
