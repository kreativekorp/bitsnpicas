package com.kreative.bitsnpicas;
import java.io.IOException;
import java.io.InputStream;

public class WIBInputStream extends InputStream {
	private final InputStream in;
	
	public WIBInputStream(InputStream in) {
		this.in = in;
	}
	
	private Integer repeatData = null;
	private int repeatCount = 0;
	private boolean eof = false;
	
	@Override
	public int read() throws IOException {
		for (;;) {
			if (eof) return -1;
			if (repeatCount > 0) {
				repeatCount--;
				if (repeatData == null) {
					int data = in.read();
					if (data < 0) eof = true;
					return data;
				} else {
					return repeatData;
				}
			}
			int data = in.read();
			if (data < 0) {
				eof = true;
				return data;
			}
			repeatCount = data & 0x1F;
			if ((data & 0x20) != 0) repeatCount <<= 5;
			switch (data & 0xC0) {
				case 0x00:
					repeatData = 0x00;
					break;
				case 0x40:
					repeatData = 0xFF;
					break;
				case 0x80:
					data = in.read();
					if (data < 0) {
						eof = true;
						return data;
					}
					repeatData = data;
					break;
				case 0xC0:
					repeatData = null;
					break;
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		in.close();
	}
}
