package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SbitSmallGlyphMetrics {
	public int height;
	public int width;
	public int bearingX;
	public int bearingY;
	public int advance;
	
	protected void read(DataInputStream in) throws IOException {
		height = in.readUnsignedByte();
		width = in.readUnsignedByte();
		bearingX = in.readByte();
		bearingY = in.readByte();
		advance = in.readUnsignedByte();
	}
	
	protected void write(DataOutputStream out) throws IOException {
		out.writeByte(height);
		out.writeByte(width);
		out.writeByte(bearingX);
		out.writeByte(bearingY);
		out.writeByte(advance);
	}
}
