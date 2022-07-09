package com.kreative.unicode.ttflib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class TtfFile extends TtfBase {
	public TtfFile(File file) throws IOException { read(file); }
	public TtfFile(InputStream in) throws IOException { read(in); }
	public TtfFile(byte[] data) throws IOException { read(data); }
	
	private void read(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		read(in);
		in.close();
	}
	
	private void read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[65536]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.close();
		read(out.toByteArray());
	}
	
	private void read(byte[] data) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(bin);
		super.read(in, new HashMap<Long,byte[]>());
		in.close();
		bin.close();
	}
}
