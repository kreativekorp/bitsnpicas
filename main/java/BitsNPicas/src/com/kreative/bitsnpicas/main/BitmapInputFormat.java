package com.kreative.bitsnpicas.main;

import java.io.File;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.importer.BDFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FZXBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HMZKBitmapFontImporter;
import com.kreative.bitsnpicas.importer.KBnPBitmapFontImporter;
import com.kreative.bitsnpicas.importer.NFNTBitmapFontImporter;
import com.kreative.bitsnpicas.importer.S10BitmapFontImporter;
import com.kreative.bitsnpicas.importer.SBFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SFDBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SRFontBitmapFontImporter;

public enum BitmapInputFormat {
	KBITS(".kbits", BitmapFont.NAME_FAMILY_AND_STYLE) {
		public BitmapFontImporter createImporter() { return new KBnPBitmapFontImporter(); }
	},
	SFD(".sfd", BitmapFont.NAME_POSTSCRIPT) {
		public BitmapFontImporter createImporter() { return new SFDBitmapFontImporter(); }
	},
	BDF(".bdf", BitmapFont.NAME_FAMILY_AND_STYLE) {
		public BitmapFontImporter createImporter() { return new BDFBitmapFontImporter(); }
	},
	SUIT(".suit", BitmapFont.NAME_FAMILY_AND_STYLE, true) {
		public BitmapFontImporter createImporter() { return new NFNTBitmapFontImporter(); }
	},
	DFONT(".dfont", BitmapFont.NAME_FAMILY_AND_STYLE) {
		public BitmapFontImporter createImporter() { return new NFNTBitmapFontImporter(); }
	},
	PNG(".png", BitmapFont.NAME_FAMILY_AND_STYLE) {
		public BitmapFontImporter createImporter() { return new SRFontBitmapFontImporter(); }
	},
	FZX(".fzx", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter() { return new FZXBitmapFontImporter(); }
	},
	HMZK(".hmzk", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter() { return new HMZKBitmapFontImporter(); }
	},
	
	// **** Add new formats above this line. ****
	
	DSF(".dsf", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter() { return new DSFBitmapFontImporter(); }
	},
	SBF(".sbf", BitmapFont.NAME_FAMILY) {
		public BitmapFontImporter createImporter() { return new SBFBitmapFontImporter(); }
	},
	S10(".s10", BitmapFont.NAME_FAMILY_AND_STYLE) {
		public BitmapFontImporter createImporter() { return new S10BitmapFontImporter(); }
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
	
	public abstract BitmapFontImporter createImporter();
	
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
