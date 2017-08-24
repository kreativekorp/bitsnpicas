package com.kreative.bitsnpicas.exporter;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import com.kreative.bitsnpicas.VectorFont;
import com.kreative.bitsnpicas.VectorFontExporter;
import com.kreative.bitsnpicas.VectorFontGlyph;

public class KBnPVectorFontExporter implements VectorFontExporter {
	@Override
	public byte[] exportFontToBytes(VectorFont font) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		exportFont(font, dos);
		dos.close();
		bos.close();
		return bos.toByteArray();
	}
	
	@Override
	public void exportFontToStream(VectorFont font, OutputStream os) throws IOException {
		exportFont(font, new DataOutputStream(os));
	}
	
	@Override
	public void exportFontToFile(VectorFont font, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		DataOutputStream dos = new DataOutputStream(fos);
		exportFont(font, dos);
		dos.close();
		fos.close();
	}
	
	private void exportFont(VectorFont font, DataOutputStream out) throws IOException {
		out.writeInt(0x4B426E50); // KBnP
		out.writeInt(0x70636173); // pcas
		out.writeInt(1); // version
		out.writeDouble(font.getEmAscent2D());
		out.writeDouble(font.getEmDescent2D());
		out.writeDouble(font.getLineAscent2D());
		out.writeDouble(font.getLineDescent2D());
		out.writeDouble(font.getLineGap2D());
		out.writeDouble(font.getXHeight2D());
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
			VectorFontGlyph g = font.getCharacter(codePoint);
			out.writeDouble(g.getCharacterWidth2D());
			Collection<? extends GeneralPath> paths = g.getContours();
			out.writeInt(paths.size());
			for (GeneralPath path : paths) {
				PathIterator pi = path.getPathIterator(null);
				double[] vals = new double[8];
				while (!pi.isDone()) {
					int type = pi.currentSegment(vals);
					switch (type) {
						case PathIterator.SEG_MOVETO:
							out.writeInt(0x6D6F7665);
							out.writeDouble(vals[0]);
							out.writeDouble(vals[1]);
							break;
						case PathIterator.SEG_LINETO:
							out.writeInt(0x6C696E65);
							out.writeDouble(vals[0]);
							out.writeDouble(vals[1]);
							break;
						case PathIterator.SEG_QUADTO:
							out.writeInt(0x71756164);
							out.writeDouble(vals[0]);
							out.writeDouble(vals[1]);
							out.writeDouble(vals[2]);
							out.writeDouble(vals[3]);
							break;
						case PathIterator.SEG_CUBICTO:
							out.writeInt(0x63756265);
							out.writeDouble(vals[0]);
							out.writeDouble(vals[1]);
							out.writeDouble(vals[2]);
							out.writeDouble(vals[3]);
							out.writeDouble(vals[4]);
							out.writeDouble(vals[5]);
							break;
						case PathIterator.SEG_CLOSE:
							out.writeInt(0x2F707468);
							break;
					}
					pi.next();
				}
				out.writeInt(0x2F637472);
			}
		}
		out.writeInt(0x66696E2E); // fin.
	}
}
