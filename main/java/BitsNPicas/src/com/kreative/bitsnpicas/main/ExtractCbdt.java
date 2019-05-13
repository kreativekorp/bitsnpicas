package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.kreative.bitsnpicas.truetype.CbdtEntry;
import com.kreative.bitsnpicas.truetype.CbdtEntryFormat17;
import com.kreative.bitsnpicas.truetype.CbdtEntryFormat18;
import com.kreative.bitsnpicas.truetype.CbdtEntryFormat19;
import com.kreative.bitsnpicas.truetype.EbdtComponent;
import com.kreative.bitsnpicas.truetype.EbdtEntry;
import com.kreative.bitsnpicas.truetype.EbdtEntryFormat1;
import com.kreative.bitsnpicas.truetype.EbdtEntryFormat2;
import com.kreative.bitsnpicas.truetype.EbdtEntryFormat5;
import com.kreative.bitsnpicas.truetype.EbdtEntryFormat6;
import com.kreative.bitsnpicas.truetype.EbdtEntryFormat7;
import com.kreative.bitsnpicas.truetype.EbdtEntryFormat8;
import com.kreative.bitsnpicas.truetype.EbdtEntryFormat9;
import com.kreative.bitsnpicas.truetype.EbdtTable;
import com.kreative.bitsnpicas.truetype.EblcBitmapSize;
import com.kreative.bitsnpicas.truetype.EblcGlyphIdOffsetPair;
import com.kreative.bitsnpicas.truetype.EblcIndexSubtable;
import com.kreative.bitsnpicas.truetype.EblcIndexSubtable1;
import com.kreative.bitsnpicas.truetype.EblcIndexSubtable2;
import com.kreative.bitsnpicas.truetype.EblcIndexSubtable3;
import com.kreative.bitsnpicas.truetype.EblcIndexSubtable4;
import com.kreative.bitsnpicas.truetype.EblcIndexSubtable5;
import com.kreative.bitsnpicas.truetype.EblcTable;
import com.kreative.bitsnpicas.truetype.HheaTable;
import com.kreative.bitsnpicas.truetype.Os2Table;
import com.kreative.bitsnpicas.truetype.SbitBigGlyphMetrics;
import com.kreative.bitsnpicas.truetype.SbitLineMetrics;
import com.kreative.bitsnpicas.truetype.SbitSmallGlyphMetrics;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class ExtractCbdt {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		for (String arg : args) {
			File file = new File(arg);
			System.out.print("Processing " + file.getAbsolutePath() + "... ");
			try {
				byte[] data = new byte[(int)file.length()];
				FileInputStream in = new FileInputStream(file);
				in.read(data);
				in.close();
				TrueTypeFile ttf = new TrueTypeFile();
				ttf.decompile(data);
				EbdtTable cbdt = (EbdtTable)ttf.getByTableName("CBDT");
				EblcTable cblc = (EblcTable)ttf.getByTableName("CBLC");
				if (cbdt == null || cblc == null) {
					System.out.println("no CBDT/CBLC table found.");
				} else {
					File outputRoot = new File(file.getParent(), file.getName() + ".cbdt.d");
					if (!outputRoot.exists()) outputRoot.mkdir();
					
					File debug = new File(outputRoot, "fontinfo.txt");
					FileOutputStream debugOS = new FileOutputStream(debug);
					OutputStreamWriter debugWR = new OutputStreamWriter(debugOS, "UTF-8");
					PrintWriter debugPR = new PrintWriter(debugWR, true);
					HheaTable hhea = (HheaTable)ttf.getByTableName("hhea");
					if (hhea != null) {
						debugPR.println("ascent: " + hhea.ascent);
						debugPR.println("descent: " + hhea.descent);
						debugPR.println("lineGap: " + hhea.lineGap);
					}
					Os2Table os2 = (Os2Table)ttf.getByTableName("OS/2");
					if (os2 != null) {
						debugPR.println("winAscent: " + os2.winAscent);
						debugPR.println("winDescent: " + os2.winDescent);
						debugPR.println("typoAscent: " + os2.typoAscent);
						debugPR.println("typoDescent: " + os2.typoDescent);
						debugPR.println("typoLineGap: " + os2.typoLineGap);
						debugPR.println("capHeight: " + os2.capHeight);
						debugPR.println("xHeight: " + os2.xHeight);
						debugPR.println("vendorID: " + os2.getVendorIDString());
					}
					debugPR.flush();
					debugPR.close();
					
					for (int i = 0, n = cblc.size(); i < n; i++) {
						EblcBitmapSize ebs = cblc.get(i);
						String ebsName = "0000" + i;
						ebsName = ebsName.substring(ebsName.length() - 4);
						File ebsRoot = new File(outputRoot, ebsName);
						if (!ebsRoot.exists()) ebsRoot.mkdir();
						
						File meta = new File(ebsRoot, "metadata.txt");
						FileOutputStream metaOS = new FileOutputStream(meta);
						OutputStreamWriter metaWR = new OutputStreamWriter(metaOS, "UTF-8");
						PrintWriter metaPR = new PrintWriter(metaWR, true);
						
						metaPR.println("indexSubTableArrayOffset: " + ebs.indexSubTableArrayOffset);
						metaPR.println("indexTablesSize: " + ebs.indexTablesSize);
						metaPR.println("numberOfIndexSubTables: " + ebs.numberOfIndexSubTables);
						metaPR.println("colorRef: " + ebs.colorRef);
						print(metaPR, "hori", ebs.hori);
						print(metaPR, "vert", ebs.vert);
						metaPR.println("startGlyphIndex: " + ebs.startGlyphIndex);
						metaPR.println("endGlyphIndex: " + ebs.endGlyphIndex);
						metaPR.println("ppemX: " + ebs.ppemX);
						metaPR.println("ppemY: " + ebs.ppemY);
						metaPR.println("bitDepth: " + ebs.bitDepth);
						metaPR.println("flags: " + ebs.flags);
						
						int index = 0;
						for (EblcIndexSubtable st : ebs) {
							metaPR.println();
							metaPR.println("indexSubTable: " + index++);
							metaPR.println("firstGlyphIndex: " + st.header.firstGlyphIndex);
							metaPR.println("lastGlyphIndex: " + st.header.lastGlyphIndex);
							metaPR.println("additionalOffsetToIndexSubtable: " + st.header.additionalOffsetToIndexSubtable);
							metaPR.println("indexFormat: " + st.header.indexFormat);
							metaPR.println("imageFormat: " + st.header.imageFormat);
							metaPR.println("imageDataOffset: " + st.header.imageDataOffset);
							if (st instanceof EblcIndexSubtable1) {
								EblcIndexSubtable1 st1 = (EblcIndexSubtable1)st;
								StringBuffer sb = new StringBuffer("offsetArray:");
								for (int offset : st1) { sb.append(" "); sb.append(offset); }
								metaPR.println(sb);
							}
							if (st instanceof EblcIndexSubtable2) {
								EblcIndexSubtable2 st2 = (EblcIndexSubtable2)st;
								metaPR.println("imageSize: " + st2.imageSize);
								print(metaPR, st2.bigMetrics);
							}
							if (st instanceof EblcIndexSubtable3) {
								EblcIndexSubtable3 st3 = (EblcIndexSubtable3)st;
								StringBuffer sb = new StringBuffer("offsetArray:");
								for (int offset : st3) { sb.append(" "); sb.append(offset); }
								metaPR.println(sb);
							}
							if (st instanceof EblcIndexSubtable4) {
								EblcIndexSubtable4 st4 = (EblcIndexSubtable4)st;
								StringBuffer sb = new StringBuffer("glyphArray:");
								for (EblcGlyphIdOffsetPair pair : st4) {
									sb.append(" "); sb.append(pair.glyphID);
									sb.append(","); sb.append(pair.offset);
								}
								metaPR.println(sb);
							}
							if (st instanceof EblcIndexSubtable5) {
								EblcIndexSubtable5 st5 = (EblcIndexSubtable5)st;
								metaPR.println("imageSize: " + st5.imageSize);
								print(metaPR, st5.bigMetrics);
							}
							
							EblcGlyphIdOffsetPair[] pairs = st.getGlyphIdOffsetPairs();
							for (int j = 0, m = pairs.length - 1; j < m; j++) {
								EblcGlyphIdOffsetPair pair = pairs[j];
								EbdtEntry e = cbdt.get(pair.offset);
								
								metaPR.println();
								metaPR.println("glyph: " + pair.glyphID);
								metaPR.println("offset: " + pair.offset);
								if (e instanceof EbdtEntryFormat1) {
									EbdtEntryFormat1 e1 = (EbdtEntryFormat1)e;
									print(metaPR, e1.smallMetrics);
									metaPR.println("imageData: <" + e1.imageData.length + "b>");
								}
								if (e instanceof EbdtEntryFormat2) {
									EbdtEntryFormat2 e2 = (EbdtEntryFormat2)e;
									print(metaPR, e2.smallMetrics);
									metaPR.println("imageData: <" + e2.imageData.length + "b>");
								}
								if (e instanceof EbdtEntryFormat5) {
									EbdtEntryFormat5 e5 = (EbdtEntryFormat5)e;
									metaPR.println("imageData: <" + e5.imageData.length + "b>");
								}
								if (e instanceof EbdtEntryFormat6) {
									EbdtEntryFormat6 e6 = (EbdtEntryFormat6)e;
									print(metaPR, e6.bigMetrics);
									metaPR.println("imageData: <" + e6.imageData.length + "b>");
								}
								if (e instanceof EbdtEntryFormat7) {
									EbdtEntryFormat7 e7 = (EbdtEntryFormat7)e;
									print(metaPR, e7.bigMetrics);
									metaPR.println("imageData: <" + e7.imageData.length + "b>");
								}
								if (e instanceof EbdtEntryFormat8) {
									EbdtEntryFormat8 e8 = (EbdtEntryFormat8)e;
									print(metaPR, e8.smallMetrics);
									StringBuffer sb = new StringBuffer("components:");
									for (EbdtComponent c : e8) {
										sb.append(" "); sb.append(c.glyphID);
										sb.append(","); sb.append(c.xOffset);
										sb.append(","); sb.append(c.yOffset);
									}
									metaPR.println(sb);
								}
								if (e instanceof EbdtEntryFormat9) {
									EbdtEntryFormat9 e9 = (EbdtEntryFormat9)e;
									print(metaPR, e9.bigMetrics);
									StringBuffer sb = new StringBuffer("components:");
									for (EbdtComponent c : e9) {
										sb.append(" "); sb.append(c.glyphID);
										sb.append(","); sb.append(c.xOffset);
										sb.append(","); sb.append(c.yOffset);
									}
									metaPR.println(sb);
								}
								if (e instanceof CbdtEntryFormat17) {
									CbdtEntryFormat17 e17 = (CbdtEntryFormat17)e;
									print(metaPR, e17.glyphMetrics);
									metaPR.println("imageData: <" + e17.imageData.length + "b>");
								}
								if (e instanceof CbdtEntryFormat18) {
									CbdtEntryFormat18 e18 = (CbdtEntryFormat18)e;
									print(metaPR, e18.glyphMetrics);
									metaPR.println("imageData: <" + e18.imageData.length + "b>");
								}
								if (e instanceof CbdtEntryFormat19) {
									CbdtEntryFormat19 e19 = (CbdtEntryFormat19)e;
									metaPR.println("imageData: <" + e19.imageData.length + "b>");
								}
								metaPR.println("endGlyph");
								
								if (e instanceof CbdtEntry) {
									File imageFile = new File(ebsRoot, "glyph_" + pair.glyphID + ".png");
									FileOutputStream imageOS = new FileOutputStream(imageFile);
									imageOS.write(((CbdtEntry)e).imageData);
									imageOS.flush(); imageOS.close();
								}
							}
							
							metaPR.println("endIndexSubTable");
						}
						
						metaPR.flush();
						metaPR.close();
					}
					System.out.println("done.");
				}
			} catch (Exception e) {
				System.out.println("failed (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ").");
			}
		}
	}
	
	private static void print(PrintWriter pr, String prefix, SbitLineMetrics metrics) {
		pr.println(prefix + "Ascender: " + metrics.ascender);
		pr.println(prefix + "Descender: " + metrics.descender);
		pr.println(prefix + "WidthMax: " + metrics.widthMax);
		pr.println(prefix + "CaretSlopeNumerator: " + metrics.caretSlopeNumerator);
		pr.println(prefix + "CaretSlopeDenominator: " + metrics.caretSlopeDenominator);
		pr.println(prefix + "CaretOffset: " + metrics.caretOffset);
		pr.println(prefix + "MinOriginSB: " + metrics.minOriginSB);
		pr.println(prefix + "MinAdvanceSB: " + metrics.minAdvanceSB);
		pr.println(prefix + "MaxBeforeBL: " + metrics.maxBeforeBL);
		pr.println(prefix + "MinAfterBL: " + metrics.minAfterBL);
	}
	
	private static void print(PrintWriter pr, SbitSmallGlyphMetrics smallMetrics) {
		pr.println("height: " + smallMetrics.height);
		pr.println("width: " + smallMetrics.width);
		pr.println("bearingX: " + smallMetrics.bearingX);
		pr.println("bearingY: " + smallMetrics.bearingY);
		pr.println("advance: " + smallMetrics.advance);
	}
	
	private static void print(PrintWriter pr, SbitBigGlyphMetrics bigMetrics) {
		pr.println("height: " + bigMetrics.height);
		pr.println("width: " + bigMetrics.width);
		pr.println("horiBearingX: " + bigMetrics.horiBearingX);
		pr.println("horiBearingY: " + bigMetrics.horiBearingY);
		pr.println("horiAdvance: " + bigMetrics.horiAdvance);
		pr.println("vertBearingX: " + bigMetrics.vertBearingX);
		pr.println("vertBearingY: " + bigMetrics.vertBearingY);
		pr.println("vertAdvance: " + bigMetrics.vertAdvance);
	}
}
