package com.kreative.bitsnpicas.edit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.FileProxy;
import com.kreative.bitsnpicas.FontExporter;
import com.kreative.bitsnpicas.FontImporter;
import com.kreative.bitsnpicas.exporter.KbitsBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.KpcasVectorFontExporter;
import com.kreative.bitsnpicas.geos.mover.GEOSMoverFrame;
import com.kreative.bitsnpicas.importer.BDFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.CybikoBitmapFontImporter;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FNTBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FONTXBitmapFontImporter;
import com.kreative.bitsnpicas.importer.FZXBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HMZKBitmapFontImporter;
import com.kreative.bitsnpicas.importer.HexBitmapFontImporter;
import com.kreative.bitsnpicas.importer.KbitsBitmapFontImporter;
import com.kreative.bitsnpicas.importer.KpcasVectorFontImporter;
import com.kreative.bitsnpicas.importer.PSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.PlaydateBitmapFontImporter;
import com.kreative.bitsnpicas.importer.RockboxBitmapFontImporter;
import com.kreative.bitsnpicas.importer.S10BitmapFontImporter;
import com.kreative.bitsnpicas.importer.SBFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SFDBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SRFontBitmapFontImporter;
import com.kreative.bitsnpicas.importer.U8MBitmapFontImporter;
import com.kreative.bitsnpicas.mover.MoverFrame;
import com.kreative.unicode.data.GlyphList;

public enum ImportFormat {
	KBITS {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".kbits"); }
		public FontImporter<?> createImporter() { return new KbitsBitmapFontImporter(); }
		public FontExporter<?> createExporter() { return new KbitsBitmapFontExporter(); }
	},
	KPCAS {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".kpcas"); }
		public FontImporter<?> createImporter() { return new KpcasVectorFontImporter(); }
		public FontExporter<?> createExporter() { return new KpcasVectorFontExporter(); }
	},
	SFD {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".sfd"); }
		public FontImporter<?> createImporter() { return new SFDBitmapFontImporter(); }
	},
	BDF {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".bdf"); }
		public FontImporter<?> createImporter() { return new BDFBitmapFontImporter(); }
	},
	PSF {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".psf", ".psfu"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new PSFEncodingSelectionFrame(file, new PSFEncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList low, GlyphList high, int puaBase) {
					return new PSFBitmapFontImporter(low, high, puaBase, false);
				}
			});
		}
	},
	PSFGZ {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".psf.gz", ".psfu.gz"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new PSFEncodingSelectionFrame(file, new PSFEncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList low, GlyphList high, int puaBase) {
					return new PSFBitmapFontImporter(low, high, puaBase, true);
				}
			});
		}
	},
	SUIT(true) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".suit"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return MoverFrame.forFile(file);
		}
	},
	DFONT(false) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".dfont"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return MoverFrame.forFile(file);
		}
	},
	SRFONT {
		public boolean recognize(FileProxy fp) {
			return (
				fp.hasExtension(".png") && fp.isImage() &&
				new SRFontBitmapFontImporter().canImportFont(fp.getImage())
			);
		}
		public FontImporter<?> createImporter() {
			return new SRFontBitmapFontImporter();
		}
	},
	HEX {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".hex"); }
		public FontImporter<?> createImporter() { return new HexBitmapFontImporter(); }
	},
	CVT {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".cvt"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return GEOSMoverFrame.forFile(file);
		}
	},
	FZX {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".fzx"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("FZX PUA", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new FZXBitmapFontImporter(encoding);
				}
			});
		}
	},
	U8M {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".u8m"); }
		public FontImporter<?> createImporter() { return new U8MBitmapFontImporter(); }
	},
	FNT {
		public boolean recognize(FileProxy fp) {
			return fp.hasExtension(".fnt") && fp.startsWith(0);
		}
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("CP1252", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new FNTBitmapFontImporter(encoding);
				}
			});
		}
	},
	FONTX {
		public boolean recognize(FileProxy fp) {
			return fp.hasExtension(".ftx", ".fnt") && fp.startsWith('F');
		}
		public JFrame createOptionFrame(File file) throws IOException {
			String dben = Charset.forName("CP943").displayName();
			return new DualEncodingSelectionFrame("CP437", dben, file, new DualEncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList sbenc, String dbenc) {
					return new FONTXBitmapFontImporter(sbenc, dbenc);
				}
			});
		}
	},
	ROCKBOX {
		public boolean recognize(FileProxy fp) {
			return fp.hasExtension(".rbf", ".rb11", ".rb12", ".fnt") && fp.startsWith('R');
		}
		public FontImporter<?> createImporter() {
			return new RockboxBitmapFontImporter();
		}
	},
	CYBIKO {
		public boolean recognize(FileProxy fp) {
			return fp.hasExtension(".cyf", ".fntz", ".fnty", ".fnt") && fp.startsWith(1);
		}
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("Cybiko", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new CybikoBitmapFontImporter(encoding);
				}
			});
		}
	},
	PLAYDATE {
		public boolean recognize(FileProxy fp) {
			return PlaydateBitmapFontImporter.canImportFont(fp);
		}
		public FontImporter<?> createImporter() {
			return new PlaydateBitmapFontImporter();
		}
	},
	HMZK {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".hmzk"); }
		public FontImporter<?> createImporter() { return new HMZKBitmapFontImporter(); }
	},
	
	// **** Add new formats above this line. ****
	
	DSF {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".dsf"); }
		public FontImporter<?> createImporter() { return new DSFBitmapFontImporter(); }
	},
	SBF {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".sbf"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("Kreative SuperLatin", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new SBFBitmapFontImporter(encoding);
				}
			});
		}
	},
	S10 {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".s10"); }
		public FontImporter<?> createImporter() { return new S10BitmapFontImporter(); }
	},
	DMOV(true) {
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
		public JFrame createOptionFrame(File file) throws IOException {
			return MoverFrame.forFile(file);
		}
	},
	BINARY {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".bin", ".rom"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new BinaryBitmapFontImporterFrame(file);
		}
	},
	IMAGE {
		public boolean recognize(FileProxy fp) { return fp.isImage(); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new ImageBitmapFontImporterFrame(file);
		}
	};
	
	public final boolean macResFork;
	
	private ImportFormat() {
		this.macResFork = false;
	}
	
	private ImportFormat(boolean macResFork) {
		this.macResFork = macResFork;
	}
	
	public abstract boolean recognize(FileProxy fp);
	public FontImporter<?> createImporter() { return null; }
	public FontExporter<?> createExporter() { return null; }
	public JFrame createOptionFrame(File file) throws IOException {
		return null;
	}
	
	public static ImportFormat forFile(File file) {
		FileProxy fp = new FileProxy(file);
		for (ImportFormat f : values()) {
			if (f.recognize(fp)) {
				return f;
			}
		}
		return null;
	}
}
