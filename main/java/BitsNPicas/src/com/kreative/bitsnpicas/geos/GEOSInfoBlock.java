package com.kreative.bitsnpicas.geos;

import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GEOSInfoBlock implements CBMConstants {
	public static final int ICON_WIDTH = 3;
	public static final int ICON_HEIGHT = 21;
	public static final int ICON_FLAGS = 0xBF;
	
	public int iconWidth;
	public int iconHeight;
	public int iconFlags;
	public byte[] iconBitmap;
	public int cbmFileType;
	public int geosFileType;
	public int fileStructure;
	public int loadAddress;
	public int endAddress;
	public int startAddress;
	public byte[] classText;
	public byte[] author;
	public byte[] creator;
	public byte[] appData;
	public byte[] description;
	
	public GEOSInfoBlock() {
		clear();
	}
	
	public void clear() {
		iconWidth = ICON_WIDTH;
		iconHeight = ICON_HEIGHT;
		iconFlags = ICON_FLAGS;
		iconBitmap = new byte[63];
		cbmFileType = CBM_FILE_TYPE_CLOSED;
		geosFileType = 0;
		fileStructure = 0;
		loadAddress = 0;
		endAddress = 0xFFFF;
		startAddress = 0;
		classText = new byte[20];
		author = new byte[20];
		creator = new byte[20];
		appData = new byte[23];
		description = new byte[96];
	}
	
	public String getIconString() { return GEOSIcons.toString(iconBitmap); }
	public byte[][] getIconGlyph() { return GEOSIcons.toGlyph(iconBitmap); }
	public int[] getIconRGB() { return GEOSIcons.toRGB(iconBitmap); }
	public BufferedImage getIconImage() { return GEOSIcons.toImage(iconBitmap); }
	
	public void setIconGlyph(byte[][] gd) { iconBitmap = GEOSIcons.fromGlyph(gd); }
	public void setIconRGB(int[] rgb) { iconBitmap = GEOSIcons.fromRGB(rgb); }
	public void setIconImage(BufferedImage image, int x, int y) { iconBitmap = GEOSIcons.fromImage(image, x, y); }
	
	public boolean hasAuthorString() {
		switch (geosFileType) {
			case GEOS_FILE_TYPE_BASIC:
			case GEOS_FILE_TYPE_ASSEMBLER:
			case GEOS_FILE_TYPE_DESK_ACCESSORY:
			case GEOS_FILE_TYPE_APPLICATION:
			case GEOS_FILE_TYPE_PRINTER_DRIVER:
			case GEOS_FILE_TYPE_INPUT_DRIVER:
				return true;
			default:
				return false;
		}
	}
	
	public boolean hasCreatorString() {
		return (geosFileType == GEOS_FILE_TYPE_DOCUMENT);
	}
	
	public String getClassTextString() { return toString(classText); }
	public String getAuthorString() { return toString(author); }
	public String getCreatorString() { return toString(creator); }
	public String getDescriptionString() { return toString(description); }
	
	public void setClassTextString(String s) { classText = toByteArray(s); }
	public void setAuthorString(String s) { author = toByteArray(s); }
	public void setCreatorString(String s) { creator = toByteArray(s); }
	public void setDescriptionString(String s) { description = toByteArray(s); }
	
	public byte getByte(int a) {
		a &= 0xFF;
		switch (a) {
			case 0x02: return (byte)iconWidth;
			case 0x03: return (byte)iconHeight;
			case 0x04: return (byte)iconFlags;
			case 0x44: return (byte)cbmFileType;
			case 0x45: return (byte)geosFileType;
			case 0x46: return (byte)fileStructure;
			case 0x47: return (byte)loadAddress;
			case 0x48: return (byte)(loadAddress >> 8);
			case 0x49: return (byte)endAddress;
			case 0x4A: return (byte)(endAddress >> 8);
			case 0x4B: return (byte)startAddress;
			case 0x4C: return (byte)(startAddress >> 8);
			default:
				if (a >= 0x05 && a <= 0x43) return iconBitmap [a - 0x05];
				if (a >= 0x4D && a <= 0x60) return classText  [a - 0x4D];
				if (a >= 0x61 && a <= 0x74) return author     [a - 0x61];
				if (a >= 0x75 && a <= 0x88) return creator    [a - 0x75];
				if (a >= 0x89 && a <= 0x9F) return appData    [a - 0x89];
				if (a >= 0xA0 && a <= 0xFF) return description[a - 0xA0];
				return 0;
		}
	}
	
	public void setByte(int a, byte b) {
		a &= 0xFF;
		switch (a) {
			case 0x02: iconWidth = (b & 0xFF); break;
			case 0x03: iconHeight = (b & 0xFF); break;
			case 0x04: iconFlags = (b & 0xFF); break;
			case 0x44: cbmFileType = (b & 0xFF); break;
			case 0x45: geosFileType = (b & 0xFF); break;
			case 0x46: fileStructure = (b & 0xFF); break;
			case 0x47: loadAddress = (loadAddress & 0xFF00) | (b & 0xFF); break;
			case 0x48: loadAddress = (loadAddress & 0xFF) | ((b & 0xFF) << 8); break;
			case 0x49: endAddress = (endAddress & 0xFF00) | (b & 0xFF); break;
			case 0x4A: endAddress = (endAddress & 0xFF) | ((b & 0xFF) << 8); break;
			case 0x4B: startAddress = (startAddress & 0xFF00) | (b & 0xFF); break;
			case 0x4C: startAddress = (startAddress & 0xFF) | ((b & 0xFF) << 8); break;
			default:
				if (a >= 0x05 && a <= 0x43) iconBitmap [a - 0x05] = b;
				if (a >= 0x4D && a <= 0x60) classText  [a - 0x4D] = b;
				if (a >= 0x61 && a <= 0x74) author     [a - 0x61] = b;
				if (a >= 0x75 && a <= 0x88) creator    [a - 0x75] = b;
				if (a >= 0x89 && a <= 0x9F) appData    [a - 0x89] = b;
				if (a >= 0xA0 && a <= 0xFF) description[a - 0xA0] = b;
				break;
		}
	}
	
	public void read(DataInput in) throws IOException {
		iconWidth = in.readUnsignedByte();
		iconHeight = in.readUnsignedByte();
		iconFlags = in.readUnsignedByte();
		iconBitmap = new byte[63]; in.readFully(iconBitmap);
		cbmFileType = in.readUnsignedByte();
		geosFileType = in.readUnsignedByte();
		fileStructure = in.readUnsignedByte();
		loadAddress = Short.reverseBytes(in.readShort()) & 0xFFFF;
		endAddress = Short.reverseBytes(in.readShort()) & 0xFFFF;
		startAddress = Short.reverseBytes(in.readShort()) & 0xFFFF;
		classText = new byte[20]; in.readFully(classText);
		author = new byte[20]; in.readFully(author);
		creator = new byte[20]; in.readFully(creator);
		appData = new byte[23]; in.readFully(appData);
		description = new byte[96]; in.readFully(description);
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeByte(iconWidth);
		out.writeByte(iconHeight);
		out.writeByte(iconFlags);
		write(out, iconBitmap, 63);
		out.writeByte(cbmFileType);
		out.writeByte(geosFileType);
		out.writeByte(fileStructure);
		out.writeShort(Short.reverseBytes((short)loadAddress));
		out.writeShort(Short.reverseBytes((short)endAddress));
		out.writeShort(Short.reverseBytes((short)startAddress));
		write(out, classText, 20);
		write(out, author, 20);
		write(out, creator, 20);
		write(out, appData, 23);
		write(out, description, 96);
	}
	
	private static String toString(byte[] data) {
		int len = 0;
		while (len < data.length && data[len] != 0) len++;
		try { return new String(data, 0, len, "US-ASCII"); }
		catch (UnsupportedEncodingException e) {
			// Should not happen.
			throw new RuntimeException(e);
		}
	}
	
	private static byte[] toByteArray(String s) {
		try { return s.getBytes("US-ASCII"); }
		catch (UnsupportedEncodingException e) {
			// Should not happen.
			throw new RuntimeException(e);
		}
	}
	
	private static void write(DataOutput out, byte[] data, int len) throws IOException {
		for (int i = 0; i < data.length && i < len; i++) out.writeByte(data[i]);
		for (int i = data.length; i < len; i++) out.writeByte(0);
	}
}
