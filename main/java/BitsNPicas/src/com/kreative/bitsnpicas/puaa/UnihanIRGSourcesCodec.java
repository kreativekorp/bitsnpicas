package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanIRGSourcesCodec extends AbstractUnihanCodec {
	public UnihanIRGSourcesCodec() {
		super(
			"Unihan_IRGSources.txt",
			Arrays.asList(
				"kCompatibilityVariant",
				"kIICore",
				"kIRG_GSource",
				"kIRG_HSource",
				"kIRG_JSource",
				"kIRG_KPSource",
				"kIRG_KSource",
				"kIRG_MSource",
				"kIRG_SSource",
				"kIRG_TSource",
				"kIRG_UKSource",
				"kIRG_USource",
				"kIRG_VSource",
				"kRSUnicode",
				"kTotalStrokes"
			)
		);
	}
}
