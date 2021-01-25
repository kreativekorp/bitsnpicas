package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class JamoCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "Jamo.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{"Jamo_Short_Name"};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		PuaaSubtable jamo = puaa.getOrCreateSubtable("Jamo_Short_Name");
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 1) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				String v = (fields.length > 1) ? fields[1].trim() : ""; // U+110B is actually ""
				PuaaSubtableEntry.Single e = new PuaaSubtableEntry.Single();
				e.firstCodePoint = r[0];
				e.lastCodePoint = r[1];
				e.value = v;
				jamo.add(e);
			} catch (NumberFormatException nfe) {}
		}
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable jamo = puaa.getSubtable("Jamo_Short_Name");
		if (jamo == null || jamo.isEmpty()) return;
		for (PuaaSubtableEntry e : jamo) {
			for (int cp = e.firstCodePoint; cp <= e.lastCodePoint; cp++) {
				out.println(PuaaUtility.toHexString(cp) + "; " + e.getPropertyValue(cp));
			}
		}
	}
}
