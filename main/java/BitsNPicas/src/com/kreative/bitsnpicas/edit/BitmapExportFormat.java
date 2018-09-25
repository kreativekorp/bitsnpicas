package com.kreative.bitsnpicas.edit;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.exporter.BDFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.FZXBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.HMZKBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.RFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SBFBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.SFontBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TOSBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.TTFBitmapFontExporter;

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
	SUIT("Mac OS Classic Font Suitcase (Resource Fork)", ".suit", "mac", true) {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return o.createNFNTExporter();
		}
		public void postProcess(File file) throws IOException {
			String[] cmd = {"/usr/bin/SetFile", "-t", "FFIL", "-c", "DMOV", file.getAbsolutePath()};
			Runtime.getRuntime().exec(cmd);
		}
	},
	DFONT("Mac OS Classic Font Suitcase (Data Fork)", ".dfont", "mac") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return o.createNFNTExporter();
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
	FZX("FZX (ZX Spectrum)", ".fzx", "none") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new FZXBitmapFontExporter();
		}
	},
	HMZK("HMZK (Mi Band 2)", ".hmzk", "none") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new HMZKBitmapFontExporter();
		}
	},
	
	// **** Add new formats above this line. ****
	
	SBF("SBF (Sabriel Font)", ".sbf", "none") {
		public BitmapFontExporter createExporter(BitmapExportOptions o) {
			return new SBFBitmapFontExporter();
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
	public final boolean macResFork;
	
	private BitmapExportFormat(String name, String suffix, String cardName) {
		this.name = name;
		this.suffix = suffix;
		this.cardName = cardName;
		this.macResFork = false;
	}
	
	private BitmapExportFormat(String name, String suffix, String cardName, boolean macResFork) {
		this.name = name;
		this.suffix = suffix;
		this.cardName = cardName;
		this.macResFork = macResFork;
	}
	
	public abstract BitmapFontExporter createExporter(BitmapExportOptions o);
	public void postProcess(File file) throws IOException {}
	
	public String toString() {
		return this.name;
	}
}
