package com.kreative.bitsnpicas.importer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;

/**
 * HMZK is the monochrome bitmap font format used for the Mi Band 2
 * <a href="https://github.com/Freeyourgadget/Gadgetbridge/wiki/Mi-Band-2-%28HMZK%29-Font-Format" target="_blank">Reference</a>
 */
public class HMZKBitmapFontImporter implements BitmapFontImporter {
    public BitmapFont[] importFont(byte[] b) {
        BitmapFont bm = new BitmapFont(16, 0, 16, 0, 0, 0);
        // skip magic and padding
        // FIXME: check magic number/charsLen and throw exceptions
        int offset = 14;
        int charsLen = ((b[offset + 1] & 0xFF) << 8) | (b[offset] & 0xFF);
        int cellCount = charsLen / 2;
        int charOff = offset + 2;
        int bitmOff = charOff + charsLen;
        for (int i = 0; i < cellCount; i++, charOff += 2, bitmOff += 32) {
            byte[][] gd = new byte[16][16];
            for (int yo = bitmOff, y = 0; y < 16; y++, yo += 2) {
                for (int j = 0; j < 8; j++) {
                    gd[y][7 - j] = (byte)(((b[yo] >> j) & 1) * 0xFF);
                    gd[y][15 - j] = (byte)(((b[yo + 1] >> j) & 1) * 0xFF);
                }
            }
            BitmapFontGlyph glyph = new BitmapFontGlyph(gd, 0, 16, 16);
            int ch = ((b[charOff + 1] & 0xFF) << 8) | (b[charOff] & 0xFF);
            bm.putCharacter(ch, glyph);

        }
        bm.setXHeight(16);
        return new BitmapFont[]{bm};
    }

	public BitmapFont[] importFont(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1048576]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.close();
		return importFont(out.toByteArray());
	}
	
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1048576]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.close();
		in.close();
		return importFont(out.toByteArray());
	}
}
