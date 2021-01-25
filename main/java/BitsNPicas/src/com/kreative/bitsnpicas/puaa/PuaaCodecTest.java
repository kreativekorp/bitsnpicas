package com.kreative.bitsnpicas.puaa;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class PuaaCodecTest {
	private static final PuaaCodecRegistry registry = PuaaCodecRegistry.instance;
	
	public static void main(String[] args) {
		for (String arg : args) {
			File src = new File(arg);
			testDir(src);
		}
	}
	
	private static void testDir(File src) {
		if (src.isDirectory()) {
			for (File child : src.listFiles()) {
				if (!child.getName().startsWith(".")) {
					testDir(child);
				}
			}
		} else {
			PuaaCodec codec = registry.getCodec(src.getName());
			if (codec != null) {
				testFile(src, codec);
			}
		}
	}
	
	private static void testFile(File src, PuaaCodec codec) {
		System.out.print(src.getName());
		System.out.print("\t");
		System.out.print(src.length());
		System.out.print("\t");
		try {
			// File -> PuaaTable
			PuaaTable puaa = new PuaaTable();
			Scanner in = new Scanner(src, "UTF-8");
			codec.compile(puaa, in);
			in.close();
			
			// PuaaTable -> byte[]
			byte[] data = puaa.compile(null);
			
			System.out.print(data.length);
			System.out.print("\t");
			
			// byte[] -> PuaaTable
			puaa = new PuaaTable();
			puaa.decompile(data, null);
			
			// PuaaTable -> File
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8");
			PrintWriter pw = new PrintWriter(osw, true);
			codec.decompile(puaa, pw);
			pw.flush();
			pw.close();
			byte[] dst = bos.toByteArray();
			
			System.out.print(dst.length);
			System.out.print("\t");
			
			// round trip test
			byte[] data2 = puaa.compile(null);
			if (Arrays.equals(data, data2)) {
				System.out.println("PASS");
			} else {
				System.out.println("FAIL");
			}
		} catch (IOException e) {
			System.out.println("ERROR: " + e);
		}
	}
}
