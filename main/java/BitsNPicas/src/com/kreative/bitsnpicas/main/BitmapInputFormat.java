package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.importer.BDFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.CybikoBitmapFontImporter;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FONTXBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FZXBitmapFontImporter;
import com.kreative.bitsnpicas.importer.GEOSBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HMZKBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HexBitmapFontImporter;
import com.kreative.bitsnpicas.importer.KBnPBitmapFontImporter;
import com.kreative.bitsnpicas.importer.NFNTBitmapFontImporter;
import com.kreative.bitsnpicas.importer.RockboxBitmapFontImporter;
import com.kreative.bitsnpicas.importer.S10BitmapFontImporter;
import com.kreative.bitsnpicas.importer.SBFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SFDBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SRFontBitmapFontImporter;
import com.kreative.bitsnpicas.importer.U8MBitmapFontImporter;
import com.kreative.bitsnpicas.unicode.EncodingList;

public enum BitmapInputFormat {
	KBITS(".kbits", BitmapFont.NAME_FAMILY_AND_STYLE) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new KBnPBitmapFontImporter();
		}
	},
	SFD(".sfd", BitmapFont.NAME_POSTSCRIPT) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new SFDBitmapFontImporter();
		}
	},
	BDF(".bdf", BitmapFont.NAME_FAMILY_AND_STYLE) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new BDFBitmapFontImporter();
		}
	},
	SUIT(".suit", BitmapFont.NAME_FAMILY_AND_STYLE, true) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new NFNTBitmapFontImporter(
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
		}
	},
	DFONT(".dfont", BitmapFont.NAME_FAMILY_AND_STYLE, false) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new NFNTBitmapFontImporter(
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
		}
	},
	PNG(".png", BitmapFont.NAME_FAMILY_AND_STYLE) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new SRFontBitmapFontImporter();
		}
	},
	HEX(".hex", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new HexBitmapFontImporter();
		}
	},
	CVT(".cvt", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new GEOSBitmapFontImporter();
		}
	},
	FZX(".fzx", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new FZXBitmapFontImporter(
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
		}
	},
	U8M(".u8m", BitmapFont.NAME_FAMILY_AND_STYLE) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new U8MBitmapFontImporter();
		}
	},
	FONTX(".ftx", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			String sben = o.encodingName;
			if (sben == null || sben.length() == 0) sben = "CP437";
			String dben = o.fontxDoubleByteEncoding;
			if (dben == null || dben.length() == 0) dben = "CP943";
			return new FONTXBitmapFontImporter(EncodingList.instance().get(sben), dben);
		}
	},
	ROCKBOX(".rbf", ".rb11", ".rb12", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new RockboxBitmapFontImporter();
		}
	},
	CYBIKO(".cyf", ".fntz", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new CybikoBitmapFontImporter(
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
		}
	},
	HMZK(".hmzk", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new HMZKBitmapFontImporter();
		}
	},
	
	// **** Add new formats above this line. ****
	
	DSF(".dsf", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new DSFBitmapFontImporter();
		}
	},
	SBF(".sbf", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new SBFBitmapFontImporter(
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
		}
	},
	S10(".s10", BitmapFont.NAME_FAMILY_AND_STYLE) {
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new S10BitmapFontImporter();
		}
	};
	
	public final String[] extensions;
	public final int nameType;
	public final boolean macResFork;
	
	private BitmapInputFormat(String extension, int nameType) {
		this.extensions = new String[]{extension};
		this.nameType = nameType;
		this.macResFork = false;
	}
	
	private BitmapInputFormat(String extension, int nameType, boolean macResFork) {
		this.extensions = new String[]{extension};
		this.nameType = nameType;
		this.macResFork = macResFork;
	}
	
	private BitmapInputFormat(String ext1, String ext2, int nameType) {
		this.extensions = new String[]{ext1, ext2};
		this.nameType = nameType;
		this.macResFork = false;
	}
	
	private BitmapInputFormat(String ext1, String ext2, String ext3, int nameType) {
		this.extensions = new String[]{ext1, ext2, ext3};
		this.nameType = nameType;
		this.macResFork = false;
	}
	
	public abstract BitmapFontImporter createImporter(BitmapInputOptions o);
	
	public static BitmapInputFormat forFile(File file) {
		String lname = file.getName().toLowerCase();
		
		// Detect the many formats using .fnt magically.
		if (lname.endsWith(".fnt")) {
			try {
				FileInputStream in = new FileInputStream(file);
				int magic = in.read();
				in.close();
				switch (magic) {
					// case 0: return FNT;
					case 1: return CYBIKO;
					case 'F': return FONTX;
					case 'R': return ROCKBOX;
				}
			} catch (IOException e) {
				return null;
			}
		}
		
		// Detect most file formats by file extension.
		for (BitmapInputFormat f : values()) {
			for (String ext : f.extensions) {
				if (lname.endsWith(ext)) {
					return f;
				}
			}
		}
		
		// Detect Mac OS Classic suitcases by file type and creator code.
		String creator = MacUtility.getCreator(file);
		if (creator == null) return null;
		if (creator.equals("DMOV") || creator.equals("movr")) return SUIT;
		if (creator.equals("MACS") || creator.equals("macs")) {
			String type = MacUtility.getType(file);
			if (type == null) return null;
			if (type.equals("ZSYS") || type.equals("zsys")) return SUIT;
		}
		return null;
	}
}
