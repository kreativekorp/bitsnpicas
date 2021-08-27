package com.kreative.bitsnpicas.edit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.FontExporter;
import com.kreative.bitsnpicas.FontImporter;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.bitsnpicas.exporter.KBnPBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.KBnPVectorFontExporter;
import com.kreative.bitsnpicas.geos.mover.GEOSMoverFrame;
import com.kreative.bitsnpicas.importer.BDFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.CybikoBitmapFontImporter;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FONTXBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FZXBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HMZKBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HexBitmapFontImporter;
import com.kreative.bitsnpicas.importer.KBnPBitmapFontImporter;
import com.kreative.bitsnpicas.importer.KBnPVectorFontImporter;
import com.kreative.bitsnpicas.importer.S10BitmapFontImporter;
import com.kreative.bitsnpicas.importer.SBFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SFDBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SRFontBitmapFontImporter;
import com.kreative.bitsnpicas.importer.U8MBitmapFontImporter;
import com.kreative.bitsnpicas.mover.MoverFrame;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public enum ImportFormat {
	KBITS(".kbits") {
		public FontImporter<?> createImporter() { return new KBnPBitmapFontImporter(); }
		public FontExporter<?> createExporter() { return new KBnPBitmapFontExporter(); }
	},
	KPCAS(".kpcas") {
		public FontImporter<?> createImporter() { return new KBnPVectorFontImporter(); }
		public FontExporter<?> createExporter() { return new KBnPVectorFontExporter(); }
	},
	SFD(".sfd") {
		public FontImporter<?> createImporter() { return new SFDBitmapFontImporter(); }
	},
	BDF(".bdf") {
		public FontImporter<?> createImporter() { return new BDFBitmapFontImporter(); }
	},
	SUIT(".suit", true) {
		public JFrame createOptionFrame(File file) throws IOException {
			return MoverFrame.forFile(file);
		}
	},
	DFONT(".dfont", false) {
		public JFrame createOptionFrame(File file) throws IOException {
			return MoverFrame.forFile(file);
		}
	},
	PNG(".png") {
		public FontImporter<?> createImporter() { return new SRFontBitmapFontImporter(); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new ImageBitmapFontImporterFrame(file);
		}
	},
	IMAGE(".jpg", ".jpeg", ".gif", ".bmp") {
		public JFrame createOptionFrame(File file) throws IOException {
			return new ImageBitmapFontImporterFrame(file);
		}
	},
	BINARY(".bin", ".rom") {
		public JFrame createOptionFrame(File file) throws IOException {
			return new BinaryBitmapFontImporterFrame(file);
		}
	},
	HEX(".hex") {
		public FontImporter<?> createImporter() { return new HexBitmapFontImporter(); }
	},
	CVT(".cvt") {
		public JFrame createOptionFrame(File file) throws IOException {
			return GEOSMoverFrame.forFile(file);
		}
	},
	FZX(".fzx") {
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("FZX PUA", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(EncodingTable encoding) {
					return new FZXBitmapFontImporter(encoding);
				}
			});
		}
	},
	U8M(".u8m") {
		public FontImporter<?> createImporter() { return new U8MBitmapFontImporter(); }
	},
	FONTX(".ftx") {
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("CP437", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(EncodingTable encoding) {
					return new FONTXBitmapFontImporter(encoding);
				}
			});
		}
	},
	CYBIKO(".cyf", ".fntz") {
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("Cybiko", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(EncodingTable encoding) {
					return new CybikoBitmapFontImporter(encoding);
				}
			});
		}
	},
	HMZK(".hmzk") {
		public FontImporter<?> createImporter() { return new HMZKBitmapFontImporter(); }
	},
	
	// **** Add new formats above this line. ****
	
	DSF(".dsf") {
		public FontImporter<?> createImporter() { return new DSFBitmapFontImporter(); }
	},
	SBF(".sbf") {
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("Kreative SuperLatin", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(EncodingTable encoding) {
					return new SBFBitmapFontImporter(encoding);
				}
			});
		}
	},
	S10(".s10") {
		public FontImporter<?> createImporter() { return new S10BitmapFontImporter(); }
	};
	
	public final String[] extensions;
	public final boolean macResFork;
	
	private ImportFormat(String... extensions) {
		this.extensions = extensions;
		this.macResFork = false;
	}
	
	private ImportFormat(String extension, boolean macResFork) {
		this.extensions = new String[]{extension};
		this.macResFork = macResFork;
	}
	
	public FontImporter<?> createImporter() { return null; }
	public FontExporter<?> createExporter() { return null; }
	public JFrame createOptionFrame(File file) throws IOException {
		return null;
	}
	
	public static ImportFormat forFile(File file) {
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
					// case 'R': return ROCKBOX;
				}
			} catch (IOException e) {
				return null;
			}
		}
		
		// Detect most file formats by file extension.
		for (ImportFormat format : values()) {
			for (String ext : format.extensions) {
				if (lname.endsWith(ext)) {
					return format;
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
