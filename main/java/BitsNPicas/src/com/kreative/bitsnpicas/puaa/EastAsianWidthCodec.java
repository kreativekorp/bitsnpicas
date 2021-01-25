package com.kreative.bitsnpicas.puaa;

public class EastAsianWidthCodec extends AbstractStringCodec {
	public EastAsianWidthCodec() {
		super("EastAsianWidth.txt", "East_Asian_Width");
	}
	
	@Override
	protected String format(String range, String value) {
		return range + ";" + value;
	}
}
