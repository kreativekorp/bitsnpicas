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

public class KBnPBitmapFontExporter implements BitmapFontExporter {
	@Override
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		exportFont(font, dos);
		dos.close();
		bos.close();
		return bos.toByteArray();
	}
	
	@Override
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		exportFont(font, new DataOutputStream(os));
	}
	
	@Override
	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);
		exportFont(font, dos);
		dos.close();
		fos.close();
	}
	
	private void exportFont(BitmapFont font, DataOutputStream out) throws IOException {
		out.writeInt(0x4B426E50); // KBnP
		out.writeInt(0x62697473); // bits
		out.writeInt(1); // version
		out.writeInt(font.getEmAscent());
		out.writeInt(font.getEmDescent());
		out.writeInt(font.getLineAscent());
		out.writeInt(font.getLineDescent());
		out.writeInt(font.getLineGap());
		out.writeInt(font.getXHeight());
		for (int nameType : font.nameTypes()) {
			out.writeInt(0x6E616D65); // name
			out.writeInt(1); // version
			out.writeInt(nameType);
			out.writeUTF(font.getName(nameType));
		}
		for (int codePoint : font.codePoints()) {
			out.writeInt(0x63686172); // char
			out.writeInt(1); // version
			out.writeInt(codePoint);
			BitmapFontGlyph g = font.getCharacter(codePoint);
			out.writeInt(g.getCharacterWidth());
			out.writeInt(g.getGlyphOffset());
			out.writeInt(g.getGlyphAscent());
			byte[][] rows = g.getGlyph();
			out.writeInt(rows.length);
			for (byte[] row : rows) {
				out.writeInt(row.length);
				out.write(row);
			}
		}
		out.writeInt(0x66696E2E); // fin.
	}
}
