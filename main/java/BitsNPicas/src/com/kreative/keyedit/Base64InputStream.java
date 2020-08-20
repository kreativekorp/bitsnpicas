package com.kreative.keyedit;

import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Base64InputStream extends InputStream {
	private final CharacterIterator ci;
	private final InputStream in;
	
	public Base64InputStream(String s) {
		this.ci = new StringCharacterIterator(s);
		this.in = null;
	}
	
	public Base64InputStream(CharacterIterator ci) {
		this.ci = ci;
		this.in = null;
	}
	
	public Base64InputStream(InputStream in) {
		this.ci = null;
		this.in = in;
	}
	
	private int word = 0;
	private int count = 0;
	private boolean eof = false;
	
	@Override
	public int read() throws IOException {
		for (;;) {
			if (count > 0) {
				word <<= 8;
				count--;
				return (word >>> 24);
			}
			if (eof) return -1;
			readWord();
		}
	}
	
	private void readWord() throws IOException {
		for (;;) {
			int c = -1;
			if (ci != null) { c = ci.current(); ci.next(); }
			if (in != null) { c = in.read(); }
			if (c < 0 || c == '=' || c == CharacterIterator.DONE) {
				padWord();
				eof = true;
				return;
			}
			c = b64d(c);
			if (c >= 0) {
				word <<= 6;
				word |= c;
				count++;
				if (count > 3) {
					count = 3;
					return;
				}
			}
		}
	}
	
	private void padWord() {
		if (count > 0) {
			for (int i = count; i <= 3; i++) {
				word <<= 6;
			}
			count--;
		}
	}
	
	private int b64d(int c) {
		switch (c) {
			case 'A': return 0; case 'B': return 1; case 'C': return 2; case 'D': return 3;
			case 'E': return 4; case 'F': return 5; case 'G': return 6; case 'H': return 7;
			case 'I': return 8; case 'J': return 9; case 'K': return 10; case 'L': return 11;
			case 'M': return 12; case 'N': return 13; case 'O': return 14; case 'P': return 15;
			case 'Q': return 16; case 'R': return 17; case 'S': return 18; case 'T': return 19;
			case 'U': return 20; case 'V': return 21; case 'W': return 22; case 'X': return 23;
			case 'Y': return 24; case 'Z': return 25; case 'a': return 26; case 'b': return 27;
			case 'c': return 28; case 'd': return 29; case 'e': return 30; case 'f': return 31;
			case 'g': return 32; case 'h': return 33; case 'i': return 34; case 'j': return 35;
			case 'k': return 36; case 'l': return 37; case 'm': return 38; case 'n': return 39;
			case 'o': return 40; case 'p': return 41; case 'q': return 42; case 'r': return 43;
			case 's': return 44; case 't': return 45; case 'u': return 46; case 'v': return 47;
			case 'w': return 48; case 'x': return 49; case 'y': return 50; case 'z': return 51;
			case '0': return 52; case '1': return 53; case '2': return 54; case '3': return 55;
			case '4': return 56; case '5': return 57; case '6': return 58; case '7': return 59;
			case '8': return 60; case '9': return 61; case '+': return 62; case '/': return 63;
			default: return -1;
		}
	}
	
	@Override
	public void close() throws IOException {
		if (in != null) in.close();
	}
}