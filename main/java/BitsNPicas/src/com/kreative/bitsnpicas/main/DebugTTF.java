package com.kreative.bitsnpicas.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.kreative.bitsnpicas.truetype.HeadTable;
import com.kreative.bitsnpicas.truetype.HheaTable;
import com.kreative.bitsnpicas.truetype.MaxpTable;
import com.kreative.bitsnpicas.truetype.Os2Table;
import com.kreative.bitsnpicas.truetype.TrueTypeFile;

public class DebugTTF {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				// Open file
				File file = new File(arg);
				FileInputStream in = new FileInputStream(file);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				
				// Read file
				byte[] buf = new byte[1048576]; int len;
				while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
				
				// Close file
				out.flush();
				out.close();
				in.close();
				
				// Decompile
				TrueTypeFile ttf = new TrueTypeFile();
				ttf.decompile(out.toByteArray());
				
				// Print head
				System.out.println("  head");
				HeadTable head = (HeadTable)ttf.getByTableName("head");
				if (head == null) System.out.println("    Not present.");
				else {
					SimpleDateFormat df = new SimpleDateFormat();
					System.out.println("    version:               " + hex(head.version, 8));
					System.out.println("    fontRevision:          " + head.getFontRevisionDouble());
					System.out.println("    checkSum:              " + hex(head.checkSum, 8));
					System.out.println("    magicNumber:           " + hex(head.magicNumber, 8));
					System.out.println("    flags:                 " + hex(head.flags, 4));
					System.out.println("    unitsPerEm:            " + head.unitsPerEm);
					System.out.println("    dateCreated:           " + df.format(head.getDateCreatedCalendar().getTime()));
					System.out.println("    dateModified:          " + df.format(head.getDateModifiedCalendar().getTime()));
					System.out.println("    xMin:                  " + head.xMin);
					System.out.println("    yMin:                  " + head.yMin);
					System.out.println("    xMax:                  " + head.xMax);
					System.out.println("    yMax:                  " + head.yMax);
					System.out.println("    macStyle:              " + hex(head.macStyle, 2));
					System.out.println("    lowestRecPPEM:         " + head.lowestRecPPEM);
					System.out.println("    fontDirectionHint:     " + head.fontDirectionHint);
					System.out.println("    indexToLocFormat:      " + head.indexToLocFormat);
					System.out.println("    glyphDataFormat:       " + head.glyphDataFormat);
				}
				
				// Print hhea
				System.out.println("  hhea");
				HheaTable hhea = (HheaTable)ttf.getByTableName("hhea");
				if (hhea == null) System.out.println("    Not present.");
				else {
					System.out.println("    version:               " + hex(hhea.version, 8));
					System.out.println("    ascent:                " + hhea.ascent);
					System.out.println("    descent:               " + hhea.descent);
					System.out.println("    lineGap:               " + hhea.lineGap);
					System.out.println("    advanceWidthMax:       " + hhea.advanceWidthMax);
					System.out.println("    minLSB:                " + hhea.minLeftSideBearing);
					System.out.println("    minRSB:                " + hhea.minRightSideBearing);
					System.out.println("    xMaxExtent:            " + hhea.xMaxExtent);
					System.out.println("    caretSlopeRise:        " + hhea.caretSlopeRise);
					System.out.println("    caretSlopeRun:         " + hhea.caretSlopeRun);
					System.out.println("    caretOffset:           " + hhea.caretOffset);
					System.out.println("    reserved1:             " + hex(hhea.reserved1, 4));
					System.out.println("    reserved2:             " + hex(hhea.reserved2, 4));
					System.out.println("    reserved3:             " + hex(hhea.reserved3, 4));
					System.out.println("    reserved4:             " + hex(hhea.reserved4, 4));
					System.out.println("    metricDataFormat:      " + hhea.metricDataFormat);
					System.out.println("    numLongHorMetrics:     " + hhea.numLongHorMetrics);
				}
				
				// Print maxp
				System.out.println("  maxp");
				MaxpTable maxp = (MaxpTable)ttf.getByTableName("maxp");
				if (maxp == null) System.out.println("    Not present.");
				else {
					System.out.println("    version:               " + hex(maxp.version, 8));
					System.out.println("    numGlyphs:             " + maxp.numGlyphs);
					System.out.println("    maxPoints:             " + maxp.maxPoints);
					System.out.println("    maxContours:           " + maxp.maxContours);
					System.out.println("    maxComponentPoints:    " + maxp.maxComponentPoints);
					System.out.println("    maxComponentContours:  " + maxp.maxComponentContours);
					System.out.println("    maxZones:              " + maxp.maxZones);
					System.out.println("    maxTwilightPoints:     " + maxp.maxTwilightPoints);
					System.out.println("    maxStorage:            " + maxp.maxStorage);
					System.out.println("    maxFunctionDefs:       " + maxp.maxFunctionDefs);
					System.out.println("    maxInstructionDefs:    " + maxp.maxInstructionDefs);
					System.out.println("    maxStackElements:      " + maxp.maxStackElements);
					System.out.println("    maxSizeOfInstructions: " + maxp.maxSizeOfInstructions);
					System.out.println("    maxComponentElements:  " + maxp.maxComponentElements);
					System.out.println("    maxComponentDepth:     " + maxp.maxComponentDepth);
				}
				
				// Print OS/2
				System.out.println("  OS/2");
				Os2Table os2 = (Os2Table)ttf.getByTableName("OS/2");
				if (os2 == null) System.out.println("    Not present.");
				else {
					System.out.println("    version:               " + os2.version);
					System.out.println("    avgCharWidth:          " + os2.averageCharWidth);
					System.out.println("    weightClass:           " + os2.weightClass);
					System.out.println("    widthClass:            " + os2.widthClass);
					System.out.println("    flags:                 " + hex(os2.flags, 4));
					System.out.println("    subscriptXSize:        " + os2.subscriptXSize);
					System.out.println("    subscriptYSize:        " + os2.subscriptYSize);
					System.out.println("    subscriptXOffset:      " + os2.subscriptXOffset);
					System.out.println("    subscriptYOffset:      " + os2.subscriptYOffset);
					System.out.println("    superscriptXSize:      " + os2.superscriptXSize);
					System.out.println("    superscriptYSize:      " + os2.superscriptYSize);
					System.out.println("    superscriptXOffset:    " + os2.superscriptXOffset);
					System.out.println("    superscriptYOffset:    " + os2.superscriptYOffset);
					System.out.println("    strikeoutWidth:        " + os2.strikeoutWidth);
					System.out.println("    strikeoutPosition:     " + os2.strikeoutPosition);
					System.out.println("    familyClass:           " + os2.familyClass);
					System.out.println("    familySubClass:        " + os2.familySubClass);
					System.out.println("    panoseFamilyType:      " + os2.panoseFamilyType);
					System.out.println("    panoseSerifStyle:      " + os2.panoseSerifStyle);
					System.out.println("    panoseWeight:          " + os2.panoseWeight);
					System.out.println("    panoseProportion:      " + os2.panoseProportion);
					System.out.println("    panoseContrast:        " + os2.panoseContrast);
					System.out.println("    panoseStrokeVariation: " + os2.panoseStrokeVariation);
					System.out.println("    panoseArmStyle:        " + os2.panoseArmStyle);
					System.out.println("    panoseLetterform:      " + os2.panoseLetterform);
					System.out.println("    panoseMidline:         " + os2.panoseMidline);
					System.out.println("    panoseXHeight:         " + os2.panoseXHeight);
					System.out.println("    unicodeRanges:         " + hex(os2.unicodeRanges, 8));
					System.out.println("    vendorID:              " + os2.getVendorIDString());
					System.out.println("    fsSelection:           " + hex(os2.fsSelection, 4));
					System.out.println("    fsFirstCharIndex:      " + hex(os2.fsFirstCharIndex, 4));
					System.out.println("    fsLastCharIndex:       " + hex(os2.fsLastCharIndex, 4));
					if (os2.version < 1) continue;
					System.out.println("    typoAscent:            " + os2.typoAscent);
					System.out.println("    typoDescent:           " + os2.typoDescent);
					System.out.println("    typoLineGap:           " + os2.typoLineGap);
					System.out.println("    winAscent:             " + os2.winAscent);
					System.out.println("    winDescent:            " + os2.winDescent);
					if (os2.version < 2) continue;
					System.out.println("    codePages:             " + hex(os2.codePages, 8));
					if (os2.version < 3) continue;
					System.out.println("    xHeight:               " + os2.xHeight);
					System.out.println("    capHeight:             " + os2.capHeight);
					System.out.println("    defaultChar:           " + hex(os2.defaultChar, 4));
					System.out.println("    breakChar:             " + hex(os2.breakChar, 4));
					System.out.println("    maxContext:            " + os2.maxContext);
					if (os2.version < 5) continue;
					System.out.println("    lowerOpticalPointSize: " + os2.getLowerOpticalPointSizeDouble());
					System.out.println("    upperOpticalPointSize: " + os2.getUpperOpticalPointSizeDouble());
				}
			} catch (IOException e) {
				System.out.println("  Could not read.");
			}
		}
	}
	
	private static String hex(int v, int len) {
		String h = Integer.toHexString(v).toUpperCase();
		while (h.length() < len) h = "0" + h;
		return h;
	}
	
	private static String hex(int[] a, int len) {
		StringBuffer sb = new StringBuffer();
		for (int v : a) sb.insert(0, hex(v, len));
		return sb.toString();
	}
}
