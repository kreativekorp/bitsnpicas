package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LocaTable extends ListBasedTable<Integer> {
	@Override
	public String tableName() {
		return "loca";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{"head","maxp"};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		int indexToLocFormat = ((HeadTable)dependencies[0]).indexToLocFormat;
		switch (indexToLocFormat) {
			case HeadTable.INDEX_TO_LOC_FORMAT_SHORT:
				for (int loc : this) out.writeShort(loc / 2);
				break;
			case HeadTable.INDEX_TO_LOC_FORMAT_LONG:
				for (int loc : this) out.writeInt(loc);
				break;
			default:
				throw new IllegalStateException("Invalid indexToLocFormat.");
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		int indexToLocFormat = ((HeadTable)dependencies[0]).indexToLocFormat;
		int numGlyphs = ((MaxpTable)dependencies[1]).numGlyphs;
		switch (indexToLocFormat) {
			case HeadTable.INDEX_TO_LOC_FORMAT_SHORT:
				this.clear();
				for (int i = 0; i <= numGlyphs; i++) {
					this.add(in.readUnsignedShort() * 2);
				}
				break;
			case HeadTable.INDEX_TO_LOC_FORMAT_LONG:
				this.clear();
				for (int i = 0; i <= numGlyphs; i++) {
					this.add(in.readInt());
				}
				break;
			default:
				throw new IllegalStateException("Invalid indexToLocFormat.");
		}
	}
}
