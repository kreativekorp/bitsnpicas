package com.kreative.bitsnpicas.exporter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.kreative.bitsnpicas.VectorFont;
import com.kreative.bitsnpicas.VectorFontExporter;
import com.kreative.bitsnpicas.VectorFontGlyph;
import com.kreative.bitsnpicas.VectorInstruction;
import com.kreative.bitsnpicas.VectorPath;

public class KpcasVectorFontExporter implements VectorFontExporter {
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
		for (Map.Entry<Integer,String> e : font.names(false).entrySet()) {
			out.writeInt(0x6E616D65); // name
			out.writeInt(1); // version
			out.writeInt(e.getKey());
			out.writeUTF(e.getValue());
		}
		for (Map.Entry<Integer,VectorFontGlyph> e : font.characters(false).entrySet()) {
			out.writeInt(0x63686172); // char
			out.writeInt(1); // version
			out.writeInt(e.getKey());
			VectorFontGlyph g = e.getValue();
			out.writeDouble(g.getCharacterWidth2D());
			Collection<? extends VectorPath> paths = g.getContours();
			out.writeInt(paths.size());
			for (VectorPath path : paths) {
				for (VectorInstruction inst : path) {
					List<Number> vals = inst.getOperands();
					switch (inst.getOperation()) {
						case 'M':
							out.writeInt(0x6D6F7665);
							out.writeDouble((vals.size() > 0) ? vals.get(0).doubleValue() : 0);
							out.writeDouble((vals.size() > 1) ? vals.get(1).doubleValue() : 0);
							break;
						case 'L':
							out.writeInt(0x6C696E65);
							out.writeDouble((vals.size() > 0) ? vals.get(0).doubleValue() : 0);
							out.writeDouble((vals.size() > 1) ? vals.get(1).doubleValue() : 0);
							break;
						case 'Q':
							out.writeInt(0x71756164);
							out.writeDouble((vals.size() > 0) ? vals.get(0).doubleValue() : 0);
							out.writeDouble((vals.size() > 1) ? vals.get(1).doubleValue() : 0);
							out.writeDouble((vals.size() > 2) ? vals.get(2).doubleValue() : 0);
							out.writeDouble((vals.size() > 3) ? vals.get(3).doubleValue() : 0);
							break;
						case 'C':
							out.writeInt(0x63756265);
							out.writeDouble((vals.size() > 0) ? vals.get(0).doubleValue() : 0);
							out.writeDouble((vals.size() > 1) ? vals.get(1).doubleValue() : 0);
							out.writeDouble((vals.size() > 2) ? vals.get(2).doubleValue() : 0);
							out.writeDouble((vals.size() > 3) ? vals.get(3).doubleValue() : 0);
							out.writeDouble((vals.size() > 4) ? vals.get(4).doubleValue() : 0);
							out.writeDouble((vals.size() > 5) ? vals.get(5).doubleValue() : 0);
							break;
						case 'Z':
							out.writeInt(0x2F707468);
							break;
						default:
							out.writeShort(inst.getOperation());
							out.writeShort(vals.size());
							for (Number n : vals) out.writeDouble(n.doubleValue());
							break;
					}
				}
				out.writeInt(0x2F637472);
			}
		}
		out.writeInt(0x66696E2E); // fin.
	}
}
