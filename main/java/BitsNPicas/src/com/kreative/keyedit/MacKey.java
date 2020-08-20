package com.kreative.keyedit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MacKey {
	public static final List<MacKey> KEYS = Collections.unmodifiableList(Arrays.asList(
		new MacKey(  0, Key.A,                 -1),
		new MacKey(  1, Key.S,                 -1),
		new MacKey(  2, Key.D,                 -1),
		new MacKey(  3, Key.F,                 -1),
		new MacKey(  4, Key.H,                 -1),
		new MacKey(  5, Key.G,                 -1),
		new MacKey(  6, Key.Z,                 -1),
		new MacKey(  7, Key.X,                 -1),
		new MacKey(  8, Key.C,                 -1),
		new MacKey(  9, Key.V,                 -1),
		new MacKey( 10, Key.SECTION,           -1),
		new MacKey( 11, Key.B,                 -1),
		new MacKey( 12, Key.Q,                 -1),
		new MacKey( 13, Key.W,                 -1),
		new MacKey( 14, Key.E,                 -1),
		new MacKey( 15, Key.R,                 -1),
		new MacKey( 16, Key.Y,                 -1),
		new MacKey( 17, Key.T,                 -1),
		new MacKey( 18, Key.NUMROW_1,          -1),
		new MacKey( 19, Key.NUMROW_2,          -1),
		new MacKey( 20, Key.NUMROW_3,          -1),
		new MacKey( 21, Key.NUMROW_4,          -1),
		new MacKey( 22, Key.NUMROW_6,          -1),
		new MacKey( 23, Key.NUMROW_5,          -1),
		new MacKey( 24, Key.EQUALS_PLUS,       -1),
		new MacKey( 25, Key.NUMROW_9,          -1),
		new MacKey( 26, Key.NUMROW_7,          -1),
		new MacKey( 27, Key.HYPHEN_UNDERSCORE, -1),
		new MacKey( 28, Key.NUMROW_8,          -1),
		new MacKey( 29, Key.NUMROW_0,          -1),
		new MacKey( 30, Key.RIGHT_BRACKET,     -1),
		new MacKey( 31, Key.O,                 -1),
		new MacKey( 32, Key.U,                 -1),
		new MacKey( 33, Key.LEFT_BRACKET,      -1),
		new MacKey( 34, Key.I,                 -1),
		new MacKey( 35, Key.P,                 -1),
		new MacKey( 36, null,                0x0D),
		new MacKey( 37, Key.L,                 -1),
		new MacKey( 38, Key.J,                 -1),
		new MacKey( 39, Key.QUOTE,             -1),
		new MacKey( 40, Key.K,                 -1),
		new MacKey( 41, Key.SEMICOLON,         -1),
		new MacKey( 42, Key.BACKSLASH,         -1),
		new MacKey( 43, Key.COMMA,             -1),
		new MacKey( 44, Key.SLASH,             -1),
		new MacKey( 45, Key.N,                 -1),
		new MacKey( 46, Key.M,                 -1),
		new MacKey( 47, Key.PERIOD,            -1),
		new MacKey( 48, null,                0x09),
		new MacKey( 49, Key.SPACE,             -1),
		new MacKey( 50, Key.GRAVE_TILDE,       -1),
		new MacKey( 51, null,                0x08),
		new MacKey( 52, null,                0x03),
		new MacKey( 53, null,                0x1B),
		new MacKey( 64, null,                0x10),
		new MacKey( 65, Key.NUMPAD_PERIOD,     -1),
		new MacKey( 66, null,                0x1D),
		new MacKey( 67, Key.NUMPAD_TIMES,      -1),
		new MacKey( 69, Key.NUMPAD_PLUS,       -1),
		new MacKey( 70, null,                0x1C),
		new MacKey( 71, null,                0x1B),
		new MacKey( 72, null,                0x1F),
		new MacKey( 75, Key.NUMPAD_DIVIDE,     -1),
		new MacKey( 76, null,                0x03),
		new MacKey( 77, null,                0x1E),
		new MacKey( 78, Key.NUMPAD_MINUS,      -1),
		new MacKey( 79, null,                0x10),
		new MacKey( 80, null,                0x10),
		new MacKey( 81, Key.NUMPAD_EQUALS,     -1),
		new MacKey( 82, Key.NUMPAD_0,          -1),
		new MacKey( 83, Key.NUMPAD_1,          -1),
		new MacKey( 84, Key.NUMPAD_2,          -1),
		new MacKey( 85, Key.NUMPAD_3,          -1),
		new MacKey( 86, Key.NUMPAD_4,          -1),
		new MacKey( 87, Key.NUMPAD_5,          -1),
		new MacKey( 88, Key.NUMPAD_6,          -1),
		new MacKey( 89, Key.NUMPAD_7,          -1),
		new MacKey( 90, null,                0x10),
		new MacKey( 91, Key.NUMPAD_8,          -1),
		new MacKey( 92, Key.NUMPAD_9,          -1),
		new MacKey( 93, Key.YEN,               -1),
		new MacKey( 94, Key.UNDERSCORE,        -1),
		new MacKey( 95, Key.NUMPAD_COMMA,      -1),
		new MacKey( 96, null,                0x10),
		new MacKey( 97, null,                0x10),
		new MacKey( 98, null,                0x10),
		new MacKey( 99, null,                0x10),
		new MacKey(100, null,                0x10),
		new MacKey(101, null,                0x10),
		new MacKey(102, null,                0x10),
		new MacKey(103, null,                0x10),
		new MacKey(104, null,                0x10),
		new MacKey(105, null,                0x10),
		new MacKey(106, null,                0x10),
		new MacKey(107, null,                0x10),
		new MacKey(108, null,                0x10),
		new MacKey(109, null,                0x10),
		new MacKey(110, null,                0x10),
		new MacKey(111, null,                0x10),
		new MacKey(112, null,                0x10),
		new MacKey(113, null,                0x10),
		new MacKey(114, null,                0x05),
		new MacKey(115, null,                0x01),
		new MacKey(116, null,                0x0B),
		new MacKey(117, null,                0x7F),
		new MacKey(118, null,                0x10),
		new MacKey(119, null,                0x04),
		new MacKey(120, null,                0x10),
		new MacKey(121, null,                0x0C),
		new MacKey(122, null,                0x10),
		new MacKey(123, null,                0x1C),
		new MacKey(124, null,                0x1D),
		new MacKey(125, null,                0x1F),
		new MacKey(126, null,                0x1E)
	));
	
	public final int keyCode;
	public final Key key;
	public final int output;
	
	private MacKey(int kc, Key key, int out) {
		this.keyCode = kc;
		this.key = key;
		this.output = out;
	}
	
	public static MacKey forKeyCode(int kc) {
		for (MacKey key : KEYS) {
			if (key.keyCode == kc) {
				return key;
			}
		}
		return null;
	}
}
