package com.kreative.unicode.ttfbin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.kreative.unicode.ttflib.DfontFile;
import com.kreative.unicode.ttflib.DfontResource;
import com.kreative.unicode.ttflib.DfontResourceType;

public class DfontUnpack {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				File file = new File(arg);
				DfontFile dfont = new DfontFile(file);
				DfontResourceType sfnt = dfont.getResourceType("sfnt");
				if (sfnt != null) {
					for (DfontResource r : sfnt.getResources()) {
						String newName = file.getName() + "." + r.getId() + ".ttf";
						System.out.println("\t" + newName);
						File newFile = new File(file.getParentFile(), newName);
						FileOutputStream out = new FileOutputStream(newFile);
						out.write(r.getData());
						out.close();
					}
				}
			} catch (IOException e) {
				System.out.println("\tERROR: " + e);
			}
		}
	}
}
