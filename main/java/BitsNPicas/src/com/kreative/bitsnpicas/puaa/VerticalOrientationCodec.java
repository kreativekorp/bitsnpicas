package com.kreative.bitsnpicas.puaa;

public class VerticalOrientationCodec extends AbstractStringCodec {
	public VerticalOrientationCodec() {
		super("VerticalOrientation.txt", "Vertical_Orientation");
	}
	
	@Override
	protected String format(String range, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append(range);
		while (sb.length() < 14) sb.append(" ");
		sb.append("; ");
		sb.append(value);
		return sb.toString();
	}
}
