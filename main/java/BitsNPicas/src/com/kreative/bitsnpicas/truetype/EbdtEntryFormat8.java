package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EbdtEntryFormat8 extends ListBasedEbdtEntry<EbdtComponent> {
	public SbitSmallGlyphMetrics smallMetrics;
	
	@Override
	public int format() {
		return 8;
	}
	
	@Override
	protected void read(DataInputStream in, int length) throws IOException {
		smallMetrics = new SbitSmallGlyphMetrics();
		smallMetrics.read(in);
		in.readByte();
		int n = in.readUnsignedShort();
		for (int i = 0; i < n; i++) {
			EbdtComponent c = new EbdtComponent();
			c.read(in);
			this.add(c);
		}
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		((smallMetrics != null) ? smallMetrics : new SbitSmallGlyphMetrics()).write(out);
		out.writeByte(0);
		out.writeShort(this.size());
		for (EbdtComponent c : this) {
			c.write(out);
		}
	}
	
	@Override
	protected int length() {
		return 8 + (this.size() * 4);
	}
}
