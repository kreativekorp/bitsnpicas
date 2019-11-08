package com.kreative.bitsnpicas.geos;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public boolean isMega() {
		byte[] r54 = vlirData.get(54);
		if (r54 != null && r54.length > 8) {
			int bo = (r54[6] & 0xFF) | ((r54[7] & 0xFF) << 8);
			if (bo >= r54.length) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isUTF8() {
		byte[] r126 = vlirData.get(126);
		return (r126 != null && r126.length > 0);
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
			int index = infoBlock.getByte(a) & 0x3F;
			if (index == 0) break;
			infoBlock.setByte(a, (byte)(index | fontID));
			infoBlock.setByte(a+1, (byte)(fontID >> 8));
		}
	}
	
	public List<Integer> getFontStrikes() {
		List<Integer> indices = new ArrayList<Integer>();
		for (int a = 0x82, i = 0; i < 15; i++, a += 2) {
			int index = infoBlock.getByte(a) & 0x3F;
			if (index == 0) break;
			indices.add(index);
		}
		return indices;
	}
	
	public GEOSFontStrike getFontStrike(int index) {
		if (index >= 0 && index < vlirData.size()) {
			byte[] data = vlirData.get(index);
			if (data != null && data.length > 8) {
				return new GEOSFontStrike(data);
			}
		}
		return null;
	}
	
	public GEOSFontStrike getFontStrike(int record, int sector) {
		byte[] r126 = vlirData.get(126);
		if (r126 != null) {
			for (int i = 0; i + 4 <= r126.length; i += 4) {
				if ((r126[i] & 0xFF) == record && (r126[i+1] & 0xFF) == sector) {
					int length = (r126[i+2] & 0xFF) | ((r126[i+3] & 0xFF) << 8);
					byte[] data = vlirData.get(record);
					if (data != null && data.length >= (sector * 254 + length)) {
						return new GEOSFontStrike(data, sector * 254, length);
					}
				}
			}
		}
		return null;
	}
	
	public void removeFontStrike(int index) {
		if (index >= 0 && index < vlirData.size()) {
			vlirData.set(index, new byte[0]);
		}
	}
	
	public void setFontStrike(int index, GEOSFontStrike gfs) {
		if (index >= 0 && index < vlirData.size()) {
			vlirData.set(index, gfs.write());
		}
	}
	
	public List<Integer> getFontPointSizes() {
		Set<Integer> exclusions = new HashSet<Integer>();
		if (isMega()) {
			for (int i = 49; i <= 54; i++) {
				exclusions.add(i);
			}
		}
		byte[] r126 = vlirData.get(126);
		if (r126 != null && r126.length > 0) {
			exclusions.add(126);
			for (int i = 0; i + 4 <= r126.length; i += 4) {
				exclusions.add(r126[i] & 0xFF);
			}
		}
		List<Integer> pointSizes = new ArrayList<Integer>();
		for (int index = 0; index < vlirData.size(); index++) {
			if (!exclusions.contains(index)) {
				byte[] data = vlirData.get(index);
				if (data.length > 8) pointSizes.add(index);
			}
		}
		return pointSizes;
	}
	
	public GEOSFontPointSize getFontPointSize(int pointSize) {
		GEOSFontPointSize f = new GEOSFontPointSize(pointSize);
		if (pointSize >= 48 && pointSize <= 54 && isMega()) {
			f.megaStrikes = new GEOSFontStrike[6];
			f.megaStrikes[0] = getFontStrike(48);
			f.megaStrikes[1] = getFontStrike(49);
			f.megaStrikes[2] = getFontStrike(50);
			f.megaStrikes[3] = getFontStrike(51);
			f.megaStrikes[4] = getFontStrike(52);
			f.megaStrikes[5] = getFontStrike(53);
			f.megaStrikeIndex = getFontStrike(54);
			f.utf8Map = f.megaStrikeIndex.utf8Map;
		} else {
			f.strike = getFontStrike(pointSize);
			if (f.strike == null) return null;
			f.utf8Map = f.strike.utf8Map;
		}
		if (f.utf8Map != null) {
			f.utf8Strikes = new HashMap<UTF8StrikeEntry, GEOSFontStrike>();
			for (UTF8StrikeEntry e : f.utf8Map.lowEntries) {
				if (e != null) {
					f.utf8Strikes.put(e, getFontStrike(e.recordIndex, e.sectorIndex));
				}
			}
			for (UTF8StrikeMap.SubMap sm : f.utf8Map.highEntries) {
				if (sm != null) {
					for (UTF8StrikeEntry e : sm.entries) {
						if (e != null) {
							f.utf8Strikes.put(e, getFontStrike(e.recordIndex, e.sectorIndex));
						}
					}
				}
			}
			for (UTF8StrikeMap.AstralMap am : f.utf8Map.astralEntries) {
				if (am != null) {
					for (UTF8StrikeMap.SubMap sm : am.entries) {
						if (sm != null) {
							for (UTF8StrikeEntry e : sm.entries) {
								if (e != null) {
									f.utf8Strikes.put(e, getFontStrike(e.recordIndex, e.sectorIndex));
								}
							}
						}
					}
				}
			}
		}
		return f;
	}
	
	public void setFontPointSize(int pointSize, GEOSFontPointSize f) {
		if (f.isUTF8()) {
			Map<UTF8StrikeEntry,UTF8StrikeEntry> remap = new HashMap<UTF8StrikeEntry,UTF8StrikeEntry>();
			UTF8StrikeIndex si = getUTF8StrikeIndex();
			if (si == null) si = new UTF8StrikeIndex();
			int recordLength = 0;
			int recordIndex = nextRecordIndex();
			ByteArrayOutputStream recordOut = new ByteArrayOutputStream();
			for (UTF8StrikeEntry e : f.utf8Map.entryList()) {
				byte[] data = f.utf8Strikes.get(e).write();
				int nextSector = ((recordLength + 253) / 254);
				int nextStart = nextSector * 254;
				if (nextStart + data.length > 65536) {
					vlirData.set(recordIndex, recordOut.toByteArray());
					recordLength = 0;
					recordIndex = nextRecordIndex();
					recordOut = new ByteArrayOutputStream();
					nextSector = 0;
					nextStart = 0;
				}
				remap.put(e, new UTF8StrikeEntry(recordIndex, nextSector, data.length));
				si.add(new UTF8StrikeEntry(recordIndex, nextSector, data.length));
				while (recordLength < nextStart) {
					recordOut.write(0);
					recordLength++;
				}
				for (byte b : data) {
					recordOut.write(b);
					recordLength++;
				}
			}
			if (recordLength > 0) {
				vlirData.set(recordIndex, recordOut.toByteArray());
			}
			setUTF8StrikeIndex(si);
			f.remap(remap);
		}
		if (f.isMega()) {
			setFontStrike(48, f.megaStrikes[0]);
			setFontStrike(49, f.megaStrikes[1]);
			setFontStrike(50, f.megaStrikes[2]);
			setFontStrike(51, f.megaStrikes[3]);
			setFontStrike(52, f.megaStrikes[4]);
			setFontStrike(53, f.megaStrikes[5]);
			setFontStrike(54, f.megaStrikeIndex);
		} else {
			setFontStrike(pointSize, f.strike);
		}
	}
	
	public int nextRecordIndex() {
		int nextID = 125;
		while (nextID > 0) {
			byte[] data = vlirData.get(nextID);
			if (data == null || data.length == 0) break;
			nextID--;
		}
		return nextID;
	}
	
	public UTF8StrikeIndex getUTF8StrikeIndex() {
		byte[] r126 = vlirData.get(126);
		if (r126 != null && r126.length > 0) {
			UTF8StrikeIndex index = new UTF8StrikeIndex();
			index.read(r126);
			return index;
		}
		return null;
	}
	
	public void removeUTF8StrikeIndex() {
		vlirData.set(126, new byte[0]);
	}
	
	public void setUTF8StrikeIndex(UTF8StrikeIndex index) {
		vlirData.set(126, index.write());
	}
	
	public void recalculate() {
		Set<Integer> pointSizeExclusions = new HashSet<Integer>();
		if (isMega()) {
			for (int i = 49; i <= 54; i++) {
				pointSizeExclusions.add(i);
			}
		}
		Set<Integer> recordExclusions = new HashSet<Integer>();
		byte[] r126 = vlirData.get(126);
		if (r126 != null && r126.length > 0) {
			recordExclusions.add(126);
			for (int i = 0; i + 4 <= r126.length; i += 4) {
				recordExclusions.add(r126[i] & 0xFF);
			}
		}
		List<Integer> pointSizes = new ArrayList<Integer>();
		List<Integer> indices = new ArrayList<Integer>();
		List<Integer> lengths = new ArrayList<Integer>();
		for (int index = 0; index < vlirData.size(); index++) {
			if (!recordExclusions.contains(index)) {
				byte[] data = vlirData.get(index);
				if (data.length > 8) {
					if (!pointSizeExclusions.contains(index)) {
						pointSizes.add(index);
					}
					indices.add(index);
					lengths.add(data.length);
				}
			}
		}
		updateDescription(pointSizes);
		setFontStrikes(indices);
		setFontRecordLengths(lengths);
		super.recalculate();
	}
	
	private void updateDescription(List<Integer> pointSizes) {
		StringBuffer sb = new StringBuffer();
		sb.append("Available in ");
		int count = pointSizes.size();
		for (int pointSize : pointSizes) {
			sb.append(pointSize); count--;
			if (count > 1) sb.append(", ");
			if (count == 1) sb.append(" and ");
		}
		sb.append(" point.");
		String d = infoBlock.getDescriptionString();
		if (d.trim().length() == 0) d = sb.toString();
		else d = d.replaceAll("Available in [^.]+ point.", sb.toString());
		infoBlock.setDescriptionString(d);
	}
	
	private void setFontStrikes(List<Integer> indices) {
		int fontID = getFontID() << 6;
		int a = 0x82, i = 0, n = indices.size();
		while (i < n && i < 15) {
			int index = indices.get(i) & 0x3F;
			infoBlock.setByte(a, (byte)(index | fontID));
			infoBlock.setByte(a+1, (byte)(fontID >> 8));
			i++; a += 2;
		}
		while (i < 15) {
			infoBlock.setByte(a, (byte)0);
			infoBlock.setByte(a+1, (byte)0);
			i++; a += 2;
		}
	}
	
	private void setFontRecordLengths(List<Integer> lengths) {
		int a = 0x61, i = 0, n = lengths.size();
		while (i < n && i < 15) {
			int length = lengths.get(i);
			infoBlock.setByte(a, (byte)length);
			infoBlock.setByte(a+1, (byte)(length >> 8));
			i++; a += 2;
		}
		while (i < 15) {
			infoBlock.setByte(a, (byte)0);
			infoBlock.setByte(a+1, (byte)0);
			i++; a += 2;
		}
	}
}
