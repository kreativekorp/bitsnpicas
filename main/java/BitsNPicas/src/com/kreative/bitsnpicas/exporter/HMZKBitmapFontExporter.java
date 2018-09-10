package com.kreative.bitsnpicas.exporter;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;

/**
 * HMZK is the bitmap font format used for the Mi Band 2.
 * <a href="https://github.com/Freeyourgadget/Gadgetbridge/wiki/Mi-Band-2-%28HMZK%29-Font-Format" target="_blank">Reference</a>
 */
public class HMZKBitmapFontExporter implements BitmapFontExporter {
    @Override
    public byte[] exportFontToBytes(BitmapFont font) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        exportFont(font, new DataOutputStream(out));
        out.close();
        return out.toByteArray();
    }

    @Override
    public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
        exportFont(font, new DataOutputStream(os));
    }

    @Override
    public void exportFontToFile(BitmapFont font, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        exportFont(font, new DataOutputStream(out));
        out.close();
    }
    
    private void exportFont(BitmapFont font, DataOutputStream out) throws IOException {
        int len = 0;
        // header
        out.writeInt(0x484d5a4b);
        out.writeInt(0x01ffffff);
        //out.writeInt(0xff00ffff); // it's like this in Mili_pro.ft.en
        out.writeInt(0xffffffff); // and like this in Mili_pro.ft
        out.writeShort(0xffff);

        ByteArrayOutputStream chbuf = new ByteArrayOutputStream();
        ByteArrayOutputStream glbuf = new ByteArrayOutputStream();
        // HashMap is not totally sorted, so we build a TreeMap from it
        // There are probably more efficient ways to do this, but it's only for export anyway
        TreeMap<Integer, BitmapFontGlyph> charmap = new TreeMap<Integer, BitmapFontGlyph>();
        Iterator<Map.Entry<Integer, BitmapFontGlyph>> fit = font.characterIterator();
        while (fit.hasNext()) {
            Map.Entry<Integer, BitmapFontGlyph> entry = fit.next();
            charmap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, BitmapFontGlyph> entry : charmap.entrySet()) {
            len++;
            if (2 * len > 0x3874) {
                System.out.println("Probably too many characters, not all were written to the file.");
                break;
            }
            int ch = entry.getKey();
            BitmapFontGlyph glyph = entry.getValue();
            int gx = glyph.getX();
            int gy = glyph.getY();
            chbuf.write((byte)(ch & 0xFF));
            chbuf.write((byte)((ch & 0xFF00) >> 8));
            // construct bitmap
            byte[][] glarr = glyph.getGlyph();
            int gytop = 16 - gy;
            for (int y = 0; y < 16; y++) {
                if(y < gytop || y - gytop >= glarr.length) {
                    glbuf.write(0);
                    glbuf.write(0);
                    continue;
                }
                byte[] row = glarr[y - gytop];
                byte b = 0;
                if(gx < 8) {
                    for (int x = 0; x < 8 && x < row.length; x++) {
                        if(row[x] != 0) {
                            b |= 1 << (7 - x - gx);
                        }
                    }
                }
                glbuf.write(b);
                b = 0;
                if(gx < 16) {
                    for (int x = 8; x < 16 && x < row.length; x++) {
                        if(row[x] != 0) {
                            b |= 1 << (15 - x - gx);
                        }
                    }
                }
                glbuf.write(b);
            }
        }
        out.writeShort(Short.reverseBytes((short)(len * 2)));
        chbuf.writeTo(out);
        glbuf.writeTo(out);
    }
}
