package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HmtxTable extends ListBasedTable<HmtxTableEntry> {
	@Override
	public String tableName() {
		return "hmtx";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{"hhea","maxp"};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		int numLongHorMetrics = ((HheaTable)dependencies[0]).numLongHorMetrics;
		for (int i = 0; i < this.size(); i++) {
			HmtxTableEntry entry = this.get(i);
			if (i < numLongHorMetrics) {
				out.writeShort(entry.advanceWidth);
			}
			out.writeShort(entry.leftSideBearing);
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		int numLongHorMetrics = ((HheaTable)dependencies[0]).numLongHorMetrics;
		int numGlyphs = ((MaxpTable)dependencies[1]).numGlyphs;
		int lastAdvanceWidth = 0;
		this.clear();
		for (int i = 0; i < numGlyphs; i++) {
			if (i < numLongHorMetrics) {
				lastAdvanceWidth = in.readUnsignedShort();
			}
			HmtxTableEntry entry = new HmtxTableEntry();
			entry.advanceWidth = lastAdvanceWidth;
			entry.leftSideBearing = in.readShort();
			this.add(entry);
		}
	}
}
