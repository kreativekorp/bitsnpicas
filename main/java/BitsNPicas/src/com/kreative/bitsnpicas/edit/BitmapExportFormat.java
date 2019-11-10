package com.kreative.bitsnpicas.edit;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.exporter.BDFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.FZXBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.GEOSBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.HMZKBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.HexBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.NFNTBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.RFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SBFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TOSBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TTFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.U8MBitmapFontExporter;

public enum BitmapExportFormat {
	TTF("TTF (TrueType)", ".ttf", "pixel") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			Dimension d = o.getPixelDimension();
			return new TTFBitmapFontExporter(d.width, d.height);
		}
	},
	BDF("BDF (Bitmap Distribution Format)", ".bdf", "none") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new BDFBitmapFontExporter();
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
			String[] cmd = {"/usr/bin/SetFile", "-t", "FFIL", "-c", "DMOV", file.getAbsolutePath()};
			Runtime.getRuntime().exec(cmd);
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
				o.getIDGenerator(),
				o.getPointSizeGenerator()
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
