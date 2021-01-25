package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanReadingsCodec extends AbstractUnihanCodec {
	public UnihanReadingsCodec() {
		super(
			"Unihan_Readings.txt",
			Arrays.asList(
				"kCantonese",
				"kDefinition",
				"kHangul",
				"kHanyuPinlu",
				"kHanyuPinyin",
				"kJapaneseKun",
				"kJapaneseOn",
				"kKorean",
				"kMandarin",
				"kTGHZ2013",
				"kTang",
				"kVietnamese",
				"kXHC1983"
			)
		);
	}
}
