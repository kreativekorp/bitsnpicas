package com.kreative.bitsnpicas.edit.importer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.FileProxy;
import com.kreative.bitsnpicas.FontExporter;
import com.kreative.bitsnpicas.FontImporter;
import com.kreative.bitsnpicas.exporter.KbitxBitmapFontExporter;
import com.kreative.bitsnpicas.exporter.KpcaxVectorFontExporter;
import com.kreative.bitsnpicas.geos.mover.GEOSMoverFrame;
import com.kreative.bitsnpicas.importer.*;
import com.kreative.bitsnpicas.mover.MoverFrame;
import com.kreative.unicode.data.GlyphList;

public enum ImportFormat {
	KBITX("Kbitx (Kreative Bits'n'Picas 2.x Bitmap Format)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".kbitx"); }
		public FontImporter<?> createImporter() { return new KbitxBitmapFontImporter(); }
		public FontExporter<?> createExporter() { return new KbitxBitmapFontExporter(); }
	},
	KPCAX("Kpcax (Kreative Bits'n'Picas 2.x Vector Format)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".kpcax"); }
		public FontImporter<?> createImporter() { return new KpcaxVectorFontImporter(); }
		public FontExporter<?> createExporter() { return new KpcaxVectorFontExporter(); }
	},
	KBITS("Kbits (Kreative Bits'n'Picas 1.x Bitmap Format)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".kbits"); }
		public FontImporter<?> createImporter() { return new KbitsBitmapFontImporter(); }
	},
	KPCAS("Kpcas (Kreative Bits'n'Picas 1.x Vector Format)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".kpcas"); }
		public FontImporter<?> createImporter() { return new KpcasVectorFontImporter(); }
	},
	SFD("SFD (FontForge Spline Font Database)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".sfd"); }
		public FontImporter<?> createImporter() { return new SFDBitmapFontImporter(); }
	},
	BDF("BDF (Bitmap Distribution Format)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".bdf"); }
		public FontImporter<?> createImporter() { return new BDFBitmapFontImporter(); }
	},
	PSF("PSF (PC Screen Font) (Uncompressed)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".psf", ".psfu"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new PSFEncodingSelectionFrame(file, new PSFEncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList low, GlyphList high, int puaBase) {
					return new PSFBitmapFontImporter(low, high, puaBase, false);
				}
			});
		}
	},
	PSFGZ("PSF (PC Screen Font) (Gzip Compressed)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".psf.gz", ".psfu.gz"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new PSFEncodingSelectionFrame(file, new PSFEncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList low, GlyphList high, int puaBase) {
					return new PSFBitmapFontImporter(low, high, puaBase, true);
				}
			});
		}
	},
	SUIT("Mac OS Classic Font Suitcase (Resource Fork)", true, true) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".suit"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return MoverFrame.forFile(file);
		}
	},
	DFONT("Mac OS Classic Font Suitcase (Data Fork)", true, false) {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".dfont"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return MoverFrame.forFile(file);
		}
	},
	NFNT("Mac OS Classic Font Resource in Data Fork") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".nfnt"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("MacRoman", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new NFNTBitmapFontImporter.FlatFile(encoding);
				}
			});
		}
	},
	SRFONT("PNG (SDL SFont or Kreative RFont)") {
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
	HEX("Hex (GNU Unifont)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".hex"); }
		public FontImporter<?> createImporter() { return new HexBitmapFontImporter(); }
	},
	CVT("GEOS Font in Convert Wrapper") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".cvt"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return GEOSMoverFrame.forFile(file);
		}
	},
	FZX("FZX (ZX Spectrum)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".fzx"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("FZX PUA", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new FZXBitmapFontImporter(encoding);
				}
			});
		}
	},
	U8M("U8/M (UTF-8 for Microcomputers)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".u8m"); }
		public FontImporter<?> createImporter() { return new U8MBitmapFontImporter(); }
	},
	AMIGA("Amiga Font File and Directory") {
		public boolean recognize(FileProxy fp) {
			return (
				fp.hasExtension(".font") &&
				(fp.startsWith(0x0F, 0x00) || fp.startsWith(0x0F, 0x02))
			);
		}
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("ISO-8859-1", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new AmigaBitmapFontImporter.ContentsFile(encoding);
				}
			});
		}
	},
	FNT("FNT (Windows 2.x or 3.x)") {
		public boolean recognize(FileProxy fp) {
			if (!fp.hasExtension(".fnt")) return false;
			byte[] b = fp.getStartBytes(2);
			return b != null && b[0] == 0 && b[1] >= 1 && b[1] <= 3;
		}
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("CP1252", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new FNTBitmapFontImporter(encoding);
				}
			});
		}
	},
	FONTX("FONTX (DOS/V)") {
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
	MGTK("MGTK (Apple II MouseGraphics ToolKit)") {
		public boolean recognize(FileProxy fp) {
			return (
				fp.hasExtension(".mgf", ".mpf", ".fnt") &&
				(fp.startsWith(0) || fp.startsWith(0x80))
			);
		}
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("MouseDesk", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new MGTKBitmapFontImporter(encoding);
				}
			});
		}
	},
	ROCKBOX("RB11 or RB12 (Rockbox or iPodLinux)") {
		public boolean recognize(FileProxy fp) {
			return fp.hasExtension(".rbf", ".rb11", ".rb12", ".fnt") && fp.startsWith('R');
		}
		public FontImporter<?> createImporter() {
			return new RockboxBitmapFontImporter();
		}
	},
	CYBIKO("Cybiko") {
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
	PLAYDATE("Playdate") {
		public boolean recognize(FileProxy fp) {
			return PlaydateBitmapFontImporter.canImportFont(fp);
		}
		public FontImporter<?> createImporter() {
			return new PlaydateBitmapFontImporter();
		}
	},
	HRCG("HRCG (Apple II Hi-Res Character Generator)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".set"); }
		public FontImporter<?> createImporter() { return new HRCGBitmapFontImporter(); }
	},
	HMZK("HMZK (Mi Band 2)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".hmzk"); }
		public FontImporter<?> createImporter() { return new HMZKBitmapFontImporter(); }
	},
	
	// **** Add new formats above this line. ****
	
	DSF("DSF (DOSStart! Font)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".dsf"); }
		public FontImporter<?> createImporter() { return new DSFBitmapFontImporter(); }
	},
	SBF("SBF (Sabriel Font)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".sbf"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("Kreative SuperLatin", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new SBFBitmapFontImporter(encoding);
				}
			});
		}
	},
	S10("S10 (SabineOS Character Set)") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".s10"); }
		public FontImporter<?> createImporter() { return new S10BitmapFontImporter(); }
	},
	DMOV("Mac OS Classic Font Suitcase (Resource Fork)", false, true) {
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
	BINARY("Binary or ROM File") {
		public boolean recognize(FileProxy fp) { return fp.hasExtension(".bin", ".rom"); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new BinaryBitmapFontImporterFrame(file);
		}
	},
	IMAGE("Image File") {
		public boolean recognize(FileProxy fp) { return fp.isImage(); }
		public JFrame createOptionFrame(File file) throws IOException {
			return new ImageBitmapFontImporterFrame(file);
		}
	},
	NFNT_NOEXT("Mac OS Classic Font Resource in Data Fork", false, false) {
		public boolean recognize(FileProxy fp) {
			return fp.startsWith(0x90, 0x00);
		}
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("MacRoman", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new NFNTBitmapFontImporter.FlatFile(encoding);
				}
			});
		}
	},
	AMIGA_NOEXT("Amiga Font File and Directory", false, false) {
		public boolean recognize(FileProxy fp) {
			return fp.startsWith(0x00, 0x00, 0x03, 0xF3);
		}
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("ISO-8859-1", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new AmigaBitmapFontImporter.DescriptorFile(encoding);
				}
			});
		}
	},
	MGTK_NOEXT("MGTK (Apple II MouseGraphics ToolKit)", false, false) {
		public boolean recognize(FileProxy fp) {
			return fp.startsWith(0) || fp.startsWith(0x80);
		}
		public JFrame createOptionFrame(File file) throws IOException {
			return new EncodingSelectionFrame("MouseDesk", file, new EncodingSelectionImporter() {
				public FontImporter<?> createImporter(GlyphList encoding) {
					return new MGTKBitmapFontImporter(encoding);
				}
			});
		}
	};
	
	public final String name;
	public final boolean listed;
	public final boolean macResFork;
	
	private ImportFormat(String name) {
		this.name = name;
		this.listed = true;
		this.macResFork = false;
	}
	
	private ImportFormat(String name, boolean listed, boolean macResFork) {
		this.name = name;
		this.listed = listed;
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
	
	public static ImportFormat[] listedValues() {
		int i = 0;
		ImportFormat[] a = ImportFormat.values();
		for (ImportFormat f : a) if (f.listed) a[i++] = f;
		ImportFormat[] b = new ImportFormat[i];
		while (i > 0) { i--; b[i] = a[i]; }
		return b;
	}
	
	public String toString() {
		return this.name;
	}
}
