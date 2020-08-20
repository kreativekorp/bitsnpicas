package com.kreative.keyedit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class WinKey {
	public static final List<WinKey> KEYS = Collections.unmodifiableList(Arrays.asList(
		new WinKey(0x02, "1",          Key.NUMROW_1),
		new WinKey(0x03, "2",          Key.NUMROW_2),
		new WinKey(0x04, "3",          Key.NUMROW_3),
		new WinKey(0x05, "4",          Key.NUMROW_4),
		new WinKey(0x06, "5",          Key.NUMROW_5),
		new WinKey(0x07, "6",          Key.NUMROW_6),
		new WinKey(0x08, "7",          Key.NUMROW_7),
		new WinKey(0x09, "8",          Key.NUMROW_8),
		new WinKey(0x0a, "9",          Key.NUMROW_9),
		new WinKey(0x0b, "0",          Key.NUMROW_0),
		new WinKey(0x0c, "OEM_MINUS",  Key.HYPHEN_UNDERSCORE),
		new WinKey(0x0d, "OEM_PLUS",   Key.EQUALS_PLUS),
		new WinKey(0x10, "Q",          Key.Q),
		new WinKey(0x11, "W",          Key.W),
		new WinKey(0x12, "E",          Key.E),
		new WinKey(0x13, "R",          Key.R),
		new WinKey(0x14, "T",          Key.T),
		new WinKey(0x15, "Y",          Key.Y),
		new WinKey(0x16, "U",          Key.U),
		new WinKey(0x17, "I",          Key.I),
		new WinKey(0x18, "O",          Key.O),
		new WinKey(0x19, "P",          Key.P),
		new WinKey(0x1a, "OEM_4",      Key.LEFT_BRACKET),
		new WinKey(0x1b, "OEM_6",      Key.RIGHT_BRACKET),
		new WinKey(0x1e, "A",          Key.A),
		new WinKey(0x1f, "S",          Key.S),
		new WinKey(0x20, "D",          Key.D),
		new WinKey(0x21, "F",          Key.F),
		new WinKey(0x22, "G",          Key.G),
		new WinKey(0x23, "H",          Key.H),
		new WinKey(0x24, "J",          Key.J),
		new WinKey(0x25, "K",          Key.K),
		new WinKey(0x26, "L",          Key.L),
		new WinKey(0x27, "OEM_1",      Key.SEMICOLON),
		new WinKey(0x28, "OEM_7",      Key.QUOTE),
		new WinKey(0x29, "OEM_3",      Key.GRAVE_TILDE),
		new WinKey(0x2b, "OEM_5",      Key.BACKSLASH),
		new WinKey(0x2c, "Z",          Key.Z),
		new WinKey(0x2d, "X",          Key.X),
		new WinKey(0x2e, "C",          Key.C),
		new WinKey(0x2f, "V",          Key.V),
		new WinKey(0x30, "B",          Key.B),
		new WinKey(0x31, "N",          Key.N),
		new WinKey(0x32, "M",          Key.M),
		new WinKey(0x33, "OEM_COMMA",  Key.COMMA),
		new WinKey(0x34, "OEM_PERIOD", Key.PERIOD),
		new WinKey(0x35, "OEM_2",      Key.SLASH),
		new WinKey(0x39, "SPACE",      Key.SPACE),
		new WinKey(0x56, "OEM_102",    Key.BACKSLASH_102),
		new WinKey(0x53, "DECIMAL",    Key.NUMPAD_PERIOD)
	));
	
	public final int scanCode;
	public final String vkConstant;
	public final Key key;
	
	private WinKey(int sc, String vk, Key key) {
		this.scanCode = sc;
		this.vkConstant = vk;
		this.key = key;
	}
	
	public static WinKey forScanCode(int sc) {
		for (WinKey key : KEYS) {
			if (key.scanCode == sc) {
				return key;
			}
		}
		return null;
	}
}
