package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ImageToSvg {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		boolean parsingOptions = true;
		int[] imageRect = new int[]{ 0, -700, 800, 800 };
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parsingOptions && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parsingOptions = false;
				} else if (arg.equals("-x") && argi < args.length) {
					imageRect[0] = parseInt(args[argi++]);
				} else if (arg.equals("-y") && argi < args.length) {
					imageRect[1] = parseInt(args[argi++]);
				} else if (arg.equals("-w") && argi < args.length) {
					imageRect[2] = parseInt(args[argi++]);
				} else if (arg.equals("-h") && argi < args.length) {
					imageRect[3] = parseInt(args[argi++]);
				} else if (arg.equals("-r") && (argi+4) <= args.length) {
					imageRect[0] = parseInt(args[argi++]);
					imageRect[1] = parseInt(args[argi++]);
					imageRect[2] = parseInt(args[argi++]);
					imageRect[3] = parseInt(args[argi++]);
				} else if (arg.equals("--help")) {
					printHelp();
				} else {
					System.err.println("Unknown option: " + arg);
				}
			} else {
				File inputFile = new File(arg);
				String fileName = inputFile.getName();
				int o = fileName.lastIndexOf('.');
				if (o <= 0) continue;
				String extension = fileName.substring(o + 1);
				String mimeType = getMimeType(extension);
				if (mimeType == null) continue;
				String dataURI = createDataURI(inputFile, mimeType);
				if (dataURI == null) continue;
				File parentFile = inputFile.getParentFile();
				String baseName = fileName.substring(0, o);
				String outputFileName = baseName + ".svg";
				File outputFile = new File(parentFile, outputFileName);
				writeSVG(outputFile, dataURI, imageRect);
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("Usage:");
		System.out.println("  java -jar BitsNPicas.jar imagetosvg <options> <files>");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -x <number>         Set X origin of image.");
		System.out.println("  -y <number>         Set Y origin of image.");
		System.out.println("  -w <number>         Set width of image.");
		System.out.println("  -h <number>         Set height of image.");
		System.out.println("  -r <x> <y> <w> <h>  Set image position and size.");
		System.out.println("  --                  Treat remaining args as file names.");
		System.out.println();
	}
	
	private static String createDataURI(File inputFile, String mimeType) {
		try {
			StringBuffer data = new StringBuffer("data:");
			data.append(mimeType); data.append(";base64,");
			OutputStream out = new Base64OutputStream(data);
			InputStream in = new FileInputStream(inputFile);
			byte[] buf = new byte[1048576]; int len;
			while ((len = in.read(buf)) >= 0) out.write(buf, 0, len);
			in.close(); out.flush(); out.close();
			return data.toString();
		} catch (IOException e) {
			System.err.println("Error reading " + inputFile.getAbsolutePath());
			return null;
		}
	}
	
	private static void writeSVG(File outputFile, String dataURI, int[] rect) {
		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			PrintWriter pw = new PrintWriter(osw, true);
			pw.print("<svg id=\"glyph{{{0}}}\"");
			pw.print(" xmlns=\"http://www.w3.org/2000/svg\"");
			pw.print(" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
			pw.print("<image x=\""+rect[0]+"\" y=\""+rect[1]+"\"");
			pw.print(" width=\""+rect[2]+"\" height=\""+rect[3]+"\"");
			pw.print(" xlink:href=\""+dataURI+"\"/>");
			pw.print("</svg>");
			pw.flush();
			pw.close();
		} catch (IOException e) {
			System.err.println("Error writing " + outputFile.getAbsolutePath());
		}
	}
	
	private static int parseInt(String s) {
		try { return Integer.parseInt(s); }
		catch (NumberFormatException e) { return 0; }
	}
	
	private static String getMimeType(String ext) {
		if (ext.equalsIgnoreCase("bmp")) return "image/bmp";
		if (ext.equalsIgnoreCase("gif")) return "image/gif";
		if (ext.equalsIgnoreCase("jpe")) return "image/jpeg";
		if (ext.equalsIgnoreCase("jpeg")) return "image/jpeg";
		if (ext.equalsIgnoreCase("jpg")) return "image/jpeg";
		if (ext.equalsIgnoreCase("png")) return "image/png";
		if (ext.equalsIgnoreCase("tif")) return "image/tiff";
		if (ext.equalsIgnoreCase("tiff")) return "image/tiff";
		if (ext.equalsIgnoreCase("wbm")) return "image/vnd.wap.wbmp";
		if (ext.equalsIgnoreCase("wbmp")) return "image/vnd.wap.wbmp";
		if (ext.equalsIgnoreCase("webp")) return "image/webp";
		return null;
	}
	
	private static class Base64OutputStream extends OutputStream {
		private final StringBuffer sb;
		public Base64OutputStream(StringBuffer sb) { this.sb = sb; }
		private int word = 0;
		private int count = 0;
		@Override
		public void write(int b) throws IOException {
			word <<= 8;
			word |= (b & 0xFF);
			count++;
			if (count >= 3) {
				writeWord();
				word = 0;
				count = 0;
			}
		}
		@Override
		public void flush() throws IOException {
			if (count > 0) {
				for (int i = count; i < 3; i++) word <<= 8;
				writeWord();
			}
			word = 0;
			count = 0;
		}
		@Override
		public void close() throws IOException {
			flush();
		}
		private void writeWord() throws IOException {
			for (int m = 18, i = 0; i <= count; m -= 6, i++) {
				sb.append(b64e[(word >> m) & 0x3F]);
			}
			for (int i = count; i < 3; i++) {
				sb.append('=');
			}
		}
		private static final char[] b64e = {
			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
			'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
			'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
			'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/',
		};
	}
}
