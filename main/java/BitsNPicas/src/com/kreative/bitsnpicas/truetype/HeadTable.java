package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class HeadTable extends TrueTypeTable {
	public static final int VERSION_DEFAULT = 0x00010000;
	public static final int MAGIC_NUMBER = 0x5F0F3CF5;
	public static final int FLAGS_Y_VALUE_OF_ZERO_SPECIFIES_BASELINE    = 0x0001;
	public static final int FLAGS_MINIMUM_X_VALUE_IS_LEFT_SIDE_BEARING  = 0x0002;
	public static final int FLAGS_SCALED_AND_ACTUAL_POINT_SIZE_DIFFER   = 0x0004;
	public static final int FLAGS_USE_INTEGER_SCALING                   = 0x0008;
	public static final int FLAGS_USED_BY_MICROSOFT                     = 0x0010;
	public static final int FLAGS_X_VALUE_OF_ZERO_SPECIFIES_BASELINE    = 0x0020;
	public static final int FLAGS_MUST_BE_ZERO                          = 0x0040;
	public static final int FLAGS_REQUIRES_LAYOUT_FOR_CORRECT_RENDERING = 0x0080;
	public static final int FLAGS_METAMORPHOSIS_EFFECT_BY_DEFAULT       = 0x0100;
	public static final int FLAGS_CONTAINS_RIGHT_TO_LEFT_GLYPHS         = 0x0200;
	public static final int FLAGS_CONTAINS_INDIC_STYLE_REARRANGEMENT    = 0x0400;
	public static final int FLAGS_DEFINED_BY_ADOBE_1                    = 0x0800;
	public static final int FLAGS_DEFINED_BY_ADOBE_2                    = 0x1000;
	public static final long DATE_EPOCH = new GregorianCalendar(1904, Calendar.JANUARY, 1, 0, 0, 0).getTimeInMillis();
	public static final int MAC_STYLE_PLAIN       = 0x00;
	public static final int MAC_STYLE_BOLD        = 0x01;
	public static final int MAC_STYLE_ITALIC      = 0x02;
	public static final int MAC_STYLE_BOLD_ITALIC = 0x03;
	public static final int MAC_STYLE_UNDERLINE   = 0x04;
	public static final int MAC_STYLE_OUTLINE     = 0x08;
	public static final int MAC_STYLE_SHADOW      = 0x10;
	public static final int MAC_STYLE_CONDENSED   = 0x20;
	public static final int MAC_STYLE_EXTENDED    = 0x40;
	public static final int MAC_STYLE_GROUPED     = 0x80;
	public static final int FONT_DIRECTION_HINT_MIXED = 0;
	public static final int FONT_DIRECTION_HINT_ONLY_LTR = 1;
	public static final int FONT_DIRECTION_HINT_LTR_WITH_NEUTRAL = 2;
	public static final int FONT_DIRECTION_HINT_ONLY_RTL = -1;
	public static final int FONT_DIRECTION_HINT_RTL_WITH_NEUTRAL = -2;
	public static final int INDEX_TO_LOC_FORMAT_SHORT = 0;
	public static final int INDEX_TO_LOC_FORMAT_LONG = 1;
	public static final int GLYPH_DATA_FORMAT_DEFAULT = 0;
	
	public int version = VERSION_DEFAULT;
	public int fontRevision = 0;
	public int checkSum = 0;
	public int magicNumber = MAGIC_NUMBER;
	public int flags = 0;
	public int unitsPerEm = 0;
	public long dateCreated = 0;
	public long dateModified = 0;
	public int xMin = 0;
	public int yMin = 0;
	public int xMax = 0;
	public int yMax = 0;
	public int macStyle = MAC_STYLE_PLAIN;
	public int lowestRecPPEM = 0;
	public int fontDirectionHint = FONT_DIRECTION_HINT_MIXED;
	public int indexToLocFormat = INDEX_TO_LOC_FORMAT_SHORT;
	public int glyphDataFormat = GLYPH_DATA_FORMAT_DEFAULT;
	
	@Override
	public String tableName() {
		return "head";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	public double getFontRevisionDouble() {
		return fontRevision / 65536.0;
	}
	
	public void setFontRevisionDouble(double revision) {
		fontRevision = (int)Math.round(revision * 65536.0);
	}
	
	public GregorianCalendar getDateCreatedCalendar() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(DATE_EPOCH + (dateCreated * 1000L));
		return cal;
	}
	
	public void setDateCreatedCalendar(Calendar cal) {
		dateCreated = (cal.getTimeInMillis() - DATE_EPOCH) / 1000L;
	}
	
	public GregorianCalendar getDateModifiedCalendar() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(DATE_EPOCH + (dateModified * 1000L));
		return cal;
	}
	
	public void setDateModifiedCalendar(Calendar cal) {
		dateModified = (cal.getTimeInMillis() - DATE_EPOCH) / 1000L;
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		out.writeInt(version);
		out.writeInt(fontRevision);
		out.writeInt(checkSum);
		out.writeInt(magicNumber);
		out.writeShort(flags);
		out.writeShort(unitsPerEm);
		out.writeLong(dateCreated);
		out.writeLong(dateModified);
		out.writeShort(xMin);
		out.writeShort(yMin);
		out.writeShort(xMax);
		out.writeShort(yMax);
		out.writeShort(macStyle);
		out.writeShort(lowestRecPPEM);
		out.writeShort(fontDirectionHint);
		out.writeShort(indexToLocFormat);
		out.writeShort(glyphDataFormat);
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		version = in.readInt();
		fontRevision = in.readInt();
		checkSum = in.readInt();
		magicNumber = in.readInt();
		flags = in.readUnsignedShort();
		unitsPerEm = in.readUnsignedShort();
		dateCreated = in.readLong();
		dateModified = in.readLong();
		xMin = in.readShort();
		yMin = in.readShort();
		xMax = in.readShort();
		yMax = in.readShort();
		macStyle = in.readUnsignedShort();
		lowestRecPPEM = in.readUnsignedShort();
		fontDirectionHint = in.readShort();
		indexToLocFormat = in.readShort();
		glyphDataFormat = in.readShort();
	}
}
