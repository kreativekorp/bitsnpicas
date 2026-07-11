package com.kreative.bitsnpicas;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MacUtility {
	public static File getDataFork(File file) {
		if (file.getName().equals("rsrc")) {
			File parent = file.getParentFile();
			if (parent != null && parent.getName().equals("..namedfork")) {
				File gparent = parent.getParentFile();
				if (gparent != null) return gparent;
			}
		}
		return file;
	}
	
	public static File getResourceFork(File file) {
		if (getDataFork(file) != file) return file;
		return new File(new File(file, "..namedfork"), "rsrc");
	}
	
	public static void setTypeAndCreator(File file, String type, String creator) {
		try {
			byte[] finf = new byte[32];
			try {
				byte[] xd = getXAttr(file, "com.apple.FinderInfo");
				if (xd != null) for (int i = 0; i < xd.length && i < 32; i++) finf[i] = xd[i];
			} catch (IOException e) {}
			byte[] td = type.getBytes("MacRoman");
			for (int i = 0; i < td.length && i < 4; i++) finf[i] = td[i];
			for (int i = td.length; i < 4; i++) finf[i] = 32;
			byte[] cd = creator.getBytes("MacRoman");
			for (int i = 0; i < cd.length && i < 4; i++) finf[i+4] = cd[i];
			for (int i = cd.length; i < 4; i++) finf[i+4] = 32;
			setXAttr(file, "com.apple.FinderInfo", finf, 0, 32);
		} catch (IOException e) {
			// Ignored.
		}
	}
	
	public static String getType(File file) {
		try {
			byte[] finf = getXAttr(file, "com.apple.FinderInfo");
			if (finf == null || finf.length < 8) return null;
			return new String(finf, 0, 4, "MacRoman");
		} catch (IOException e) {
			return null;
		}
	}
	
	public static String getCreator(File file) {
		try {
			byte[] finf = getXAttr(file, "com.apple.FinderInfo");
			if (finf == null || finf.length < 8) return null;
			return new String(finf, 4, 4, "MacRoman");
		} catch (IOException e) {
			return null;
		}
	}
	
	private static byte[] getXAttr(File file, String key) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		String[] cmd = {"/usr/bin/xattr", "-px", key, file.getAbsolutePath()};
		Process p = Runtime.getRuntime().exec(cmd);
		Scanner scan = new Scanner(p.getInputStream());
		while (scan.hasNextLine()) {
			char[] line = scan.nextLine().toCharArray();
			int i = 0, n = line.length;
			while (i < n) {
				int d1 = Character.digit(line[i++], 16);
				if (d1 >= 0) {
					if (i < n) {
						int d2 = Character.digit(line[i++], 16);
						if (d2 >= 0) {
							bytes.write((d1 << 4) | d2);
							continue;
						}
					}
					bytes.write(d1);
					continue;
				}
			}
		}
		scan.close();
		try { p.waitFor(); }
		catch (InterruptedException e) {}
		return bytes.toByteArray();
	}
	
	private static void setXAttr(File file, String key, byte[] data, int off, int len) throws IOException {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			sb.append(HEX[data[off++] & 0xFF]);
			if ((i & 15) == 15) sb.append("\n");
			else sb.append(" ");
		}
		String[] cmd = {"/usr/bin/xattr", "-wx", key, sb.toString(), file.getAbsolutePath()};
		Process p = Runtime.getRuntime().exec(cmd);
		try { p.waitFor(); }
		catch (InterruptedException e) {}
	}
	
	private static final String[] HEX = {
		"00","01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F",
		"10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
		"20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F",
		"30","31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",
		"40","41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F",
		"50","51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",
		"60","61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F",
		"70","71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",
		"80","81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F",
		"90","91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",
		"A0","A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF",
		"B0","B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",
		"C0","C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF",
		"D0","D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",
		"E0","E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF",
		"F0","F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF",
	};
}
