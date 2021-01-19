package com.kreative.bitsnpicas.truetype;

import java.io.UnsupportedEncodingException;

public class NameTableEntry implements Comparable<NameTableEntry> {
	public static final int NAME_ID_COPYRIGHT_NOTICE = 0;
	public static final int NAME_ID_FONT_FAMILY = 1;
	public static final int NAME_ID_FONT_SUBFAMILY = 2;
	public static final int NAME_ID_UNIQUE_SUBFAMILY_ID = 3;
	public static final int NAME_ID_FULL_NAME = 4;
	public static final int NAME_ID_VERSION = 5;
	public static final int NAME_ID_POSTSCRIPT_NAME = 6;
	public static final int NAME_ID_TRADEMARK_NOTICE = 7;
	public static final int NAME_ID_MANUFACTURER_NAME = 8;
	public static final int NAME_ID_DESIGNER = 9;
	public static final int NAME_ID_DESCRIPTION = 10;
	public static final int NAME_ID_FONT_VENDOR_URL = 11;
	public static final int NAME_ID_FONT_DESIGNER_URL = 12;
	public static final int NAME_ID_LICENSE_DESCRIPTION = 13;
	public static final int NAME_ID_LICENSE_INFORMATION_URL = 14;
	public static final int NAME_ID_PREFERRED_FAMILY = 16;
	public static final int NAME_ID_PREFERRED_SUBFAMILY = 17;
	public static final int NAME_ID_COMPATIBLE_FULL = 18;
	public static final int NAME_ID_SAMPLE_TEXT = 19;
	
	public int platformID;
	public int platformSpecificID;
	public int languageID;
	public int nameID;
	public byte[] nameData;
	public int padding;
	
	public static NameTableEntry forUnicode(int nameID, String name) {
		NameTableEntry e = new NameTableEntry();
		e.platformID = PlatformConstants.PLATFORM_ID_UNICODE;
		e.platformSpecificID = PlatformConstants.PLATFORM_SPECIFIC_ID_UNICODE_2_0;
		e.languageID = 0;
		e.nameID = nameID;
		e.setNameString(name);
		return e;
	}
	
	public static NameTableEntry forMacintosh(int nameID, String name) {
		NameTableEntry e = new NameTableEntry();
		e.platformID = PlatformConstants.PLATFORM_ID_MACINTOSH;
		e.platformSpecificID = PlatformConstants.PLATFORM_SPECIFIC_ID_MACINTOSH_ROMAN;
		e.languageID = PlatformConstants.LANGUAGE_ID_MACINTOSH_ENGLISH;
		e.nameID = nameID;
		e.setNameString(name);
		return e;
	}
	
	public static NameTableEntry forWindows(int nameID, String name) {
		NameTableEntry e = new NameTableEntry();
		e.platformID = PlatformConstants.PLATFORM_ID_WINDOWS;
		e.platformSpecificID = PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_16;
		e.languageID = PlatformConstants.LANGUAGE_ID_WINDOWS_ENGLISH;
		e.nameID = nameID;
		e.setNameString(name);
		return e;
	}
	
	@Override
	public int compareTo(NameTableEntry other) {
		if (this.platformID != other.platformID) return this.platformID - other.platformID;
		if (this.platformSpecificID != other.platformSpecificID) return this.platformSpecificID - other.platformSpecificID;
		if (this.languageID != other.languageID) return this.languageID - other.languageID;
		if (this.nameID != other.nameID) return this.nameID - other.nameID;
		return 0;
	}
	
	public String getNameString() {
		try {
			switch (platformID) {
			case PlatformConstants.PLATFORM_ID_UNICODE:
				return new String(nameData, "UTF-16BE");
			case PlatformConstants.PLATFORM_ID_MACINTOSH:
				switch (platformSpecificID) {
				case PlatformConstants.PLATFORM_SPECIFIC_ID_MACINTOSH_ROMAN:
					try {
						return new String(nameData, "MacRoman");
					} catch (UnsupportedEncodingException uee2) {
						return new String(nameData, "US-ASCII");
					}
				default:
					throw new IllegalStateException("I have no idea how to decode this.");
				}
			case PlatformConstants.PLATFORM_ID_ISO_10646:
				return new String(nameData, "UTF-16BE");
			case PlatformConstants.PLATFORM_ID_WINDOWS:
			case PlatformConstants.PLATFORM_ID_WINDOWS_UNICODE:
				switch (platformSpecificID) {
				case PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_16:
					return new String(nameData, "UTF-16BE");
				case PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_32:
					return new String(nameData, "UTF-32BE");
				default:
					throw new IllegalStateException("I have no idea how to decode this.");
				}
			default:
				throw new IllegalStateException("I have no idea how to decode this.");
			}
		} catch (UnsupportedEncodingException uee) {
			throw new IllegalStateException("Java is stupid.");
		}
	}
	
	public void setNameString(String name) {
		try {
			switch (platformID) {
			case PlatformConstants.PLATFORM_ID_UNICODE:
				nameData = name.getBytes("UTF-16BE");
				return;
			case PlatformConstants.PLATFORM_ID_MACINTOSH:
				switch (platformSpecificID) {
				case PlatformConstants.PLATFORM_SPECIFIC_ID_MACINTOSH_ROMAN:
					try {
						nameData = name.getBytes("MacRoman");
						return;
					} catch (UnsupportedEncodingException uee2) {
						nameData = name.getBytes("US-ASCII");
						return;
					}
				default:
					throw new IllegalStateException("I have no idea how to encode this.");
				}
			case PlatformConstants.PLATFORM_ID_ISO_10646:
				nameData = name.getBytes("UTF-16BE");
				return;
			case PlatformConstants.PLATFORM_ID_WINDOWS:
			case PlatformConstants.PLATFORM_ID_WINDOWS_UNICODE:
				switch (platformSpecificID) {
				case PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_16:
					nameData = name.getBytes("UTF-16BE");
					return;
				case PlatformConstants.PLATFORM_SPECIFIC_ID_WINDOWS_UNICODE_32:
					nameData = name.getBytes("UTF-32BE");
					return;
				default:
					throw new IllegalStateException("I have no idea how to encode this.");
				}
			default:
				throw new IllegalStateException("I have no idea how to encode this.");
			}
		} catch (UnsupportedEncodingException uee) {
			throw new IllegalStateException("Java is stupid.");
		}
	}
}
