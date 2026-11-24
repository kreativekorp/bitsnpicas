package com.kreative.bitsnpicas.fon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

// A Windows font file (.fon): a 16-bit library of resources alone, which are
// the fonts, each a whole FNT file, a directory of them and a version.
// The fonts are kept as they are. What else the file tells is kept as
// properties, and the rest of the file is made anew when it is written.
public class FONFile {
	public static final String PROP_PREFIX = "fon.";
	public static final String PROP_MODULE_NAME = "fon.moduleName";
	public static final String PROP_DESCRIPTION = "fon.description";
	public static final String PROP_WINDOWS_VERSION = "fon.windowsVersion";
	public static final String PROP_LANGUAGE = "fon.language";
	public static final String PROP_CODE_PAGE = "fon.codePage";
	public static final String PROP_FILE_VERSION_NUMBER = "fon.fileVersionNumber";
	public static final String PROP_PRODUCT_VERSION_NUMBER = "fon.productVersionNumber";
	// Strings of the version resource, each a property: fon.companyName and so on.
	private static final String[] VERSION_STRINGS = {
		"CompanyName", "FileDescription", "FileVersion", "InternalName",
		"LegalCopyright", "OriginalFilename", "ProductName", "ProductVersion",
	};
	
	private static final int MZ_SIZE = 64;
	private static final int NE_AT = 128;
	private static final int NE_SIZE = 64;
	private static final int RT_FONTDIR = 0x8007;
	private static final int RT_FONT = 0x8008;
	private static final int RT_VERSION = 0x8010;
	private static final int VERSION_SIGNATURE = 0xFEEF04BD;
	private static final int FIXED_INFO_SIZE = 52;
	private static final String RESOURCE_NAME = "FONTDIR";
	private static final byte[] STUB_CODE = {
		0x0E, 0x1F, (byte)0xBA, 0x0E, 0x00, (byte)0xB4, 0x09, (byte)0xCD,
		0x21, (byte)0xB8, 0x01, 0x4C, (byte)0xCD, 0x21,
	};
	private static final String STUB_MESSAGE = "This program cannot be run in DOS mode.\r\n$";
	
	private final List<byte[]> fonts = new ArrayList<byte[]>();
	private final SortedMap<String,String> properties = new TreeMap<String,String>();
	// Where the properties were read, to tell where a wrong one is.
	private String source = null;
	private final Map<String,int[]> places = new HashMap<String,int[]>();
	
	public int getFontCount() {
		return fonts.size();
	}
	
	public byte[] getFont(int i) {
		return fonts.get(i);
	}
	
	public void addFont(byte[] font) throws IOException {
		new FNTHeader(font);
		fonts.add(font);
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}
	
	public void setProperty(String key, String value) {
		properties.put(key, value);
		places.remove(key);
	}
	
	public SortedMap<String,String> properties() {
		return new TreeMap<String,String>(properties);
	}
	
	public static boolean isProperty(String key) {
		if (key.equals(PROP_MODULE_NAME) || key.equals(PROP_DESCRIPTION)) return true;
		if (key.equals(PROP_WINDOWS_VERSION)) return true;
		if (key.equals(PROP_LANGUAGE) || key.equals(PROP_CODE_PAGE)) return true;
		if (key.equals(PROP_FILE_VERSION_NUMBER)) return true;
		if (key.equals(PROP_PRODUCT_VERSION_NUMBER)) return true;
		for (String name : VERSION_STRINGS) {
			if (key.equals(stringProperty(name))) return true;
		}
		return false;
	}
	
	private static String stringProperty(String name) {
		return PROP_PREFIX + name.substring(0, 1).toLowerCase() + name.substring(1);
	}
	
	// Properties as text, one to a line, as name=value.
	
	public String getPropertiesText() {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String,String> e : properties.entrySet()) {
			sb.append(e.getKey());
			sb.append("=");
			sb.append(e.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public void setPropertiesText(String source, String text, List<String> warnings) {
		this.source = source;
		if (text.length() > 0 && text.charAt(0) == 0xFEFF) text = text.substring(1);
		String[] lines = text.split("\r\n|\r|\n", -1);
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.trim().length() == 0) continue;
			int o = line.indexOf('=');
			String key = (o < 0) ? "" : line.substring(0, o).trim();
			if (key.length() == 0) {
				warnings.add(source + ", line " + (i + 1) + ": not a property, which is name=value");
				continue;
			}
			if (!isProperty(key)) {
				warnings.add(source + ", line " + (i + 1) + ": " + key + " is not a known property");
				continue;
			}
			int v = o + 1;
			while (v < line.length() && line.charAt(v) <= ' ') v++;
			properties.put(key, line.substring(v).trim());
			places.put(key, new int[] { i + 1, v + 1 });
		}
	}
	
	// Reading.
	
	public static FONFile read(File file) throws IOException {
		byte[] d = new byte[(int)Math.min(file.length(), Integer.MAX_VALUE)];
		FileInputStream in = new FileInputStream(file);
		try {
			int n = 0;
			while (n < d.length) {
				int r = in.read(d, n, d.length - n);
				if (r < 0) throw new IOException("the file ended early");
				n += r;
			}
		} finally {
			in.close();
		}
		return read(d);
	}
	
	public static FONFile read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[65536];
		for (int r; (r = in.read(buffer)) >= 0;) out.write(buffer, 0, r);
		return read(out.toByteArray());
	}
	
	public static FONFile read(byte[] d) throws IOException {
		try {
			return readImpl(d);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IOException("the file is damaged: a table of it leads out of the file");
		}
	}
	
	private static FONFile readImpl(byte[] d) throws IOException {
		if (d.length < MZ_SIZE || u16(d, 0) != 0x5A4D) throw new IOException("not a Windows FON file");
		int ne = u32(d, 60);
		if (ne < MZ_SIZE || ne > d.length - NE_SIZE || u16(d, ne) != 0x454E) {
			throw new IOException("not a 16-bit Windows FON file");
		}
		FONFile f = new FONFile();
		int resources = ne + u16(d, ne + 36);
		int names = ne + u16(d, ne + 38);
		int version = -1;
		int versionLength = 0;
		if (resources < names) {
			int shift = u16(d, resources);
			for (int p = resources + 2;;) {
				int type = u16(d, p);
				int count = u16(d, p + 2);
				if (type == 0) break;
				p += 8;
				for (int i = 0; i < count; i++, p += 12) {
					int offset = u16(d, p) << shift;
					int length = u16(d, p + 2) << shift;
					if (offset + length > d.length) length = d.length - offset;
					if (length < 0) throw new ArrayIndexOutOfBoundsException();
					if (type == RT_FONT) {
						// A resource is padded; a font tells its own size.
						int size = (length >= FNTHeader.MIN_SIZE) ? u32(d, offset + 2) : 0;
						if (size < FNTHeader.MIN_SIZE || size > length) size = length;
						f.addFont(Arrays.copyOfRange(d, offset, offset + size));
					} else if (type == RT_VERSION && version < 0) {
						version = offset;
						versionLength = length;
					}
				}
			}
		}
		
		Map<String,byte[]> strings = new TreeMap<String,byte[]>();
		int codePage = 1252;
		if (version >= 0) {
			try {
				String table = f.readVersion(d, version, version + versionLength, strings);
				if (table != null) {
					f.properties.put(PROP_LANGUAGE, table.substring(0, 4).toUpperCase());
					codePage = Integer.parseInt(table.substring(4), 16);
					f.properties.put(PROP_CODE_PAGE, Integer.toString(codePage));
				}
			} catch (RuntimeException e) {
				// A damaged version resource tells nothing.
			}
		}
		Charset cs = charset(codePage);
		if (cs == null) cs = Charset.forName("ISO-8859-1");
		for (String name : VERSION_STRINGS) {
			byte[] s = strings.get(name);
			if (s != null) f.properties.put(stringProperty(name), text(s, cs));
		}
		byte[] moduleName = name(d, names, d.length);
		if (moduleName != null) f.properties.put(PROP_MODULE_NAME, text(moduleName, cs));
		int nonresident = u32(d, ne + 44);
		byte[] description = (u16(d, ne + 32) > 0) ? name(d, nonresident, d.length) : null;
		if (description != null) f.properties.put(PROP_DESCRIPTION, text(description, cs));
		int windows = u16(d, ne + 62);
		if (windows != 0) f.properties.put(PROP_WINDOWS_VERSION, (windows >> 8) + "." + (windows & 0xFF));
		return f;
	}
	
	// Reads the version numbers and the strings of the first string table,
	// and returns the name of that table: its language and code page in hex.
	private String readVersion(byte[] d, int start, int limit, Map<String,byte[]> strings) {
		int end = Math.min(start + u16(d, start), limit);
		int value = align(start + 4 + zlen(d, start + 4, end) + 1, start);
		if (u16(d, start + 2) >= FIXED_INFO_SIZE && u32(d, value) == VERSION_SIGNATURE) {
			properties.put(PROP_FILE_VERSION_NUMBER, versionNumber(u32(d, value + 8), u32(d, value + 12)));
			properties.put(PROP_PRODUCT_VERSION_NUMBER, versionNumber(u32(d, value + 16), u32(d, value + 20)));
		}
		String table = null;
		int child = align(value + u16(d, start + 2), start);
		while (child + 6 <= end) {
			int size = u16(d, child);
			if (size < 6) break;
			int keyLength = zlen(d, child + 4, end);
			if (table == null && ascii(d, child + 4, keyLength).equals("StringFileInfo")) {
				int t = align(child + 4 + keyLength + 1, start);
				int tableEnd = Math.min(t + u16(d, t), end);
				int tableKeyLength = zlen(d, t + 4, tableEnd);
				String key = ascii(d, t + 4, tableKeyLength);
				if (key.matches("[0-9A-Fa-f]{8}")) table = key;
				int s = align(t + 4 + tableKeyLength + 1, start);
				while (s + 6 <= tableEnd) {
					int stringSize = u16(d, s);
					if (stringSize < 6) break;
					int nameLength = zlen(d, s + 4, tableEnd);
					int at = align(s + 4 + nameLength + 1, start);
					int length = Math.min(u16(d, s + 2), Math.max(tableEnd - at, 0));
					strings.put(ascii(d, s + 4, nameLength), Arrays.copyOfRange(d, at, at + length));
					s = align(s + stringSize, start);
				}
			}
			child = align(child + size, start);
		}
		return table;
	}
	
	// Writing.
	
	public void write(File file) throws IOException {
		byte[] d = write(file.getName());
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(d);
		} finally {
			out.close();
		}
	}
	
	// The name of the file is what it is told to have been made as,
	// unless the properties tell that; it may be null.
	public byte[] write(String fileName) throws IOException {
		int n = fonts.size();
		if (n == 0) throw new IOException("no fonts to make a FON file of");
		FNTHeader[] headers = new FNTHeader[n];
		for (int i = 0; i < n; i++) headers[i] = new FNTHeader(fonts.get(i));
		String face = headers[0].getFaceName();
		if (fileName == null) fileName = fileName(face) + ".fon";
		
		// What the properties tell, and what is told for them when they do not.
		String languageText = properties.get(PROP_LANGUAGE);
		if (languageText == null) languageText = "0409";
		if (!languageText.matches("[0-9A-Fa-f]{4}")) throw wrong(PROP_LANGUAGE, "must be four hex digits, as 0409");
		int language = Integer.parseInt(languageText, 16);
		int codePage = number(PROP_CODE_PAGE, properties.get(PROP_CODE_PAGE), 1252, 0xFFFF);
		Charset cs = charset(codePage);
		if (cs == null) throw wrong(PROP_CODE_PAGE, "the code page " + codePage + " is not known");
		int[] windows = numbers(PROP_WINDOWS_VERSION, "3.10", 2, 0xFF);
		int[] fileVersion = numbers(PROP_FILE_VERSION_NUMBER, "1.0", 4, 0xFFFF);
		int[] productVersion = numbers(PROP_PRODUCT_VERSION_NUMBER, "1.0", 4, 0xFFFF);
		byte[] moduleName = bytes(PROP_MODULE_NAME, moduleName(fileName), cs, codePage);
		byte[] description = bytes(PROP_DESCRIPTION, description(headers), cs, codePage);
		if (moduleName.length < 1 || moduleName.length > 255) throw wrong(PROP_MODULE_NAME, "must be of 1 to 255 bytes");
		if (description.length < 1 || description.length > 255) throw wrong(PROP_DESCRIPTION, "must be of 1 to 255 bytes");
		String fileVersionText = properties.get(PROP_FILE_VERSION_NUMBER);
		String productVersionText = properties.get(PROP_PRODUCT_VERSION_NUMBER);
		String[] defaults = {
			"", face + " font", (fileVersionText != null) ? fileVersionText : "1.0", face,
			headers[0].getCopyright(), fileName, face, (productVersionText != null) ? productVersionText : "1.0",
		};
		
		// The version resource.
		byte[] fixed = new byte[FIXED_INFO_SIZE];
		put32(fixed, 0, VERSION_SIGNATURE);
		put32(fixed, 4, 0x00010000);
		put32(fixed, 8, (fileVersion[0] << 16) | fileVersion[1]);
		put32(fixed, 12, (fileVersion[2] << 16) | fileVersion[3]);
		put32(fixed, 16, (productVersion[0] << 16) | productVersion[1]);
		put32(fixed, 20, (productVersion[2] << 16) | productVersion[3]);
		put32(fixed, 24, 0x3F); // every flag is told
		put32(fixed, 32, 0x00010001); // for 16-bit Windows over DOS
		put32(fixed, 36, 4); // a font
		put32(fixed, 40, headers[0].isVector() ? 2 : 1);
		Block table = new Block(String.format("%04X%04X", language, codePage), new byte[0]);
		for (int i = 0; i < VERSION_STRINGS.length; i++) {
			byte[] s = bytes(stringProperty(VERSION_STRINGS[i]), defaults[i], cs, codePage);
			table.children.add(new Block(VERSION_STRINGS[i], Arrays.copyOf(s, s.length + 1)));
		}
		byte[] translation = new byte[4];
		put16(translation, 0, language);
		put16(translation, 2, codePage);
		Block stringInfo = new Block("StringFileInfo", new byte[0]);
		stringInfo.children.add(table);
		Block varInfo = new Block("VarFileInfo", new byte[0]);
		varInfo.children.add(new Block("Translation", translation));
		Block versionInfo = new Block("VS_VERSION_INFO", fixed);
		versionInfo.children.add(stringInfo);
		versionInfo.children.add(varInfo);
		
		// The directory of fonts: of each, its number, the start of its header and its names.
		ByteArrayOutputStream directory = new ByteArrayOutputStream();
		directory.write(n);
		directory.write(n >> 8);
		for (int i = 0; i < n; i++) {
			directory.write(i + 1);
			directory.write((i + 1) >> 8);
			directory.write(fonts.get(i), 0, FNTHeader.DIRECTORY_SIZE);
			directory.write(headers[i].deviceName);
			directory.write(0);
			directory.write(headers[i].faceName);
			directory.write(0);
		}
		
		List<byte[]> bodies = new ArrayList<byte[]>();
		bodies.add(directory.toByteArray());
		bodies.addAll(fonts);
		bodies.add(versionInfo.build(0));
		
		// The tables follow the header back to back: resources, their one name,
		// the name of the module, no entries, and outside the header the description.
		int namesAt = 2 + 3 * 8 + (n + 2) * 12 + 2;
		int residentAt = NE_SIZE + namesAt + 1 + RESOURCE_NAME.length();
		int entriesAt = residentAt + 1 + moduleName.length + 3;
		int nonresidentAt = NE_AT + entriesAt + 2;
		int nonresidentSize = 1 + description.length + 3;
		if (entriesAt + 2 > 0xFFFF || n > 0x7FFF) throw new IOException("too many fonts for a FON file");
		int shift = 4;
		int first, size;
		for (;; shift++) {
			if (shift > 15) throw new IOException("the fonts are too large for a FON file");
			first = round(nonresidentAt + nonresidentSize, shift);
			size = first;
			for (byte[] body : bodies) size += round(body.length, shift);
			if ((size >> shift) <= 0xFFFF) break;
		}
		
		byte[] d = new byte[size];
		put16(d, 0, 0x5A4D);
		put16(d, 2, 251);
		put16(d, 4, 1);
		put16(d, 8, 4);
		put16(d, 12, 0xFFFF);
		put16(d, 16, 0xB8);
		put16(d, 24, MZ_SIZE);
		put32(d, 60, NE_AT);
		System.arraycopy(STUB_CODE, 0, d, MZ_SIZE, STUB_CODE.length);
		byte[] message = STUB_MESSAGE.getBytes("US-ASCII");
		System.arraycopy(message, 0, d, MZ_SIZE + STUB_CODE.length, message.length);
		
		put16(d, NE_AT, 0x454E);
		d[NE_AT + 2] = 5;
		d[NE_AT + 3] = 1;
		put16(d, NE_AT + 4, entriesAt);
		put16(d, NE_AT + 6, 2);
		put16(d, NE_AT + 12, 0x8300); // a library with no data of its own
		put16(d, NE_AT + 32, nonresidentSize);
		put16(d, NE_AT + 34, NE_SIZE);
		put16(d, NE_AT + 36, NE_SIZE);
		put16(d, NE_AT + 38, residentAt);
		put16(d, NE_AT + 40, entriesAt);
		put16(d, NE_AT + 42, entriesAt);
		put32(d, NE_AT + 44, nonresidentAt);
		put16(d, NE_AT + 50, shift);
		d[NE_AT + 54] = 2; // for Windows
		put16(d, NE_AT + 62, (windows[0] << 8) | windows[1]);
		
		int p = NE_AT + NE_SIZE;
		int at = first;
		put16(d, p, shift);
		p += 2;
		for (int i = 0; i < bodies.size(); i++) {
			byte[] body = bodies.get(i);
			boolean font = (i > 0 && i <= n);
			if (i <= 1 || i == n + 1) {
				put16(d, p, (i == 0) ? RT_FONTDIR : font ? RT_FONT : RT_VERSION);
				put16(d, p + 2, font ? n : 1);
				p += 8;
			}
			put16(d, p, at >> shift);
			put16(d, p + 2, round(body.length, shift) >> shift);
			put16(d, p + 4, font ? 0x1C30 : (i == 0) ? 0x0C50 : 0x0C30);
			put16(d, p + 6, (i == 0) ? namesAt : font ? (0x8000 | i) : 0x8001);
			p += 12;
			System.arraycopy(body, 0, d, at, body.length);
			at += round(body.length, shift);
		}
		p += 2;
		p = putName(d, p, RESOURCE_NAME.getBytes("US-ASCII"));
		putName(d, p, moduleName);
		putName(d, nonresidentAt, description);
		return d;
	}
	
	// A block of a version resource: a name, a value and blocks within it,
	// each of these starting at a multiple of 4 bytes from the start of the resource.
	private static class Block {
		private final String key;
		private final byte[] value;
		private final List<Block> children = new ArrayList<Block>();
		
		private Block(String key, byte[] value) {
			this.key = key;
			this.value = value;
		}
		
		private byte[] build(int at) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(new byte[4]);
			out.write(key.getBytes("US-ASCII"));
			out.write(0);
			if (value.length > 0 || !children.isEmpty()) pad(out, at);
			out.write(value);
			for (int i = 0; i < children.size(); i++) {
				pad(out, at);
				out.write(children.get(i).build(at + out.size()));
			}
			byte[] b = out.toByteArray();
			put16(b, 0, b.length);
			put16(b, 2, value.length);
			return b;
		}
		
		private static void pad(ByteArrayOutputStream out, int at) {
			while (((at + out.size()) & 3) != 0) out.write(0);
		}
	}
	
	// What is told when the properties tell nothing.
	
	private static String moduleName(String fileName) {
		StringBuffer sb = new StringBuffer();
		for (char ch : fileName.toUpperCase().toCharArray()) {
			if (ch == '.' || sb.length() >= 8) break;
			if ((ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')) sb.append(ch);
		}
		return (sb.length() > 0) ? sb.toString() : "FONT";
	}
	
	private static String description(FNTHeader[] headers) {
		TreeSet<Integer> points = new TreeSet<Integer>();
		for (FNTHeader h : headers) points.add(h.points);
		StringBuffer sb = new StringBuffer();
		sb.append("FONTRES 100," + headers[0].horizRes + "," + headers[0].vertRes);
		sb.append(" : " + headers[0].getFaceName());
		char separator = ' ';
		for (int p : points) {
			sb.append(separator);
			sb.append(p);
			separator = ',';
		}
		return sb.toString();
	}
	
	// A name for a file, of a text that may have what a name may not.
	public static String fileName(String text) {
		StringBuffer sb = new StringBuffer();
		for (char ch : text.toCharArray()) {
			boolean bad = (Character.isISOControl(ch) || ch == 0xA0 || ch == 0xAD || "\\/:*?\"<>|".indexOf(ch) >= 0);
			sb.append(bad ? '_' : ch);
		}
		String name = sb.toString().replaceAll("^[ .]+|[ .]+$", "");
		return (name.length() > 0) ? name : "font";
	}
	
	// Values of properties.
	
	private IOException wrong(String key, String message) {
		return new IOException(place(key, -1) + ": " + message);
	}
	
	private String place(String key, int index) {
		int[] place = places.get(key);
		if (place == null) return (index < 0) ? key : (key + ", character " + (index + 1));
		return source + ", line " + place[0] + ((index < 0) ? "" : (", column " + (place[1] + index))) + " (" + key + ")";
	}
	
	private int number(String key, String text, int def, int max) throws IOException {
		if (text == null) return def;
		try {
			int v = Integer.parseInt(text.trim());
			if (v >= 0 && v <= max) return v;
		} catch (NumberFormatException e) {
			// Told below.
		}
		throw wrong(key, "must be a number, 0 to " + max);
	}
	
	private int[] numbers(String key, String def, int count, int max) throws IOException {
		String text = properties.get(key);
		String[] parts = ((text != null) ? text : def).trim().split("\\.", -1);
		int[] values = new int[count];
		try {
			if (parts.length > count) throw new NumberFormatException();
			for (int i = 0; i < parts.length; i++) {
				values[i] = Integer.parseInt(parts[i]);
				if (values[i] < 0 || values[i] > max) throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			throw wrong(key, "must be up to " + count + " numbers, each 0 to " + max + ", with periods between");
		}
		return values;
	}
	
	// The bytes of a property in the code page, or else of what is told for it.
	// A property may not have a character that the code page lacks.
	private byte[] bytes(String key, String def, Charset cs, int codePage) throws IOException {
		String text = properties.get(key);
		if (text == null) return def.getBytes(cs.name());
		CharsetEncoder encoder = cs.newEncoder();
		for (int i = 0; i < text.length();) {
			int ch = text.codePointAt(i);
			int length = Character.charCount(ch);
			if (!encoder.canEncode(text.substring(i, i + length))) {
				String shown = Character.isISOControl(ch) ? "" : ("\"" + text.substring(i, i + length) + "\" ");
				throw new IOException(
					place(key, i) + ": the character " + shown + String.format("(U+%04X)", ch) +
					" is not in the code page " + codePage
				);
			}
			i += length;
		}
		return text.getBytes(cs.name());
	}
	
	private static Charset charset(int codePage) {
		String[] prefixes = { "windows-", "x-windows-", "Cp", "IBM", "x-IBM", "MS", "x-MS" };
		for (String prefix : prefixes) {
			try {
				return Charset.forName(prefix + codePage);
			} catch (IllegalArgumentException e) {
				// Try the next name.
			}
		}
		return null;
	}
	
	private static String text(byte[] b, Charset cs) {
		int length = 0;
		while (length < b.length && b[length] != 0) length++;
		return new String(b, 0, length, cs).replaceAll("[\r\n]+", " ").trim();
	}
	
	private static String versionNumber(int high, int low) {
		return (high >>> 16) + "." + (high & 0xFFFF) + "." + (low >>> 16) + "." + (low & 0xFFFF);
	}
	
	// Bytes.
	
	// The first name of a table of names, each a length, the text and a number.
	private static byte[] name(byte[] d, int at, int limit) {
		if (at <= 0 || at >= limit) return null;
		int length = d[at] & 0xFF;
		if (length == 0 || at + 1 + length > limit) return null;
		return Arrays.copyOfRange(d, at + 1, at + 1 + length);
	}
	
	private static int putName(byte[] d, int at, byte[] name) {
		d[at] = (byte)name.length;
		System.arraycopy(name, 0, d, at + 1, name.length);
		return at + 1 + name.length;
	}
	
	private static int zlen(byte[] d, int start, int limit) {
		int end = start;
		while (end < limit && d[end] != 0) end++;
		return end - start;
	}
	
	private static String ascii(byte[] d, int start, int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) sb.append((char)(d[start + i] & 0xFF));
		return sb.toString();
	}
	
	// The next multiple of 4 bytes from the start of a resource.
	private static int align(int at, int start) {
		return start + ((at - start + 3) & ~3);
	}
	
	// The next multiple of the unit that resources are placed by.
	private static int round(int size, int shift) {
		return ((size + (1 << shift) - 1) >> shift) << shift;
	}
	
	private static int u16(byte[] d, int o) {
		return FNTHeader.u16(d, o);
	}
	
	private static int u32(byte[] d, int o) {
		return FNTHeader.u32(d, o);
	}
	
	private static void put16(byte[] d, int o, int v) {
		d[o] = (byte)v;
		d[o + 1] = (byte)(v >> 8);
	}
	
	private static void put32(byte[] d, int o, int v) {
		put16(d, o, v);
		put16(d, o + 2, v >> 16);
	}
}
