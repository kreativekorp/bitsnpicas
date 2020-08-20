package com.kreative.keyedit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class XkbKey {
	public static final List<XkbKey> KEYS = Collections.unmodifiableList(Arrays.asList(
		new XkbKey("TLDE", Key.GRAVE_TILDE),
		new XkbKey("AE01", Key.NUMROW_1),
		new XkbKey("AE02", Key.NUMROW_2),
		new XkbKey("AE03", Key.NUMROW_3),
		new XkbKey("AE04", Key.NUMROW_4),
		new XkbKey("AE05", Key.NUMROW_5),
		new XkbKey("AE06", Key.NUMROW_6),
		new XkbKey("AE07", Key.NUMROW_7),
		new XkbKey("AE08", Key.NUMROW_8),
		new XkbKey("AE09", Key.NUMROW_9),
		new XkbKey("AE10", Key.NUMROW_0),
		new XkbKey("AE11", Key.HYPHEN_UNDERSCORE),
		new XkbKey("AE12", Key.EQUALS_PLUS),
		null,
		new XkbKey("AD01", Key.Q),
		new XkbKey("AD02", Key.W),
		new XkbKey("AD03", Key.E),
		new XkbKey("AD04", Key.R),
		new XkbKey("AD05", Key.T),
		new XkbKey("AD06", Key.Y),
		new XkbKey("AD07", Key.U),
		new XkbKey("AD08", Key.I),
		new XkbKey("AD09", Key.O),
		new XkbKey("AD10", Key.P),
		new XkbKey("AD11", Key.LEFT_BRACKET),
		new XkbKey("AD12", Key.RIGHT_BRACKET),
		new XkbKey("BKSL", Key.BACKSLASH),
		null,
		new XkbKey("AC01", Key.A),
		new XkbKey("AC02", Key.S),
		new XkbKey("AC03", Key.D),
		new XkbKey("AC04", Key.F),
		new XkbKey("AC05", Key.G),
		new XkbKey("AC06", Key.H),
		new XkbKey("AC07", Key.J),
		new XkbKey("AC08", Key.K),
		new XkbKey("AC09", Key.L),
		new XkbKey("AC10", Key.SEMICOLON),
		new XkbKey("AC11", Key.QUOTE),
		null,
		new XkbKey("AB01", Key.Z),
		new XkbKey("AB02", Key.X),
		new XkbKey("AB03", Key.C),
		new XkbKey("AB04", Key.V),
		new XkbKey("AB05", Key.B),
		new XkbKey("AB06", Key.N),
		new XkbKey("AB07", Key.M),
		new XkbKey("AB08", Key.COMMA),
		new XkbKey("AB09", Key.PERIOD),
		new XkbKey("AB10", Key.SLASH),
		null,
		new XkbKey("SPCE", Key.SPACE)
	));
	
	public final String id;
	public final Key key;
	
	private XkbKey(String id, Key key) {
		this.id = id;
		this.key = key;
	}
	
	public static XkbKey forId(String id) {
		for (XkbKey key : KEYS) {
			if (key != null && key.id.equalsIgnoreCase(id)) {
				return key;
			}
		}
		return null;
	}
}
