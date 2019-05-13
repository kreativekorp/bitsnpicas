package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SbitLineMetrics {
	public int ascender;
	public int descender;
	public int widthMax;
	public int caretSlopeNumerator;
	public int caretSlopeDenominator;
	public int caretOffset;
	public int minOriginSB;
	public int minAdvanceSB;
	public int maxBeforeBL;
	public int minAfterBL;
	
	protected void read(DataInputStream in) throws IOException {
		ascender = in.readByte();
		descender = in.readByte();
		widthMax = in.readUnsignedByte();
		caretSlopeNumerator = in.readByte();
		caretSlopeDenominator = in.readByte();
		caretOffset = in.readByte();
		minOriginSB = in.readByte();
		minAdvanceSB = in.readByte();
		maxBeforeBL = in.readByte();
		minAfterBL = in.readByte();
		/* pad = */ in.readShort();
	}
	
	protected void write(DataOutputStream out) throws IOException {
		out.writeByte(ascender);
		out.writeByte(descender);
		out.writeByte(widthMax);
		out.writeByte(caretSlopeNumerator);
		out.writeByte(caretSlopeDenominator);
		out.writeByte(caretOffset);
		out.writeByte(minOriginSB);
		out.writeByte(minAdvanceSB);
		out.writeByte(maxBeforeBL);
		out.writeByte(minAfterBL);
		out.writeShort(0);
	}
}
