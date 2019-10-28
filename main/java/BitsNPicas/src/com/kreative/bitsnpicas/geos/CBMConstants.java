package com.kreative.bitsnpicas.geos;

public interface CBMConstants {
	public static final int CBM_FILE_TYPE_DEL = 0x00;
	public static final int CBM_FILE_TYPE_SEQ = 0x01;
	public static final int CBM_FILE_TYPE_PRG = 0x02;
	public static final int CBM_FILE_TYPE_USR = 0x03;
	public static final int CBM_FILE_TYPE_REL = 0x04;
	public static final int CBM_FILE_TYPE_MASK = 0x0F;
	public static final int CBM_FILE_TYPE_LOCKED = 0x40;
	public static final int CBM_FILE_TYPE_CLOSED = 0x80;
	
	public static final int FILE_STRUCTURE_SEQ = 0x00;
	public static final int FILE_STRUCTURE_VLIR = 0x01;
	
	public static final int GEOS_FILE_TYPE_NONE = 0x00;
	public static final int GEOS_FILE_TYPE_BASIC = 0x01;
	public static final int GEOS_FILE_TYPE_ASSEMBLER = 0x02;
	public static final int GEOS_FILE_TYPE_DATA = 0x03;
	public static final int GEOS_FILE_TYPE_SYSTEM = 0x04;
	public static final int GEOS_FILE_TYPE_DESK_ACCESSORY = 0x05;
	public static final int GEOS_FILE_TYPE_APPLICATION = 0x06;
	public static final int GEOS_FILE_TYPE_DOCUMENT = 0x07;
	public static final int GEOS_FILE_TYPE_FONT = 0x08;
	public static final int GEOS_FILE_TYPE_PRINTER_DRIVER = 0x09;
	public static final int GEOS_FILE_TYPE_INPUT_DRIVER = 0x0A;
	public static final int GEOS_FILE_TYPE_DISK_DRIVER = 0x0B;
	public static final int GEOS_FILE_TYPE_BOOT = 0x0C;
	public static final int GEOS_FILE_TYPE_TEMPORARY = 0x0D;
	public static final int GEOS_FILE_TYPE_AUTO_EXECUTE = 0x0E;
}
