package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class TangutSourcesCodec extends AbstractUnihanCodec {
	public TangutSourcesCodec() {
		super(
			"TangutSources.txt",
			Arrays.asList(
				"kTGT_MergedSrc",
				"kRSTUnicode"
			)
		);
	}
}
