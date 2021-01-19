package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class TTFRewriteTest {
	public static void main(String[] args) {
		for (String arg : args) {
			File file = new File(arg);
			System.out.print("Testing " + file.getName() + "...");
			try {
				if (rewriteTest(file)) {
					System.out.println(" SAME");
				} else {
					System.out.println(" DIFF");
				}
			} catch (IOException e) {
				System.out.println(" ERROR: " + e);
			}
		}
	}
	
	public static boolean rewriteTest(File file) throws IOException {
		long length = file.length();
		if (length < 12) throw new IOException("file too small");
		if (length > Integer.MAX_VALUE) throw new IOException("file too large");
		
		byte[] originalData = new byte[(int)length];
		FileInputStream in = new FileInputStream(file);
		in.read(originalData);
		in.close();
		
		TrueTypeFile ttf = new TrueTypeFile();
		ttf.decompile(originalData);
		byte[] rewrittenData = ttf.compile();
		
		if (Arrays.equals(originalData, rewrittenData)) return true;
		
		File diff = new File(file.getName() + ".diff");
		FileOutputStream out = new FileOutputStream(diff);
		out.write(rewrittenData);
		out.flush();
		out.close();
		return false;
	}
}
