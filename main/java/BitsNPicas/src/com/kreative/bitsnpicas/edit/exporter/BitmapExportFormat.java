package com.kreative.bitsnpicas.edit.exporter;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.exporter.*;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public enum BitmapExportFormat {
	KBITS("Kbits (Kreative Bits'n'Picas 1.x)", ".kbits", "v1") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new KbitsBitmapFontExporter();
		}
	},
	TTF("TTF (TrueType)", ".ttf", "ttf") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			Dimension d = o.getPixelDimension();
			boolean exWinMtx = o.getExtendWinMetrics();
			return new TTFBitmapFontExporter(d.width, d.height, exWinMtx);
		}
	},
	OTB("OTB (OpenType Bitmap)", ".otb", "otb") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			boolean exWinMtx = o.getExtendWinMetrics();
			return new OTBBitmapFontExporter(exWinMtx);
		}
	},
	BDF("BDF (Bitmap Distribution Format)", ".bdf", "none") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new BDFBitmapFontExporter();
		}
	},
	PSF("PSF (PC Screen Font) (Uncompressed)", ".psf", "psf") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new PSFBitmapFontExporter(
				o.getPSFVersion(), o.getPSFLowEncoding(), o.getPSFHighEncoding(),
				o.getPSFUseLowEncoding(), o.getPSFUseHighEncoding(),
				o.getPSFUseAllGlyphs(), o.getPSFUnicodeTable(), false
			);
		}
	},
	PSFGZ("PSF (PC Screen Font) (Gzip Compressed)", ".psf.gz", "psf") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new PSFBitmapFontExporter(
				o.getPSFVersion(), o.getPSFLowEncoding(), o.getPSFHighEncoding(),
				o.getPSFUseLowEncoding(), o.getPSFUseHighEncoding(),
				o.getPSFUseAllGlyphs(), o.getPSFUnicodeTable(), true
			);
		}
	},
	SUIT("Mac OS Classic Font Suitcase (Resource Fork)", ".suit", "mac", "MacRoman", true) {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new NFNTBitmapFontExporter(
				o.getIDGenerator(),
				o.getPointSizeGenerator(),
				o.getSelectedEncoding()
			);
		}
		public void postProcess(File file) throws IOException {
			MacUtility.setTypeAndCreator(file, "FFIL", "DMOV");
		}
	},
	DFONT("Mac OS Classic Font Suitcase (Data Fork)", ".dfont", "mac", "MacRoman", false) {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new NFNTBitmapFontExporter(
				o.getIDGenerator(),
				o.getPointSizeGenerator(),
				o.getSelectedEncoding()
			);
		}
	},
	SFONT("PNG (SDL SFont)", ".png", "color") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new SFontBitmapFontExporter(o.getSelectedColor());
		}
	},
	RFONT("PNG (Kreative RFont)", ".png", "color") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new RFontBitmapFontExporter(o.getSelectedColor());
		}
	},
	HEX("Hex (GNU Unifont)", ".hex", "none") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new HexBitmapFontExporter();
		}
	},
	CVT("GEOS Font in Convert Wrapper", ".cvt", "geos") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new GEOSBitmapFontExporter(
				o.getIDGenerator(), o.getPointSizeGenerator(),
				o.getGEOSMega(), o.getGEOSKerning(), o.getGEOSUTF8()
			);
		}
	},
	FZX("FZX (ZX Spectrum)", ".fzx", "encoding", "FZX PUA") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new FZXBitmapFontExporter(o.getSelectedEncoding());
		}
	},
	U8M("U8/M (UTF-8 for Microcomputers)", ".u8m", "u8m", "U8/M PETSCII") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new U8MBitmapFontExporter(o.getLoadAddress(), o.getSelectedEncoding());
		}
	},
	FNT3("FNT (Windows 3.x)", ".fnt", "encoding", "CP1252") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new FNTBitmapFontExporter(3, o.getSelectedEncoding());
		}
	},
	FNT2("FNT (Windows 2.x)", ".fnt", "encoding", "CP1252") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new FNTBitmapFontExporter(2, o.getSelectedEncoding());
		}
	},
	FONTX("FONTX (DOS/V)", ".fnt", "fontx") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			if (o.getFONTXDoubleByte()) {
				String en = o.getFONTXDoubleByteEncoding();
				if (en == null || en.length() == 0) en = "CP943";
				return new FONTXBitmapFontExporter(en);
			} else {
				GlyphList et = o.getSelectedEncoding();
				if (et == null) et = EncodingList.instance().getGlyphList("CP437");
				return new FONTXBitmapFontExporter(et);
			}
		}
	},
	RB12("RB12 (Rockbox 2.3 or above)", ".fnt", "none") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new RockboxBitmapFontExporter(RockboxBitmapFontExporter.RB12);
		}
	},
	RB11("RB11 (Rockbox 2.2 or below and iPodLinux)", ".fnt", "none") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new RockboxBitmapFontExporter(RockboxBitmapFontExporter.RB11);
		}
	},
	CYBIKO("Cybiko", ".fnt", "encoding", "Cybiko") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new CybikoBitmapFontExporter(o.getSelectedEncoding());
		}
	},
	PLAYDATE("Playdate", ".fnt", "playdate") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new PlaydateBitmapFontExporter(o.getPlaydateSeparate());
		}
	},
	HMZK("HMZK (Mi Band 2)", ".hmzk", "none") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new HMZKBitmapFontExporter();
		}
	},
	
	// **** Add new formats above this line. ****
	
	SBF("SBF (Sabriel Font)", ".sbf", "encoding", "Kreative SuperLatin") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new SBFBitmapFontExporter(o.getSelectedEncoding());
		}
	},
	TOS("TOS Character Set", ".ft", "none") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new TOSBitmapFontExporter();
		}
	};
	
	public final String name;
	public final String suffix;
	public final String cardName;
	public final String defaultEncodingName;
	public final boolean macResFork;
	
	private BitmapExportFormat(String name, String suffix, String cardName) {
		this.name = name;
		this.suffix = suffix;
		this.cardName = cardName;
		this.defaultEncodingName = null;
		this.macResFork = false;
	}
	
	private BitmapExportFormat(String name, String suffix, String cardName, String defaultEncodingName) {
		this.name = name;
		this.suffix = suffix;
		this.cardName = cardName;
		this.defaultEncodingName = defaultEncodingName;
		this.macResFork = false;
	}
	
	private BitmapExportFormat(String name, String suffix, String cardName, String defaultEncodingName, boolean macResFork) {
		this.name = name;
		this.suffix = suffix;
		this.cardName = cardName;
		this.defaultEncodingName = defaultEncodingName;
		this.macResFork = macResFork;
	}
	
	public abstract BitmapFontExporter createExporter(BitmapExportOptions o);
	public void postProcess(File file) throws IOException {}
	
	public String toString() {
		return this.name;
	}
}
