package com.kreative.bitsnpicas.main;

import java.nio.charset.Charset;

public class DebugEncodings {
	public static void main(String[] args) {
		for (Charset cs : Charset.availableCharsets().values()) {
			System.out.println(cs.displayName());
			for (String alias : cs.aliases()) {
				System.out.println("\t" + alias);
			}
		}
	}
}
