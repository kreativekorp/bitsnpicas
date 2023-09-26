package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanRadicalStrokeCountsCodec extends AbstractUnihanCodec {
	public UnihanRadicalStrokeCountsCodec() {
		super(
			"Unihan_RadicalStrokeCounts.txt",
			Arrays.asList(
				"kRSAdobe_Japan1_6", //
				"kRSJapanese",       // removed in Unicode 13.0
				"kRSKangXi",         // removed in Unicode 15.1
				"kRSKanWa",          // removed in Unicode 13.0
				"kRSKorean"          // removed in Unicode 13.0
			)
		);
	}
}
