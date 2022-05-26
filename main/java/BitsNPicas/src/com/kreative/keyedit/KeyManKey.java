package com.kreative.keyedit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class KeyManKey {
	public static final List<KeyManKey> KEYS = Collections.unmodifiableList(Arrays.asList(
		new KeyManKey("K_BKQUOTE", Key.GRAVE_TILDE),
		new KeyManKey("K_1",       Key.NUMROW_1),
		new KeyManKey("K_2",       Key.NUMROW_2),
		new KeyManKey("K_3",       Key.NUMROW_3),
		new KeyManKey("K_4",       Key.NUMROW_4),
		new KeyManKey("K_5",       Key.NUMROW_5),
		new KeyManKey("K_6",       Key.NUMROW_6),
		new KeyManKey("K_7",       Key.NUMROW_7),
		new KeyManKey("K_8",       Key.NUMROW_8),
		new KeyManKey("K_9",       Key.NUMROW_9),
		new KeyManKey("K_0",       Key.NUMROW_0),
		new KeyManKey("K_HYPHEN",  Key.HYPHEN_UNDERSCORE),
		new KeyManKey("K_EQUAL",   Key.EQUALS_PLUS),
		new KeyManKey("K_Q",       Key.Q),
		new KeyManKey("K_W",       Key.W),
		new KeyManKey("K_E",       Key.E),
		new KeyManKey("K_R",       Key.R),
		new KeyManKey("K_T",       Key.T),
		new KeyManKey("K_Y",       Key.Y),
		new KeyManKey("K_U",       Key.U),
		new KeyManKey("K_I",       Key.I),
		new KeyManKey("K_O",       Key.O),
		new KeyManKey("K_P",       Key.P),
		new KeyManKey("K_LBRKT",   Key.LEFT_BRACKET),
		new KeyManKey("K_RBRKT",   Key.RIGHT_BRACKET),
		new KeyManKey("K_BKSLASH", Key.BACKSLASH),
		new KeyManKey("K_A",       Key.A),
		new KeyManKey("K_S",       Key.S),
		new KeyManKey("K_D",       Key.D),
		new KeyManKey("K_F",       Key.F),
		new KeyManKey("K_G",       Key.G),
		new KeyManKey("K_H",       Key.H),
		new KeyManKey("K_J",       Key.J),
		new KeyManKey("K_K",       Key.K),
		new KeyManKey("K_L",       Key.L),
		new KeyManKey("K_COLON",   Key.SEMICOLON),
		new KeyManKey("K_QUOTE",   Key.QUOTE),
		new KeyManKey("K_oE2",     Key.BACKSLASH_102),
		new KeyManKey("K_Z",       Key.Z),
		new KeyManKey("K_X",       Key.X),
		new KeyManKey("K_C",       Key.C),
		new KeyManKey("K_V",       Key.V),
		new KeyManKey("K_B",       Key.B),
		new KeyManKey("K_N",       Key.N),
		new KeyManKey("K_M",       Key.M),
		new KeyManKey("K_COMMA",   Key.COMMA),
		new KeyManKey("K_PERIOD",  Key.PERIOD),
		new KeyManKey("K_SLASH",   Key.SLASH),
		new KeyManKey("K_SPACE",   Key.SPACE)
	));
	
	public final String id;
	public final Key key;
	
	private KeyManKey(String id, Key key) {
		this.id = id;
		this.key = key;
	}
	
	public static KeyManKey forId(String id) {
		for (KeyManKey key : KEYS) {
			if (key.id.equalsIgnoreCase(id)) {
				return key;
			}
		}
		return null;
	}
}
