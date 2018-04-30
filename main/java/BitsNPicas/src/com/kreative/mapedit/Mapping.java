package com.kreative.mapedit;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import com.kreative.bitsnpicas.unicode.CharacterData;
import com.kreative.bitsnpicas.unicode.CharacterDatabase;

public class Mapping {
	public String name = null;
	public String date = null;
	public String author = null;
	public final MappingTable root = new MappingTable();
	
	public void read(InputStream in) {
		read(new Scanner(in));
	}
	
	public void read(Scanner in) {
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			if (line.startsWith("#")) {
				line = line.substring(1).trim();
				String[] parts = line.split(":", 2);
				if (parts.length == 2) {
					String key = parts[0].trim().toLowerCase();
					String value = parts[1].trim();
					if (key.equals("name")) name = value;
					if (key.equals("date")) date = value;
					if (key.equals("author")) author = value;
				}
			} else {
				String[] parts = line.split("\\s+");
				if (parts.length >= 2) {
					try {
						byte[] key = parseBytes(parts[0]);
						CodePointSequence value = CodePointSequence.parse(parts[1]);
						root.setSequence(value, key);
					} catch (NumberFormatException nfe) {
						continue;
					}
				}
			}
		}
	}
	
	public void write(OutputStream out) {
		write(new PrintWriter(new OutputStreamWriter(out), true));
	}
	
	public void write(PrintWriter out) {
		if (name   != null) out.println("#\tName:   " + name  );
		if (date   != null) out.println("#\tDate:   " + date  );
		if (author != null) out.println("#\tAuthor: " + author);
		out.println();
		write(out, root, "0x");
	}
	
	private void write(PrintWriter out, MappingTable tab, String pfx) {
		for (int i = 0; i < 256; i++) {
			String pfxi = pfx + formatByte(i);
			CodePointSequence cs = tab.getSequence(i);
			if (cs != null) {
				String csc = CodePointSequence.format(cs);
				if (cs.length() == 1) {
					CharacterData data = CharacterDatabase.instance().get(cs.get(0));
					if (data != null) csc += "\t# " + data.toString();
				} else if (cs.length() == 2) {
					MappingTag tag = MappingTag.forIntValue(cs.get(0));
					if (tag != null) {
						CharacterData data = CharacterDatabase.instance().get(cs.get(1));
						if (data != null) csc += "\t# " + data.toString() + ", " + tag.description;
					}
				}
				out.println(pfxi + "\t" + csc);
			}
			MappingTable st = tab.getSubtable(i);
			if (st != null) write(out, st, pfxi);
		}
	}
	
	private static byte[] parseBytes(String s) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		String[] parts = s.split("[+]");
		for (String part : parts) {
			part = part.toUpperCase();
			if (part.startsWith("0X")) part = part.substring(2);
			if ((part.length() & 1) == 1) part = "0" + part;
			for (int i = 0, n = part.length(); i < n; i += 2) {
				String h = part.substring(i, i + 2);
				os.write(Integer.parseInt(h, 16));
			}
		}
		return os.toByteArray();
	}
	
	private static String formatByte(int b) {
		String h = "00" + Integer.toHexString(b);
		return h.substring(h.length() - 2).toUpperCase();
	}
}
