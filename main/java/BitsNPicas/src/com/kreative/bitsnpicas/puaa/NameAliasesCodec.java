package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public class NameAliasesCodec extends PuaaCodec {
	@Override
	public String getFileName() {
		return "NameAliases.txt";
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{"Name_Alias"};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		PuaaSubtable names = puaa.getOrCreateSubtable("Name_Alias");
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 3) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				String n = fields[1].trim();
				String t = fields[2].trim();
				PuaaSubtableEntry.NameAlias e = new PuaaSubtableEntry.NameAlias();
				e.firstCodePoint = r[0];
				e.lastCodePoint = r[1];
				e.alias = n;
				e.type = t;
				names.add(e);
			} catch (NumberFormatException nfe) {}
		}
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable names = puaa.getSubtable("Name_Alias");
		if (names == null || names.isEmpty()) return;
		for (PuaaSubtableEntry e : names) {
			for (int cp = e.firstCodePoint; cp <= e.lastCodePoint; cp++) {
				out.println(PuaaUtility.toHexString(cp) + ";" + e.getPropertyValue(cp));
			}
		}
	}
}
