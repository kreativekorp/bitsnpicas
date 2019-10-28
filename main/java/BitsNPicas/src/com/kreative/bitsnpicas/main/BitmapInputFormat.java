package com.kreative.bitsnpicas.main;

import java.io.File;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.importer.BDFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FZXBitmapFontImporter;
import com.kreative.bitsnpicas.importer.GEOSBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HMZKBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HexBitmapFontImporter;
import com.kreative.bitsnpicas.importer.KBnPBitmapFontImporter;
import com.kreative.bitsnpicas.importer.NFNTBitmapFontImporter;
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
	DFONT(".dfont", BitmapFont.NAME_FAMILY_AND_STYLE) {
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
	
	public abstract BitmapFontImporter createImporter(BitmapInputOptions o);
	
	public static BitmapInputFormat forFile(File file) {
		String lname = file.getName().toLowerCase();
		for (BitmapInputFormat f : values()) {
			for (String ext : f.extensions) {
				if (lname.endsWith(ext)) {
					return f;
				}
			}
		}
		return null;
	}
}
