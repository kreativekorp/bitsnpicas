package com.kreative.bitsnpicas.main;

import java.io.File;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.FileProxy;
import com.kreative.bitsnpicas.importer.BDFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.CybikoBitmapFontImporter;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FNTBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FONTXBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FZXBitmapFontImporter;
import com.kreative.bitsnpicas.importer.GEOSBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HMZKBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HexBitmapFontImporter;
import com.kreative.bitsnpicas.importer.KBnPBitmapFontImporter;
import com.kreative.bitsnpicas.importer.NFNTBitmapFontImporter;
import com.kreative.bitsnpicas.importer.PSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.RockboxBitmapFontImporter;
import com.kreative.bitsnpicas.importer.S10BitmapFontImporter;
import com.kreative.bitsnpicas.importer.SBFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SFDBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SRFontBitmapFontImporter;
import com.kreative.bitsnpicas.importer.U8MBitmapFontImporter;
import com.kreative.unicode.data.EncodingList;

public enum BitmapInputFormat {
	KBITS(BitmapFont.NAME_FAMILY_AND_STYLE) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".kbits"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new KBnPBitmapFontImporter();
		}
	},
	SFD(BitmapFont.NAME_POSTSCRIPT) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".sfd"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new SFDBitmapFontImporter();
		}
	},
	BDF(BitmapFont.NAME_FAMILY_AND_STYLE) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".bdf"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new BDFBitmapFontImporter();
		}
	},
	PSF(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".psf", ".psfu"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new PSFBitmapFontImporter(
				o.getPsfLowEncoding(),
				o.getPsfHighEncoding(),
				o.psfPuaBase, false
			);
		}
	},
	PSFGZ(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".psf.gz", ".psfu.gz"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new PSFBitmapFontImporter(
				o.getPsfLowEncoding(),
				o.getPsfHighEncoding(),
				o.psfPuaBase, true
			);
		}
	},
	SUIT(BitmapFont.NAME_FAMILY_AND_STYLE, true) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".suit"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new NFNTBitmapFontImporter(o.getEncoding());
		}
	},
	DFONT(BitmapFont.NAME_FAMILY_AND_STYLE, false) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".dfont"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new NFNTBitmapFontImporter(o.getEncoding());
		}
	},
	SRFONT(BitmapFont.NAME_FAMILY_AND_STYLE) {
		public boolean recognize(FileProxy fp) {
			return (
				fp.hasExtension(".png") && fp.isImage() &&
				new SRFontBitmapFontImporter().canImportFont(fp.getImage())
			);
		}
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new SRFontBitmapFontImporter();
		}
	},
	HEX(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".hex"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new HexBitmapFontImporter();
		}
	},
	CVT(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".cvt"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new GEOSBitmapFontImporter();
		}
	},
	FZX(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".fzx"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new FZXBitmapFontImporter(o.getEncoding());
		}
	},
	U8M(BitmapFont.NAME_FAMILY_AND_STYLE) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".u8m"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new U8MBitmapFontImporter();
		}
	},
	FNT(BitmapFont.NAME_FAMILY_AND_STYLE) {
		public boolean recognize(FileProxy fp) {
			return fp.hasExtension(".fnt") && fp.startsWith(0);
		}
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new FNTBitmapFontImporter(o.getEncoding());
		}
	},
	FONTX(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) {
			return fp.hasExtension(".ftx", ".fnt") && fp.startsWith('F');
		}
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			String sben = o.encodingName;
			if (sben == null || sben.length() == 0) sben = "CP437";
			String dben = o.fontxDoubleByteEncoding;
			if (dben == null || dben.length() == 0) dben = "CP943";
			return new FONTXBitmapFontImporter(EncodingList.instance().getGlyphList(sben), dben);
		}
	},
	ROCKBOX(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) {
			return fp.hasExtension(".rbf", ".rb11", ".rb12", ".fnt") && fp.startsWith('R');
		}
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new RockboxBitmapFontImporter();
		}
	},
	CYBIKO(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) {
			return fp.hasExtension(".cyf", ".fntz", ".fnty", ".fnt") && fp.startsWith(1);
		}
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new CybikoBitmapFontImporter(o.getEncoding());
		}
	},
	HMZK(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".hmzk"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new HMZKBitmapFontImporter();
		}
	},
	
	// **** Add new formats above this line. ****
	
	DSF(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".dsf"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new DSFBitmapFontImporter();
		}
	},
	SBF(BitmapFont.NAME_FAMILY) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".sbf"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new SBFBitmapFontImporter(o.getEncoding());
		}
	},
	S10(BitmapFont.NAME_FAMILY_AND_STYLE) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".s10"); }
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new S10BitmapFontImporter();
		}
	},
	DMOV(BitmapFont.NAME_FAMILY_AND_STYLE, true) {
		public boolean recognize(FileProxy fp) {
			return (
				fp.hasMacCreator("DMOV") ||
				fp.hasMacCreator("movr") ||
				(
					(fp.hasMacCreator("MACS") || fp.hasMacCreator("macs")) &&
					(fp.hasMacType("ZSYS") || fp.hasMacType("zsys"))
				)
			);
		}
		public BitmapFontImporter createImporter(BitmapInputOptions o) {
			return new NFNTBitmapFontImporter(o.getEncoding());
		}
	};
	
	public final int nameType;
	public final boolean macResFork;
	
	private BitmapInputFormat(int nameType) {
		this.nameType = nameType;
		this.macResFork = false;
	}
	
	private BitmapInputFormat(int nameType, boolean macResFork) {
		this.nameType = nameType;
		this.macResFork = macResFork;
	}
	
	public abstract boolean recognize(FileProxy fp);
	public abstract BitmapFontImporter createImporter(BitmapInputOptions o);
	
	public static BitmapInputFormat forFile(File file) {
		FileProxy fp = new FileProxy(file);
		for (BitmapInputFormat f : values()) {
			if (f.recognize(fp)) {
				return f;
			}
		}
		return null;
	}
}
