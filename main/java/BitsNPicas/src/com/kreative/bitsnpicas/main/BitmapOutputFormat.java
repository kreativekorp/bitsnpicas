package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.IOException;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.exporter.BDFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.FZXBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.HMZKBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.HexBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.KBnPBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.NFNTBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.RFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SBFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TOSBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TTFBitmapFontExporter;
import com.kreative.bitsnpicas.unicode.EncodingList;

public enum BitmapOutputFormat {
	KBITS(".kbits", "kbits", "kbnp") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new KBnPBitmapFontExporter();
		}
	},
	TTF(".ttf", "ttf", "truetype") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new TTFBitmapFontExporter(o.xSize, o.ySize);
		}
	},
	BDF(".bdf", "bdf") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new BDFBitmapFontExporter();
		}
	},
	SUIT(".suit", "nfnt", "suit", true) {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new NFNTBitmapFontExporter(
				o.macID, o.macSize, o.macSnapSize,
				(o.encodingName == null) ? null :
				EncodingList.instance().get(o.encodingName)
			);
		}
		public void postProcess(File file) throws IOException {
			String[] cmd = {"/usr/bin/SetFile", "-t", "FFIL", "-c", "DMOV", file.getAbsolutePath()};
			Runtime.getRuntime().exec(cmd);
		}
	},
	DFONT(".dfont", "dfont") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new NFNTBitmapFontExporter(
				o.macID, o.macSize, o.macSnapSize,
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
	FZX(".fzx", "fzx") {
		public BitmapFontExporter createExporter(BitmapOutputOptions o) {
			return new FZXBitmapFontExporter(
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
