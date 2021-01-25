package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class SentenceBreakPropertyCodec extends AbstractCategoryCodec {
	public SentenceBreakPropertyCodec() {
		super(
			"SentenceBreakProperty.txt",
			"Sentence_Break",
			Arrays.asList(
				"CR",
				"LF",
				"Extend",
				"Sep",
				"Format",
				"Sp",
				"Lower",
				"Upper",
				"OLetter",
				"Numeric",
				"ATerm",
				"STerm",
				"Close",
				"SContinue"
			)
		);
	}
}
