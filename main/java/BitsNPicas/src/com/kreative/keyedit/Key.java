package com.kreative.keyedit;

public enum Key {
	GRAVE_TILDE       ('`', '~',       -1,  -1,  -1),
	NUMROW_1          ('1', '!',       -1,  -1,  -1),
	NUMROW_2          ('2', '@', '@'&0x1F,  -1,  -1),
	NUMROW_3          ('3', '#',       -1,  -1,  -1),
	NUMROW_4          ('4', '$',       -1,  -1,  -1),
	NUMROW_5          ('5', '%',       -1,  -1,  -1),
	NUMROW_6          ('6', '^', '^'&0x1F,  -1,  -1),
	NUMROW_7          ('7', '&',       -1,  -1,  -1),
	NUMROW_8          ('8', '*',       -1,  -1,  -1),
	NUMROW_9          ('9', '(',       -1,  -1,  -1),
	NUMROW_0          ('0', ')',       -1,  -1,  -1),
	HYPHEN_UNDERSCORE ('-', '_', '_'&0x1F,  -1,  -1),
	EQUALS_PLUS       ('=', '+',       -1,  -1,  -1),
	Q                 ('q', 'Q', 'Q'&0x1F,  -1,  -1),
	W                 ('w', 'W', 'W'&0x1F,  -1,  -1),
	E                 ('e', 'E', 'E'&0x1F,  -1,  -1),
	R                 ('r', 'R', 'R'&0x1F,  -1,  -1),
	T                 ('t', 'T', 'T'&0x1F,  -1,  -1),
	Y                 ('y', 'Y', 'Y'&0x1F,  -1,  -1),
	U                 ('u', 'U', 'U'&0x1F,  -1,  -1),
	I                 ('i', 'I', 'I'&0x1F,  -1,  -1),
	O                 ('o', 'O', 'O'&0x1F,  -1,  -1),
	P                 ('p', 'P', 'P'&0x1F,  -1,  -1),
	LEFT_BRACKET      ('[', '{', '['&0x1F,  -1,  -1),
	RIGHT_BRACKET     (']', '}', ']'&0x1F,  -1,  -1),
	BACKSLASH         ('\\','|','\\'&0x1F,  -1,  -1),
	A                 ('a', 'A', 'A'&0x1F,  -1,  -1),
	S                 ('s', 'S', 'S'&0x1F,  -1,  -1),
	D                 ('d', 'D', 'D'&0x1F,  -1,  -1),
	F                 ('f', 'F', 'F'&0x1F,  -1,  -1),
	G                 ('g', 'G', 'G'&0x1F,  -1,  -1),
	H                 ('h', 'H', 'H'&0x1F,  -1,  -1),
	J                 ('j', 'J', 'J'&0x1F,  -1,  -1),
	K                 ('k', 'K', 'K'&0x1F,  -1,  -1),
	L                 ('l', 'L', 'L'&0x1F,  -1,  -1),
	SEMICOLON         (';', ':',       -1,  -1,  -1),
	QUOTE             ('\'','"',       -1,  -1,  -1),
	Z                 ('z', 'Z', 'Z'&0x1F,  -1,  -1),
	X                 ('x', 'X', 'X'&0x1F,  -1,  -1),
	C                 ('c', 'C', 'C'&0x1F,  -1,  -1),
	V                 ('v', 'V', 'V'&0x1F,  -1,  -1),
	B                 ('b', 'B', 'B'&0x1F,  -1,  -1),
	N                 ('n', 'N', 'N'&0x1F,  -1,  -1),
	M                 ('m', 'M', 'M'&0x1F,  -1,  -1),
	COMMA             (',', '<',       -1,  -1,  -1),
	PERIOD            ('.', '>',       -1,  -1,  -1),
	SLASH             ('/', '?',       -1,  -1,  -1),
	SECTION           (0xA7,0xB1,      -1,0xA7,0xB1),
	YEN               (0xA5,'|',       -1,0xA5,0xA5),
	UNDERSCORE        ('_', '_', '_'&0x1F, '_', '_'),
	BACKSLASH_102     ('\\','|','\\'&0x1F,  -1,  -1),
	SPACE             (' ', ' ',       -1, ' ', ' '),
	NUMPAD_0          ('0', '0',       -1, '0', '0'),
	NUMPAD_COMMA      (',', ',',       -1, ',', ','),
	NUMPAD_PERIOD     ('.', '.',       -1, '.', '.'),
	NUMPAD_1          ('1', '1',       -1, '1', '1'),
	NUMPAD_2          ('2', '2',       -1, '2', '2'),
	NUMPAD_3          ('3', '3',       -1, '3', '3'),
	NUMPAD_4          ('4', '4',       -1, '4', '4'),
	NUMPAD_5          ('5', '5',       -1, '5', '5'),
	NUMPAD_6          ('6', '6',       -1, '6', '6'),
	NUMPAD_7          ('7', '7',       -1, '7', '7'),
	NUMPAD_8          ('8', '8',       -1, '8', '8'),
	NUMPAD_9          ('9', '9',       -1, '9', '9'),
	NUMPAD_PLUS       ('+', '+',       -1, '+', '+'),
	NUMPAD_MINUS      ('-', '-',       -1, '-', '-'),
	NUMPAD_TIMES      ('*', '*',       -1, '*', '*'),
	NUMPAD_DIVIDE     ('/', '/',       -1, '/', '/'),
	NUMPAD_EQUALS     ('=', '=',       -1, '=', '=');
	
	public final int defaultUnshifted;
	public final int defaultShifted;
	public final int defaultCtrl;
	public final int defaultAltUnshifted;
	public final int defaultAltShifted;
	
	private Key(int u, int s, int c, int au, int as) {
		this.defaultUnshifted = u;
		this.defaultShifted = s;
		this.defaultCtrl = c;
		this.defaultAltUnshifted = au;
		this.defaultAltShifted = as;
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
