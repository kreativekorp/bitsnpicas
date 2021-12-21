package com.kreative.keyedit;

public enum Key {
	GRAVE_TILDE       ('`', '~',       -1,  -1,  -1, KeyBlock.ALPHA),
	NUMROW_1          ('1', '!',       -1,  -1,  -1, KeyBlock.ALPHA),
	NUMROW_2          ('2', '@', '@'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	NUMROW_3          ('3', '#',       -1,  -1,  -1, KeyBlock.ALPHA),
	NUMROW_4          ('4', '$',       -1,  -1,  -1, KeyBlock.ALPHA),
	NUMROW_5          ('5', '%',       -1,  -1,  -1, KeyBlock.ALPHA),
	NUMROW_6          ('6', '^', '^'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	NUMROW_7          ('7', '&',       -1,  -1,  -1, KeyBlock.ALPHA),
	NUMROW_8          ('8', '*',       -1,  -1,  -1, KeyBlock.ALPHA),
	NUMROW_9          ('9', '(',       -1,  -1,  -1, KeyBlock.ALPHA),
	NUMROW_0          ('0', ')',       -1,  -1,  -1, KeyBlock.ALPHA),
	HYPHEN_UNDERSCORE ('-', '_', '_'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	EQUALS_PLUS       ('=', '+',       -1,  -1,  -1, KeyBlock.ALPHA),
	Q                 ('q', 'Q', 'Q'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	W                 ('w', 'W', 'W'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	E                 ('e', 'E', 'E'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	R                 ('r', 'R', 'R'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	T                 ('t', 'T', 'T'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	Y                 ('y', 'Y', 'Y'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	U                 ('u', 'U', 'U'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	I                 ('i', 'I', 'I'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	O                 ('o', 'O', 'O'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	P                 ('p', 'P', 'P'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	LEFT_BRACKET      ('[', '{', '['&0x1F,  -1,  -1, KeyBlock.ALPHA),
	RIGHT_BRACKET     (']', '}', ']'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	BACKSLASH         ('\\','|','\\'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	A                 ('a', 'A', 'A'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	S                 ('s', 'S', 'S'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	D                 ('d', 'D', 'D'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	F                 ('f', 'F', 'F'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	G                 ('g', 'G', 'G'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	H                 ('h', 'H', 'H'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	J                 ('j', 'J', 'J'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	K                 ('k', 'K', 'K'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	L                 ('l', 'L', 'L'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	SEMICOLON         (';', ':',       -1,  -1,  -1, KeyBlock.ALPHA),
	QUOTE             ('\'','"',       -1,  -1,  -1, KeyBlock.ALPHA),
	Z                 ('z', 'Z', 'Z'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	X                 ('x', 'X', 'X'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	C                 ('c', 'C', 'C'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	V                 ('v', 'V', 'V'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	B                 ('b', 'B', 'B'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	N                 ('n', 'N', 'N'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	M                 ('m', 'M', 'M'&0x1F,  -1,  -1, KeyBlock.ALPHA),
	COMMA             (',', '<',       -1,  -1,  -1, KeyBlock.ALPHA),
	PERIOD            ('.', '>',       -1,  -1,  -1, KeyBlock.ALPHA),
	SLASH             ('/', '?',       -1,  -1,  -1, KeyBlock.ALPHA),
	SECTION           (0xA7,0xB1,      -1,0xA7,0xB1, KeyBlock.INTL),
	YEN               (0xA5,'|',       -1,0xA5,0xA5, KeyBlock.INTL),
	UNDERSCORE        ('_', '_', '_'&0x1F, '_', '_', KeyBlock.INTL),
	BACKSLASH_102     ('\\','|','\\'&0x1F,  -1,  -1, KeyBlock.INTL),
	SPACE             (' ', ' ',       -1, ' ', ' ', KeyBlock.ALPHA),
	NUMPAD_0          ('0', '0',       -1, '0', '0', KeyBlock.NUMPAD),
	NUMPAD_COMMA      (',', ',',       -1, ',', ',', KeyBlock.NUMPAD),
	NUMPAD_PERIOD     ('.', '.',       -1, '.', '.', KeyBlock.NUMPAD),
	NUMPAD_1          ('1', '1',       -1, '1', '1', KeyBlock.NUMPAD),
	NUMPAD_2          ('2', '2',       -1, '2', '2', KeyBlock.NUMPAD),
	NUMPAD_3          ('3', '3',       -1, '3', '3', KeyBlock.NUMPAD),
	NUMPAD_4          ('4', '4',       -1, '4', '4', KeyBlock.NUMPAD),
	NUMPAD_5          ('5', '5',       -1, '5', '5', KeyBlock.NUMPAD),
	NUMPAD_6          ('6', '6',       -1, '6', '6', KeyBlock.NUMPAD),
	NUMPAD_7          ('7', '7',       -1, '7', '7', KeyBlock.NUMPAD),
	NUMPAD_8          ('8', '8',       -1, '8', '8', KeyBlock.NUMPAD),
	NUMPAD_9          ('9', '9',       -1, '9', '9', KeyBlock.NUMPAD),
	NUMPAD_PLUS       ('+', '+',       -1, '+', '+', KeyBlock.NUMPAD),
	NUMPAD_MINUS      ('-', '-',       -1, '-', '-', KeyBlock.NUMPAD),
	NUMPAD_TIMES      ('*', '*',       -1, '*', '*', KeyBlock.NUMPAD),
	NUMPAD_DIVIDE     ('/', '/',       -1, '/', '/', KeyBlock.NUMPAD),
	NUMPAD_EQUALS     ('=', '=',       -1, '=', '=', KeyBlock.NUMPAD);
	
	public final int defaultUnshifted;
	public final int defaultShifted;
	public final int defaultCtrl;
	public final int defaultAltUnshifted;
	public final int defaultAltShifted;
	public final KeyBlock keyBlock;
	
	private Key(int u, int s, int c, int au, int as, KeyBlock keyBlock) {
		this.defaultUnshifted = u;
		this.defaultShifted = s;
		this.defaultCtrl = c;
		this.defaultAltUnshifted = au;
		this.defaultAltShifted = as;
		this.keyBlock = keyBlock;
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
