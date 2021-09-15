package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanDictionaryLikeDataCodec extends AbstractUnihanCodec {
	public UnihanDictionaryLikeDataCodec() {
		super(
			"Unihan_DictionaryLikeData.txt",
			Arrays.asList(
				"kCangjie",
				"kCheungBauer",
				"kCihaiT",
				"kFenn",
				"kFourCornerCode",
				"kFrequency",
				"kGradeLevel",
				"kHDZRadBreak",
				"kHKGlyph",
				"kPhonetic",
				"kStrange",
				"kUnihanCore2020"
			)
		);
	}
}
