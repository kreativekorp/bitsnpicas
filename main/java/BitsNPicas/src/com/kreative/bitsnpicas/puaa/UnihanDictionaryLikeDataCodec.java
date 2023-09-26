package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanDictionaryLikeDataCodec extends AbstractUnihanCodec {
	public UnihanDictionaryLikeDataCodec() {
		super(
			"Unihan_DictionaryLikeData.txt",
			Arrays.asList(
				"kAlternateTotalStrokes", // added in Unicode 15.0
				"kCangjie",               //
				"kCheungBauer",           // added in Unicode 5.0
				"kFenn",                  //
				"kFourCornerCode",        // added in Unicode 5.0
				"kFrequency",             //
				"kGradeLevel",            //
				"kHDZRadBreak",           //
				"kHKGlyph",               //
				"kMojiJoho",              // added in Unicode 15.1
				"kPhonetic",              //
				"kStrange",               // added in Unicode 14.0
				"kUnihanCore2020"         // added in Unicode 13.0
			)
		);
	}
}
