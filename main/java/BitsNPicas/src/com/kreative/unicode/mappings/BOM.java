package com.kreative.unicode.mappings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BOM {
	public static List<String> getResourceNames() {
		List<String> names = new ArrayList<String>();
		Scanner scan = new Scanner(BOM.class.getResourceAsStream("BOM.txt"));
		while (scan.hasNextLine()) names.add(scan.nextLine().trim());
		scan.close();
		return names;
	}
	
	public static InputStream getResource(String name) {
		return BOM.class.getResourceAsStream(name);
	}
}
