package com.kreative.bitsnpicas.mover;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

public class FONDResource {
	public String name;
	public int flags;
	public int id;
	public int firstChar;
	public int lastChar;
	public int maxAscent;
	public int maxDescent;
	public int maxLeading;
	public int maxCharWidth;
	public int offsetToGlyphWidthTable;
	public int offsetToKerningTable;
	public int offsetToStyleMappingTable;
	public int extraWidthForPlain;
	public int extraWidthForBold;
	public int extraWidthForItalic;
	public int extraWidthForUnderline;
	public int extraWidthForOutline;
	public int extraWidthForShadow;
	public int extraWidthForCondensed;
	public int extraWidthForExtended;
	public int extraWidthForGroup;
	public int internationalInfo;
	public int version;
	public final SortedSet<FONDEntry> entries = new TreeSet<FONDEntry>();
	public byte[] optionalTables;
	
	public FONDResource(String name, int id) {
		this.name = name;
		this.flags = 0x6000;
		this.id = id;
		this.version = 1;
	}
	
	public FONDResource(FONDResource src, Iterable<FONDEntry> ee) {
		this.name = src.name;
		this.flags = src.flags;
		this.id = src.id;
		this.firstChar = src.firstChar;
		this.lastChar = src.lastChar;
		this.maxAscent = src.maxAscent;
		this.maxDescent = src.maxDescent;
		this.maxLeading = src.maxLeading;
		this.maxCharWidth = src.maxCharWidth;
		this.offsetToGlyphWidthTable = src.offsetToGlyphWidthTable;
		this.offsetToKerningTable = src.offsetToKerningTable;
		this.offsetToStyleMappingTable = src.offsetToStyleMappingTable;
		this.extraWidthForPlain = src.extraWidthForPlain;
		this.extraWidthForBold = src.extraWidthForBold;
		this.extraWidthForItalic = src.extraWidthForItalic;
		this.extraWidthForUnderline = src.extraWidthForUnderline;
		this.extraWidthForOutline = src.extraWidthForOutline;
		this.extraWidthForShadow = src.extraWidthForShadow;
		this.extraWidthForCondensed = src.extraWidthForCondensed;
		this.extraWidthForExtended = src.extraWidthForExtended;
		this.extraWidthForGroup = src.extraWidthForGroup;
		this.internationalInfo = src.internationalInfo;
		this.version = src.version;
		for (FONDEntry e : ee) this.entries.add(e);
		this.optionalTables = src.optionalTables;
	}
	
	public FONDResource(String name, DataInput in, int length) throws IOException {
		this.name = name;
		this.flags = in.readUnsignedShort();
		this.id = in.readShort();
		this.firstChar = in.readUnsignedShort();
		this.lastChar = in.readUnsignedShort();
		this.maxAscent = in.readUnsignedShort();
		this.maxDescent = in.readUnsignedShort();
		this.maxLeading = in.readUnsignedShort();
		this.maxCharWidth = in.readUnsignedShort();
		this.offsetToGlyphWidthTable = in.readInt();
		this.offsetToKerningTable = in.readInt();
		this.offsetToStyleMappingTable = in.readInt();
		this.extraWidthForPlain = in.readUnsignedShort();
		this.extraWidthForBold = in.readUnsignedShort();
		this.extraWidthForItalic = in.readUnsignedShort();
		this.extraWidthForUnderline = in.readUnsignedShort();
		this.extraWidthForOutline = in.readUnsignedShort();
		this.extraWidthForShadow = in.readUnsignedShort();
		this.extraWidthForCondensed = in.readUnsignedShort();
		this.extraWidthForExtended = in.readUnsignedShort();
		this.extraWidthForGroup = in.readUnsignedShort();
		this.internationalInfo = in.readInt();
		this.version = in.readUnsignedShort();
		int count = in.readShort() + 1;
		for (int i = 0; i < count; i++) entries.add(new FONDEntry(in));
		int end = 54 + 6 * count;
		if (offsetToGlyphWidthTable != 0) offsetToGlyphWidthTable -= end;
		if (offsetToKerningTable != 0) offsetToKerningTable -= end;
		if (offsetToStyleMappingTable != 0) offsetToStyleMappingTable -= end;
		if (length > end) in.readFully(optionalTables = new byte[length - end]);
	}
	
	public FONDResource(String name, byte[] data) throws IOException {
		this(name, new DataInputStream(new ByteArrayInputStream(data)), data.length);
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeShort(flags);
		out.writeShort(id);
		out.writeShort(firstChar);
		out.writeShort(lastChar);
		out.writeShort(maxAscent);
		out.writeShort(maxDescent);
		out.writeShort(maxLeading);
		out.writeShort(maxCharWidth);
		int end = 54 + 6 * entries.size();
		out.writeInt((offsetToGlyphWidthTable == 0) ? 0 : (offsetToGlyphWidthTable + end));
		out.writeInt((offsetToKerningTable == 0) ? 0 : (offsetToKerningTable + end));
		out.writeInt((offsetToStyleMappingTable == 0) ? 0 : (offsetToStyleMappingTable + end));
		out.writeShort(extraWidthForPlain);
		out.writeShort(extraWidthForBold);
		out.writeShort(extraWidthForItalic);
		out.writeShort(extraWidthForUnderline);
		out.writeShort(extraWidthForOutline);
		out.writeShort(extraWidthForShadow);
		out.writeShort(extraWidthForExtended);
		out.writeShort(extraWidthForCondensed);
		out.writeShort(extraWidthForGroup);
		out.writeInt(internationalInfo);
		out.writeShort(version);
		out.writeShort(entries.size() - 1);
		for (FONDEntry e : entries) e.write(out);
		if (optionalTables != null) out.write(optionalTables);
	}
	
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		write(new DataOutputStream(out));
		out.flush(); out.close();
		return out.toByteArray();
	}
	
	public int length() {
		int length = 54 + 6 * entries.size();
		if (optionalTables != null) length += optionalTables.length;
		return length;
	}
	
	@Override
	public String toString() {
		if (entries.size() == 1) {
			return entries.first().toString(name);
		} else {
			return name;
		}
	}
}
