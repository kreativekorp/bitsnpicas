package com.kreative.bitsnpicas.main;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DebugCybiko {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				FileInputStream in = new FileInputStream(new File(arg));
				debugCybiko(new DataInputStream(in));
				in.close();
			} catch (IOException e) {
				System.out.println("  ERROR: " + e);
			}
		}
	}
	
	private static void debugCybiko(DataInputStream in) throws IOException {
		int mn = in.readUnsignedByte();
		int cc = in.readUnsignedByte();
		int mw = in.readUnsignedByte();
		int mh = in.readUnsignedByte();
		System.out.println("\tMN\tCC\tMW\tMH");
		System.out.println("\t" + mn + "\t" + cc + "\t" + mw + "\t" + mh);
		System.out.println("\tC\tL\tT\tW\tH\tOK\tCHK");
		for (int c = 0; c < cc; c++) {
			int l = in.readUnsignedByte();
			int t = in.readUnsignedByte();
			int w = in.readUnsignedByte();
			int h = in.readUnsignedByte();
			boolean ok = l > 0 || t > 0 || w > 1 || h > 1;
			List<Integer> gd = new ArrayList<Integer>();
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x += 8) {
					int b = in.readUnsignedByte();
					for (int m = 0x80, i = x; m != 0 && i < w; m >>= 1, i++) {
						if ((b & m) != 0) {
							ok = true;
							gd.add((l + i) | ((t + y) << 16));
						}
					}
				}
			}
			System.out.println(
				"\t" + (c+32) +
				"\t" + l + "\t" + t +
				"\t" + w + "\t" + h +
				"\t" + ok + "\t" + gd.hashCode()
			);
		}
	}
}
