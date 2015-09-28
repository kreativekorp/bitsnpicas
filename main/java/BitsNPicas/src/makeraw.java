import java.io.*;

import com.kreative.bitsnpicas.*;
import com.kreative.bitsnpicas.exporter.RawBitmapFontExporter;
import com.kreative.bitsnpicas.importer.DSFBitmapFontImporter;
import com.kreative.bitsnpicas.importer.S10BitmapFontImporter;
import com.kreative.bitsnpicas.importer.SFDBitmapFontImporter;
import com.kreative.bitsnpicas.importer.SRFontBitmapFontImporter;
import com.kreative.bitsnpicas.transformer.BoldBitmapFontGlyphTransformer;

public class makeraw {
	private static String search = null, replace = null;
	private static boolean embolden = false;
	
	public static void main(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("-")) {
				if (arg.startsWith("-s") || arg.startsWith("-f")) {
					search = arg.substring(2);
				}
				else if (arg.startsWith("-r")) {
					replace = arg.substring(2);
				}
				else if (arg.startsWith("-b")) {
					embolden = true;
				}
			} else try {
				System.out.print(arg+"...");
				if (arg.toLowerCase().endsWith(".sfd")) {
					BitmapFont[] myFonts = new SFDBitmapFontImporter().importFont(new File(arg));
					convert(myFonts, Font.NAME_POSTSCRIPT);
					System.out.println(" done.");
				}
				else if (arg.toLowerCase().endsWith(".s10")) {
					BitmapFont[] myFonts = new S10BitmapFontImporter().importFont(new File(arg));
					convert(myFonts, Font.NAME_FAMILY_AND_STYLE);
					System.out.println(" done.");
				}
				else if (arg.toLowerCase().endsWith(".png")) {
					BitmapFont[] myFonts = new SRFontBitmapFontImporter().importFont(new File(arg));
					convert(myFonts, Font.NAME_FAMILY_AND_STYLE);
					System.out.println(" done.");
				}
				else if (arg.toLowerCase().endsWith(".dsf")) {
					BitmapFont[] myFonts = new DSFBitmapFontImporter().importFont(new File(arg));
					convert(myFonts, Font.NAME_FAMILY);
					System.out.println(" done.");
				}
				else {
					System.out.println(" unknown type.");
				}
			} catch (IOException e) {
				System.out.println(" could not convert.");
			}
		}
	}
	
	public static void convert(BitmapFont[] myFonts, int nameType) throws IOException {
		if (search != null && search.length() > 0 && replace != null) {
			for (BitmapFont myFont : myFonts) {
				int[] nt = myFont.nameTypes();
				for (int n : nt) {
					String s = myFont.getName(n);
					if (s.contains(search)) {
						myFont.setName(n, s.replaceAll(search, replace));
					}
				}
			}
		}
		for (BitmapFont myFont : myFonts) {
			if (embolden) {
				myFont.transform(new BoldBitmapFontGlyphTransformer());
				myFont.setName(Font.NAME_STYLE, "Bold");
			}
			myFont.autoFillNames();
			File out = makefile(myFont.getName(nameType), ".ft");
			new RawBitmapFontExporter().exportFontToFile(myFont, out);
		}
	}
	
	public static File makefile(String basename, String ext) {
		if (!ext.startsWith(".")) ext = "." + ext;
		File f = new File(basename + ext);
		if (f.exists()) {
			int i = 2;
			do {
				f = new File(basename + "-" + (i++) + ext);
			} while (f.exists());
		}
		return f;
	}
}
