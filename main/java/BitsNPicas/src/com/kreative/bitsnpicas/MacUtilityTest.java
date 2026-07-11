package com.kreative.bitsnpicas;

import java.io.File;
import java.io.IOException;

public class MacUtilityTest {
	public static void main(String[] args) throws IOException {
		File file = File.createTempFile("kbnpMacUtilityTest", ".tmp");
		MacUtility.setTypeAndCreator(file, "Te§t", "KBñP");
		String type = MacUtility.getType(file);
		String creator = MacUtility.getCreator(file);
		boolean ok = "Te§t".equals(type) && "KBñP".equals(creator);
		System.out.println(ok ? "PASS" : "FAIL");
		file.delete();
	}
}
