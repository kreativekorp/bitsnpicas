package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SbitBigGlyphMetrics {
	public int height;
	public int width;
	public int horiBearingX;
	public int horiBearingY;
	public int horiAdvance;
	public int vertBearingX;
	public int vertBearingY;
	public int vertAdvance;
	
	protected void read(DataInputStream in) throws IOException {
		height = in.readUnsignedByte();
		width = in.readUnsignedByte();
		horiBearingX = in.readByte();
		horiBearingY = in.readByte();
		horiAdvance = in.readUnsignedByte();
		vertBearingX = in.readByte();
		vertBearingY = in.readByte();
		vertAdvance = in.readUnsignedByte();
	}
	
	protected void write(DataOutputStream out) throws IOException {
		out.writeByte(height);
		out.writeByte(width);
		out.writeByte(horiBearingX);
		out.writeByte(horiBearingY);
		out.writeByte(horiAdvance);
		out.writeByte(vertBearingX);
		out.writeByte(vertBearingY);
		out.writeByte(vertAdvance);
	}
}
