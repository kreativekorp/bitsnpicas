package com.kreative.mapedit;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Scanner;
import com.kreative.unicode.data.NameResolver;

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
					int cp = cs.get(0);
					String n = NameResolver.instance(cp).getName(cp);
					csc += "\t# " + n;
				} else if (cs.length() == 2) {
					MappingTag tag = MappingTag.forIntValue(cs.get(0));
					if (tag != null) {
						int cp = cs.get(1);
						String n = NameResolver.instance(cp).getName(cp);
						csc += "\t# " + n + ", " + tag.description;
					}
				}
				out.println(pfxi + "\t" + csc);
			}
			MappingTable st = tab.getSubtable(i);
			if (st != null) write(out, st, pfxi);
		}
	}
	
	public void decode(Charset cs) {
		name = cs.displayName();
		CharsetDecoder decoder = cs.newDecoder();
		decoder.onMalformedInput(CodingErrorAction.REPLACE);
		decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		decoder.replaceWith("\uFFFF");
		decode(decoder, new byte[256], 0, new char[256]);
	}
	
	private void decode(CharsetDecoder decoder, byte[] in, int pos, char[] out) {
		for (int ch = 0; ch < 256; ch++) {
			in[pos] = (byte)ch;
			ByteBuffer inb = ByteBuffer.wrap(in, 0, pos + 1);
			CharBuffer outb = CharBuffer.wrap(out);
			decoder.reset();
			decoder.decode(inb, outb, false);
			// Do NOT finish the decode so we can distinguish
			// incomplete input from invalid input.
			if (outb.position() > 0) {
				String s = new String(out, 0, outb.position());
				if (s.contains("\uFFFF")) continue;
				CodePointSequence cps = new CodePointSequence(s);
				root.setSequence(cps, in, 0, pos + 1);
			} else if (pos + 1 < in.length) {
				decode(decoder, in, pos + 1, out);
			} else {
				System.err.println("Failed to decode encoding " + name + "; it should be added to the list of broken charsets.");
			}
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
