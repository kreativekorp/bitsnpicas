package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CpalTable extends TrueTypeTable {
	public static final int USABLE_WITH_LIGHT_BACKGROUND = 0x0001;
	public static final int USABLE_WITH_DARK_BACKGROUND  = 0x0002;
	
	public int version = 0;
	public int numPaletteEntries = 0;
	public int[] colorRecordIndices = new int[0];
	public int[] colorRecordsArray = new int[0];
	public int[] paletteTypesArray = null;
	public int[] paletteLabelsArray = null;
	public int[] paletteEntryLabelsArray = null;
	
	@Override
	public String tableName() {
		return "CPAL";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		int offset = colorRecordIndices.length * 2 + 12;
		if (version >= 1) offset += 12;
		
		// Version 0 Header
		out.writeShort(version);
		out.writeShort(numPaletteEntries);
		out.writeShort(colorRecordIndices.length);
		out.writeShort(colorRecordsArray.length);
		out.writeInt(offset); // colorRecordsArrayOffset
		offset += colorRecordsArray.length * 4;
		for (int i : colorRecordIndices) {
			out.writeShort(i);
		}
		
		// Version 1 Header
		if (version >= 1) {
			if (paletteTypesArray != null) {
				out.writeInt(offset);
				offset += paletteTypesArray.length * 4;
			} else {
				out.writeInt(0);
			}
			if (paletteLabelsArray != null) {
				out.writeInt(offset);
				offset += paletteLabelsArray.length * 2;
			} else {
				out.writeInt(0);
			}
			if (paletteEntryLabelsArray != null) {
				out.writeInt(offset);
				offset += paletteEntryLabelsArray.length * 2;
			} else {
				out.writeInt(0);
			}
		}
		
		// Color Records
		for (int argb : colorRecordsArray) {
			out.writeInt(Integer.reverseBytes(argb));
		}
		
		// Palette Records
		if (version >= 1) {
			if (paletteTypesArray != null) {
				for (int type : paletteTypesArray) {
					out.writeInt(type);
				}
			}
			if (paletteLabelsArray != null) {
				for (int label : paletteLabelsArray) {
					out.writeShort(label);
				}
			}
			if (paletteEntryLabelsArray != null) {
				for (int label : paletteEntryLabelsArray) {
					out.writeShort(label);
				}
			}
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		// Version 0 Header
		version = in.readUnsignedShort();
		numPaletteEntries = in.readUnsignedShort();
		colorRecordIndices = new int[in.readUnsignedShort()];
		colorRecordsArray = new int[in.readUnsignedShort()];
		int colorRecordsArrayOffset = in.readInt();
		for (int i = 0; i < colorRecordIndices.length; i++) {
			colorRecordIndices[i] = in.readUnsignedShort();
		}
		
		// Version 1 Header
		int paletteTypesArrayOffset = (version < 1) ? 0 : in.readInt();
		int paletteLabelsArrayOffset = (version < 1) ? 0 : in.readInt();
		int paletteEntryLabelsArrayOffset = (version < 1) ? 0 : in.readInt();
		
		// Color Records
		in.reset();
		in.skipBytes(colorRecordsArrayOffset);
		for (int i = 0; i < colorRecordsArray.length; i++) {
			colorRecordsArray[i] = Integer.reverseBytes(in.readInt());
		}
		
		// Palette Records
		if (paletteTypesArrayOffset > 0) {
			in.reset();
			in.skipBytes(paletteTypesArrayOffset);
			paletteTypesArray = new int[colorRecordIndices.length];
			for (int i = 0; i < colorRecordIndices.length; i++) {
				paletteTypesArray[i] = in.readInt();
			}
		} else {
			paletteTypesArray = null;
		}
		
		if (paletteLabelsArrayOffset > 0) {
			in.reset();
			in.skipBytes(paletteLabelsArrayOffset);
			paletteLabelsArray = new int[colorRecordIndices.length];
			for (int i = 0; i < colorRecordIndices.length; i++) {
				paletteLabelsArray[i] = in.readUnsignedShort();
			}
		} else {
			paletteLabelsArray = null;
		}
		
		if (paletteEntryLabelsArrayOffset > 0) {
			in.reset();
			in.skipBytes(paletteEntryLabelsArrayOffset);
			paletteEntryLabelsArray = new int[colorRecordsArray.length];
			for (int i = 0; i < colorRecordsArray.length; i++) {
				paletteEntryLabelsArray[i] = in.readUnsignedShort();
			}
		} else {
			paletteEntryLabelsArray = null;
		}
	}
}
