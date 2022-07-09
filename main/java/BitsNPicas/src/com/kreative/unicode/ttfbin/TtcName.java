package com.kreative.unicode.ttfbin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.kreative.unicode.ttflib.NameTable;
import com.kreative.unicode.ttflib.TtcFile;
import com.kreative.unicode.ttflib.TtcFont;

public class TtcName {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				TtcFile ttc = new TtcFile(new File(arg));
				List<TtcFont> fonts = ttc.getFonts();
				for (int i = 0; i < fonts.size(); i++) {
					System.out.println("\tFont #" + i);
					NameTable names = fonts.get(i).getTableAs(NameTable.class, "name");
					if (names != null) {
						for (int j = 0; j < 256; j++) {
							String name = names.getName(j);
							if (name != null) {
								System.out.println("\t\t" + j + "\t" + name);
							}
						}
					}
				}
			} catch (IOException e) {
				System.out.println("\tERROR: " + e);
			}
		}
	}
}
