package com.kreative.keyedit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class KeyManPackageWriter {
	public static void write(File file, KeyboardMapping km, String buildPrefix) throws IOException {
		if (file.isDirectory()) {
			String basename = km.getKeymanIdentifierNotEmpty();
			File packageFile = new File(file, basename + ".kps");
			write(file, packageFile, basename, km, buildPrefix);
		} else if (file.isFile() || file.getName().contains(".")) {
			String basename = stripSuffix(file.getName(), ".kps");
			write(file.getParentFile(), file, basename, km, buildPrefix);
		} else {
			file.mkdir();
			String basename = km.getKeymanIdentifierNotEmpty();
			File packageFile = new File(file, basename + ".kps");
			write(file, packageFile, basename, km, buildPrefix);
		}
	}
	
	public static void write(File parentFile, File packageFile, String basename, KeyboardMapping km, String buildPrefix) throws IOException {
		if (buildPrefix == null) buildPrefix = "";
		String kmxFileName = buildPrefix + basename + ".kmx";
		String jsFileName = buildPrefix + basename.replaceAll("[^A-Za-z0-9_]", "_").toLowerCase() + ".js";
		String kvkFileName = buildPrefix + basename + ".kvk";
		String welcomeFileName = "welcome.htm";
		String readmeFileName = "readme.htm";
		
		File kmnFile = new File(parentFile, basename + ".kmn");
		File ktlFile = new File(parentFile, basename + ".keyman-touch-layout");
		File kvksFile = new File(parentFile, basename + ".kvks");
		File welcomeFile = new File(parentFile, welcomeFileName);
		File readmeFile = new File(parentFile, readmeFileName);
		
		HTMLWriter.write(readmeFile, km, false);
		HTMLWriter.write(welcomeFile, km, false);
		KeyManVisualWriter.write(kvksFile, km);
		KeyManTouchWriter.write(ktlFile, km);
		KeyManWriter.write(kmnFile, km);
		
		ArrayList<String> extras = new ArrayList<String>();
		if (!(km.htmlSquareChars == null || km.htmlSquareChars.isEmpty())) {
			extras.add("KreativeSquare.ttf");
		}
		if (!(km.keymanAttachments == null || km.keymanAttachments.isEmpty())) {
			for (Map.Entry<String,byte[]> e : km.keymanAttachments.entrySet()) {
				FileOutputStream fos = new FileOutputStream(new File(parentFile, e.getKey()));
				fos.write(e.getValue());
				fos.flush();
				fos.close();
				extras.add(e.getKey());
			}
		}
		
		FileOutputStream fos = new FileOutputStream(packageFile);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
		write(
			pw, km, kmxFileName, jsFileName, kvkFileName, welcomeFileName, readmeFileName,
			extras.toArray(new String[extras.size()])
		);
		pw.flush();
		pw.close();
		fos.close();
	}
	
	public static void write(
		PrintWriter out, KeyboardMapping km, String kmxFileName, String jsFileName,
		String kvkFileName, String welcomeFileName, String readmeFileName, String... extras
	) {
		out.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
		out.print("<Package>\r\n");
		out.print("  <System>\r\n");
		out.print("    <KeymanDeveloperVersion>16.0</KeymanDeveloperVersion>\r\n");
		out.print("    <FileVersion>7.0</FileVersion>\r\n");
		out.print("  </System>\r\n");
		out.print("  <Options>\r\n");
		out.print("    <ExecuteProgram></ExecuteProgram>\r\n");
		out.print("    <ReadMeFile>" + quote(readmeFileName) + "</ReadMeFile>\r\n");
		out.print("    <MSIFileName></MSIFileName>\r\n");
		out.print("    <MSIOptions></MSIOptions>\r\n");
		out.print("    <FollowKeyboardVersion/>\r\n");
		out.print("  </Options>\r\n");
		out.print("  <StartMenu>\r\n");
		out.print("    <Folder></Folder>\r\n");
		out.print("    <Items/>\r\n");
		out.print("  </StartMenu>\r\n");
		out.print("  <Info>\r\n");
		out.print("    <Name URL=\"\">" + quote(km.getKeymanNameNotEmpty()) + "</Name>\r\n");
		out.print("    <Copyright URL=\"\">" + quote(km.getKeymanCopyrightNotEmpty()) + "</Copyright>\r\n");
		String email = (
			(km.keymanEmailAddress != null && km.keymanEmailAddress.length() > 0) ?
			("mailto:" + km.keymanEmailAddress) : null
		);
		out.print("    <Author URL=" + aquote(email) + ">" + quote(km.getKeymanAuthorNotEmpty()) + "</Author>\r\n");
		out.print("    <Version URL=\"\"></Version>\r\n");
		out.print("    <WebSite URL=" + aquote(km.keymanWebSite) + ">" + quote(km.keymanWebSite) + "</WebSite>\r\n");
		out.print("  </Info>\r\n");
		out.print("  <Files>\r\n");
		
		if (kmxFileName != null && kmxFileName.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <Name>" + quote(kmxFileName) + "</Name>\r\n");
			out.print("      <Description></Description>\r\n");
			out.print("      <CopyLocation>0</CopyLocation>\r\n");
			out.print("      <FileType>.kmx</FileType>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (jsFileName != null && jsFileName.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <Name>" + quote(jsFileName) + "</Name>\r\n");
			out.print("      <Description></Description>\r\n");
			out.print("      <CopyLocation>0</CopyLocation>\r\n");
			out.print("      <FileType>.js</FileType>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (kvkFileName != null && kvkFileName.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <Name>" + quote(kvkFileName) + "</Name>\r\n");
			out.print("      <Description></Description>\r\n");
			out.print("      <CopyLocation>0</CopyLocation>\r\n");
			out.print("      <FileType>.kvk</FileType>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (welcomeFileName != null && welcomeFileName.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <Name>" + quote(welcomeFileName) + "</Name>\r\n");
			out.print("      <Description></Description>\r\n");
			out.print("      <CopyLocation>0</CopyLocation>\r\n");
			out.print("      <FileType>.htm</FileType>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (readmeFileName != null && readmeFileName.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <Name>" + quote(readmeFileName) + "</Name>\r\n");
			out.print("      <Description></Description>\r\n");
			out.print("      <CopyLocation>0</CopyLocation>\r\n");
			out.print("      <FileType>.htm</FileType>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (extras != null && extras.length > 0) {
			for (String extra : extras) {
				if (extra != null && extra.length() > 0) {
					String[] parts = extra.split("[\\\\/.]");
					String ext = parts[parts.length - 1];
					out.print("    <File>\r\n");
					out.print("      <Name>" + quote(extra) + "</Name>\r\n");
					out.print("      <Description></Description>\r\n");
					out.print("      <CopyLocation>0</CopyLocation>\r\n");
					out.print("      <FileType>." + ext + "</FileType>\r\n");
					out.print("    </File>\r\n");
				}
			}
		}
		
		out.print("  </Files>\r\n");
		out.print("  <Keyboards>\r\n");
		out.print("    <Keyboard>\r\n");
		out.print("      <Name>" + quote(km.getKeymanNameNotEmpty()) + "</Name>\r\n");
		out.print("      <ID>" + quote(km.getKeymanIdentifierNotEmpty()) + "</ID>\r\n");
		out.print("      <Version>" + quote(km.getKeymanVersionNotEmpty()) + "</Version>\r\n");
		if (km.keymanOSKFontFile != null && km.keymanOSKFontFile.length() > 0) {
			out.print("      <OSKFont>" + quote(km.keymanOSKFontFile) + "</OSKFont>\r\n");
		}
		if (km.keymanDisplayFontFile != null && km.keymanDisplayFontFile.length() > 0) {
			out.print("      <DisplayFont>" + quote(km.keymanDisplayFontFile) + "</DisplayFont>\r\n");
		}
		out.print("      <Languages>\r\n");
		if (km.keymanLanguages.isEmpty()) {
			out.print("        <Language ID=\"en\">English</Language>\r\n");
		} else for (Map.Entry<String,String> e : km.keymanLanguages.entrySet()) {
			out.print("        <Language ID=" + aquote(e.getKey()) + ">" + quote(e.getValue()) + "</Language>\r\n");
		}
		out.print("      </Languages>\r\n");
		out.print("    </Keyboard>\r\n");
		out.print("  </Keyboards>\r\n");
		out.print("  <Strings/>\r\n");
		out.print("</Package>\r\n");
	}
	
	private static String quote(String s) {
		if (s == null || s.length() == 0) return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
	
	private static String aquote(String s) {
		if (s == null || s.length() == 0) return "\"\"";
		return "\"" + quote(s).replace("\"", "&quot;") + "\"";
	}
	
	private static String stripSuffix(String s, String suffix) {
		if (s.toLowerCase().endsWith(suffix.toLowerCase())) {
			return s.substring(0, s.length() - suffix.length());
		} else {
			return s;
		}
	}
}
