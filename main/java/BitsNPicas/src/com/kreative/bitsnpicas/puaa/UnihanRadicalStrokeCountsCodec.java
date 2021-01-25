package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanRadicalStrokeCountsCodec extends AbstractUnihanCodec {
	public UnihanRadicalStrokeCountsCodec() {
		super(
			"Unihan_RadicalStrokeCounts.txt",
			Arrays.asList(
				"kRSAdobe_Japan1_6",
				"kRSKangXi"
			)
		);
	}
}
