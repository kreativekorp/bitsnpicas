package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Collection;

public class Os2Table extends TrueTypeTable {
	public static final int VERSION_TRUETYPE_1_5  = 0; // 68 bytes
	public static final int VERSION_TRUETYPE_1_66 = 1; // 78 bytes
	public static final int VERSION_OPENTYPE_1_1  = 2; // 86 bytes
	public static final int VERSION_OPENTYPE_1_4  = 3; // 96 bytes
	public static final int VERSION_OPENTYPE_1_5  = 4; // 96 bytes
	public static final int VERSION_OPENTYPE_1_7  = 5; // 100 bytes
	public static final int VERSION_MAX = 5;
	public static final int WEIGHT_CLASS_ULTRA_LIGHT = 100;
	public static final int WEIGHT_CLASS_EXTRA_LIGHT = 200;
	public static final int WEIGHT_CLASS_LIGHT = 300;
	public static final int WEIGHT_CLASS_SEMI_LIGHT = 400;
	public static final int WEIGHT_CLASS_MEDIUM = 500;
	public static final int WEIGHT_CLASS_SEMI_BOLD = 600;
	public static final int WEIGHT_CLASS_BOLD = 700;
	public static final int WEIGHT_CLASS_EXTRA_BOLD = 800;
	public static final int WEIGHT_CLASS_ULTRA_BOLD = 900;
	public static final int WIDTH_CLASS_ULTRA_CONDENSED = 1;
	public static final int WIDTH_CLASS_EXTRA_CONDENSED = 2;
	public static final int WIDTH_CLASS_CONDENSED = 3;
	public static final int WIDTH_CLASS_SEMI_CONDENSED = 4;
	public static final int WIDTH_CLASS_MEDIUM = 5;
	public static final int WIDTH_CLASS_SEMI_EXPANDED = 6;
	public static final int WIDTH_CLASS_EXPANDED = 7;
	public static final int WIDTH_CLASS_EXTRA_EXPANDED = 8;
	public static final int WIDTH_CLASS_ULTRA_EXPANDED = 9;
	public static final int FLAGS_INSTALLABLE_EMBEDDING = 0;
	public static final int FLAGS_RESTRICTED_LICENSE_EMBEDDING = 2;
	public static final int FLAGS_PREVIEW_AND_PRINT_EMBEDDING = 4;
	public static final int FLAGS_EDITABLE_EMBEDDING = 8;
	public static final int FLAGS_NO_SUBSETTING = 0x0100;
	public static final int FLAGS_BITMAP_EMBEDDING_ONLY = 0x0200;
	public static final int FAMILY_CLASS_NO_CLASSIFICATION = 0;
	public static final int FAMILY_CLASS_OLDSTYLE_SERIFS = 1;
	public static final int FAMILY_CLASS_TRANSITIONAL_SERIFS = 2;
	public static final int FAMILY_CLASS_MODERN_SERIFS = 3;
	public static final int FAMILY_CLASS_CLARENDON_SERIFS = 4;
	public static final int FAMILY_CLASS_SLAB_SERIFS = 5;
	public static final int FAMILY_CLASS_FREEFORM_SERIFS = 7;
	public static final int FAMILY_CLASS_SANS_SERIF = 8;
	public static final int FAMILY_CLASS_ORNAMENTAL = 9;
	public static final int FAMILY_CLASS_SCRIPT = 10;
	public static final int FAMILY_CLASS_SYMBOL = 12;
	public static final int FAMILY_SUBCLASS_NO_CLASSIFICATION = 0;
	public static final int FAMILY_SUBCLASS_OLDSTYLE_SERIFS_IBM_ROUNDED_LEGIBILITY = 1;
	public static final int FAMILY_SUBCLASS_OLDSTYLE_SERIFS_GARALDE = 2;
	public static final int FAMILY_SUBCLASS_OLDSTYLE_SERIFS_VENETIAN = 3;
	public static final int FAMILY_SUBCLASS_OLDSTYLE_SERIFS_MODIFIED_VENETIAN = 4;
	public static final int FAMILY_SUBCLASS_OLDSTYLE_SERIFS_DUTCH_MODERN = 5;
	public static final int FAMILY_SUBCLASS_OLDSTYLE_SERIFS_DUTCH_TRADITIONAL = 6;
	public static final int FAMILY_SUBCLASS_OLDSTYLE_SERIFS_CONTEMPORARY = 7;
	public static final int FAMILY_SUBCLASS_OLDSTYLE_SERIFS_CALLIGRAPHIC = 8;
	public static final int FAMILY_SUBCLASS_TRANSITIONAL_SERIFS_DIRECT_LINE = 1;
	public static final int FAMILY_SUBCLASS_TRANSITIONAL_SERIFS_SCRIPT = 2;
	public static final int FAMILY_SUBCLASS_MODERN_SERIFS_ITALIAN = 1;
	public static final int FAMILY_SUBCLASS_MODERN_SERIFS_SCRIPT = 2;
	public static final int FAMILY_SUBCLASS_CLARENDON_SERIFS_CLARENDON = 1;
	public static final int FAMILY_SUBCLASS_CLARENDON_SERIFS_MODERN = 2;
	public static final int FAMILY_SUBCLASS_CLARENDON_SERIFS_TRADITIONAL = 3;
	public static final int FAMILY_SUBCLASS_CLARENDON_SERIFS_NEWSPAPER = 4;
	public static final int FAMILY_SUBCLASS_CLARENDON_SERIFS_STUB_SERIF = 5;
	public static final int FAMILY_SUBCLASS_CLARENDON_SERIFS_MONOTONE = 6;
	public static final int FAMILY_SUBCLASS_CLARENDON_SERIFS_TYPEWRITER = 7;
	public static final int FAMILY_SUBCLASS_SLAB_SERIFS_MONOTONE = 1;
	public static final int FAMILY_SUBCLASS_SLAB_SERIFS_HUMANIST = 2;
	public static final int FAMILY_SUBCLASS_SLAB_SERIFS_GEOMETRIC = 3;
	public static final int FAMILY_SUBCLASS_SLAB_SERIFS_SWISS = 4;
	public static final int FAMILY_SUBCLASS_SLAB_SERIFS_TYPEWRITER = 5;
	public static final int FAMILY_SUBCLASS_FREEFORM_SERIFS_MODERN = 1;
	public static final int FAMILY_SUBCLASS_SANS_SERIF_IBM_NEO_GROTESQUE_GOTHIC = 1;
	public static final int FAMILY_SUBCLASS_SANS_SERIF_HUMANIST = 2;
	public static final int FAMILY_SUBCLASS_SANS_SERIF_LOW_X_ROUND_GEOMETRIC = 3;
	public static final int FAMILY_SUBCLASS_SANS_SERIF_HIGH_X_ROUND_GEOMETRIC = 4;
	public static final int FAMILY_SUBCLASS_SANS_SERIF_NEO_GROTESQUE_GOTHIC = 5;
	public static final int FAMILY_SUBCLASS_SANS_SERIF_MODIFIED_NEO_GROTESQUE_GOTHIC = 6;
	public static final int FAMILY_SUBCLASS_SANS_SERIF_TYPEWRITER_GOTHIC = 9;
	public static final int FAMILY_SUBCLASS_SANS_SERIF_MATRIX = 10;
	public static final int FAMILY_SUBCLASS_ORNAMENTAL_ENGRAVER = 1;
	public static final int FAMILY_SUBCLASS_ORNAMENTAL_BLACK_LETTER = 2;
	public static final int FAMILY_SUBCLASS_ORNAMENTAL_DECORATIVE = 3;
	public static final int FAMILY_SUBCLASS_ORNAMENTAL_3D = 4;
	public static final int FAMILY_SUBCLASS_SCRIPT_UNCIAL = 1;
	public static final int FAMILY_SUBCLASS_SCRIPT_BRUSH_JOINED = 2;
	public static final int FAMILY_SUBCLASS_SCRIPT_FORMAL_JOINED = 3;
	public static final int FAMILY_SUBCLASS_SCRIPT_MONOTONE_JOINED = 4;
	public static final int FAMILY_SUBCLASS_SCRIPT_CALLIGRAPHIC = 5;
	public static final int FAMILY_SUBCLASS_SCRIPT_BRUSH_UNJOINED = 6;
	public static final int FAMILY_SUBCLASS_SCRIPT_FORMAL_UNJOINED = 7;
	public static final int FAMILY_SUBCLASS_SCRIPT_MONOTONE_UNJOINED = 8;
	public static final int FAMILY_SUBCLASS_SYMBOL_MIXED_SERIF = 3;
	public static final int FAMILY_SUBCLASS_SYMBOL_OLDSTYLE_SERIF = 6;
	public static final int FAMILY_SUBCLASS_SYMBOL_NEO_GROTESQUE_SANS_SERIF = 7;
	public static final int FAMILY_SUBCLASS_MISCELLANEOUS = 15;
	public static final int PANOSE_FAMILY_TYPE_ANY = 0;
	public static final int PANOSE_FAMILY_TYPE_NO_FIT = 1;
	public static final int PANOSE_FAMILY_TYPE_TEXT_AND_DISPLAY = 2;
	public static final int PANOSE_FAMILY_TYPE_SCRIPT = 3;
	public static final int PANOSE_FAMILY_TYPE_DECORATIVE = 4;
	public static final int PANOSE_FAMILY_TYPE_PICTORIAL = 5;
	public static final int PANOSE_SERIF_STYLE_ANY = 0;
	public static final int PANOSE_SERIF_STYLE_NO_FIT = 1;
	public static final int PANOSE_SERIF_STYLE_COVE = 2;
	public static final int PANOSE_SERIF_STYLE_OBTUSE_COVE = 3;
	public static final int PANOSE_SERIF_STYLE_SQUARE_COVE = 4;
	public static final int PANOSE_SERIF_STYLE_OBTUSE_SQUARE_COVE = 5;
	public static final int PANOSE_SERIF_STYLE_SQUARE = 6;
	public static final int PANOSE_SERIF_STYLE_THIN = 7;
	public static final int PANOSE_SERIF_STYLE_BONE = 8;
	public static final int PANOSE_SERIF_STYLE_EXAGGERATED = 9;
	public static final int PANOSE_SERIF_STYLE_TRIANGLE = 10;
	public static final int PANOSE_SERIF_STYLE_NORMAL_SANS = 11;
	public static final int PANOSE_SERIF_STYLE_OBTUSE_SANS = 12;
	public static final int PANOSE_SERIF_STYLE_PERP_SANS = 13;
	public static final int PANOSE_SERIF_STYLE_FLARED = 14;
	public static final int PANOSE_SERIF_STYLE_ROUNDED = 15;
	public static final int PANOSE_WEIGHT_ANY = 0;
	public static final int PANOSE_WEIGHT_NO_FIT = 1;
	public static final int PANOSE_WEIGHT_VERY_LIGHT = 2;
	public static final int PANOSE_WEIGHT_LIGHT = 3;
	public static final int PANOSE_WEIGHT_THIN = 4;
	public static final int PANOSE_WEIGHT_BOOK = 5;
	public static final int PANOSE_WEIGHT_MEDIUM = 6;
	public static final int PANOSE_WEIGHT_DEMI = 7;
	public static final int PANOSE_WEIGHT_BOLD = 8;
	public static final int PANOSE_WEIGHT_HEAVY = 9;
	public static final int PANOSE_WEIGHT_BLACK = 10;
	public static final int PANOSE_WEIGHT_NORD = 11;
	public static final int PANOSE_PROPORTION_ANY = 0;
	public static final int PANOSE_PROPORTION_NO_FIT = 1;
	public static final int PANOSE_PROPORTION_OLD_STYLE = 2;
	public static final int PANOSE_PROPORTION_MODERN = 3;
	public static final int PANOSE_PROPORTION_EVEN_WIDTH = 4;
	public static final int PANOSE_PROPORTION_EXPANDED = 5;
	public static final int PANOSE_PROPORTION_CONDENSED = 6;
	public static final int PANOSE_PROPORTION_VERY_EXPANDED = 7;
	public static final int PANOSE_PROPORTION_VERY_CONDENSED = 8;
	public static final int PANOSE_PROPORTION_MONOSPACED = 9;
	public static final int PANOSE_CONTRAST_ANY = 0;
	public static final int PANOSE_CONTRAST_NO_FIT = 1;
	public static final int PANOSE_CONTRAST_NONE = 2;
	public static final int PANOSE_CONTRAST_VERY_LOW = 3;
	public static final int PANOSE_CONTRAST_LOW = 4;
	public static final int PANOSE_CONTRAST_MEDIUM_LOW = 5;
	public static final int PANOSE_CONTRAST_MEDIUM = 6;
	public static final int PANOSE_CONTRAST_MEDIUM_HIGH = 7;
	public static final int PANOSE_CONTRAST_HIGH = 8;
	public static final int PANOSE_CONTRAST_VERY_HIGH = 9;
	public static final int PANOSE_STROKE_VARIATION_ANY = 0;
	public static final int PANOSE_STROKE_VARIATION_NO_FIT = 1;
	public static final int PANOSE_STROKE_VARIATION_GRADUAL_DIAGONAL = 2;
	public static final int PANOSE_STROKE_VARIATION_GRADUAL_TRANSITIONAL = 3;
	public static final int PANOSE_STROKE_VARIATION_GRADUAL_VERTICAL = 4;
	public static final int PANOSE_STROKE_VARIATION_GRADUAL_HORIZONTAL = 5;
	public static final int PANOSE_STROKE_VARIATION_RAPID_VERTICAL = 6;
	public static final int PANOSE_STROKE_VARIATION_RAPID_HORIZONTAL = 7;
	public static final int PANOSE_STROKE_VARIATION_INSTANT_VERTICAL = 8;
	public static final int PANOSE_ARM_STYLE_ANY = 0;
	public static final int PANOSE_ARM_STYLE_NO_FIT = 1;
	public static final int PANOSE_ARM_STYLE_STRAIGHT_ARMS_HORIZONTAL = 2;
	public static final int PANOSE_ARM_STYLE_STRAIGHT_ARMS_WEDGE = 3;
	public static final int PANOSE_ARM_STYLE_STRAIGHT_ARMS_VERTICAL = 4;
	public static final int PANOSE_ARM_STYLE_STRAIGHT_ARMS_SINGLE_SERIF = 5;
	public static final int PANOSE_ARM_STYLE_STRAIGHT_ARMS_DOUBLE_SERIF = 6;
	public static final int PANOSE_ARM_STYLE_NON_STRAIGHT_ARMS_HORIZONTAL = 7;
	public static final int PANOSE_ARM_STYLE_NON_STRAIGHT_ARMS_WEDGE = 8;
	public static final int PANOSE_ARM_STYLE_NON_STRAIGHT_ARMS_VERTICAL = 9;
	public static final int PANOSE_ARM_STYLE_NON_STRAIGHT_ARMS_SINGLE_SERIF = 10;
	public static final int PANOSE_ARM_STYLE_NON_STRAIGHT_ARMS_DOUBLE_SERIF = 11;
	public static final int PANOSE_LETTERFORM_ANY = 0;
	public static final int PANOSE_LETTERFORM_NO_FIT = 1;
	public static final int PANOSE_LETTERFORM_NORMAL_CONTACT = 2;
	public static final int PANOSE_LETTERFORM_NORMAL_WEIGHTED = 3;
	public static final int PANOSE_LETTERFORM_NORMAL_BOXED = 4;
	public static final int PANOSE_LETTERFORM_NORMAL_FLATTENED = 5;
	public static final int PANOSE_LETTERFORM_NORMAL_ROUNDED = 6;
	public static final int PANOSE_LETTERFORM_NORMAL_OFF_CENTER = 7;
	public static final int PANOSE_LETTERFORM_NORMAL_SQUARE = 8;
	public static final int PANOSE_LETTERFORM_OBLIQUE_CONTACT = 9;
	public static final int PANOSE_LETTERFORM_OBLIQUE_WEIGHTED = 10;
	public static final int PANOSE_LETTERFORM_OBLIQUE_BOXED = 11;
	public static final int PANOSE_LETTERFORM_OBLIQUE_FLATTENED = 12;
	public static final int PANOSE_LETTERFORM_OBLIQUE_ROUNDED = 13;
	public static final int PANOSE_LETTERFORM_OBLIQUE_OFF_CENTER = 14;
	public static final int PANOSE_LETTERFORM_OBLIQUE_SQUARE = 15;
	public static final int PANOSE_MIDLINE_ANY = 0;
	public static final int PANOSE_MIDLINE_NO_FIT = 1;
	public static final int PANOSE_MIDLINE_STANDARD_TRIMMED = 2;
	public static final int PANOSE_MIDLINE_STANDARD_POINTED = 3;
	public static final int PANOSE_MIDLINE_STANDARD_SERIFED = 4;
	public static final int PANOSE_MIDLINE_HIGH_TRIMMED = 5;
	public static final int PANOSE_MIDLINE_HIGH_POINTED = 6;
	public static final int PANOSE_MIDLINE_HIGH_SERIFED = 7;
	public static final int PANOSE_MIDLINE_CONSTANT_TRIMMED = 8;
	public static final int PANOSE_MIDLINE_CONSTANT_POINTED = 9;
	public static final int PANOSE_MIDLINE_CONSTANT_SERIFED = 10;
	public static final int PANOSE_MIDLINE_LOW_TRIMMED = 11;
	public static final int PANOSE_MIDLINE_LOW_POINTED = 12;
	public static final int PANOSE_MIDLINE_LOW_SERIFED = 13;
	public static final int PANOSE_X_HEIGHT_ANY = 0;
	public static final int PANOSE_X_HEIGHT_NO_FIT = 1;
	public static final int PANOSE_X_HEIGHT_CONSTANT_SMALL = 2;
	public static final int PANOSE_X_HEIGHT_CONSTANT_STANDARD = 3;
	public static final int PANOSE_X_HEIGHT_CONSTANT_LARGE = 4;
	public static final int PANOSE_X_HEIGHT_DUCKING_SMALL = 5;
	public static final int PANOSE_X_HEIGHT_DUCKING_STANDARD = 6;
	public static final int PANOSE_X_HEIGHT_DUCKING_LARGE = 7;
	public static final int VENDOR_ID_KBnP = 0x4B426E50;
	public static final int VENDOR_ID_KrKo = 0x4B724B6F;
	public static final int VENDOR_ID_ckbt = 0x636B6274;
	public static final int FS_SELECTION_ITALIC           = 0x0001;
	public static final int FS_SELECTION_UNDERLINE        = 0x0002;
	public static final int FS_SELECTION_NEGATIVE         = 0x0004;
	public static final int FS_SELECTION_OUTLINE          = 0x0008;
	public static final int FS_SELECTION_STRIKEOUT        = 0x0010;
	public static final int FS_SELECTION_BOLD             = 0x0020;
	public static final int FS_SELECTION_REGULAR          = 0x0040;
	public static final int FS_SELECTION_USE_TYPO_METRICS = 0x0080;
	public static final int FS_SELECTION_WWS              = 0x0100;
	public static final int FS_SELECTION_OBLIQUE          = 0x0200;
	
	// Version 0 (68-byte) begins
	public int version = VERSION_MAX;
	public int averageCharWidth = 0;
	public int weightClass = WEIGHT_CLASS_MEDIUM;
	public int widthClass = WIDTH_CLASS_MEDIUM;
	public int flags = 0;
	public int subscriptXSize = 0;
	public int subscriptYSize = 0;
	public int subscriptXOffset = 0;
	public int subscriptYOffset = 0;
	public int superscriptXSize = 0;
	public int superscriptYSize = 0;
	public int superscriptXOffset = 0;
	public int superscriptYOffset = 0;
	public int strikeoutWidth = 0;
	public int strikeoutPosition = 0;
	public int familyClass = FAMILY_CLASS_NO_CLASSIFICATION;
	public int familySubClass = FAMILY_SUBCLASS_NO_CLASSIFICATION;
	public int panoseFamilyType = PANOSE_FAMILY_TYPE_ANY;
	public int panoseSerifStyle = PANOSE_SERIF_STYLE_ANY;
	public int panoseWeight = PANOSE_WEIGHT_ANY;
	public int panoseProportion = PANOSE_PROPORTION_ANY;
	public int panoseContrast = PANOSE_CONTRAST_ANY;
	public int panoseStrokeVariation = PANOSE_STROKE_VARIATION_ANY;
	public int panoseArmStyle = PANOSE_ARM_STYLE_ANY;
	public int panoseLetterform = PANOSE_LETTERFORM_ANY;
	public int panoseMidline = PANOSE_MIDLINE_ANY;
	public int panoseXHeight = PANOSE_X_HEIGHT_ANY;
	public final int[] unicodeRanges = new int[4];
	public int vendorID = VENDOR_ID_ckbt;
	public int fsSelection = 0;
	public int fsFirstCharIndex = 0x20;
	public int fsLastCharIndex = 0x20;
	// Version 0 (68-byte) ends
	
	// Version 1 (78-byte) begins
	public int typoAscent = 0;
	public int typoDescent = 0;
	public int typoLineGap = 0;
	public int winAscent = 0;
	public int winDescent = 0;
	// Version 1 (78-byte) ends
	
	// Version 2 (86-byte) begins
	public final int[] codePages = new int[2];
	// Version 2 (86-byte) ends
	
	// Version 3 or 4 (96-byte) begins
	public int xHeight = 0;
	public int capHeight = 0;
	public int defaultChar = 0;
	public int breakChar = 0x20;
	public int maxContext = 0;
	// Version 3 or 4 (96-byte) ends
	
	// Version 5 (100-byte) begins
	public int lowerOpticalPointSize = 0;
	public int upperOpticalPointSize = 0xFFFF;
	// Version 5 (100-byte) ends
	
	@Override
	public String tableName() {
		return "OS/2";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	public String getVendorIDString() {
		return new String(new char[]{
			(char)((vendorID >> 24) & 0xFF),
			(char)((vendorID >> 16) & 0xFF),
			(char)((vendorID >>  8) & 0xFF),
			(char)((vendorID >>  0) & 0xFF),
		});
	}
	
	public void setVendorIDString(String vendorIDString) {
		char[] a = vendorIDString.toCharArray();
		vendorID = (((a.length > 0 && a[0] >= 0x20 && a[0] < 0x7F) ? a[0] : 0x20) << 24)
		         | (((a.length > 1 && a[1] >= 0x20 && a[1] < 0x7F) ? a[1] : 0x20) << 16)
		         | (((a.length > 2 && a[2] >= 0x20 && a[2] < 0x7F) ? a[2] : 0x20) <<  8)
		         | (((a.length > 3 && a[3] >= 0x20 && a[3] < 0x7F) ? a[3] : 0x20) <<  0);
	}
	
	public double getLowerOpticalPointSizeDouble() {
		if (lowerOpticalPointSize >= 0xFFFF) return Double.POSITIVE_INFINITY;
		if (lowerOpticalPointSize <= 0) return 0.0;
		return lowerOpticalPointSize / 20.0;
	}
	
	public double getUpperOpticalPointSizeDouble() {
		if (upperOpticalPointSize >= 0xFFFF) return Double.POSITIVE_INFINITY;
		if (upperOpticalPointSize <= 0) return 0.0;
		return upperOpticalPointSize / 20.0;
	}
	
	public void setLowerOpticalPointSizeDouble(double pt) {
		pt *= 20.0;
		lowerOpticalPointSize =
				(pt <= 0) ? 0 :
				(pt >= 0xFFFF) ? 0xFFFF :
				(int)Math.round(pt);
	}
	
	public void setUpperOpticalPointSizeDouble(double pt) {
		pt *= 20.0;
		upperOpticalPointSize =
				(pt <= 0) ? 0 :
				(pt >= 0xFFFF) ? 0xFFFF :
				(int)Math.round(pt);
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		out.writeShort(version);
		out.writeShort(averageCharWidth);
		out.writeShort(weightClass);
		out.writeShort(widthClass);
		out.writeShort(flags);
		out.writeShort(subscriptXSize);
		out.writeShort(subscriptYSize);
		out.writeShort(subscriptXOffset);
		out.writeShort(subscriptYOffset);
		out.writeShort(superscriptXSize);
		out.writeShort(superscriptYSize);
		out.writeShort(superscriptXOffset);
		out.writeShort(superscriptYOffset);
		out.writeShort(strikeoutWidth);
		out.writeShort(strikeoutPosition);
		out.writeByte(familyClass);
		out.writeByte(familySubClass);
		out.writeByte(panoseFamilyType);
		out.writeByte(panoseSerifStyle);
		out.writeByte(panoseWeight);
		out.writeByte(panoseProportion);
		out.writeByte(panoseContrast);
		out.writeByte(panoseStrokeVariation);
		out.writeByte(panoseArmStyle);
		out.writeByte(panoseLetterform);
		out.writeByte(panoseMidline);
		out.writeByte(panoseXHeight);
		out.writeInt(unicodeRanges[0]);
		out.writeInt(unicodeRanges[1]);
		out.writeInt(unicodeRanges[2]);
		out.writeInt(unicodeRanges[3]);
		out.writeInt(vendorID);
		out.writeShort(fsSelection);
		out.writeShort(fsFirstCharIndex);
		out.writeShort(fsLastCharIndex);
		if (version >= 1) {
			out.writeShort(typoAscent);
			out.writeShort(typoDescent);
			out.writeShort(typoLineGap);
			out.writeShort(winAscent);
			out.writeShort(winDescent);
		}
		if (version >= 2) {
			out.writeInt(codePages[0]);
			out.writeInt(codePages[1]);
		}
		if (version >= 3) {
			out.writeShort(xHeight);
			out.writeShort(capHeight);
			out.writeShort(defaultChar);
			out.writeShort(breakChar);
			out.writeShort(maxContext);
		}
		if (version >= 5) {
			out.writeShort(lowerOpticalPointSize);
			out.writeShort(upperOpticalPointSize);
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		version = in.readUnsignedShort();
		averageCharWidth = in.readShort();
		weightClass = in.readUnsignedShort();
		widthClass = in.readUnsignedShort();
		flags = in.readUnsignedShort();
		subscriptXSize = in.readShort();
		subscriptYSize = in.readShort();
		subscriptXOffset = in.readShort();
		subscriptYOffset = in.readShort();
		superscriptXSize = in.readShort();
		superscriptYSize = in.readShort();
		superscriptXOffset = in.readShort();
		superscriptYOffset = in.readShort();
		strikeoutWidth = in.readShort();
		strikeoutPosition = in.readShort();
		familyClass = in.readUnsignedByte();
		familySubClass = in.readUnsignedByte();
		panoseFamilyType = in.readUnsignedByte();
		panoseSerifStyle = in.readUnsignedByte();
		panoseWeight = in.readUnsignedByte();
		panoseProportion = in.readUnsignedByte();
		panoseContrast = in.readUnsignedByte();
		panoseStrokeVariation = in.readUnsignedByte();
		panoseArmStyle = in.readUnsignedByte();
		panoseLetterform = in.readUnsignedByte();
		panoseMidline = in.readUnsignedByte();
		panoseXHeight = in.readUnsignedByte();
		unicodeRanges[0] = in.readInt();
		unicodeRanges[1] = in.readInt();
		unicodeRanges[2] = in.readInt();
		unicodeRanges[3] = in.readInt();
		vendorID = in.readInt();
		fsSelection = in.readUnsignedShort();
		fsFirstCharIndex = in.readUnsignedShort();
		fsLastCharIndex = in.readUnsignedShort();
		typoAscent = (version >= 1) ? in.readShort() : 0;
		typoDescent = (version >= 1) ? in.readShort() : 0;
		typoLineGap = (version >= 1) ? in.readShort() : 0;
		winAscent = (version >= 1) ? in.readShort() : 0;
		winDescent = (version >= 1) ? in.readShort() : 0;
		codePages[0] = (version >= 2) ? in.readInt() : 0;
		codePages[1] = (version >= 2) ? in.readInt() : 0;
		xHeight = (version >= 3) ? in.readShort() : 0;
		capHeight = (version >= 3) ? in.readShort() : 0;
		defaultChar = (version >= 3) ? in.readUnsignedShort() : 0;
		breakChar = (version >= 3) ? in.readUnsignedShort() : 0x20;
		maxContext = (version >= 3) ? in.readUnsignedShort() : 0;
		lowerOpticalPointSize = (version >= 5) ? in.readUnsignedShort() : 0;
		upperOpticalPointSize = (version >= 5) ? in.readUnsignedShort() : 0xFFFF;
	}
	
	public static final int[][] UNICODE_RANGES = new int[][] {
		new int[] { 0x0000, 0x007F }, // 0
		new int[] { 0x0080, 0x00FF }, // 1
		new int[] { 0x0100, 0x017F }, // 2
		new int[] { 0x0180, 0x024F }, // 3
		new int[] { 0x0250, 0x02AF, 0x1D00, 0x1DBF }, // 4
		new int[] { 0x02B0, 0x02FF, 0xA700, 0xA71F }, // 5
		new int[] { 0x0300, 0x036F, 0x1DC0, 0x1DFF }, // 6
		new int[] { 0x0370, 0x03FF }, // 7
		new int[] { 0x2C80, 0x2CFF }, // 8
		new int[] { 0x0400, 0x052F, 0x2DE0, 0x2DFF, 0xA640, 0xA69F }, // 9
		new int[] { 0x0530, 0x058F }, // 10
		new int[] { 0x0590, 0x05FF }, // 11
		new int[] { 0xA500, 0xA63F }, // 12
		new int[] { 0x0600, 0x06FF, 0x0750, 0x077F }, // 13
		new int[] { 0x07C0, 0x07FF }, // 14
		new int[] { 0x0900, 0x097F }, // 15
		new int[] { 0x0980, 0x09FF }, // 16
		new int[] { 0x0A00, 0x0A7F }, // 17
		new int[] { 0x0A80, 0x0AFF }, // 18
		new int[] { 0x0B00, 0x0B7F }, // 19
		new int[] { 0x0B80, 0x0BFF }, // 20
		new int[] { 0x0C00, 0x0C7F }, // 21
		new int[] { 0x0C80, 0x0CFF }, // 22
		new int[] { 0x0D00, 0x0D7F }, // 23
		new int[] { 0x0E00, 0x0E7F }, // 24
		new int[] { 0x0E80, 0x0EFF }, // 25
		new int[] { 0x10A0, 0x10FF, 0x2D00, 0x2D2F }, // 26
		new int[] { 0x1B00, 0x1B7F }, // 27
		new int[] { 0x1100, 0x11FF }, // 28
		new int[] { 0x1E00, 0x1EFF, 0x2C60, 0x2C7F, 0xA720, 0xA7FF }, // 29
		new int[] { 0x1F00, 0x1FFF }, // 30
		new int[] { 0x2000, 0x206F, 0x2E00, 0x2E7F }, // 31
		new int[] { 0x2070, 0x209F }, // 32
		new int[] { 0x20A0, 0x20CF }, // 33
		new int[] { 0x20D0, 0x20FF }, // 34
		new int[] { 0x2100, 0x214F }, // 35
		new int[] { 0x2150, 0x218F }, // 36
		new int[] { 0x2190, 0x21FF, 0x27F0, 0x27FF, 0x2900, 0x297F, 0x2B00, 0x2BFF }, // 37
		new int[] { 0x2200, 0x22FF, 0x27C0, 0x27EF, 0x2980, 0x2AFF }, // 38
		new int[] { 0x2300, 0x23FF }, // 39
		new int[] { 0x2400, 0x243F }, // 40
		new int[] { 0x2440, 0x245F }, // 41
		new int[] { 0x2460, 0x24FF }, // 42
		new int[] { 0x2500, 0x257F }, // 43
		new int[] { 0x2580, 0x259F }, // 44
		new int[] { 0x25A0, 0x25FF }, // 45
		new int[] { 0x2600, 0x26FF }, // 46
		new int[] { 0x2700, 0x27BF }, // 47
		new int[] { 0x3000, 0x303F }, // 48
		new int[] { 0x3040, 0x309F }, // 49
		new int[] { 0x30A0, 0x30FF, 0x31F0, 0x31FF }, // 50
		new int[] { 0x3100, 0x312F, 0x31A0, 0x31BF }, // 51
		new int[] { 0x3130, 0x318F }, // 52
		new int[] { 0xA840, 0xA87F }, // 53
		new int[] { 0x3200, 0x32FF }, // 54
		new int[] { 0x3300, 0x33FF }, // 55
		new int[] { 0xAC00, 0xD7AF }, // 56
		new int[] { 0x10000, 0x10FFFF }, // 57
		new int[] { 0x10900, 0x1091F }, // 58
		new int[] { 0x2E80, 0x2FDF, 0x2FF0, 0x2FFF, 0x3190, 0x319F, 0x3400, 0x4DBF, 0x4E00, 0x9FFF, 0x20000, 0x2A6DF }, // 59
		new int[] { 0xE000, 0xF8FF }, // 60
		new int[] { 0x31C0, 0x31EF, 0xF900, 0xFAFF, 0x2F800, 0x2FA1F }, // 61
		new int[] { 0xFB00, 0xFB4F }, // 62
		new int[] { 0xFB50, 0xFDFF }, // 63
		new int[] { 0xFE20, 0xFE2F }, // 64
		new int[] { 0xFE10, 0xFE1F, 0xFE30, 0xFE4F }, // 65
		new int[] { 0xFE50, 0xFE6F }, // 66
		new int[] { 0xFE70, 0xFEFF }, // 67
		new int[] { 0xFF00, 0xFFEF }, // 68
		new int[] { 0xFFF0, 0xFFFF }, // 69
		new int[] { 0x0F00, 0x0FFF }, // 70
		new int[] { 0x0700, 0x074F }, // 71
		new int[] { 0x0780, 0x07BF }, // 72
		new int[] { 0x0D80, 0x0DFF }, // 73
		new int[] { 0x1000, 0x109F }, // 74
		new int[] { 0x1200, 0x139F, 0x2D80, 0x2DDF }, // 75
		new int[] { 0x13A0, 0x13FF }, // 76
		new int[] { 0x1400, 0x167F }, // 77
		new int[] { 0x1680, 0x169F }, // 78
		new int[] { 0x16A0, 0x16FF }, // 79
		new int[] { 0x1780, 0x17FF, 0x19E0, 0x19FF }, // 80
		new int[] { 0x1800, 0x18AF }, // 81
		new int[] { 0x2800, 0x28FF }, // 82
		new int[] { 0xA000, 0xA4CF }, // 83
		new int[] { 0x1700, 0x177F }, // 84
		new int[] { 0x10300, 0x1032F }, // 85
		new int[] { 0x10330, 0x1034F }, // 86
		new int[] { 0x10400, 0x1044F }, // 87
		new int[] { 0x1D000, 0x1D24F }, // 88
		new int[] { 0x1D400, 0x1D7FF }, // 89
		new int[] { 0xF0000, 0xFFFFD, 0x100000, 0x10FFFD }, // 90
		new int[] { 0xFE00, 0xFE0F, 0xE0100, 0xE01EF }, // 91
		new int[] { 0xE0000, 0xE007F }, // 92
		new int[] { 0x1900, 0x194F }, // 93
		new int[] { 0x1950, 0x197F }, // 94
		new int[] { 0x1980, 0x19DF }, // 95
		new int[] { 0x1A00, 0x1A1F }, // 96
		new int[] { 0x2C00, 0x2C5F }, // 97
		new int[] { 0x2D30, 0x2D7F }, // 98
		new int[] { 0x4DC0, 0x4DFF }, // 99
		new int[] { 0xA800, 0xA82F }, // 100
		new int[] { 0x10000, 0x1013F }, // 101
		new int[] { 0x10140, 0x1018F }, // 102
		new int[] { 0x10380, 0x1039F }, // 103
		new int[] { 0x103A0, 0x103DF }, // 104
		new int[] { 0x10450, 0x1047F }, // 105
		new int[] { 0x10480, 0x104AF }, // 106
		new int[] { 0x10800, 0x1083F }, // 107
		new int[] { 0x10A00, 0x10A5F }, // 108
		new int[] { 0x1D300, 0x1D35F }, // 109
		new int[] { 0x12000, 0x1247F }, // 110
		new int[] { 0x1D360, 0x1D37F }, // 111
		new int[] { 0x1B80, 0x1BBF }, // 112
		new int[] { 0x1C00, 0x1C4F }, // 113
		new int[] { 0x1C50, 0x1C7F }, // 114
		new int[] { 0xA880, 0xA8DF }, // 115
		new int[] { 0xA900, 0xA92F }, // 116
		new int[] { 0xA930, 0xA95F }, // 117
		new int[] { 0xAA00, 0xAA5F }, // 118
		new int[] { 0x10190, 0x101CF }, // 119
		new int[] { 0x101D0, 0x101FF }, // 120
		new int[] { 0x10280, 0x102DF, 0x10920, 0x1093F }, // 121
		new int[] { 0x1F000, 0x1F09F }, // 122
		null, // 123
		null, // 124
		null, // 125
		null, // 126
		null, // 127
	};
	
	public static final String[] CODE_PAGES = new String[] {
		"CP1252", // 0
		"CP1250", // 1
		"CP1251", // 2
		"CP1253", // 3
		"CP1254", // 4
		"CP1255", // 5
		"CP1256", // 6
		"CP1257", // 7
		"CP1258", // 8
		null, // 9
		null, // 10
		null, // 11
		null, // 12
		null, // 13
		null, // 14
		null, // 15
		"CP874", // 16
		"CP932", // 17
		"CP936", // 18
		"CP949", // 19
		"CP950", // 20
		"CP1361", // 21
		null, // 22
		null, // 23
		null, // 24
		null, // 25
		null, // 26
		null, // 27
		null, // 28
		"MACROMAN", // 29
		null, // 30
		null, // 31
		null, // 32
		null, // 33
		null, // 34
		null, // 35
		null, // 36
		null, // 37
		null, // 38
		null, // 39
		null, // 40
		null, // 41
		null, // 42
		null, // 43
		null, // 44
		null, // 45
		null, // 46
		null, // 47
		"CP869", // 48
		"CP866", // 49
		"CP865", // 50
		"CP864", // 51
		"CP863", // 52
		"CP862", // 53
		"CP861", // 54
		"CP860", // 55
		"CP857", // 56
		"CP855", // 57
		"CP852", // 58
		"CP775", // 59
		"CP737", // 60
		"CP708", // 61
		"CP850", // 62
		"CP437", // 63
	};
	
	public void setCharIndices(Collection<? extends Integer> codePoints) {
		int minChar = 0x7FFF0020;
		int maxChar = 0x80000020;
		for (int ch : codePoints) {
			if (ch >= 0x10000) {
				maxChar = 0xFFFF;
			} else if (ch >= 0) {
				if (ch < minChar) minChar = ch;
				if (ch > maxChar) maxChar = ch;
			}
		}
		fsFirstCharIndex = minChar & 0xFFFF;
		fsLastCharIndex = maxChar & 0xFFFF;
	}
	
	public void setUnicodeRanges(Collection<? extends Integer> codePoints) {
		for (int k = 0, mx = 31, mn = 0; k < 4; k++, mx += 32, mn += 32) {
			int uniRanges = 0;
			for (int i = mx; i >= mn; i--) {
				uniRanges <<= 1;
				if (UNICODE_RANGES[i] != null) {
					boolean supportsAny = false;
					int[] range = UNICODE_RANGES[i];
					for (int j = 0; j < range.length; j+=2) {
						int start = range[j], end = range[j+1];
						for (int ch : codePoints) {
							if (ch >= start && ch <= end) {
								supportsAny = true;
								break;
							}
						}
					}
					if (supportsAny) uniRanges |= 1;
				}
			}
			this.unicodeRanges[k] = uniRanges;
		}
	}
	
	public void setCodePages(Collection<? extends Integer> codePoints) {
		byte[] prog = new byte[256];
		for (int i = 0; i < 256; i++) prog[i] = (byte)i;
		for (int k = 0, mx = 31, mn = 0; k < 2; k++, mx += 32, mn += 32) {
			int codePages = 0;
			for (int i = mx; i >= mn; i--) {
				codePages <<= 1;
				if (CODE_PAGES[i] != null) {
					try {
						String s = new String(prog, CODE_PAGES[i]);
						boolean supportsAll = true;
						CharacterIterator ci = new StringCharacterIterator(s);
						for (int ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
							if ((ch >= 0x20 && ch < 0x7F) || (ch >= 0xA0 && ch < 0xD800) || (ch >= 0xF900 && ch < 0xFFFD)) {
								if (!codePoints.contains(ch)) {
									supportsAll = false;
									break;
								}
							}
						}
						if (supportsAll) codePages |= 1;
					} catch (UnsupportedEncodingException uee) {}
				}
			}
			this.codePages[k] = codePages;
		}
	}
}
