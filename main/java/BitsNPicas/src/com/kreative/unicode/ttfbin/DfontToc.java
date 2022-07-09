package com.kreative.unicode.ttfbin;

import java.io.File;
import java.io.IOException;
import com.kreative.unicode.ttflib.DfontFile;
import com.kreative.unicode.ttflib.DfontResource;
import com.kreative.unicode.ttflib.DfontResourceType;

public class DfontToc {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				DfontFile dfont = new DfontFile(new File(arg));
				System.out.println("\tType\tID\tLength\tName");
				for (DfontResourceType t : dfont.getResourceTypes()) {
					for (DfontResource r : t.getResources()) {
						String name = r.getName();
						if (name == null) name = "";
						System.out.println(
							"\t" + t.getTypeString() +
							"\t" + r.getId() +
							"\t" + r.getData().length +
							"\t" + name
						);
					}
				}
			} catch (IOException e) {
				System.out.println("\tERROR: " + e);
			}
		}
	}
}
