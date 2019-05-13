package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EblcBitmapSize extends ArrayList<EblcIndexSubtable> {
	private static final long serialVersionUID = 1L;
	
	public static final int BIT_DEPTH_BLACK_WHITE = 1;
	public static final int BIT_DEPTH_LEVELS_4 = 2;
	public static final int BIT_DEPTH_LEVELS_16 = 4;
	public static final int BIT_DEPTH_LEVELS_256 = 8;
	
	public static final int FLAGS_HORIZONTAL_METRICS = 1;
	public static final int FLAGS_VERTICAL_METRICS = 2;
	
	public int indexSubTableArrayOffset;
	public int indexTablesSize;
	public int numberOfIndexSubTables;
	public int colorRef;
	public SbitLineMetrics hori;
	public SbitLineMetrics vert;
	public int startGlyphIndex;
	public int endGlyphIndex;
	public int ppemX;
	public int ppemY;
	public int bitDepth;
	public int flags;
	
	protected void read(DataInputStream in) throws IOException {
		indexSubTableArrayOffset = in.readInt();
		indexTablesSize = in.readInt();
		numberOfIndexSubTables = in.readInt();
		colorRef = in.readInt();
		hori = new SbitLineMetrics(); hori.read(in);
		vert = new SbitLineMetrics(); vert.read(in);
		startGlyphIndex = in.readUnsignedShort();
		endGlyphIndex = in.readUnsignedShort();
		ppemX = in.readUnsignedByte();
		ppemY = in.readUnsignedByte();
		bitDepth = in.readUnsignedByte();
		flags = in.readUnsignedByte();
	}
	
	protected void write(DataOutputStream out) throws IOException {
		out.writeInt(indexSubTableArrayOffset);
		out.writeInt(indexTablesSize);
		out.writeInt(numberOfIndexSubTables);
		out.writeInt(colorRef);
		((hori != null) ? hori : new SbitLineMetrics()).write(out);
		((vert != null) ? vert : new SbitLineMetrics()).write(out);
		out.writeShort(startGlyphIndex);
		out.writeShort(endGlyphIndex);
		out.writeByte(ppemX);
		out.writeByte(ppemY);
		out.writeByte(bitDepth);
		out.writeByte(flags);
	}
}
