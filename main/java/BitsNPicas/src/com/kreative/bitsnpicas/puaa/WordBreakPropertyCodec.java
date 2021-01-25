package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class WordBreakPropertyCodec extends AbstractCategoryCodec {
	public WordBreakPropertyCodec() {
		super(
			"WordBreakProperty.txt",
			"Word_Break",
			Arrays.asList(
				"Double_Quote",
				"Single_Quote",
				"Hebrew_Letter",
				"CR",
				"LF",
				"Newline",
				"Extend",
				"Regional_Indicator",
				"Format",
				"Katakana",
				"ALetter",
				"MidLetter",
				"MidNum",
				"MidNumLet",
				"Numeric",
				"ExtendNumLet",
				"ZWJ",
				"WSegSpace"
			)
		);
	}
}
