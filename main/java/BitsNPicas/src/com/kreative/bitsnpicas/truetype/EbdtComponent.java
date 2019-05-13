package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EbdtComponent {
	public int glyphID;
	public int xOffset;
	public int yOffset;
	
	protected void read(DataInputStream in) throws IOException {
		glyphID = in.readUnsignedShort();
		xOffset = in.readByte();
		yOffset = in.readByte();
	}
	
	protected void write(DataOutputStream out) throws IOException {
		out.writeShort(glyphID);
		out.writeByte(xOffset);
		out.writeByte(yOffset);
	}
}
