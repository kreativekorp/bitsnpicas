package com.kreative.bitsnpicas;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class WIBOutputStream extends OutputStream {
	private final OutputStream out;
	
	public WIBOutputStream(OutputStream out) {
		this.out = out;
	}
	
	private ByteArrayOutputStream dataRun = new ByteArrayOutputStream();
	private int rleCount = 0;
	private int rleData = 0;
	
	@Override
	public void write(int b) throws IOException {
		b &= 0xFF;
		if (rleCount < 1) {
			rleCount = 1;
			rleData = b;
		} else if (rleData == b) {
			rleCount++;
		} else if (rleCount == 1) {
			dataRun.write(rleData);
			rleData = b;
		} else {
			writeRuns();
			rleCount = 1;
			rleData = b;
		}
	}
	
	public void finish() throws IOException {
		if (rleCount == 1) {
			dataRun.write(rleData);
			rleCount = 0;
		}
		writeRuns();
	}
	
	private void writeRuns() throws IOException {
		byte[] data = dataRun.toByteArray();
		int n = data.length;
		if (n > 0) {
			int o = 0;
			while (n >= 992) {
				out.write(0xFF);
				out.write(data, o, 992);
				o += 992;
				n -= 992;
			}
			if (n >= 32) {
				int m = n >> 5;
				out.write(0xE0 | m);
				m <<= 5;
				out.write(data, o, m);
				o += m;
				n -= m;
			}
			if (n > 1) {
				out.write(0xC0 | n);
				out.write(data, o, n);
			}
			if (n == 1) {
				switch (data[o]) {
					case (byte)0x00: out.write(0x01); break;
					case (byte)0xFF: out.write(0x41); break;
					default: out.write(0x81); out.write(data[o]); break;
				}
			}
			dataRun = new ByteArrayOutputStream();
		}
		if (rleCount > 0) {
			int base = (rleData == 0x00) ? 0x00 : (rleData == 0xFF) ? 0x40 : 0x80;
			boolean pat = (rleData != 0x00 && rleData != 0xFF);
			while (rleCount >= 992) {
				out.write(base | 0x3F);
				if (pat) out.write(rleData);
				rleCount -= 992;
			}
			if (rleCount >= 32) {
				int m = rleCount >> 5;
				out.write(base | 0x20 | m);
				m <<= 5;
				if (pat) out.write(rleData);
				rleCount -= m;
			}
			if (rleCount > 0) {
				out.write(base | rleCount);
				if (pat) out.write(rleData);
				rleCount = 0;
			}
		}
	}
	
	@Override
	public void flush() throws IOException {
		out.flush();
	}
	
	@Override
	public void close() throws IOException {
		finish();
		out.close();
	}
}
