package com.kreative.bitsnpicas.fon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

// A FON file split into a directory: each font as an FNT file, a copy of
// its bytes, and the properties of the FON file as a text file, in UTF-8.
// Merging makes a FON file of every FNT file of a directory.
public class FONDirectory {
	public static final String DIRECTORY_SUFFIX = ".files";
	public static final String FON_SUFFIX = ".fon";
	public static final String FNT_SUFFIX = ".fnt";
	public static final String PROPERTIES_NAME = "fon.txt";
	
	// The directory that a FON file is split into, beside it unless a parent is given.
	public static File directoryFor(File fonFile, File parent) {
		if (parent == null) parent = fonFile.getAbsoluteFile().getParentFile();
		return new File(parent, fonFile.getName() + DIRECTORY_SUFFIX);
	}
	
	// The FON file that a directory is merged into, beside it.
	public static File fileFor(File directory) {
		directory = directory.getAbsoluteFile();
		String name = directory.getName();
		if (name.toLowerCase().endsWith(FON_SUFFIX + DIRECTORY_SUFFIX)) {
			name = name.substring(0, name.length() - DIRECTORY_SUFFIX.length());
		} else {
			name += FON_SUFFIX;
		}
		return new File(directory.getParentFile(), name);
	}
	
	// The files of a directory that merging reads: the fonts and the properties.
	public static List<File> listFiles(File directory) {
		List<File> files = new ArrayList<File>();
		File[] all = directory.listFiles();
		if (all == null) return files;
		Arrays.sort(all);
		for (File file : all) {
			if (!file.isFile()) continue;
			String name = file.getName().toLowerCase();
			if (name.endsWith(FNT_SUFFIX) || name.equals(PROPERTIES_NAME)) files.add(file);
		}
		return files;
	}
	
	// Returns the number of fonts. A directory that exists is not written into,
	// unless it is to be replaced: then every file of it is deleted first.
	public static int split(File fonFile, File directory, boolean replace) throws IOException {
		FONFile fon = FONFile.read(fonFile);
		int n = fon.getFontCount();
		if (n == 0) throw new IOException("no fonts in " + fonFile.getName());
		String[] names = fontNames(fon);
		if (directory.exists()) {
			if (!replace || !directory.isDirectory()) throw new IOException(directory.getPath() + " already exists");
			File[] files = directory.listFiles();
			if (files == null) throw new IOException("cannot read the directory " + directory.getPath());
			for (File file : files) {
				if (file.isDirectory()) {
					throw new IOException(directory.getPath() + " has a directory in it, " + file.getName() + ", and is left as it is");
				}
			}
			for (File file : files) {
				if (!file.delete()) throw new IOException("cannot delete " + file.getPath());
			}
		} else if (!directory.mkdirs()) {
			throw new IOException("cannot make the directory " + directory.getPath());
		}
		for (int i = 0; i < n; i++) {
			write(new File(directory, names[i] + FNT_SUFFIX), fon.getFont(i));
		}
		write(new File(directory, PROPERTIES_NAME), fon.getPropertiesText().getBytes("UTF-8"));
		return n;
	}
	
	// Returns the number of fonts. Warnings are of lines of the text file that are left out.
	public static int merge(File directory, File fonFile, List<String> warnings) throws IOException {
		if (!directory.isDirectory()) throw new IOException(directory.getPath() + " is not a directory");
		final List<File> files = new ArrayList<File>();
		final List<byte[]> fonts = new ArrayList<byte[]>();
		final List<FNTHeader> headers = new ArrayList<FNTHeader>();
		File properties = null;
		for (File file : listFiles(directory)) {
			if (!file.getName().toLowerCase().endsWith(FNT_SUFFIX)) {
				properties = file;
				continue;
			}
			byte[] font = read(file);
			try {
				headers.add(new FNTHeader(font));
			} catch (IOException e) {
				throw new IOException(file.getName() + ": " + e.getMessage());
			}
			files.add(file);
			fonts.add(font);
		}
		if (fonts.isEmpty()) throw new IOException("no " + FNT_SUFFIX + " files in " + directory.getPath());
		
		// In the order that the fonts of most FON files are in.
		Integer[] order = new Integer[fonts.size()];
		for (int i = 0; i < order.length; i++) order[i] = i;
		Arrays.sort(order, new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {
				FNTHeader ha = headers.get(a), hb = headers.get(b);
				if (ha.pixHeight != hb.pixHeight) return ha.pixHeight - hb.pixHeight;
				if (ha.weight != hb.weight) return ha.weight - hb.weight;
				if (ha.isItalic() != hb.isItalic()) return ha.isItalic() ? 1 : -1;
				if (ha.avgWidth != hb.avgWidth) return ha.avgWidth - hb.avgWidth;
				String na = files.get(a).getName(), nb = files.get(b).getName();
				int c = na.compareToIgnoreCase(nb);
				return (c != 0) ? c : na.compareTo(nb);
			}
		});
		
		FONFile fon = new FONFile();
		for (int i : order) fon.addFont(fonts.get(i));
		if (properties != null) {
			String text;
			try {
				text = Charset.forName("UTF-8").newDecoder()
					.onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT)
					.decode(ByteBuffer.wrap(read(properties))).toString();
			} catch (CharacterCodingException e) {
				throw new IOException(properties.getName() + " is not a text in UTF-8");
			}
			fon.setPropertiesText(properties.getName(), text, warnings);
		}
		fon.write(fonFile);
		return fonts.size();
	}
	
	// A name for the file of each font: its face, size and style, as "Courier 8x13px Bold".
	// Fonts that differ in nothing of these but the character set are told apart by that,
	// and others of one name by a number.
	private static String[] fontNames(FONFile fon) throws IOException {
		int n = fon.getFontCount();
		FNTHeader[] headers = new FNTHeader[n];
		String[] families = new String[n];
		String[] styles = new String[n];
		for (int i = 0; i < n; i++) {
			FNTHeader h = headers[i] = new FNTHeader(fon.getFont(i));
			String family = h.getFaceName().trim();
			// Such an ending marks the face of a raster font that has a namesake.
			if (family.toLowerCase().endsWith(" fon")) family = family.substring(0, family.length() - 4).trim();
			if (family.length() == 0) family = "font";
			int width = (h.fixedWidth > 0) ? h.fixedWidth : h.pixWidth;
			String size = " " + ((width > 0) ? (width + "x") : "") + h.pixHeight + "px";
			if (!family.toLowerCase().endsWith(size)) family += size;
			families[i] = family;
			styles[i] = (h.isBold() ? " Bold" : "") + (h.isItalic() ? " Italic" : "");
		}
		String[] names = new String[n];
		HashSet<String> taken = new HashSet<String>();
		for (int i = 0; i < n; i++) {
			String family = families[i];
			for (int j = 0; j < n; j++) {
				if (headers[j].charSet == headers[i].charSet) continue;
				if (!families[j].equalsIgnoreCase(families[i]) || !styles[j].equals(styles[i])) continue;
				family += " " + charSetName(headers[i].charSet);
				break;
			}
			String base = FONFile.fileName(family + styles[i]);
			String name = base;
			for (int serial = 2; !taken.add(name.toLowerCase()); serial++) {
				name = base + " (" + serial + ")";
			}
			names[i] = name;
		}
		return names;
	}
	
	private static String charSetName(int charSet) {
		switch (charSet) {
			case 0: return "cp1252";
			case 2: return "Symbol";
			case 161: return "cp1253";
			case 162: return "cp1254";
			case 177: return "cp1255";
			case 178: return "cp1256";
			case 186: return "cp1257";
			case 204: return "cp1251";
			case 238: return "cp1250";
			case 255: return "OEM";
			default: return "charset " + charSet;
		}
	}
	
	private static byte[] read(File file) throws IOException {
		byte[] d = new byte[(int)Math.min(file.length(), Integer.MAX_VALUE)];
		FileInputStream in = new FileInputStream(file);
		try {
			int n = 0;
			while (n < d.length) {
				int r = in.read(d, n, d.length - n);
				if (r < 0) throw new IOException(file.getName() + " ended early");
				n += r;
			}
		} finally {
			in.close();
		}
		return d;
	}
	
	private static void write(File file, byte[] d) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(d);
		} finally {
			out.close();
		}
	}
}
