package com.kreative.bitsnpicas.geos;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CBMDirectoryBlock implements CBMConstants {
	public static final String COMMENT_PRG = "PRG formatted GEOS file";
	public static final String COMMENT_SEQ = "SEQ formatted GEOS file";
	
	public int cbmFileType;
	public int startingTrack;
	public int startingSector;
	public byte[] fileName;
	public int sideTrack;
	public int sideSector;
	public int recordLength;
	public int infoTrack;
	public int infoSector;
	public int fileStructure;
	public int geosFileType;
	public int year;
	public int month;
	public int day;
	public int hour;
	public int minute;
	public int sectorSize;
	public byte[] comment;
	
	public CBMDirectoryBlock() {
		clear();
	}
	
	public void clear() {
		cbmFileType = CBM_FILE_TYPE_CLOSED;
		startingTrack = 0;
		startingSector = 0;
		fileName = new byte[0];
		sideTrack = 0;
		sideSector = 0;
		recordLength = 0;
		infoTrack = 0;
		infoSector = 0;
		fileStructure = 0;
		geosFileType = 0;
		GregorianCalendar now = new GregorianCalendar();
		year = now.get(Calendar.YEAR);
		month = now.get(Calendar.MONTH) + 1;
		day = now.get(Calendar.DAY_OF_MONTH);
		hour = now.get(Calendar.HOUR_OF_DAY);
		minute = now.get(Calendar.MINUTE);
		sectorSize = 0;
		comment = new byte[0];
	}
	
	public String getFileTypeString() {
		int type = cbmFileType & CBM_FILE_TYPE_MASK;
		switch (type) {
			case CBM_FILE_TYPE_DEL: return "DEL";
			case CBM_FILE_TYPE_SEQ: return "SEQ";
			case CBM_FILE_TYPE_PRG: return "PRG";
			case CBM_FILE_TYPE_USR: return "USR";
			case CBM_FILE_TYPE_REL: return "REL";
			default: return "[" + Integer.toHexString(type).toUpperCase() + "]";
		}
	}
	
	public boolean setFileTypeString(String s) {
		char[] ch = s.trim().toCharArray();
		if (ch.length > 0) {
			switch (ch[0]) {
				case 'D': case 'd':
					cbmFileType &=~ CBM_FILE_TYPE_MASK;
					cbmFileType |= CBM_FILE_TYPE_DEL;
					return true;
				case 'S': case 's':
					cbmFileType &=~ CBM_FILE_TYPE_MASK;
					cbmFileType |= CBM_FILE_TYPE_SEQ;
					return true;
				case 'P': case 'p':
					cbmFileType &=~ CBM_FILE_TYPE_MASK;
					cbmFileType |= CBM_FILE_TYPE_PRG;
					return true;
				case 'U': case 'u':
					cbmFileType &=~ CBM_FILE_TYPE_MASK;
					cbmFileType |= CBM_FILE_TYPE_USR;
					return true;
				case 'R': case 'r':
					cbmFileType &=~ CBM_FILE_TYPE_MASK;
					cbmFileType |= CBM_FILE_TYPE_REL;
					return true;
			}
		}
		return false;
	}
	
	public String getFileName(boolean alt, boolean geos) {
		if (geos && (geosFileType != 0)) {
			try { return new String(fileName, "US-ASCII"); }
			catch (UnsupportedEncodingException e) {
				// Should not happen.
				throw new RuntimeException(e);
			}
		} else {
			return PETSCII.toString(fileName, alt);
		}
	}
	
	public void setFileName(String name, boolean alt, boolean geos) {
		if (geos && (geosFileType != 0)) {
			try { fileName = name.getBytes("US-ASCII"); }
			catch (UnsupportedEncodingException e) {
				// Should not happen.
				throw new RuntimeException(e);
			}
		} else {
			fileName = PETSCII.fromString(name, alt);
		}
	}
	
	public String getCommentString() {
		try { return new String(comment, "US-ASCII"); }
		catch (UnsupportedEncodingException e) {
			// Should not happen.
			throw new RuntimeException(e);
		}
	}
	
	public void setCommentString(String s) {
		try { comment = s.getBytes("US-ASCII"); }
		catch (UnsupportedEncodingException e) {
			// Should not happen.
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return toString(false, false);
	}
	
	public String toString(boolean alt, boolean geos) {
		StringBuffer sb = new StringBuffer();
		sb.append(sectorSize);
		while (sb.codePointCount(0, sb.length()) < 5) sb.append(' ');
		
		sb.append('"');
		sb.append(getFileName(alt, geos));
		sb.append('"');
		while (sb.codePointCount(0, sb.length()) < 23) sb.append(' ');
		
		if ((cbmFileType & CBM_FILE_TYPE_CLOSED) == 0) sb.append('*');
		else if ((cbmFileType & CBM_FILE_TYPE_LOCKED) != 0) sb.append('>');
		else sb.append(' ');
		sb.append(getFileTypeString());
		
		sb.append(' ');
		sb.append(toString(year, 4));
		sb.append('-');
		sb.append(toString(month, 2));
		sb.append('-');
		sb.append(toString(day, 2));
		sb.append(' ');
		sb.append(toString(hour, 2));
		sb.append(':');
		sb.append(toString(minute, 2));
		return sb.toString();
	}
	
	private String toString(int val, int len) {
		String s = Integer.toString(val);
		while (s.length() < len) s = "0" + s;
		return s.substring(s.length() - len);
	}
	
	public void read(DataInput in) throws IOException {
		cbmFileType = in.readUnsignedByte();
		startingTrack = in.readByte();
		startingSector = in.readByte();
		fileName = read(in, 16, (byte)0xA0);
		sideTrack = infoTrack = in.readByte();
		sideSector = infoSector = in.readByte();
		recordLength = fileStructure = in.readUnsignedByte();
		geosFileType = in.readUnsignedByte();
		year = in.readUnsignedByte() + 1900;
		month = in.readUnsignedByte();
		day = in.readUnsignedByte();
		hour = in.readUnsignedByte();
		minute = in.readUnsignedByte();
		sectorSize = Short.reverseBytes(in.readShort()) & 0xFFFF;
		comment = read(in, 0xE0, (byte)0);
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeByte(cbmFileType);
		out.writeByte(startingTrack);
		out.writeByte(startingSector);
		write(out, fileName, 16, (byte)0xA0);
		
		if (geosFileType == 0) {
			out.writeByte(sideTrack);
			out.writeByte(sideSector);
			out.writeByte(recordLength);
		} else {
			out.writeByte(infoTrack);
			out.writeByte(infoSector);
			out.writeByte(fileStructure);
		}
		
		out.writeByte(geosFileType);
		out.writeByte(year - 1900);
		out.writeByte(month);
		out.writeByte(day);
		out.writeByte(hour);
		out.writeByte(minute);
		out.writeShort(Short.reverseBytes((short)sectorSize));
		write(out, comment, 0xE0, (byte)0);
	}
	
	private static byte[] read(DataInput in, int len, byte fill) throws IOException {
		byte[] buf = new byte[len]; in.readFully(buf);
		while (len > 0 && buf[len-1] == fill) len--;
		byte[] trimmed = new byte[len];
		while (len > 0) { len--; trimmed[len] = buf[len]; }
		return trimmed;
	}
	
	private static void write(DataOutput out, byte[] data, int len, byte fill) throws IOException {
		for (int i = 0; i < data.length && i < len; i++) out.writeByte(data[i]);
		for (int i = data.length; i < len; i++) out.writeByte(fill);
	}
}
