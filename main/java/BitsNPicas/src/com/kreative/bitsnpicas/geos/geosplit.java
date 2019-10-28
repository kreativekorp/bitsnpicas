package com.kreative.bitsnpicas.geos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class geosplit {
	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				process(new File(arg));
			}
		} else {
			System.out.println("Splits GEOS .cvt files into their constituent parts. As applicable:");
			System.out.println("  .dir.bin            - CBM directory entry");
			System.out.println("  .info.bin           - GEOS info block");
			System.out.println("  .rec.bin            - VLIR record block");
			System.out.println("  .seq.bin            - sequential data");
			System.out.println("  .0.bin, .1.bin, ... - VLIR records");
		}
	}
	
	private static void process(File f) {
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				if (!ff.getName().startsWith(".")) {
					process(ff);
				}
			}
		} else if (f.isFile()) {
			if (f.getName().toLowerCase().endsWith(".cvt")) {
				try {
					ConvertFile cvt = new ConvertFile();
					FileInputStream in = new FileInputStream(f);
					cvt.read(new DataInputStream(in));
					in.close();
					if (cvt.directoryBlock != null) {
						DataOutputStream out = new DataOutputStream(
								new FileOutputStream(sibling(f, ".dir.bin")));
						out.write(0);
						out.write(0);
						cvt.directoryBlock.write(out);
						out.flush();
						out.close();
					}
					if (cvt.infoBlock != null) {
						DataOutputStream out = new DataOutputStream(
								new FileOutputStream(sibling(f, ".info.bin")));
						out.write(0);
						out.write(0);
						cvt.infoBlock.write(out);
						out.flush();
						out.close();
						ImageIO.write(cvt.infoBlock.getIconImage(), "png", sibling(f, ".icon.png"));
					}
					if (cvt.recordBlock != null) {
						DataOutputStream out = new DataOutputStream(
								new FileOutputStream(sibling(f, ".rec.bin")));
						out.write(0);
						out.write(0);
						cvt.recordBlock.write(out);
						out.flush();
						out.close();
					}
					if (cvt.sequentialData != null) {
						DataOutputStream out = new DataOutputStream(
								new FileOutputStream(sibling(f, ".seq.bin")));
						out.write(cvt.sequentialData);
						out.flush();
						out.close();
					}
					if (cvt.vlirData != null) {
						for (int i = 0; i < cvt.vlirData.size(); i++) {
							byte[] data = cvt.vlirData.get(i);
							if (data.length > 0) {
								DataOutputStream out = new DataOutputStream(
										new FileOutputStream(sibling(f, "." + i + ".bin")));
								out.write(data);
								out.flush();
								out.close();
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static File sibling(File f, String suffix) {
		return new File(f.getParentFile(), f.getName() + suffix);
	}
}
