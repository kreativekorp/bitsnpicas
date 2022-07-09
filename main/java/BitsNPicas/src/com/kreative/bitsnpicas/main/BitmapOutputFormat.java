package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.IOException;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.exporter.BDFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.CybikoBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.FNTBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.FONTXBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.FZXBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.GEOSBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.HMZKBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.HexBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.KBnPBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.NFNTBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.OTBBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.PSFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.RFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.RockboxBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SBFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TOSBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TTFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.U8MBitmapFontExporter;
import com.kreative.unicode.data.EncodingList;

public enum BitmapOutputFormat {
	KBITS(".kbits", "kbits", "kbnp") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new KBnPBitmapFontExporter();
		}
	},
	TTF(".ttf", "ttf", "truetype") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new TTFBitmapFontExporter(o.xSize, o.ySize, o.extendWinMetrics);
		}
	},
	OTB(".otb", "otb") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new OTBBitmapFontExporter(o.extendWinMetrics);
		}
	},
	BDF(".bdf", "bdf") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new BDFBitmapFontExporter();
		}
	},
	PSF2(".psf", "psf", "psf2") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new PSFBitmapFontExporter(
				2, o.getPsfLowEncoding(), o.getPsfHighEncoding(),
				o.psfUseLowEncoding, o.psfUseHighEncoding,
				o.psfUseAllGlyphs, o.psfUnicodeTable, false
			);
		}
	},
	PSF1(".psf", "psf1") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new PSFBitmapFontExporter(
				1, o.getPsfLowEncoding(), o.getPsfHighEncoding(),
				o.psfUseLowEncoding, o.psfUseHighEncoding,
				o.psfUseAllGlyphs, o.psfUnicodeTable, false
			);
		}
	},
	PSF2GZ(".psf.gz", "psfgz", "psf2gz") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new PSFBitmapFontExporter(
				2, o.getPsfLowEncoding(), o.getPsfHighEncoding(),
				o.psfUseLowEncoding, o.psfUseHighEncoding,
				o.psfUseAllGlyphs, o.psfUnicodeTable, true
			);
		}
	},
	PSF1GZ(".psf.gz", "psf1gz") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new PSFBitmapFontExporter(
				1, o.getPsfLowEncoding(), o.getPsfHighEncoding(),
				o.psfUseLowEncoding, o.psfUseHighEncoding,
				o.psfUseAllGlyphs, o.psfUnicodeTable, true
			);
		}
	},
	SUIT(".suit", "nfnt", "suit", true) {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			o.idgen.setRange(128, 32768);
			o.sizegen.setRange(4, 127);
			o.sizegen.setPointSizes(9, 10, 12, 14, 18, 24, 36, 48, 72);
			return new NFNTBitmapFontExporter(o.idgen, o.sizegen, o.getEncoding());
		}
		public void postProcess(File file) throws IOException {
			MacUtility.setTypeAndCreator(file, "FFIL", "DMOV");
		}
	},
	DFONT(".dfont", "dfont") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			o.idgen.setRange(128, 32768);
			o.sizegen.setRange(4, 127);
			o.sizegen.setPointSizes(9, 10, 12, 14, 18, 24, 36, 48, 72);
			return new NFNTBitmapFontExporter(o.idgen, o.sizegen, o.getEncoding());
		}
	},
	SFONT(".png", "png", "sfont") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new SFontBitmapFontExporter();
		}
	},
	RFONT(".png", "rfont") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new RFontBitmapFontExporter();
		}
	},
	HEX(".hex", "hex") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new HexBitmapFontExporter();
		}
	},
	CVT(".cvt", "cvt", "geos") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			o.idgen.setRange(128, 1024);
			o.sizegen.setRange(6, 63);
			o.sizegen.setPointSizes(9, 10, 12, 14, 18, 24, 36, 48, 60);
			return new GEOSBitmapFontExporter(
				o.idgen, o.sizegen, o.geosMega,
				o.geosKerning, o.geosUTF8
			);
		}
	},
	FZX(".fzx", "fzx") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new FZXBitmapFontExporter(o.getEncoding());
		}
	},
	U8M(".u8m", "u8m") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new U8MBitmapFontExporter(o.u8mLoadAddress, o.getEncoding());
		}
	},
	FNT3(".fnt", "fnt", "fnt3") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new FNTBitmapFontExporter(3, o.getEncoding());
		}
	},
	FNT2(".fnt", "fnt2") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new FNTBitmapFontExporter(2, o.getEncoding());
		}
	},
	FONTX(".fnt", "dosv", "fontx", "fontx2") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			if (o.fontxDoubleByte) {
				String en = o.fontxDoubleByteEncoding;
				if (en == null || en.length() == 0) en = "CP943";
				return new FONTXBitmapFontExporter(en);
			} else {
				String en = o.encodingName;
				if (en == null || en.length() == 0) en = "CP437";
				return new FONTXBitmapFontExporter(EncodingList.instance().getGlyphList(en));
			}
		}
	},
	RB12(".fnt", "rb12") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new RockboxBitmapFontExporter(RockboxBitmapFontExporter.RB12);
		}
	},
	RB11(".fnt", "rb11") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new RockboxBitmapFontExporter(RockboxBitmapFontExporter.RB11);
		}
	},
	CYBIKO(".fnt", "cybiko") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new CybikoBitmapFontExporter(o.getEncoding());
		}
	},
	HMZK(".hmzk", "hmzk") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new HMZKBitmapFontExporter();
		}
	},
	
	// **** Add new formats above this line. ****
	
	SBF(".sbf", "sbf") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new SBFBitmapFontExporter(o.getEncoding());
		}
	},
	TOS(".ft", "tos", "ft") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new TOSBitmapFontExporter();
		}
	};
	
	public final String[] ids;
	public final String suffix;
	public final boolean macResFork;
	
	private BitmapOutputFormat(String suffix, String... ids) {
		this.ids = ids;
		this.suffix = suffix;
		this.macResFork = false;
	}
	
	private BitmapOutputFormat(String suffix, String id1, String id2, boolean macResFork) {
		this.ids = new String[]{id1, id2};
		this.suffix = suffix;
		this.macResFork = macResFork;
	}
	
	public abstract BitmapFontExporter createExporter(BitmapOutputOptions o);
	public void postProcess(File file) throws IOException {}
}
