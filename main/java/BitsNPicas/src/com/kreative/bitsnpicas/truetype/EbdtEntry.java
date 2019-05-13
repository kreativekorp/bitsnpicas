package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class EbdtEntry {
	public abstract int format();
	protected abstract void read(DataInputStream in, int length) throws IOException;
	protected abstract void write(DataOutputStream out) throws IOException;
	protected abstract int length();
}
