package com.kreative.unicode.ttfbin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.kreative.unicode.ttflib.TtcFile;
import com.kreative.unicode.ttflib.TtcFont;
import com.kreative.unicode.ttflib.TtfTable;

public class TtcToc {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				TtcFile ttc = new TtcFile(new File(arg));
				List<TtcFont> fonts = ttc.getFonts();
				for (int i = 0; i < fonts.size(); i++) {
					System.out.println("\tFont #" + i);
					System.out.println("\t\tTag\tOffset\tLength");
					for (TtfTable t : fonts.get(i).getTables()) {
						System.out.println(
							"\t\t" + t.getTagString() +
							"\t" + t.getOffset() +
							"\t" + t.getLength()
						);
					}
				}
			} catch (IOException e) {
				System.out.println("\tERROR: " + e);
			}
		}
	}
}
