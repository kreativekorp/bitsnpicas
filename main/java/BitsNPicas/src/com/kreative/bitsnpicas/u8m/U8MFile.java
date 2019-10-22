package com.kreative.bitsnpicas.u8m;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;

public class U8MFile {
	public static final int MAGIC_NUMBER = 0x55382F4D;
	
	public String familyName;
	public int familyID;
	public int style;
	public int pointSize;
	public int glyphTableLocation;
	public int glyphTableSize;
	public int mapTableLocation;
	public int mapTableSize;
	public final int[] nativeMapIndex  = new int[4];
	public final int[] lowBMPMapIndex  = new int[32];
	public final int[] highBMPMapIndex = new int[16];
	public final int[] astralMapIndex  = new int[6];
	public int lineAscent;
	public int lineDescent;
	public int lineGap;
	public int lineHeight;
	public final U8MMapTable maps = new U8MMapTable();
	public final U8MGlyphTable glyphs = new U8MGlyphTable();
	
	public int createMap() {
		int i = maps.size();
		maps.add(new U8MMapData());
		return i;
	}
	
	public int createGlyph() {
		int i = glyphs.size();
		glyphs.add(new U8MGlyph());
		return i;
	}
	
	public int getNativeGlyphIndex(int cp) {
		if (cp < 0 || cp > 0xFF) return 0;
		int i = nativeMapIndex[cp >> 6];
		if (i == 0) return 0;
		return maps.get(i).mapGet(cp & 0x3F);
	}
	
	public int getLowBMPGlyphIndex(int cp) {
		if (cp < 0 || cp > 0x07FF) return 0;
		int i = lowBMPMapIndex[cp >> 6];
		if (i == 0) return 0;
		return maps.get(i).mapGet(cp & 0x3F);
	}
	
	public int getHighBMPGlyphIndex(int cp) {
		if (cp < 0 || cp > 0xFFFF) return 0;
		int i = highBMPMapIndex[cp >> 12];
		if (i == 0) return 0;
		i = maps.get(i).mapGet((cp >> 6) & 0x3F);
		if (i == 0) return 0;
		return maps.get(i).mapGet(cp & 0x3F);
	}
	
	public int getAstralGlyphIndex(int cp) {
		if (cp < 0 || cp > 0x17FFFF) return 0;
		int i = astralMapIndex[cp >> 18];
		if (i == 0) return 0;
		i = maps.get(i).mapGet((cp >> 12) & 0x3F);
		if (i == 0) return 0;
		i = maps.get(i).mapGet((cp >> 6) & 0x3F);
		if (i == 0) return 0;
		return maps.get(i).mapGet(cp & 0x3F);
	}
	
	public int getUnicodeGlyphIndex(int cp) {
		int i = getLowBMPGlyphIndex(cp);
		if (i != 0) return i;
		i = getHighBMPGlyphIndex(cp);
		if (i != 0) return i;
		return getAstralGlyphIndex(cp);
	}
	
	public void setNativeGlyphIndex(int cp, int index) {
		if (cp < 0 || cp > 0xFF) return;
		int i = nativeMapIndex[cp >> 6];
		if (i == 0) nativeMapIndex[cp >> 6] = (i = createMap());
		maps.get(i).mapPut(cp & 0x3F, index);
	}
	
	public void setLowBMPGlyphIndex(int cp, int index) {
		if (cp < 0 || cp > 0x07FF) return;
		int i = lowBMPMapIndex[cp >> 6];
		if (i == 0) lowBMPMapIndex[cp >> 6] = (i = createMap());
		maps.get(i).mapPut(cp & 0x3F, index);
	}
	
	public void setHighBMPGlyphIndex(int cp, int index) {
		if (cp < 0 || cp > 0xFFFF) return;
		int i = highBMPMapIndex[cp >> 12];
		if (i == 0) highBMPMapIndex[cp >> 12] = (i = createMap());
		U8MMapData map = maps.get(i);
		i = map.mapGet((cp >> 6) & 0x3F);
		if (i == 0) map.mapPut((cp >> 6) & 0x3F, (i = createMap()));
		maps.get(i).mapPut(cp & 0x3F, index);
	}
	
	public void setAstralGlyphIndex(int cp, int index) {
		if (cp < 0 || cp > 0x17FFFF) return;
		int i = astralMapIndex[cp >> 18];
		if (i == 0) astralMapIndex[cp >> 18] = (i = createMap());
		U8MMapData map = maps.get(i);
		i = map.mapGet((cp >> 12) & 0x3F);
		if (i == 0) map.mapPut((cp >> 12) & 0x3F, (i = createMap()));
		map = maps.get(i);
		i = map.mapGet((cp >> 6) & 0x3F);
		if (i == 0) map.mapPut((cp >> 6) & 0x3F, (i = createMap()));
		maps.get(i).mapPut(cp & 0x3F, index);
	}
	
	public void setUnicodeGlyphIndex(int cp, int index) {
		if (cp >= 0x10000) setAstralGlyphIndex(cp, index);
		else if (cp >= 0x800) setHighBMPGlyphIndex(cp, index);
		else setLowBMPGlyphIndex(cp, index);
	}
	
	public int setLocations(int loc) {
		loc += 0x100;
		if ((loc & 0xFF) != 0) loc = (loc | 0xFF) + 1;
		mapTableLocation = loc;
		mapTableSize = maps.size();
		loc = maps.setMapLocations(loc);
		if ((loc & 0xFF) != 0) loc = (loc | 0xFF) + 1;
		glyphTableLocation = loc;
		glyphTableSize = glyphs.size();
		loc = glyphs.setBitmapLocations(loc);
		return loc;
	}
	
	public void readHeader(DataInput in) throws IOException {
		if (in.readInt() != MAGIC_NUMBER) throw new IOException("bad magic");
		int nameLength = in.readUnsignedByte();
		byte[] nameData = new byte[118]; in.readFully(nameData);
		if (in.readByte() != 0) throw new IOException("bad name terminator");
		familyName = new String(nameData, 0, Math.min(nameLength, 118), "UTF-8");
		familyID = Short.reverseBytes(in.readShort()) & 0xFFFF;
		style = in.readUnsignedByte();
		pointSize = in.readUnsignedByte();
		glyphTableLocation = (Short.reverseBytes(in.readShort()) & 0xFFFF) << 8;
		glyphTableSize = Short.reverseBytes(in.readShort()) & 0xFFFF;
		mapTableLocation = (Short.reverseBytes(in.readShort()) & 0xFFFF) << 8;
		mapTableSize = Short.reverseBytes(in.readShort()) & 0xFFFF;
		for (int i = 0; i <  4; i++) nativeMapIndex [i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
		for (int i = 0; i < 32; i++) lowBMPMapIndex [i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
		for (int i = 0; i < 16; i++) highBMPMapIndex[i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
		for (int i = 0; i <  6; i++) astralMapIndex [i] = Short.reverseBytes(in.readShort()) & 0xFFFF;
		lineAscent = in.readUnsignedByte();
		lineDescent = in.readUnsignedByte();
		lineGap = in.readUnsignedByte();
		lineHeight = in.readUnsignedByte();
	}
	
	public void writeHeader(DataOutput out) throws IOException {
		out.writeInt(MAGIC_NUMBER);
		if (familyName == null) {
			out.write(new byte[120]);
		} else {
			byte[] nameData = familyName.getBytes("UTF-8");
			int nameLength = Math.min(nameData.length, 118);
			out.writeByte(nameLength);
			out.write(nameData, 0, nameLength);
			out.write(new byte[118 - nameLength]);
			out.writeByte(0);
		}
		out.writeShort(Short.reverseBytes((short)familyID));
		out.writeByte(style);
		out.writeByte(pointSize);
		out.writeShort(Short.reverseBytes((short)(glyphTableLocation >> 8)));
		out.writeShort(Short.reverseBytes((short)glyphTableSize));
		out.writeShort(Short.reverseBytes((short)(mapTableLocation >> 8)));
		out.writeShort(Short.reverseBytes((short)mapTableSize));
		for (int i = 0; i <  4; i++) out.writeShort(Short.reverseBytes((short)nativeMapIndex [i]));
		for (int i = 0; i < 32; i++) out.writeShort(Short.reverseBytes((short)lowBMPMapIndex [i]));
		for (int i = 0; i < 16; i++) out.writeShort(Short.reverseBytes((short)highBMPMapIndex[i]));
		for (int i = 0; i <  6; i++) out.writeShort(Short.reverseBytes((short)astralMapIndex [i]));
		out.writeByte(lineAscent);
		out.writeByte(lineDescent);
		out.writeByte(lineGap);
		out.writeByte(lineHeight);
	}
	
	public void read(RandomAccessFile raf, long seekBase) throws IOException {
		readHeader(raf);
		
		maps.clear();
		raf.seek(seekBase + mapTableLocation);
		for (int i = 0; i < mapTableSize; i++) {
			U8MMapData m = new U8MMapData();
			m.readHeader(raf);
			maps.add(m);
		}
		for (U8MMapData m : maps) {
			if (m.mapLocation != 0) {
				raf.seek(seekBase + m.mapLocation);
				m.readData(raf);
			}
		}
		
		glyphs.clear();
		raf.seek(seekBase + glyphTableLocation);
		for (int i = 0; i < glyphTableSize; i++) {
			U8MGlyph g = new U8MGlyph();
			g.readGlyphRecord(raf);
			glyphs.add(g);
		}
		for (U8MGlyph g : glyphs) {
			if (g.bitmapLocation != 0) {
				raf.seek(seekBase + g.bitmapLocation);
				g.readBitmapRecord(raf);
			}
		}
	}
	
	public void write(RandomAccessFile raf, int locationBase, long seekBase) throws IOException {
		setLocations(locationBase);
		writeHeader(raf);
		
		raf.seek(seekBase + mapTableLocation);
		for (U8MMapData m : maps) {
			m.writeHeader(raf);
		}
		for (U8MMapData m : maps) {
			if (m.mapLocation != 0) {
				raf.seek(seekBase + m.mapLocation);
				m.writeData(raf);
			}
		}
		
		raf.seek(seekBase + glyphTableLocation);
		for (U8MGlyph g : glyphs) {
			g.writeGlyphRecord(raf);
		}
		for (U8MGlyph g : glyphs) {
			if (g.bitmapLocation != 0) {
				raf.seek(seekBase + g.bitmapLocation);
				g.writeBitmapRecord(raf);
			}
		}
	}
}
