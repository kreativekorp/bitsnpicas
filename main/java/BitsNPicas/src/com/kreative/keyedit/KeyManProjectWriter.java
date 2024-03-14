package com.kreative.keyedit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class KeyManProjectWriter {
	public static void write(File file, KeyboardMapping km) throws IOException {
		if (file.isDirectory()) {
			String basename = km.getKeymanIdentifierNotEmpty();
			File projectFile = new File(file, basename + ".kpj");
			write(file, projectFile, basename, km);
		} else if (file.isFile() || file.getName().contains(".")) {
			String basename = stripSuffix(file.getName(), ".kpj");
			write(file.getParentFile(), file, basename, km);
		} else {
			file.mkdir();
			String basename = km.getKeymanIdentifierNotEmpty();
			File projectFile = new File(file, basename + ".kpj");
			write(file, projectFile, basename, km);
		}
	}
	
	public static void write(File parentFile, File projectFile, String basename, KeyboardMapping km) throws IOException {
		File sourceFile = new File(parentFile, "source"); sourceFile.mkdir();
		File packageFile = new File(sourceFile, basename + ".kps");
		KeyManPackageWriter.write(sourceFile, packageFile, basename, km, "..\\build\\");
		
		File helpFile = new File(sourceFile, "help"); helpFile.mkdir();
		HTMLWriter.writeKeymanPHP(new File(helpFile, basename + ".php"), km);
		
		writeHistory(new File(parentFile, "HISTORY.md"), km);
		writeLicense(new File(parentFile, "LICENSE.md"), km);
		writeReadme(new File(parentFile, "README.md"), km);
		writeKeyboardInfo(new File(parentFile, basename + ".keyboard_info"), km);
		
		ArrayList<String> extras = new ArrayList<String>();
		if (!(km.htmlSquareChars == null || km.htmlSquareChars.isEmpty())) {
			extras.add("source\\KreativeSquare.ttf");
		}
		if (!(km.keymanAttachments == null || km.keymanAttachments.isEmpty())) {
			for (String name : km.keymanAttachments.keySet()) {
				extras.add("source\\" + name);
			}
		}
		
		PrintWriter out = open(projectFile);
		writeProject(
			out, km, "$PROJECTPATH\\build",
			"source\\" + basename + ".kmn",
			"source\\" + basename + ".kps",
			"HISTORY.md",
			"LICENSE.md",
			"README.md",
			basename + ".keyboard_info",
			"source\\" + basename + ".ico",
			"build\\" + basename + ".kmx",
			"build\\" + basename.replaceAll("[^A-Za-z0-9_]", "_").toLowerCase() + ".js",
			"build\\" + basename + ".kvk",
			"source\\welcome.htm",
			"source\\readme.htm",
			extras.toArray(new String[extras.size()])
		);
		out.flush();
		out.close();
	}
	
	public static void updateFileIds(KeyboardMapping km) {
		ArrayList<String> keys = new ArrayList<String>(Arrays.asList(
			"*.kmn", "*.kps", "HISTORY.md", "LICENSE.md", "README.md",
			"*.keyboard_info", "*.ico", "*.kmx", "*.js", "*.kvk",
			"welcome.htm", "readme.htm"
		));
		if (!(km.htmlSquareChars == null || km.htmlSquareChars.isEmpty())) {
			keys.add("KreativeSquare.ttf");
		}
		if (!(km.keymanAttachments == null || km.keymanAttachments.isEmpty())) {
			keys.addAll(km.keymanAttachments.keySet());
		}
		km.keymanFileIds.keySet().retainAll(keys);
		for (String key : keys) km.getKeymanFileId(key);
	}
	
	public static void writeKeyboardInfo(File file, KeyboardMapping km) throws IOException {
		PrintWriter out = open(file);
		writeKeyboardInfo(out, km);
		out.flush();
		out.close();
	}
	
	public static void writeHistory(File file, KeyboardMapping km) throws IOException {
		PrintWriter out = open(file);
		writeHistory(out, km);
		out.flush();
		out.close();
	}
	
	public static void writeLicense(File file, KeyboardMapping km) throws IOException {
		PrintWriter out = open(file);
		writeLicense(out, km);
		out.flush();
		out.close();
	}
	
	public static void writeReadme(File file, KeyboardMapping km) throws IOException {
		PrintWriter out = open(file);
		writeReadme(out, km);
		out.flush();
		out.close();
	}
	
	public static void writeProject(
		PrintWriter out, KeyboardMapping km, String buildPath, String kmnPath, String kpsPath,
		String historyMdPath, String licenseMdPath, String readmeMdPath, String keyboardInfoPath,
		String icoPath, String kmxPath, String jsPath, String kvkPath,
		String welcomeHtmPath, String readmeHtmPath, String... extras
	) {
		String kmnID = km.getKeymanFileId("*.kmn");
		String kpsID = km.getKeymanFileId("*.kps");
		out.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
		out.print("<KeymanDeveloperProject>\r\n");
		out.print("  <Options>\r\n");
		out.print("    <BuildPath>" + xquote(basepath(buildPath)) + "</BuildPath>\r\n");
		out.print("    <CompilerWarningsAsErrors>True</CompilerWarningsAsErrors>\r\n");
		out.print("    <WarnDeprecatedCode>True</WarnDeprecatedCode>\r\n");
		out.print("    <CheckFilenameConventions>False</CheckFilenameConventions>\r\n");
		out.print("    <ProjectType>keyboard</ProjectType>\r\n");
		out.print("  </Options>\r\n");
		out.print("  <Files>\r\n");
		
		if (kmnPath != null && kmnPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + kmnID + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(kmnPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(kmnPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion>" + xquote(km.getKeymanVersionNotEmpty()) + "</FileVersion>\r\n");
			out.print("      <FileType>.kmn</FileType>\r\n");
			out.print("      <Details>\r\n");
			out.print("        <Name>" + xquote(km.getKeymanNameNotEmpty()) + "</Name>\r\n");
			out.print("        <Copyright>" + xquote(km.getKeymanCopyrightNotEmpty()) + "</Copyright>\r\n");
			if (km.keymanMessage != null && km.keymanMessage.length() > 0) {
				out.print("        <Message>" + xquote(km.keymanMessage) + "</Message>\r\n");
			}
			out.print("      </Details>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (kpsPath != null && kpsPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + kpsID + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(kpsPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(kpsPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.kps</FileType>\r\n");
			out.print("      <Details>\r\n");
			out.print("        <Name>" + xquote(km.getKeymanNameNotEmpty()) + "</Name>\r\n");
			out.print("        <Copyright>" + xquote(km.getKeymanCopyrightNotEmpty()) + "</Copyright>\r\n");
			out.print("      </Details>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (historyMdPath != null && historyMdPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + km.getKeymanFileId("HISTORY.md") + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(historyMdPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(historyMdPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.md</FileType>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (licenseMdPath != null && licenseMdPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + km.getKeymanFileId("LICENSE.md") + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(licenseMdPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(licenseMdPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.md</FileType>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (readmeMdPath != null && readmeMdPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + km.getKeymanFileId("README.md") + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(readmeMdPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(readmeMdPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.md</FileType>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (keyboardInfoPath != null && keyboardInfoPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + km.getKeymanFileId("*.keyboard_info") + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(keyboardInfoPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(keyboardInfoPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.keyboard_info</FileType>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (icoPath != null && icoPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + km.getKeymanFileId("*.ico") + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(icoPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(icoPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.ico</FileType>\r\n");
			out.print("      <ParentFileID>" + kmnID + "</ParentFileID>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (kmxPath != null && kmxPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + km.getKeymanFileId("*.kmx") + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(kmxPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(kmxPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.kmx</FileType>\r\n");
			out.print("      <ParentFileID>" + kpsID + "</ParentFileID>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (jsPath != null && jsPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + km.getKeymanFileId("*.js") + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(jsPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(jsPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.js</FileType>\r\n");
			out.print("      <ParentFileID>" + kpsID + "</ParentFileID>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (kvkPath != null && kvkPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + km.getKeymanFileId("*.kvk") + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(kvkPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(kvkPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.kvk</FileType>\r\n");
			out.print("      <ParentFileID>" + kpsID + "</ParentFileID>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (welcomeHtmPath != null && welcomeHtmPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + km.getKeymanFileId("welcome.htm") + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(welcomeHtmPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(welcomeHtmPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.htm</FileType>\r\n");
			out.print("      <ParentFileID>" + kpsID + "</ParentFileID>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (readmeHtmPath != null && readmeHtmPath.length() > 0) {
			out.print("    <File>\r\n");
			out.print("      <ID>" + km.getKeymanFileId("readme.htm") + "</ID>\r\n");
			out.print("      <Filename>" + xquote(basename(readmeHtmPath)) + "</Filename>\r\n");
			out.print("      <Filepath>" + xquote(basepath(readmeHtmPath)) + "</Filepath>\r\n");
			out.print("      <FileVersion></FileVersion>\r\n");
			out.print("      <FileType>.htm</FileType>\r\n");
			out.print("      <ParentFileID>" + kpsID + "</ParentFileID>\r\n");
			out.print("    </File>\r\n");
		}
		
		if (extras != null && extras.length > 0) {
			for (String extra : extras) {
				if (extra != null && extra.length() > 0) {
					String[] parts = extra.split("[\\\\/.]");
					String ext = parts[parts.length - 1];
					out.print("    <File>\r\n");
					out.print("      <ID>" + km.getKeymanFileId(basename(extra)) + "</ID>\r\n");
					out.print("      <Filename>" + xquote(basename(extra)) + "</Filename>\r\n");
					out.print("      <Filepath>" + xquote(basepath(extra)) + "</Filepath>\r\n");
					out.print("      <FileVersion></FileVersion>\r\n");
					out.print("      <FileType>." + xquote(ext) + "</FileType>\r\n");
					out.print("      <ParentFileID>" + kpsID + "</ParentFileID>\r\n");
					out.print("    </File>\r\n");
				}
			}
		}
		
		out.print("  </Files>\r\n");
		out.print("</KeymanDeveloperProject>\r\n");
	}
	
	public static void writeKeyboardInfo(PrintWriter out, KeyboardMapping km) {
		out.print("{\r\n");
		out.print("    \"license\": " + jquote(getLicenseType(km)) + ",\r\n");
		out.print("    \"languages\": [\r\n");
		if (km.keymanLanguages == null || km.keymanLanguages.isEmpty()) {
			out.print("        \"en\"\r\n");
		} else {
			out.print("        ");
			boolean first = true;
			for (String lang : km.keymanLanguages.keySet()) {
				if (first) first = false;
				else out.print(", ");
				out.print(jquote(lang));
			}
			out.print("\r\n");
		}
		out.print("    ],\r\n");
		out.print("    \"description\": " + jquote(km.getKeymanDescriptionNotEmpty()) + "\r\n");
		out.print("}\r\n");
	}
	
	public static void writeHistory(PrintWriter out, KeyboardMapping km) {
		writeContent(out, km.keymanHistory, "keyman-history.md", km);
	}
	
	public static void writeLicense(PrintWriter out, KeyboardMapping km) {
		writeContent(out, km.keymanLicenseText, "keyman-license.md", km);
	}
	
	public static void writeReadme(PrintWriter out, KeyboardMapping km) {
		writeContent(out, km.keymanReadme, "keyman-readme.md", km);
	}
	
	private static PrintWriter open(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		return new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
	}
	
	private static void writeContent(PrintWriter out, String content, String name, KeyboardMapping km) {
		if (content != null && content.length() > 0) {
			for (String line : content.split("\r\n|\r|\n")) {
				out.print(line + "\r\n");
			}
		} else {
			Scanner scan = KeyManWriterUtility.getTemplate(name);
			while (scan.hasNextLine()) {
				content = KeyManWriterUtility.replaceFields(scan.nextLine(), km);
				for (String line : content.split("\r\n|\r|\n")) {
					out.print(line + "\r\n");
				}
			}
			scan.close();
		}
	}
	
	private static String basename(String path) {
		String[] parts = path.split("[\\\\/]");
		return parts[parts.length - 1];
	}
	
	private static String basepath(String path) {
		return path.replaceAll("[\\\\/]", "\\\\");
	}
	
	private static String getLicenseType(KeyboardMapping km) {
		if (km.keymanLicenseType != null && km.keymanLicenseType.length() > 0) {
			return km.keymanLicenseType;
		}
		if (km.keymanLicenseText != null && km.keymanLicenseText.length() > 0) {
			return "unknown";
		}
		return "mit";
	}
	
	private static String xquote(String s) {
		if (s == null || s.length() == 0) return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
	
	private static String jquote(String s) {
		if (s == null || s.length() == 0) return "\"\"";
		return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}
	
	private static String stripSuffix(String s, String suffix) {
		if (s.toLowerCase().endsWith(suffix.toLowerCase())) {
			return s.substring(0, s.length() - suffix.length());
		} else {
			return s;
		}
	}
}
