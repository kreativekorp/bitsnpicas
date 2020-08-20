package com.kreative.keyedit;

public enum XkbDeadKey {
	dead_grave,
	dead_acute,
	dead_circumflex,
	dead_tilde,
	dead_macron,
	dead_breve,
	dead_abovedot,
	dead_diaeresis,
	dead_abovering,
	dead_doubleacute,
	dead_caron,
	dead_cedilla,
	dead_ogonek,
	dead_iota,
	dead_voiced_sound,
	dead_semivoiced_sound,
	dead_belowdot,
	dead_hook,
	dead_horn,
	dead_stroke,
	dead_abovecomma,
	dead_abovereversedcomma,
	dead_doublegrave,
	dead_belowring,
	dead_belowmacron,
	dead_belowcircumflex,
	dead_belowtilde,
	dead_belowbreve,
	dead_belowdiaeresis,
	dead_invertedbreve,
	dead_belowcomma,
	dead_currency,
	dead_a,
	dead_A,
	dead_e,
	dead_E,
	dead_i,
	dead_I,
	dead_o,
	dead_O,
	dead_u,
	dead_U,
	dead_small_schwa,
	dead_capital_schwa,
	dead_greek,
	dead_lowline,
	dead_aboveverticalline,
	dead_belowverticalline,
	dead_longsolidusoverlay;
	
	public static XkbDeadKey forUnicode(int u) {
		switch (u) {
			case 0x0022: return dead_diaeresis;
			case 0x0024: return dead_currency;
			case 0x0027: return dead_abovedot;
			case 0x002F: return dead_stroke;
			case 0x0041: return dead_A;
			case 0x0045: return dead_E;
			case 0x0049: return dead_I;
			case 0x004F: return dead_O;
			case 0x0055: return dead_U;
			case 0x005E: return dead_circumflex;
			case 0x005F: return dead_lowline;
			case 0x0060: return dead_grave;
			case 0x0061: return dead_a;
			case 0x0065: return dead_e;
			case 0x0069: return dead_i;
			case 0x006F: return dead_o;
			case 0x0075: return dead_u;
			case 0x007E: return dead_tilde;
			case 0x00A2: return dead_currency;
			case 0x00A3: return dead_currency;
			case 0x00A4: return dead_currency;
			case 0x00A5: return dead_currency;
			case 0x00A8: return dead_diaeresis;
			case 0x00AF: return dead_macron;
			case 0x00B0: return dead_abovering;
			case 0x00B4: return dead_acute;
			case 0x00B8: return dead_cedilla;
			case 0x018F: return dead_capital_schwa;
			case 0x01DD: return dead_small_schwa;
			case 0x0259: return dead_small_schwa;
			case 0x02C6: return dead_circumflex;
			case 0x02C7: return dead_caron;
			case 0x02C8: return dead_aboveverticalline;
			case 0x02C9: return dead_macron;
			case 0x02CA: return dead_acute;
			case 0x02CB: return dead_grave;
			case 0x02CC: return dead_belowverticalline;
			case 0x02D8: return dead_breve;
			case 0x02D9: return dead_abovedot;
			case 0x02DA: return dead_abovering;
			case 0x02DB: return dead_ogonek;
			case 0x02DC: return dead_tilde;
			case 0x02DD: return dead_doubleacute;
			case 0x0300: return dead_grave;
			case 0x0301: return dead_acute;
			case 0x0302: return dead_circumflex;
			case 0x0303: return dead_tilde;
			case 0x0304: return dead_macron;
			case 0x0306: return dead_breve;
			case 0x0307: return dead_abovedot;
			case 0x0308: return dead_diaeresis;
			case 0x0309: return dead_hook;
			case 0x030A: return dead_abovering;
			case 0x030B: return dead_doubleacute;
			case 0x030C: return dead_caron;
			case 0x030D: return dead_aboveverticalline;
			case 0x030F: return dead_doublegrave;
			case 0x0311: return dead_invertedbreve;
			case 0x0313: return dead_abovecomma;
			case 0x0314: return dead_abovereversedcomma;
			case 0x031B: return dead_horn;
			case 0x0323: return dead_belowdot;
			case 0x0324: return dead_belowdiaeresis;
			case 0x0325: return dead_belowring;
			case 0x0326: return dead_belowcomma;
			case 0x0327: return dead_cedilla;
			case 0x0328: return dead_ogonek;
			case 0x0329: return dead_belowverticalline;
			case 0x032D: return dead_belowcircumflex;
			case 0x032E: return dead_belowbreve;
			case 0x0330: return dead_belowtilde;
			case 0x0331: return dead_belowmacron;
			case 0x0332: return dead_lowline;
			case 0x0335: return dead_stroke;
			case 0x0336: return dead_stroke;
			case 0x0337: return dead_stroke;
			case 0x0338: return dead_longsolidusoverlay;
			case 0x0345: return dead_iota;
			case 0x037A: return dead_iota;
			case 0x04D8: return dead_capital_schwa;
			case 0x04D9: return dead_small_schwa;
			case 0x058F: return dead_currency;
			case 0x060B: return dead_currency;
			case 0x07FE: return dead_currency;
			case 0x07FF: return dead_currency;
			case 0x09F2: return dead_currency;
			case 0x09F3: return dead_currency;
			case 0x09FB: return dead_currency;
			case 0x0AF1: return dead_currency;
			case 0x0BF9: return dead_currency;
			case 0x0E3F: return dead_currency;
			case 0x17DB: return dead_currency;
			case 0x2044: return dead_stroke;
			case 0x2215: return dead_stroke;
			case 0x3099: return dead_voiced_sound;
			case 0x309A: return dead_semivoiced_sound;
			case 0x309B: return dead_voiced_sound;
			case 0x309C: return dead_semivoiced_sound;
			case 0xA838: return dead_currency;
			case 0xFDFC: return dead_currency;
			default:
				if (u >= 0x0390 && u <  0x03CF) return dead_greek;
				if (u >= 0x20A0 && u <= 0x20CF) return dead_currency;
				return null;
		}
	}
	
	public static XkbDeadKey forKeySym(String s) {
		for (XkbDeadKey key : values()) {
			if (key.name().equals(s)) {
				return key;
			}
		}
		return null;
	}
}
