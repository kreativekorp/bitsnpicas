package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanReadingsCodec extends AbstractUnihanCodec {
	public UnihanReadingsCodec() {
		super(
			"Unihan_Readings.txt",
			Arrays.asList(
				"kCantonese",         //
				"kDefinition",        //
				"kHangul",            // added in Unicode 5.0
				"kHanyuPinlu",        //
				"kHanyuPinyin",       // added in Unicode 5.2
				"kJapanese",          // added in Unicode 15.1
				"kJapaneseKun",       //
				"kJapaneseOn",        //
				"kKorean",            //
				"kMandarin",          //
				"kSMSZD2003Readings", // added in Unicode 15.1
				"kTang",              //
				"kTGHZ2013",          // added in Unicode 13.0
				"kVietnamese",        //
				"kXHC1983"            // added in Unicode 5.1
			)
		);
	}
}
