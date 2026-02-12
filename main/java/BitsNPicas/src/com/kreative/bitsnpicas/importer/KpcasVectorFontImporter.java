package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.kreative.bitsnpicas.VectorFont;
import com.kreative.bitsnpicas.VectorFontGlyph;
import com.kreative.bitsnpicas.VectorFontImporter;
import com.kreative.bitsnpicas.VectorInstruction;
import com.kreative.bitsnpicas.VectorPath;

public class KpcasVectorFontImporter implements VectorFontImporter {
	@Override
	public VectorFont[] importFont(byte[] data) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		VectorFont f = importFont(dis);
		dis.close();
		bis.close();
		return new VectorFont[]{f};
	}
	
	@Override
	public VectorFont[] importFont(InputStream is) throws IOException {
		VectorFont f = importFont(new DataInputStream(is));
		return new VectorFont[]{f};
	}
	
	@Override
	public VectorFont[] importFont(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		VectorFont f = importFont(dis);
		dis.close();
		fis.close();
		return new VectorFont[]{f};
	}
	
	private VectorFont importFont(DataInputStream in) throws IOException {
		if (in.readInt() != 0x4B426E50) throw new IOException("bad magic number");
		if (in.readInt() != 0x70636173) throw new IOException("bad magic number");
		if (in.readInt() != 1) throw new IOException("bad version number");
		double ma = in.readDouble();
		double md = in.readDouble();
		double la = in.readDouble();
		double ld = in.readDouble();
		double lg = in.readDouble();
		double xh = in.readDouble();
		VectorFont font = new VectorFont(ma, md, la, ld, xh, xh, lg, ma + md);
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
					double advance = in.readDouble();
					List<VectorPath> paths = new ArrayList<VectorPath>();
					int pathCount = in.readInt();
					for (int i = 0; i < pathCount; i++) {
						VectorPath path = new VectorPath();
						while (true) {
							int type = in.readInt();
							if (type == 0x2F637472) {
								break;
							} else if (type == 0x6D6F7665) {
								double x = in.readDouble();
								double y = in.readDouble();
								path.add(new VectorInstruction('M', x, y));
							} else if (type == 0x6C696E65) {
								double x = in.readDouble();
								double y = in.readDouble();
								path.add(new VectorInstruction('L', x, y));
							} else if (type == 0x71756164) {
								double x1 = in.readDouble();
								double y1 = in.readDouble();
								double x2 = in.readDouble();
								double y2 = in.readDouble();
								path.add(new VectorInstruction('Q', x1, y1, x2, y2));
							} else if (type == 0x63756265) {
								double x1 = in.readDouble();
								double y1 = in.readDouble();
								double x2 = in.readDouble();
								double y2 = in.readDouble();
								double x3 = in.readDouble();
								double y3 = in.readDouble();
								path.add(new VectorInstruction('C', x1, y1, x2, y2, x3, y3));
							} else if (type == 0x2F707468) {
								path.add(new VectorInstruction('Z'));
							} else if ((type & 0xFFC0FFF0) == 0x00400000) {
								char operation = (char)(type >> 16);
								Double[] args = new Double[type & 0x0F];
								for (int j = 0; j < args.length; j++) args[j] = in.readDouble();
								path.add(new VectorInstruction(operation, args));
							} else {
								throw new IOException("bad magic number");
							}
						}
						paths.add(path);
					}
					VectorFontGlyph glyph = new VectorFontGlyph(paths, advance);
					font.putCharacter(codePoint, glyph);
					break;
				case 0x66696E2E: // fin.
					font.setCapHeight2D();
					return font;
				default:
					throw new IOException("bad magic number");
			}
		}
	}
}
