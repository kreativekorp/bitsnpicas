package com.kreative.bitsnpicas.puaa;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import com.kreative.bitsnpicas.truetype.PuaaSubtable;
import com.kreative.bitsnpicas.truetype.PuaaSubtableEntry;
import com.kreative.bitsnpicas.truetype.PuaaTable;

public abstract class AbstractStringCodec extends PuaaCodec {
	protected final String fileName;
	protected final String propName;
	
	protected AbstractStringCodec(String fileName, String propName) {
		this.fileName = fileName;
		this.propName = propName;
	}
	
	@Override
	public String getFileName() {
		return fileName;
	}
	
	@Override
	public String[] getPropertyNames() {
		return new String[]{propName};
	}
	
	@Override
	public void compile(PuaaTable puaa, Scanner in) {
		HashMap<Integer,String> values = new HashMap<Integer,String>();
		while (in.hasNextLine()) {
			String[] fields = PuaaUtility.splitLine(in.nextLine());
			if (fields == null || fields.length < 2) continue;
			try {
				int[] r = PuaaUtility.splitRange(fields[0]);
				String v = fields[1].trim();
				for (int cp = r[0]; cp <= r[1]; cp++) values.put(cp, v);
			} catch (NumberFormatException nfe) {}
		}
		PuaaSubtable st = puaa.getOrCreateSubtable(propName);
		st.addAll(PuaaUtility.createEntriesFromStringMap(values));
	}
	
	@Override
	public void decompile(PuaaTable puaa, PrintWriter out) {
		PuaaSubtable values = puaa.getSubtable(propName);
		if (values == null || values.isEmpty()) return;
		for (PuaaSubtableEntry.Single e : PuaaUtility.createRunsFromEntries(values)) {
			out.println(format(PuaaUtility.joinRange(e), e.value));
		}
	}
	
	protected abstract String format(String range, String value);
}
