package com.kreative.unicode.ttflib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TtcFile {
	private List<TtcFont> fonts;
	
	public TtcFile(File file) throws IOException { read(file); }
	public TtcFile(InputStream in) throws IOException { read(in); }
	public TtcFile(byte[] data) throws IOException { read(data); }
	
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
		read(in, new HashMap<Long,byte[]>());
		in.close();
		bin.close();
	}
	
	private void read(DataInputStream in, Map<Long,byte[]> dataCache) throws IOException {
		this.fonts = new ArrayList<TtcFont>();
		
		in.readInt();
		in.readInt();
		int count = in.readInt();
		
		for (int i = 0; i < count; i++) {
			TtcFont f = new TtcFont();
			f.readHead(in);
			this.fonts.add(f);
		}
		
		for (TtcFont f : this.fonts) {
			f.readBody(in, dataCache);
		}
		
		this.fonts = Collections.unmodifiableList(this.fonts);
	}
	
	public List<TtcFont> getFonts() { return fonts; }
}
