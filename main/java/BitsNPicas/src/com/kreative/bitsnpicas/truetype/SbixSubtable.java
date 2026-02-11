package com.kreative.bitsnpicas.truetype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SbixSubtable extends ArrayList<SbixEntry> {
	private static final long serialVersionUID = 1L;
	
	public static final int DPI_DEFAULT = 72;
	
	public int ppem = 0;
	public int dpi = DPI_DEFAULT;
	
	private void compile(DataOutputStream out, int numGlyphs) throws IOException {
		out.writeShort(ppem);
		out.writeShort(dpi);
		List<byte[]> imageData = new ArrayList<byte[]>();
		int currentLocation = 4 + (this.size() + 1) * 4;
		for (SbixEntry e : this) {
			out.writeInt(currentLocation);
			byte[] d = e.compile();
			imageData.add(d);
			currentLocation += d.length;
		}
		out.writeInt(currentLocation);
		for (byte[] d : imageData) {
			out.write(d);
		}
		while ((currentLocation & 0x03) != 0) {
			out.writeByte(0);
			currentLocation++;
		}
	}
	
	public byte[] compile(int numGlyphs) throws IOException {
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteout);
		compile(out, numGlyphs);
		out.flush();
		byteout.flush();
		out.close();
		byteout.close();
		return byteout.toByteArray();
	}
	
	private void decompile(DataInputStream in, int length, int numGlyphs) throws IOException {
		ppem = in.readUnsignedShort();
		dpi = in.readUnsignedShort();
		int[] imageOffset = new int[numGlyphs + 1];
		for (int i = 0; i <= numGlyphs; i++) {
			imageOffset[i] = in.readInt();
		}
		this.clear();
		for (int i = 0; i < numGlyphs; i++) {
			in.reset();
			in.skipBytes(imageOffset[i]);
			byte[] d = new byte[imageOffset[i+1] - imageOffset[i]];
			in.readFully(d);
			SbixEntry e = new SbixEntry();
			e.decompile(d);
			this.add(e);
		}
	}
	
	public void decompile(byte[] data, int numGlyphs) throws IOException {
		ByteArrayInputStream bytein = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(bytein);
		decompile(in, data.length, numGlyphs);
		in.close();
		bytein.close();
	}
}
