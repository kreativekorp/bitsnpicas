package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class BidiMirroringCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "BidiMirroring.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{"Bidi_Mirroring_Glyph"};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		HashMap<Integer,Integer> values = new HashMap<Integer,Integer>();
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 2) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				int v = Integer.parseInt(fields[1].trim(), 16);
				for (int cp = r[0]; cp <= r[1]; cp++) values.put(cp, v);
			} catch (NumberFormatException nfe) {}
		}
		PuaaSubtable st = puaa.getOrCreateSubtable("Bidi_Mirroring_Glyph");
		st.addAll(PuaaUtility.createEntriesFromHexadecimalMap(values));
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable values = puaa.getSubtable("Bidi_Mirroring_Glyph");
		if (values == null || values.isEmpty()) return;
		for (Map.Entry<Integer,String> e : PuaaUtility.createMapFromEntries(values).entrySet()) {
			out.println(PuaaUtility.toHexString(e.getKey()) + "; " + e.getValue());
		}
	}
}
