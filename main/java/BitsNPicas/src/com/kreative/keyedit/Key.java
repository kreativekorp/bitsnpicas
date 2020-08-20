package com.kreative.keyedit;

public enum Key {
	GRAVE_TILDE       ('`', '~',       -1),
	NUMROW_1          ('1', '!',       -1),
	NUMROW_2          ('2', '@', '@'&0x1F),
	NUMROW_3          ('3', '#',       -1),
	NUMROW_4          ('4', '$',       -1),
	NUMROW_5          ('5', '%',       -1),
	NUMROW_6          ('6', '^', '^'&0x1F),
	NUMROW_7          ('7', '&',       -1),
	NUMROW_8          ('8', '*',       -1),
	NUMROW_9          ('9', '(',       -1),
	NUMROW_0          ('0', ')',       -1),
	HYPHEN_UNDERSCORE ('-', '_', '_'&0x1F),
	EQUALS_PLUS       ('=', '+',       -1),
	Q                 ('q', 'Q', 'Q'&0x1F),
	W                 ('w', 'W', 'W'&0x1F),
	E                 ('e', 'E', 'E'&0x1F),
	R                 ('r', 'R', 'R'&0x1F),
	T                 ('t', 'T', 'T'&0x1F),
	Y                 ('y', 'Y', 'Y'&0x1F),
	U                 ('u', 'U', 'U'&0x1F),
	I                 ('i', 'I', 'I'&0x1F),
	O                 ('o', 'O', 'O'&0x1F),
	P                 ('p', 'P', 'P'&0x1F),
	LEFT_BRACKET      ('[', '{', '['&0x1F),
	RIGHT_BRACKET     (']', '}', ']'&0x1F),
	BACKSLASH         ('\\','|','\\'&0x1F),
	A                 ('a', 'A', 'A'&0x1F),
	S                 ('s', 'S', 'S'&0x1F),
	D                 ('d', 'D', 'D'&0x1F),
	F                 ('f', 'F', 'F'&0x1F),
	G                 ('g', 'G', 'G'&0x1F),
	H                 ('h', 'H', 'H'&0x1F),
	J                 ('j', 'J', 'J'&0x1F),
	K                 ('k', 'K', 'K'&0x1F),
	L                 ('l', 'L', 'L'&0x1F),
	SEMICOLON         (';', ':',       -1),
	QUOTE             ('\'','"',       -1),
	Z                 ('z', 'Z', 'Z'&0x1F),
	X                 ('x', 'X', 'X'&0x1F),
	C                 ('c', 'C', 'C'&0x1F),
	V                 ('v', 'V', 'V'&0x1F),
	B                 ('b', 'B', 'B'&0x1F),
	N                 ('n', 'N', 'N'&0x1F),
	M                 ('m', 'M', 'M'&0x1F),
	COMMA             (',', '<',       -1),
	PERIOD            ('.', '>',       -1),
	SLASH             ('/', '?',       -1),
	SECTION           (0xA7,0xB1,      -1),
	YEN               (0xA5,'|',       -1),
	UNDERSCORE        ('_', '_', '_'&0x1F),
	BACKSLASH_102     ('\\','|','\\'&0x1F),
	SPACE             (' ', ' ',       -1),
	NUMPAD_0          ('0', '0',       -1),
	NUMPAD_COMMA      (',', ',',       -1),
	NUMPAD_PERIOD     ('.', '.',       -1),
	NUMPAD_1          ('1', '1',       -1),
	NUMPAD_2          ('2', '2',       -1),
	NUMPAD_3          ('3', '3',       -1),
	NUMPAD_4          ('4', '4',       -1),
	NUMPAD_5          ('5', '5',       -1),
	NUMPAD_6          ('6', '6',       -1),
	NUMPAD_7          ('7', '7',       -1),
	NUMPAD_8          ('8', '8',       -1),
	NUMPAD_9          ('9', '9',       -1),
	NUMPAD_PLUS       ('+', '+',       -1),
	NUMPAD_MINUS      ('-', '-',       -1),
	NUMPAD_TIMES      ('*', '*',       -1),
	NUMPAD_DIVIDE     ('/', '/',       -1),
	NUMPAD_EQUALS     ('=', '=',       -1);
	
	public final int defaultUnshifted;
	public final int defaultShifted;
	public final int defaultCtrl;
	
	private Key(int u, int s, int c) {
		this.defaultUnshifted = u;
		this.defaultShifted = s;
		this.defaultCtrl = c;
	}
	
	public static Key forName(String name) {
		for (Key key : values()) {
			if (key.name().equalsIgnoreCase(name)) {
				return key;
			}
		}
		return null;
	}
}
