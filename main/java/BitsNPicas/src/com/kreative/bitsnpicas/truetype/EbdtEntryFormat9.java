package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EbdtEntryFormat9 extends ListBasedEbdtEntry<EbdtComponent> {
	public SbitBigGlyphMetrics bigMetrics;
	
	@Override
	public int format() {
		return 9;
	}
	
	@Override
	protected void read(DataInputStream in, int length) throws IOException {
		bigMetrics = new SbitBigGlyphMetrics();
		bigMetrics.read(in);
		int n = in.readUnsignedShort();
		for (int i = 0; i < n; i++) {
			EbdtComponent c = new EbdtComponent();
			c.read(in);
			this.add(c);
		}
	}
	
	@Override
	protected void write(DataOutputStream out) throws IOException {
		((bigMetrics != null) ? bigMetrics : new SbitBigGlyphMetrics()).write(out);
		out.writeShort(this.size());
		for (EbdtComponent c : this) {
			c.write(out);
		}
	}
	
	@Override
	protected int length() {
		return 10 + (this.size() * 4);
	}
}
