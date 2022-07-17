package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;

public class KbitsBitmapFontImporter implements BitmapFontImporter {
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		BitmapFont f = importFont(dis);
		dis.close();
		bis.close();
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(InputStream is) throws IOException {
		BitmapFont f = importFont(new DataInputStream(is));
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		BitmapFont f = importFont(dis);
		dis.close();
		fis.close();
		return new BitmapFont[]{f};
	}
	
	private BitmapFont importFont(DataInputStream in) throws IOException {
		if (in.readInt() != 0x4B426E50) throw new IOException("bad magic number");
		if (in.readInt() != 0x62697473) throw new IOException("bad magic number");
		if (in.readInt() != 1) throw new IOException("bad version number");
		int ma = in.readInt();
		int md = in.readInt();
		int la = in.readInt();
		int ld = in.readInt();
		int lg = in.readInt();
		int xh = in.readInt();
		BitmapFont font = new BitmapFont(ma, md, la, ld, xh, xh, lg);
		while (true) {
			int blockType = in.readInt();
			switch (blockType) {
				case 0x6E616D65: // name
					if (in.readInt() != 1) throw new IOException("bad version number");
					int nameType = in.readInt();
					String name = in.readUTF();
					font.setName(nameType, name);
					break;
				case 0x63686172: // char
					if (in.readInt() != 1) throw new IOException("bad version number");
					int codePoint = in.readInt();
					int a = in.readInt();
					int x = in.readInt();
					int y = in.readInt();
					int rowCount = in.readInt();
					byte[][] rows = new byte[rowCount][];
					for (int i = 0; i < rowCount; i++) {
						int colCount = in.readInt();
						rows[i] = new byte[colCount];
						in.readFully(rows[i]);
					}
					BitmapFontGlyph glyph = new BitmapFontGlyph(rows, x, a, y);
					font.putCharacter(codePoint, glyph);
					break;
				case 0x66696E2E: // fin.
					font.setCapHeight();
					return font;
				default:
					throw new IOException("bad magic number");
			}
		}
	}
}
