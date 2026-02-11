package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HheaTable extends TrueTypeTable {
	public static final int VERSION_DEFAULT = 0x00010000;
	
	public int version = VERSION_DEFAULT;
	public int ascent = 0;
	public int descent = 0;
	public int lineGap = 0;
	public int advanceWidthMax = 0;
	public int minLeftSideBearing = 0;
	public int minRightSideBearing = 0;
	public int xMaxExtent = 0;
	public int caretSlopeRise = 1;
	public int caretSlopeRun = 0;
	public int caretOffset = 0;
	public int reserved1 = 0;
	public int reserved2 = 0;
	public int reserved3 = 0;
	public int reserved4 = 0;
	public int metricDataFormat = 0;
	public int numLongHorMetrics = 0;
	
	@Override
	public String tableName() {
		return "hhea";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		out.writeInt(version);
		out.writeShort(ascent);
		out.writeShort(descent);
		out.writeShort(lineGap);
		out.writeShort(advanceWidthMax);
		out.writeShort(minLeftSideBearing);
		out.writeShort(minRightSideBearing);
		out.writeShort(xMaxExtent);
		out.writeShort(caretSlopeRise);
		out.writeShort(caretSlopeRun);
		out.writeShort(caretOffset);
		out.writeShort(reserved1);
		out.writeShort(reserved2);
		out.writeShort(reserved3);
		out.writeShort(reserved4);
		out.writeShort(metricDataFormat);
		out.writeShort(numLongHorMetrics);
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		version = in.readInt();
		ascent = in.readShort();
		descent = in.readShort();
		lineGap = in.readShort();
		advanceWidthMax = in.readUnsignedShort();
		minLeftSideBearing = in.readShort();
		minRightSideBearing = in.readShort();
		xMaxExtent = in.readShort();
		caretSlopeRise = in.readShort();
		caretSlopeRun = in.readShort();
		caretOffset = in.readShort();
		reserved1 = in.readShort();
		reserved2 = in.readShort();
		reserved3 = in.readShort();
		reserved4 = in.readShort();
		metricDataFormat = in.readShort();
		numLongHorMetrics = in.readUnsignedShort();
	}
}
