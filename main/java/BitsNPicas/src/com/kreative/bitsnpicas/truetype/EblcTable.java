package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EblcTable extends ListBasedTable<EblcBitmapSize> {
	public SbitTableType type;
	public int version;
	
	public EblcTable(SbitTableType type) {
		this.type = type;
		this.version = type.version;
	}
	
	@Override
	public String tableName() {
		return type.locTableName;
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		// Recalculate offsets.
		int ptr = 8 + (this.size() * 48);
		for (EblcBitmapSize ebs : this) {
			ebs.indexSubTableArrayOffset = ptr;
			ebs.indexTablesSize = 0;
			ebs.numberOfIndexSubTables = ebs.size();
			ptr += ebs.size() * 8;
			for (EblcIndexSubtable st : ebs) {
				st.header.additionalOffsetToIndexSubtable = ptr - ebs.indexSubTableArrayOffset;
				ebs.indexTablesSize += 8 + st.length();
				ptr += 8 + st.length();
			}
		}
		// Write new table.
		out.writeInt(version);
		out.writeInt(this.size());
		for (EblcBitmapSize ebs : this) {
			ebs.write(out);
		}
		for (EblcBitmapSize ebs : this) {
			for (EblcIndexSubtable st : ebs) {
				st.header.writeElement(out);
			}
			for (EblcIndexSubtable st : ebs) {
				st.header.writeHeader(out);
				st.write(out);
			}
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		version = in.readInt();
		int n = in.readInt();
		for (int i = 0; i < n; i++) {
			EblcBitmapSize ebs = new EblcBitmapSize();
			ebs.read(in);
			this.add(ebs);
		}
		for (EblcBitmapSize ebs : this) {
			List<EblcIndexSubtableHeader> headers = new ArrayList<EblcIndexSubtableHeader>();
			in.reset();
			in.skip(ebs.indexSubTableArrayOffset);
			for (int i = 0; i < ebs.numberOfIndexSubTables; i++) {
				EblcIndexSubtableHeader header = new EblcIndexSubtableHeader();
				header.readElement(in);
				headers.add(header);
			}
			for (EblcIndexSubtableHeader header : headers) {
				in.reset();
				in.skip(ebs.indexSubTableArrayOffset + header.additionalOffsetToIndexSubtable);
				header.readHeader(in);
				EblcIndexSubtable st = createEblcIndexSubtable(header.indexFormat);
				st.header = header;
				st.read(in);
				ebs.add(st);
			}
		}
	}
	
	private static final EblcIndexSubtable createEblcIndexSubtable(int format) throws IOException {
		switch (format) {
			case 1: return new EblcIndexSubtable1();
			case 2: return new EblcIndexSubtable2();
			case 3: return new EblcIndexSubtable3();
			case 4: return new EblcIndexSubtable4();
			case 5: return new EblcIndexSubtable5();
			default: throw new IOException("invalid indexFormat: " + format);
		}
	}
}
