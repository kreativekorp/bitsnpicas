package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import com.kreative.bitsnpicas.truetype.SvgTableEntry;
import com.kreative.bitsnpicas.truetype.SvgTable;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class ExtractSvg {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
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
					Map<byte[], BitSet> documents = new HashMap<byte[], BitSet>();
					for (SvgTableEntry entry : svg) {
						if (entry.svgDocument.length > 0) {
							BitSet indices = documents.get(entry.svgDocument);
							if (indices == null) {
								indices = new BitSet();
								documents.put(entry.svgDocument, indices);
							}
							int fromIndex = Math.min(entry.startGlyphID, entry.endGlyphID);
							int toIndex = Math.max(entry.startGlyphID, entry.endGlyphID) + 1;
							indices.set(fromIndex, toIndex);
						}
					}
					for (SvgTableEntry entry : svg) {
						if (documents.containsKey(entry.svgDocument)) {
							BitSet indices = documents.remove(entry.svgDocument);
							StringBuffer indexString = new StringBuffer();
							int fromIndex = indices.nextSetBit(0);
							while (fromIndex >= 0) {
								int toIndex = indices.nextClearBit(fromIndex);
								if (indexString.length() > 0) {
									indexString.append("+");
								}
								indexString.append(fromIndex);
								if (toIndex - 1 > fromIndex) {
									indexString.append("-");
									indexString.append(toIndex - 1);
								}
								fromIndex = indices.nextSetBit(toIndex);
							}
							String name = "glyph_" + indexString.toString();
							File outputFile = new File(outputRoot, name + ".svg");
							copyAndClose(entry.getInputStream(), new FileOutputStream(outputFile));
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
	
	private static void copyAndClose(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[65536]; int len;
		while ((len = in.read(buf)) >= 0) out.write(buf, 0, len);
		out.flush(); out.close(); in.close();
	}
}
