package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class GraphemeBreakPropertyCodec extends AbstractCategoryCodec {
	public GraphemeBreakPropertyCodec() {
		super(
			"GraphemeBreakProperty.txt",
			"Grapheme_Cluster_Break",
			Arrays.asList(
				"Prepend",
				"CR",
				"LF",
				"Control",
				"Extend",
				"Regional_Indicator",
				"SpacingMark",
				"L",
				"V",
				"T",
				"LV",
				"LVT",
				"ZWJ"
			)
		);
	};
}
