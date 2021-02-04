package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.IOException;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.exporter.BDFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.CybikoBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.FZXBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.GEOSBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.HMZKBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.HexBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.KBnPBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.NFNTBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.RFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SBFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TOSBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TTFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.U8MBitmapFontExporter;
import com.kreative.bitsnpicas.unicode.EncodingList;

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
	BDF(".bdf", "bdf") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new BDFBitmapFontExporter();
		}
	},
	SUIT(".suit", "nfnt", "suit", true) {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			o.idgen.setRange(128, 32768);
			o.sizegen.setRange(4, 127);
			o.sizegen.setPointSizes(9, 10, 12, 14, 18, 24, 36, 48, 72);
			return new NFNTBitmapFontExporter(
				o.idgen, o.sizegen,
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
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
			return new NFNTBitmapFontExporter(
				o.idgen, o.sizegen,
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
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
			return new FZXBitmapFontExporter(
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
		}
	},
	U8M(".u8m", "u8m") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new U8MBitmapFontExporter(
				o.u8mLoadAddress,
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
		}
	},
	CYBIKO(".fnt", "cybiko") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new CybikoBitmapFontExporter(
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
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
			return new SBFBitmapFontExporter(
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
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
