package com.kreative.mapedit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class RewriteTest {
	public static void main(String[] args) throws IOException, InterruptedException {
		for (String arg : args) {
			// Read mapping
			File f = new File(arg);
			Scanner s = new Scanner(f);
			Mapping m = new Mapping();
			m.read(s);
			s.close();
			
			// Write mapping
			File t = File.createTempFile("com.kreative.mapedit.RewriteTest.", ".txt");
			FileOutputStream o = new FileOutputStream(t);
			m.write(o);
			o.close();
			
			// Diff
			System.out.println(f.getAbsolutePath());
			String[] a = {
				"diff",
				"--strip-trailing-cr",
				f.getAbsolutePath(),
				t.getAbsolutePath()
			};
			Process p = Runtime.getRuntime().exec(a);
			InputStream i = p.getInputStream();
			byte[] b = new byte[65536];
			for (int r; (r = i.read(b)) > 0; System.out.write(b, 0, r));
			i.close();
			
			p.waitFor();
			t.delete();
			System.out.println();
			System.out.println();
		}
	}
}
