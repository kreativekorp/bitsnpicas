package com.kreative.bitsnpicas.truetype;

public class CmapTableEntry implements Comparable<CmapTableEntry> {
	public int platformID;
	public int platformSpecificID;
	public CmapSubtable subtable;
	
	public static CmapTableEntry forUnicode(CmapSubtable subtable) {
		CmapTableEntry e = new CmapTableEntry();
		e.platformID = PlatformConstants.PLATFORM_ID_UNICODE;
		e.platformSpecificID = PlatformConstants.PLATFORM_SPECIFIC_ID_UNICODE_2_0;
		e.subtable = subtable;
		return e;
	}
	
	public static CmapTableEntry forMacintosh(CmapSubtable subtable) {
		CmapTableEntry e = new CmapTableEntry();
		e.platformID = PlatformConstants.PLATFORM_ID_MACINTOSH;
		e.platformSpecificID = PlatformConstants.PLATFORM_SPECIFIC_ID_MACINTOSH_ROMAN;
		e.subtable = subtable;
		return e;
	}
	
	public static CmapTableEntry forWindowsUnicode16(CmapSubtable subtable) {
		CmapTableEntry e = new CmapTableEntry();
		e.platformID = PlatformConstants.PLATFORM_ID_WINDOWS;
		e.platformSpecificID = PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_16;
		e.subtable = subtable;
		return e;
	}
	
	public static CmapTableEntry forWindowsUnicode32(CmapSubtable subtable) {
		CmapTableEntry e = new CmapTableEntry();
		e.platformID = PlatformConstants.PLATFORM_ID_WINDOWS;
		e.platformSpecificID = PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_32;
		e.subtable = subtable;
		return e;
	}
	
	@Override
	public int compareTo(CmapTableEntry other) {
		if (this.platformID != other.platformID) return this.platformID - other.platformID;
		if (this.platformSpecificID != other.platformSpecificID) return this.platformSpecificID - other.platformSpecificID;
		return 0;
	}
}
