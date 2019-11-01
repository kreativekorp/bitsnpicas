package com.kreative.bitsnpicas.geos;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GEOSFontFile extends ConvertFile {
	public GEOSFontFile() {
		super(
			CBM_FILE_TYPE_CLOSED | CBM_FILE_TYPE_USR,
			GEOS_FILE_TYPE_FONT, FILE_STRUCTURE_VLIR
		);
		infoBlock.iconBitmap = GEOSIcons.fontIcon();
		for (int i = 0; i < 127; i++) {
			vlirData.add(new byte[0]);
		}
	}
	
	public GEOSFontFile(DataInput in) throws IOException {
		super();
		read(in);
	}
	
	public boolean isValid() {
		return (
			directoryBlock != null &&
			directoryBlock.geosFileType == GEOS_FILE_TYPE_FONT &&
			directoryBlock.fileStructure == FILE_STRUCTURE_VLIR &&
			infoBlock != null &&
			infoBlock.geosFileType == GEOS_FILE_TYPE_FONT &&
			infoBlock.fileStructure == FILE_STRUCTURE_VLIR &&
			recordBlock != null &&
			vlirData != null
		);
	}
	
	public String getFontName() {
		return directoryBlock.getFileName(true, true);
	}
	
	public void setFontName(String name) {
		directoryBlock.setFileName(name, true, true);
	}
	
	public String getClassTextString() {
		return infoBlock.getClassTextString();
	}
	
	public void setClassTextString(String s) {
		infoBlock.setClassTextString(s);
	}
	
	public String getDescriptionString() {
		return infoBlock.getDescriptionString();
	}
	
	public void setDescriptionString(String s) {
		infoBlock.setDescriptionString(s);
	}
	
	public int getFontID() {
		return (
			(infoBlock.getByte(0x80) & 0xFF) |
			((infoBlock.getByte(0x81) & 0xFF) << 8)
		);
	}
	
	public void setFontID(int fontID) {
		infoBlock.setByte(0x80, (byte)fontID);
		infoBlock.setByte(0x81, (byte)(fontID >> 8));
		fontID <<= 6;
		for (int a = 0x82, i = 0; i < 15; i++, a += 2) {
			int pointSize = infoBlock.getByte(a) & 0x3F;
			if (pointSize == 0) break;
			infoBlock.setByte(a, (byte)(pointSize | fontID));
			infoBlock.setByte(a+1, (byte)(fontID >> 8));
		}
	}
	
	public List<Integer> getFontPointSizes() {
		List<Integer> pointSizes = new ArrayList<Integer>();
		for (int a = 0x82, i = 0; i < 15; i++, a += 2) {
			int pointSize = infoBlock.getByte(a) & 0x3F;
			if (pointSize == 0) break;
			pointSizes.add(pointSize);
		}
		return pointSizes;
	}
	
	public GEOSFontStrike getFontStrike(int pointSize) {
		if (pointSize >= 0 && pointSize < vlirData.size()) {
			byte[] data = vlirData.get(pointSize);
			if (data != null && data.length > 8) {
				return new GEOSFontStrike(data);
			}
		}
		return null;
	}
	
	public void removeFontStrike(int pointSize) {
		if (pointSize >= 0 && pointSize < vlirData.size()) {
			vlirData.set(pointSize, new byte[0]);
		}
	}
	
	public void setFontStrike(int pointSize, GEOSFontStrike gfs) {
		if (pointSize >= 0 && pointSize < vlirData.size()) {
			vlirData.set(pointSize, gfs.write());
		}
	}
	
	public void recalculate() {
		List<Integer> pointSizes = new ArrayList<Integer>();
		List<Integer> recordLengths = new ArrayList<Integer>();
		for (int fontSize = 0; fontSize < vlirData.size(); fontSize++) {
			byte[] data = vlirData.get(fontSize);
			if (data.length > 8) {
				pointSizes.add(fontSize);
				recordLengths.add(data.length);
			}
		}
		updateDescription(pointSizes);
		setFontPointSizes(pointSizes);
		setFontRecordLengths(recordLengths);
		super.recalculate();
	}
	
	private void updateDescription(List<Integer> pointSizes) {
		StringBuffer sb = new StringBuffer();
		sb.append("Available in ");
		int count = pointSizes.size();
		for (int fontSize : pointSizes) {
			sb.append(fontSize); count--;
			if (count > 1) sb.append(", ");
			if (count == 1) sb.append(" and ");
		}
		sb.append(" point.");
		String d = infoBlock.getDescriptionString();
		if (d.trim().length() == 0) d = sb.toString();
		else d = d.replaceAll("Available in [^.]+ point.", sb.toString());
		infoBlock.setDescriptionString(d);
	}
	
	private void setFontPointSizes(List<Integer> pointSizes) {
		int fontID = getFontID() << 6;
		int a = 0x82, i = 0, n = pointSizes.size();
		while (i < n && i < 15) {
			int pointSize = pointSizes.get(i) & 0x3F;
			infoBlock.setByte(a, (byte)(pointSize | fontID));
			infoBlock.setByte(a+1, (byte)(fontID >> 8));
			i++; a += 2;
		}
		while (i < 15) {
			infoBlock.setByte(a, (byte)0);
			infoBlock.setByte(a+1, (byte)0);
			i++; a += 2;
		}
	}
	
	private void setFontRecordLengths(List<Integer> recordLengths) {
		int a = 0x61, i = 0, n = recordLengths.size();
		while (i < n && i < 15) {
			int recordLength = recordLengths.get(i);
			infoBlock.setByte(a, (byte)recordLength);
			infoBlock.setByte(a+1, (byte)(recordLength >> 8));
			i++; a += 2;
		}
		while (i < 15) {
			infoBlock.setByte(a, (byte)0);
			infoBlock.setByte(a+1, (byte)0);
			i++; a += 2;
		}
	}
}
