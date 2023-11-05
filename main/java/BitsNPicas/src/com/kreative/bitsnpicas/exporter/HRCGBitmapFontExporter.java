package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class HRCGBitmapFontExporter implements BitmapFontExporter {
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		exportFontImpl(font, new DataOutputStream(out));
		out.close();
		return out.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		exportFontImpl(font, new DataOutputStream(os));
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		exportFontImpl(font, new DataOutputStream(out));
		out.close();
	}
	
	private void exportFontImpl(BitmapFont font, DataOutputStream out) throws IOException {
		boolean doubleWidth = false;
		boolean doubleHeight = font.getLineAscent() > 10;
		BitmapFontGlyph[] bfgs = new BitmapFontGlyph[96];
		for (int i = 0; i < 96; i++) {
			if ((bfgs[i] = font.getCharacter(i + 32)) == null) continue;
			if (bfgs[i].getCharacterWidth() > 10) doubleWidth = true;
		}
		
		byte[] data = new byte[768];
		for (int i = 0; i < 96; i++) {
			if (bfgs[i] == null) continue;
			int gy = -font.getLineAscent();
			for (int dy = 0; dy < 8; dy++) {
				if (doubleWidth) {
					byte cb = 0, db = 0, eb = 0;
					for (int gx = 1, m = 1; m < 0x80; m <<= 1, gx += 2) {
						if (bfgs[i].getPixel(gx - 1, gy) < 0) cb |= m;
						if (bfgs[i].getPixel(gx,     gy) < 0) db |= m;
						if (bfgs[i].getPixel(gx + 1, gy) < 0) eb |= m;
					}
					if (cb != db && db == eb) db |= 0x80;
					data[i * 8 + dy] = db;
				} else {
					byte db = 0;
					for (int gx = 0, m = 1; m < 0x80; m <<= 1, gx++) {
						if (bfgs[i].getPixel(gx, gy) < 0) db |= m;
					}
					data[i * 8 + dy] = db;
				}
				gy += doubleHeight ? 2 : 1;
			}
		}
		out.write(data);
	}
}
