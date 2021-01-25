package com.kreative.bitsnpicas.puaa;

public class LineBreakCodec extends AbstractStringCodec {
	public LineBreakCodec() {
		super("LineBreak.txt", "Line_Break");
	}
	
	@Override
	protected String format(String range, String value) {
		return range + ";" + value;
	}
}
