package com.kreative.keyedit;

import java.io.IOException;
import java.io.OutputStream;

public class Base64OutputStream extends OutputStream {
	private final StringBuffer sb;
	private final OutputStream out;
	private final boolean pad;
	
	public Base64OutputStream(StringBuffer sb) {
		this(sb, true);
	}
	
	public Base64OutputStream(OutputStream out) {
		this(out, true);
	}
	
	public Base64OutputStream(StringBuffer sb, boolean pad) {
		this.sb = sb;
		this.out = null;
		this.pad = pad;
	}
	
	public Base64OutputStream(OutputStream out, boolean pad) {
		this.sb = null;
		this.out = out;
		this.pad = pad;
	}
	
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
		if (out != null) out.flush();
	}
	
	@Override
	public void close() throws IOException {
		if (count > 0) {
			for (int i = count; i < 3; i++) word <<= 8;
			writeWord();
		}
		word = 0;
		count = 0;
		if (out != null) out.close();
	}
	
	private void writeWord() throws IOException {
		for (int m = 18, i = 0; i <= count; m -= 6, i++) {
			char c = b64e[(word >> m) & 0x3F];
			if (sb != null) sb.append(c);
			if (out != null) out.write(c);
		}
		if (pad) {
			for (int i = count; i < 3; i++) {
				if (sb != null) sb.append('=');
				if (out != null) out.write('=');
			}
		}
	}
	
	private static final char[] b64e = {
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
		'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
		'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
		'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/',
	};
}